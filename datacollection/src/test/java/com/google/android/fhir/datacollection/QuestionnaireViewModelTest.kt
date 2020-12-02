package com.android.fhir.datacollection

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacollection.QuestionnaireViewModel
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for {@link FhirIndexerImpl}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireViewModelTest {
    @Test
    fun questionnaireResponse_shouldCopyQuestionnaireId() {
        val questionnaire = Questionnaire()
        questionnaire.id = "a-questionnaire"
        val viewModel = QuestionnaireViewModel(questionnaire)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply { id = "a-questionnaire" }
        )
    }

    @Test
    fun questionnaireResponse_booleanQuestion_shouldCopyQuestion() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Yes or no?"
        item.type = Questionnaire.QuestionnaireItemType.BOOLEAN
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                item.text = "Yes or no?"
                addItem(item)
            }
        )
    }

    @Test
    fun questionnaireResponse_booleanQuestion_setAnswer_shouldSetAnswer() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Yes or no?"
        item.type = Questionnaire.QuestionnaireItemType.BOOLEAN
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        viewModel.setAnswer("a-link-id", true)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                answer.value = BooleanType(true)
                item.text = "Yes or no?"
                item.answer = listOf(answer)
                addItem(item)
            }
        )
    }

    @Test
    fun questionnaireResponse_stringQuestion_shouldCopyQuestion() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Name?"
        item.type = Questionnaire.QuestionnaireItemType.STRING
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                item.text = "Name?"
                addItem(item)
            }
        )
    }

    @Test
    fun questionnaireResponse_stringQuestion_setAnswer_shouldSetAnswer() {
        val questionnaire = Questionnaire()
        val item = Questionnaire.QuestionnaireItemComponent()
        item.linkId = "a-link-id"
        item.text = "Name?"
        item.type = Questionnaire.QuestionnaireItemType.STRING
        questionnaire.addItem(item)
        val viewModel = QuestionnaireViewModel(questionnaire)
        viewModel.setAnswer("a-link-id", "John")
        assertResourceEquals(
            viewModel.questionnaireResponse,
            QuestionnaireResponse().apply {
                val item = QuestionnaireResponse.QuestionnaireResponseItemComponent()
                item.linkId = "a-link-id"
                val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                answer.value = StringType("John")
                item.text = "Name?"
                item.answer = listOf(answer)
                addItem(item)
            }
        )
    }

    private companion object {
        val parser = FhirContext.forR4().newJsonParser()
        fun assertResourceEquals(r1: Resource, r2: Resource) {
            assertThat(parser.encodeResourceToString(r1))
                .isEqualTo(parser.encodeResourceToString(r2))
        }
    }
}