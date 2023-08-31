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

package com.google.android.fhir.datacapture.test.views

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.test.utilities.delayMainThread
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuantityViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionnaireItemQuantityViewHolderFactoryEspressoTest {
  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  private lateinit var parent: FrameLayout
  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity -> parent = FrameLayout(activity) }
    viewHolder = QuantityViewHolderFactory.create(parent)
    setTestLayout(viewHolder.itemView)
  }

  @Test
  fun shouldSetDraftWithUnit() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "cm"
                  system = "http://unitofmeasure.com"
                  display = "centimeter"
                }
              )
            }
          )
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "[in_i]"
                  system = "http://unitofmeasure.com"
                  display = "inch"
                }
              )
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, draft ->
          answerHolder = answers
          draftHolder = draft
        },
      )

    runOnUI {
      viewHolder.bind(questionnaireViewItem)
      viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.unit_auto_complete).showDropDown()
    }

    onView(withId(R.id.unit_auto_complete)).perform(delayMainThread())
    onView(ViewMatchers.withText("centimeter"))
      .inRoot(RootMatchers.isPlatformPopup())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(click())

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.unit_auto_complete).text.toString())
      .isEqualTo("centimeter")

    with(draftHolder as Coding) {
      assertThat(system).isEqualTo("http://unitofmeasure.com")
      assertThat(code).isEqualTo("cm")
      assertThat(display).isEqualTo("centimeter")
    }
    assertThat(answerHolder).isEmpty()
  }

  @Test
  fun shouldSetDraftWithDecimalValue() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "cm"
                  system = "http://unitofmeasure.com"
                  display = "centimeter"
                }
              )
            }
          )
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "[in_i]"
                  system = "http://unitofmeasure.com"
                  display = "inch"
                }
              )
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, draft ->
          answerHolder = answers
          draftHolder = draft
        },
      )

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    onView(withId(R.id.text_input_edit_text)).perform(click())
    onView(withId(R.id.text_input_edit_text)).perform(typeText("22"))

    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.text_input_edit_text).text.toString()
      )
      .isEqualTo("22")

    assertThat(draftHolder as BigDecimal).isEqualTo(BigDecimal(22))
    assertThat(answerHolder).isEmpty()
  }

  @Test
  fun draftWithUnit_shouldCompleteQuantity() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "cm"
                  system = "http://unitofmeasure.com"
                  display = "centimeter"
                }
              )
            }
          )
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "[in_i]"
                  system = "http://unitofmeasure.com"
                  display = "inch"
                }
              )
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, draft ->
          answerHolder = answers
          draftHolder = draft
        },
        draftAnswer = Coding("http://unitofmeasure.com", "cm", "centimeter"),
      )

    runOnUI {
      viewHolder.bind(questionnaireViewItem)
      viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.unit_auto_complete).showDropDown()
    }

    onView(withId(R.id.text_input_edit_text)).perform(click())
    onView(withId(R.id.text_input_edit_text)).perform(typeText("22"))
    assertThat(
        viewHolder.itemView.findViewById<TextView>(R.id.text_input_edit_text).text.toString()
      )
      .isEqualTo("22")

    with(answerHolder!!.single().valueQuantity) {
      assertThat(system).isEqualTo("http://unitofmeasure.com")
      assertThat(code).isEqualTo("cm")
      assertThat(unit).isEqualTo("centimeter")
      assertThat(value).isEqualTo(BigDecimal("22.0"))
    }
    assertThat(draftHolder).isNull()
  }

  @Test
  fun draftWithDecimalValue_shouldCompleteQuantity() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          required = true
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "cm"
                  system = "http://unitofmeasure.com"
                  display = "centimeter"
                }
              )
            }
          )
          addExtension(
            Extension().apply {
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"
              setValue(
                Coding().apply {
                  code = "[in_i]"
                  system = "http://unitofmeasure.com"
                  display = "inch"
                }
              )
            }
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, draft ->
          answerHolder = answers
          draftHolder = draft
        },
        draftAnswer = BigDecimal(22),
      )

    runOnUI {
      viewHolder.bind(questionnaireViewItem)
      viewHolder.itemView.findViewById<AutoCompleteTextView>(R.id.unit_auto_complete).showDropDown()
    }

    onView(withId(R.id.unit_auto_complete)).perform(delayMainThread())
    onView(ViewMatchers.withText("centimeter"))
      .inRoot(RootMatchers.isPlatformPopup())
      .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
      .perform(click())
    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.unit_auto_complete).text.toString())
      .isEqualTo("centimeter")

    with(answerHolder!!.single().valueQuantity) {
      assertThat(system).isEqualTo("http://unitofmeasure.com")
      assertThat(code).isEqualTo("cm")
      assertThat(unit).isEqualTo("centimeter")
      assertThat(value).isEqualTo(BigDecimal("22.0"))
    }
    assertThat(draftHolder).isNull()
  }

  /** Method to run code snippet on UI/main thread */
  private fun runOnUI(action: () -> Unit) {
    activityScenarioRule.scenario.onActivity { action() }
  }

  /** Method to set content view for test activity */
  private fun setTestLayout(view: View) {
    activityScenarioRule.scenario.onActivity { activity -> activity.setContentView(view) }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }
}
