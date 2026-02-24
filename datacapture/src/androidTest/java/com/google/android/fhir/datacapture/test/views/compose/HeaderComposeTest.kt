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

package com.google.android.fhir.datacapture.test.views.compose

import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.view.isVisible
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.DisplayItemControlType
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.HEADER_TAG
import com.google.android.fhir.datacapture.views.compose.HELP_BUTTON_TAG
import com.google.android.fhir.datacapture.views.compose.HELP_CARD_TAG
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeaderComposeTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun shouldShowPrefix() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }

    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.prefix).text.toString())
      .isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefix() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }

    assertThat(composeTestRule.activity.findViewById<TextView?>(R.id.prefix)).isNull()
  }

  @Test
  fun shouldShowQuestion() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        },
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }

    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldShowQuestionFromLocalizedText() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          textElement.apply {
            addExtension(
              Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                addExtension(Extension("lang", StringType("en")))
                addExtension(Extension("content", StringType("Question?")))
              },
            )
          }
        },
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }

    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldShowQuestionFromQuestionnaireResponseItemDerivedText() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply { repeats = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { text = "Question?" },
      )

    composeTestRule.setContent { Header(questionnaireViewItem) }
    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun showsInstructions() {
    val itemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "nested-display-question"
          text = "subtitle text"
          extension = listOf(displayCategoryExtensionWithInstructionsCode)
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        },
      )
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
        Questionnaire.QuestionnaireItemComponent().apply { item = itemList },
        itemList,
      )

    composeTestRule.setContent { Header(questionnaireViewItem) }

    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.hint).isVisible).isTrue()
    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.hint).text.toString())
      .isEqualTo("subtitle text")
  }

  @Test
  fun hidesInstructions() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              },
            )
        },
      )

    composeTestRule.setContent { Header(questionnaireViewItem) }

    assertThat(composeTestRule.activity.findViewById<TextView?>(R.id.hint)).isNull()
  }

  @Test
  fun showsHelpButtonIfHelpCodeIsPresent() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(itemControlExtensionWithHelpCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              },
            )
        },
      )

    composeTestRule.setContent { Header(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(HEADER_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(HELP_BUTTON_TAG).assertIsDisplayed()
  }

  @Test
  fun hidesHelpButtonIfHelpCodeIsNotPresent() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              },
            )
        },
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(HELP_BUTTON_TAG).assertDoesNotExist()
  }

  @Test
  fun showsHelpCardViewOnHelpButtonClick() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(itemControlExtensionWithHelpCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              },
            )
        },
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(HELP_BUTTON_TAG).performClick()
    composeTestRule.onNodeWithTag(HELP_CARD_TAG).assertIsDisplayed()
  }

  @Test
  fun hidesHelpCardViewOnHelpButtonClickIfHelpCardViewWasAlreadyVisible() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(itemControlExtensionWithHelpCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              },
            )
        },
      )

    composeTestRule.setContent { Header(questionnaireViewItem) }

    composeTestRule.onNodeWithTag(HELP_BUTTON_TAG).performClick()
    composeTestRule.onNodeWithTag(HELP_CARD_TAG).assertIsDisplayed()

    composeTestRule.onNodeWithTag(HELP_BUTTON_TAG).performClick()
    composeTestRule.onNodeWithTag(HELP_CARD_TAG).assertIsNotDisplayed()
  }

  @Test
  fun updatesTextviewForHelpCodeText() {
    val itemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "nested-display-question"
          text = "help text"
          extension = listOf(itemControlExtensionWithHelpCode)
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        },
      )
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
        Questionnaire.QuestionnaireItemComponent().apply { item = itemList },
        itemList,
      )

    composeTestRule.setContent { Header(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(HELP_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()

    assertThat(composeTestRule.activity.findViewById<TextView>(R.id.helpText).text.toString())
      .isEqualTo("help text")
  }

  @Test
  fun showsHeaderItemView() {
    val itemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "nested-display-question"
          text = "subtitle text"
          extension = listOf(displayCategoryExtensionWithInstructionsCode)
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        },
      )
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
        Questionnaire.QuestionnaireItemComponent().apply { item = itemList },
        itemList,
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(HEADER_TAG).assertIsDisplayed()
  }

  @Test
  fun shouldHideHeaderView() {
    val questionnaireViewItem =
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              },
            )
        },
      )
    composeTestRule.setContent { Header(questionnaireViewItem) }
    composeTestRule.onNodeWithTag(HEADER_TAG).assertIsNotDisplayed()
  }

  @Test
  fun showsRequiredText() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
        questionnaireItem =
          Questionnaire.QuestionnaireItemComponent().apply {
            text = "Question?"
            required = true
          },
        questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    composeTestRule.setContent { Header(questionnaireViewItem, showRequiredOrOptionalText = true) }
    val requiredText = composeTestRule.activity.getString(R.string.required)

    composeTestRule.onNodeWithText(requiredText).assertIsDisplayed()
  }

  @Test
  fun showOptionalText() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
        questionnaireItem =
          Questionnaire.QuestionnaireItemComponent().apply {
            text = "Question?"
            required = false
          },
        questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    composeTestRule.setContent { Header(questionnaireViewItem, showRequiredOrOptionalText = true) }
    val optionalText = composeTestRule.activity.getString(R.string.optional_helper_text)
    composeTestRule.onNodeWithText(optionalText).assertIsDisplayed()
  }

  @Test
  fun showsErrorText() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem = Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("missing answer for required field")),
        answersChangedCallback = { _, _, _, _ -> },
      )

    composeTestRule.setContent { Header(questionnaireViewItem, displayValidationResult = true) }
    composeTestRule
      .onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG)
      .assertIsDisplayed()
      .assertTextEquals("missing answer for required field")
  }

  @Test
  fun hidesErrorTextWhenNotValidated() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem = Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    composeTestRule.setContent { Header(questionnaireViewItem, displayValidationResult = true) }
    composeTestRule.onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertIsNotDisplayed()
  }

  @Test
  fun hidesErrorTextWhenValid() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem = Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      )

    composeTestRule.setContent { Header(questionnaireViewItem, displayValidationResult = true) }
    composeTestRule.onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertIsNotDisplayed()
  }

  private fun getQuestionnaireViewItemWithQuestionnaireItem(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent =
      QuestionnaireResponse.QuestionnaireResponseItemComponent(),
  ): QuestionnaireViewItem {
    return QuestionnaireViewItem(
      questionnaireItem = questionnaireItem,
      questionnaireResponseItem = questionnaireResponseItem,
      validationResult = Valid,
      answersChangedCallback = { _, _, _, _ -> },
    )
  }

  private fun getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    enabledDisplayItems: List<Questionnaire.QuestionnaireItemComponent>,
  ): QuestionnaireViewItem {
    return QuestionnaireViewItem(
      questionnaireItem = questionnaireItem,
      questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent(),
      validationResult = Valid,
      answersChangedCallback = { _, _, _, _ -> },
      enabledDisplayItems = enabledDisplayItems,
    )
  }

  private val displayCategoryExtensionWithInstructionsCode =
    Extension().apply {
      url = EXTENSION_DISPLAY_CATEGORY_URL
      setValue(
        CodeableConcept().apply {
          coding =
            listOf(
              Coding().apply {
                code = EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS
                system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
              },
            )
        },
      )
    }

  private val itemControlExtensionWithHelpCode =
    Extension().apply {
      url = EXTENSION_ITEM_CONTROL_URL
      setValue(
        CodeableConcept().apply {
          coding =
            listOf(
              Coding().apply {
                code = DisplayItemControlType.HELP.extensionCode
                system = EXTENSION_ITEM_CONTROL_SYSTEM
              },
            )
        },
      )
    }
}
