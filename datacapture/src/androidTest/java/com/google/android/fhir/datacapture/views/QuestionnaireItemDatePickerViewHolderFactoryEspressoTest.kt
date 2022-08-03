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

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.common.truth.Truth.assertThat
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemDatePickerViewHolderFactoryEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder
  @Before
  fun setup() {
    activityScenarioRule.getScenario().onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuestionnaireItemDatePickerViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun textInputEditText_dateInput_localeUs() = runBlocking {
    activityScenarioRule.scenario.onActivity { activity -> setLocale(Locale.US, activity) }

    val item =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    runOnUI {
      viewHolder.bind(item)
      viewHolder.itemView.findViewById<TextView>(R.id.text_input_edit_text).text = "11/19/20"
    }
    delay(3000)
    val answer = item.answers.singleOrNull()?.value as DateType
    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  @Test
  fun textInputTextField_dateInput_localeJapan() = runBlocking {
    activityScenarioRule.getScenario().onActivity { activity -> setLocale(Locale.JAPAN, activity) }
    val item =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = null,
        answersChangedCallback = { _, _, _ -> },
      )
    runOnUI {
      viewHolder.bind(item)
      viewHolder.itemView.findViewById<TextView>(R.id.text_input_edit_text).text = "2020/11/19"
    }
    delay(3000)
    val answer = item.answers.singleOrNull()?.value as DateType
    assertThat(answer.day).isEqualTo(19)
    assertThat(answer.month).isEqualTo(10)
    assertThat(answer.year).isEqualTo(2020)
  }

  /** Runs code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { activity -> action() }
  }

  /** Sets content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  private fun setLocale(locale: Locale, context: Context) {
    Locale.setDefault(locale)
    context.resources.configuration.setLocale(locale)
  }
}
