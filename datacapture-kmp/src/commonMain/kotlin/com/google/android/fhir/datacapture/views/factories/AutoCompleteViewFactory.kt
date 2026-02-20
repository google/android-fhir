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
import com.google.android.fhir.datacapture.extensions.elementValue
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.toQuestionnaireResponseItemAnswer
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DropDownAnswerOption
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.MultiAutoCompleteTextItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object AutoCompleteViewFactory : QuestionnaireItemViewFactory {
  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val canHaveMultipleAnswers =
      remember(questionnaireViewItem.questionnaireItem) {
        questionnaireViewItem.questionnaireItem.repeats?.value ?: false
      }
    val enabledAnswerOptions =
      questionnaireViewItem.enabledAnswerOptions.map { DropDownAnswerOption.of(it) }
    var selectedAnswerOptions by
      remember(questionnaireViewItem.answers, enabledAnswerOptions) {
        val answersElementSet = questionnaireViewItem.answers.map { it.elementValue }.toSet()
        mutableStateOf(
          enabledAnswerOptions.filter { it.elementValue in answersElementSet },
        )
      }
    val errorTextMessage =
      remember(questionnaireViewItem.validationResult) {
        when (val validationResult = questionnaireViewItem.validationResult) {
          is Invalid -> validationResult.singleStringValidationMessage.takeIf { it.isNotBlank() }
          else -> null
        }
      }
    val isReadOnly =
      remember(questionnaireViewItem.questionnaireItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
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
              .first { it.elementValue == answerOption.elementValue }
              .toQuestionnaireResponseItemAnswer()

          val answerNotPresent =
            questionnaireViewItem.answers.none {
              it.elementValue == questionnaireResponseAnswer.elementValue
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

          coroutineScope.launch {
            if (canHaveMultipleAnswers) {
              val deSelectedAnswerOption =
                questionnaireViewItem.enabledAnswerOptions.first {
                  it.elementValue == option.elementValue
                }

              questionnaireViewItem.removeAnswer(
                deSelectedAnswerOption.toQuestionnaireResponseItemAnswer(),
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
