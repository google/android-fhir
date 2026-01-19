/*
 * Copyright 2025 Google LLC
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.EditTextViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemComposeViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditTextViewHolderDelegateComposeTest {

  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var testViewHolder: QuestionnaireItemViewHolder
  private lateinit var parent: FrameLayout

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity ->
      parent = FrameLayout(activity)
      testViewHolder =
        object : QuestionnaireItemComposeViewHolderFactory {
            private var programmaticUpdateCounter = 0

            override fun getQuestionnaireItemViewHolderDelegate() =
              EditTextViewHolderDelegate(
                KeyboardOptions.Default,
                uiInputText = {
                  programmaticUpdateCounter += 1
                  "$programmaticUpdateCounter"
                },
                uiValidationMessage = { questionnaireViewItem, context ->
                  if (questionnaireViewItem.draftAnswer != null) {
                    context.getString(
                      R.string.decimal_format_validation_error_msg,
                    )
                  } else {
                    getValidationErrorMessage(
                      context,
                      questionnaireViewItem,
                      questionnaireViewItem.validationResult,
                    )
                  }
                },
                handleInput = { _, _ -> },
              )
          }
          .create(parent)
      activity.setContentView(testViewHolder.itemView)
    }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun bindingWhenViewIsInFocusDoesNotProgrammaticallyUpdateEditTextButUpdatesValidationUi() {
    testViewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          answer =
            listOf(
              QuestionnaireResponseItemAnswerComponent().apply { value = StringType("1") },
            )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule.waitForIdle()
    testViewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          answer =
            listOf(
              QuestionnaireResponseItemAnswerComponent().apply { value = StringType("1.1") },
            )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG)
      .assertTextEquals("2") // Value of [programmaticUpdateCounter] in the [testViewHolder]
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).requestFocus()

    testViewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { answer = emptyList() },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1.1.",
      ),
    )
    composeTestRule
      .onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)
      .assertTextEquals("2") // Since the view is in focus the value will not be updated

    composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    val decimalFormatValidationMessage =
      testViewHolder.itemView.context.getString(R.string.decimal_format_validation_error_msg)
    composeTestRule.onNodeWithText(decimalFormatValidationMessage).assertIsDisplayed()
  }
}
