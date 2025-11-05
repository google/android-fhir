/*
 * Copyright 2025 Google LLC
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
import androidx.appcompat.app.AppCompatActivity
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RepeatedGroupAddItemViewHolderTest {

  private val parent =
    FrameLayout(
      Robolectric.buildActivity(AppCompatActivity::class.java).create().get().apply {
        setTheme(com.google.android.material.R.style.Theme_Material3_DayNight)
      },
    )
  private val viewHolder: RepeatedGroupAddItemViewHolder =
    RepeatedGroupAddItemViewHolder.create(parent)

  @Test
  fun testRepeatedGroupIsReadOnlyDisablesAddButton() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          type = Questionnaire.QuestionnaireItemType.GROUP
          repeats = true
          readOnly = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    assertThat(
        (viewHolder.itemView.findViewById<Button>(R.id.add_item_to_repeated_group).isEnabled),
      )
      .isFalse()
  }

  @Test
  fun repeatingGroup_shouldHaveAddItemButtonVisible() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { repeats = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    assertThat(viewHolder.itemView.findViewById<View>(R.id.add_item_to_repeated_group).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun testRepeatedGroupIsNotReadOnlyEnablesAddButton() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          type = Questionnaire.QuestionnaireItemType.GROUP
          repeats = true
          readOnly = false
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    assertThat(
        (viewHolder.itemView.findViewById<Button>(R.id.add_item_to_repeated_group).isEnabled),
      )
      .isTrue()
  }
}
