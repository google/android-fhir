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
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.TestActivity
import com.google.android.fhir.datacapture.extensions.DisplayItemControlType
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.utilities.assertQuestionnaireResponseAtIndex
import com.google.android.fhir.datacapture.utilities.clickOnText
import com.google.android.fhir.datacapture.utilities.clickOnTextInDialog
import com.google.android.fhir.datacapture.utilities.endIconClickInTextInputLayout
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemDialogSelectViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.android.material.textfield.TextInputLayout
import com.google.common.truth.StringSubject
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matchers.not
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
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
    var answerHolder: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(true, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5"),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 1")
    clickOnText("Coding 3")
    clickOnText("Coding 5")
    clickOnText("Save")

    assertDisplayedText().isEqualTo("Coding 1, Coding 3, Coding 5")
    assertQuestionnaireResponseAtIndex(answerHolder!!, "Coding 1", "Coding 3", "Coding 5")
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Save")

    assertDisplayedText().isEmpty()
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 3")
    clickOnText("Coding 1")
    clickOnText("Cancel")

    assertDisplayedText().isEmpty()
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Coding 1")
    clickOnText("Save")

    assertDisplayedText().isEqualTo("Coding 1")
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Save")

    assertDisplayedText().isEqualTo("Coding 2")
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Save")

    assertDisplayedText().isEmpty()
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
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }
    val questionnaireViewItem =
      QuestionnaireViewItem(
        answerOptions(false, "Coding 1", "Coding 2", "Coding 3", "Coding 4", "Coding 5")
          .addItem(hintItem),
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        enabledDisplayItems = listOf(hintItem)
      )
    runOnUI { viewHolder.bind(questionnaireViewItem) }

    assertThat(
        viewHolder.itemView
          .findViewById<TextInputLayout>(R.id.multi_select_summary_holder)
          .hint.toString()
      )
      .isEqualTo("Select code")
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }
    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Cancel")

    assertDisplayedText().isEmpty()
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
        "Coding 8"
      )
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    onView(withId(R.id.recycler_view))
      .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(8))
    clickOnTextInDialog("Other")
    onView(withId(R.id.add_another)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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
        "Coding 8"
      )
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    onView(withId(R.id.recycler_view))
      .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(8))
    clickOnTextInDialog("Other")
    clickOnTextInDialog("Other")
    onView(ViewMatchers.withId(R.id.add_another)).check(doesNotExist())
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
        "Coding 8"
      )
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        responseOptions(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    onView(withId(R.id.recycler_view))
      .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(8))
    clickOnTextInDialog("Other")
    onView(withId(R.id.add_another)).perform(click())
    onView(withId(R.id.add_another)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
  }

  @Test
  fun `shouldHideErrorTextviewInHeader`() {
    val questionnaireItem = answerOptions(true, "Coding 1")
    questionnaireItem.addExtension(openChoiceType)
    val questionnaireViewItem =
      QuestionnaireViewItem(
        questionnaireItem,
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    onView(withId(R.id.error_text_at_header)).check(matches(not(isDisplayed())))
  }

  @Test
  fun show_asterisk() {
    runOnUI {
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
          questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true)
        )
      )

      assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
        .isEqualTo("Question? *")
    }
  }

  @Test
  fun hide_asterisk() {
    runOnUI {
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
          questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false)
        )
      )

      assertThat(viewHolder.itemView.findViewById<TextView>(R.id.question).text.toString())
        .isEqualTo("Question?")
    }
  }

  @Test
  fun show_requiredText() {
    runOnUI {
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
          questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true)
        )
      )

      assertThat(
          viewHolder.itemView
            .findViewById<TextInputLayout>(R.id.multi_select_summary_holder)
            .helperText.toString()
        )
        .isEqualTo("Required")
    }
  }

  @Test
  fun hide_requiredText() {
    runOnUI {
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
          questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false)
        )
      )

      assertThat(
          viewHolder.itemView
            .findViewById<TextInputLayout>(R.id.multi_select_summary_holder)
            .helperText
        )
        .isNull()
    }
  }

  @Test
  fun shows_optionalText() {
    runOnUI {
      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.QuestionnaireItemComponent().apply { linkId = "1" },
          QuestionnaireResponse.QuestionnaireResponseItemComponent(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true)
        )
      )
      assertThat(
          viewHolder.itemView
            .findViewById<TextInputLayout>(R.id.multi_select_summary_holder)
            .helperText.toString()
        )
        .isEqualTo("Optional")
    }
  }

  @Test
  fun hide_optionalText() {
    runOnUI {
      viewHolder.bind(
        QuestionnaireViewItem(
          Questionnaire.QuestionnaireItemComponent().apply { linkId = "1" },
          QuestionnaireResponse.QuestionnaireResponseItemComponent(),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
          questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false)
        )
      )
      assertThat(
          viewHolder.itemView
            .findViewById<TextInputLayout>(R.id.multi_select_summary_holder)
            .helperText
        )
        .isNull()
    }
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.multi_select_summary_holder).error
      )
      .isNull()
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

    runOnUI { viewHolder.bind(questionnaireViewItem) }

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Save")
    assertDisplayedText().isEqualTo("Coding 2")

    endIconClickInTextInputLayout(R.id.multi_select_summary_holder)
    clickOnTextInDialog("Coding 2")
    clickOnText("Save")

    assertThat(
        viewHolder.itemView.findViewById<TextInputLayout>(R.id.multi_select_summary_holder).error
      )
      .isEqualTo("Missing answer for required field.")
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

  private val openChoiceType =
    Extension().apply {
      url = EXTENSION_ITEM_CONTROL_URL
      setValue(
        CodeableConcept()
          .addCoding(
            Coding()
              .setCode(ItemControlTypes.OPEN_CHOICE.extensionCode)
              .setDisplay("Open Choice")
              .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
          )
      )
    }

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
