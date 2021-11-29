/*
 * Copyright 2020 Google LLC
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

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.fhir.datacapture.views.QuestionnaireItemAutoCompleteViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemBooleanTypePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDatePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDateTimePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDialogSelectViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDisplayViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDropDownViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextDecimalViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextIntegerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextMultiLineViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextQuantityViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextSingleLineViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemRadioGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemSliderViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType

internal class QuestionnaireItemAdapter(
  private val questionnaireItemViewHolderMatchers:
    List<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher> =
    emptyList()
) : ListAdapter<QuestionnaireItemViewItem, QuestionnaireItemViewHolder>(DiffCallback) {
  /**
   * @param viewType the integer value of the [QuestionnaireItemViewHolderType] used to render the
   * [QuestionnaireItemViewItem].
   */
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionnaireItemViewHolder {
    val numOfCanonicalWidgets = QuestionnaireItemViewHolderType.values().size
    check(viewType < numOfCanonicalWidgets + questionnaireItemViewHolderMatchers.size) {
      "Invalid widget type specified. Widget Int type cannot exceed the total number of supported custom and canonical widgets"
    }

    // Map custom widget viewTypes to their corresponding widget factories
    if (viewType >= numOfCanonicalWidgets)
      return questionnaireItemViewHolderMatchers[viewType - numOfCanonicalWidgets].factory.create(
        parent
      )

    val viewHolderFactory =
      when (QuestionnaireItemViewHolderType.fromInt(viewType)) {
        QuestionnaireItemViewHolderType.GROUP -> QuestionnaireItemGroupViewHolderFactory
        QuestionnaireItemViewHolderType.BOOLEAN_TYPE_PICKER ->
          QuestionnaireItemBooleanTypePickerViewHolderFactory
        QuestionnaireItemViewHolderType.DATE_PICKER -> QuestionnaireItemDatePickerViewHolderFactory
        QuestionnaireItemViewHolderType.DATE_TIME_PICKER ->
          QuestionnaireItemDateTimePickerViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE ->
          QuestionnaireItemEditTextSingleLineViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE ->
          QuestionnaireItemEditTextMultiLineViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER ->
          QuestionnaireItemEditTextIntegerViewHolderFactory
        QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL ->
          QuestionnaireItemEditTextDecimalViewHolderFactory
        QuestionnaireItemViewHolderType.RADIO_GROUP -> QuestionnaireItemRadioGroupViewHolderFactory
        QuestionnaireItemViewHolderType.DROP_DOWN -> QuestionnaireItemDropDownViewHolderFactory
        QuestionnaireItemViewHolderType.DISPLAY -> QuestionnaireItemDisplayViewHolderFactory
        QuestionnaireItemViewHolderType.QUANTITY ->
          QuestionnaireItemEditTextQuantityViewHolderFactory
        QuestionnaireItemViewHolderType.CHECK_BOX_GROUP ->
          QuestionnaireItemCheckBoxGroupViewHolderFactory
        QuestionnaireItemViewHolderType.AUTO_COMPLETE ->
          QuestionnaireItemAutoCompleteViewHolderFactory
        QuestionnaireItemViewHolderType.DIALOG_SELECT ->
          QuestionnaireItemDialogSelectViewHolderFactory
        QuestionnaireItemViewHolderType.SLIDER -> QuestionnaireItemSliderViewHolderFactory
      }
    return viewHolderFactory.create(parent)
  }

  override fun onBindViewHolder(holder: QuestionnaireItemViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  /**
   * Returns the integer value of the [QuestionnaireItemViewHolderType] that will be used to render
   * the [QuestionnaireItemViewItem]. This is determined by a combination of the data type of the
   * question and any additional Questionnaire Item UI Control Codes
   * (http://hl7.org/fhir/R4/valueset-questionnaire-item-control.html) used in the itemControl
   * extension (http://hl7.org/fhir/R4/extension-questionnaire-itemcontrol.html).
   */
  override fun getItemViewType(position: Int): Int {
    return getItemViewTypeMapping(getItem(position))
  }

  internal fun getItemViewTypeMapping(questionnaireItemViewItem: QuestionnaireItemViewItem): Int {
    val questionnaireItem = questionnaireItemViewItem.questionnaireItem
    // For custom widgets, generate an int value that's greater than any int assigned to the
    // canonical FHIR widgets
    questionnaireItemViewHolderMatchers.forEachIndexed { index, matcher ->
      if (matcher.matches(questionnaireItem)) {
        return index + QuestionnaireItemViewHolderType.values().size
      }
    }

    return when (val type = questionnaireItem.type) {
      QuestionnaireItemType.GROUP -> QuestionnaireItemViewHolderType.GROUP
      QuestionnaireItemType.BOOLEAN -> QuestionnaireItemViewHolderType.BOOLEAN_TYPE_PICKER
      QuestionnaireItemType.DATE -> QuestionnaireItemViewHolderType.DATE_PICKER
      QuestionnaireItemType.DATETIME -> QuestionnaireItemViewHolderType.DATE_TIME_PICKER
      QuestionnaireItemType.STRING -> QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE
      QuestionnaireItemType.TEXT -> QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE
      QuestionnaireItemType.INTEGER -> getIntegerViewHolderType(questionnaireItemViewItem)
      QuestionnaireItemType.DECIMAL -> QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL
      QuestionnaireItemType.CHOICE -> getChoiceViewHolderType(questionnaireItemViewItem)
      QuestionnaireItemType.DISPLAY -> QuestionnaireItemViewHolderType.DISPLAY
      QuestionnaireItemType.QUANTITY -> QuestionnaireItemViewHolderType.QUANTITY
      else -> throw NotImplementedError("Question type $type not supported.")
    }.value
  }

  private fun getChoiceViewHolderType(
    questionnaireItemViewItem: QuestionnaireItemViewItem
  ): QuestionnaireItemViewHolderType {
    val questionnaireItem = questionnaireItemViewItem.questionnaireItem

    // Use the view type that the client wants if they specified an itemControl
    return questionnaireItem.itemControl?.viewHolderType
    // Otherwise, choose a sensible UI element automatically
    ?: run {
        val numOptions = questionnaireItemViewItem.answerOption.size
        when {
          // Always use a dialog for questions with a large number of options
          numOptions >= MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DIALOG ->
            QuestionnaireItemViewHolderType.DIALOG_SELECT

          // Use a check box group if repeated answers are permitted
          questionnaireItem.repeats -> QuestionnaireItemViewHolderType.CHECK_BOX_GROUP

          // Use a dropdown if there are a medium number of options
          numOptions >= MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN ->
            QuestionnaireItemViewHolderType.DROP_DOWN

          // Use a radio group only if there are a small number of options
          else -> QuestionnaireItemViewHolderType.RADIO_GROUP
        }
      }
  }

  private fun getIntegerViewHolderType(
    questionnaireItemViewItem: QuestionnaireItemViewItem
  ): QuestionnaireItemViewHolderType {
    val questionnaireItem = questionnaireItemViewItem.questionnaireItem
    // Use the view type that the client wants if they specified an itemControl
    return questionnaireItem.itemControl?.viewHolderType
      ?: QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER
  }

  internal companion object {
    // Choice questions are rendered as dialogs if they have at least this many options
    const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DIALOG = 10

    // Choice questions are rendered as radio group if number of options less than this constant
    const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN = 4
  }
}

internal object DiffCallback : DiffUtil.ItemCallback<QuestionnaireItemViewItem>() {
  override fun areItemsTheSame(
    oldItem: QuestionnaireItemViewItem,
    newItem: QuestionnaireItemViewItem
  ) = oldItem.questionnaireItem.linkId == newItem.questionnaireItem.linkId

  override fun areContentsTheSame(
    oldItem: QuestionnaireItemViewItem,
    newItem: QuestionnaireItemViewItem
  ) =
    oldItem.questionnaireItem.equalsDeep(newItem.questionnaireItem) &&
      oldItem.questionnaireResponseItem.equalsDeep(newItem.questionnaireResponseItem)
}
