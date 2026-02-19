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

package com.google.android.fhir.datacapture.views.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class RepeatedGroupAddButtonItemTest {

  @Test
  fun testRepeatedGroupIsReadOnlyDisablesAddButton() = runComposeUiTest {
    val viewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "repeated-group-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
          readOnly = FhirR4Boolean(value = true),
          repeats = FhirR4Boolean(value = true),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "repeated-group-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { RepeatedGroupAddButtonItem(viewItem) }
    onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).assertIsNotEnabled()
  }

  @Test
  fun repeatingGroup_shouldHaveAddItemButtonVisible() = runComposeUiTest {
    val viewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "repeated-group-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
          repeats = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "repeated-group-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { RepeatedGroupAddButtonItem(viewItem) }
    onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).assertIsDisplayed()
  }

  @Test
  fun testRepeatedGroupIsNotReadOnlyEnablesAddButton() = runComposeUiTest {
    val viewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "repeated-group-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Group),
          readOnly = FhirR4Boolean(value = false),
          repeats = FhirR4Boolean(value = true),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "repeated-group-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    setContent { RepeatedGroupAddButtonItem(viewItem) }
    onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).assertIsEnabled()
  }
}
