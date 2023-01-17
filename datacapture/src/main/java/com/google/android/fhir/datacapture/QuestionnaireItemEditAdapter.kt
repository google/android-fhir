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

package com.google.android.fhir.datacapture

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.fhir.datacapture.contrib.views.QuestionnaireItemPhoneNumberViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemAttachmentViewHolderFactory
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

internal class QuestionnaireItemEditAdapter(
  private val questionnaireItemViewHolderMatchers:
    List<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher> =
    emptyList(),
) : ListAdapter<QuestionnaireAdapterItem, QuestionnaireItemViewHolder>(DiffCallbacks.ITEMS) {
  /**
   * @param viewType the integer value of the [QuestionnaireItemViewHolderType] used to render the
   * [QuestionnaireItemViewItem].
   */
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionnaireItemViewHolder {
    val typedViewType = ViewType.parse(viewType)
    val subtype = typedViewType.subtype
    return when (typedViewType.type) {
      ViewType.Type.QUESTION -> onCreateViewHolderQuestion(parent = parent, subtype = subtype)
    }
  }

  private fun onCreateViewHolderQuestion(
    parent: ViewGroup,
    subtype: Int,
  ): QuestionnaireItemViewHolder {
    val numOfCanonicalWidgets = QuestionnaireItemViewHolderType.values().size
    check(subtype < numOfCanonicalWidgets + questionnaireItemViewHolderMatchers.size) {
      "Invalid widget type specified. Widget Int type cannot exceed the total number of supported custom and canonical widgets"
    }

    // Map custom widget viewTypes to their corresponding widget factories
    if (subtype >= numOfCanonicalWidgets)
      return questionnaireItemViewHolderMatchers[subtype - numOfCanonicalWidgets]
        .factory.create(parent)

    val viewHolderFactory =
      when (QuestionnaireItemViewHolderType.fromInt(subtype)) {
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
        QuestionnaireItemViewHolderType.PHONE_NUMBER ->
          QuestionnaireItemPhoneNumberViewHolderFactory
        QuestionnaireItemViewHolderType.ATTACHMENT -> QuestionnaireItemAttachmentViewHolderFactory
      }
    return viewHolderFactory.create(parent)
  }

  override fun onBindViewHolder(holder: QuestionnaireItemViewHolder, position: Int) {
    when (val item = getItem(position)) {
      is QuestionnaireAdapterItem.Question -> {
        holder.bind(item.item)
      }
    }
  }

  override fun getItemViewType(position: Int): Int {
    // Because we have multiple Item subtypes, we will pack two ints into the item view type.

    // The first 8 bits will be represented by this type, which is unique for each Item subclass.
    val type: ViewType.Type
    // The last 24 bits will be represented by this subtype, which will further divide each Item
    // subclass into more view types.
    val subtype: Int
    when (val item = getItem(position)) {
      is QuestionnaireAdapterItem.Question -> {
        type = ViewType.Type.QUESTION
        subtype = getItemViewTypeForQuestion(item.item)
      }
    }
    return ViewType.from(type = type, subtype = subtype).viewType
  }

  /**
   * Utility to pack two types (a "type" and "subtype") into a single "viewType" int, for use with
   * [getItemViewType].
   *
   * [type] is contained in the first 8 bits of the int, and should be unique for each type of
   * [QuestionnaireAdapterItem].
   *
   * [subtype] is contained in the lower 24 bits of the int, and should be used to differentiate
   * between different items within the same [QuestionnaireAdapterItem] type.
   */
  @JvmInline
  internal value class ViewType(val viewType: Int) {
    val subtype: Int
      get() = viewType and 0xFFFFFF
    val type: Type
      get() = Type.values()[viewType shr 24]

    companion object {
      fun parse(viewType: Int): ViewType = ViewType(viewType)

      fun from(type: Type, subtype: Int): ViewType = ViewType((type.ordinal shl 24) or subtype)
    }

    enum class Type {
      QUESTION,
    }
  }

  /**
   * Returns the integer value of the [QuestionnaireItemViewHolderType] that will be used to render
   * the [QuestionnaireItemViewItem]. This is determined by a combination of the data type of the
   * question and any additional Questionnaire Item UI Control Codes
   * (http://hl7.org/fhir/R4/valueset-questionnaire-item-control.html) used in the itemControl
   * extension (http://hl7.org/fhir/R4/extension-questionnaire-itemcontrol.html).
   */
  internal fun getItemViewTypeForQuestion(
    questionnaireItemViewItem: QuestionnaireItemViewItem,
  ): Int {
    val questionnaireItem = questionnaireItemViewItem.questionnaireItem
    // For custom widgets, generate an int value that's greater than any int assigned to the
    // canonical FHIR widgets
    questionnaireItemViewHolderMatchers.forEachIndexed { index, matcher ->
      if (matcher.matches(questionnaireItem)) {
        return index + QuestionnaireItemViewHolderType.values().size
      }
    }

    if (questionnaireItemViewItem.answerOption.isNotEmpty()) {
      return getChoiceViewHolderType(questionnaireItemViewItem).value
    }

    return when (val type = questionnaireItem.type) {
      QuestionnaireItemType.GROUP -> QuestionnaireItemViewHolderType.GROUP
      QuestionnaireItemType.BOOLEAN -> QuestionnaireItemViewHolderType.BOOLEAN_TYPE_PICKER
      QuestionnaireItemType.DATE -> QuestionnaireItemViewHolderType.DATE_PICKER
      QuestionnaireItemType.DATETIME -> QuestionnaireItemViewHolderType.DATE_TIME_PICKER
      QuestionnaireItemType.STRING -> getStringViewHolderType(questionnaireItemViewItem)
      QuestionnaireItemType.TEXT -> QuestionnaireItemViewHolderType.EDIT_TEXT_MULTI_LINE
      QuestionnaireItemType.INTEGER -> getIntegerViewHolderType(questionnaireItemViewItem)
      QuestionnaireItemType.DECIMAL -> QuestionnaireItemViewHolderType.EDIT_TEXT_DECIMAL
      QuestionnaireItemType.CHOICE -> getChoiceViewHolderType(questionnaireItemViewItem)
      QuestionnaireItemType.DISPLAY -> QuestionnaireItemViewHolderType.DISPLAY
      QuestionnaireItemType.QUANTITY -> QuestionnaireItemViewHolderType.QUANTITY
      QuestionnaireItemType.REFERENCE -> getChoiceViewHolderType(questionnaireItemViewItem)
      QuestionnaireItemType.ATTACHMENT -> QuestionnaireItemViewHolderType.ATTACHMENT
      else -> throw NotImplementedError("Question type $type not supported.")
    }.value
  }

  private fun getChoiceViewHolderType(
    questionnaireItemViewItem: QuestionnaireItemViewItem,
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
    questionnaireItemViewItem: QuestionnaireItemViewItem,
  ): QuestionnaireItemViewHolderType {
    val questionnaireItem = questionnaireItemViewItem.questionnaireItem
    // Use the view type that the client wants if they specified an itemControl
    return questionnaireItem.itemControl?.viewHolderType
      ?: QuestionnaireItemViewHolderType.EDIT_TEXT_INTEGER
  }

  private fun getStringViewHolderType(
    questionnaireItemViewItem: QuestionnaireItemViewItem,
  ): QuestionnaireItemViewHolderType {
    val questionnaireItem = questionnaireItemViewItem.questionnaireItem
    // Use the view type that the client wants if they specified an itemControl
    return questionnaireItem.itemControl?.viewHolderType
      ?: QuestionnaireItemViewHolderType.EDIT_TEXT_SINGLE_LINE
  }

  internal companion object {
    // Choice questions are rendered as dialogs if they have at least this many options
    const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DIALOG = 10

    // Choice questions are rendered as radio group if number of options less than this constant
    const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN = 4
  }
}

internal object DiffCallbacks {
  val ITEMS =
    object : DiffUtil.ItemCallback<QuestionnaireAdapterItem>() {
      override fun areItemsTheSame(
        oldItem: QuestionnaireAdapterItem,
        newItem: QuestionnaireAdapterItem,
      ): Boolean =
        when (oldItem) {
          is QuestionnaireAdapterItem.Question -> {
            newItem is QuestionnaireAdapterItem.Question &&
              QUESTIONS.areItemsTheSame(oldItem, newItem)
          }
        }

      override fun areContentsTheSame(
        oldItem: QuestionnaireAdapterItem,
        newItem: QuestionnaireAdapterItem,
      ): Boolean =
        when (oldItem) {
          is QuestionnaireAdapterItem.Question -> {
            newItem is QuestionnaireAdapterItem.Question &&
              QUESTIONS.areContentsTheSame(oldItem, newItem)
          }
        }
    }

  val QUESTIONS =
    object : DiffUtil.ItemCallback<QuestionnaireAdapterItem.Question>() {
      /**
       * [QuestionnaireItemViewItem] is a transient object for the UI only. Whenever the user makes
       * any change via the UI, a new list of [QuestionnaireItemViewItem]s will be created, each
       * holding references to the underlying [QuestionnaireItem] and [QuestionnaireResponseItem],
       * both of which should be read-only, and the current answers. To help recycler view handle
       * update and/or animations, we consider two [QuestionnaireItemViewItem]s to be the same if
       * they have the same underlying [QuestionnaireItem] and [QuestionnaireResponseItem].
       */
      override fun areItemsTheSame(
        oldItem: QuestionnaireAdapterItem.Question,
        newItem: QuestionnaireAdapterItem.Question,
      ) = oldItem.item.hasTheSameItem(newItem.item)

      override fun areContentsTheSame(
        oldItem: QuestionnaireAdapterItem.Question,
        newItem: QuestionnaireAdapterItem.Question,
      ): Boolean {
        return oldItem.item.hasTheSameItem(newItem.item) &&
          oldItem.item.hasTheSameAnswer(newItem.item) &&
          oldItem.item.hasTheSameValidationResult(newItem.item)
      }
    }
}
