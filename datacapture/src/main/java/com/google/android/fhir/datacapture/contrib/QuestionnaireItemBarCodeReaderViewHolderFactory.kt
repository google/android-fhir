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

package com.google.android.fhir.datacapture.contrib

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentResultListener
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.LiveBarcodeScanningFragment
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.android.fhir.datacapture.views.tryUnwrapContext
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

object QuestionnaireItemBarCodeReaderViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_bar_code_reader_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var textQuestion: TextView
      private lateinit var barcodeTextView: TextView
      private lateinit var reScanView: TextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        textQuestion = itemView.findViewById(R.id.question)
        barcodeTextView = itemView.findViewById(R.id.textInputEditText)
        reScanView = itemView.findViewById(R.id.tv_rescan)
        itemView.findViewById<View>(R.id.textInputLayout).setOnClickListener {

          // The application is wrapped in a ContextThemeWrapper in QuestionnaireFragment
          // and again in TextInputEditText during layout inflation. As a result, it is
          // necessary to access the base context twice to retrieve the application object
          // from the view's context.
          val context = itemView.context.tryUnwrapContext()!!

          context.supportFragmentManager.setFragmentResultListener(
            LiveBarcodeScanningFragment.RESULT_REQUEST_KEY,
            context,
            object : FragmentResultListener {

              override fun onFragmentResult(requestKey: String, result: Bundle) {
                val barcode =
                  result.getString(LiveBarcodeScanningFragment.RESULT_REQUEST_KEY)?.trim()

                questionnaireItemViewItem.singleAnswerOrNull =
                  barcode.let {
                    if (it!!.isEmpty()) {
                      null
                    } else {
                      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                        .setValue(StringType(it))
                    }
                  }

                setInitial(questionnaireItemViewItem.singleAnswerOrNull, reScanView)

                questionnaireItemViewItem.questionnaireResponseItemChangedCallback()
              }
            }
          )
          LiveBarcodeScanningFragment()
            .show(
              context.supportFragmentManager,
              QuestionnaireItemBarCodeReaderViewHolderFactory.javaClass.simpleName
            )
        }
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }
        textQuestion.text = questionnaireItemViewItem.questionnaireItem.localizedText
        setInitial(questionnaireItemViewItem.singleAnswerOrNull, reScanView)
      }

      private fun setInitial(
        barcode: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?,
        reScanView: TextView
      ) {
        barcode?.let {
          it.valueStringType?.value?.toString()?.let { result ->
            barcodeTextView.text = result

            barcodeTextView.typeface = Typeface.create(barcodeTextView.typeface, Typeface.NORMAL)
            reScanView.visibility = View.VISIBLE
          }
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        /* at least for now, there is no validation needed in this widget because we are not using any selector or edit-text field */
      }
    }
}
