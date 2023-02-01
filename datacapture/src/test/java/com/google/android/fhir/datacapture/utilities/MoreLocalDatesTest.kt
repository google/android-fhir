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

package com.google.android.fhir.datacapture.utilities

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.text.ParseException
import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.test.assertFailsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.N])
class MoreLocalDatesTest {

  @Test
  fun localizedString_US() {
    Locale.setDefault(Locale.US)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.localizedString).isEqualTo("10/18/10")
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
    assertThat(localDate.localizedString).isEqualTo("18/10/10")
  }

  @Test
  fun `get date separator for US locale`() {
    Locale.setDefault(Locale.US)
    val localeDatePattern =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.getDefault()
      )
    assertThat(getDateSeparator(localeDatePattern)).isEqualTo('/')
  }

  @Test
  fun `get date separator for Korean locale`() {
    Locale.setDefault(Locale.KOREA)
    val localeDatePattern =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.getDefault()
      )
    assertThat(getDateSeparator(localeDatePattern)).isEqualTo('.')
  }

  @Test
  fun `get date separator for Canada locale`() {
    Locale.setDefault(Locale.CANADA)
    val localeDatePattern =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.getDefault()
      )
    assertThat(getDateSeparator(localeDatePattern)).isEqualTo('-')
  }

  @Test
  fun `generate acceptable date pattern from US locale date pattern`() {
    Locale.setDefault(Locale.US)
    val localeDatePattern =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.getDefault()
      )
    assertThat(generateAcceptableDateFormat(localeDatePattern)).isEqualTo("MM/dd/yyyy")
  }

  @Test
  fun `generate acceptable date pattern from Korean locale date pattern`() {
    Locale.setDefault(Locale.KOREA)
    val localeDatePattern =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.getDefault()
      )
    assertThat(generateAcceptableDateFormat(localeDatePattern)).isEqualTo("yyyy.MM.dd.")
  }

  @Test
  fun `generate acceptable date pattern from Canada locale date pattern`() {
    Locale.setDefault(Locale.CANADA)
    val localeDatePattern =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.getDefault()
      )
    assertThat(generateAcceptableDateFormat(localeDatePattern)).isEqualTo("yyyy-MM-dd")
  }

  @Test
  fun `parse date for US locale`() {
    Locale.setDefault(Locale.US)
    val localDate = parseDate("01/25/2023", "MM/dd/yyyy")
    assertThat(localDate.dayOfMonth).isEqualTo(25)
    assertThat(localDate.month.value).isEqualTo(1)
    assertThat(localDate.year).isEqualTo(2023)
  }

  @Test
  fun `parse date for Korean locale`() {
    Locale.setDefault(Locale.KOREA)
    val localDate = parseDate("2023.01.25.", "yyyy.MM.dd.")
    assertThat(localDate.dayOfMonth).isEqualTo(25)
    assertThat(localDate.month.value).isEqualTo(1)
    assertThat(localDate.year).isEqualTo(2023)
  }

  @Test
  fun `parse US locale date for Canada locale`() {
    Locale.setDefault(Locale.CANADA)
    assertFailsWith<ParseException> { parseDate("01/25/2023", "yyyy-MM-dd") }
  }
}
