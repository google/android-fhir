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

package com.google.android.fhir.datacapture.contrib.views.barcode

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.LiveBarcodeScanningFragment
import com.google.android.fhir.datacapture.extensions.localizedPrefixSpanned
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

object BarCodeReaderViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_bar_code_reader_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var textQuestion: TextView
      private lateinit var barcodeTextView: TextView
      private lateinit var reScanView: TextView
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

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
            context
          ) { _, result ->
            val barcode = result.getString(LiveBarcodeScanningFragment.RESULT_REQUEST_KEY)?.trim()

            val answer =
              barcode.let {
                if (it!!.isEmpty()) {
                  null
                } else {
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(StringType(it))
                }
              }

            if (answer == null) {
              questionnaireViewItem.clearAnswer()
            } else {
              questionnaireViewItem.setAnswer(answer)
            }

            setInitial(questionnaireViewItem.answers.singleOrNull(), reScanView)
          }
          LiveBarcodeScanningFragment()
            .show(
              context.supportFragmentManager,
              BarCodeReaderViewHolderFactory.javaClass.simpleName
            )
        }
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        if (!questionnaireViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        textQuestion.text = questionnaireViewItem.questionnaireItem.localizedTextSpanned
        setInitial(questionnaireViewItem.answers.singleOrNull(), reScanView)
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

      override fun setReadOnly(isReadOnly: Boolean) {}
    }

  const val WIDGET_EXTENSION = "https://fhir.labs.smartregister.org/barcode-type-widget-extension"
  const val WIDGET_TYPE = "barcode"
}
