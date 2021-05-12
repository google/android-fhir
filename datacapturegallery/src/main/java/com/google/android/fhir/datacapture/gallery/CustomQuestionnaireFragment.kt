package com.google.android.fhir.datacapture.gallery

import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Created by Vincent Karuri on 12/05/2021
 */
class CustomQuestionnaireFragment : QuestionnaireFragment() {
    override fun pick(viewType: Int): QuestionnaireItemViewHolderFactory? {
        return CustomViewPicker.pick(viewType)
    }

    override fun getType(questionnaireItem: Questionnaire.QuestionnaireItemComponent): Int? {
        return CustomViewPicker.getType(questionnaireItem)
    }
}