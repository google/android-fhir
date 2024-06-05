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

import kotlin.math.min

internal class StronglyConnectedPatches(private val diGraph: Graph, private val nodesCount: Int) {

  fun sscOrdered(ordering: (Graph) -> List<Node>): List<List<Node>> {
    val stronglyConnected = sscTarzan()
    val (superNodeToConnectedComponents, connectedComponentNodeToSuperNode) =
      reduceToSuperNodes(
        stronglyConnected,
      )
    val updatedGraph =
      transformGraphWithSSCtoGraphWithSuperNodes(diGraph, connectedComponentNodeToSuperNode)
    val orderedNodes = ordering(updatedGraph)
    return superNodeToSSC(orderedNodes, superNodeToConnectedComponents)
  }

  private fun sscTarzan(): List<List<Node>> {
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

  private fun reduceToSuperNodes(
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

  /**
   * Converts a graph that has ssc to a graph with super nodes as per the provided [nodeToSuperNode]
   * mapping.
   *
   * **Input**
   * * oldGraph `[A : B, C], [B : A, C], [C : A, B, F], [D : E], [E : D], [F : G]`
   * * nodeToSuperNode `[A:1, B:1, C:1, D:2, E:2, F:3, G:4]`
   *
   * **Output** `[1 : 3], [2], [3: 4]`
   */
  private fun transformGraphWithSSCtoGraphWithSuperNodes(
    oldGraph: Graph,
    nodeToSuperNode: Map<Node, Node>,
  ): Graph {
    val newGraph = mutableMapOf<Node, List<Node>>()
    // Remove any cyclic dependency from connected components in the graph.
    // Reduce all the ssc nodes from graph into to a single node in graph.
    // Replace the ssc nodes with super node.
    oldGraph.forEach {
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
}
