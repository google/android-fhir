/*
 * Copyright 2024-2026 Google LLC
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
import android_fhir.datacapture_kmp.generated.resources.required_text_and_new_line
import android_fhir.datacapture_kmp.generated.resources.time
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.DataCapture
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.TimeFieldItem
import com.google.android.fhir.datacapture.views.compose.getRequiredOrOptionalText
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Time
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalTime::class)
object TimeViewFactory : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val requiredTextNewLineStringResource = stringResource(Res.string.required_text_and_new_line)
    val localDateTimeFormatter = remember { DataCapture.getConfiguration().localDateTimeFormatter }
    val validationMessage =
      remember(questionnaireViewItem.validationResult) {
        val validationMessage =
          questionnaireViewItem.validationResult.getSingleStringValidationMessage()

        if (
          questionnaireViewItem.questionnaireItem.required?.value == true &&
            questionnaireViewItem.questionViewTextConfiguration.showRequiredText
        ) {
          requiredTextNewLineStringResource + validationMessage
        } else {
          validationMessage
        }
      }
    val readOnly =
      remember(questionnaireViewItem.questionnaireItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
    val questionnaireViewItemLocalTimeAnswer =
      remember(questionnaireViewItem) {
        questionnaireViewItem.answers.singleOrNull()?.value?.asTime()?.value?.value
      }
    val initialTimeForSelection =
      remember(questionnaireViewItemLocalTimeAnswer) {
        questionnaireViewItemLocalTimeAnswer
          ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
      }
    val questionnaireViewItemLocalTimeAnswerDisplay =
      remember(questionnaireViewItemLocalTimeAnswer) {
        questionnaireViewItemLocalTimeAnswer?.let { localDateTimeFormatter.localizedTimeString(it) }
      }

    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }

    Column(
      modifier =
        Modifier.padding(
          horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
          vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
        ),
    ) {
      Header(questionnaireViewItem)
      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }
      TimeFieldItem(
        modifier = Modifier.fillMaxWidth(),
        initialStartTime = initialTimeForSelection,
        timeSelectedDisplay = questionnaireViewItemLocalTimeAnswerDisplay,
        enabled = !readOnly,
        hint = stringResource(Res.string.time),
        supportingHelperText =
          if (!validationMessage.isNullOrBlank()) {
            validationMessage
          } else {
            getRequiredOrOptionalText(questionnaireViewItem)
          },
        isError = !validationMessage.isNullOrBlank(),
      ) {
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
      QuestionnaireResponse.Item.Answer(
        value = QuestionnaireResponse.Item.Answer.Value.Time(value = Time(value = localDateTime)),
      ),
    )
}
