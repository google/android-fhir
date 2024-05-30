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
import kotlin.math.min

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
    val stronglyConnected: List<List<Node>> =
      stronglyConnectedComponents(adjacencyList, resourceIdToPatchMapping.size)
    val mapping = reduceToSuperNode(stronglyConnected)
    val updatedGraph = transformGraph(adjacencyList, mapping)
    val orderedNodes = createTopologicalOrderedList(updatedGraph)
    val orderedStronglyConnected = superNodeToSSC(orderedNodes, mapping.first)
    return orderedStronglyConnected.map {
      val mappings = it.mapNotNull { resourceIdToPatchMapping[it] }
      if (mappings.size == 1) {
        OrderedMapping.IndividualMapping(mappings.first())
      } else {
        OrderedMapping.CombinedMapping(mappings)
      }
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

  private fun stronglyConnectedComponents(diGraph: Graph, nodesCount: Int): List<List<Node>> {
    return sscTarzan(diGraph, nodesCount)
  }

  private fun reduceToSuperNode(
    ssc: List<List<Node>>,
  ): Pair<Map<Node, List<Node>>, Map<Node, Node>> {
    val superNodesMap = mutableMapOf<Node, List<Node>>()
    val nodeToSuperNode = mutableMapOf<Node, String>()

    var counter = 0
    ssc.forEach {
      superNodesMap[(++counter).toString()] = it
      it.forEach { nodeToSuperNode[it] = (counter).toString() }
    }

    return superNodesMap to nodeToSuperNode
  }

  private fun transformGraph(
    oldGraph: Graph,
    nodeMapping: Pair<Map<Node, List<Node>>, Map<Node, Node>>,
  ): Graph {
    val newGraph = mutableMapOf<Node, List<Node>>()
    val (superNodeToNodes, nodeToSuperNode) = nodeMapping
    // Remove any cyclic dependency from connected components.
    // reduce them to a single node
    // replace the ssc nodes with super node
    oldGraph.forEach {
      //      println(it.key)
      val superNode = nodeToSuperNode[it.key]!!

      if (newGraph.containsKey(superNode)) {
        newGraph[superNode] =
          (newGraph[superNode]!! + it.value.mapNotNull { nodeToSuperNode[it] })
            .distinct()
            .filterNot { superNode == it }
      } else {
        newGraph[superNode] = it.value.mapNotNull { nodeToSuperNode[it] }
      }
    }

    return newGraph
  }

  private fun superNodeToSSC(
    orderedNodes: List<Node>,
    mapping: Map<Node, List<Node>>,
  ): List<List<Node>> {
    return orderedNodes.mapNotNull { mapping[it] }
  }

  private fun sscTarzan(diGraph: Graph, nodesCount: Int): List<List<Node>> {
    // Code inspired from
    // https://github.com/williamfiset/Algorithms/blob/master/src/main/java/com/williamfiset/algorithms/graphtheory/TarjanSccSolverAdjacencyList.java
    var counter = -1
    val intToNode = mutableMapOf<Int, String>()
    val nodeToInt = mutableMapOf<String, Int>()

    val graph = MutableList<MutableList<Int>>(nodesCount) { mutableListOf() }

    diGraph.forEach { (key, value) ->
      val intForKey =
        nodeToInt[key]
          ?: let {
            intToNode[++counter] = key
            nodeToInt[key] = counter
            counter
          }

      value.forEach { node ->
        val intForValue =
          nodeToInt[node]
            ?: let {
              intToNode[++counter] = node
              nodeToInt[node] = counter
              counter
            }
        graph[intForKey].add(intForValue)
      }
    }

    var id = 0
    var sccCount = 0
    val visited = BooleanArray(graph.size)
    val ids = IntArray(graph.size) { -1 }
    val low = IntArray(graph.size)
    val sccs = IntArray(graph.size)
    val stack = ArrayDeque<Int>()

    fun dfs(at: Int) {
      low[at] = id++
      ids[at] = low[at]
      stack.addFirst(at)
      visited[at] = true
      for (to in graph[at]) {
        if (ids[to] == -1) {
          dfs(to)
        }
        if (visited[to]) {
          low[at] = min(low[at], low[to])
        }
      }

      // On recursive callback, if we're at the root node (start of SCC)
      // empty the seen stack until back to root.
      if (ids[at] == low[at]) {
        var node: Int = stack.removeFirst()
        while (true) {
          visited[node] = false
          sccs[node] = sccCount
          if (node == at) break
          node = stack.removeFirst()
        }
        sccCount++
      }
    }

    for (i in 0 until nodesCount) {
      if (ids[i] == -1) {
        dfs(i)
      }
    }

    val lowLinkToConnectedMap = mutableMapOf<Int, MutableList<Int>>()
    for (i in 0 until nodesCount) {
      if (!lowLinkToConnectedMap.containsKey(sccs[i])) {
        lowLinkToConnectedMap[sccs[i]] = mutableListOf()
      }
      lowLinkToConnectedMap[sccs[i]]!!.add(i)
    }
    return lowLinkToConnectedMap.values.map { it.mapNotNull { intToNode[it] } }
  }
}
