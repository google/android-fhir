/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.TimeAnswerValue
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.QUESTION_HEADER_TAG
import com.google.android.fhir.datacapture.views.compose.TIME_PICKER_INPUT_FIELD
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Time
import kotlin.test.Test
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalTestApi::class)
class TimeViewFactoryTest {

  @Composable
  fun QuestionnaireTimeView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { TimeViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "time-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Time),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "time-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireTimeView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldSetEmptyTimeInput() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "time-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Time),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "time-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireTimeView(questionnaireViewItem) }

    onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals("")
  }

  @Test
  fun shouldDisplayAMTimeInCorrectFormat_dependingOnSystemSettings() = runComposeUiTest {
    val context = viewHolder.itemView.context
    val is24Hour = DateFormat.is24HourFormat(context)

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "time-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Time),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "time-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = TimeAnswerValue(value = Time(value = LocalTime.parse("10:10:00"))),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireTimeView(questionnaireViewItem) }

    val expectedTime = if (is24Hour) "10:10" else "10:10 AM"

    onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals(expectedTime)
  }

  @Test
  fun shouldDisplayPMTimeInCorrectFormat_dependingOnSystemSettings() = runComposeUiTest {
    val context = viewHolder.itemView.context
    val is24Hour = DateFormat.is24HourFormat(context)

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "time-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Time),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "time-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = TimeAnswerValue(value = Time(value = LocalTime.parse("22:10:00"))),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireTimeView(questionnaireViewItem) }

    val expectedTime = if (is24Hour) "22:10" else "10:10 PM"

    onNodeWithTag(TIME_PICKER_INPUT_FIELD, useUnmergedTree = true).assertTextEquals(expectedTime)
  }
}
