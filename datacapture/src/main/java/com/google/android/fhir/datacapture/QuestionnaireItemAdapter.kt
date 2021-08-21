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
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDatePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDateTimePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDisplayViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemDropDownViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextDecimalViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextIntegerViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextMultiLineViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextQuantityViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextSingleLineViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemRadioGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import org.hl7.fhir.r4.model.Questionnaire
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
        QuestionnaireItemViewHolderType.CHECK_BOX -> QuestionnaireItemCheckBoxViewHolderFactory
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
    return getItemViewTypeMapping(getItem(position).questionnaireItem)
  }

  internal fun getItemViewTypeMapping(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent
  ): Int {
    // For custom widgets, generate an int value that's greater than any int assigned to the
    // canonical FHIR widgets
    questionnaireItemViewHolderMatchers.forEachIndexed { index, matcher ->
      if (matcher.matches(questionnaireItem)) {
        return index + QuestionnaireItemViewHolderType.values().size
      }
    }

    return when (val type = questionnaireItem.type) {
      QuestionnaireItemType.GROUP -> QuestionnaireItemViewHolderType.GROUP
      QuestionnaireItemType.BOOLEAN -> QuestionnaireItemViewHolderType.CHECK_BOX
      QuestionnaireItemType.DATE -> QuestionnaireItemViewHolderType.DATE_PICKER
      QuestionnaireItemType.DATETIME -> QuestionnaireItemViewHolderType.DATE_TIME_PICKER
      QuestionnaireItemType.STRING -> QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE
      QuestionnaireItemType.TEXT -> QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE
      QuestionnaireItemType.INTEGER -> QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER
      QuestionnaireItemType.DECIMAL -> QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL
      QuestionnaireItemType.CHOICE -> getChoiceViewHolderType(questionnaireItem)
      QuestionnaireItemType.DISPLAY -> QuestionnaireItemViewHolderType.DISPLAY
      QuestionnaireItemType.QUANTITY -> QuestionnaireItemViewHolderType.QUANTITY
      else -> throw NotImplementedError("Question type $type not supported.")
    }.value
  }

  private fun getChoiceViewHolderType(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent
  ): QuestionnaireItemViewHolderType {
    if (questionnaireItem.itemControl == ITEM_CONTROL_AUTO_COMPLETE) {
      return QuestionnaireItemViewHolderType.AUTO_COMPLETE
    } else if (questionnaireItem.itemControl == ITEM_CONTROL_CHECK_BOX || questionnaireItem.repeats
    ) {
      return QuestionnaireItemViewHolderType.CHECK_BOX_GROUP
    } else if (questionnaireItem.itemControl == ITEM_CONTROL_DROP_DOWN) {
      return QuestionnaireItemViewHolderType.DROP_DOWN
    } else if (questionnaireItem.itemControl == ITEM_CONTROL_RADIO_BUTTON) {
      return QuestionnaireItemViewHolderType.RADIO_GROUP
    } else if (questionnaireItem.answerOption.size >= MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN
    ) {
      return QuestionnaireItemViewHolderType.DROP_DOWN
    } else {
      return QuestionnaireItemViewHolderType.RADIO_GROUP
    }
  }

  internal companion object {
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
