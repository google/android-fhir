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
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.view.get
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.identifierString
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG
import com.google.android.fhir.datacapture.views.compose.MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.compose.REQUIRED_OPTIONAL_HEADER_TEXT_TAG
import com.google.android.fhir.datacapture.views.factories.AutoCompleteViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutoCompleteViewHolderFactoryTest {
  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = AutoCompleteViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question")
  }

  @Test
  fun shouldHaveSingleAnswerChip() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        repeats = false
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent()
            .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code")),
        )
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent()
            .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code")),
        )
      }

    viewHolder.bind(
      QuestionnaireViewItem(
        questionnaireItem,
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                questionnaireItem.answerOption
                  .first { it.value.displayString(viewHolder.itemView.context) == "Test1 Code" }
                  .valueCoding
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(1)
  }

  @Test
  fun shouldHaveTwoAnswerChipWithExternalValueSet() {
    val answers =
      listOf(
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code")),
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code")),
      )

    val fakeAnswerValueSetResolver = { uri: String ->
      if (uri == "http://answwer-value-set-url") {
        answers
      } else {
        emptyList()
      }
    }

    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        repeats = true
        answerValueSet = "http://answwer-value-set-url"
      }
    viewHolder.bind(
      QuestionnaireViewItem(
        questionnaireItem,
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                answers
                  .first { it.value.displayString(viewHolder.itemView.context) == "Test1 Code" }
                  .valueCoding
            },
          )

          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                answers
                  .first { it.value.displayString(viewHolder.itemView.context) == "Test2 Code" }
                  .valueCoding
            },
          )
        },
        enabledAnswerOptions = fakeAnswerValueSetResolver.invoke(questionnaireItem.answerValueSet),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(2)
  }

  @Test
  fun shouldHaveTwoAnswerChipWithAnswerOptionsHavingSameDisplayStringDifferentId() {
    val answers =
      listOf(
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(
            Coding().setCode("test1-code").setDisplay("Test Code").setId("test1-code") as Coding,
          ),
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(
            Coding()
              .setSystem("http://answers/test-codes")
              .setVersion("1.0")
              .setCode("test2-code")
              .setDisplay("Test Code") as Coding,
          ),
      )

    val fakeAnswerValueSetResolver = { uri: String ->
      if (uri == "http://answwer-value-set-url") {
        answers
      } else {
        emptyList()
      }
    }
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        repeats = true
        answerValueSet = "http://answwer-value-set-url"
      }
    viewHolder.bind(
      QuestionnaireViewItem(
        questionnaireItem,
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answers.first { it.value.id == "test1-code" }.valueCoding
            },
          )

          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                answers
                  .first {
                    it.value.identifierString(viewHolder.itemView.context) ==
                      "http://answers/test-codes1.0|test2-code"
                  }
                  .valueCoding
            },
          )
        },
        enabledAnswerOptions = fakeAnswerValueSetResolver.invoke(questionnaireItem.answerValueSet),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(2)
  }

  @Test
  fun shouldHaveSingleAnswerChipWithContainedAnswerValueSet() {
    val answers =
      listOf(
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code")),
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code")),
      )

    val fakeAnswerValueSetResolver = { uri: String ->
      if (uri == "http://answwer-value-set-url") {
        answers
      } else {
        emptyList()
      }
    }
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        repeats = false
        answerValueSet = "#ContainedValueSet"
      }

    viewHolder.bind(
      QuestionnaireViewItem(
        questionnaireItem,
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                answers
                  .first { it.value.displayString(viewHolder.itemView.context) == "Test1 Code" }
                  .valueCoding
            },
          )
        },
        enabledAnswerOptions = fakeAnswerValueSetResolver.invoke(questionnaireItem.answerValueSet),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG)).assertCountEquals(1)
  }

  @Test
  fun noDisplayString_shouldShowCode() {
    val answers =
      listOf(
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(Coding().setCode("test1-code"))
          .setInitialSelected(true),
      )

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answers.first().valueCoding
            },
          )
        },
        enabledAnswerOptions = answers,
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onAllNodes(hasTestTag(MULTI_AUTO_COMPLETE_INPUT_CHIP_TAG))
      .onFirst()
      .assertTextEquals("test1-code")
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Missing answer for required field.",
        ),
      )
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "display" }
            },
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "display" }
            },
          )
        },
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(MULTI_AUTO_COMPLETE_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.keyNotDefined(
          SemanticsProperties.Error,
        ),
      )
  }

  @Test
  fun hidesErrorTextviewInTheHeader() {
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
  fun showAsterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question"
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
      .isEqualTo("Question *")
  }

  @Test
  fun hideAsterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question"
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
      .isEqualTo("Question")
  }

  @Test
  fun showsRequiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      ),
    )

    composeTestRule
      .onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsDisplayed()
      .assertTextEquals("Required")
  }

  @Test
  fun hideRequiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      ),
    )

    composeTestRule
      .onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsNotDisplayed()
      .assertDoesNotExist()
  }

  @Test
  fun showsOptionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      ),
    )

    composeTestRule
      .onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsDisplayed()
      .assertTextEquals("Optional")
  }

  @Test
  fun hideOptionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      ),
    )

    composeTestRule
      .onNodeWithTag(REQUIRED_OPTIONAL_HEADER_TEXT_TAG)
      .assertIsNotDisplayed()
      .assertDoesNotExist()
  }
}
