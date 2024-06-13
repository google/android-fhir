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
   * Takes a [directedGraph] and computes all the strongly connected components in the graph.
   *
   * @return An ordered List of strongly connected components of the [directedGraph]. The SCCs are
   *   topologically ordered which may change based on the ordering algorithm and the [Node]s inside
   *   a SSC may be ordered randomly depending on the path taken by algorithm to discover the nodes.
   */
  fun scc(directedGraph: Graph): List<List<Node>> {
    return findSCCWithTarjan(directedGraph)
  }

  /**
   * Finds strongly connected components in topological order. See
   * https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm.
   */
  private fun findSCCWithTarjan(diGraph: Graph): List<List<Node>> {
    val nodeCount = (diGraph.keys + diGraph.values.flatten().toSet()).size
    val sccs = mutableListOf<List<Node>>()
    val lowLinks = mutableMapOf<Node, Int>()
    var exploringCounter = 0
    val discoveryTimes = mutableMapOf<Node, Int>()

    val visitedNodes = BooleanArray(nodeCount)
    val nodesCurrentlyInStack = BooleanArray(nodeCount)
    val stack = ArrayDeque<Node>()

    fun visited(node: Node) = discoveryTimes[node]?.let { visitedNodes[it] } ?: false

    /**
     * Discovery time of each Node is unique, starts with 0 and is linearly incremented. Thus, it
     * can be used to map (index) each node in an array.
     */
    fun Node.discoveryIndex() = discoveryTimes[this] ?: -1

    fun dfs(at: Node) {
      lowLinks[at] = exploringCounter
      discoveryTimes[at] = exploringCounter
      visitedNodes[exploringCounter] = true
      exploringCounter++
      stack.addFirst(at)
      nodesCurrentlyInStack[at.discoveryIndex()] = true

      diGraph[at]?.forEach {
        if (!visited(it)) {
          dfs(it)
        }

        if (nodesCurrentlyInStack[it.discoveryIndex()]) {
          lowLinks[at] = min(lowLinks[at]!!, lowLinks[it]!!)
        }
      }

      // We have found the head node in the scc.
      if (lowLinks[at] == discoveryTimes[at]) {
        val connected = mutableListOf<Node>()
        var node: Node
        do {
          node = stack.removeFirst()
          connected.add(node)
          nodesCurrentlyInStack[node.discoveryIndex()] = false
        } while (node != at && stack.isNotEmpty())
        sccs.add(connected.reversed())
      }
    }

    diGraph.keys.forEach {
      if (!visited(it)) {
        dfs(it)
      }
    }

    return sccs
  }
}
