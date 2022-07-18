/*
 * Copyright 2021 Google LLC
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
import com.google.android.fhir.SyncStrategyTypes
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.ReferenceIndexEntity
import com.google.android.fhir.index.entities.ReferenceIndex
import java.util.Map.entry
import java.util.UUID
import kotlin.reflect.KSuspendFunction1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

abstract class SyncStrategy {
  abstract val syncStrategyTypes: SyncStrategyTypes

  abstract suspend fun rearrangeSyncList(
    localChanges: List<SquashedLocalChange>,
    database: Database,
    collectAndEmitLocalChange:
      suspend (
        List<SquashedLocalChange>,
        suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>) -> Unit,
    upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  )
}

class SequentialSyncStrategy() : SyncStrategy() {
  override val syncStrategyTypes: SyncStrategyTypes = SyncStrategyTypes.SEQUENTIAL
  private lateinit var database: Database
  private val idsDone = mutableListOf<String>()
  private var listOfLocalChange = mutableListOf<SquashedLocalChange>()
  private lateinit var mapOfResourceIdLocalChange: MutableMap<String, SquashedLocalChange>
  private lateinit var collectAndEmitLocalChange:
    suspend (
      List<SquashedLocalChange>,
      suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>) -> Unit
  private lateinit var upload:
    suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>

  override suspend fun rearrangeSyncList(
    localChanges: List<SquashedLocalChange>,
    database: Database,
    collectAndEmitLocalChange:
      suspend (
        List<SquashedLocalChange>,
        suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>) -> Unit,
    upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {
    idsDone.clear()
    this.database = database
    this.upload = upload
    this.collectAndEmitLocalChange = collectAndEmitLocalChange
    mapOfResourceIdLocalChange =
      localChanges.associateBy { it.localChange.resourceId } as
        MutableMap<String, SquashedLocalChange>
    if (mapOfResourceIdLocalChange.isNotEmpty()) {
      walkAndReturnLocalChange(mapOfResourceIdLocalChange, ::collectAndEmitLocalChangesList)
    }
    println("number of local changes ${localChanges.size}")
    if (listOfLocalChange.isNotEmpty()) {
      println("not empty function exit ${listOfLocalChange.size}")
      collectAndEmitLocalChange(listOfLocalChange, upload)
      listOfLocalChange = mutableListOf()
    }
  }

  private suspend fun collectAndEmitLocalChangesList(resourceId: String) {
    println("collected $resourceId")
    if (listOfLocalChange.size >= 10) {
      mapOfResourceIdLocalChange[resourceId]?.let { it -> listOfLocalChange.add(it) }
      println("emitting ${listOfLocalChange.size}")
      collectAndEmitLocalChange(listOfLocalChange, upload)
      listOfLocalChange = mutableListOf()
    } else {
      mapOfResourceIdLocalChange[resourceId]?.let { it -> listOfLocalChange.add(it) }
    }
  }

  private fun walkAndReturnLocalChange(
    mapOfResourceIdLocalChange: MutableMap<String, SquashedLocalChange>,
    collectAndEmitLocalChangesList: KSuspendFunction1<String, Unit>
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
      processLocalChange(it, mapOfResourceIdLocalChange, collectAndEmitLocalChangesList)
    }
  }

  private fun processLocalChange(
    idSquashedLocalChange: Map.Entry<String, SquashedLocalChange>,
    mapOfResourceIdLocalChange: MutableMap<String, SquashedLocalChange>,
    collectAndEmit: suspend (String) -> Unit
  ) {
    if (idsDone.contains(idSquashedLocalChange.key)) {
      return
    } else {
      val localChange = idSquashedLocalChange.value
      var references: MutableList<ReferenceIndexEntity>
      runBlocking {
        references = returnReferences(localChange) as MutableList<ReferenceIndexEntity>
      }
      if (references.isEmpty()) {
        idsDone.add(idSquashedLocalChange.key)
        runBlocking { collectAndEmit(idSquashedLocalChange.key) }
        println("Added ${idSquashedLocalChange.key} size ${idsDone.size}")
      } else {
        if (localChange.localChange.resourceType == "Encounter") {
          val encounter =
            FhirContext.forCached(FhirVersionEnum.R4)
              .newJsonParser()
              .parseResource(localChange.localChange.payload) as
              Encounter
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
                !idsDone.contains(referenceId)
            ) {
              val mapEntry = entry(referenceId, mapOfResourceIdLocalChange[referenceId]!!)
              processLocalChange(mapEntry, mapOfResourceIdLocalChange, collectAndEmit)
            }
          }
        }
        idsDone.add(idSquashedLocalChange.key)
        runBlocking { collectAndEmit(idSquashedLocalChange.key) }
        println("Added ${idSquashedLocalChange.key} size ${idsDone.size}")
      }
    }
  }

  private suspend fun returnReferences(
    localChange: SquashedLocalChange
  ): List<ReferenceIndexEntity> {
    val resourceType = ResourceType.fromCode(localChange.localChange.resourceType)
    val resourceEntity = database.selectEntity(resourceType, localChange.localChange.resourceId)
    return database.getAllReferences(resourceEntity.resourceUuid, resourceType)
  }
}

class AllLocalChangesSyncStrategy() : SyncStrategy() {
  override val syncStrategyTypes: SyncStrategyTypes = SyncStrategyTypes.ALL
  override suspend fun rearrangeSyncList(
    localChanges: List<SquashedLocalChange>,
    database: Database,
    collectAndEmitLocalChange:
      suspend (
        List<SquashedLocalChange>,
        suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>) -> Unit,
    upload: suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {}
}
