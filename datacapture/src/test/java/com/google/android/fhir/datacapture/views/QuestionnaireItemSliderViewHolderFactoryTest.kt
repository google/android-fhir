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
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.EXTENSION_SLIDER_STEP_VALUE_URL
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.material.slider.Slider
import com.google.common.truth.Truth.assertThat
import java.lang.IllegalStateException
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemSliderViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val viewHolder = QuestionnaireItemSliderViewHolderFactory.create(parent)

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(10)
  }

  @Test
  fun `step size should come from the sliderStepValue extension`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "slider-step-value"
          addExtension(EXTENSION_SLIDER_STEP_VALUE_URL, IntegerType(10))
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).stepSize).isEqualTo(10)
  }

  @Test
  fun `step size should be 1 if the sliderStepValue extension is not present`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "slider-step-value" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).stepSize).isEqualTo(1)
  }

  @Test
  fun `slider valueTo should come from the maxValue extension`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/maxValue"
            setValue(IntegerType("200"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).valueTo).isEqualTo(200)
  }

  @Test
  fun `slider valueTo should be set to default value if maxValue extension is not present`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).valueTo).isEqualTo(100.0F)
  }

  @Test
  fun `slider valueFrom should come from the maxValue extension`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(IntegerType("50"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).valueFrom).isEqualTo(50)
  }

  @Test
  fun `slider valueFrom should be set to default value if minValue extension is not present`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).valueFrom).isEqualTo(0.0F)
  }

  @Test
  fun `throws exception if minValue is greater than maxvalue`() {
    assertFailsWith<IllegalStateException> {
      viewHolder.bind(
        QuestionnaireItemViewItem(
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
        )
      )
    }
  }

  @Test
  fun shouldSetQuestionnaireResponseSliderAnswer() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireItemViewItem)
    viewHolder.itemView.findViewById<Slider>(R.id.slider).value = 10.0F

    assertThat(answerHolder!!.single().valueIntegerType.value).isEqualTo(10)
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(0.0F)
  }

  @Test
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text.toString()).isEqualTo("")
  }

  @Test
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
        },
        validationResult = Invalid(listOf("Minimum value allowed is:50")),
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text.toString())
      .isEqualTo("Minimum value allowed is:50")
  }

  @Test
  fun `hides error textview in the header`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { readOnly = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).isEnabled).isFalse()
  }

  @Test
  fun `bind multiple times with different QuestionnaireItemViewItem should show proper slider value`() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(10)
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(10)

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = IntegerType(12)
            }
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(12)

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addExtension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/minValue"
            setValue(IntegerType("50"))
          }
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<Slider>(R.id.slider).value).isEqualTo(50)
  }
}
