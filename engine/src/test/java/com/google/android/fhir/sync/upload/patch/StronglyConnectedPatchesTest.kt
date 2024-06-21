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

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StronglyConnectedPatchesTest {

  @Test
  fun `sscOrdered should return strongly connected components in order`() {
    val graph = mutableMapOf<String, MutableList<String>>()

    graph.addEdge("0", "1")
    graph.addEdge("1", "2")
    graph.addEdge("2", "1")

    graph.addEdge("3", "4")
    graph.addEdge("4", "5")
    graph.addEdge("5", "3")

    graph.addEdge("6", "7")
    graph.addEdge("7", "8")

    val result = StronglyConnectedPatches.scc(graph)

    assertThat(result)
      .containsExactly(
        listOf("1", "2"),
        listOf("0"),
        listOf("3", "4", "5"),
        listOf("8"),
        listOf("7"),
        listOf("6"),
      )
      .inOrder()
  }

  @Test
  fun `sscOrdered empty graph should return empty result`() {
    val graph = mutableMapOf<String, MutableList<String>>()
    val result = StronglyConnectedPatches.scc(graph)
    assertThat(result).isEmpty()
  }

  @Test
  fun `sscOrdered  graph with single node should return single scc`() {
    val graph = mutableMapOf<String, MutableList<String>>()
    graph.addNode("0")
    val result = StronglyConnectedPatches.scc(graph)
    assertThat(result).containsExactly(listOf("0"))
  }

  @Test
  fun `sscOrdered  graph with two node should return two scc`() {
    val graph = mutableMapOf<String, MutableList<String>>()
    graph.addNode("0")
    graph.addNode("1")
    val result = StronglyConnectedPatches.scc(graph)
    assertThat(result).containsExactly(listOf("0"), listOf("1"))
  }

  @Test
  fun `sscOrdered  graph with two acyclic node should return two scc in order`() {
    val graph = mutableMapOf<String, MutableList<String>>()
    graph.addEdge("1", "0")
    val result = StronglyConnectedPatches.scc(graph)
    assertThat(result).containsExactly(listOf("0"), listOf("1")).inOrder()
  }

  @Test
  fun `sscOrdered  graph with two cyclic node should return single scc`() {
    val graph = mutableMapOf<String, MutableList<String>>()
    graph.addEdge("0", "1")
    graph.addEdge("1", "0")
    val result = StronglyConnectedPatches.scc(graph)
    assertThat(result).containsExactly(listOf("0", "1"))
  }
}

private fun Graph.addEdge(node: Node, dependsOn: Node) {
  (this as MutableMap).getOrPut(node) { mutableListOf() }.let { (it as MutableList).add(dependsOn) }
}

private fun Graph.addNode(node: Node) {
  (this as MutableMap)[node] = mutableListOf()
}
