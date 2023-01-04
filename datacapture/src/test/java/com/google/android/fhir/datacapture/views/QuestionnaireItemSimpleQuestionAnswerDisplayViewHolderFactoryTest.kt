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

import android.app.Application
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.DisplayItemControlType
import com.google.android.fhir.datacapture.EXTENSION_DISPLAY_CATEGORY_SYSTEM
import com.google.android.fhir.datacapture.EXTENSION_DISPLAY_CATEGORY_URL
import com.google.android.fhir.datacapture.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.INSTRUCTIONS
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.material.divider.MaterialDivider
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemSimpleQuestionAnswerDisplayViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val viewHolder =
    QuestionnaireItemSimpleQuestionAnswerDisplayViewHolderFactory.create(parent)

  @Test
  fun `bind() should set question header`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun `bind() should set fly over text visibility gone`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.flyover_text_view).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `bind() should set fly over text`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                text = "flyover text"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = DisplayItemControlType.FLYOVER.extensionCode
                    }
                  }
                )
              }
            )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.flyover_text_view).text.toString())
      .isEqualTo("flyover text")
  }

  @Test
  fun `bind() should set answer text visibility gone when question type display`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `bind() should set answer text visibility gone when question type group`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.GROUP
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `bind() should set answer text not answered when empty answer`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).text)
      .isEqualTo(
        ApplicationProvider.getApplicationContext<Application>().getString(R.string.not_answered)
      )
  }

  @Test
  fun `bind() should set answer text when answered`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          ),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).text)
      .isEqualTo(ApplicationProvider.getApplicationContext<Application>().getString(R.string.yes))
  }

  @Test
  fun `bind() should set divider visibility gone`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `bind() should show divider with header`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun `bind() should show divider with fly over text`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                text = "flyover text"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = DisplayItemControlType.FLYOVER.extensionCode
                    }
                  }
                )
              }
            )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun `bind() should show divider with answer`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          ),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun `shows error icon if answer is not present`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<View>(R.id.error_icon_in_review_mode).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun `does not show error icon if answer is present`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          ),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<View>(R.id.error_icon_in_review_mode).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `shows prefix text`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).text.toString())
      .isEqualTo("Prefix?")
  }

  @Test
  fun `hides prefix text`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  fun `shows question text`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun `shows instructions`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "subtitle text"
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.hint).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.hint).text.toString())
      .isEqualTo("subtitle text")
  }

  @Test
  fun `hides instructions`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.hint).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `shows headerItem view`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "subtitle text"
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.visibility).isEqualTo(View.VISIBLE)
  }

  @Test
  fun `hides headerItem view`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `shows * at the end of question text`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question? *")
  }

  @Test
  fun `does not show * at the end of question text`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun `shows * even if question text is missing`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Valid,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo(
        viewHolder.itemView.context.applicationContext.resources.getString(R.string.space_asterisk)
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
                code = INSTRUCTIONS
                system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
              }
            )
        }
      )
    }
}
