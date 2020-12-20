package com.google.android.fhir.datacapture.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.google.android.fhir.datacapture.ViewPicker
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextViewHolder
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import org.hl7.fhir.r4.model.Questionnaire

object CustomViewPicker : ViewPicker {
    override fun pick(viewType: Int): QuestionnaireItemViewHolderFactory? {
        return when (viewType) {
            1000 -> CustomFactory
            else -> null
        }
    }

    override fun getType(questionnaireItem: Questionnaire.QuestionnaireItemComponent): Int? {
        if (questionnaireItem.type == Questionnaire.QuestionnaireItemType.DATE) {
            return 1000;
        }
        return null
    }

}

object CustomFactory : QuestionnaireItemViewHolderFactory {
    override fun create(parent: ViewGroup): QuestionnaireItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_layout, parent, false)
            return CustomQuestionnaireItemViewHolder(view)
    }
}

class CustomQuestionnaireItemViewHolder(
    itemView: View
) : QuestionnaireItemViewHolder(itemView) {
    val numberPicker = itemView.findViewById<NumberPicker>(R.id.number_picker)
    override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        numberPicker.minValue = 1
        numberPicker.maxValue = 100
    }
}