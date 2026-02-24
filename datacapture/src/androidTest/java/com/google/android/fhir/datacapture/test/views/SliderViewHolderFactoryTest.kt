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
import android.widget.TextView
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.EXTENSION_SLIDER_STEP_VALUE_URL
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_TAG
import com.google.android.fhir.datacapture.views.compose.SLIDER_TAG
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.factories.SliderViewHolderFactory
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SliderViewHolderFactoryTest {

  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = SliderViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun shouldSetQuestionHeader() {
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
  fun shouldSetSliderValue() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 10f, range = 0f..100f, steps = 99))
  }

  @Test
  fun stepSizeShouldComeFromTheSliderStepValueExtension() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "slider-step-value"
          addExtension(EXTENSION_SLIDER_STEP_VALUE_URL, IntegerType(10))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    val sliderStepsFromStepSize10: Int = 100.div(10) - 1

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(
        ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = sliderStepsFromStepSize10),
      )
  }

  @Test
  fun stepSizeShouldBe1IfTheSliderStepValueExtensionIsNotPresent() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "slider-step-value" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    val sliderStepsWithStepSize1: Int = 100 - 1

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(
        ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = sliderStepsWithStepSize1),
      )
  }

  @Test
  fun sliderValueToShouldComeFromTheMaxValueExtension() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(IntegerType("200"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0f, range = 0f..200f, steps = 199))
  }

  @Test
  fun sliderValueToShouldBeSetToDefaultValueIfMaxValueExtensionIsNotPresent() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = 99))
  }

  @Test
  fun sliderValueFromShouldComeFromTheMinValueExtension() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(IntegerType("50"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 50f, range = 50f..100f, steps = 49))
  }

  @Test
  fun sliderValueFromShouldBeSetToDefaultValueIfMinValueExtensionIsNotPresent() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = 99))
  }

  @Test
  fun throwsExceptionIfMinValueIsGreaterThanMaxvalue() {
    assertThrows(
      IllegalStateException::class.java,
      {
        viewHolder.bind(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              addExtension().apply {
                url = "http://hl7.org/fhir/StructureDefinition/minValue"
                setValue(IntegerType("100"))
              }
              addExtension().apply {
                url = "http://hl7.org/fhir/StructureDefinition/maxValue"
                setValue(IntegerType("50"))
              }
            },
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        )

        // Wait for synchronization
        composeTestRule.waitForIdle()
      },
    )
  }

  @Test
  fun shouldSetQuestionnaireResponseSliderAnswer() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(SLIDER_TAG).performSemanticsAction(SemanticsActions.SetProgress) {
      it.invoke(20f)
    }
    // Synchronize
    composeTestRule.waitForIdle()

    assertThat(answerHolder!!.single().valueIntegerType.value).isEqualTo(20)
  }

  @Test
  fun shouldSetSliderValueToDefaultWhenQuestionnaireResponseHasMultipleAnswers() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            },
          )
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(0.0f, 0.0f..100f, steps = 99))
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(IntegerType("50"))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(IntegerType("100"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType("75")
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(ERROR_TEXT_TAG).assertIsNotDisplayed().assertDoesNotExist()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(IntegerType("50"))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(IntegerType("100"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType("25")
            },
          )
        },
        validationResult = Invalid(listOf("Minimum value allowed is:50")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(ERROR_TEXT_TAG).assertTextEquals("Minimum value allowed is:50")
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
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule.onNodeWithTag(SLIDER_TAG).assertIsNotEnabled()
  }

  @Test
  fun bindMultipleTimesWithDifferentQuestionnaireItemViewItemShouldShowProperSliderValue() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(10f, 0f..100f, steps = 99))

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(12)
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(12f, 0f..100f, steps = 99))

    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(IntegerType("50"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(50f, 50f..100f, steps = 49))
  }

  @Test
  fun hidesAsterisk() {
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

    composeTestRule.onNodeWithText("Required").assertIsDisplayed()
  }

  @Test
  fun hidesRequiredtext() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      ),
    )

    composeTestRule.onNodeWithText("Required").assertIsNotDisplayed().assertDoesNotExist()
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

    composeTestRule.onNodeWithText("Optional").assertIsDisplayed()
  }

  @Test
  fun hidesOptionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      ),
    )

    composeTestRule.onNodeWithText("Optional").assertIsNotDisplayed().assertDoesNotExist()
  }
}
