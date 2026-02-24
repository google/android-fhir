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
import androidx.compose.foundation.selection.selectableGroup
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
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ChoiceRadioButton
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object RadioGroupViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
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
        var selectedAnswerOption by
          remember(questionnaireViewItem) {
            mutableStateOf(
              enabledAnswerOptions.singleOrNull {
                questionnaireViewItem.isAnswerOptionSelected(it)
              },
            )
          }
        val onAnswerOptionChoiceChange:
          suspend (Questionnaire.QuestionnaireItemAnswerOptionComponent) -> Unit =
          { answerOption ->
            if (selectedAnswerOption != answerOption) {
              selectedAnswerOption = answerOption
              questionnaireViewItem.setAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = answerOption.value
                },
              )
            } else {
              // Deselect an answerOption
              selectedAnswerOption = null
              questionnaireViewItem.clearAnswer()
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
                modifier = Modifier.selectableGroup().fillMaxWidth(),
                horizontalArrangement =
                  Arrangement.spacedBy(dimensionResource(R.dimen.option_item_margin_horizontal)),
                verticalArrangement =
                  Arrangement.spacedBy(dimensionResource(R.dimen.option_item_margin_vertical)),
              ) {
                enabledAnswerOptions.forEach {
                  ChoiceRadioButton(
                    label = remember(it) { AnnotatedString(it.value.displayString(context)) },
                    selected = it == selectedAnswerOption,
                    enabled = !readOnly,
                    modifier = Modifier.weight(1f).testTag(RADIO_OPTION_TAG),
                    image = it.itemAnswerOptionImage(context),
                  ) {
                    coroutineScope.launch { onAnswerOptionChoiceChange(it) }
                  }
                }
              }
            }
            ChoiceOrientationTypes.VERTICAL -> {
              Column(
                modifier = Modifier.selectableGroup().fillMaxWidth(),
                verticalArrangement =
                  Arrangement.spacedBy(dimensionResource(R.dimen.option_item_margin_vertical)),
              ) {
                enabledAnswerOptions.forEach {
                  ChoiceRadioButton(
                    label = remember(it) { AnnotatedString(it.value.displayString(context)) },
                    selected = it == selectedAnswerOption,
                    enabled = !readOnly,
                    modifier = Modifier.fillMaxWidth().testTag(RADIO_OPTION_TAG),
                    image = it.itemAnswerOptionImage(context),
                  ) {
                    coroutineScope.launch { onAnswerOptionChoiceChange(it) }
                  }
                }
              }
            }
          }
        }
      }
    }
}

const val RADIO_OPTION_TAG = "radio_group_option"
