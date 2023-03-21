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

package com.google.android.fhir.datacapture.extensions

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import java.time.LocalTime
import java.util.Locale
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class MoreLocalTimesTest {

  @Before
  fun setUp() {
    Locale.setDefault(Locale.US)
  }

  @Test
  fun localizedString_US() {
    Locale.setDefault(Locale.US)
    val localTime = LocalTime.of(18, 10, 10)
    assertThat(localTime.toLocalizedString(ApplicationProvider.getApplicationContext()))
      .isEqualTo("6:10 PM")
  }

  @Test
  fun localizedString_US_with24HrsSetting() {
    org.robolectric.shadows.ShadowSettings.set24HourTimeFormat(true)
    Locale.setDefault(Locale.US)
    val localTime = LocalTime.of(18, 10, 10)
    assertThat(localTime.toLocalizedString(ApplicationProvider.getApplicationContext()))
      .isEqualTo("18:10")
  }

  @Test
  fun localizedString_Japan() {
    Locale.setDefault(Locale.JAPAN)
    val localTime = LocalTime.of(18, 10, 10)
    assertThat(localTime.toLocalizedString(ApplicationProvider.getApplicationContext()))
      .isEqualTo("6:10 午後")
  }

  @Test
  fun localizedString_Japan_with24HrsSetting() {
    org.robolectric.shadows.ShadowSettings.set24HourTimeFormat(true)
    Locale.setDefault(Locale.JAPAN)
    val localTime = LocalTime.of(18, 10, 10)
    assertThat(localTime.toLocalizedString(ApplicationProvider.getApplicationContext()))
      .isEqualTo("18:10")
  }
}
