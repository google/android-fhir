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
import com.google.android.fhir.datacapture.views.factories.getLocalizedDateTimePattern
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.test.assertFailsWith
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class MoreLocalDatesTest {

  @Test
  fun `should format date in US locale`() {
    Locale.setDefault(Locale.US)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.format()).isEqualTo("10/18/10")
  }

  @Test
  fun `should format date in Japan locale`() {
    Locale.setDefault(Locale.JAPAN)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.format()).isEqualTo("2010/10/18")
  }

  @Test
  fun `should format date in Italy locale`() {
    Locale.setDefault(Locale.ITALY)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.format()).isEqualTo("18/10/10")
  }

  @Test
  fun `should get date separator for US locale`() {
    Locale.setDefault(Locale.US)
    assertThat(getDateSeparator(getLocalizedDateTimePattern())).isEqualTo('/')
  }

  @Test
  fun `should get date separator for Korean locale`() {
    Locale.setDefault(Locale.KOREA)
    assertThat(getDateSeparator(getLocalizedDateTimePattern())).isEqualTo('.')
  }

  @Test
  fun `should get date separator for Canada locale`() {
    Locale.setDefault(Locale.CANADA)
    assertThat(getDateSeparator(getLocalizedDateTimePattern())).isEqualTo('-')
  }

  @Test
  fun `should canonicalize date format from US locale date pattern`() {
    Locale.setDefault(Locale.US)
    assertThat(canonicalizeDatePattern(getLocalizedDateTimePattern())).isEqualTo("MM/dd/yyyy")
  }

  @Test
  fun `should canonicalize date format from Korean locale date pattern`() {
    Locale.setDefault(Locale.KOREA)
    assertThat(canonicalizeDatePattern(getLocalizedDateTimePattern())).isEqualTo("yyyy.MM.dd.")
  }

  @Test
  fun `should canonicalize date format from Canada locale date pattern`() {
    Locale.setDefault(Locale.CANADA)
    assertThat(canonicalizeDatePattern(getLocalizedDateTimePattern())).isEqualTo("yyyy-MM-dd")
  }

  @Test
  fun `should parse date for US locale`() {
    Locale.setDefault(Locale.US)
    val localDate = parseDate("01/25/2023", "MM/dd/yyyy")
    assertThat(localDate.dayOfMonth).isEqualTo(25)
    assertThat(localDate.month.value).isEqualTo(1)
    assertThat(localDate.year).isEqualTo(2023)
  }

  @Test
  fun `should parse date for Korean locale`() {
    Locale.setDefault(Locale.KOREA)
    val localDate = parseDate("2023.01.25.", "yyyy.MM.dd.")
    assertThat(localDate.dayOfMonth).isEqualTo(25)
    assertThat(localDate.month.value).isEqualTo(1)
    assertThat(localDate.year).isEqualTo(2023)
  }

  @Test
  fun `should not parse US date for Canada locale`() {
    Locale.setDefault(Locale.CANADA)
    assertFailsWith<DateTimeParseException> { parseDate("01/25/2023", "yyyy-MM-dd") }
  }

  @Test
  fun `should not parse date when the input is valid but not the same as the date pattern`() {
    assertFailsWith<DateTimeParseException> { parseDate("1/25/2023", "MM/dd/yyyy") }
    assertFailsWith<DateTimeParseException> { parseDate("11/1/2023", "MM/dd/yyyy") }
  }

  @Test
  fun `should format ITALY locale date using its canonicalizedDatePattern`() {
    Locale.setDefault(Locale.ITALY)
    val localDate = LocalDate.of(2010, 1, 1)
    assertThat(localDate.format(canonicalizeDatePattern(getLocalizedDateTimePattern())))
      .isEqualTo("01/01/2010")
  }

  @Test
  fun `should use default locale if pattern is empty`() {
    Locale.setDefault(Locale.ITALY)
    val localDate = LocalDate.of(2010, 1, 1)
    assertThat(localDate.format("")).isEqualTo(localDate.format())
  }
}
