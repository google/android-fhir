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

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MoreBuildersTest {

  @Test(expected = BlockingMainThreadException::class)
  fun `runBlockingOrThrowMainThreadException should throw exception when called from main thread`():
    Unit = runBlockingOrThrowMainThreadException { 1 + 1 }

  @Test
  fun `runBlockingOrThrowMainThreadException should return 2`() = runBlockingOnWorkerThread {
    val result = runBlockingOrThrowMainThreadException { 1 + 1 }
    assertThat(result).isEqualTo(2)
  }
}
