/*
 * Copyright 2022-2025 Google LLC
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

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.canonicalizeDatePattern
import com.google.android.fhir.datacapture.extensions.dateEntryFormatOrSystemDefault
import com.google.android.fhir.datacapture.extensions.dateType
import com.google.android.fhir.datacapture.extensions.format
import com.google.android.fhir.datacapture.extensions.getDateSeparator
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localDate
import com.google.android.fhir.datacapture.extensions.parseLocalDateOrNull
import com.google.android.fhir.datacapture.extensions.toLocalDate
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DateInput
import com.google.android.fhir.datacapture.views.compose.DateInputFormat
import com.google.android.fhir.datacapture.views.compose.DatePickerItem
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object DatePickerViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val dateEntryFormat =
          remember(questionnaireViewItem) {
            questionnaireViewItem.questionnaireItem.dateEntryFormatOrSystemDefault
          }
        val datePatternSeparator =
          remember(dateEntryFormat) { getDateSeparator(dateEntryFormat) ?: '/' }
        val canonicalizedDatePattern =
          remember(dateEntryFormat) { canonicalizeDatePattern(dateEntryFormat) }
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
        val questionnaireItemAnswerLocalDate =
          remember(questionnaireViewItem.answers) {
            questionnaireViewItem.answers.singleOrNull()?.valueDateType?.localDate
          }
        val questionnaireItemAnswerDateInMillis =
          remember(questionnaireItemAnswerLocalDate) {
            questionnaireItemAnswerLocalDate?.atStartOfDay(ZONE_ID_UTC)?.toInstant()?.toEpochMilli()
          }
        val initialSelectedDateInMillis =
          remember(questionnaireItemAnswerDateInMillis) {
            questionnaireItemAnswerDateInMillis ?: MaterialDatePicker.todayInUtcMilliseconds()
          }
        val draftAnswer =
          remember(questionnaireViewItem) { questionnaireViewItem.draftAnswer as? String }
        val dateInput =
          remember(dateInputFormat, questionnaireItemAnswerLocalDate, draftAnswer) {
            questionnaireItemAnswerLocalDate
              ?.format(dateInputFormat.patternWithoutDelimiters)
              ?.let { DateInput(it, questionnaireItemAnswerLocalDate) }
              ?: DateInput(display = draftAnswer ?: "", null)
          }

        val selectableDatesResult =
          remember(questionnaireViewItem) { getSelectableDates(questionnaireViewItem) }

        val selectableDates = remember(selectableDatesResult) { selectableDatesResult.getOrNull() }

        val prohibitInput = remember(selectableDatesResult) { selectableDatesResult.isFailure }

        val validationMessage =
          remember(draftAnswer, selectableDatesResult) {
            if (selectableDatesResult.isFailure) {
              selectableDatesResult.exceptionOrNull()?.localizedMessage
            } else {
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

          DatePickerItem(
            modifier = Modifier.fillMaxWidth(),
            initialSelectedDateMillis = initialSelectedDateInMillis,
            selectableDates = selectableDates,
            dateInputFormat = dateInputFormat,
            dateInput = dateInput,
            labelText = uiDatePatternText,
            helperText = validationMessage.takeIf { !it.isNullOrBlank() }
                ?: getRequiredOrOptionalText(questionnaireViewItem, context),
            isError = !validationMessage.isNullOrBlank(),
            enabled = !(questionnaireViewItem.questionnaireItem.readOnly || prohibitInput),
            parseStringToLocalDate = { str, pattern -> parseLocalDateOrNull(str, pattern) },
            onDateInputEntry = {
              val (display, date) = it
              if (date != null) {
                coroutineScope.launch {
                  setQuestionnaireItemViewItemAnswer(questionnaireViewItem, date)
                }
              } else {
                coroutineScope.launch {
                  parseDateOnTextChanged(
                    questionnaireViewItem,
                    display,
                    dateInputFormat.patternWithoutDelimiters,
                  )
                }
              }
            },
          )
        }
      }

      private fun getSelectableDates(
        questionnaireViewItem: QuestionnaireViewItem,
      ): Result<SelectableDates> {
        val min = (questionnaireViewItem.minAnswerValue as? DateType)?.value?.time
        val max = (questionnaireViewItem.maxAnswerValue as? DateType)?.value?.time

        return if (min != null && max != null && min > max) {
          Result.failure(IllegalArgumentException("minValue cannot be greater than maxValue"))
        } else {
          Result.success(selectableDates(min, max))
        }
      }

      /** Set the answer in the [QuestionnaireResponse]. */
      private suspend fun setQuestionnaireItemViewItemAnswer(
        questionnaireViewItem: QuestionnaireViewItem,
        localDate: LocalDate,
      ) =
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = localDate.dateType
          },
        )

      /**
       * Each time the user types in a character, parse the string and if it can be parsed into a
       * date, set the answer in the [QuestionnaireResponse], otherwise, set the draft answer.
       */
      private suspend fun parseDateOnTextChanged(
        questionnaireViewItem: QuestionnaireViewItem,
        dateToDisplay: String,
        pattern: String,
      ) {
        val localDate = parseLocalDateOrNull(dateToDisplay, pattern)
        if (localDate != null) {
          setQuestionnaireItemViewItemAnswer(questionnaireViewItem, localDate)
        } else {
          questionnaireViewItem.setDraftAnswer(dateToDisplay)
        }
      }
    }
}

internal const val TAG = "date-picker"
internal val ZONE_ID_UTC = ZoneId.of("UTC")

internal fun invalidDateErrorText(context: Context, formatPattern: String) =
  context.getString(
    R.string.date_format_validation_error_msg,
    // Use 'mm' for month instead of 'MM' to avoid confusion.
    // See https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.
    formatPattern.lowercase(),
    formatPattern.replace("dd", "31").replace("MM", "01").replace("yyyy", "2023"),
  )

@OptIn(ExperimentalMaterial3Api::class)
internal fun selectableDates(minDateMillis: Long?, maxDateMillis: Long?) =
  object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long) =
      (minDateMillis == null || utcTimeMillis >= minDateMillis) &&
        (maxDateMillis == null || utcTimeMillis <= maxDateMillis)

    private fun getYear(timeInMillis: Long) = timeInMillis.toLocalDate().year

    override fun isSelectableYear(year: Int): Boolean {
      return (minDateMillis == null || year >= getYear(minDateMillis)) &&
        (maxDateMillis == null || year <= getYear(maxDateMillis))
    }
  }
