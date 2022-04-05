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

package com.google.android.fhir.datacapture

import android.view.View
import android.widget.FrameLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextSingleLineViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemEditTextSingleLineViewHolderFactoryEspressoTests {
  @JvmField @Rule var activityRule = ActivityTestRule(TestActivity::class.java)

  @Test
  fun test() {
    // arrange
    val parent = FrameLayout(activityRule.activity)
    val viewHolder = QuestionnaireItemEditTextSingleLineViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
    val questionnaireItemView =
      QuestionnaireItemViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { prefix = "Prefix?" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent()
      ) {}

    //    act
    runOnUI { viewHolder.bind(questionnaireItemView) }
    onView(withId(R.id.text_input_edit_text))
      .perform(ViewActions.click())
      .perform(ViewActions.typeText("Answer"))

    // assert
    onView(withId(R.id.prefix_text_view)).check(matches(isDisplayed()))
    onView(withId(R.id.text_input_edit_text)).check(matches(withText("Answer")))
    onView(withId(R.id.prefix_text_view)).check(matches(withText("Prefix?")))
    Truth.assertThat(
        questionnaireItemView.questionnaireResponseItem.answerFirstRep.value.asStringValue()
      )
      .isEqualTo("Answer")
  }

  private fun setTestLayout(view: View) {
    runOnUI { activityRule.activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  private fun runOnUI(action: () -> Unit) {
    activityRule.activity.runOnUiThread { action() }
  }
}
