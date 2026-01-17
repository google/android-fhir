/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.datacapture.test.views.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.ADD_REPEATED_GROUP_BUTTON_TAG
import com.google.android.fhir.datacapture.views.components.RepeatedGroupAddItem
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepeatedGroupAddItemTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testRepeatedGroupIsReadOnlyDisablesAddButton() {
    val viewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          type = Questionnaire.QuestionnaireItemType.GROUP
          repeats = true
          readOnly = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    composeTestRule.setContent { RepeatedGroupAddItem(viewItem) }
    composeTestRule.onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).assertIsNotEnabled()
  }

  @Test
  fun repeatingGroup_shouldHaveAddItemButtonVisible() {
    val viewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { repeats = true },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    composeTestRule.setContent { RepeatedGroupAddItem(viewItem) }
    composeTestRule.onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).assertIsDisplayed()
  }

  @Test
  fun testRepeatedGroupIsNotReadOnlyEnablesAddButton() {
    val viewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          text = "Question?"
          type = Questionnaire.QuestionnaireItemType.GROUP
          repeats = true
          readOnly = false
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    composeTestRule.setContent { RepeatedGroupAddItem(viewItem) }
    composeTestRule.onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).assertIsEnabled()
  }
}
