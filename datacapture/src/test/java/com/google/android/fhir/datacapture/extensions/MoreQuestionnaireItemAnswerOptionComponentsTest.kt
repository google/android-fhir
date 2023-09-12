/*
 * Copyright 2022-2023 Google LLC
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
import com.google.common.truth.Truth.assertThat
import java.util.Locale
import kotlin.test.assertFailsWith
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreQuestionnaireItemAnswerOptionComponentsTest {

  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Test
  fun getDisplayString_choiceItemType_answerOptionShouldReturnValueCodingDisplayValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent()
        .setValue(Coding().setCode("test-code").setDisplay("Test Code"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("Test Code")
  }

  @Test
  fun getDisplayString_choiceItemType_answerOptionShouldReturnValueCodingCodeValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(Coding().setCode("test-code"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("test-code")
  }

  @Test
  fun getDisplayString_choiceItemType_shouldThrowExceptionForIllegalAnswerOptionValueX() {
    val answerOption = Questionnaire.QuestionnaireItemAnswerOptionComponent()

    assertFailsWith<NullPointerException> { answerOption.value.displayString(context) }
  }

  @Test
  fun `getDisplayString should return answer option with reference display value for reference item type`() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent()
        .setValue(Reference().setReference("Patient/123").setDisplay("John Doe"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("John Doe")
  }

  @Test
  fun `getDisplayString should return answer option with reference type and id for reference item type for missing display`() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent()
        .setValue(Reference().setReference("Patient/123"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("Patient/123")
  }

  @Test
  fun getDisplayString_integerType_shouldReturnIntegerValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(IntegerType().setValue(1))

    assertThat(answerOption.value.displayString(context)).isEqualTo("1")
  }

  @Test
  fun getDisplayString_stringType_shouldReturnStringValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent()
        .setValue(StringType().setValue("string type value"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("string type value")
  }

  @Test
  fun getDisplayString_validExtension_shouldReturnLocalizedText() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value =
          Coding().apply {
            code = "test-code"
            display = "Test Code"
            displayElement.apply {
              addExtension(
                Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                  addExtension(Extension("lang", StringType("vi-VN")))
                  addExtension(Extension("content", StringType("Thí nghiệm")))
                }
              )
            }
          }
      }
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("Thí nghiệm")
  }

  @Test
  fun getDisplayString_invalidExtension_shouldReturnDisplayValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value =
          Coding().apply {
            code = "test-code"
            display = "Test Code"
            displayElement.apply {
              addExtension(
                Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                  addExtension(Extension("lang", StringType("vi-VN")))
                }
              )
            }
          }
      }
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("Test Code")
  }

  @Test
  fun getDisplayString_stringType_validTranslationExtension_shouldReturnLocalizedText() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value =
          StringType().apply {
            value = "string value"
            addExtension(
              Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                addExtension(Extension("lang", StringType("vi-VN")))
                addExtension(Extension("content", StringType("Thí nghiệm")))
              }
            )
          }
      }
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("Thí nghiệm")
  }

  @Test
  fun getDisplayString_timeType_shouldReturnTimeValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(TimeType("16:25:00"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("16:25:00")
  }

  @Test
  fun getDisplayString_dateType_shouldReturnDateValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(DateType("2022-06-23"))

    assertThat(answerOption.value.displayString(context)).isEqualTo("6/23/22")
  }

  @Test
  fun optionExclusiveExtension_valueTrue_returnsTrue() = runBlocking {
    val answerOptionTest = Coding("test", "option", "1")
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            answerOption =
              listOf(
                Questionnaire.QuestionnaireItemAnswerOptionComponent(answerOptionTest).apply {
                  extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
                },
              )
          }
        )
      }

    assertThat(questionnaire.item.single().answerOption.single().optionExclusive).isTrue()
  }

  @Test
  fun `initialSelected should not select option with initialSelected as null`() {
    val answerOptions =
      listOf(answerOptionOf("test-code 1", "http://code.com", "Test Code 1", null))

    assertThat(answerOptions.initialSelected).isEmpty()
  }

  @Test
  fun `initialSelected should not select option with initialSelected as false`() {
    val answerOptions =
      listOf(answerOptionOf("test-code 1", "http://code.com", "Test Code 1", false))

    assertThat(answerOptions.initialSelected).isEmpty()
  }

  @Test
  fun `initialSelected should select option with initialSelected as true`() {
    val answerOptions =
      listOf(answerOptionOf("test-code 1", "http://code.com", "Test Code 1", true))

    assertThat(answerOptions.initialSelected.map { (it as Coding).code })
      .containsExactly("test-code 1")
  }

  @Test
  fun `initialSelected should select multiple options with initialSelected as true`() {
    val answerOptions =
      listOf(
        answerOptionOf("test-code 1", "http://code.com", "Test Code 1", null),
        answerOptionOf("test-code 2", "http://code.com", "Test Code 2", true),
        answerOptionOf("test-code 3", "http://code.com", "Test Code 3", false),
        answerOptionOf("test-code 4", "http://code.com", "Test Code 4", true)
      )

    assertThat(answerOptions.initialSelected.map { (it as Coding).code })
      .containsExactly("test-code 2", "test-code 4")
  }

  private fun answerOptionOf(
    code: String,
    url: String,
    display: String,
    initialSelected: Boolean?
  ) =
    Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
      value = Coding().setCode(code).setDisplay(display).setSystem(url)
      initialSelected?.let { this.initialSelected = it }
    }
}
