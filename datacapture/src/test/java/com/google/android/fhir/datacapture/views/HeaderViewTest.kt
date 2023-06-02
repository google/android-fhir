/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.views

import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.DisplayItemControlType
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.INSTRUCTIONS
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.material.card.MaterialCardView
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class HeaderViewTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val view = HeaderView(parent.context, null)

  private fun getQuestionnaireViewItemWithQuestionnaireItem(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent =
      QuestionnaireResponse.QuestionnaireResponseItemComponent()
  ): QuestionnaireViewItem {
    return QuestionnaireViewItem(
      questionnaireItem = questionnaireItem,
      questionnaireResponseItem = questionnaireResponseItem,
      validationResult = Valid,
      answersChangedCallback = { _, _, _, _ -> }
    )
  }

  private fun getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    enabledDisplayItems: List<Questionnaire.QuestionnaireItemComponent>
  ): QuestionnaireViewItem {
    return QuestionnaireViewItem(
      questionnaireItem = questionnaireItem,
      questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent(),
      validationResult = Valid,
      answersChangedCallback = { _, _, _, _ -> },
      enabledDisplayItems = enabledDisplayItems
    )
  }

  @Test
  fun shouldShowPrefix() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" }
      )
    )

    assertThat(view.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(view.findViewById<TextView>(R.id.prefix).text.toString()).isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefix() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" }
      )
    )

    assertThat(view.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  fun shouldShowQuestion() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        }
      )
    )

    assertThat(view.findViewById<TextView>(R.id.question).text.toString()).isEqualTo("Question?")
  }

  @Test
  fun `should show question from localized text`() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          textElement.apply {
            addExtension(
              Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                addExtension(Extension("lang", StringType("en")))
                addExtension(Extension("content", StringType("Question?")))
              }
            )
          }
        }
      )
    )

    assertThat(view.findViewById<TextView>(R.id.question).text.toString()).isEqualTo("Question?")
  }

  @Test
  fun `should show question from questionnaire response item derived text`() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply { repeats = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { text = "Question?" }
      )
    )

    assertThat(view.findViewById<TextView>(R.id.question).text.toString()).isEqualTo("Question?")
  }

  @Test
  fun `shows instructions`() {
    val itemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "nested-display-question"
          text = "subtitle text"
          extension = listOf(displayCategoryExtensionWithInstructionsCode)
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        }
      )
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
        Questionnaire.QuestionnaireItemComponent().apply { item = itemList },
        itemList
      )
    )

    assertThat(view.findViewById<TextView>(R.id.hint).isVisible).isTrue()
    assertThat(view.findViewById<TextView>(R.id.hint).text.toString()).isEqualTo("subtitle text")
  }

  @Test
  fun `hides instructions`() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    )

    assertThat(view.findViewById<TextView>(R.id.hint).visibility).isEqualTo(View.GONE)
  }

  @Test
  fun `shows helpButton if help code is present`() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(itemControlExtensionWithHelpCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    )

    assertThat(view.findViewById<Button>(R.id.helpButton).isVisible).isTrue()
  }

  @Test
  fun `hides helpButton if help code is not present`() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    )

    assertThat(view.findViewById<Button>(R.id.helpButton).isVisible).isFalse()
  }

  @Test
  fun `shows helpCardView on help button click`() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(itemControlExtensionWithHelpCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    )
    view.findViewById<Button>(R.id.helpButton).performClick()

    assertThat(view.findViewById<MaterialCardView>(R.id.helpCardView).isVisible).isTrue()
  }

  @Test
  fun `hides helpCardView on help button click if help card view was already visible`() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "help text"
                extension = listOf(itemControlExtensionWithHelpCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    )
    view.findViewById<Button>(R.id.helpButton).performClick()
    view.findViewById<Button>(R.id.helpButton).performClick()

    assertThat(view.findViewById<MaterialCardView>(R.id.helpCardView).isVisible).isFalse()
  }

  @Test
  fun `updates textview for help code Text`() {
    val itemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "nested-display-question"
          text = "help text"
          extension = listOf(itemControlExtensionWithHelpCode)
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        }
      )
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
        Questionnaire.QuestionnaireItemComponent().apply { item = itemList },
        itemList
      )
    )

    assertThat(view.findViewById<TextView>(R.id.helpText).text.toString()).isEqualTo("help text")
  }

  @Test
  fun `shows headerItem view`() {
    val itemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "nested-display-question"
          text = "subtitle text"
          extension = listOf(displayCategoryExtensionWithInstructionsCode)
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        }
      )
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItemAndEnabledDisplayItems(
        Questionnaire.QuestionnaireItemComponent().apply { item = itemList },
        itemList
      )
    )

    assertThat(view.visibility).isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldHideHeaderView() {
    view.bind(
      getQuestionnaireViewItemWithQuestionnaireItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    )

    assertThat(view.visibility).isEqualTo(View.GONE)
  }

  @Test
  fun `shows error text`() {
    view.showErrorText("missing answer for required field")
    assertThat(view.findViewById<TextView>(R.id.error_text_at_header).text.toString())
      .isEqualTo("missing answer for required field")
    assertThat(view.findViewById<TextView>(R.id.error_text_at_header).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun `hides error text`() {
    view.showErrorText(isErrorTextVisible = false)
    assertThat(view.findViewById<TextView>(R.id.error_text_at_header).visibility)
      .isEqualTo(View.GONE)
  }

  private val displayCategoryExtensionWithInstructionsCode =
    Extension().apply {
      url = EXTENSION_DISPLAY_CATEGORY_URL
      setValue(
        CodeableConcept().apply {
          coding =
            listOf(
              Coding().apply {
                code = INSTRUCTIONS
                system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
              }
            )
        }
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
              }
            )
        }
      )
    }
}
