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

import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.junit.Test

class MoreQuestionnaireResponseItemComponentsTest {

  @Test
  fun `descendant should return all descendants in pre-order`() {
    val questionnaireResponseItem11 = QuestionnaireResponseItemComponent().apply { linkId = "1.1" }
    val questionnaireResponseItem12 = QuestionnaireResponseItemComponent().apply { linkId = "1.2" }
    val questionnaireResponseItem1 =
      QuestionnaireResponseItemComponent().apply {
        linkId = "1"
        // Nested questions
        addItem(questionnaireResponseItem11)
        addItem(questionnaireResponseItem12)
      }

    val questionnaireResponseItem21 = QuestionnaireResponseItemComponent().apply { linkId = "1.1" }
    val questionnaireResponseItem22 = QuestionnaireResponseItemComponent().apply { linkId = "1.2" }
    val questionnaireResponseItem2 =
      QuestionnaireResponseItemComponent().apply {
        linkId = "2"
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            // Nested questions under answer
            addItem(questionnaireResponseItem21)
            addItem(questionnaireResponseItem22)
          },
        )
      }

    val questionnaireResponseItem =
      QuestionnaireResponseItemComponent().apply {
        linkId = "root"
        addItem(questionnaireResponseItem1)
        addItem(questionnaireResponseItem2)
      }

    assertThat(questionnaireResponseItem.descendant)
      .containsExactly(
        questionnaireResponseItem,
        questionnaireResponseItem1,
        questionnaireResponseItem11,
        questionnaireResponseItem12,
        questionnaireResponseItem2,
        questionnaireResponseItem21,
        questionnaireResponseItem22,
      )
  }
}
