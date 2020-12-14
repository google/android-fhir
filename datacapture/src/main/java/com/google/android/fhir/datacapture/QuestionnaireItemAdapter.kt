package com.google.android.fhir.datacapture

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.google.android.fhir.datacapture.views.QuestionnaireItemCheckBoxViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent

class QuestionnaireItemAdapter(
    val viewModel: QuestionnaireViewModel
) : ListAdapter<QuestionnaireItemComponent, QuestionnaireItemViewHolder>(
    QuestionDiffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionnaireItemViewHolder {
        val viewHolder = when (WidgetType.fromInt(viewType)) {
            WidgetType.CHECK_BOX -> QuestionnaireItemCheckBoxViewHolderFactory
            WidgetType.GROUP -> QuestionnaireItemGroupViewHolderFactory
            WidgetType.EDIT_TEXT -> QuestionnaireItemEditTextViewHolderFactory
        }
        return viewHolder.create(parent, viewModel)
    }

    override fun onBindViewHolder(holder: QuestionnaireItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int) = when (val type = currentList[position].type) {
        Questionnaire.QuestionnaireItemType.BOOLEAN -> WidgetType.CHECK_BOX
        Questionnaire.QuestionnaireItemType.STRING -> WidgetType.EDIT_TEXT
        Questionnaire.QuestionnaireItemType.GROUP -> WidgetType.GROUP
        else -> throw NotImplementedError("Question type ${type} not supported.")
    }.value
}

private object QuestionDiffCallback : DiffUtil.ItemCallback<QuestionnaireItemComponent>() {
    override fun areItemsTheSame(
        oldItem: QuestionnaireItemComponent,
        newItem: QuestionnaireItemComponent
    ) = oldItem.linkId.contentEquals(newItem.linkId)

    override fun areContentsTheSame(
        oldItem: QuestionnaireItemComponent,
        newItem: QuestionnaireItemComponent
    ) = oldItem.equalsDeep(newItem)
}
