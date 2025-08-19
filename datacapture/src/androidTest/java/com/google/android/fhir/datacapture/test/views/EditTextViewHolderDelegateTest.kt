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
import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.EditTextViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemComposeViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditTextViewHolderDelegateTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val emptyComposeTestRule = createEmptyComposeRule()

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
                      com.google.android.fhir.datacapture.R.string
                        .decimal_format_validation_error_msg,
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

    Truth.assertThat(
        testViewHolder.itemView
          .findViewById<TextInputEditText>(
            com.google.android.fhir.datacapture.R.id.text_input_edit_text,
          )
          .text
          .toString(),
      )
      .isEqualTo("2") // Value of [programmaticUpdateCounter] in the [testViewHolder]

    testViewHolder.itemView
      .findViewById<TextInputEditText>(
        com.google.android.fhir.datacapture.R.id.text_input_edit_text,
      )
      .requestFocus()

    testViewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { answer = emptyList() },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1.1.",
      ),
    )

    Truth.assertThat(
        testViewHolder.itemView
          .findViewById<TextInputEditText>(
            com.google.android.fhir.datacapture.R.id.text_input_edit_text,
          )
          .text
          .toString(),
      )
      .isEqualTo("2") // Since the view is in focus the value will not be updated

    Truth.assertThat(
        testViewHolder.itemView
          .findViewById<TextInputLayout>(com.google.android.fhir.datacapture.R.id.text_input_layout)
          .error
          .toString(),
      )
      .isEqualTo(
        testViewHolder.itemView
          .findViewById<TextInputLayout>(com.google.android.fhir.datacapture.R.id.text_input_layout)
          .context
          .getString(
            com.google.android.fhir.datacapture.R.string.decimal_format_validation_error_msg,
          ),
      )
  }

  @Test
  fun externalUpdateOfQuestionnaireViewItemDoesNotUpdateEditTextAndOverrideKeyboardInput() {
    var pendingTextChange = 0
    val handlingTextIdlingResource =
      object : IdlingResource {
        override val isIdleNow: Boolean
          get() = pendingTextChange == 0
      }
    composeTestRule.registerIdlingResource(handlingTextIdlingResource)
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.QuestionnaireItemComponent(),
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            answer =
              listOf(
                QuestionnaireResponseItemAnswerComponent().apply { value = StringType("") },
              )
          },
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    val editTextViewHolderDelegate =
      EditTextViewHolderDelegate(
        keyboardOptions = KeyboardOptions.Default,
        uiInputText = { it.answers.single().valueStringType.value },
        uiValidationMessage = { q, context ->
          if (q.draftAnswer != null) {
            context.getString(
              com.google.android.fhir.datacapture.R.string.decimal_format_validation_error_msg,
            )
          } else {
            getValidationErrorMessage(
              context,
              q,
              q.validationResult,
            )
          }
        },
        handleInput = { text, q ->
          println("Hello => START")
          questionnaireViewItem =
            q.copy(
              questionnaireResponseItem =
                QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                  answer =
                    listOf(
                      QuestionnaireResponseItemAnswerComponent().apply { value = StringType(text) },
                    )
                },
            )
          println("Hello => END!!")
          println("Pending text change: $$pendingTextChange")
          pendingTextChange -= if (pendingTextChange > 0) 1 else 0
        },
      )

    composeTestRule.setContent { editTextViewHolderDelegate.Content(questionnaireViewItem) }
    composeTestRule.onRoot().printToLog("EditTextViewHolderDelegateTest")
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("Yellow")

    //        composeTestRule.onRoot().printToLog("EditTextViewHolderDelegateTest")
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("Yellow")

    println(questionnaireViewItem.answers.single().valueStringType.value)
    composeTestRule.unregisterIdlingResource(handlingTextIdlingResource)
  }
}
