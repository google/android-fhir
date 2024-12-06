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

package com.google.android.fhir.datacapture.test

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineDispatcher

/** https://github.com/Kotlin/kotlinx.coroutines/issues/242#issuecomment-561503344 */
class EspressoTrackedDispatcher(private val wrappedCoroutineDispatcher: CoroutineDispatcher) :
  CoroutineDispatcher() {
  private val counter: CountingIdlingResource =
    CountingIdlingResource("EspressoTrackedDispatcher for $wrappedCoroutineDispatcher")

  init {
    IdlingRegistry.getInstance().register(counter)
  }

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    counter.increment()
    val blockWithDecrement = Runnable {
      try {
        block.run()
      } finally {
        counter.decrement()
      }
    }
    wrappedCoroutineDispatcher.dispatch(context, blockWithDecrement)
  }

  fun cleanUp() {
    IdlingRegistry.getInstance().unregister(counter)
  }
}
