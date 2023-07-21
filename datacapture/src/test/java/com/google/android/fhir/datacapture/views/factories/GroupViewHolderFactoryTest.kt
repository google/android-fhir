/*
 * Copyright 2023 Google LLC
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
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.GroupHeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GroupViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply {
        setTheme(com.google.android.material.R.style.Theme_Material3_DayNight)
      }
    )
  private val viewHolder = GroupViewHolderFactory.create(parent)

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Group header" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Group header")
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

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text)
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "display" }
            }
          ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.error).text).isEqualTo("")
  }

  @Test
  fun hintText_nestedDisplayItem_shouldNotShowHintText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          type = Questionnaire.QuestionnaireItemType.GROUP
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "text"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(
        viewHolder.itemView
          .findViewById<GroupHeaderView>(R.id.header)
          .findViewById<TextView>(R.id.hint)
          .text.isNullOrEmpty()
      )
      .isTrue()
    assertThat(
        viewHolder.itemView
          .findViewById<GroupHeaderView>(R.id.header)
          .findViewById<TextView>(R.id.hint)
          .visibility
      )
      .isEqualTo(View.GONE)
  }

  @Test
  fun shouldHaveHeaderViewVisible() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Group header" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )
    assertThat(viewHolder.itemView.findViewById<GroupHeaderView>(R.id.header).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldSetHeaderViewVisibilityAsGone() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<GroupHeaderView>(R.id.header).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  fun repeatingGroup_shouldHaveAddItemButtonVisible() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { repeats = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<View>(R.id.add_item).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun nonRepeatingGroup_shouldHaveAddItemButtonGone() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { repeats = false },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    )

    assertThat(viewHolder.itemView.findViewById<View>(R.id.add_item).visibility)
      .isEqualTo(View.GONE)
  }
}
