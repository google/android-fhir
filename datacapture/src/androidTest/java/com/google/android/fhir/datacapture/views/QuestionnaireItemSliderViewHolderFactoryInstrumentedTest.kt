/*
 * Copyright 2021 Google LLC
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

import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.material.slider.Slider
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemSliderViewHolderFactoryInstrumentedTest {
  private lateinit var context: ContextThemeWrapper
  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setUp() {
    context =
      ContextThemeWrapper(
        InstrumentationRegistry.getInstrumentation().targetContext,
        R.style.Theme_MaterialComponents
      )
    parent = FrameLayout(context)
    viewHolder = QuestionnaireItemSliderViewHolderFactory.create(parent)
  }

  @Test
  fun shouldSetQuestionHeader() {
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).text.toString())
      .isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix_text_view).isVisible)
      .isFalse()
  }

  @Test
  fun shouldSetHeaderTextViewText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun shouldSetSliderValue() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            }
          )
        }
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(10)
  }

  @Test
  fun shouldSetQuestionnaireResponseSliderAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}

    viewHolder.bind(questionnaireItemViewItem, 0)
    viewHolder.itemView.findViewById<Slider>(R.id.slider).value = 10.0F

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    assertThat(answer.size).isEqualTo(1)
    assertThat(answer[0].valueIntegerType.value).isEqualTo(10)
  }

  @Test
  fun shouldSetSliderValueToDefault() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            }
          )
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            }
          )
        }
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(0.0F)
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
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
            }
          )
        }
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text.toString()).isEqualTo("")
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
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
            }
          )
        }
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text.toString())
      .isEqualTo("Minimum value allowed is:50")
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {},
      0
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).isEnabled).isFalse()
  }
}
