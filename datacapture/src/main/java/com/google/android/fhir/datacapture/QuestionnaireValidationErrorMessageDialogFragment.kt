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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.use
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.fhir.datacapture.extensions.flattened
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.hl7.fhir.r4.model.Questionnaire

/**
 * [Dialog] to highlight the required fields that need to be filled by the user before submitting
 * the [Questionnaire]. This is shown when user has pressed internal submit button in the
 * [QuestionnaireFragment] and there are some validation errors.
 */
internal class QuestionnaireValidationErrorMessageDialogFragment(
  /**
   * Factory helps with testing and should not be set to anything in the regular production flow.
   */
  private val factoryProducer: (() -> ViewModelProvider.Factory)? = null
) : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    isCancelable = false
    return MaterialAlertDialogBuilder(requireContext())
      .setView(onCreateCustomView())
      .setPositiveButton(R.string.questionnaire_validation_error_fix_button_text) { dialog, _ ->
        setFragmentResult(RESULT_CALLBACK, bundleOf(RESULT_KEY to RESULT_VALUE_FIX))
        dialog?.dismiss()
      }
      .setNegativeButton(R.string.questionnaire_validation_error_submit_button_text) { dialog, _ ->
        setFragmentResult(RESULT_CALLBACK, bundleOf(RESULT_KEY to RESULT_VALUE_SUBMIT))
        dialog?.dismiss()
      }
      .create()
  }

  @VisibleForTesting
  fun onCreateCustomView(layoutInflater: LayoutInflater = getLayoutInflater()): View {
    val themeId =
      layoutInflater.context.obtainStyledAttributes(R.styleable.QuestionnaireTheme).use {
        it.getResourceId(
          // Use the custom questionnaire theme if it is specified
          R.styleable.QuestionnaireTheme_questionnaire_theme,
          // Otherwise, use the default questionnaire theme
          R.style.Theme_Questionnaire
        )
      }

    return layoutInflater
      .cloneInContext(ContextThemeWrapper(layoutInflater.context, themeId))
      .inflate(R.layout.questionnaire_validation_dialog, null)
      .apply {
        findViewById<TextView>(R.id.body).apply {
          val viewModel: QuestionnaireValidationErrorViewModel by
            activityViewModels(factoryProducer = factoryProducer)
          text =
            viewModel.getItemsTextWithValidationErrors().joinToString(separator = "\n") {
              context.getString(R.string.questionnaire_validation_error_item_text_with_bullet, it)
            }
        }
      }
  }

  companion object {
    const val TAG = "QuestionnaireValidationErrorMessageDialogFragment"
    const val RESULT_CALLBACK = "QuestionnaireValidationResultCallback"
    const val RESULT_KEY = "result"
    const val RESULT_VALUE_FIX = "result_fix"
    const val RESULT_VALUE_SUBMIT = "result_submit"
  }
}

internal class QuestionnaireValidationErrorViewModel : ViewModel() {
  private var questionnaire: Questionnaire? = null
  private var validation: Map<String, List<ValidationResult>>? = null

  fun setQuestionnaireAndValidation(
    questionnaire: Questionnaire,
    validation: Map<String, List<ValidationResult>>
  ) {
    this.questionnaire = questionnaire
    this.validation = validation
  }

  /** @return Texts associated with the failing [Questionnaire.QuestionnaireItemComponent]s. */
  fun getItemsTextWithValidationErrors(): List<String> {
    val invalidFields =
      validation?.filterValues { it.filterIsInstance<Invalid>().isNotEmpty() } ?: emptyMap()
    return questionnaire
      ?.item
      ?.flattened()
      ?.filter { invalidFields.contains(it.linkId) }
      ?.map { if (it.text.isNullOrEmpty()) it.localizedFlyoverSpanned.toString() else it.text }
      ?: emptyList()
  }
}
