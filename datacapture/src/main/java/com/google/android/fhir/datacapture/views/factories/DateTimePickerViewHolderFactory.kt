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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
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
import com.google.android.fhir.datacapture.extensions.canonicalizeDatePattern
import com.google.android.fhir.datacapture.extensions.format
import com.google.android.fhir.datacapture.extensions.getDateSeparator
import com.google.android.fhir.datacapture.extensions.getLocalizedDatePattern
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localDateTime
import com.google.android.fhir.datacapture.extensions.parseLocalDateOrNull
import com.google.android.fhir.datacapture.extensions.toLocalizedString
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.DateInput
import com.google.android.fhir.datacapture.views.components.DateInputFormat
import com.google.android.fhir.datacapture.views.components.DatePickerItem
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import com.google.android.fhir.datacapture.views.components.TimePickerItem
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object DateTimePickerViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {

  @OptIn(ExperimentalMaterial3Api::class)
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val locale = Locale.getDefault()
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val itemReadOnly =
          remember(questionnaireViewItem.questionnaireItem) {
            questionnaireViewItem.questionnaireItem.readOnly
          }
        val localDatePattern = remember(locale) { getLocalizedDatePattern() }
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

        val requiredOrOptionalText =
          remember(questionnaireViewItem) {
            getRequiredOrOptionalText(questionnaireViewItem, context)
          }
        val questionnaireItemViewItemDateTimeAnswer =
          remember(questionnaireViewItem.answers) {
            questionnaireViewItem.answers.singleOrNull()?.valueDateTimeType?.localDateTime
          }
        val questionnaireItemViewItemDate =
          remember(questionnaireItemViewItemDateTimeAnswer) {
            questionnaireItemViewItemDateTimeAnswer?.toLocalDate()
          }
        val questionnaireViewItemLocalTime =
          remember(questionnaireItemViewItemDateTimeAnswer) {
            questionnaireItemViewItemDateTimeAnswer?.toLocalTime()
          }
        val questionnaireItemAnswerDateInMillis =
          remember(questionnaireItemViewItemDateTimeAnswer) {
            questionnaireItemViewItemDateTimeAnswer
              ?.toLocalDate()
              ?.atStartOfDay(ZONE_ID_UTC)
              ?.toInstant()
              ?.toEpochMilli()
          }
        val initialSelectedDateInMillis =
          remember(questionnaireItemAnswerDateInMillis) {
            questionnaireItemAnswerDateInMillis ?: MaterialDatePicker.todayInUtcMilliseconds()
          }
        val draftAnswer =
          remember(questionnaireViewItem.draftAnswer) {
            questionnaireViewItem.draftAnswer as? String
          }
        val dateInput =
          remember(dateInputFormat, questionnaireItemViewItemDate, draftAnswer) {
            questionnaireItemViewItemDate?.format(dateInputFormat.pattern)?.let {
              DateInput(it, questionnaireItemViewItemDate)
            }
              ?: DateInput(display = draftAnswer ?: "", null)
          }

        val questionnaireViewItemLocalTimeAnswerDisplay =
          remember(questionnaireViewItemLocalTime) {
            questionnaireViewItemLocalTime?.toLocalizedString(context) ?: ""
          }
        val initialTimeSelection =
          remember(questionnaireViewItemLocalTime) {
            questionnaireViewItemLocalTime ?: LocalTime.now()
          }
        var timeInputEnabled by
          remember(questionnaireItemViewItemDate) {
            mutableStateOf(!itemReadOnly && questionnaireItemViewItemDate != null)
          }

        val selectableDates = remember { object : SelectableDates {} }
        val dateValidationMessage =
          remember(draftAnswer, questionnaireItemViewItemDateTimeAnswer) {
            // If the draft answer is set, this means the user has yet to type a parseable answer,
            // so we display an error.
            getValidationErrorMessage(
              context,
              questionnaireViewItem,
              if (!draftAnswer.isNullOrEmpty()) {
                Invalid(
                  listOf(invalidDateErrorText(context, canonicalizedDatePattern)),
                )
              } else {
                questionnaireViewItem.validationResult
              },
            )
          }

        Column(
          modifier =
            Modifier.padding(
              horizontal = dimensionResource(R.dimen.item_margin_horizontal),
              vertical = dimensionResource(R.dimen.item_margin_vertical),
            ),
        ) {
          Header(questionnaireViewItem)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          Row(modifier = Modifier.fillMaxWidth()) {
            DatePickerItem(
              modifier = Modifier.weight(1f),
              initialSelectedDateMillis = initialSelectedDateInMillis,
              selectableDates = selectableDates,
              dateInputFormat = dateInputFormat,
              dateInput = dateInput,
              labelText = uiDatePatternText,
              helperText = dateValidationMessage.takeIf { !it.isNullOrBlank() }
                  ?: requiredOrOptionalText,
              isError = !dateValidationMessage.isNullOrBlank(),
              enabled = !itemReadOnly,
              parseStringToLocalDate = { str, pattern -> parseLocalDateOrNull(str, pattern) },
              onDateInputEntry = {
                val (display, date) = it
                coroutineScope.launch {
                  if (date != null) {
                    val dateTime =
                      LocalDateTime.of(
                        date,
                        LocalTime.of(0, 0),
                      )
                    setQuestionnaireItemViewItemAnswer(questionnaireViewItem, dateTime)
                  } else {
                    questionnaireViewItem.setDraftAnswer(display)
                  }
                }

                timeInputEnabled = date != null
              },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.date_picker_and_time_picker_gap)))

            TimePickerItem(
              modifier = Modifier.weight(0.6f),
              initialStartTime = initialTimeSelection,
              timeSelectedDisplay = questionnaireViewItemLocalTimeAnswerDisplay,
              enabled = timeInputEnabled,
              hint = stringResource(R.string.time),
              supportingHelperText = "",
              isError = false,
            ) {
              coroutineScope.launch {
                val dateTime =
                  LocalDateTime.of(
                    questionnaireItemViewItemDate,
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
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
            .setValue(
              DateTimeType(
                Date(
                  localDateTime.year - 1900,
                  localDateTime.monthValue - 1,
                  localDateTime.dayOfMonth,
                  localDateTime.hour,
                  localDateTime.minute,
                  localDateTime.second,
                ),
              ),
            ),
        )
    }
}
