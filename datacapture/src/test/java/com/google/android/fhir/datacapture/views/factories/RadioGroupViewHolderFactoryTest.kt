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

package com.google.android.fhir.datacapture.views.factories

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.extensions.EXTENSION_CHOICE_ORIENTATION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_ANSWER_MEDIA
import com.google.android.fhir.datacapture.extensions.INSTRUCTIONS
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RadioGroupViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_Material3_DayNight) }
    )
  private val viewHolder = RadioGroupViewHolderFactory.create(parent)

  private val itemAnswerMediaExtension =
    Extension().apply {
      url = EXTENSION_ITEM_ANSWER_MEDIA
      setValue(
        Attachment().apply {
          data =
            "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".toByteArray()
          contentType = "image/png"
        }
      )
    }

  @Test
  fun bind_shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
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
      QuestionnaireViewItem(
        questionnaire,
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
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
      QuestionnaireViewItem(
        questionnaire,
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
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
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    val radioButton =
      viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
        as RadioButton
    assertThat(radioButton.isChecked).isFalse()
  }

  @Test
  fun bind_withImageInItemAnswerMediaExtension_shouldShowImageAfterRadioButtons() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              extension = listOf(itemAnswerMediaExtension)
              value = StringType("Test Code")
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    val radioButton =
      viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
        as RadioButton
    assertThat(radioButton.compoundDrawablesRelative[0]).isNotNull()
    assertThat(radioButton.compoundDrawablesRelative[1]).isNull()
    assertThat(radioButton.compoundDrawablesRelative[2]).isNull()
    assertThat(radioButton.compoundDrawablesRelative[3]).isNull()
  }

  @Test
  fun bind_answer_shouldCheckRadioButton() {
    viewHolder.bind(
      QuestionnaireViewItem(
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _v, _ -> },
      )
    )

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
            as RadioButton)
          .isChecked
      )
      .isTrue()
    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(2)
            as RadioButton)
          .isChecked
      )
      .isFalse()
  }

  @Test
  fun click_shouldSetQuestionnaireResponseItemAnswer() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView
      .findViewById<ConstraintLayout>(R.id.radio_group)
      .getChildAt(1)
      .performClick()

    assertThat(answerHolder!!.single().valueCoding.display).isEqualTo("Coding 1")
  }

  @Test
  fun click_shouldCheckRadioButton() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    viewHolder.bind(questionnaireViewItem)
    viewHolder.itemView
      .findViewById<ConstraintLayout>(R.id.radio_group)
      .getChildAt(1)
      .performClick()

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
            as RadioButton)
          .isChecked
      )
      .isTrue()
    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(2)
            as RadioButton)
          .isChecked
      )
      .isFalse()
  }

  @Test
  fun click_shouldCheckOtherRadioButton() {
    viewHolder.bind(
      QuestionnaireViewItem(
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
            as RadioButton)
          .isChecked
      )
      .isTrue()

    viewHolder.itemView
      .findViewById<ConstraintLayout>(R.id.radio_group)
      .getChildAt(2)
      .performClick()

    assertThat(
        (viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(2)
            as RadioButton)
          .isChecked
      )
      .isTrue()
  }

  @Test
  fun `unselect radio button if selected radio button is clicked`() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)
    val singleChoiceOption =
      viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
        as RadioButton
    singleChoiceOption.performClick()
    assertThat(singleChoiceOption.isChecked).isTrue()
    singleChoiceOption.performClick()

    assertThat(singleChoiceOption.isChecked).isFalse()
  }

  @Test
  fun `clear the answer if selected radio button is clicked`() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)
    val singleChoiceOption =
      viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
        as RadioButton
    singleChoiceOption.performClick()
    singleChoiceOption.performClick()

    assertThat(questionnaireViewItem.answers.isEmpty()).isTrue()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).text)
      .isEqualTo("Missing answer for required field.")
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
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
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error_text_at_header).text.isEmpty())
      .isTrue()
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          readOnly = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding 1" }
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )
    val radioButton =
      viewHolder.itemView.findViewById<ConstraintLayout>(R.id.radio_group).getChildAt(1)
        as RadioButton

    assertThat(radioButton.isEnabled).isFalse()
  }

  @Test
  fun `show asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question *")
  }

  @Test
  fun `hide asterisk`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false)
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question")
  }

  @Test
  fun `show required text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true)
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
      )
      .isEqualTo("Required")
  }

  @Test
  fun `hide required text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { required = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false)
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
      )
      .isEmpty()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun `show optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true)
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
      )
      .isEqualTo("Optional")
  }

  @Test
  fun `hide optional text`() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Question" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false)
      )
    )

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).text.toString()
      )
      .isEmpty()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.required_optional_text).visibility)
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
}
