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

package com.google.android.fhir.datacapture.gallery

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import kotlinx.coroutines.flow.merge

class QuestionnaireResponseDialogFragment() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val questionnaireResponseContent =
            requireArguments().getString(BUNDLE_KEY_QUESTIONNAIRE_RESPONSE)
        return activity?.let {
            val view = requireActivity().layoutInflater.inflate(
                R.layout.questionnaire_response_dialog_contents,
                null
            )
            view.findViewById<TextView>(R.id.contents).text = questionnaireResponseContent

            AlertDialog.Builder(it)
                .setView(view)
                .setPositiveButton("Extract") { dialog, which ->
                    val questionnaireResponseBuilder = QuestionnaireResponse.newBuilder()
                    val questionnaireResponse = JsonFormat
                        .getParser()
                        .merge(questionnaireResponseContent, questionnaireResponseBuilder)
                        .build()

                    val questionnaireContent =
                        requireArguments().getString(BUNDLE_KEY_QUESTIONNAIRE)
                    val questionnaireBuilder = Questionnaire.newBuilder()
                    val questionnaire = JsonFormat
                        .getParser()
                        .merge(questionnaireContent, questionnaireBuilder)
                        .build()

                    val resource = ResourceMapper.extract(questionnaire, questionnaireResponse)
                    val json = JsonFormat.getPrinter().print(resource)
                    val dialogFragment = QuestionnaireResponseExtractionDialogFragment()
                    dialogFragment.arguments = bundleOf(
                        QuestionnaireResponseExtractionDialogFragment.BUNDLE_KEY_RESULT to json,
                    )

                    dialogFragment.show(
                        this.requireActivity().supportFragmentManager,
                        QuestionnaireResponseDialogFragment.TAG
                    )
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        const val TAG = "questionnaire-response-dialog-fragment"
        const val BUNDLE_KEY_QUESTIONNAIRE = "questionnaire"
        const val BUNDLE_KEY_QUESTIONNAIRE_RESPONSE = "questionnaire-response"
    }
}
