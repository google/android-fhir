package com.google.android.fhir.datacapture.gallery

import android.view.View
import android.widget.NumberPicker
import com.google.android.fhir.datacapture.ViewPicker
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Created by Vincent Karuri on 11/05/2021
 */
object CustomViewPicker: ViewPicker {

    override fun pick(viewType: Int): QuestionnaireItemViewHolderFactory? {
        return when (viewType) {
            1000 -> CustomFactory
            else -> null
        }
    }

    override fun getType(questionnaireItem: Questionnaire.QuestionnaireItemComponent): Int? {
        return if (questionnaireItem.type == Questionnaire.QuestionnaireItemType.DATE) 1000 else null
    }
}

object CustomFactory: QuestionnaireItemViewHolderFactory(R.layout.custom_layout) {
    override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
            object : QuestionnaireItemViewHolderDelegate {
                private lateinit var numberPicker: NumberPicker

                override fun init(itemView: View) {
                    numberPicker = itemView.findViewById(R.id.number_picker)
                }

                override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
                    numberPicker.minValue = 1
                    numberPicker.maxValue = 100
                }
            }
}
