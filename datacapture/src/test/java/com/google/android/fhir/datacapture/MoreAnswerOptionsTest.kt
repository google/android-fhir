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

package com.google.android.fhir.datacapture

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.r4.core.String
import kotlin.test.assertFailsWith
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
      Questionnaire.Item.AnswerOption.newBuilder()
        .setValue(
          Questionnaire.Item.AnswerOption.ValueX.newBuilder()
            .setCoding(
              Coding.newBuilder()
                .setCode(Code.newBuilder().setValue("test-code"))
                .setDisplay(String.newBuilder().setValue("Test Code"))
            )
        )
        .build()

    assertThat(answerOption.displayString).isEqualTo("Test Code")
  }

  @Test
  fun getDisplayString_choiceItemType_answerOptionShouldReturnValueCodingCodeValue() {
    val answerOption =
      Questionnaire.Item.AnswerOption.newBuilder()
        .setValue(
          Questionnaire.Item.AnswerOption.ValueX.newBuilder()
            .setCoding(Coding.newBuilder().setCode(Code.newBuilder().setValue("test-code")))
        )
        .build()

    assertThat(answerOption.displayString).isEqualTo("test-code")
  }

  @Test
  fun getDisplayString_choiceItemType_shouldThrowExceptionForIllegalAnswerOptionValueX() {
    val answerOption =
      Questionnaire.Item.AnswerOption.newBuilder()
        .setValue(
          Questionnaire.Item.AnswerOption.ValueX.newBuilder()
            .setStringValue(String.newBuilder().setValue("test"))
        )
        .build()

    assertFailsWith<IllegalArgumentException> { answerOption.displayString }
  }

  @Test
  fun getResponseAnswerValueX_ShouldReturnAnswerValueCoding() {
    val answerOption =
      Questionnaire.Item.AnswerOption.newBuilder()
        .setValue(
          Questionnaire.Item.AnswerOption.ValueX.newBuilder()
            .setCoding(
              Coding.newBuilder()
                .setCode(Code.newBuilder().setValue("test-code"))
                .setDisplay(String.newBuilder().setValue("Test Code"))
            )
        )
        .build()
    val answerValueX =
      QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
        .setCoding(
          Coding.newBuilder()
            .setCode(Code.newBuilder().setValue("test-code"))
            .setDisplay(String.newBuilder().setValue("Test Code"))
        )
        .build()

    assertThat(answerOption.responseAnswerValueX).isEqualTo(answerValueX)
  }

  @Test
  fun getResponseAnswerValueX_choiceItemType_shouldThrowExceptionForIllegalAnswerOptionValueX() {
    val answerOption =
      Questionnaire.Item.AnswerOption.newBuilder()
        .setValue(
          Questionnaire.Item.AnswerOption.ValueX.newBuilder()
            .setStringValue(String.newBuilder().setValue("test"))
        )
        .build()

    assertFailsWith<IllegalArgumentException> { answerOption.responseAnswerValueX }
  }
}
