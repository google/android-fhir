/*
 * Copyright 2022-2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.datacapture.views.factories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.identifierString
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import com.google.android.fhir.datacapture.views.components.MultiAutoCompleteTextItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object AutoCompleteViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val canHaveMultipleAnswers =
          remember(questionnaireViewItem.questionnaireItem) {
            questionnaireViewItem.questionnaireItem.repeats
          }
        val enabledAnswerOptions =
          remember(questionnaireViewItem.enabledAnswerOptions) {
            questionnaireViewItem.enabledAnswerOptions.map {
              DropDownAnswerOption(
                answerId = it.value.identifierString(context),
                answerOptionString = it.value.displayString(context),
              )
            }
          }
        var selectedAnswerOptions by
          remember(questionnaireViewItem.answers) {
            mutableStateOf(
              questionnaireViewItem.answers.map {
                DropDownAnswerOption(
                  answerId = it.value.identifierString(context),
                  answerOptionString = it.value.displayString(context),
                )
              },
            )
          }
        val errorTextMessage =
          remember(questionnaireViewItem.validationResult) {
            (questionnaireViewItem.validationResult as? Invalid)
              ?.getSingleStringValidationMessage()
              ?.takeIf { it.isNotBlank() }
          }
        val isReadOnly =
          remember(questionnaireViewItem.questionnaireItem) {
            questionnaireViewItem.questionnaireItem.readOnly
          }

        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(
                horizontal = dimensionResource(R.dimen.item_margin_horizontal),
                vertical = dimensionResource(R.dimen.item_margin_vertical),
              ),
        ) {
          Header(questionnaireViewItem, showRequiredOrOptionalText = true)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          MultiAutoCompleteTextItem(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isReadOnly,
            supportingText = errorTextMessage,
            isError = errorTextMessage.isNullOrBlank().not(),
            options = enabledAnswerOptions,
            selectedOptions = selectedAnswerOptions,
            onNewOptionSelected = { answerOption ->
              selectedAnswerOptions =
                if (canHaveMultipleAnswers) {
                  if (answerOption in selectedAnswerOptions) {
                    selectedAnswerOptions
                  } else {
                    selectedAnswerOptions + answerOption
                  }
                } else {
                  listOf(answerOption)
                }

              val questionnaireResponseAnswer =
                questionnaireViewItem.enabledAnswerOptions
                  .first { it.value.identifierString(context) == answerOption.answerId }
                  .valueCoding
                  .let {
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = it
                    }
                  }

              val answerNotPresent =
                questionnaireViewItem.answers.none {
                  it.value.equalsDeep(questionnaireResponseAnswer.value)
                }
              if (answerNotPresent) {
                coroutineScope.launch {
                  if (canHaveMultipleAnswers) {
                    questionnaireViewItem.addAnswer(questionnaireResponseAnswer)
                  } else {
                    questionnaireViewItem.setAnswer(questionnaireResponseAnswer)
                  }
                }
              }
            },
            onOptionDeselected = { option ->
              selectedAnswerOptions = selectedAnswerOptions.filterNot { it == option }

              val answerOptionCoding =
                questionnaireViewItem.enabledAnswerOptions
                  .first { it.value.identifierString(context) == option.answerId }
                  .valueCoding
              coroutineScope.launch {
                if (canHaveMultipleAnswers) {
                  questionnaireViewItem.removeAnswer(
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = answerOptionCoding
                    },
                  )
                } else {
                  questionnaireViewItem.clearAnswer()
                }
              }
            },
          )
        }
      }
    }
}
