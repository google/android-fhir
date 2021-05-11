package com.google.android.fhir.datacapture

import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import org.hl7.fhir.r4.model.Questionnaire

interface ViewPicker {
    fun pick(viewType: Int): QuestionnaireItemViewHolderFactory?
    fun getType(questionnaireItem: Questionnaire.QuestionnaireItemComponent): Int?
}