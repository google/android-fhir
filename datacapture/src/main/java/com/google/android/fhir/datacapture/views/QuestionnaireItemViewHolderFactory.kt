/*
 * Copyright 2021 Google LLC
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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.QuestionnaireItemAdapter
import com.google.android.fhir.datacapture.QuestionnaireItemViewHolderType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.contrib.views.QuestionnaireItemPhoneNumberViewHolderFactory
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.ValidationResult
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Factory for [QuestionnaireItemViewHolder].
 *
 * @param resId the layout resource for the view
 */
abstract class QuestionnaireItemViewHolderFactory(@LayoutRes val resId: Int) {
  internal open fun create(parent: View): QuestionnaireItemViewHolder {
    return QuestionnaireItemViewHolder(
      parent,//ViewProvider.getView(parent, resId),
      getQuestionnaireItemViewHolderDelegate()
    )
  }

   open lateinit var questionnaireItem : Questionnaire.QuestionnaireItemComponent
   open lateinit var itemAdapter : QuestionnaireItemAdapter
   open var newViewType: Int = 0

  internal open fun create(parent: View, viewType: Int, questionnaireItemAdapter: QuestionnaireItemAdapter): QuestionnaireItemViewHolder {
    newViewType = viewType + 1000
    itemAdapter = questionnaireItemAdapter

    val viewHolderFactory2 =
    when (QuestionnaireItemViewHolderType.fromInt(newViewType)) {
      QuestionnaireItemViewHolderType.GROUP -> QuestionnaireItemGroupViewHolderFactory
      QuestionnaireItemViewHolderType.BOOLEAN_TYPE_PICKER ->
        QuestionnaireItemBooleanTypePickerViewHolderFactory
      QuestionnaireItemViewHolderType.DATE_PICKER ->
        QuestionnaireItemDatePickerViewHolderFactory
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
      QuestionnaireItemViewHolderType.RADIO_GROUP ->
        QuestionnaireItemRadioGroupViewHolderFactory
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
    }


    val layout = LayoutInflater.from(parent.context).inflate(resId, parent as ViewGroup, false)
    val viewGroup = layout.findViewById<LinearLayout>(R.id.repeat_layout)

    viewGroup.addView(ViewProvider.getView(parent, viewHolderFactory2.resId), viewGroup.childCount - 1)

    // Add the - button
    if (parent.tag == "first") {
      // Add the + button
    }

    return QuestionnaireItemViewHolder(
    viewGroup,//ViewProvider.getView(parent, resId),
    getQuestionnaireItemViewHolderDelegate()
    )
  }


  /**
   * Returns a [QuestionnaireItemViewHolderDelegate] that handles the initialization of views and
   * binding of items in [RecyclerView].
   */
  abstract fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate
}

/**
 * The [RecyclerView.ViewHolder] for [QuestionnaireItemViewItem].
 *
 * This is used by [QuestionnaireItemAdapter] to initialize views and bind items in [RecyclerView].
 */
open class QuestionnaireItemViewHolder(
  itemView: View,
  private val delegate: QuestionnaireItemViewHolderDelegate
) : RecyclerView.ViewHolder(itemView) {
  init {
    delegate.init(itemView)
  }

  open fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem, position: Int) {
    delegate.questionnaireItemViewItem = questionnaireItemViewItem
    delegate.bind(questionnaireItemViewItem, position)
    delegate.setReadOnly(questionnaireItemViewItem.questionnaireItem.readOnly)
    delegate.displayValidationResult(delegate.getValidationResult(itemView.context))
  }
}

/**
 * Delegate for [QuestionnaireItemViewHolder].
 *
 * This interface provides an abstraction of the operations that need to be implemented for a type
 * of view in the questionnaire.
 *
 * There is a 1:1 relationship between this and [QuestionnaireItemViewHolder]. In other words, there
 * is a unique [QuestionnaireItemViewHolderDelegate] for each [QuestionnaireItemViewHolder]. This is
 * critical for the correctness of the recycler view.
 */
interface QuestionnaireItemViewHolderDelegate {

  var questionnaireItemViewItem: QuestionnaireItemViewItem

  /**
   * Initializes the view in [QuestionnaireItemViewHolder]. Any listeners to record user input
   * should be set in this function.
   */
  fun init(itemView: View)

  /** Binds a [QuestionnaireItemViewItem] to the view. */
  fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem, position: Int)

  /** Displays validation messages on the view. */
  fun displayValidationResult(validationResult: ValidationResult)

  /** Sets view read only if [isReadOnly] is true. */
  fun setReadOnly(isReadOnly: Boolean)

  /**
   * Runs validation to display the correct message and calls the
   * questionnaireResponseChangedCallback
   */
  fun onAnswerChanged(context: Context) {
    questionnaireItemViewItem.questionnaireResponseItemChangedCallback()
    displayValidationResult(getValidationResult(context))
  }

  val canHaveMultipleAnswers
    get() = questionnaireItemViewItem.questionnaireItem.repeats

  /** Run the [QuestionnaireResponseItemValidator.validate] function. */
  fun getValidationResult(context: Context): ValidationResult {
    return QuestionnaireResponseItemValidator.validate(
      questionnaireItemViewItem.questionnaireItem,
      questionnaireItemViewItem.questionnaireResponseItem,
      context
    )
  }
}
