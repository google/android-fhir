package com.google.fhirengine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

class QuestionnaireViewModel(val questionnaire: Questionnaire) : ViewModel() {
    val map = mutableMapOf<String, QuestionnaireResponse.QuestionnaireResponseItemComponent>()

    val questionnaireResponse = QuestionnaireResponse()

    init {
        questionnaire.item.forEach({
            createQuestionnaireResponseItemComponent(it, questionnaireResponse.item)
        })
    }

    private fun createQuestionnaireResponseItemComponent(
            questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent,
            questionnaireResponseItemComponentList: MutableList<QuestionnaireResponse.QuestionnaireResponseItemComponent>) {
        when (questionnaireItemComponent.type) {
            Questionnaire.QuestionnaireItemType.BOOLEAN -> {
                val questionnaireResponseItemComponent =
                        QuestionnaireResponse.QuestionnaireResponseItemComponent(
                                StringType(questionnaireItemComponent.linkId))
                questionnaireResponseItemComponent.text = questionnaireItemComponent.text
                questionnaireResponseItemComponentList.add(questionnaireResponseItemComponent)
                map[questionnaireItemComponent.linkId] = questionnaireResponseItemComponent
            }
            Questionnaire.QuestionnaireItemType.STRING -> {
                val questionnaireResponseItemComponent =
                        QuestionnaireResponse.QuestionnaireResponseItemComponent(
                                StringType(questionnaireItemComponent.linkId))
                questionnaireResponseItemComponent.text = questionnaireItemComponent.text
                questionnaireResponseItemComponentList.add(questionnaireResponseItemComponent)
                map[questionnaireItemComponent.linkId] = questionnaireResponseItemComponent
            }
            Questionnaire.QuestionnaireItemType.GROUP -> {
                val questionnaireResponseItemComponent =
                        QuestionnaireResponse.QuestionnaireResponseItemComponent(
                                StringType(questionnaireItemComponent.linkId))
                questionnaireResponseItemComponent.text = questionnaireItemComponent.text
                questionnaireResponseItemComponent.item = mutableListOf()
                questionnaireItemComponent.item.forEach {
                    createQuestionnaireResponseItemComponent(it,
                            questionnaireResponseItemComponent.item)
                }
                questionnaireResponseItemComponentList.add(questionnaireResponseItemComponent)
                map[questionnaireItemComponent.linkId] = questionnaireResponseItemComponent
            }
        }
    }
}

class QuestionnaireViewModelFactory(
        private val questionnaire: Questionnaire
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionnaireViewModel::class.java)) {
            return QuestionnaireViewModel(questionnaire) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}