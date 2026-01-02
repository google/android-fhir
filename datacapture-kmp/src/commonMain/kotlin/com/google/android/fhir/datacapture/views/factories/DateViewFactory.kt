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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.android.fhir.datacapture.extensions.DateAnswerValue
import com.google.android.fhir.datacapture.extensions.FhirR4DateType
import com.google.android.fhir.datacapture.extensions.ZONE_ID_UTC
import com.google.android.fhir.datacapture.extensions.canonicalizeDatePattern
import com.google.android.fhir.datacapture.extensions.dateEntryFormatOrSystemDefault
import com.google.android.fhir.datacapture.extensions.format
import com.google.android.fhir.datacapture.extensions.getDateSeparator
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.parseLocalDateOrNull
import com.google.android.fhir.datacapture.extensions.toLocalDate
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.DateInput
import com.google.android.fhir.datacapture.views.compose.DateInputFormat
import com.google.android.fhir.datacapture.views.compose.DatePickerItem
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.getRequiredOrOptionalText
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalTime::class)
internal object DateViewFactory : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val isReadOnly =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
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
        (questionnaireViewItem.answers.singleOrNull()?.value?.asDate()?.value?.value
            as? FhirDate.Date)
          ?.date
      }

    val questionnaireItemAnswerDateInMillis =
      remember(questionnaireItemAnswerLocalDate) {
        questionnaireItemAnswerLocalDate?.atStartOfDayIn(ZONE_ID_UTC)?.toEpochMilliseconds()
      }
    val initialSelectedDateInMillis =
      remember(questionnaireItemAnswerDateInMillis) {
        questionnaireItemAnswerDateInMillis ?: Clock.System.now().toEpochMilliseconds()
      }
    val draftAnswer =
      remember(questionnaireViewItem.draftAnswer) { questionnaireViewItem.draftAnswer as? String }
    val dateInput =
      remember(dateInputFormat, questionnaireItemAnswerLocalDate, draftAnswer) {
        questionnaireItemAnswerLocalDate?.format(dateInputFormat.pattern)?.let {
          DateInput(it, questionnaireItemAnswerLocalDate)
        }
          ?: DateInput(display = draftAnswer ?: "", null)
      }

    val selectableDatesResult =
      remember(questionnaireViewItem) { getSelectableDates(questionnaireViewItem) }

    val selectableDates = remember(selectableDatesResult) { selectableDatesResult.getOrNull() }

    val prohibitInput = remember(selectableDatesResult) { selectableDatesResult.isFailure }

    val invalidDraftDateErrorString =
      stringResource(
        Res.string.date_format_validation_error_msg,
        // Use 'mm' for month instead of 'MM' to avoid confusion.
        // See https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat.
        canonicalizedDatePattern.lowercase(),
        canonicalizedDatePattern.replace("dd", "31").replace("MM", "01").replace("yyyy", "2023"),
      )
    val requiredTextNewLineStringResource = stringResource(Res.string.required_text_and_new_line)
    val itemValidationMessage =
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

    val validationMessage =
      remember(itemValidationMessage, selectableDatesResult) {
        if (selectableDatesResult.isFailure) {
          selectableDatesResult.exceptionOrNull()?.message
        } else {
          itemValidationMessage
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

      DatePickerItem(
        modifier = Modifier.fillMaxWidth(),
        initialSelectedDateMillis = initialSelectedDateInMillis,
        selectableDates = selectableDates,
        dateInputFormat = dateInputFormat,
        dateInput = dateInput,
        labelText = uiDatePatternText,
        helperText = validationMessage.takeIf { !it.isNullOrBlank() }
            ?: getRequiredOrOptionalText(questionnaireViewItem),
        isError = !validationMessage.isNullOrBlank(),
        enabled = !(isReadOnly || prohibitInput),
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
                dateInputFormat.pattern,
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
    val min =
      (questionnaireViewItem.minAnswerValue?.asDate()?.value?.value as? FhirDate.Date)
        ?.date
        ?.atStartOfDayIn(ZONE_ID_UTC)
        ?.toEpochMilliseconds()
    val max =
      (questionnaireViewItem.maxAnswerValue?.asDate()?.value?.value as? FhirDate.Date)
        ?.date
        ?.atStartOfDayIn(ZONE_ID_UTC)
        ?.toEpochMilliseconds()

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
      QuestionnaireResponse.Item.Answer(
        value = DateAnswerValue(value = FhirR4DateType(value = FhirDate.Date(date = localDate))),
      ),
    )

  /**
   * Each time the user types in a character, parse the string and if it can be parsed into a date,
   * set the answer in the [QuestionnaireResponse], otherwise, set the draft answer.
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

internal const val TAG = "date-picker"

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
