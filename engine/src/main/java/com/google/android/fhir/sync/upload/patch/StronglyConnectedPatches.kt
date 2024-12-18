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
    // Ideally the graph.keys should have all the nodes in the graph. But use values as well in case
    // the input graph looks something like [ N1: [N2] ].
    val nodeToIndex =
      (diGraph.keys + diGraph.values.flatten().toSet())
        .mapIndexed { index, s -> s to index }
        .toMap()

    val sccs = mutableListOf<List<Node>>()
    val lowLinks = IntArray(nodeToIndex.size)
    var exploringCounter = 0
    val discoveryTimes = IntArray(nodeToIndex.size)

    val visitedNodes = BooleanArray(nodeToIndex.size)
    val nodesCurrentlyInStack = BooleanArray(nodeToIndex.size)
    val stack = ArrayDeque<Node>()

    fun Node.index() = nodeToIndex[this]!!

    fun dfs(at: Node) {
      lowLinks[at.index()] = exploringCounter
      discoveryTimes[at.index()] = exploringCounter
      visitedNodes[at.index()] = true
      exploringCounter++
      stack.addFirst(at)
      nodesCurrentlyInStack[at.index()] = true

      diGraph[at]?.forEach {
        if (!visitedNodes[it.index()]) {
          dfs(it)
        }

        if (nodesCurrentlyInStack[it.index()]) {
          lowLinks[at.index()] = min(lowLinks[at.index()], lowLinks[it.index()])
        }
      }

      // We have found the head node in the scc.
      if (lowLinks[at.index()] == discoveryTimes[at.index()]) {
        val connected = mutableListOf<Node>()
        var node: Node
        do {
          node = stack.removeFirst()
          connected.add(node)
          nodesCurrentlyInStack[node.index()] = false
        } while (node != at && stack.isNotEmpty())
        sccs.add(connected.reversed())
      }
    }

    diGraph.keys.forEach {
      if (!visitedNodes[it.index()]) {
        dfs(it)
      }
    }

    return sccs
  }
}
