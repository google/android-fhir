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

internal object StronglyConnectedPatches {

  /**
   * Takes a [directedGraph] and the number of the nodes [nodeCount] in the [directedGraph] and
   * computes all the strongly connected components in the graph.
   *
   * @return An ordered List of strongly connected components of the [directedGraph]. The SCCs are
   *   topologically ordered which may change based on the ordering algorithm and the [Node]s inside
   *   a SSC may be ordered randomly depending on the path taken by algorithm to discover the nodes.
   */
  fun scc(directedGraph: Graph, nodeCount: Int): List<List<Node>> {
    return sccTarjan(directedGraph, nodeCount)
  }

  private fun sccTarjan(directedGraph: Graph, nodeCount: Int): List<List<Node>> {
    // Code inspired from
    // https://github.com/williamfiset/Algorithms/blob/master/src/main/java/com/williamfiset/algorithms/graphtheory/TarjanSccSolverAdjacencyList.java
    var counter = -1
    val intToNode = mutableMapOf<Int, String>()
    val nodeToInt = mutableMapOf<String, Int>()

    val graph = MutableList<MutableList<Int>>(nodeCount) { mutableListOf() }

    directedGraph.forEach { (key, value) ->
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

    for (i in 0 until nodeCount) {
      if (ids[i] == -1) {
        dfs(i)
      }
    }

    // The algorithm discovers the scc in topological order, so use a sorted map to maintain the
    // order.
    val lowLinkToConnectedMap = sortedMapOf<Int, MutableList<Int>>()
    for (i in 0 until nodeCount) {
      if (!lowLinkToConnectedMap.containsKey(sccs[i])) {
        lowLinkToConnectedMap[sccs[i]] = mutableListOf()
      }
      lowLinkToConnectedMap[sccs[i]]!!.add(i)
    }
    return lowLinkToConnectedMap.values.map { it.mapNotNull { intToNode[it] } }
  }
}
