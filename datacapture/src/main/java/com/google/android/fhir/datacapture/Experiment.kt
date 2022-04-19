package com.google.android.fhir.datacapture

import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 19-04-2022.
 */
object Experiment {

    lateinit var questionnaireResponse : QuestionnaireResponse
    lateinit var questionnaire: Questionnaire
}