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
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal class QuestionnaireCancelDialogFragment : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    isCancelable = false
    return MaterialAlertDialogBuilder(requireContext())
      .setView(onCreateCustomView())
      .setPositiveButton(R.string.questionnaire_yes_button_text) { dialog, _ ->
        setFragmentResult(RESULT_CALLBACK, bundleOf(RESULT_KEY to RESULT_VALUE_YES))
        dialog?.dismiss()
      }
      .setNegativeButton(R.string.questionnaire_no_button_text) { dialog, _ ->
        setFragmentResult(RESULT_CALLBACK, bundleOf(RESULT_KEY to RESULT_VALUE_NO))
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
        findViewById<TextView>(R.id.dialog_title).apply { visibility = View.GONE }
        findViewById<TextView>(R.id.dialog_subtitle).apply { visibility = View.GONE }
        findViewById<TextView>(R.id.body).apply {
          text = context.getString(R.string.questionnaire_cancel_text)
        }
      }
  }

  companion object {
    const val TAG = "QuestionnaireCancelDialogFragment"
    const val RESULT_CALLBACK = "QuestionnaireCancelDialogFragmentCallback"
    const val RESULT_KEY = "result"
    const val RESULT_VALUE_NO = "result_value_no"
    const val RESULT_VALUE_YES = "result_value_yes"
  }
}
