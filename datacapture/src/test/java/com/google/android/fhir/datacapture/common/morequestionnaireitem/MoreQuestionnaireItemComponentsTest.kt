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

package com.google.android.fhir.datacapture.common.morequestionnaireitem

import android.os.Build
import com.google.android.fhir.datacapture.createQuestionnaireResponseItem
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreQuestionnaireItemComponentsTest {

  @Test
  fun createQuestionnaireResponse() {
    val questionnaire =
      Questionnaire().apply {
        this.addItem(
          Questionnaire.QuestionnaireItemComponent(
              StringType("gender"),
              Enumeration(
                Questionnaire.QuestionnaireItemTypeEnumFactory(),
                Questionnaire.QuestionnaireItemType.STRING
              )
            )
            .apply {
              initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(StringType("male")))
            }
        )

        this.addItem(
          Questionnaire.QuestionnaireItemComponent(
              StringType("country"),
              Enumeration(
                Questionnaire.QuestionnaireItemTypeEnumFactory(),
                Questionnaire.QuestionnaireItemType.STRING
              )
            )
            .apply {
              initial =
                listOf(Questionnaire.QuestionnaireItemInitialComponent(StringType("south-africa")))
            }
        )

        this.addItem(
          Questionnaire.QuestionnaireItemComponent(
              StringType("isActive"),
              Enumeration(
                Questionnaire.QuestionnaireItemTypeEnumFactory(),
                Questionnaire.QuestionnaireItemType.BOOLEAN
              )
            )
            .apply {
              initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(BooleanType(true)))
            }
        )
      }

    val questionnaireResponse = questionnaire.item.map { it.createQuestionnaireResponseItem() }

    Truth.assertThat((questionnaireResponse[0].answer[0].value as StringType).value)
      .isEqualTo("male")
    Truth.assertThat((questionnaireResponse[1].answer[0].value as StringType).value)
      .isEqualTo("south-africa")
    Truth.assertThat((questionnaireResponse[2].answer[0].value as BooleanType).booleanValue())
      .isEqualTo(true)
  }
}
