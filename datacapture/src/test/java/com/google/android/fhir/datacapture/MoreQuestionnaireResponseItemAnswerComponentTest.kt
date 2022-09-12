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

package com.google.android.fhir.datacapture

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.utilities.localizedString
import com.google.android.fhir.datacapture.utilities.toLocalizedString
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
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class MoreQuestionnaireResponseItemAnswerComponentTest {
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Test
  fun `displayString() should return url value for attachment`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Attachment().setUrl("http://photos.com/a.png"))

    assertThat(answer.displayString(context)).isEqualTo("http://photos.com/a.png")
  }

  @Test
  fun `displayString() should return not answered for null attachment`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Attachment())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return yes for boolean type with true`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(true))

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.yes))
  }

  @Test
  fun `displayString() should return no for boolean type with false`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType(false))

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.no))
  }

  @Test
  fun `displayString() should return not answered for null boolean type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(BooleanType())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return coding display value for coding type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Coding().setCode("test-code").setDisplay("Test Code"))

    assertThat(answer.displayString(context)).isEqualTo("Test Code")
  }

  @Test
  fun `displayString() should return coding code value for coding type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Coding().setCode("test-code"))

    assertThat(answer.displayString(context)).isEqualTo("test-code")
  }

  @Test
  fun `displayString() should return not answered for null coding type`() {
    val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Coding())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return localized date string value for date type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(DateType(Date()))

    assertThat(answer.displayString(context)).isEqualTo(LocalDate.now().localizedString)
  }

  @Test
  fun `displayString() should return localized date time string value for date time type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DateTimeType(Date()))

    assertThat(answer.displayString(context))
      .isEqualTo("${LocalDate.now().localizedString} ${LocalTime.now().toLocalizedString(context)}")
  }

  @Test
  fun `displayString() should return decimal value for decimal type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(DecimalType(12.5612))

    assertThat(answer.displayString(context)).isEqualTo("12.5612")
  }

  @Test
  fun `displayString() should return not answered string for null decimal type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(DecimalType())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return integer value for integer type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(IntegerType(12))

    assertThat(answer.displayString(context)).isEqualTo("12")
  }

  @Test
  fun `displayString() should return not answered string for null integer type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(IntegerType())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return quantity string value for quantity type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Quantity(59.125))

    assertThat(answer.displayString(context)).isEqualTo("59.125")
  }

  @Test
  fun `displayString() should return string value for string type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(StringType("Answer"))

    assertThat(answer.displayString(context)).isEqualTo("Answer")
  }

  @Test
  fun `displayString() should return not answered value for null string type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(StringType())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return time value for time type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(TimeType("03:00:56"))

    assertThat(answer.displayString(context)).isEqualTo("03:00:56")
  }

  @Test
  fun `displayString() should return not answered value for null time type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(TimeType())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }

  @Test
  fun `displayString() should return uri value for uri type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(UriType("./a"))

    assertThat(answer.displayString(context)).isEqualTo("./a")
  }

  @Test
  fun `displayString() should return not answered for null uri type`() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(UriType())

    assertThat(answer.displayString(context)).isEqualTo(context.getString(R.string.not_answered))
  }
}
