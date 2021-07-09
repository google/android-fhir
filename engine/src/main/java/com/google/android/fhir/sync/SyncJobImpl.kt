/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.sync

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class SyncJobImpl(val dispatcher: CoroutineDispatcher) : SyncJob {

  private fun getTag(): String {
    return javaClass.name
  }

  @ExperimentalCoroutinesApi
  override fun poll(delay: Long, initialDelay: Long?): Flow<Result> {
    Log.i(getTag(), "Initiating polling")

    return channelFlow {
        Log.i(getTag(), "Initiating channel flow")
        if (initialDelay != null && initialDelay > 0) {
          delay(initialDelay)
        }

        while (!isClosedForSend) {
          Log.i(getTag(), "Running channel flow")

          val result = Result.Success; // todo//repository.getData()
          send(result)
          delay(delay)
        }
      }
      .flowOn(dispatcher)
  }

  override fun close() {
    dispatcher.cancel()
  }
}
