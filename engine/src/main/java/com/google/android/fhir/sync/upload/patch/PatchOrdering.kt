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
import timber.log.Timber

typealias Node = String

typealias Graph = Map<Node, List<Node>>

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
   * @return A ordered list of the [OrderedMapping] containing:
   * - [OrderedMapping.IndividualMapping] for the [PatchMapping] based on the references to other
   *   [PatchMapping] if the mappings are acyclic
   * - [OrderedMapping.CombinedMapping] for [PatchMapping]s based on the references to other
   *   [PatchMapping]s if the mappings are cyclic.
   */
  suspend fun List<PatchMapping>.orderByReferences(
    database: Database,
  ): List<OrderedMapping> {
    val resourceIdToPatchMapping = associateBy { patchMapping -> patchMapping.resourceTypeAndId }

    /* Get LocalChangeResourceReferences for all the local changes. A single LocalChange may have
    multiple LocalChangeResourceReference, one for each resource reference in the
    LocalChange.payload.*/
    val localChangeIdToResourceReferenceMap: Map<Long, List<LocalChangeResourceReference>> =
      database
        .getLocalChangeResourceReferences(flatMap { it.localChanges.flatMap { it.token.ids } })
        .groupBy { it.localChangeId }

    val adjacencyList = createAdjacencyListForCreateReferences(localChangeIdToResourceReferenceMap)

    val weakConnectedComponents = weakConnectedComponents(adjacencyList)

    val graphsWithCycles = mutableListOf<Graph>()
    val graphsWithOutCycles = mutableListOf<Graph>()
    weakConnectedComponents.forEach {
      try {
        checkCycle(it)
        graphsWithOutCycles.add(it)
      } catch (e: IllegalStateException) {
        Timber.i(e)
        graphsWithCycles.add(it)
      }
    }

    val nodesPerConnectedGraph: List<List<Node>> =
      if (graphsWithCycles.isNotEmpty()) {
        graphsWithCycles.map { graph: Graph ->
          (graph.keys + graph.values.flatten().toSet()).toList()
        }
      } else {
        emptyList()
      }

    val combinedGraphWithoutCycles = combineGraphs(graphsWithOutCycles)
    return nodesPerConnectedGraph
      .map { it.mapNotNull { resourceIdToPatchMapping[it] } }
      .map { OrderedMapping.CombinedMapping(it) } +
      createTopologicalOrderedList(combinedGraphWithoutCycles)
        .mapNotNull { resourceIdToPatchMapping[it] }
        .map { OrderedMapping.IndividualMapping(it) }
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

  private fun weakConnectedComponents(diGraph: Graph): List<Graph> {
    // convert digraph to a graph
    val graph = mutableMapOf<Node, MutableList<Node>>() // Map<Node, List<Node>>
    diGraph.forEach { entry ->
      if (!graph.containsKey(entry.key)) graph[entry.key] = mutableListOf()
      entry.value.forEach {
        graph[entry.key]!!.add(it)
        graph.getOrPut(it) { mutableListOf() }.add(entry.key)
        // add nodes
      }
    }

    // find connected components
    val connectedComponents = findConnectedComponents(graph)

    // convert connected components back to digraph
    val connectedDigraphs = mutableListOf<Graph>()
    connectedComponents.forEach {
      val connectedDigraph = mutableMapOf<Node, MutableList<Node>>()
      it.forEach {
        if (diGraph.containsKey(it)) {
          connectedDigraph[it] = diGraph[it]!! as MutableList<Node>
        }
      }
      if (connectedDigraph.isNotEmpty()) {
        connectedDigraphs.add(connectedDigraph)
      }
    }

    return connectedDigraphs
  }

  private fun findConnectedComponents(graph: Graph): List<List<Node>> {
    val connectedComponents = mutableListOf<MutableList<Node>>()
    val visited = mutableSetOf<String>()
    val connectedNodes = mutableListOf<Node>()

    fun dfs(node: Node) {
      if (visited.add(node)) {
        connectedNodes.add(node)
        graph[node]?.forEach { dfs(it) }
      }
    }

    graph.keys.forEach {
      connectedNodes.clear()
      dfs(it)
      if (connectedNodes.isNotEmpty()) {
        connectedComponents.add(ArrayList(connectedNodes))
      }
    }
    return connectedComponents
  }

  private fun checkCycle(diGraph: Graph) {
    val stack = ArrayDeque<String>()
    val visited = mutableSetOf<String>()
    val currentPath = mutableSetOf<String>()

    fun dfs(key: String) {
      check(currentPath.add(key)) { "Detected a cycle." }
      if (visited.add(key)) {
        diGraph[key]?.forEach { dfs(it) }
        stack.addFirst(key)
      }
      currentPath.remove(key)
    }

    diGraph.keys.forEach { dfs(it) }
  }

  private fun combineGraphs(graphs: List<Graph>): Graph {
    val combinedGraph = mutableMapOf<Node, List<Node>>()
    graphs.forEach { combinedGraph.putAll(it) }
    return combinedGraph
  }
}
