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
import android_fhir.datacapture_kmp.generated.resources.select_date
import android_fhir.datacapture_kmp.generated.resources.submit_questionnaire
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4DateType
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.components.ADD_REPEATED_GROUP_BUTTON_TAG
import com.google.android.fhir.datacapture.views.components.DATE_TEXT_INPUT_FIELD
import com.google.android.fhir.datacapture.views.components.DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG
import com.google.android.fhir.datacapture.views.components.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.components.HANDLE_INPUT_DEBOUNCE_TIME
import com.google.android.fhir.datacapture.views.components.HINT_HEADER_TAG
import com.google.android.fhir.datacapture.views.components.QUESTIONNAIRE_BOTTOM_NAVIGATION_TEST_TAG
import com.google.android.fhir.datacapture.views.components.QUESTIONNAIRE_PAGE_NAVIGATION_BUTTON_TEST_TAG
import com.google.android.fhir.datacapture.views.components.QUESTION_HEADER_TAG
import com.google.android.fhir.datacapture.views.components.REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG
import com.google.android.fhir.datacapture.views.components.TIME_PICKER_INPUT_FIELD
import com.google.android.fhir.datacapture.views.factories.NO_CHOICE_RADIO_BUTTON_TAG
import com.google.android.fhir.datacapture.views.factories.YES_CHOICE_RADIO_BUTTON_TAG
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.terminologies.PublicationStatus
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.kotest.matchers.shouldBe
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class UIQuestionnaireTest {

  private val testDispatcher = StandardTestDispatcher()

  @BeforeTest
  fun setup() {
    Dispatchers.setMain(testDispatcher)
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

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

  @Test
  fun integerTextEdit_inputOutOfRange_shouldShowError() = runComposeUiTest {
    setQuestionnaireContent("files/text_questionnaire_integer.json")

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("12345678901")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)

    onNodeWithText("Number must be between -2,147,483,648 and 2,147,483,647").assertIsDisplayed()
    onNodeWithContentDescription("Error").assertIsDisplayed()
  }

  @Test
  fun integerTextEdit_typingZeroBeforeAnyIntegerShouldKeepZeroDisplayed() = runComposeUiTest {
    // Do not skip cursor when typing on the numeric field if the initial value is set to 0
    // as from an integer comparison, leading zeros do not change how the answer is saved.
    // e.g whether 000001 or 1 is input, the answer saved will be 1.

    val getQuestionnaireResponseFunc =
      setQuestionnaireContent("files/text_questionnaire_integer.json")

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("0")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)

    getQuestionnaireResponseFunc()
      .item
      .first()
      .answer
      .first()
      .value
      ?.asInteger()
      ?.value
      ?.value
      .shouldBe(0)

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("01")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)

    getQuestionnaireResponseFunc()
      .item
      .first()
      .answer
      .first()
      .value
      ?.asInteger()
      ?.value
      ?.value
      .shouldBe(1)

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("001")

    getQuestionnaireResponseFunc()
      .item
      .first()
      .answer
      .first()
      .value
      ?.asInteger()
      ?.value
      ?.value
      .shouldBe(1)
  }

  @Test
  fun decimalTextEdit_typingZeroBeforeAnyIntegerShouldKeepZeroDisplayed() = runComposeUiTest {
    val getQuestionnaireResponseFunc =
      setQuestionnaireContent("files/text_questionnaire_decimal.json")

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("0.")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)
    getQuestionnaireResponseFunc()
      .item
      .first()
      .answer
      .first()
      .value
      ?.asDecimal()
      ?.value
      ?.value
      .shouldBe(0.toBigDecimal())

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("01")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("0.01")
    getQuestionnaireResponseFunc()
      .item
      .first()
      .answer
      .first()
      .value
      ?.asDecimal()
      ?.value
      ?.value
      .shouldBe(0.01.toBigDecimal())
  }

  @Test
  fun decimalTextEdit_typingInvalidTextShouldShowError() = runComposeUiTest {
    setQuestionnaireContent("files/text_questionnaire_decimal.json")

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextInput("1.1.1.1")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)

    onNodeWithText("Invalid number").assertIsDisplayed()
    onNodeWithContentDescription("Error").assertIsDisplayed()
  }

  @Test
  fun dateTimePicker_shouldShowErrorForWrongDate() = runComposeUiTest {
    setQuestionnaireContent("files/component_date_time_picker.json")

    // Add month and day. No need to add slashes as they are added automatically
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextReplacement("0105")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)
    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.keyIsDefined(
          SemanticsProperties.Error,
        ),
      )
    onNodeWithTag(TIME_PICKER_INPUT_FIELD).assertIsNotEnabled()
  }

  @Test
  fun datePicker_shouldShowErrorForWrongDate() = runComposeUiTest {
    setQuestionnaireContent("files/component_date_picker.json")

    //     Add month and day. No need to add slashes as they are added automatically
    onNodeWithTag(DATE_TEXT_INPUT_FIELD).performTextInput("0105")
    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)
    onNodeWithTag(DATE_TEXT_INPUT_FIELD)
      .assert(
        SemanticsMatcher.keyIsDefined(
          SemanticsProperties.Error,
        ),
      )
  }

  @Test
  fun datePicker_shouldSetDateInput_withinRange() = runComposeUiTest {
    val questionnaire =
      Questionnaire(
        id = "a-questionnaire",
        status = Enumeration(value = PublicationStatus.Active),
        item =
          listOf(
            Questionnaire.Item(
              linkId =
                FhirR4String(
                  value = "link-1",
                ),
              type =
                Enumeration(
                  value = Questionnaire.QuestionnaireItemType.Date,
                ),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minValue",
                    value =
                      Extension.Value.Date(
                        value =
                          FhirR4DateType(
                            value =
                              FhirDate.Date(
                                Clock.System.now()
                                  .toLocalDateTime(TimeZone.currentSystemDefault())
                                  .date
                                  .minus(
                                    DatePeriod(
                                      years = 1,
                                    ),
                                  ),
                              ),
                          ),
                      ),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                    value =
                      Extension.Value.Date(
                        value =
                          FhirR4DateType(
                            value =
                              FhirDate.Date(
                                Clock.System.now()
                                  .toLocalDateTime(TimeZone.currentSystemDefault())
                                  .date
                                  .plus(
                                    DatePeriod(
                                      years = 4,
                                    ),
                                  ),
                              ),
                          ),
                      ),
                  ),
                ),
            ),
          ),
      )

    val getQuestionnaireResponseFunc = setQuestionnaireContent(questionnaire)
    onNodeWithContentDescription(getString(Res.string.select_date)).performClick()
    onNode(hasText("OK") and hasAnyAncestor(isDialog())).assertIsDisplayed().performClick()

    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)
    val questionnaireResponse = getQuestionnaireResponseFunc()

    (questionnaireResponse.item.first().answer.first().value?.asDate()?.value?.value
        as? FhirDate.Date)
      ?.date
      .shouldBe(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    //      val today = DateTimeType.today().valueAsString

    //      val answer =
    //   getQuestionnaireResponse().item.first().answer.first().valueDateType.valueAsString
    //      assertThat(answer).isEqualTo(today)
    //
    val validationResult =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse,
      )
    validationResult["link-1"]?.first().shouldBe(Valid)
  }

  @Test
  fun datePicker_shouldNotSetDateInput_outsideMaxRange() = runComposeUiTest {
    val maxDate =
      Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .minus(
          DatePeriod(
            years = 2,
          ),
        )
    val questionnaire =
      Questionnaire(
        id = "a-questionnaire",
        status = Enumeration(value = PublicationStatus.Active),
        item =
          listOf(
            Questionnaire.Item(
              linkId = FhirR4String(value = "link-1"),
              type =
                Enumeration(
                  value = Questionnaire.QuestionnaireItemType.Date,
                ),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minValue",
                    value =
                      Extension.Value.Date(
                        value =
                          FhirR4DateType(
                            value =
                              FhirDate.Date(
                                Clock.System.now()
                                  .toLocalDateTime(TimeZone.currentSystemDefault())
                                  .date
                                  .minus(
                                    DatePeriod(
                                      years = 4,
                                    ),
                                  ),
                              ),
                          ),
                      ),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                    value =
                      Extension.Value.Date(
                        value =
                          FhirR4DateType(
                            value =
                              FhirDate.Date(
                                maxDate,
                              ),
                          ),
                      ),
                  ),
                ),
            ),
          ),
      )

    val getQuestionnaireResponseFunc = setQuestionnaireContent(questionnaire)
    onNodeWithContentDescription(getString(Res.string.select_date)).performClick()
    onNode(hasText("OK") and hasAnyAncestor(isDialog())).assertIsDisplayed().performClick()

    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)
    val questionnaireResponse = getQuestionnaireResponseFunc()

    val maxDateAllowed = maxDate.toString()

    val validationResult =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse,
      )

    (validationResult["link-1"]?.first() as Invalid)
      .singleStringValidationMessage
      .shouldBe("Maximum value allowed is:$maxDateAllowed")
  }

  @Test
  fun datePicker_shouldNotSetDateInput_outsideMinRange() = runComposeUiTest {
    val minDate =
      Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .plus(
          DatePeriod(
            years = 1,
          ),
        )
    val questionnaire =
      Questionnaire(
        id = "a-questionnaire",
        status = Enumeration(value = PublicationStatus.Active),
        item =
          listOf(
            Questionnaire.Item(
              linkId = FhirR4String(value = "link-1"),
              type =
                Enumeration(
                  value = Questionnaire.QuestionnaireItemType.Date,
                ),
              extension =
                listOf(
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/minValue",
                    value =
                      Extension.Value.Date(
                        value =
                          FhirR4DateType(
                            value =
                              FhirDate.Date(
                                minDate,
                              ),
                          ),
                      ),
                  ),
                  Extension(
                    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                    value =
                      Extension.Value.Date(
                        value =
                          FhirR4DateType(
                            value =
                              FhirDate.Date(
                                Clock.System.now()
                                  .toLocalDateTime(TimeZone.currentSystemDefault())
                                  .date
                                  .plus(
                                    DatePeriod(
                                      years = 2,
                                    ),
                                  ),
                              ),
                          ),
                      ),
                  ),
                ),
            ),
          ),
      )

    val getQuestionnaireResponseFunc = setQuestionnaireContent(questionnaire)
    onNodeWithContentDescription(getString(Res.string.select_date)).performClick()
    onNode(hasText("OK") and hasAnyAncestor(isDialog())).assertIsDisplayed().performClick()

    mainClock.advanceTimeBy(HANDLE_INPUT_DEBOUNCE_TIME + 500L)
    val questionnaireResponse = getQuestionnaireResponseFunc()

    val minDateAllowed = minDate.toString()

    val validationResult =
      QuestionnaireResponseValidator.validateQuestionnaireResponse(
        questionnaire,
        questionnaireResponse,
      )

    (validationResult["link-1"]?.first() as Invalid)
      .singleStringValidationMessage
      .shouldBe("Minimum value allowed is:$minDateAllowed")
  }

  @Test
  fun datePicker_shouldProhibitInputWithErrorMessage_whenMinValueRangeIsGreaterThanMaxValueRange() =
    runComposeUiTest {
      val questionnaire =
        Questionnaire(
          id = "a-questionnaire",
          status = Enumeration(value = PublicationStatus.Active),
          item =
            listOf(
              Questionnaire.Item(
                linkId = FhirR4String(value = "link-1"),
                type =
                  Enumeration(
                    value = Questionnaire.QuestionnaireItemType.Date,
                  ),
                extension =
                  listOf(
                    Extension(
                      url = "http://hl7.org/fhir/StructureDefinition/minValue",
                      value =
                        Extension.Value.Date(
                          value =
                            FhirR4DateType(
                              value =
                                FhirDate.Date(
                                  Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date
                                    .plus(
                                      DatePeriod(
                                        years = 1,
                                      ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                    Extension(
                      url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                      value =
                        Extension.Value.Date(
                          value =
                            FhirR4DateType(
                              value =
                                FhirDate.Date(
                                  Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date
                                    .minus(
                                      DatePeriod(
                                        years = 1,
                                      ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                  ),
              ),
            ),
        )

      setQuestionnaireContent(questionnaire)
      onNodeWithTag(DATE_TEXT_INPUT_FIELD)
        .assert(
          SemanticsMatcher.expectValue(
            SemanticsProperties.Error,
            "minValue cannot be greater than maxValue",
          ),
        )
      onNodeWithTag(DATE_TEXT_INPUT_FIELD).assertIsNotEnabled()
      onNodeWithContentDescription(getString(Res.string.select_date)).assertIsNotEnabled()
    }

  //  @Test
  //  fun clearAllAnswers_shouldClearDraftAnswer() =
  //    runComposeUiTest {
  //          val questionnaireFragment =
  // setQuestionnaireContent("files/component_date_picker.json")
  //
  // //       Add month and day. No need to add slashes as they are added automatically
  //            onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
  //              .performTextInput("0105")
  //            onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
  //              .assertTextEquals("01/05")
  //            delay(1.seconds) // Add delay to give time for new questionnaire state
  //            awaitIdle()
  //
  //            questionnaireFragment.clearAllAnswers()
  //            awaitIdle()
  //
  //            onNodeWithTag(DATE_TEXT_INPUT_FIELD, useUnmergedTree = true)
  //              .assertTextEquals("")
  //    }

  // todo: Add a test for questionnaire slider view to check minAnswerValue and maxAnswerValue with
  // cqf-calculated extension work correctly
}
