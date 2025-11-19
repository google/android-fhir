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

package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire

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
internal val List<Questionnaire.Item.AnswerOption>.initialSelected: List<Any>
  get() = this.filter { it.initialSelected?.value == true }.map { it.value }

//
// fun Questionnaire.Item.AnswerOption.itemAnswerOptionImage(
// ): Drawable? {
//  return (extension.singleOrNull { it.url == EXTENSION_ITEM_ANSWER_MEDIA }?.value as Attachment?)
//    ?.let {
//      if (!it.hasContentType() || !it.hasData()) {
//        return null
//      }
//      when (it.contentType) {
//        "image/jpeg",
//        "image/jpg",
//        "image/png", -> {
//          val bitmap = BitmapFactory.decodeByteArray(it.data, 0, it.data.size)
//          val imageSize =
//            context.resources.getDimensionPixelOffset(R.dimen.item_answer_media_image_size)
//          val drawable: Drawable = BitmapDrawable(context.resources, bitmap)
//          drawable.setBounds(0, 0, imageSize, imageSize)
//          drawable
//        }
//        else -> null
//      }
//    }
// }
