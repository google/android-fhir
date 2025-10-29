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

package com.google.android.fhir.datacapture.contrib.views

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.QuestionnaireEditAdapter
import com.google.android.fhir.datacapture.QuestionnaireViewHolderType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhoneNumberViewHolderFactoryInstrumentedTest {

  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  private var pendingTextChange = 0
  private val handlingTextIdlingResource =
    object : IdlingResource {
      override val isIdleNow: Boolean
        get() = pendingTextChange == 0
    }

  @Before
  fun setUp() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = PhoneNumberViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
    composeTestRule.registerIdlingResource(handlingTextIdlingResource)
  }

  @After
  fun tearDown() {
    composeTestRule.unregisterIdlingResource(handlingTextIdlingResource)
  }

  @Test
  fun createViewHolder_phoneNumberViewHolderFactory_returnsViewHolder() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val viewHolderFromAdapter =
      questionnaireEditAdapter.createViewHolder(
        parent,
        QuestionnaireEditAdapter.ViewType.from(
            type = QuestionnaireEditAdapter.ViewType.Type.QUESTION,
            subtype = QuestionnaireViewHolderType.PHONE_NUMBER.value,
          )
          .viewType,
      ) as QuestionnaireEditAdapter.ViewHolder.QuestionHolder

    assertThat(
        viewHolderFromAdapter.holder.itemView.visibility,
      )
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldSetTextViewText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldSetInputText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = StringType("+12345678910")
            },
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("+12345678910")
  }

  @Test
  fun shouldSetInputTextToEmpty() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = StringType("+12345678910")
            },
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("")
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswer() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> = emptyList()
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, newAnswers, _ ->
          answers = newAnswers
          pendingTextChange -= if (pendingTextChange > 0) 1 else 0
        },
      )
    viewHolder.bind(questionnaireViewItem)
    composeTestRule
      .onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG)
      .performTextReplacement("+12345678910")
      .also { pendingTextChange += 1 }

    composeTestRule.waitForIdle()
    assertThat(answers.single().valueStringType.value).isEqualTo("+12345678910")
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswerToEmpty() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("")

    assertThat(questionnaireViewItem.answers).isEmpty()
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minLength"
            setValue(IntegerType("10"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = StringType("hello there")
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithContentDescription("Error").assertDoesNotExist()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { maxLength = 10 },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = StringType("+1234567891011")
            },
          )
        },
        validationResult =
          Invalid(
            listOf("The maximum number of characters that are permitted in the answer is: 10"),
          ),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    composeTestRule
      .onNodeWithText("The maximum number of characters that are permitted in the answer is: 10")
      .assertIsDisplayed()
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertIsNotEnabled()
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }
}
