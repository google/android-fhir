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

package com.google.android.fhir.datacapture.views

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireItemViewItemTest {
  @Test
  fun singleAnswerOrNull_noAnswer_shouldReturnNull() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.Item.getDefaultInstance(),
        QuestionnaireResponse.Item.newBuilder()
      ) {}
    assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
  }

  @Test
  fun singleAnswerOrNull_singleAnswer_shouldReturnSingleAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.Item.getDefaultInstance(),
        QuestionnaireResponse.Item.newBuilder().apply {
          addAnswer(
            QuestionnaireResponse.Item.Answer.newBuilder().apply {
              value =
                QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                  .setBoolean(Boolean.newBuilder().setValue(true))
                  .build()
            }
          )
        }
      ) {}
    assertThat(questionnaireItemViewItem.singleAnswerOrNull!!.value.boolean.value).isTrue()
  }

  @Test
  fun singleAnswerOrNull_multipleAnswers_shouldReturnNull() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.Item.getDefaultInstance(),
        QuestionnaireResponse.Item.newBuilder().apply {
          addAnswer(
            QuestionnaireResponse.Item.Answer.newBuilder().apply {
              value =
                QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                  .setBoolean(Boolean.newBuilder().setValue(true))
                  .build()
            }
          )
          addAnswer(
            QuestionnaireResponse.Item.Answer.newBuilder().apply {
              value =
                QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                  .setBoolean(Boolean.newBuilder().setValue(true))
                  .build()
            }
          )
        }
      ) {}
    assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
  }
}
