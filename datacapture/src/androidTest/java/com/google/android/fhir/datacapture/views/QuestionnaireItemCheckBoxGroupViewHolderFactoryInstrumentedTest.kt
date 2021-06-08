/*
 * Copyright 2020 Google LLC
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

import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemCheckBoxGroupViewHolderFactoryInstrumentedTest {
  private val parent = FrameLayout(InstrumentationRegistry.getInstrumentation().context)
  private val viewHolder = QuestionnaireItemCheckBoxGroupViewHolderFactory.create(parent)

  @Test
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          prefix = "Prefix?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).text).isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          prefix = ""
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  fun bind_shouldSetHeaderText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.checkbox_group_header).text)
      .isEqualTo("Question?")
  }

  @Test
  fun bind_shouldCreateCheckBoxButtons() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    val checkBoxGroup = viewHolder.itemView.findViewById<LinearLayout>(R.id.checkbox_group)
    assertThat(checkBoxGroup.childCount).isEqualTo(2)
    val linearLayoutGroup1 = checkBoxGroup.getChildAt(0) as LinearLayout
    assertThat(linearLayoutGroup1.childCount).isEqualTo(2)
    val answerOptionText1 = linearLayoutGroup1.getChildAt(1) as TextView
    assertThat(answerOptionText1.text).isEqualTo("Coding 1")
    val linearLayoutGroup2 = checkBoxGroup.getChildAt(1) as LinearLayout
    assertThat(linearLayoutGroup2.childCount).isEqualTo(2)
    val answerOptionText2 = linearLayoutGroup1.getChildAt(1) as TextView
    assertThat(answerOptionText2.text).isEqualTo("Coding 1")
  }

  @Test
  fun bind_noAnswer_shouldLeaveCheckButtonsUnchecked() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    val checkBoxGroup = viewHolder.itemView.findViewById<LinearLayout>(R.id.checkbox_group)
    val linearLayoutGroup = checkBoxGroup.getChildAt(0) as LinearLayout
    val checkBox = linearLayoutGroup.getChildAt(1) as CheckBox
    assertThat(checkBox.isChecked).isFalse()
  }

  @Test
  @UiThreadTest
  fun bind_answer_shouldSetCheckBoxButton() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value =
                Coding().apply {
                  code = "code 1"
                  display = "Coding 1"
                }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Coding().apply {
                  code = "code 1"
                  display = "Coding 1"
                }
            }
          )
        }
      ) {}
    )
    val checkBoxGroup = viewHolder.itemView.findViewById<LinearLayout>(R.id.checkbox_group)
    val linearLayoutGroup = checkBoxGroup.getChildAt(0) as LinearLayout
    val checkBox = linearLayoutGroup.getChildAt(1) as CheckBox

    assertThat(checkBox.isChecked).isTrue()
  }

  @Test
  @UiThreadTest
  fun click_shouldAddQuestionnaireResponseItemAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value =
                Coding().apply {
                  code = "code 1"
                  display = "Coding 1"
                }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    val checkBoxGroup = viewHolder.itemView.findViewById<LinearLayout>(R.id.checkbox_group)
    val linearLayoutGroup = checkBoxGroup.getChildAt(0) as LinearLayout
    val checkBox = linearLayoutGroup.getChildAt(1) as CheckBox
    checkBox.performClick()
    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer

    assertThat(answer).hasSize(1)
    assertThat(answer[0].valueCoding.display).isEqualTo("Coding 1")
  }

  @Test
  @UiThreadTest
  fun click_shouldRemoveQuestionnaireResponseItemAnswer() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value =
                Coding().apply {
                  code = "code 1"
                  display = "Coding 1"
                }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Coding().apply {
                  code = "code 1"
                  display = "Coding 1"
                }
            }
          )
        }
      ) {}
    viewHolder.bind(questionnaireItemViewItem)
    val checkBoxGroup = viewHolder.itemView.findViewById<LinearLayout>(R.id.checkbox_group)
    val linearLayoutGroup = checkBoxGroup.getChildAt(0) as LinearLayout
    val checkBox = linearLayoutGroup.getChildAt(1) as CheckBox
    checkBox.performClick()
    val answer = questionnaireItemViewItem.questionnaireResponseItem.answer

    assertThat(answer).isEmpty()
  }
}
