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

internal data class BenchmarkDuration(val size: Int, val duration: Duration) {
  val averageDuration: Duration
    get() = if (size > 0) duration.div(size) else Duration.ZERO

  companion object {
    val ZERO: BenchmarkDuration = BenchmarkDuration(size = 0, duration = Duration.ZERO)
  }
}

internal operator fun BenchmarkDuration.plus(other: BenchmarkDuration): BenchmarkDuration {
  return BenchmarkDuration(this.size + other.size, this.duration + other.duration)
}
