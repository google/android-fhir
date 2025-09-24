/*
 * Copyright 2024-2025 Google LLC
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
import androidx.compose.ui.res.stringResource
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.toLocalizedString
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.TimePickerItem
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.TimeType

object TimePickerViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val validationMessage =
          remember(questionnaireViewItem.validationResult) {
            getValidationErrorMessage(
              context,
              questionnaireViewItem,
              questionnaireViewItem.validationResult,
            )
          }
        val requiredOptionalText =
          remember(questionnaireViewItem) {
            getRequiredOrOptionalText(questionnaireViewItem, context)
          }
        val readOnly =
          remember(questionnaireViewItem.questionnaireItem) {
            questionnaireViewItem.questionnaireItem.readOnly
          }
        val questionnaireViewItemLocalTimeAnswer =
          remember(questionnaireViewItem.answers) {
            questionnaireViewItem.answers.singleOrNull()?.valueTimeType?.localTime
          }
        var questionnaireViewItemLocalTimeAnswerDisplay by
          remember(questionnaireViewItemLocalTimeAnswer) {
            mutableStateOf(questionnaireViewItemLocalTimeAnswer?.toLocalizedString(context))
          }
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }

        Column(
          modifier =
            Modifier.padding(
              horizontal = dimensionResource(R.dimen.item_margin_horizontal),
              vertical = dimensionResource(R.dimen.item_margin_vertical),
            ),
        ) {
          Header(questionnaireViewItem)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }
          TimePickerItem(
            modifier = Modifier.fillMaxWidth(),
            selectedTime = questionnaireViewItemLocalTimeAnswerDisplay,
            enabled = !readOnly,
            hint = stringResource(R.string.time),
            supportingHelperText =
              if (!validationMessage.isNullOrBlank()) validationMessage else requiredOptionalText,
            isError = !validationMessage.isNullOrBlank(),
          ) {
            questionnaireViewItemLocalTimeAnswerDisplay = it.toLocalizedString(context)
            coroutineScope.launch { setQuestionnaireItemViewItemAnswer(questionnaireViewItem, it) }
          }
        }
      }

      /** Set the answer in the [QuestionnaireResponse]. */
      private suspend fun setQuestionnaireItemViewItemAnswer(
        questionnaireViewItem: QuestionnaireViewItem,
        localDateTime: LocalTime,
      ) =
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
            .setValue(TimeType(localDateTime.format(DateTimeFormatter.ISO_TIME))),
        )
    }

  private val TimeType.localTime
    get() =
      LocalTime.of(
        hour,
        minute,
        second.toInt(),
      )
}
