package com.google.android.fhir.datacapture

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator.checkQuestionnaireResponse
import com.google.android.fhir.datacapture.views.QuestionnaireResponseItemViewItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal class QuestionnaireResponseDisplayViewModel(
  application: Application,
  state: SavedStateHandle
) : AndroidViewModel(application) {

  internal val questionnaire: Questionnaire

  init {
    questionnaire =
      when {
        state.contains(QuestionnaireResponseDisplayFragment.EXTRA_QUESTIONNAIRE_JSON_URI) -> {
          if (state.contains(QuestionnaireResponseDisplayFragment.EXTRA_QUESTIONNAIRE_JSON_STRING)
          ) {
            Log.w(
              TAG,
              "Both EXTRA_QUESTIONNAIRE_URI & EXTRA_JSON_ENCODED_QUESTIONNAIRE are provided. " +
                "EXTRA_QUESTIONNAIRE_URI takes precedence."
            )
          }
          val uri: Uri = state[QuestionnaireResponseDisplayFragment.EXTRA_QUESTIONNAIRE_JSON_URI]!!
          FhirContext.forR4()
            .newJsonParser()
            .parseResource(application.contentResolver.openInputStream(uri)) as
            Questionnaire
        }
        state.contains(QuestionnaireResponseDisplayFragment.EXTRA_QUESTIONNAIRE_JSON_STRING) -> {
          val questionnaireJson: String =
            state[QuestionnaireResponseDisplayFragment.EXTRA_QUESTIONNAIRE_JSON_STRING]!!
          FhirContext.forR4().newJsonParser().parseResource(questionnaireJson) as Questionnaire
        }
        else ->
          error("Neither EXTRA_QUESTIONNAIRE_URI nor EXTRA_JSON_ENCODED_QUESTIONNAIRE is supplied.")
      }
  }

  internal val questionnaireResponse: QuestionnaireResponse

  init {
    val questionnaireJsonResponseString: String? =
      state[QuestionnaireResponseDisplayFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING]
    if (questionnaireJsonResponseString != null) {
      questionnaireResponse =
        FhirContext.forR4().newJsonParser().parseResource(questionnaireJsonResponseString) as
          QuestionnaireResponse
      checkQuestionnaireResponse(questionnaire, questionnaireResponse)
    } else error("EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING string is not supplied.")
  }

  internal val questionnaireResponseItemViewItemList:
    Flow<List<QuestionnaireResponseItemViewItem>> =
      flow {
    emit(
      getQuestionnaireResponseItemViewItemList(
        questionnaireItemList = questionnaire.item,
        questionnaireResponseItemList = questionnaireResponse.item,
      )
    )
  }

  private fun getQuestionnaireResponseItemViewItemList(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    items: MutableList<QuestionnaireResponseItemViewItem> = mutableListOf()
  ): List<QuestionnaireResponseItemViewItem> {
    val questionnaireItemIterator = questionnaireItemList.iterator()
    val questionnaireResponseInputItemIterator = questionnaireResponseItemList.iterator()
    while (questionnaireResponseInputItemIterator.hasNext()) {
      val questionnaireResponseItem = questionnaireResponseInputItemIterator.next()
      var questionnaireItem: Questionnaire.QuestionnaireItemComponent?
      do {
        questionnaireItem = questionnaireItemIterator.next()
      } while (questionnaireItem!!.linkId != questionnaireResponseItem.linkId)

      getQuestionnaireResponseItemViewItem(questionnaireItem, questionnaireResponseItem, items)
    }
    return items
  }

  private fun getQuestionnaireResponseItemViewItem(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    items: MutableList<QuestionnaireResponseItemViewItem>
  ) {
    when (checkNotNull(questionnaireItem.type) { "Questionnaire item must have type" }) {
      Questionnaire.QuestionnaireItemType.DISPLAY, Questionnaire.QuestionnaireItemType.NULL -> Unit
      Questionnaire.QuestionnaireItemType.GROUP ->
        getQuestionnaireResponseItemViewItemList(
          questionnaireItem.item,
          questionnaireResponseItem.item,
          items
        )
      Questionnaire.QuestionnaireItemType.CHOICE -> {
        items.add(QuestionnaireResponseItemViewItem(questionnaireItem, questionnaireResponseItem))
        if (questionnaireResponseItem.answer.isNotEmpty())
          getQuestionnaireResponseItemViewItemList(
            questionnaireItem.item,
            questionnaireResponseItem.answer.first().item,
            items
          )
      }
      else -> {
        items.add(QuestionnaireResponseItemViewItem(questionnaireItem, questionnaireResponseItem))
      }
    }
  }

  private companion object {
    const val TAG = "QuestionnaireResponseVM"
  }
}
