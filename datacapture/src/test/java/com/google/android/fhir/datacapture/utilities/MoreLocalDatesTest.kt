/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.datacapture.utilities

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.util.Locale
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.M, Build.VERSION_CODES.N])
class MoreLocalDatesTest {

  @Test
  fun localizedString_US() {
    Locale.setDefault(Locale.US)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.localizedString).isEqualTo("Oct 18, 2010")
  }

  @Test
  fun localizedString_Japan() {
    Locale.setDefault(Locale.JAPAN)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.localizedString).isEqualTo("2010/10/18")
  }

  @Test
  fun localizedString_Italy() {
    Locale.setDefault(Locale.ITALY)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.localizedString).isEqualTo("18 ott 2010")
  }
}
