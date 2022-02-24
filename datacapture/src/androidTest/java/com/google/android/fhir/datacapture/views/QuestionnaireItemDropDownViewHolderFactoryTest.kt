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

 import android.widget.AutoCompleteTextView
 import android.widget.FrameLayout
 import androidx.test.espresso.Espresso.onView
 import androidx.test.espresso.action.ViewActions.click
 import androidx.test.espresso.matcher.ViewMatchers.withText
 import androidx.test.ext.junit.rules.activityScenarioRule
 import androidx.test.ext.junit.runners.AndroidJUnit4
 import com.google.android.fhir.datacapture.R
 import com.google.android.fhir.datacapture.TestActivity
 import com.google.common.truth.Truth
 import org.hl7.fhir.r4.model.Coding
 import org.hl7.fhir.r4.model.Questionnaire
 import org.hl7.fhir.r4.model.QuestionnaireResponse
 import org.junit.Rule
 import org.junit.Test
 import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
 class QuestionnaireItemDropDownViewHolderFactoryTest {
  @get:Rule val rule = activityScenarioRule<TestActivity>()

  @Test
  fun should_clearAutoCompleteTextView() = withViewHolder { holder ->
    val answerOption =
      Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
        value =
          Coding().apply {
            code = "test-code"
            display = "Test Code"
          }
      }
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          answerValueSet = "http://coding-value-set-url"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = answerOption.value
            }
          )
        },
        resolveAnswerValueSet = {
          if (it == "http://coding-value-set-url") {
            listOf(answerOption)
          } else {
            emptyList()
          }
        }
      ) {}
    holder.bind(questionnaireItemViewItem)
    var autoCompleteTextView =
      holder.itemView.findViewById<AutoCompleteTextView>(R.id.auto_complete)
    
  autoCompleteTextView.showDropDown()
    onView(withText("-")).perform(click())
    
    Truth.assertThat(autoCompleteTextView.text).isEqualTo("")
  }

  private inline fun withViewHolder(
    crossinline block: TestActivity.(QuestionnaireItemViewHolder) -> Unit
  ) {
    rule.scenario.onActivity {
      block(it, QuestionnaireItemDropDownViewHolderFactory.create(FrameLayout(it)))
    }
  }
 }
