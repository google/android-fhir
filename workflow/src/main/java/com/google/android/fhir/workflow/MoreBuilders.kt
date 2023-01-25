/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.workflow

import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

private const val CLASS = "com.google.android.fhir.workflow.FhirOperator"

/**
 * Blocks the current thread and runs the code in a [CoroutineScope].
 * @throws BlockingMainThreadException if the calling thread is the main thread.
 */
internal fun <T> runBlockingOrThrowMainThreadException(block: suspend (CoroutineScope) -> T): T {
  if (Looper.myLooper() == Looper.getMainLooper()) {
    throw BlockingMainThreadException(
      "The $CLASS API has been called from the main thread resulting in a blocking call. Make sure that the $CLASS API is called from a worker thread instead. See https://developer.android.com/kotlin/coroutines for more details."
    )
  }
  return runBlocking { block(this) }
}

/**
 * The exception that is thrown when an application attempts to perform [runBlocking] operation its
 * main thread.
 */
internal class BlockingMainThreadException(message: String) : RuntimeException(message)
