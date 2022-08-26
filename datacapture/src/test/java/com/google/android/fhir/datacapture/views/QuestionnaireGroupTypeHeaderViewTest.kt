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
import androidx.core.view.isVisible
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Questionnaire
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class QuestionnaireGroupTypeHeaderViewTest {
  private val parent =
    FrameLayout(
      RuntimeEnvironment.getApplication().apply { setTheme(R.style.Theme_MaterialComponents) }
    )
  private val view = QuestionnaireGroupTypeHeaderView(parent.context, null)

  @Test
  fun shouldShowPrefix() {
    view.bind(Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" })

    assertThat(view.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(view.findViewById<TextView>(R.id.prefix).text.toString()).isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefix() {
    view.bind(Questionnaire.QuestionnaireItemComponent().apply { prefix = "" })

    assertThat(view.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  fun shouldShowQuestion() {
    view.bind(
      Questionnaire.QuestionnaireItemComponent().apply {
        repeats = true
        text = "Question?"
      }
    )

    assertThat(view.findViewById<TextView>(R.id.question).text.toString()).isEqualTo("Question?")
  }

  @Test
  fun shouldShowHint() {
    view.bind(
      Questionnaire.QuestionnaireItemComponent().apply {
        item =
          listOf(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "nested-display-question"
              text = "subtitle text"
              type = Questionnaire.QuestionnaireItemType.DISPLAY
            }
          )
      }
    )

    assertThat(view.findViewById<TextView>(R.id.hint).isVisible).isTrue()
    assertThat(view.findViewById<TextView>(R.id.hint).text.toString()).isEqualTo("subtitle text")
  }

  @Test
  fun shouldHideHint() {
    view.bind(
      Questionnaire.QuestionnaireItemComponent().apply {
        item =
          listOf(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "nested-display-question"
              type = Questionnaire.QuestionnaireItemType.DISPLAY
            }
          )
      }
    )

    assertThat(view.findViewById<TextView>(R.id.hint).visibility).isEqualTo(View.GONE)
  }

  @Test
  fun shouldShowHeaderView() {
    view.bind(
      Questionnaire.QuestionnaireItemComponent().apply {
        item =
          listOf(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "nested-display-question"
              text = "subtitle text"
              type = Questionnaire.QuestionnaireItemType.DISPLAY
            }
          )
      }
    )

    assertThat(view.visibility).isEqualTo(View.VISIBLE)
  }

  @Test
  fun shouldHideHeaderView() {
    view.bind(Questionnaire.QuestionnaireItemComponent())

    assertThat(view.visibility).isEqualTo(View.GONE)
  }
}
