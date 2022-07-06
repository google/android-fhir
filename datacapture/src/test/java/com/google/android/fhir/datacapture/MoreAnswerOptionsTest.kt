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

import android.os.Build
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
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreAnswerOptionsTest {

  @Test
  fun getDisplayString_choiceItemType_answerOptionShouldReturnValueCodingDisplayValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent()
        .setValue(Coding().setCode("test-code").setDisplay("Test Code"))

    assertThat(answerOption.displayString).isEqualTo("Test Code")
  }

  @Test
  fun getDisplayString_choiceItemType_answerOptionShouldReturnValueCodingCodeValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(Coding().setCode("test-code"))

    assertThat(answerOption.displayString).isEqualTo("test-code")
  }

  @Test
  fun getDisplayString_choiceItemType_shouldThrowExceptionForIllegalAnswerOptionValueX() {
    val answerOption = Questionnaire.QuestionnaireItemAnswerOptionComponent()

    assertFailsWith<IllegalArgumentException> { answerOption.displayString }
  }

  @Test
  fun getDisplayString_integerType_shouldReturnIntegerValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(IntegerType().setValue(1))

    assertThat(answerOption.displayString).isEqualTo("1")
  }

  @Test
  fun getDisplayString_stringType_shouldReturnStringValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent()
        .setValue(StringType().setValue("string type value"))

    assertThat(answerOption.displayString).isEqualTo("string type value")
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

    assertThat(answerOption.displayString).isEqualTo("Thí nghiệm")
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

    assertThat(answerOption.displayString).isEqualTo("Test Code")
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

    assertThat(answerOption.displayString).isEqualTo("Thí nghiệm")
  }

  @Test
  fun getDisplayString_timeType_shouldReturnTimeValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(TimeType("16:25:00"))

    assertThat(answerOption.displayString).isEqualTo("16:25:00")
  }

  @Test
  fun getDisplayString_dateType_shouldReturnDateValue() {
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(DateType("2022-06-23"))

    assertThat(answerOption.displayString).isEqualTo("2022-06-23")
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
  fun getDisplayString_answerCodingType_answerShouldReturnValueCodingDisplayValue() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Coding().setCode("test-code").setDisplay("Test Code"))

    assertThat(answer.displayString).isEqualTo("Test Code")
  }

  @Test
  fun getDisplayString_answerCodingType_answerShouldReturnValueCodingCodeValue() {
    val answer =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
        .setValue(Coding().setCode("test-code"))

    assertThat(answer.displayString).isEqualTo("test-code")
  }

  @Test
  fun getDisplayString_answerCodingType_answerShouldReturnNull() {
    val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(Coding())
    assertThat(answer.displayString).isNull()
  }
}
