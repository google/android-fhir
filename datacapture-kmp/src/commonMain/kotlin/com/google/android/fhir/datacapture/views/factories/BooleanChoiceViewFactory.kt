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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.no
import android_fhir.datacapture_kmp.generated.resources.yes
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import com.google.android.fhir.datacapture.extensions.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.choiceOrientation
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ChoiceRadioButton
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal object BooleanChoiceViewFactory : QuestionnaireItemViewFactory {
  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val readOnly =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
    val choiceOrientationType =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.choiceOrientation ?: ChoiceOrientationTypes.VERTICAL
      }
    val currentAnswer =
      remember(questionnaireViewItem) {
        questionnaireViewItem.answers.singleOrNull()?.value?.asBoolean()?.value?.value
      }
    var selectedChoiceState by remember(currentAnswer) { mutableStateOf(currentAnswer) }

    val onChoiceSelection: suspend (Boolean) -> Unit = { selected ->
      if (selectedChoiceState != selected) {
        selectedChoiceState = selected
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.Item.Answer(
            value =
              QuestionnaireResponse.Item.Answer.Value.Boolean(
                value = FhirR4Boolean(value = selected),
              ),
          ),
        )
      } else {
        // clear selection
        selectedChoiceState = null
        questionnaireViewItem.clearAnswer()
      }
    }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
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
              Arrangement.spacedBy(QuestionnaireTheme.dimensions.optionItemMarginHorizontal),
            verticalArrangement =
              Arrangement.spacedBy(QuestionnaireTheme.dimensions.optionItemMarginVertical),
          ) {
            ChoiceRadioButton(
              label = AnnotatedString(stringResource(Res.string.yes)),
              selected = selectedChoiceState == true,
              enabled = !readOnly,
              modifier = Modifier.weight(1f).testTag(YES_CHOICE_RADIO_BUTTON_TAG),
            ) {
              coroutineScope.launch { onChoiceSelection(true) }
            }

            ChoiceRadioButton(
              label = AnnotatedString(stringResource(Res.string.no)),
              selected = selectedChoiceState == false,
              enabled = !readOnly,
              modifier = Modifier.weight(1f).testTag(NO_CHOICE_RADIO_BUTTON_TAG),
              onClick = { coroutineScope.launch { onChoiceSelection(false) } },
            )
          }
        }
        ChoiceOrientationTypes.VERTICAL -> {
          Column(
            modifier = Modifier.selectableGroup().fillMaxWidth(),
            verticalArrangement =
              Arrangement.spacedBy(QuestionnaireTheme.dimensions.optionItemMarginVertical),
          ) {
            ChoiceRadioButton(
              label = AnnotatedString(stringResource(Res.string.yes)),
              selected = selectedChoiceState == true,
              enabled = !readOnly,
              modifier = Modifier.fillMaxWidth().testTag(YES_CHOICE_RADIO_BUTTON_TAG),
            ) {
              coroutineScope.launch { onChoiceSelection(true) }
            }

            ChoiceRadioButton(
              label = AnnotatedString(stringResource(Res.string.no)),
              selected = selectedChoiceState == false,
              enabled = !readOnly,
              modifier = Modifier.fillMaxWidth().testTag(NO_CHOICE_RADIO_BUTTON_TAG),
              onClick = { coroutineScope.launch { onChoiceSelection(false) } },
            )
          }
        }
      }
    }
  }
}

const val YES_CHOICE_RADIO_BUTTON_TAG = "yes_radio_button"
const val NO_CHOICE_RADIO_BUTTON_TAG = "no_radio_button"
