package com.google.android.fhir.datacapture.views

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.fhir.datacapture.QuestionnaireViewModel
import org.hl7.fhir.r4.model.Questionnaire

interface QuestionnaireItemViewHolderFactory {
    fun create(parent: ViewGroup, viewModel: QuestionnaireViewModel): QuestionnaireItemViewHolder
}

abstract class QuestionnaireItemViewHolder(
    itemView: View, val viewModel: QuestionnaireViewModel
) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent)
}