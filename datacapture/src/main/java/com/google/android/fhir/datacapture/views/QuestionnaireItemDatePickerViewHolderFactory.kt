package com.google.android.fhir.datacapture.views

import android.app.Activity
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.fhir.datacapture.QuestionnaireViewModel
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.Questionnaire

object QuestionnaireItemDatePickerViewHolderFactory: QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup, viewModel: QuestionnaireViewModel): QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_date_picker_view, parent, false)
        return QuestionnaireItemDatePickerViewHolder(view, viewModel)
    }
}

private class QuestionnaireItemDatePickerViewHolder(itemView: View, viewModel: QuestionnaireViewModel) :
    QuestionnaireItemViewHolder(itemView, viewModel) {
    private val textView = itemView.findViewById<TextView>(R.id.text)
    private val input = itemView.findViewById<TextView>(R.id.input)
    private val button = itemView.findViewById<TextView>(R.id.button)

    override fun bind(questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent) {
        textView.text = questionnaireItemComponent.text
        button.setOnClickListener {
            // TODO: find a more robust way to do this.
            val context = itemView.context as AppCompatActivity
            DatePickerFragment().show(context.supportFragmentManager, DatePickerFragment.TAG)
        }
    }
}