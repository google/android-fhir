package com.google.android.fhir.datacapture.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.google.android.fhir.datacapture.QuestionnaireViewModel
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.Questionnaire

object QuestionnaireItemCheckBoxViewHolderFactory: QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup, viewModel: QuestionnaireViewModel): QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_check_box_view, parent, false)
        return QuestionnaireItemCheckBoxViewHolder(view, viewModel)
    }
}

private class QuestionnaireItemCheckBoxViewHolder(itemView: View, viewModel: QuestionnaireViewModel) :
    QuestionnaireItemViewHolder(itemView, viewModel) {
    private val checkBox = itemView.findViewById<CheckBox>(R.id.check_box)

    override fun bind(questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent) {
        checkBox.text = questionnaireItemComponent.text
        checkBox.setOnClickListener {
            viewModel.recordAnswer(questionnaireItemComponent.linkId, checkBox.isChecked)
        }
    }
}