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

package com.google.android.fhir.datacapture.views

import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

object QuestionnaireItemSubmitButtonViewHolderFactory : QuestionnaireItemViewHolderFactory {
  override fun create(parent: ViewGroup): QuestionnaireItemViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.questionnaire_submit_button_view, parent, false)

    return QuestionnaireItemSubmitButtonViewHolder(view)
  }

}

class QuestionnaireItemSubmitButtonViewHolder(
  itemView: View
) : QuestionnaireItemViewHolder(itemView) {
  private val submitButton =  itemView.findViewById<Button>(R.id.submit)




  override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
    Log.d("subButton","inside bind");
  }

  override fun setOnClickOfSubmitButton(onButtonClickListener: View.OnClickListener) {
    super.setOnClickOfSubmitButton(onButtonClickListener)
    submitButton.setOnClickListener(onButtonClickListener)

  }

}
