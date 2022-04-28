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

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireItemGroupViewHolderFactoryTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_MaterialComponents) }
    )
  private val viewHolder = QuestionnaireItemGroupViewHolderFactory.create(parent)

  @Test
  fun shouldSetQuestionHeader() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Group header" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Group header")
  }

  @Test
  fun hintText_nestedDisplayItem_shouldNotShowHintText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
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
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(
        viewHolder
          .itemView
          .findViewById<QuestionnaireItemHeaderView>(R.id.header)
          .findViewById<TextView>(R.id.hint)
          .text
          .isNullOrEmpty()
      )
      .isTrue()
    assertThat(
        viewHolder
          .itemView
          .findViewById<QuestionnaireItemHeaderView>(R.id.header)
          .findViewById<TextView>(R.id.hint)
          .visibility
      )
      .isEqualTo(View.GONE)
  }

  @Test
  fun shouldHaveHeaderViewVisible() {

    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Group header" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )
    assertThat(
        viewHolder.itemView.findViewById<QuestionnaireItemHeaderView>(R.id.header).visibility
      )
      .isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldSetHeaderViewVisibilityAsGone() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(
        viewHolder.itemView.findViewById<QuestionnaireItemHeaderView>(R.id.header).visibility
      )
      .isEqualTo(View.GONE)
  }
}
