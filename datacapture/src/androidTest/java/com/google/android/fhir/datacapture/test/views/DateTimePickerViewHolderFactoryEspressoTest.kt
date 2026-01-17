/*
 * Copyright 2023-2026 Google LLC
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

import android.widget.FrameLayout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isEditable
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.DATE_TEXT_INPUT_FIELD
import com.google.android.fhir.datacapture.views.components.TIME_PICKER_INPUT_FIELD
import com.google.android.fhir.datacapture.views.factories.DateTimePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateTimePickerViewHolderFactoryEspressoTest {

  @Rule
  @JvmField
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = DateTimePickerViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun showsTimePickerInInputMode() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItemView)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .onChildren()
      .filterToOne(
        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .performClick()
    composeTestRule.onNodeWithText("OK").performClick()
    composeTestRule.onNodeWithTag(TIME_PICKER_INPUT_FIELD).performClick()

    composeTestRule
      .onNode(
        hasContentDescription("Switch to clock input", substring = true) and
          SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .assertIsDisplayed()
    composeTestRule
      .onNode(hasContentDescription("for hour", substring = true) and isEditable())
      .assertIsDisplayed()
    composeTestRule
      .onNode(hasContentDescription("for minutes", substring = true) and isEditable())
      .assertExists()
  }

  @Test
  fun showsTimePickerInClockMode() {
    val questionnaireItemView =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent(),
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireItemView)
    composeTestRule
      .onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .onChildren()
      .filterToOne(
        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .performClick()
    composeTestRule.onNodeWithText("OK").performClick()
    composeTestRule
      .onNodeWithTag(TIME_PICKER_INPUT_FIELD)
      .onChildren()
      .filterToOne(
        SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .performClick()

    composeTestRule
      .onNode(
        hasContentDescription("Switch to text input", substring = true) and
          SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
      )
      .assertIsDisplayed()
    composeTestRule
      .onNode(
        hasAnyChild(hasContentDescription("12 o'clock", substring = true)) and
          SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup),
      )
      .assertIsDisplayed()
  }
}
