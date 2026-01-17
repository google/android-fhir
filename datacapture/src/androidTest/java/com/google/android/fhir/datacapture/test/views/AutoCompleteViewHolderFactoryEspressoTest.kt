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

package com.google.android.fhir.datacapture.test.views

import android.widget.FrameLayout
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.DROP_DOWN_ANSWER_MENU_ITEM_TAG
import com.google.android.fhir.datacapture.views.components.MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG
import com.google.android.fhir.datacapture.views.components.MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.factories.AutoCompleteViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AutoCompleteViewHolderFactoryEspressoTest {
  @get:Rule
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = AutoCompleteViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun shouldReturnFilteredDropDownMenuItems() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)
    composeTestRule
      .onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG)
      .performTextReplacement("Coding 1")
    composeTestRule.onAllNodes(hasTestTag(DROP_DOWN_ANSWER_MENU_ITEM_TAG)).assertCountEquals(1)
  }

  @Test
  fun shouldAddDropDownValueSelectedForMultipleAnswersAutoCompleteTextView() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions("Coding 1", "Coding 5"),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    viewHolder.bind(questionnaireViewItem)

    composeTestRule
      .onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG)
      .performTextReplacement("Coding 3")

    composeTestRule
      .onNode(
        hasTestTag(DROP_DOWN_ANSWER_MENU_ITEM_TAG) and
          hasTextExactly("Coding 3") and
          hasAnyAncestor(isPopup()),
      )
      .assertIsDisplayed()
      .performClick()

    composeTestRule.onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG).assertTextEquals("")

    composeTestRule.waitUntil { answerHolder != null }
    assertThat(answerHolder!!.map { it.valueCoding.display })
      .containsExactly("Coding 1", "Coding 5", "Coding 3")
  }

  @Test
  fun shouldSetCorrectNumberOfChipsForSelectedAnswers() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions("Coding 1", "Coding 5"),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)
    composeTestRule.onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(2)
  }

  private fun answerOptions(repeats: Boolean, vararg options: String) =
    Questionnaire.QuestionnaireItemComponent().apply {
      this.repeats = repeats
      options.forEach { option ->
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value =
              Coding().apply {
                code = option.replace(" ", "_")
                display = option
              }
          },
        )
      }
    }

  private fun responseOptions(vararg options: String) =
    QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
      options.forEach { option ->
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value =
              Coding().apply {
                code = option.replace(" ", "_")
                display = option
              }
          },
        )
      }
    }
}
