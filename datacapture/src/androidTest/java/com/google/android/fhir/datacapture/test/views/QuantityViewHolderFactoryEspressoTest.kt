/*
 * Copyright 2023-2025 Google LLC
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

import android.view.View
import android.widget.FrameLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DROP_DOWN_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.QuantityViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuantityViewHolderFactoryEspressoTest {
  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuantityViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldSetDraftWithUnit() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem = createQuestionnaireViewItem { answers, draft ->
      answerHolder = answers
      draftHolder = draft
    }

    runOnUI { viewHolder.bind(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).performClick()
    composeTestRule
      .onNode(hasText("centimeter") and hasAnyAncestor(isPopup()))
      .assertIsDisplayed()
      .performClick()
    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("centimeter")

    composeTestRule.waitUntil { draftHolder != null }

    with(draftHolder as Coding) {
      assertThat(system).isEqualTo("http://unitofmeasure.com")
      assertThat(code).isEqualTo("cm")
      assertThat(display).isEqualTo("centimeter")
    }
    assertThat(answerHolder).isEmpty()
  }

  @Test
  fun shouldSetDraftWithDecimalValue() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem = createQuestionnaireViewItem { answers, draft ->
      answerHolder = answers
      draftHolder = draft
    }

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performClick().performTextInput("22")
    composeTestRule.waitUntil { draftHolder != null }
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("22")

    assertThat(draftHolder as BigDecimal).isEqualTo(BigDecimal(22))
    assertThat(answerHolder).isEmpty()
  }

  @Test
  fun draftWithUnit_shouldCompleteQuantity() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      createQuestionnaireViewItem(Coding("http://unitofmeasure.com", "cm", "centimeter")) {
        answers,
        draft,
        ->
        answerHolder = answers
        draftHolder = draft
      }

    runOnUI { viewHolder.bind(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).performClick()
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performClick().performTextInput("22")

    composeTestRule.waitUntil { !answerHolder.isNullOrEmpty() }

    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("22")

    with(answerHolder!!.single().valueQuantity) {
      assertThat(system).isEqualTo("http://unitofmeasure.com")
      assertThat(code).isEqualTo("cm")
      assertThat(unit).isEqualTo("centimeter")
      assertThat(value).isEqualTo(BigDecimal("22.0"))
    }
    assertThat(draftHolder).isNull()
  }

  @Test
  fun draftWithDecimalValue_shouldCompleteQuantity() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      createQuestionnaireViewItem(BigDecimal(22)) { answers, draft ->
        answerHolder = answers
        draftHolder = draft
      }

    runOnUI { viewHolder.bind(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).performClick()
    composeTestRule
      .onNode(hasText("centimeter") and hasAnyAncestor(isPopup()))
      .assertIsDisplayed()
      .performClick()
    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("centimeter")

    composeTestRule.waitUntil { !answerHolder.isNullOrEmpty() }

    with(answerHolder!!.single().valueQuantity) {
      assertThat(system).isEqualTo("http://unitofmeasure.com")
      assertThat(code).isEqualTo("cm")
      assertThat(unit).isEqualTo("centimeter")
      assertThat(value).isEqualTo(BigDecimal("22.0"))
    }
    assertThat(draftHolder).isNull()
  }

  private fun createQuestionnaireViewItem(
    draftAnswer: Any? = null,
    answersChangedCallback:
      (List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>, Any?) -> Unit,
  ): QuestionnaireViewItem {
    return QuestionnaireViewItem(
      Questionnaire.QuestionnaireItemComponent().apply {
        required = true
        addExtension(
          Extension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
            setValue(
              Coding().apply {
                code = "cm"
                system = "http://unitofmeasure.com"
                display = "centimeter"
              },
            )
          },
        )
        addExtension(
          Extension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
            setValue(
              Coding().apply {
                code = "[in_i]"
                system = "http://unitofmeasure.com"
                display = "inch"
              },
            )
          },
        )
      },
      QuestionnaireResponse.QuestionnaireResponseItemComponent(),
      validationResult = NotValidated,
      answersChangedCallback = { _, _, answers, draft -> answersChangedCallback(answers, draft) },
      draftAnswer = draftAnswer,
    )
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }
}
