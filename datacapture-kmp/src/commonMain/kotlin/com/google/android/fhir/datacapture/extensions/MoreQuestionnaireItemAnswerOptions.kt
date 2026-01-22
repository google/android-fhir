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

package com.google.android.fhir.datacapture.extensions

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import com.google.android.fhir.datacapture.DataCapture
import com.google.fhir.model.r4.Element
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse

internal const val EXTENSION_OPTION_EXCLUSIVE_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-optionExclusive"
internal const val EXTENSION_ITEM_ANSWER_MEDIA =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemAnswerMedia"

/** Indicates that if this answerOption is selected, no other possible answers may be selected. */
internal val Questionnaire.Item.AnswerOption.optionExclusive: Boolean
  get() {
    val extension =
      this.extension.singleOrNull { it.url == EXTENSION_OPTION_EXCLUSIVE_URL } ?: return false
    val value = extension.value
    if (value is Extension.Value.Boolean) {
      return value.asBoolean()?.value?.value ?: false
    }
    return false
  }

/** Get the answer options values with `initialSelected` set to true */
internal val List<Questionnaire.Item.AnswerOption>.initialSelected
  get() = this.filter { it.initialSelected?.value == true }.map { it.value }

fun Questionnaire.Item.AnswerOption.itemAnswerOptionImage(): ImageBitmap? {
  return extension
    .singleOrNull { it.url == EXTENSION_ITEM_ANSWER_MEDIA }
    ?.value
    ?.asAttachment()
    ?.value
    ?.let {
      if (it.contentType?.value == null || it.data?.value == null) {
        return null
      }

      when (it.contentType?.value!!) {
        "image/jpeg",
        "image/jpg",
        "image/png", -> it.data?.data?.decodeToImageBitmap()
        else -> null
      }
    }
}

val Questionnaire.Item.AnswerOption.elementValue: Element
  get() =
    when (this.value) {
      is Questionnaire.Item.AnswerOption.Value.Reference ->
        (value as Questionnaire.Item.AnswerOption.Value.Reference).value
      is Questionnaire.Item.AnswerOption.Value.Coding ->
        (value as Questionnaire.Item.AnswerOption.Value.Coding).value
      is Questionnaire.Item.AnswerOption.Value.Date ->
        (value as Questionnaire.Item.AnswerOption.Value.Date).value
      is Questionnaire.Item.AnswerOption.Value.Integer ->
        (value as Questionnaire.Item.AnswerOption.Value.Integer).value
      is Questionnaire.Item.AnswerOption.Value.String ->
        (value as Questionnaire.Item.AnswerOption.Value.String).value
      is Questionnaire.Item.AnswerOption.Value.Time ->
        (value as Questionnaire.Item.AnswerOption.Value.Time).value
    }

fun Questionnaire.Item.AnswerOption.toQuestionnaireResponseItemAnswer():
  QuestionnaireResponse.Item.Answer =
  QuestionnaireResponse.Item.Answer(
    value =
      when (value) {
        is Questionnaire.Item.AnswerOption.Value.Integer ->
          IntegerAnswerValue(
            value = (value as Questionnaire.Item.AnswerOption.Value.Integer).value,
          )
        is Questionnaire.Item.AnswerOption.Value.Coding ->
          QuestionnaireResponse.Item.Answer.Value.Coding(
            value = (value as Questionnaire.Item.AnswerOption.Value.Coding).value,
          )
        is Questionnaire.Item.AnswerOption.Value.Date ->
          QuestionnaireResponse.Item.Answer.Value.Date(
            value = (value as Questionnaire.Item.AnswerOption.Value.Date).value,
          )
        is Questionnaire.Item.AnswerOption.Value.Reference ->
          QuestionnaireResponse.Item.Answer.Value.Reference(
            value = (value as Questionnaire.Item.AnswerOption.Value.Reference).value,
          )
        is Questionnaire.Item.AnswerOption.Value.String ->
          QuestionnaireResponse.Item.Answer.Value.String(
            value = (value as Questionnaire.Item.AnswerOption.Value.String).value,
          )
        is Questionnaire.Item.AnswerOption.Value.Time ->
          QuestionnaireResponse.Item.Answer.Value.Time(
            value = (value as Questionnaire.Item.AnswerOption.Value.Time).value,
          )
      },
  )

val Questionnaire.Item.AnswerOption.Value.id: String?
  get() =
    when (this) {
      is Questionnaire.Item.AnswerOption.Value.Reference -> value.id
      is Questionnaire.Item.AnswerOption.Value.Coding -> value.id
      is Questionnaire.Item.AnswerOption.Value.Date -> value.id
      is Questionnaire.Item.AnswerOption.Value.Integer -> value.id
      is Questionnaire.Item.AnswerOption.Value.String -> value.id
      is Questionnaire.Item.AnswerOption.Value.Time -> value.id
    }

/**
 * Returns what to display on the UI depending on the [QuestionnaireItemAnswerOptionValue]. Used to
 * get the display representation for item answer options.
 */
fun QuestionnaireItemAnswerOptionValue.displayString(): String = getDisplayString(this) ?: ""

private fun getDisplayString(type: QuestionnaireItemAnswerOptionValue): String? =
  when (type) {
    is Questionnaire.Item.AnswerOption.Value.Coding -> type.value.display?.getLocalizedText()
        ?: type.value.code?.value
    is Questionnaire.Item.AnswerOption.Value.Date -> {
      val localDateFormatter = DataCapture.getConfiguration().localDateTimeFormatter
      val localDate = (type.value.value as? FhirDate.Date)?.date
      localDate?.let { localDateFormatter.format(it) }
    }
    is Questionnaire.Item.AnswerOption.Value.Integer -> type.value.value?.toString()
    is Questionnaire.Item.AnswerOption.Value.Reference -> type.value.display?.value
        ?: type.value.reference?.value
    is Questionnaire.Item.AnswerOption.Value.String -> type.value.getLocalizedText()
    is Questionnaire.Item.AnswerOption.Value.Time -> {
      val localDateFormatter = DataCapture.getConfiguration().localDateTimeFormatter
      val localTime = type.value.value
      localTime?.let { localDateFormatter.localizedTimeString(it) }
    }
  }

typealias QuestionnaireItemAnswerOptionValue = Questionnaire.Item.AnswerOption.Value
