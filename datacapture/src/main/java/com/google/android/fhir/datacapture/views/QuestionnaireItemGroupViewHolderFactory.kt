package com.google.android.fhir.datacapture.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.fhir.datacapture.QuestionnaireViewModel
import com.google.android.fhir.datacapture.R
import org.hl7.fhir.r4.model.Questionnaire

object QuestionnaireItemGroupViewHolderFactory : QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup,
        viewModel: QuestionnaireViewModel): QuestionnaireItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.questionnaire_item_group_header_view, parent, false)
        return QuestionnaireItemGroupViewHolder(view, viewModel)
    }
}

private class QuestionnaireItemGroupViewHolder(itemView: View,
    viewModel: QuestionnaireViewModel) : QuestionnaireItemViewHolder(itemView, viewModel) {
    private val groupHeader = itemView.findViewById<TextView>(R.id.group_header)

    override fun bind(questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent) {
        groupHeader.text = questionnaireItemComponent.text
    }
}