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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import com.google.fhir.model.r4.Element
import com.google.fhir.model.r4.Extension
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
    this.value.let {
      when (it) {
        is Questionnaire.Item.AnswerOption.Value.Reference -> it.value
        is Questionnaire.Item.AnswerOption.Value.Coding -> it.value
        is Questionnaire.Item.AnswerOption.Value.Date -> it.value
        is Questionnaire.Item.AnswerOption.Value.Integer -> it.value
        is Questionnaire.Item.AnswerOption.Value.String -> it.value
        is Questionnaire.Item.AnswerOption.Value.Time -> it.value
      }
    }

fun Questionnaire.Item.AnswerOption.toQuestionnaireResponseItemAnswer():
  QuestionnaireResponse.Item.Answer =
  QuestionnaireResponse.Item.Answer(
    value =
      value.let {
        when (it) {
          is Questionnaire.Item.AnswerOption.Value.Integer ->
            QuestionnaireResponse.Item.Answer.Value.Integer(
              value = it.value,
            )
          is Questionnaire.Item.AnswerOption.Value.Coding ->
            QuestionnaireResponse.Item.Answer.Value.Coding(
              value = it.value,
            )
          is Questionnaire.Item.AnswerOption.Value.Date ->
            QuestionnaireResponse.Item.Answer.Value.Date(
              value = it.value,
            )
          is Questionnaire.Item.AnswerOption.Value.Reference ->
            QuestionnaireResponse.Item.Answer.Value.Reference(
              value = it.value,
            )
          is Questionnaire.Item.AnswerOption.Value.String ->
            QuestionnaireResponse.Item.Answer.Value.String(
              value = it.value,
            )
          is Questionnaire.Item.AnswerOption.Value.Time ->
            QuestionnaireResponse.Item.Answer.Value.Time(
              value = it.value,
            )
        }
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
 * Returns what to display on the UI depending on the [Questionnaire.Item.AnswerOption]. Used to get
 * the display representation for item answer options.
 */
@Composable
fun Questionnaire.Item.AnswerOption.displayString(): String {
  val localizedDisplayString = this.elementValue.displayString
  return remember(this) { localizedDisplayString ?: "" }
}
