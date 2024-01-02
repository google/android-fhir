/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir.demo

sealed class Hello {
  class Hi : Hello()
}

enum class Direction(val degrees: Int) {
  NORTH(0),
  EAST(90),
  SOUTH(180),
  WEST(270),
}

fun main() {
  println(
    "${Hello.Hi::class.qualifiedName?.substringBeforeLast(".")}\$${Hello.Hi::class.qualifiedName?.substringAfterLast(".")}",
  )
  println(Hello.Hi::class.java.packageName)
  println(Hello.Hi::class.java.name)
  println(Direction.EAST.degrees)
}

enum class AllowedPackages(val qualifiedName: String) {
  SUCCEDDED(Hello.Hi::class.java.canonicalName.substringAfterLast(".")),
  FAILED("HI"),
}

// fun main() = runBlocking {
//  val originalFlow = flowOf(1, 2, 3)
//  val originalFlow_2 = flowOf("A", "B", "C")
//
//  val zipFlow = originalFlow.zip(originalFlow_2) {
//    a1,a2 ->
//    a1
//  }
//  zipFlow.collect {
//    println(it)
//  }
//  val flowOfFlow = flowOf(flowOf(4,5), flowOf(6,7))
//
//  val lastElement = originalFlow.last()
//  val remove3Flow = originalFlow.takeWhile { number ->
//    number!=3
//  }
//
//  val flattenFlow = remove3Flow.map { number ->
//    if (number==2) {
//      flowOfFlow.flattenConcat().collect{
//        flow<Int>{ emit(it) }
//      }
//    }else {
//      number
//    }
//  }
//
//  flattenFlow.collect{
//    println(it)
//  }
//
// //  val appendFlow = remove3Flow.onCompletion {
// //    emit(lastElement)
// //  }
// //  appendFlow.collect{ println(it) }
//
// //  flowOf("a", "b", "c")
// //    .onCompletion { emit("Done") }
// //    .collect { println(it) } // prints a, b, c, Done
// }
