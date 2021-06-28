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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireResponseValidatorTest {

  lateinit var context: Context

  @Before
  fun initContext() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun shouldReturnValidResult() {
    val questionnaire =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent()
            .setLinkId("a-question")
            .setMaxLength(3)
            .setType(Questionnaire.QuestionnaireItemType.INTEGER)
            .setText("Age in years?")
        )
    val questionnaireResponse =
      QuestionnaireResponse()
        .addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
            .setLinkId("a-question")
            .setAnswer(
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                  .setValue(IntegerType(3))
              )
            )
        )
    val result =
      QuestionnaireResponseValidator.validate(
        questionnaire.item,
        questionnaireResponse.item,
        context
      )
    assertThat(result.get("a-question")).isEqualTo(listOf(ValidationResult(true, listOf())))
  }

  @Test
  fun shouldReturnInvalidResultWithMessages() {
    val questionnaire =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent()
            .setLinkId("a-question")
            .setMaxLength(3)
            .setType(Questionnaire.QuestionnaireItemType.INTEGER)
            .setText("Age in years?")
        )
    val questionnaireResponse =
      QuestionnaireResponse()
        .addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
            .setLinkId("a-question")
            .setAnswer(
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                  .setValue(IntegerType(1000))
              )
            )
        )
    val result =
      QuestionnaireResponseValidator.validate(
        questionnaire.item,
        questionnaireResponse.item,
        context
      )
    assertThat(result.get("a-question"))
      .isEqualTo(
        listOf(
          ValidationResult(
            false,
            listOf("The maximum number of characters that are permitted in the answer is: 3")
          )
        )
      )
  }

  @Test
  fun shouldReturnInvalidResultWithMessages_forNestedItems() {
    val questionnaire =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent()
            .setLinkId("a-question")
            .setMaxLength(3)
            .setType(Questionnaire.QuestionnaireItemType.INTEGER)
            .setText("Age in years?")
            .addItem(
              Questionnaire.QuestionnaireItemComponent()
                .setLinkId("a-nested-question")
                .setMaxLength(3)
                .setType(Questionnaire.QuestionnaireItemType.STRING)
                .setText("Country code")
            )
        )
    val questionnaireResponse =
      QuestionnaireResponse()
        .addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
            .setLinkId("a-question")
            .setAnswer(
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                  .setValue(IntegerType(1000))
                  .addItem(
                    QuestionnaireResponse.QuestionnaireResponseItemComponent()
                      .setLinkId("a-nested-question")
                      .setAnswer(
                        listOf(
                          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                            .setValue(StringType("ABCD"))
                        )
                      )
                  )
              )
            )
        )
    val result =
      QuestionnaireResponseValidator.validate(
        questionnaire.item,
        questionnaireResponse.item,
        context
      )
    assertThat(result.get("a-question"))
      .containsExactly(
        ValidationResult(
          false,
          listOf("The maximum number of characters that are permitted in the answer is: 3")
        )
      )
    assertThat(result.get("a-nested-question"))
      .containsExactly(
        ValidationResult(
          false,
          listOf("The maximum number of characters that are permitted in the answer is: 3")
        )
      )
  }
}
