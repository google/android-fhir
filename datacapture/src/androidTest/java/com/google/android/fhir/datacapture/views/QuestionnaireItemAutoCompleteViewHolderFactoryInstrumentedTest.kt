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

import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemAutoCompleteViewHolderFactoryInstrumentedTest {
  private lateinit var context: ContextWrapper
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
    viewHolder = QuestionnaireItemAutoCompleteViewHolderFactory.create(parent)
  }

  @Test
  @UiThreadTest
  fun shouldShowPrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).text).isEqualTo("Prefix?")
  }

  @Test
  @UiThreadTest
  fun shouldHidePrefixText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  @UiThreadTest
  fun shouldSetTextViewText() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Display" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).text)
      .isEqualTo("Display")
  }

  @Test
  @UiThreadTest
  fun shouldSetTextViewVisible() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "Display" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).visibility)
      .isEqualTo(View.VISIBLE)
  }

  @Test
  @UiThreadTest
  fun shouldSetTextViewGone() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { text = "" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.group_header).visibility)
      .isEqualTo(View.GONE)
  }

  @Test
  @UiThreadTest
  fun shouldHaveSingleAnswerChip() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            repeats = false
            answerOption.add(
              Questionnaire.QuestionnaireItemAnswerOptionComponent()
                .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code"))
            )
            answerOption.add(
              Questionnaire.QuestionnaireItemAnswerOptionComponent()
                .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code"))
            )
          },
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
        ) {}
        .apply {
          singleAnswerOrNull =
            (QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                questionnaireItem.answerOption
                  .first { it.displayString == "Test1 Code" }
                  .valueCoding
            })
        }
    )

    assertThat(viewHolder.itemView.findViewById<ViewGroup>(R.id.flexboxLayout).childCount)
      .isEqualTo(2)
  }

  @Test
  @UiThreadTest
  fun shouldHaveTwoAnswerChip() {
    viewHolder.bind(
      QuestionnaireItemViewItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            repeats = true
            answerOption.add(
              Questionnaire.QuestionnaireItemAnswerOptionComponent()
                .setValue(Coding().setCode("test1-code").setDisplay("Test1 Code"))
            )
            answerOption.add(
              Questionnaire.QuestionnaireItemAnswerOptionComponent()
                .setValue(Coding().setCode("test2-code").setDisplay("Test2 Code"))
            )
          },
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
        ) {}
        .apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                questionnaireItem.answerOption
                  .first { it.displayString == "Test1 Code" }
                  .valueCoding
            }
          )

          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                questionnaireItem.answerOption
                  .first { it.displayString == "Test2 Code" }
                  .valueCoding
            }
          )
        }
    )

    assertThat(viewHolder.itemView.findViewById<ViewGroup>(R.id.flexboxLayout).childCount)
      .isEqualTo(3)
  }
}
