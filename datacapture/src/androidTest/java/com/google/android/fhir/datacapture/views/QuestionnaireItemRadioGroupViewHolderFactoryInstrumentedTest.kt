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

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.EXTENSION_CHOICE_ORIENTATION_URL
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemRadioGroupViewHolderFactoryInstrumentedTest {
  private val parent =
    FrameLayout(
      ContextThemeWrapper(
        InstrumentationRegistry.getInstrumentation().targetContext,
        R.style.Theme_MaterialComponents
      )
    )
  private val viewHolder = QuestionnaireItemRadioGroupViewHolderFactory.create(parent)

  @Test
  fun bind_shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun bind_vertical_shouldCreateRadioButtons() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          EXTENSION_CHOICE_ORIENTATION_URL,
          CodeType(ChoiceOrientationTypes.VERTICAL.extensionCode)
        )
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = "Coding 1" }
          }
        )
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = "Coding 2" }
          }
        )
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        questionnaire,
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    val radioGroup = viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group)
    val children = radioGroup.children.asIterable().filterIsInstance<RadioButton>()
    children.forEachIndexed { index, view ->
      assertThat(view.text).isEqualTo(questionnaire.answerOption[index].valueCoding.display)
      assertThat(view.layoutParams.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)
    }
  }

  @Test
  fun bind_horizontal_shouldCreateRadioButtons() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          EXTENSION_CHOICE_ORIENTATION_URL,
          CodeType(ChoiceOrientationTypes.HORIZONTAL.extensionCode)
        )
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = "Coding 1" }
          }
        )
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = "Coding 2" }
          }
        )
      }
    viewHolder.bind(
      QuestionnaireItemViewItem(
        questionnaire,
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    val radioGroup = viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group)
    val children = radioGroup.children.asIterable().filterIsInstance<RadioButton>()
    children.forEachIndexed { index, view ->
      assertThat(view.text).isEqualTo(questionnaire.answerOption[index].valueCoding.display)
      assertThat(view.layoutParams.width).isEqualTo(ViewGroup.LayoutParams.WRAP_CONTENT)
    }
  }

  @Test
  fun bind_noAnswer_shouldLeaveRadioButtonsUnchecked() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    val radioButton =
      viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1) as
        RadioButton
    assertThat(radioButton.isChecked).isFalse()
  }

  @Test
  @UiThreadTest
  fun bind_answer_shouldCheckRadioButton() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 2" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        }
      ) {}
    )

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1) as
            RadioButton)
          .isChecked
      )
      .isTrue()
    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(2) as
            RadioButton)
          .isChecked
      )
      .isFalse()
  }

  @Test
  @UiThreadTest
  fun click_shouldSetQuestionnaireResponseItemAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder
      .itemView
      .findViewById<ConstraintLayout>(R.id.radio_group)
      .getChildAt(1)
      .performClick()

    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer
    assertThat(answer.size).isEqualTo(1)
    assertThat(answer[0].valueCoding.display).isEqualTo("Coding 1")
  }

  @Test
  @UiThreadTest
  fun click_shouldCheckRadioButton() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          answerValueSet = "http://coding-value-set-url"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        resolveAnswerValueSet = {
          if (it == "http://coding-value-set-url") {
            listOf(
              Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                value = Coding().apply { display = "Coding 1" }
              },
              Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                value = Coding().apply { display = "Coding 2" }
              }
            )
          } else {
            emptyList()
          }
        }
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    viewHolder
      .itemView
      .findViewById<ConstraintLayout>(R.id.radio_group)
      .getChildAt(1)
      .performClick()

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1) as
            RadioButton)
          .isChecked
      )
      .isTrue()
    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(2) as
            RadioButton)
          .isChecked
      )
      .isFalse()
  }

  @Test
  @UiThreadTest
  fun click_shouldCheckOtherRadioButton() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 2" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        }
      ) {}
    )

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1) as
            RadioButton)
          .isChecked
      )
      .isTrue()

    viewHolder
      .itemView
      .findViewById<ConstraintLayout>(R.id.radio_group)
      .getChildAt(2)
      .performClick()

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(2) as
            RadioButton)
          .isChecked
      )
      .isTrue()
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  @UiThreadTest
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "display" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "display" }
            }
          )
        }
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text.isEmpty()).isTrue()
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          readOnly = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )
    val radioButton =
      viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1) as
        RadioButton

    assertThat(radioButton.isEnabled).isFalse()
  }
}
