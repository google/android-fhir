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
import android.widget.TextView
import androidx.core.view.get
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemAutoCompleteViewHolderFactoryInstrumentedTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_MaterialComponents) }
    )
  private val viewHolder = QuestionnaireItemAutoCompleteViewHolderFactory.create(parent)

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question")
  }

  @Test
  fun shouldHaveSingleAnswerChip() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            repeats = false
            addAnswerOption(
              Questionnaire.QuestionnaireItemAnswerOptionComponent()
                .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code"))
            )
            addAnswerOption(
              Questionnaire.QuestionnaireItemAnswerOptionComponent()
                .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code"))
            )
          },
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
        ) {}
        .apply {
          singleAnswerOrNull =
            (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answerOption.first { it.displayString == "Test1 Code" }.valueCoding
            })
        }
    )

    assertThat(viewHolder.itemView.findViewById<ViewGroup>(R.id.flexboxLayout).childCount)
      .isEqualTo(2)
  }

  @Test
  fun shouldHaveTwoAnswerChipWithExternalValueSet() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            repeats = true
            answerValueSet = "http://answwer-value-set-url"
          },
          QuestionnaireResponse.QuestionnaireResponseItemComponent(),
          resolveAnswerValueSet = {
            if (it == "http://answwer-value-set-url") {
              listOf(
                Questionnaire.QuestionnaireItemAnswerOptionComponent()
                  .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code")),
                Questionnaire.QuestionnaireItemAnswerOptionComponent()
                  .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code"))
              )
            } else {
              emptyList()
            }
          }
        ) {}
        .apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answerOption.first { it.displayString == "Test1 Code" }.valueCoding
            }
          )

          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answerOption.first { it.displayString == "Test2 Code" }.valueCoding
            }
          )
        }
    )

    assertThat(viewHolder.itemView.findViewById<ViewGroup>(R.id.flexboxLayout).childCount)
      .isEqualTo(3)
  }

  @Test
  fun shouldHaveSingleAnswerChipWithContainedAnswerValueSet() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            repeats = false
            answerValueSet = "#ContainedValueSet"
          },
          QuestionnaireResponse.QuestionnaireResponseItemComponent(),
          resolveAnswerValueSet = {
            if (it == "#ContainedValueSet") {
              listOf(
                Questionnaire.QuestionnaireItemAnswerOptionComponent()
                  .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code")),
                Questionnaire.QuestionnaireItemAnswerOptionComponent()
                  .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code"))
              )
            } else {
              emptyList()
            }
          }
        ) {}
        .apply {
          singleAnswerOrNull =
            (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answerOption.first { it.displayString == "Test1 Code" }.valueCoding
            })
        }
    )

    assertThat(viewHolder.itemView.findViewById<ViewGroup>(R.id.flexboxLayout).childCount)
      .isEqualTo(2)
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
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

    assertThat(viewHolder.itemView.findViewById<TextInputLayout>(R.id.text_input_layout).error)
      .isNull()
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            readOnly = true
            addAnswerOption(
              Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                value = Coding().apply { display = "readOnly" }
              }
            )
          },
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
        ) {}
        .apply {
          singleAnswerOrNull =
            (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answerOption.first { it.displayString == "readOnly" }.valueCoding
            })
        }
    )

    assertThat(viewHolder.itemView.findViewById<ViewGroup>(R.id.flexboxLayout)[0].isEnabled)
      .isFalse()
  }
}
