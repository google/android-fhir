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

import android.graphics.drawable.Drawable
import android.text.Spanned
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.identifierString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
import com.google.android.fhir.datacapture.extensions.toSpanned
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.AutoCompleteDropDownItem
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object DropDownViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val hyphen = stringResource(R.string.hyphen)
        val isQuestionnaireItemReadOnly =
          remember(questionnaireViewItem.questionnaireItem) {
            questionnaireViewItem.questionnaireItem.readOnly
          }
        val flyOverText =
          remember(questionnaireViewItem.enabledDisplayItems) {
            questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString
          }
        val requiredOptionalText =
          remember(questionnaireViewItem) {
            getRequiredOrOptionalText(questionnaireViewItem, context)
          }
        val questionnaireItemAnswerDropDownOptions =
          remember(questionnaireViewItem.enabledAnswerOptions) {
            questionnaireViewItem.enabledAnswerOptions.map {
              DropDownAnswerOption(
                it.value.identifierString(context),
                it.value.displayString(context),
                it.itemAnswerOptionImage(context),
              )
            }
          }
        val validationErrorMessage =
          remember(questionnaireViewItem.validationResult) {
            getValidationErrorMessage(
              context,
              questionnaireViewItem,
              questionnaireViewItem.validationResult,
            )
              ?: ""
          }
        val showClearInput =
          remember(questionnaireViewItem.answers) { questionnaireViewItem.answers.isNotEmpty() }

        val dropDownOptions =
          remember(questionnaireItemAnswerDropDownOptions) {
            listOf(
              DropDownAnswerOption(hyphen, hyphen, null),
              *questionnaireItemAnswerDropDownOptions.toTypedArray(),
            )
          }
        val selectedAnswerIdentifier =
          remember(questionnaireViewItem.answers) {
            questionnaireViewItem.answers.singleOrNull()?.value?.identifierString(context)
          }
        val selectedOption =
          remember(dropDownOptions, selectedAnswerIdentifier) {
            questionnaireItemAnswerDropDownOptions.firstOrNull {
              it.answerId == selectedAnswerIdentifier
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
          Header(questionnaireViewItem)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          AutoCompleteDropDownItem(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isQuestionnaireItemReadOnly,
            labelText = flyOverText,
            supportingText = validationErrorMessage.ifBlank { requiredOptionalText },
            isError = validationErrorMessage.isNotBlank(),
            showClearIcon = showClearInput,
            selectedOption = selectedOption,
            options = dropDownOptions,
          ) { answerOption ->
            val selectedAnswer =
              questionnaireViewItem.enabledAnswerOptions
                .firstOrNull { it.value.identifierString(context) == answerOption?.answerId }
                ?.value

            coroutineScope.launch {
              if (selectedAnswer != null) {
                questionnaireViewItem.setAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                    .setValue(selectedAnswer),
                )
              } else {
                questionnaireViewItem.clearAnswer()
              }
            }
          }
        }
      }
    }
}

internal data class DropDownAnswerOption(
  val answerId: String,
  val answerOptionString: String,
  val answerOptionImage: Drawable? = null,
) {
  override fun toString(): String {
    return this.answerOptionString
  }

  fun answerOptionStringSpanned(): Spanned = answerOptionString.toSpanned()

  fun answerOptionAnnotatedString() = answerOptionString.toAnnotatedString()
}
