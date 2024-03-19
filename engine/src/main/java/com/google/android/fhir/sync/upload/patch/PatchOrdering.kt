/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.sync.upload.patch

import androidx.annotation.VisibleForTesting
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.LocalChangeResourceReference

private typealias Node = String

/**
 * Orders the [PatchMapping]s to maintain referential integrity during upload.
 *
 * ```
 * Encounter().apply {
 *   id = "encounter-1"
 *   subject = Reference("Patient/patient-1")
 * }
 *
 * Observation().apply {
 *   id = "observation-1"
 *   subject = Reference("Patient/patient-1")
 *   encounter = Reference("Encounter/encounter-1")
 * }
 * ```
 * * The Encounter has an outgoing reference to Patient and the Observation has outgoing references
 *   to Patient and the Encounter.
 * * Now, to maintain the referential integrity of the resources during the upload,
 *   `Encounter/encounter-1` must go before the `Observation/observation-1`, irrespective of the
 *   order in which the Encounter and Observation were added to the database.
 */
internal object PatchOrdering {

  private val PatchMapping.resourceTypeAndId: String
    get() = "${generatedPatch.resourceType}/${generatedPatch.resourceId}"

  /**
   * Order the [PatchMapping] so that if the resource A has outgoing references {B,C} (CREATE) and
   * {D} (UPDATE), then B,C needs to go before the resource A so that referential integrity is
   * retained. Order of D shouldn't matter for the purpose of referential integrity.
   *
   * @return - A ordered list of the [PatchMapping]s based on the references to other [PatchMapping]
   *   if the mappings are acyclic
   * - throws [IllegalStateException] otherwise
   */
  suspend fun List<PatchMapping>.orderByReferences(
    database: Database,
  ): List<PatchMapping> {
    val resourceIdToPatchMapping = associateBy { patchMapping -> patchMapping.resourceTypeAndId }

    /* Get LocalChangeResourceReferences for all the local changes. A single LocalChange may have
    multiple LocalChangeResourceReference, one for each resource reference in the
    LocalChange.payload.*/
    val localChangeIdToResourceReferenceMap: Map<Long, List<LocalChangeResourceReference>> =
      database
        .getLocalChangeResourceReferences(flatMap { it.localChanges.flatMap { it.token.ids } })
        .groupBy { it.localChangeId }

    val adjacencyList = createAdjacencyListForCreateReferences(localChangeIdToResourceReferenceMap)
    return createTopologicalOrderedList(adjacencyList).mapNotNull { resourceIdToPatchMapping[it] }
  }

  /**
   * @return A map of [PatchMapping] to all the outgoing references to the other [PatchMapping]s of
   *   type [Patch.Type.INSERT] .
   */
  @VisibleForTesting
  internal fun List<PatchMapping>.createAdjacencyListForCreateReferences(
    localChangeIdToReferenceMap: Map<Long, List<LocalChangeResourceReference>>,
  ): Map<Node, List<Node>> {
    val adjacencyList = mutableMapOf<Node, List<Node>>()
    /* if the outgoing reference is to a resource that's just an update and not create, then don't
    link to it. This may make the sub graphs smaller and also help avoid cyclic dependencies.*/
    val resourceIdsOfInsertTypeLocalChanges =
      asSequence()
        .filter { it.generatedPatch.type == Patch.Type.INSERT }
        .map { it.resourceTypeAndId }
        .toSet()

    forEach { patchMapping ->
      adjacencyList[patchMapping.resourceTypeAndId] =
        patchMapping.findOutgoingReferences(localChangeIdToReferenceMap).filter {
          resourceIdsOfInsertTypeLocalChanges.contains(it)
        }
    }
    return adjacencyList
  }

  private fun PatchMapping.findOutgoingReferences(
    localChangeIdToReferenceMap: Map<Long, List<LocalChangeResourceReference>>,
  ): Set<Node> {
    val references = mutableSetOf<Node>()
    when (generatedPatch.type) {
      Patch.Type.INSERT,
      Patch.Type.UPDATE, -> {
        localChanges.forEach { localChange ->
          localChange.token.ids.forEach { id ->
            localChangeIdToReferenceMap[id]?.let {
              references.addAll(it.map { it.resourceReferenceValue })
            }
          }
        }
      }
      Patch.Type.DELETE -> {
        // do nothing
      }
    }
    return references
  }

  private fun createTopologicalOrderedList(adjacencyList: Map<Node, List<Node>>): List<Node> {
    val stack = ArrayDeque<String>()
    val visited = mutableSetOf<String>()
    val currentPath = mutableSetOf<String>()

    fun dfs(key: String) {
      check(currentPath.add(key)) { "Detected a cycle." }
      if (visited.add(key)) {
        adjacencyList[key]?.forEach { dfs(it) }
        stack.addFirst(key)
      }
      currentPath.remove(key)
    }

    adjacencyList.keys.forEach { dfs(it) }
    return stack.reversed()
  }
}
