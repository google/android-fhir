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
import android_fhir.datacapture_kmp.generated.resources.date_format_validation_error_msg
import android_fhir.datacapture_kmp.generated.resources.required_text_and_new_line
import android_fhir.datacapture_kmp.generated.resources.time
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.DataCapture
import com.google.android.fhir.datacapture.extensions.DateTimeAnswerValue
import com.google.android.fhir.datacapture.extensions.canonicalizeDatePattern
import com.google.android.fhir.datacapture.extensions.getDateSeparator
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.parseLocalDateOrNull
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DateFieldItem
import com.google.android.fhir.datacapture.views.compose.DateInput
import com.google.android.fhir.datacapture.views.compose.DateInputFormat
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.TimeFieldItem
import com.google.android.fhir.datacapture.views.compose.getRequiredOrOptionalText
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalTime::class)
internal object DateTimeViewFactory : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val localDateTimeFormatter = remember { DataCapture.getConfiguration().localDateTimeFormatter }
    val itemReadOnly =
      remember(questionnaireViewItem.questionnaireItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
    val localDatePattern = remember { localDateTimeFormatter.localDateShortFormatPattern }
    val datePatternSeparator =
      remember(localDatePattern) { getDateSeparator(localDatePattern) ?: '/' }
    val canonicalizedDatePattern =
      remember(localDatePattern) { canonicalizeDatePattern(localDatePattern) }
    val uiDatePatternText =
      remember(canonicalizedDatePattern) {
        // Use 'mm' for month instead of 'MM' to avoid confusion.
        // See https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.
        canonicalizedDatePattern.lowercase()
      }
    val dateInputFormat =
      remember(canonicalizedDatePattern, datePatternSeparator) {
        DateInputFormat(
          canonicalizedDatePattern,
          datePatternSeparator,
        )
      }

    var questionnaireItemViewItemAnswerLocalDateTime by
      remember(questionnaireViewItem) {
        mutableStateOf(
          (questionnaireViewItem.answers.singleOrNull()?.value?.asDateTime()?.value?.value
              as? FhirDateTime.DateTime)
            ?.dateTime,
        )
      }

    val questionnaireItemViewItemDate =
      remember(questionnaireItemViewItemAnswerLocalDateTime) {
        questionnaireItemViewItemAnswerLocalDateTime?.date
      }
    val questionnaireViewItemLocalTime =
      remember(questionnaireItemViewItemAnswerLocalDateTime) {
        questionnaireItemViewItemAnswerLocalDateTime?.time
      }
    val questionnaireItemAnswerDateInMillis =
      remember(questionnaireItemViewItemDate) {
        questionnaireItemViewItemDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
      }
    val initialSelectedDateInMillis =
      remember(questionnaireItemAnswerDateInMillis) {
        questionnaireItemAnswerDateInMillis ?: Clock.System.now().toEpochMilliseconds()
      }
    val draftAnswer =
      remember(questionnaireViewItem.draftAnswer) { questionnaireViewItem.draftAnswer as? String }
    val dateInput =
      remember(dateInputFormat, questionnaireItemViewItemDate, draftAnswer) {
        questionnaireItemViewItemDate
          ?.let { localDateTimeFormatter.format(it, dateInputFormat.pattern) }
          ?.let { DateInput(it, questionnaireItemViewItemDate) }
          ?: DateInput(display = draftAnswer ?: "", null)
      }

    val questionnaireViewItemLocalTimeAnswerDisplay =
      remember(questionnaireViewItemLocalTime) {
        questionnaireViewItemLocalTime?.let { localDateTimeFormatter.localizedTimeString(it) } ?: ""
      }
    val initialTimeSelection =
      remember(questionnaireViewItemLocalTime) {
        questionnaireViewItemLocalTime
          ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
      }
    val timeInputEnabled =
      remember(questionnaireItemViewItemDate) {
        !itemReadOnly && questionnaireItemViewItemDate != null
      }

    val selectableDates = remember { object : SelectableDates {} }
    val invalidDraftDateErrorString =
      stringResource(
        Res.string.date_format_validation_error_msg,
        // Use 'mm' for month instead of 'MM' to avoid confusion.
        // See https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.
        canonicalizedDatePattern.lowercase(),
        canonicalizedDatePattern.replace("dd", "31").replace("MM", "01").replace("yyyy", "2023"),
      )
    val requiredTextNewLineStringResource = stringResource(Res.string.required_text_and_new_line)
    val dateValidationMessage =
      remember(draftAnswer, questionnaireViewItem.validationResult) {
        val validationMessage =
          if (!draftAnswer.isNullOrBlank()) {
            // If the draft answer is set, this means the user has yet to type a parseable answer,
            // so we display an error.
            invalidDraftDateErrorString
          } else {
            questionnaireViewItem.validationResult.getSingleStringValidationMessage()
          }

        if (
          questionnaireViewItem.questionnaireItem.required?.value == true &&
            questionnaireViewItem.questionViewTextConfiguration.showRequiredText
        ) {
          requiredTextNewLineStringResource + validationMessage
        } else {
          validationMessage
        }
      }

    Column(
      modifier =
        Modifier.padding(
          horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
          vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
        ),
    ) {
      Header(questionnaireViewItem)
      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

      Row(modifier = Modifier.fillMaxWidth()) {
        DateFieldItem(
          modifier = Modifier.weight(1f),
          initialSelectedDateMillis = initialSelectedDateInMillis,
          selectableDates = selectableDates,
          dateInputFormat = dateInputFormat,
          dateInput = dateInput,
          labelText = uiDatePatternText,
          helperText = dateValidationMessage.takeIf { !it.isNullOrBlank() }
              ?: getRequiredOrOptionalText(questionnaireViewItem),
          isError = !dateValidationMessage.isNullOrBlank(),
          enabled = !itemReadOnly,
          parseStringToLocalDate = { str, pattern ->
            localDateTimeFormatter.parseLocalDateOrNull(str, pattern)
          },
          onDateInputEntry = {
            val (display, date) = it
            if (date != null) {
              val dateTime =
                LocalDateTime(
                  date,
                  LocalTime(0, 0),
                )
              coroutineScope.launch {
                setQuestionnaireItemViewItemAnswer(questionnaireViewItem, dateTime)
              }
              questionnaireItemViewItemAnswerLocalDateTime = dateTime
            } else {
              coroutineScope.launch { questionnaireViewItem.setDraftAnswer(display) }
            }
          },
        )

        Spacer(Modifier.width(QuestionnaireTheme.dimensions.datePickerAndTimePickerGap))

        TimeFieldItem(
          modifier = Modifier.weight(0.6f),
          initialStartTime = initialTimeSelection,
          timeSelectedDisplay = questionnaireViewItemLocalTimeAnswerDisplay,
          enabled = timeInputEnabled,
          hint = stringResource(Res.string.time),
          supportingHelperText = "",
          isError = false,
        ) {
          coroutineScope.launch {
            val dateTime =
              LocalDateTime(
                questionnaireItemViewItemDate!!,
                it,
              )
            setQuestionnaireItemViewItemAnswer(questionnaireViewItem, dateTime)
          }
        }
      }
    }
  }

  /** Set the answer in the [QuestionnaireResponse]. */
  private suspend fun setQuestionnaireItemViewItemAnswer(
    questionnaireViewItem: QuestionnaireViewItem,
    localDateTime: LocalDateTime,
  ) =
    questionnaireViewItem.setAnswer(
      QuestionnaireResponse.Item.Answer(
        value =
          DateTimeAnswerValue(
            value =
              DateTime(
                value =
                  FhirDateTime.DateTime(
                    localDateTime,
                    TimeZone.currentSystemDefault().offsetAt(Clock.System.now()),
                  ),
              ),
          ),
      ),
    )
}
