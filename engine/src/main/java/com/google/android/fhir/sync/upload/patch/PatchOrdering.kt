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
import com.google.android.fhir.db.LocalChangeResourceReference

/** Represents a resource e.g. 'Patient/123' , 'Encounter/123'. */
internal typealias Node = String

/**
 * Represents a collection of resources with reference to other resource represented as an edge.
 * e.g. Two Patient resources p1 and p2, each with an encounter and subsequent observation will be
 * represented as follows
 *
 * ```
 * [
 *   'Patient/p1' : [],
 *   'Patient/p2' : [],
 *   'Encounter/e1' : ['Patient/p1'],  // Encounter.subject
 *   'Encounter/e2' : ['Patient/p2'],  // Encounter.subject
 *   'Observation/o1' : ['Patient/p1', 'Encounter/e1'], // Observation.subject, Observation.encounter
 *   'Observation/o2' : ['Patient/p2', 'Encounter/e2'], // Observation.subject, Observation.encounter
 *  ]
 *  ```
 */
internal typealias Graph = Map<Node, List<Node>>

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
   * Orders the list of [PatchMapping]s to maintain referential integrity.
   *
   * This function ensures that if resource A has a CREATE reference to resources B and C, then B
   * and C appear before A in the ordered list. UPDATE references are not considered as they do not
   * impact referential integrity.
   *
   * The function uses Strongly Connected Components (SCC) to handle cyclic dependencies.
   *
   * @return A list of [StronglyConnectedPatchMappings]:
   *     - Each [StronglyConnectedPatchMappings] object represents an SCC.
   *     - If the graph of references is acyclic, each [StronglyConnectedPatchMappings] will contain
   *       a single [PatchMapping].
   *     - If the graph has cycles, a [StronglyConnectedPatchMappings] object will contain multiple
   *       [PatchMapping]s involved in the cycle.
   */
  fun List<PatchMapping>.sccOrderByReferences(
    localChangeResourceReferences: List<LocalChangeResourceReference>,
  ): List<StronglyConnectedPatchMappings> {
    val resourceIdToPatchMapping = associateBy { patchMapping -> patchMapping.resourceTypeAndId }
    val localChangeIdToResourceReferenceMap =
      localChangeResourceReferences.groupBy { it.localChangeId }

    val adjacencyList = createAdjacencyListForCreateReferences(localChangeIdToResourceReferenceMap)

    return StronglyConnectedPatches.scc(adjacencyList).map {
      StronglyConnectedPatchMappings(it.mapNotNull { resourceIdToPatchMapping[it] })
    }
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
          references.addAll(
            localChange.token.ids.flatMap { id ->
              localChangeIdToReferenceMap[id]?.map { it.resourceReferenceValue } ?: emptyList()
            }
          )
        }
      }
      Patch.Type.DELETE -> {
        // do nothing
      }
    }
    return references
  }
}
