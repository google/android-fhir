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
import android.widget.TextView
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.DisplayItemControlType
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_OPTION_EXCLUSIVE_URL
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.test.TestActivity
import com.google.android.fhir.datacapture.test.utilities.assertQuestionnaireResponseAtIndex
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.OPTION_CHOICE_LIST_TAG
import com.google.android.fhir.datacapture.views.compose.OPTION_CHOICE_TAG
import com.google.android.fhir.datacapture.views.compose.OTHER_OPTION_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.factories.DialogSelectViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.MULTI_SELECT_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DialogMultiSelectViewHolderFactoryTest {
  @get:Rule
  var activityScenarioRule: ActivityScenarioRule<TestActivity> =
    ActivityScenarioRule(TestActivity::class.java)

  @get:Rule val composeTestRule = createEmptyComposeRule()

  private lateinit var viewHolder: QuestionnaireItemViewHolder

  @Before
  fun setup() {
    activityScenarioRule.scenario.onActivity { activity ->
      viewHolder = DialogSelectViewHolderFactory.create(FrameLayout(activity))
      activity.setContentView(viewHolder.itemView)
    }

    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
  }

  @Test
  fun multipleChoice_selectMultiple_clickSave_shouldSaveMultipleOptions() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    // Click to open the dialog
    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    // Select options in the dialog
    composeTestRule.onNodeWithText("Coding 1").performClick()
    composeTestRule.onNodeWithText("Coding 3").performClick()
    composeTestRule.onNodeWithText("Coding 5").performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding 1, Coding 3, Coding 5")
      .assertIsDisplayed()
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding 1", "Coding 3", "Coding 5")
  }

  @Test
  fun multipleChoice_selectMultiple_selectExclusive_clickSave_shouldSaveOnlyExclusiveOption() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3")
          .addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding Exclusive" }
              extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
            },
          ),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding 1").performClick()
    composeTestRule.onNodeWithText("Coding 3").performClick()
    composeTestRule.onNodeWithText("Coding Exclusive").performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding Exclusive")
      .assertIsDisplayed()
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding Exclusive")
  }

  @Test
  fun multipleChoice_selectExclusive_selectMultiple_clickSave_shouldSaveWithoutExclusiveOption() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3")
          .addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding Exclusive" }
              extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
            },
          ),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding Exclusive").performClick()
    composeTestRule.onNodeWithText("Coding 1").performClick()
    composeTestRule.onNodeWithText("Coding 3").performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding 1, Coding 3")
      .assertIsDisplayed()
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding 1", "Coding 3")
  }

  @Test
  fun multipleChoice_multipleOptionExclusive_selectMultiple_selectExclusive1_selectExclusive2_clickSave_shouldSaveOnlyLastSelectedExclusiveOption() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3")
          .addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding Exclusive 1" }
              extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
            },
          )
          .addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding Exclusive 2" }
              extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
            },
          ),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding 1").performClick()
    composeTestRule.onNodeWithText("Coding 3").performClick()
    composeTestRule.onNodeWithText("Coding Exclusive 1").performClick()
    composeTestRule.onNodeWithText("Coding Exclusive 2").performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding Exclusive 2")
      .assertIsDisplayed()
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding Exclusive 2")
  }

  @Test
  fun multipleChoice_multipleOptionExclusive_selectExclusive1_selectExclusive2_selectMultiple_clickSave_shouldSaveWithoutAnyExclusiveOption() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3")
          .addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding Exclusive 1" }
              extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
            },
          )
          .addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "Coding Exclusive 2" }
              extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
            },
          ),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding Exclusive 1").performClick()
    composeTestRule.onNodeWithText("Coding Exclusive 2").performClick()
    composeTestRule.onNodeWithText("Coding 1").performClick()
    composeTestRule.onNodeWithText("Coding 3").performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding 1, Coding 3")
      .assertIsDisplayed()
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding 1", "Coding 3")
  }

  @Test
  fun multipleChoice_SelectNothing_clickSave_shouldSaveNothing() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    // When nothing is selected, the field should be empty
    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("")
      .assertIsDisplayed()
    assertThat(questionnaireViewItem.answers).isEmpty()
  }

  @Test
  fun multipleChoice_selectMultiple_clickCancel_shouldSaveNothing() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding 3").performClick()
    composeTestRule.onNodeWithText("Coding 1").performClick()
    composeTestRule.onNodeWithText("Cancel").performClick()

    // When cancelled, nothing should be saved
    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("")
      .assertIsDisplayed()
    assertThat(questionnaireViewItem.answers).isEmpty()
  }

  @Test
  fun shouldSelectSingleOptionOnChangeInOptionFromDropDown() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding 2").performClick()
    composeTestRule.onNodeWithText("Coding 1").performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding 1")
      .assertIsDisplayed()
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding 1")
  }

  @Test
  fun singleOption_select_clickSave_shouldSaveSingleOption() {
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding 2").performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertTextEquals("Coding 2")
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding 2")
  }

  @Test
  fun singleOption_selectNothing_clickSave_shouldSaveNothing() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    // When nothing is selected, the field should be empty
    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("")
      .assertIsDisplayed()
    assertThat(questionnaireViewItem.answers).isEmpty()
  }

  @Test
  fun bindView_setHintText() {
    val hintItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "1.1"
        text = "Select code"
        type = Questionnaire.QuestionnaireItemType.DISPLAY
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.FLYOVER.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM),
                ),
            ),
        )
      }
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5")
          .addItem(hintItem),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        enabledDisplayItems = listOf(hintItem),
      )
    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithText("Select code").assertIsDisplayed()
  }

  @Test
  fun singleOption_select_clickCancel_shouldSaveNothing() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNodeWithText("Coding 2").performClick()
    composeTestRule.onNodeWithText("Cancel").performClick()
    composeTestRule.waitForIdle()

    // When cancelled, nothing should be saved
    assertThat(questionnaireViewItem.answers).isEmpty()
  }

  @Test
  fun selectOther_shouldScrollDownToShowAddAnotherAnswer() {
    val questionnaireItem =
      answerOptions(
        true,
        "Coding 1",
        "Coding 2",
        "Coding 3",
        "Coding 4",
        "Coding 5",
        "Coding 6",
        "Coding 7",
        "Coding 8",
      )
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    val context = viewHolder.itemView.context
    // Select "Other" option
    val otherText = context.getString(R.string.open_choice_other)
    composeTestRule.onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    composeTestRule.onNodeWithText(otherText).performClick()

    // "Add Another" button should be displayed in multi-select mode
    composeTestRule
      .onNodeWithText(context.getString(R.string.open_choice_other_add_another))
      .assertIsDisplayed()
  }

  @Test
  fun unselectOther_shouldHideAddAnotherAnswer() {
    val questionnaireItem =
      answerOptions(
        true,
        "Coding 1",
        "Coding 2",
        "Coding 3",
        "Coding 4",
        "Coding 5",
        "Coding 6",
        "Coding 7",
        "Coding 8",
      )
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    val context = viewHolder.itemView.context
    // Select and then unselect "Other" option
    val otherText = context.getString(R.string.open_choice_other)
    composeTestRule.onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    composeTestRule.onNodeWithText(otherText).performClick()
    composeTestRule.onNodeWithText(otherText).performClick()

    // "Add Another" button should not be displayed when "Other" is unselected
    composeTestRule
      .onNodeWithText(context.getString(R.string.open_choice_other_add_another))
      .assertDoesNotExist()
  }

  @Test
  fun clickAddAnotherAnswer_shouldScrollDownToShowAddAnotherAnswer() {
    val questionnaireItem =
      answerOptions(
        true,
        "Coding 1",
        "Coding 2",
        "Coding 3",
        "Coding 4",
        "Coding 5",
        "Coding 6",
        "Coding 7",
        "Coding 8",
      )
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    val context = viewHolder.itemView.context
    // Select "Other" option
    val otherText = context.getString(R.string.open_choice_other)
    composeTestRule.onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    composeTestRule.onNodeWithText(otherText).performClick()

    // Click "Add Another" button
    val addAnotherText = context.getString(R.string.open_choice_other_add_another)
    composeTestRule
      .onNodeWithTag(OPTION_CHOICE_LIST_TAG)
      .performScrollToNode(hasText(addAnotherText))
    composeTestRule.onNodeWithText(addAnotherText).performClick()

    // "Add Another" button should still be displayed after clicking
    composeTestRule.onNodeWithText(addAnotherText).assertIsDisplayed()
  }

  @Test
  fun selectOther_selectExclusive_shouldHideAddAnotherAnswer() {
    val questionnaireItem =
      answerOptions(
          true,
          "Coding 1",
          "Coding 2",
          "Coding 3",
          "Coding 4",
          "Coding 5",
          "Coding 6",
          "Coding 7",
          "Coding 8",
        )
        .addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = "Coding Exclusive" }
            extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
          },
        )

    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    val context = viewHolder.itemView.context
    // Select "Other" option
    val otherText = context.getString(R.string.open_choice_other)
    composeTestRule.onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    composeTestRule.onNodeWithText(otherText).performClick()

    // Select exclusive option
    composeTestRule.onNodeWithText("Coding Exclusive").performClick()

    // "Add Another" button should not be displayed when exclusive option is selected
    composeTestRule
      .onNodeWithText(context.getString(R.string.open_choice_other_add_another))
      .assertDoesNotExist()
  }

  @Test
  fun selectOther_clickAddAnotherAnswer_selectExclusive_shouldHideAddAnotherAnswerWithEditText() {
    val questionnaireItem =
      answerOptions(
          true,
          "Coding 1",
          "Coding 2",
          "Coding 3",
          "Coding 4",
          "Coding 5",
          "Coding 6",
          "Coding 7",
          "Coding 8",
        )
        .addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = "Coding Exclusive" }
            extension = listOf(Extension(EXTENSION_OPTION_EXCLUSIVE_URL, BooleanType(true)))
          },
        )

    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    val context = viewHolder.itemView.context
    // Select "Other" option
    val otherText = context.getString(R.string.open_choice_other)
    composeTestRule.onNodeWithTag(OPTION_CHOICE_LIST_TAG).performScrollToNode(hasText(otherText))
    composeTestRule.onNodeWithText(otherText).performClick()

    // Click "Add Another" button
    composeTestRule
      .onNodeWithText(context.getString(R.string.open_choice_other_add_another))
      .performClick()

    // Select exclusive option
    composeTestRule.onNodeWithText("Coding Exclusive").performClick()

    // "Add Another" button and edit text should not be displayed when exclusive option is selected
    composeTestRule.onAllNodes(hasTestTag(OTHER_OPTION_TEXT_FIELD_TAG)).assertCountEquals(0)
    composeTestRule
      .onNodeWithText(context.getString(R.string.open_choice_other_add_another))
      .assertDoesNotExist()
  }

  @Test
  fun shouldHideErrorTextviewInHeader() {
    val questionnaireItem = answerOptions(true, "Coding 1")
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)
    composeTestRule.onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun show_asterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      ),
    )
    composeTestRule.waitForIdle()

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question? *")
  }

  @Test
  fun hide_asterisk() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          text = "Question?"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      ),
    )
    composeTestRule.waitForIdle()

    assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
      .isEqualTo("Question?")
  }

  @Test
  fun show_requiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          required = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      ),
    )

    // The "Required" text should be displayed in the supporting text of the OutlinedTextField
    composeTestRule.onNodeWithText("Required").assertIsDisplayed()
  }

  @Test
  fun hide_requiredText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          required = true
          text = "Question?"
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      ),
    )

    // When showRequiredText is false, "Required" text should not be displayed
    composeTestRule.onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun shows_optionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "1" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      ),
    )

    // The "Optional" text should be displayed in the supporting text of the OutlinedTextField
    composeTestRule.onNodeWithText("Optional").assertIsDisplayed()
  }

  @Test
  fun hide_optionalText() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "1" },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      ),
    )

    // When showOptionalText is false, "Optional" text should not be displayed
    composeTestRule.onNodeWithText("Optional").assertDoesNotExist()
  }

  @Test
  fun multipleChoice_doNotShowErrorInitially() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5").apply {
          required = true
        },
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)
    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.keyNotDefined(
          SemanticsProperties.Error,
        ),
      )
  }

  @Test
  fun multipleChoice_unselectSelectedAnswer_showErrorWhenNoAnswerIsSelected() {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2").apply { required = true },
        responseOptions(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, answers, _ -> },
      )

    viewHolder.bind(questionnaireViewItem)

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNode(hasTestTag(OPTION_CHOICE_TAG) and hasText("Coding 2")).performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.EditableText,
          AnnotatedString("Coding 2"),
        ),
      )
      .assertIsDisplayed()

    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).performClick()

    composeTestRule.onNode(hasTestTag(OPTION_CHOICE_TAG) and hasText("Coding 2")).performClick()
    composeTestRule
      .onNodeWithText(viewHolder.itemView.context.getString(R.string.save))
      .performClick()

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.EditableText,
          AnnotatedString(""),
        ),
      )
      .assertIsDisplayed()
    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Missing answer for required field.",
        ),
      )
  }

  @Test
  fun emptyResponseOptions_showNoneSelected() {
    viewHolder.bind(
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("")
      .assertIsDisplayed()
  }

  @Test
  fun selectedResponseOptions_showSelectedOptions() {
    viewHolder.bind(
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3"),
        responseOptions(
          "Coding 1",
          "Coding 3",
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assertTextEquals("Coding 1, Coding 3")
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          required = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.Error,
          "Missing answer for required field.",
        ),
      )
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          required = true
          addAnswerOption(
            Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
              value = Coding().apply { display = "display" }
            },
          )
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
          addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = Coding().apply { display = "display" }
            },
          )
        },
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )

    composeTestRule
      .onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG)
      .assert(
        SemanticsMatcher.keyNotDefined(
          SemanticsProperties.Error,
        ),
      )
  }

  @Test
  fun bind_readOnly_shouldDisableView() {
    viewHolder.bind(
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "1"
          readOnly = true
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      ),
    )
    composeTestRule.onNodeWithTag(MULTI_SELECT_TEXT_FIELD_TAG).assertIsNotEnabled()
  }

  private val openChoiceType =
    Extension().apply {
      url = EXTENSION_ITEM_CONTROL_URL
      setValue(
        CodeableConcept()
          .addCoding(
            Coding()
              .setCode(ItemControlTypes.OPEN_CHOICE.extensionCode)
              .setDisplay("Open Choice")
              .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM),
          ),
      )
    }

  private fun answerOptions(multiSelect: Boolean, vararg options: String) =
    Questionnaire.QuestionnaireItemComponent().apply {
      this.repeats = multiSelect
      linkId = "1"
      options.forEach { option ->
        addAnswerOption(
          Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
            value = Coding().apply { display = option }
          },
        )
      }
    }

  private fun responseOptions(vararg responses: String) =
    QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
      responses.forEach { response ->
        addAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = Coding().apply { display = response }
          },
        )
      }
    }
}
