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
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.DisplayItemControlType
import com.google.android.fhir.datacapture.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.R
import com.google.android.material.divider.MaterialDivider
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
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
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_MaterialComponents) }
    )
  private val viewHolder =
    QuestionnaireItemSimpleQuestionAnswerDisplayViewHolderFactory.create(parent)

  @Test
  fun bind_shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun bind_shouldSetFlyoverTextVisibilityGone() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.flyover_text_view).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun bind_shouldSetFlyoverText() {
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
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.flyover_text_view).text.toString())
      .isEqualTo("flyover text")
  }

  @Test
  fun bind_withQuestionTypeDisplay_shouldSetAnswerTextVisibilityGone() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun bind_withQuestionTypeGroup_shouldSetAnswerTextVisibilityGone() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.GROUP
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun bind_withEmptyAnswer_shouldSetAnswerTextNotAnswered() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).text)
      .isEqualTo(
        ApplicationProvider.getApplicationContext<Application>().getString(R.string.not_answered)
      )
  }

  @Test
  fun bind_shouldSetAnswerText() {
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
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.answer_text_view).text)
      .isEqualTo(ApplicationProvider.getApplicationContext<Application>().getString(R.string.yes))
  }

  @Test
  fun bind_shouldSetDividerVisibilityGone() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          type = Questionnaire.QuestionnaireItemType.DISPLAY
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun bind_withHeader_shouldSetDividerVisible() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun bind_withFlyOverText_shouldSetDividerVisible() {
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
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun bind_withAnswer_shouldSetDividerVisible() {
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
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<MaterialDivider>(R.id.text_divider).visibility)
      .isEqualTo(View.VISIBLE)
  }
}
