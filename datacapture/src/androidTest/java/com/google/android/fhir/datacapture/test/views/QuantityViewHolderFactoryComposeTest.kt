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
import android.widget.TextView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DROP_DOWN_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.QuantityViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuantityViewHolderFactoryComposeTest {

  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = QuantityViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun shouldSetQuestionText() {
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
  fun shouldSetInputDecimalValue() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { value = BigDecimal("5") }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("5")
  }

  @Test
  fun shouldClearInputDecimalValue() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { value = BigDecimal("5") }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("5")

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
  fun shouldSetUnitValue() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { unit = "kg" }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("kg")
  }

  @Test
  fun shouldSetUnitValueFromInitialWhenAnswerIsMissing() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addInitial(
            Questionnaire.QuestionnaireItemInitialComponent(
              Quantity().apply {
                this.unit = "kg"
                this.code = "kilo"
              },
            ),
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("kg")
  }

  @Test
  fun shouldClearUnitValue() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity().apply { unit = "kg" }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("kg")

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("")
  }

  @Test
  fun shouldDisplayErrorMessageInValidationResult() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    composeTestRule.onNodeWithText("Missing answer for required field.").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayNoErrorMessageWhenValidationResultIsValid() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Quantity(22.5)
            },
          )
        },
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithContentDescription("Error").assertDoesNotExist()
  }

  @Test
  fun shouldDisableTextInputInReadOnlyMode() {
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

  @Test
  fun shouldDisableUnitInputInReadOnlyMode() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertIsNotEnabled()
  }

  @Test
  fun shouldAlwaysHideErrorTextviewInTheHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG)
      .assertIsNotDisplayed()
      .assertDoesNotExist()
  }

  @Test
  fun shouldSetDraftWithUnit() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem = createQuestionnaireViewItem { answers, draft ->
      answerHolder = answers
      draftHolder = draft
    }

    viewHolder.bind(questionnaireViewItem)
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

    viewHolder.bind(questionnaireViewItem)

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

    viewHolder.bind(questionnaireViewItem)
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

    viewHolder.bind(questionnaireViewItem)
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

  @Test
  fun shouldShowAsterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      ),
    )
    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question? *")
  }

  @Test
  fun shouldHideAsterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      ),
    )

    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldShowRequiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      ),
    )

    composeTestRule.onNodeWithText("Required").assertIsDisplayed()
  }

  @Test
  fun shouldHideRequiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      ),
    )

    composeTestRule.onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun shouldShowOptionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      ),
    )

    composeTestRule.onNodeWithText("Optional").assertIsDisplayed()
  }

  @Test
  fun shouldHideOptionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      ),
    )

    composeTestRule.onNodeWithText("Optional").assertDoesNotExist()
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
}
