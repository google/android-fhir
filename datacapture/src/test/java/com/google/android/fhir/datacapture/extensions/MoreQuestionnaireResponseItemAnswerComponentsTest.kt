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

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreQuestionnaireResponseItemAnswerComponentsTest {
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Test
  fun `displayString() should return name value for reference`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Reference().setDisplay("Newton"))

    assertThat(answer.value.displayString(context)).isEqualTo("Newton")
  }

  @Test
  fun `displayString() should return name field when both display and reference set`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Reference().setDisplay("Newton").setReference("obs/apple"))

    assertThat(answer.value.displayString(context)).isEqualTo("Newton")
  }

  @Test
  fun `displayString() should return not answered for null reference`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Reference())
    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return not answered for empty display field`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Reference().setDisplay(""))
    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return not answered for empty reference field`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Reference().setReference(""))
    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return reference field for no display but reference set`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Reference().setReference("obs/apple"))

    assertThat(answer.value.displayString(context)).isEqualTo("obs/apple")
  }

  @Test
  fun `displayString() should return url value for attachment`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Attachment().setUrl("http://photos.com/a.png"))

    assertThat(answer.value.displayString(context)).isEqualTo("http://photos.com/a.png")
  }

  @Test
  fun `displayString() should return not answered for null attachment`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Attachment())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return yes for boolean type with true`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(true))

    assertThat(answer.value.displayString(context)).isEqualTo(context.getString(R.string.yes))
  }

  @Test
  fun `displayString() should return no for boolean type with false`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(false))

    assertThat(answer.value.displayString(context)).isEqualTo(context.getString(R.string.no))
  }

  @Test
  fun `displayString() should return not answered for null boolean type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return coding display value for coding type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Coding().setCode("test-code").setDisplay("Test Code"))

    assertThat(answer.value.displayString(context)).isEqualTo("Test Code")
  }

  @Test
  fun `displayString() should return coding code value for coding type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Coding().setCode("test-code"))

    assertThat(answer.value.displayString(context)).isEqualTo("test-code")
  }

  @Test
  fun `displayString() should return not answered for null coding type`() {
    val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Coding())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return localized date string value for date type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(DateType(Date()))

    assertThat(answer.value.displayString(context)).isEqualTo(LocalDate.now().format())
  }

  @Test
  fun `displayString() should return localized date time string value for date time type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateTimeType(Date()))

    assertThat(answer.value.displayString(context))
      .isEqualTo("${LocalDate.now().format()} ${LocalTime.now().toLocalizedString(context)}")
  }

  @Test
  fun `displayString() should return decimal value for decimal type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DecimalType(12.5612))

    assertThat(answer.value.displayString(context)).isEqualTo("12.5612")
  }

  @Test
  fun `displayString() should return not answered string for null decimal type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(DecimalType())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return integer value for integer type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(IntegerType(12))

    assertThat(answer.value.displayString(context)).isEqualTo("12")
  }

  @Test
  fun `displayString() should return not answered string for null integer type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(IntegerType())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return quantity string value for quantity type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Quantity(59.125))

    assertThat(answer.value.displayString(context)).isEqualTo("59.125")
  }

  @Test
  fun `displayString() should return string value for string type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(StringType("Answer"))

    assertThat(answer.value.displayString(context)).isEqualTo("Answer")
  }

  @Test
  fun `displayString() should return not answered value for null string type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(StringType())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return time value for time type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(TimeType("03:00:56"))

    assertThat(answer.value.displayString(context)).isEqualTo("03:00:56")
  }

  @Test
  fun `displayString() should return not answered value for null time type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(TimeType())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return uri value for uri type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(UriType("./a"))

    assertThat(answer.value.displayString(context)).isEqualTo("./a")
  }

  @Test
  fun `displayString() should return not answered for null uri type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(UriType())

    assertThat(answer.value.displayString(context))
      .isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `hasDifferentAnswerSet() should return false when both list values are exactly same`() {
    val list1 =
      listOf(
        createCodingQuestionnaireResponseItemAnswerComponent("http://abc.org", "code1", "Code 1"),
        createCodingQuestionnaireResponseItemAnswerComponent("http://abc.org", "code2", "Code 2")
      )
    val list2 =
      listOf(
        Coding("http://abc.org", "code1", "Code 1"),
        Coding("http://abc.org", "code2", "Code 2")
      )
    assertThat(list1.hasDifferentAnswerSet(list2)).isFalse()
  }

  @Test
  fun `hasDifferentAnswerSet() should return true when both list sizes are different`() {
    val list1 =
      listOf(
        createCodingQuestionnaireResponseItemAnswerComponent("http://abc.org", "code1", "Code 1"),
      )
    val list2 =
      listOf(
        Coding("http://abc.org", "code1", "Code 1"),
        Coding("http://abc.org", "code2", "Code 2")
      )
    assertThat(list1.hasDifferentAnswerSet(list2)).isTrue()
  }

  @Test
  fun `hasDifferentAnswerSet() should return true when both list sizes are same with different items`() {
    val list1 =
      listOf(
        createCodingQuestionnaireResponseItemAnswerComponent("http://abc.org", "code1", "Code 1"),
        createCodingQuestionnaireResponseItemAnswerComponent("http://abc.org", "code2", "Code 2"),
      )
    val list2 =
      listOf(
        Coding("http://abc.org", "code1", "Code 1"),
        Coding("http://abc.org", "code4", "Code 4")
      )
    assertThat(list1.hasDifferentAnswerSet(list2)).isTrue()
  }

  private fun createCodingQuestionnaireResponseItemAnswerComponent(
    url: String,
    code: String,
    display: String
  ) =
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
      .setValue(Coding(url, code, display))
}
