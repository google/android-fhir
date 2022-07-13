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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.android.fhir.datacapture.utilities.assertQuestionnaireResponseAtIndex
import com.google.android.fhir.datacapture.utilities.clickOnText
import com.google.android.fhir.datacapture.utilities.clickOnTextInDialog
import com.google.android.fhir.datacapture.utilities.endIconClickInTextInputLayout
import com.google.common.truth.StringSubject
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionnaireItemDialogMultiSelectViewHolderFactoryEspressoTest {
  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuestionnaireItemDialogSelectViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun multipleChoice_selectMultiple_clickSave_shouldSaveMultipleOptions() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 1")
    clickOnText("Coding 3")
    clickOnText("Coding 5")
    clickOnText("Save")

    assertDisplayedText().isEqualTo("Coding 1, Coding 3, Coding 5")
    assertQuestionnaireResponseAtIndex(
      questionnaireItemViewItem,
      "Coding 1",
      "Coding 3",
      "Coding 5"
    )
  }

  @Test
  fun multipleChoice_SelectNothing_clickSave_shouldSaveNothing() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Save")

    assertDisplayedText().isEmpty()
    assertThat(questionnaireItemViewItem.answers).isEmpty()
  }

  @Test
  fun multipleChoice_selectMultiple_clickCancel_shouldSaveNothing() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 3")
    clickOnText("Coding 1")
    clickOnText("Cancel")

    assertDisplayedText().isEmpty()
    assertThat(questionnaireItemViewItem.answers).isEmpty()
  }

  @Test
  fun shouldSelectSingleOptionOnChangeInOptionFromDropDown() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Coding 1")
    clickOnText("Save")

    assertDisplayedText().isEqualTo("Coding 1")
    assertQuestionnaireResponseAtIndex(questionnaireItemViewItem, "Coding 1")
  }

  @Test
  fun singleOption_select_clickSave_shouldSaveSingleOption() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Save")

    assertDisplayedText().isEqualTo("Coding 2")
    assertQuestionnaireResponseAtIndex(questionnaireItemViewItem, "Coding 2")
  }

  @Test
  fun singleOption_selectNothing_clickSave_shouldSaveNothing() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Save")

    assertDisplayedText().isEmpty()
    assertThat(questionnaireItemViewItem.answers).isEmpty()
  }

  @Test
  fun singleOption_select_clickCancel_shouldSaveNothing() {
    val questionnaireItemViewItem =
      QuestionnaireItemViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireItemViewItem) }
    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Cancel")

    assertDisplayedText().isEmpty()
    assertThat(questionnaireItemViewItem.answers).isEmpty()
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { activity -> action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  private fun assertDisplayedText(): StringSubject =
    assertThat(
      viewHolder.itemView.findViewById<TextView>(R.id.multi_select_summary).text.toString()
    )

  internal companion object {
    private fun answerOptions(multiSelect: Boolean, vararg options: String) =
      Questionnaire.QuestionnaireItemComponent().apply {
        this.repeats = multiSelect
        linkId = "1"
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
  }
}
