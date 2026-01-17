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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.extensions.choiceOrientation
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.optionExclusive
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.ChoiceCheckbox
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object CheckBoxGroupViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val readOnly =
          remember(questionnaireViewItem) { questionnaireViewItem.questionnaireItem.readOnly }
        val choiceOrientationType =
          remember(questionnaireViewItem) {
            questionnaireViewItem.questionnaireItem.choiceOrientation
              ?: ChoiceOrientationTypes.VERTICAL
          }
        val enabledAnswerOptions =
          remember(questionnaireViewItem) { questionnaireViewItem.enabledAnswerOptions }
        var selectedAnswerOptions by
          remember(questionnaireViewItem) {
            mutableStateOf(
              enabledAnswerOptions
                .filter { questionnaireViewItem.isAnswerOptionSelected(it) }
                .toSet(),
            )
          }

        val onAnswerOptionCheckedChange:
          suspend (Questionnaire.QuestionnaireItemAnswerOptionComponent, Boolean) -> Unit =
          { answerOption, checked ->
            when {
              checked && answerOption.optionExclusive -> {
                // If this answer option has optionExclusive extension, deselect other options
                selectedAnswerOptions = setOf(answerOption)
                questionnaireViewItem.setAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = answerOption.value
                  },
                )
              }
              checked -> {
                // Deselect any optionExclusive answer options
                val exclusiveOptions = enabledAnswerOptions.filter { it.optionExclusive }.toSet()
                selectedAnswerOptions = (selectedAnswerOptions - exclusiveOptions) + answerOption

                // Add the answer
                val answers =
                  questionnaireViewItem.answers +
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = answerOption.value
                    }
                // Remove exclusive options from answers
                val newAnswers =
                  answers.filterNot { answer ->
                    exclusiveOptions.any { it.value.equalsDeep(answer.value) }
                  }
                questionnaireViewItem.setAnswer(*newAnswers.toTypedArray())
              }
              else -> {
                // Remove the answer
                selectedAnswerOptions = selectedAnswerOptions - answerOption
                questionnaireViewItem.removeAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = answerOption.value
                  },
                )
              }
            }
          }

        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(
                horizontal = dimensionResource(R.dimen.item_margin_horizontal),
                vertical = dimensionResource(R.dimen.item_margin_vertical),
              ),
        ) {
          Header(
            questionnaireViewItem,
            showRequiredOrOptionalText = true,
            displayValidationResult = true,
          )
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          when (choiceOrientationType) {
            ChoiceOrientationTypes.HORIZONTAL -> {
              FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                  Arrangement.spacedBy(dimensionResource(R.dimen.option_item_margin_horizontal)),
                verticalArrangement =
                  Arrangement.spacedBy(dimensionResource(R.dimen.option_item_margin_vertical)),
              ) {
                enabledAnswerOptions.forEach { answerOption ->
                  ChoiceCheckbox(
                    label =
                      remember(answerOption) {
                        AnnotatedString(answerOption.value.displayString(context))
                      },
                    checked = answerOption in selectedAnswerOptions,
                    enabled = !readOnly,
                    modifier = Modifier.weight(1f).testTag(CHECKBOX_OPTION_TAG),
                    image = answerOption.itemAnswerOptionImage(context),
                  ) {
                    coroutineScope.launch { onAnswerOptionCheckedChange(answerOption, it) }
                  }
                }
              }
            }
            ChoiceOrientationTypes.VERTICAL -> {
              Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement =
                  Arrangement.spacedBy(dimensionResource(R.dimen.option_item_margin_vertical)),
              ) {
                enabledAnswerOptions.forEach { answerOption ->
                  ChoiceCheckbox(
                    label =
                      remember(answerOption) {
                        AnnotatedString(answerOption.value.displayString(context))
                      },
                    checked = answerOption in selectedAnswerOptions,
                    enabled = !readOnly,
                    modifier = Modifier.fillMaxWidth().testTag(CHECKBOX_OPTION_TAG),
                    image = answerOption.itemAnswerOptionImage(context),
                  ) {
                    coroutineScope.launch { onAnswerOptionCheckedChange(answerOption, it) }
                  }
                }
              }
            }
          }
        }
      }
    }
}

internal const val CHECKBOX_OPTION_TAG = "checkbox_group_option"
