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

  private fun findSCCWithTarjan(diGraph: Graph): List<List<Node>> {
    val connectedComponents = mutableListOf<List<Node>>()
    val currentLowValueForEachNode = mutableMapOf<Node, Int>()
    var exploringCounter = 0
    val assignedLowValueForEachNode = mutableMapOf<Node, Int>()
    val nodesCurrentlyInStack = mutableSetOf<Node>()
    val visitedNodes = mutableSetOf<Node>()
    val stack = ArrayDeque<Node>()

    fun dfs(at: Node) {
      currentLowValueForEachNode[at] = ++exploringCounter
      assignedLowValueForEachNode[at] = exploringCounter
      visitedNodes.add(at)
      stack.addFirst(at)
      nodesCurrentlyInStack.add(at)

      diGraph[at]?.forEach {
        if (!visitedNodes.contains(it)) {
          dfs(it)
        }

        if (nodesCurrentlyInStack.contains(it)) {
          currentLowValueForEachNode[at] =
            min(currentLowValueForEachNode[at]!!, currentLowValueForEachNode[it]!!)
        }
      }

      // We have found the head node in the scc.
      if (currentLowValueForEachNode[at] == assignedLowValueForEachNode[at]) {
        val connected = mutableListOf<Node>()
        while (true) {
          val node = stack.removeFirst()
          connected.add(node)
          nodesCurrentlyInStack.remove(node)
          if (node == at || stack.isEmpty()) break
        }
        connectedComponents.add(connected.reversed())
      }
    }

    diGraph.keys.forEach {
      if (!visitedNodes.contains(it)) {
        dfs(it)
      }
    }

    return connectedComponents
  }
}
