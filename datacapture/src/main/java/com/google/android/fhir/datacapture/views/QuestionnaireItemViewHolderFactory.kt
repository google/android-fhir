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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseItemValidator
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.common.annotations.VisibleForTesting

/**
 * Factory for [QuestionnaireItemViewHolder].
 *
 * @param resId the layout resource for the view
 */
abstract class QuestionnaireItemViewHolderFactory(@LayoutRes val resId: Int) {
  internal open fun create(parent: ViewGroup): QuestionnaireItemViewHolder {
    return QuestionnaireItemViewHolder(
      LayoutInflater.from(parent.context).inflate(resId, parent, false),
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
  @org.jetbrains.annotations.VisibleForTesting
  private val delegate: QuestionnaireItemViewHolderDelegate
) : RecyclerView.ViewHolder(itemView) {
  init {
    delegate.init(itemView)
  }

  open fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
    delegate.questionnaireItemViewItem = questionnaireItemViewItem
    delegate.bind(questionnaireItemViewItem)
    delegate.setReadOnly(questionnaireItemViewItem.questionnaireItem.readOnly)
    // Only validate the questionnaire item after the user has modified it or if there's any
    // existing answers.
    // This will prevent cluttering the UI with validation errors when the user opens an empty
    // questionnaire.
    if (delegate.questionnaireItemViewItem.modified ||
        delegate.questionnaireItemViewItem.questionnaireResponseItem.answer.size > 0
    ) {
      delegate.displayValidationResult(delegate.getValidationResult(itemView.context))
    } else {
      delegate.displayValidationResult(ValidationResult(true, listOf()))
    }
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
  fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem)

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
    questionnaireItemViewItem.modified = true
    displayValidationResult(getValidationResult(context))
  }

  /** Run the [QuestionnaireResponseItemValidator.validate] function. */
  fun getValidationResult(context: Context): ValidationResult {
    return QuestionnaireResponseItemValidator.validate(
      questionnaireItemViewItem.questionnaireItem,
      questionnaireItemViewItem.questionnaireResponseItem,
      context
    )
  }
}
