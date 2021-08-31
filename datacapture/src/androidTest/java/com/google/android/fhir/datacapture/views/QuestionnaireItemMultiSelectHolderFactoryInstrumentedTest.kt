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

import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemMultiSelectHolderFactoryInstrumentedTest {
  @get:Rule val rule = activityScenarioRule<TestActivity>()

  @Test
  fun shouldShowPrefixText() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          prefix = "Prefix?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(holder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isTrue()
    assertThat(holder.itemView.findViewById<TextView>(R.id.prefix).text).isEqualTo("Prefix?")
  }

  @Test
  fun shouldHidePrefixText() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          repeats = true
          prefix = ""
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}
    )

    assertThat(holder.itemView.findViewById<TextView>(R.id.prefix).isVisible).isFalse()
  }

  @Test
  fun emptyResponseOptions_showNoneSelected() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(answerOptions("Coding 1", "Coding 2"), responseOptions()) {}
    )
    assertThat(holder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString())
      .isEqualTo("")
  }

  @Test
  fun selectedResponseOptions_showSelectedOptions() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(
        answerOptions("Coding 1", "Coding 2", "Coding 3"),
        responseOptions("Coding 1", "Coding 3")
      ) {}
    )
    assertThat(holder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString())
      .isEqualTo("Coding 1, Coding 3")
  }

  @Test
  fun click_showsDialog() = withViewHolder { holder ->
    holder.bind(
      QuestionnaireItemViewItem(
        answerOptions("Coding 1", "Coding 2", "Coding 3"),
        responseOptions("Coding 1", "Coding 3")
      ) {}
    )
    holder.itemView.findViewById<TextView>(R.id.multi_select_summary).performClick()
    Thread.sleep(2000)
    val fragment =
      supportFragmentManager.fragments.first { it is MultiSelectDialogFragment } as MultiSelectDialogFragment
    assertThat(fragment.options).containsExactly(
      MultiSelectOption("Coding 1", selected = true),
      MultiSelectOption("Coding 2", selected = false),
      MultiSelectOption("Coding 3", selected = true),
    ).inOrder()
  }

  private inline fun withViewHolder(
    crossinline block: TestActivity.(QuestionnaireItemViewHolder) -> Unit
  ) {
    rule.scenario.onActivity {
      block(it, QuestionnaireItemMultiSelectViewHolderFactory.create(FrameLayout(it)))
    }
  }
}

private fun answerOptions(vararg options: String) =
  Questionnaire.QuestionnaireItemComponent().apply {
    options.forEach { option ->
      addAnswerOption(
        Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
          value = Coding().apply { display = option }
        }
      )
    }
  }

private fun responseOptions(vararg responses: String) =
  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
    responses.forEach { response ->
      addAnswer(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          value = Coding().apply { display = response }
        }
      )
    }
  }
