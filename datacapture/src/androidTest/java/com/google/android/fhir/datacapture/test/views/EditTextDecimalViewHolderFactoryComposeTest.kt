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
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.UNIT_TEXT_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.EditTextDecimalViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditTextDecimalViewHolderFactoryComposeTest {

  @get:Rule
  val activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder
  private lateinit var parent: FrameLayout

  private var pendingTextChange = 0
  private val handlingTextIdlingResource =
    object : IdlingResource {
      override val isIdleNow: Boolean
        get() = pendingTextChange == 0
    }

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity ->
      parent = FrameLayout(activity)
      viewHolder = EditTextDecimalViewHolderFactory.create(parent)
      activity.setContentView(viewHolder.itemView)
    }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()

    composeTestRule.registerIdlingResource(handlingTextIdlingResource)
  }

  @After
  fun tearDown() {
    composeTestRule.unregisterIdlingResource(handlingTextIdlingResource)
  }

  @Test
  fun shouldSetQuestionnaireHeader() {
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
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = DecimalType("1.1")
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("1.1")
  }

  @Test
  fun shouldSetInputTextToEmpty() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = DecimalType("1.1")
            },
          )
        },
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
  fun shouldSetUnitText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          type = Questionnaire.QuestionnaireItemType.DECIMAL
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unit"
              setValue(Coding().apply { code = "kg" })
            },
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = DecimalType("1.1")
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(UNIT_TEXT_TEST_TAG).assertTextEquals("kg")
  }

  @Test
  fun shouldClearUnitText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          type = Questionnaire.QuestionnaireItemType.DECIMAL
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unit"
              setValue(Coding().apply { code = "kg" })
            },
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = DecimalType("1.1")
            },
          )
        },
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
  fun shouldSetQuestionnaireResponseItemAnswerIfTextIsValid() {
    var answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, result, _ ->
          answers = result
          pendingTextChange -= if (pendingTextChange > 0) 1 else 0
        },
      )

    viewHolder.bind(questionnaireViewItem)
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("1.1").also {
      pendingTextChange += 1
    }
    composeTestRule.waitForIdle()

    assertThat(answers!!.single().valueDecimalType.value).isEqualTo(BigDecimal.valueOf(1.1))
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
    composeTestRule.waitForIdle()

    assertThat(questionnaireViewItem.answers).isEmpty()
  }

  @Test
  fun shouldSetDraftAnswerIfTextIsInvalid() {
    var draftAnswer: Any? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, result ->
          draftAnswer = result
          pendingTextChange -= if (pendingTextChange > 0) 1 else 0
        },
      )
    viewHolder.bind(questionnaireViewItem)
    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("1.1.1.1").also {
      pendingTextChange += 1
    }
    composeTestRule.waitForIdle()
    assertThat(draftAnswer as String).isEqualTo("1.1.1.1")
  }

  @Test
  fun displayValidationResultShouldShowNoErrorMesssage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(DecimalType("2.2"))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(DecimalType("4.4"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = DecimalType("3.3")
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
  fun displayValidationResultShouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(DecimalType("2.1"))
          }
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(DecimalType("4.2"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = DecimalType("1.1")
            },
          )
        },
        validationResult = Invalid(listOf("Minimum value allowed is:2.1")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    composeTestRule.onNodeWithText("Minimum value allowed is:2.1").assertIsDisplayed()
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

    composeTestRule.onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertIsNotDisplayed()
  }

  @Test
  fun bindReadOnlyShouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertIsDisplayed().assertIsNotEnabled()
  }

  @Test
  fun showAsterisk() {
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
  fun hideAsterisk() {
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
    composeTestRule.onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun showOptionalText() {
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
  fun hideOptionalText() {
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

  @Test
  fun bindAgainShouldRemovePreviousText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        draftAnswer = "1.1.1.1",
      ),
    )

    composeTestRule
      .onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)
      .assertTextEquals("1.1.1.1")
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true)
      .assertTextEquals("")
  }
}
