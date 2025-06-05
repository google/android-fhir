/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.engine.benchmarks.app

import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.TimedValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hl7.fhir.r4.model.Resource

@OptIn(ExperimentalCoroutinesApi::class)
internal val benchmarkingViewModelWorkDispatcher = Dispatchers.Default.limitedParallelism(1)

internal suspend fun measureTimeAsync(block: suspend () -> Unit): Duration {
  val mark = TimeSource.Monotonic.markNow()
  block()
  return mark.elapsedNow()
}

internal suspend fun <T> measureTimedValueAsync(block: suspend () -> T): TimedValue<T> {
  val mark = TimeSource.Monotonic.markNow()
  val result = block()
  return TimedValue(result, mark.elapsedNow())
}

internal val Resource.logicalId: String
  get() {
    return this.idElement?.idPart.orEmpty()
  }
