/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.impl

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.SyncStrategyTypes
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.entities.ReferenceIndexEntity
import com.google.android.fhir.index.entities.ReferenceIndex
import java.util.Map.entry
import java.util.UUID
import kotlin.reflect.KSuspendFunction2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

abstract class SyncStrategy {
  abstract val syncStrategyTypes: SyncStrategyTypes

  abstract suspend fun rearrangeSyncList(
    localChanges: List<LocalChange>,
    database: Database,
    collectAndEmitLocalChange:
      suspend (
        List<LocalChange>,
        suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
      ) -> Unit,
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  )
}

data class ContextObject(
  val idsDone: MutableList<String>,
  val listOfLocalChange: MutableList<LocalChange>,
  val mapOfResourceIdLocalChange: MutableMap<String, LocalChange>
)

class SequentialSyncStrategy() : SyncStrategy() {
  override val syncStrategyTypes: SyncStrategyTypes = SyncStrategyTypes.SEQUENTIAL
  private lateinit var database: Database
  //  private var idsDone = mutableListOf<String>()
  //  private var listOfLocalChange = mutableListOf<SquashedLocalChange>()
  //  private lateinit var mapOfResourceIdLocalChange: MutableMap<String, SquashedLocalChange>
  private lateinit var collectAndEmitLocalChange:
    suspend (
      List<LocalChange>,
      suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
    ) -> Unit
  private lateinit var upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>

  override suspend fun rearrangeSyncList(
    localChanges: List<LocalChange>,
    database: Database,
    collectAndEmitLocalChange:
      suspend (
        List<LocalChange>,
        suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
      ) -> Unit,
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {
    val idsDone = mutableListOf<String>()
    val listOfLocalChange = mutableListOf<LocalChange>()
    this.database = database
    this.upload = upload
    this.collectAndEmitLocalChange = collectAndEmitLocalChange
    val mapOfResourceIdLocalChange =
      localChanges.associateBy { it.resourceId } as MutableMap<String, LocalChange>
    val contextWorkSpace = ContextObject(idsDone, listOfLocalChange, mapOfResourceIdLocalChange)

    if (mapOfResourceIdLocalChange.isNotEmpty()) {
      walkAndReturnLocalChange(
        mapOfResourceIdLocalChange,
        ::collectAndEmitLocalChangesList,
        contextWorkSpace
      )
    }
    if (contextWorkSpace.listOfLocalChange.isNotEmpty()) {
      val uniqueList = ArrayList(contextWorkSpace.listOfLocalChange.toSet().toList())
      collectAndEmitLocalChange(uniqueList, upload)
      contextWorkSpace.listOfLocalChange.clear()
    }
  }

  private suspend fun collectAndEmitLocalChangesList(
    resourceId: String,
    contextWorkSpace: ContextObject
  ) {
    if (contextWorkSpace.listOfLocalChange.size >= 10 ||
        contextWorkSpace.listOfLocalChange.toString().toByteArray().size > 1000000
    ) {
      contextWorkSpace.mapOfResourceIdLocalChange[resourceId]?.let { it ->
        contextWorkSpace.listOfLocalChange.add(it)
      }

      val uniqueList = ArrayList(contextWorkSpace.listOfLocalChange.toSet().toList())
      collectAndEmitLocalChange(uniqueList, upload)
      contextWorkSpace.listOfLocalChange.clear()
    } else {
      contextWorkSpace.mapOfResourceIdLocalChange[resourceId]?.let { it ->
        contextWorkSpace.listOfLocalChange.add(it)
      }
    }
  }

  private fun walkAndReturnLocalChange(
    mapOfResourceIdLocalChange: MutableMap<String, LocalChange>,
    collectAndEmitLocalChangesList: KSuspendFunction2<String, ContextObject, Unit>,
    contextWorkSpace: ContextObject
  ) {
    // Map of resourceId -> localChange
    //  if not
    // take the local change
    // if there are no references, that means this is an independent resource and can be queued up
    // for sync
    // If there are references:
    // for all references we check if they are part of the local changes
    // if not then we queue the local change up for sync cause there are no changes of the parent
    // resource
    // if yes we  check if the parent resource local change is queued up for sync
    // if not, we queue the parent resource local change for sync
    // if yes, do nothing.
    // After all references are queued up, we queue up the local Change for sync
    // Check if resource is queued up for sync
    mapOfResourceIdLocalChange.forEach {
      processLocalChange(
        it,
        mapOfResourceIdLocalChange,
        collectAndEmitLocalChangesList,
        contextWorkSpace
      )
    }
  }

  private fun processLocalChange(
    localChange: Map.Entry<String, LocalChange>,
    mapOfResourceIdLocalChange: MutableMap<String, LocalChange>,
    collectAndEmit: KSuspendFunction2<String, ContextObject, Unit>,
    contextWorkSpace: ContextObject
  ) {
    if (contextWorkSpace.idsDone.contains(localChange.key)) {
      return
    } else {
      var references: MutableList<ReferenceIndexEntity>
      runBlocking {
        references = returnReferences(localChange.value) as MutableList<ReferenceIndexEntity>
      }
      if (references.isEmpty()) {
        contextWorkSpace.idsDone.add(localChange.key)
        runBlocking { collectAndEmit(localChange.key, contextWorkSpace) }
      } else {
        if (localChange.value.resourceType == "Encounter" &&
            localChange.value.type == LocalChange.Type.INSERT
        ) {
          val encounter =
            FhirContext.forCached(FhirVersionEnum.R4)
              .newJsonParser()
              .parseResource(localChange.value.payload) as Encounter
          if (encounter.location.size > 1) {
            encounter.location.forEach {
              references.add(
                ReferenceIndexEntity(
                  -10,
                  UUID.randomUUID(),
                  ResourceType.Encounter,
                  ReferenceIndex("workaround", "Encounter.location.loc", it.location.reference)
                )
              )
            }
          }
        }
        references.forEach { refIndexEntity ->
          if (refIndexEntity.index.value.contains("/")) {
            val referenceId = refIndexEntity.index.value.split("/")[1]
            if (mapOfResourceIdLocalChange.containsKey(referenceId) &&
                !contextWorkSpace.idsDone.contains(referenceId)
            ) {
              val mapEntry = entry(referenceId, mapOfResourceIdLocalChange[referenceId]!!)
              processLocalChange(
                mapEntry,
                mapOfResourceIdLocalChange,
                collectAndEmit,
                contextWorkSpace
              )
            }
          }
        }
        contextWorkSpace.idsDone.add(localChange.key)
        runBlocking { collectAndEmit(localChange.key, contextWorkSpace) }
      }
    }
  }

  private suspend fun returnReferences(localChange: LocalChange): List<ReferenceIndexEntity> {
    val resourceType = ResourceType.fromCode(localChange.resourceType)
    if (localChange.type == LocalChange.Type.DELETE) {
      return mutableListOf()
    }
    val resourceEntity = database.selectEntity(resourceType, localChange.resourceId)
    return database.getAllReferences(resourceEntity.resourceUuid, resourceType)
  }
}

class AllLocalChangesSyncStrategy() : SyncStrategy() {
  override val syncStrategyTypes: SyncStrategyTypes = SyncStrategyTypes.ALL
  override suspend fun rearrangeSyncList(
    localChanges: List<LocalChange>,
    database: Database,
    collectAndEmitLocalChange:
      suspend (
        List<LocalChange>,
        suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
      ) -> Unit,
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {}
}
