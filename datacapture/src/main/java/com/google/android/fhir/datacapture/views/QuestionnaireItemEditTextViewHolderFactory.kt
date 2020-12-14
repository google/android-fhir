package com.google.android.fhir.datacapture.views

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.QuestionnaireViewModel
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.Questionnaire

object QuestionnaireItemEditTextViewHolderFactory : QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup,
        viewModel: QuestionnaireViewModel): QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_edit_text_view, parent, false)
        return QuestionnaireItemEditTextViewHolder(view, viewModel)
    }
}

private class QuestionnaireItemEditTextViewHolder(itemView: View,
    viewModel: QuestionnaireViewModel) :
    QuestionnaireItemViewHolder(itemView, viewModel) {
    private val textView = itemView.findViewById<TextView>(R.id.text)
    private val editText = itemView.findViewById<EditText>(R.id.input)

    override fun bind(questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent) {
        textView.text = questionnaireItemComponent.text
        editText.doAfterTextChanged { editable: Editable? ->
            viewModel.setAnswer(questionnaireItemComponent.linkId, editable.toString())
        }
    }
}