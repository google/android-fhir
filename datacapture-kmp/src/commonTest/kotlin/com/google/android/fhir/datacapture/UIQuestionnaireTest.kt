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

package com.google.android.fhir.datacapture

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.button_pagination_next
import android_fhir.datacapture_kmp.generated.resources.button_pagination_previous
import android_fhir.datacapture_kmp.generated.resources.button_review
import android_fhir.datacapture_kmp.generated.resources.edit_button_text
import android_fhir.datacapture_kmp.generated.resources.submit_questionnaire
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.views.components.ADD_REPEATED_GROUP_BUTTON_TAG
import com.google.android.fhir.datacapture.views.components.DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG
import com.google.android.fhir.datacapture.views.components.HINT_HEADER_TAG
import com.google.android.fhir.datacapture.views.components.QUESTIONNAIRE_BOTTOM_NAVIGATION_TEST_TAG
import com.google.android.fhir.datacapture.views.components.QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG
import com.google.android.fhir.datacapture.views.components.QUESTION_HEADER_TAG
import com.google.android.fhir.datacapture.views.components.REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG
import com.google.android.fhir.datacapture.views.factories.NO_CHOICE_RADIO_BUTTON_TAG
import com.google.android.fhir.datacapture.views.factories.YES_CHOICE_RADIO_BUTTON_TAG
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.terminologies.PublicationStatus
import kotlin.test.Test
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalTestApi::class)
class UIQuestionnaireTest {

  private val fhirR4Json = FhirR4Json()

  @Test
  fun shouldDisplayReviewButtonWhenNoMorePagesToDisplay() = runComposeUiTest {
    setQuestionnaireContent("files/paginated_questionnaire_with_dependent_answer.json", true)
    val reviewButtonText = getString(Res.string.button_review)
    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(reviewButtonText))
      .assertIsDisplayed()

    onNodeWithText("Yes").performClick()

    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(reviewButtonText))
      .assertDoesNotExist()

    onNodeWithText("No").performClick()

    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(reviewButtonText))
      .assertIsDisplayed()
  }

  @Test
  fun shouldHideNextButtonIfDisabled() = runComposeUiTest {
    setQuestionnaireContent("files/layout_paginated.json", true)

    val nextButtonText = getString(Res.string.button_pagination_next)
    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(nextButtonText))
      .performClick()

    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(nextButtonText))
      .assertDoesNotExist()
  }

  @Test
  fun shouldDisplayNextButtonIfEnabled() = runComposeUiTest {
    setQuestionnaireContent("files/layout_paginated.json", true)

    val nextButtonText = getString(Res.string.button_pagination_next)
    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(nextButtonText))
      .assertIsDisplayed()
  }

  @Test
  fun displayItems_shouldGetEnabled_withAnswerChoice() = runComposeUiTest {
    setQuestionnaireContent("files/questionnaire_with_enabled_display_items.json")

    onNodeWithTag(HINT_HEADER_TAG).assertDoesNotExist()
    onNodeWithTag(YES_CHOICE_RADIO_BUTTON_TAG).performClick()
    onNodeWithTag(HINT_HEADER_TAG).assertTextEquals("Text when yes is selected").assertIsDisplayed()

    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).performClick()
    onNodeWithTag(HINT_HEADER_TAG).assertIsDisplayed().assertTextEquals("Text when no is selected")

    onNodeWithTag(NO_CHOICE_RADIO_BUTTON_TAG).performClick()
    onNodeWithTag(HINT_HEADER_TAG).assertDoesNotExist()
  }

  @Test
  fun cqfExpression_shouldSetText_withEvaluatedAnswer() = runComposeUiTest {
    setQuestionnaireContent("files/questionnaire_with_dynamic_question_text.json")

    onRoot().printToLog("Anyting!!!")
    onNode(hasTestTag(QUESTION_HEADER_TAG) and hasText("Option Date")).assertIsDisplayed()
    onNode(hasTestTag(QUESTION_HEADER_TAG) and hasText("Provide \"First Option\" Date"))
      .assertDoesNotExist()

    onNodeWithText("First Option").performClick()

    onNode(hasTestTag(QUESTION_HEADER_TAG) and hasText("Option Date")).assertDoesNotExist()
    onNode(hasTestTag(QUESTION_HEADER_TAG) and hasText("Provide \"First Option\" Date"))
      .assertIsDisplayed()
  }

  @Test
  fun progressBar_shouldBeVisible_withSinglePageQuestionnaire() = runComposeUiTest {
    setQuestionnaireContent("files/text_questionnaire_integer.json")
    onNodeWithTag(QUESTIONNAIRE_PROGRESS_INDICATOR_TEST_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(1f, 0f..1f))
  }

  @Test
  fun progressBar_shouldBeVisible_withPaginatedQuestionnaire() = runComposeUiTest {
    setQuestionnaireContent("files/layout_paginated.json")
    onNodeWithTag(QUESTIONNAIRE_PROGRESS_INDICATOR_TEST_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(0.5f, 0f..1f))
  }

  @Test
  fun progressBar_shouldProgress_onPaginationNext() = runComposeUiTest {
    setQuestionnaireContent("files/layout_paginated.json")

    val nextButtonText = getString(Res.string.button_pagination_next)
    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(nextButtonText))
      .performClick()

    onNodeWithTag(QUESTIONNAIRE_PROGRESS_INDICATOR_TEST_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(1f, 0f..1f))
  }

  @Test
  fun progressBar_shouldBeGone_whenNavigatedToReviewScreen() = runComposeUiTest {
    setQuestionnaireContent("files/text_questionnaire_integer.json", isReviewMode = true)

    val reviewButtonText = getString(Res.string.button_review)
    onNode(
        hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(reviewButtonText),
      )
      .performClick()

    onNodeWithTag(QUESTIONNAIRE_PROGRESS_INDICATOR_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun progressBar_shouldBeVisible_whenNavigatedToEditScreenFromReview() = runComposeUiTest {
    setQuestionnaireContent("files/text_questionnaire_integer.json", isReviewMode = true)

    val reviewButtonText = getString(Res.string.button_review)
    onNode(
        hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(reviewButtonText),
      )
      .performClick()

    onNodeWithText(getString(Res.string.edit_button_text)).performClick()

    onNodeWithTag(QUESTIONNAIRE_PROGRESS_INDICATOR_TEST_TAG).assertIsDisplayed()
  }

  @Test
  fun test_add_item_button_does_not_exist_for_non_repeated_groups() = runComposeUiTest {
    setQuestionnaireContent("files/component_non_repeated_group.json")
    onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).assertDoesNotExist()
  }

  @Test
  fun test_repeated_group_is_added() = runComposeUiTest {
    setQuestionnaireContent("files/component_repeated_group.json")
    onNodeWithTag(ADD_REPEATED_GROUP_BUTTON_TAG).performClick()

    onNodeWithTag(QUESTIONNAIRE_EDIT_LIST).assertExists().assertIsDisplayed()

    onNodeWithTag(REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG).assertIsDisplayed()

    onNodeWithTag(DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG).assertIsDisplayed()
  }

  @Test
  fun test_repeated_group_adds_multiple_items() = runComposeUiTest {
    setQuestionnaireContent("files/component_multiple_repeated_group.json")
    onNode(hasTestTag(ADD_REPEATED_GROUP_BUTTON_TAG) and hasText("Add Repeated Group"))
      .performClick()
    onNodeWithTag(DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG).assertIsDisplayed()
    onNodeWithTag(REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG).assertIsDisplayed()

    onNode(hasTestTag(ADD_REPEATED_GROUP_BUTTON_TAG) and hasText("Add Decimal Repeated Group"))
      .performClick()
    onAllNodes(hasTestTag(DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG)).assertCountEquals(2)
    onAllNodes(hasTestTag(REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG)).assertCountEquals(2)
  }

  @Test
  fun test_repeated_group_is_deleted() = runComposeUiTest {
    setQuestionnaireContent(
      "files/component_repeated_group.json",
      responseFileName = "files/repeated_group_response.json",
    )

    onNodeWithTag(QUESTIONNAIRE_EDIT_LIST).assertExists().assertIsDisplayed()
    onNodeWithTag(REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG).assertIsDisplayed()
    onNodeWithTag(DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG).performClick()
    onNodeWithTag(REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG).assertDoesNotExist()
  }

  @Test
  fun test_repeated_group_populates_multiple_answers() = runComposeUiTest {
    setQuestionnaireContent(
      "files/component_repeated_group.json",
      responseFileName = "files/repeated_group_multiple_response.json",
    )

    onAllNodes(hasTestTag(REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG)).assertCountEquals(2)
    onAllNodes(hasTestTag(DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG)).assertCountEquals(2)
  }

  @Test
  fun shouldHideNextButtonOnLastPage() = runComposeUiTest {
    val questionnaireJson =
      """{
  "resourceType": "Questionnaire",
  "status": "active",
  "item": [
    {
      "linkId": "1",
      "type": "group",
      "extension": [
        {
          "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
          "valueCodeableConcept": {
            "coding": [
              {
                "system": "http://hl7.org/fhir/questionnaire-item-control",
                "code": "page",
                "display": "Page"
              }
            ],
            "text": "Page"
          }
        }
      ],
      "item": [
        {
          "linkId": "1.1",
          "type": "display",
          "text": "Item 1"
        }
      ]
    },
    {
      "linkId": "2",
      "type": "group",
      "extension": [
        {
          "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
          "valueCodeableConcept": {
            "coding": [
              {
                "system": "http://hl7.org/fhir/questionnaire-item-control",
                "code": "page",
                "display": "Page"
              }
            ],
            "text": "Page"
          }
        }
      ],
      "item": [
        {
          "linkId": "2.1",
          "type": "display",
          "text": "Item 2"
        }
      ]
    }
  ]
}
"""
    val questionnaire = fhirR4Json.decodeFromString(questionnaireJson) as Questionnaire
    setQuestionnaireContent(questionnaire)
    val nextButtonText = getString(Res.string.button_pagination_next)
    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(nextButtonText))
      .performClick()
    onNode(hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(nextButtonText))
      .assertDoesNotExist()
  }

  @Test
  fun reviewPageShouldShowBothEditAndSubmitButton() = runComposeUiTest {
    val questionnaire =
      Questionnaire(
        id = "a-questionnaire",
        status = Enumeration(value = PublicationStatus.Active),
        item =
          listOf(
            Questionnaire.Item(
              linkId = FhirR4String(value = "a-link-id"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
            ),
          ),
      )
    setQuestionnaireContent(questionnaire, isReviewMode = true, showReviewPageFirst = true)

    onNodeWithText(getString(Res.string.edit_button_text)).assertIsDisplayed()
    onNode(
        hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and
          hasText(
            getString(Res.string.submit_questionnaire),
          ),
      )
      .assertIsDisplayed()
  }

  @Test
  fun questionnaireSubmitButtonTextShouldBeEditable() = runComposeUiTest {
    val questionnaire =
      Questionnaire(
        id = "a-questionnaire",
        status = Enumeration(value = PublicationStatus.Active),
        item =
          listOf(
            Questionnaire.Item(
              linkId = FhirR4String(value = "a-link-id"),
              type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
            ),
          ),
      )
    val customButtonText = "Apply"
    setQuestionnaireContent(questionnaire, submitText = customButtonText)
    onNode(
        hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and hasText(customButtonText),
      )
      .assertIsDisplayed()
  }

  @Test
  fun shouldHidePreviousButtonOnFirstPage() = runComposeUiTest {
    val questionnaireJson =
      """{
  "resourceType": "Questionnaire",
  "status": "active",
  "item": [
    {
      "linkId": "1",
      "type": "group",
      "extension": [
        {
          "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
          "valueCodeableConcept": {
            "coding": [
              {
                "system": "http://hl7.org/fhir/questionnaire-item-control",
                "code": "page",
                "display": "Page"
              }
            ],
            "text": "Page"
          }
        }
      ],
      "item": [
        {
          "linkId": "1.1",
          "type": "display",
          "text": "Item 1"
        }
      ]
    },
    {
      "linkId": "2",
      "type": "group",
      "extension": [
        {
          "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
          "valueCodeableConcept": {
            "coding": [
              {
                "system": "http://hl7.org/fhir/questionnaire-item-control",
                "code": "page",
                "display": "Page"
              }
            ],
            "text": "Page"
          }
        }
      ],
      "item": [
        {
          "linkId": "2.1",
          "type": "display",
          "text": "Item 2"
        }
      ]
    }
  ]
}
"""
    val questionnaire = fhirR4Json.decodeFromString(questionnaireJson) as Questionnaire
    setQuestionnaireContent(questionnaire)
    onNode(
        hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and
          hasText(
            getString(
              Res.string.button_pagination_previous,
            ),
          ),
      )
      .assertDoesNotExist()
  }

  @Test
  fun showBottomNavigationContainerWhenSetShowNavigationInDefaultLongScrollIsSetToFalse() =
    runComposeUiTest {
      val questionnaire =
        Questionnaire(
          id = "a-questionnaire",
          status = Enumeration(value = PublicationStatus.Active),
          item =
            listOf(
              Questionnaire.Item(
                linkId = FhirR4String(value = "a-link-id"),
                type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
              ),
            ),
        )
      setQuestionnaireContent(questionnaire, showNavigationLongScroll = false)

      onNode(
          hasTestTag(QUESTIONNAIRE_BOTTOM_NAVIGATION_TEST_TAG) and
            hasAnyAncestor(hasTestTag(QUESTIONNAIRE_EDIT_LIST)),
        )
        .assertDoesNotExist()

      onNode(
          hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and
            hasText(
              getString(Res.string.submit_questionnaire),
            ),
        )
        .assertIsDisplayed()
        .assertIsEnabled()
    }

  @Test
  fun hideTheBottomNavigationContainerWhenSetShowNavigationInDefaultLongScrollIsSetToTrue() =
    runComposeUiTest {
      val questionnaire =
        Questionnaire(
          id = "a-questionnaire",
          status = Enumeration(value = PublicationStatus.Active),
          item =
            listOf(
              Questionnaire.Item(
                linkId = FhirR4String(value = "a-link-id"),
                type = Enumeration(value = Questionnaire.QuestionnaireItemType.Boolean),
              ),
            ),
        )
      setQuestionnaireContent(questionnaire, showNavigationLongScroll = true)

      onNode(
          hasTestTag(QUESTIONNAIRE_BOTTOM_NAVIGATION_TEST_TAG) and
            hasAnyAncestor(hasTestTag(QUESTIONNAIRE_EDIT_LIST)),
        )
        .assertExists()

      onNode(
          hasTestTag(QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG) and
            hasText(
              getString(Res.string.submit_questionnaire),
            ),
        )
        .assertIsDisplayed()
        .assertIsEnabled()
    }

  private suspend fun ComposeUiTest.setQuestionnaireContent(
    fileName: String,
    isReviewMode: Boolean = false,
    responseFileName: String? = null,
  ) {
    val questionnaireJsonString = Res.readBytes(fileName).decodeToString()
    val questionnaireResponseJsonString =
      responseFileName?.let { Res.readBytes(it).decodeToString() }

    val testLifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
    setContent {
      val viewModelStoreOwner = remember {
        object : ViewModelStoreOwner {
          override val viewModelStore = ViewModelStore()
        }
      }

      CompositionLocalProvider(
        LocalLifecycleOwner provides testLifecycleOwner,
        LocalViewModelStoreOwner provides viewModelStoreOwner,
      ) {
        Questionnaire(
          questionnaireJson = questionnaireJsonString,
          questionnaireResponseJson = questionnaireResponseJsonString,
          showCancelButton = true,
          showReviewPage = isReviewMode,
          onSubmit = { _ -> },
          onCancel = {},
        )
      }
    }
  }

  private suspend fun ComposeUiTest.setQuestionnaireContent(
    questionnaire: Questionnaire,
    isReviewMode: Boolean = false,
    showReviewPageFirst: Boolean = false,
    showNavigationLongScroll: Boolean = false,
    submitText: String? = null,
  ) {
    val questionnaireJsonString = fhirR4Json.encodeToString(questionnaire)

    val testLifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
    setContent {
      val viewModelStoreOwner = remember {
        object : ViewModelStoreOwner {
          override val viewModelStore = ViewModelStore()
        }
      }

      CompositionLocalProvider(
        LocalLifecycleOwner provides testLifecycleOwner,
        LocalViewModelStoreOwner provides viewModelStoreOwner,
      ) {
        Questionnaire(
          questionnaireJson = questionnaireJsonString,
          showReviewPage = isReviewMode,
          showReviewPageFirst = showReviewPageFirst,
          showNavigationLongScroll = showNavigationLongScroll,
          submitButtonText = submitText,
          onSubmit = { _ -> },
          onCancel = {},
        )
      }
    }
  }
}
