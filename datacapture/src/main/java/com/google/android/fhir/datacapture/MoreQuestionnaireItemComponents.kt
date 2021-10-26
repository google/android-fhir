/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.util.Locale
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

internal enum class ItemControlTypes(
  val extensionCode: String,
  val viewHolderType: QuestionnaireItemViewHolderType,
) {
  AUTO_COMPLETE("autocomplete", QuestionnaireItemViewHolderType.AUTO_COMPLETE),
  CHECK_BOX("check-box", QuestionnaireItemViewHolderType.CHECK_BOX_GROUP),
  DROP_DOWN("drop-down", QuestionnaireItemViewHolderType.DROP_DOWN),
  OPEN_CHOICE("open-choice", QuestionnaireItemViewHolderType.DIALOG_SELECT),
  RADIO_BUTTON("radio-button", QuestionnaireItemViewHolderType.RADIO_GROUP),
}

internal const val EXTENSION_ITEM_CONTROL_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl"
internal const val EXTENSION_ITEM_CONTROL_SYSTEM = "http://hl7.org/fhir/questionnaire-item-control"
internal const val EXTENSION_HIDDEN_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-hidden"
internal const val EXTENSION_ITEM_IMAGE =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/cpg-itemImage"

// Item control code, or null
internal val Questionnaire.QuestionnaireItemComponent.itemControl: ItemControlTypes?
  get() {
    val codeableConcept =
      this.extension.firstOrNull { it.url == EXTENSION_ITEM_CONTROL_URL }?.value as CodeableConcept?
    val code =
      codeableConcept?.coding?.firstOrNull { it.system == EXTENSION_ITEM_CONTROL_SYSTEM }?.code
    return ItemControlTypes.values().firstOrNull { it.extensionCode == code }
  }

/**
 * Whether the corresponding [QuestionnaireResponse.QuestionnaireResponseItemComponent] should have
 * nested items within [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent](s).
 */
internal val Questionnaire.QuestionnaireItemComponent.hasNestedItemsWithinAnswers: Boolean
  get() = item.isNotEmpty() && type != Questionnaire.QuestionnaireItemType.GROUP

private fun StringType.getLocalizedText(
  lang: String = Locale.getDefault().toLanguageTag()
): String? {
  return getTranslation(lang) ?: getTranslation(lang.split("-").first()) ?: value
}

/**
 * Localized value of [Questionnaire.QuestionnaireItemComponent.text] if translation is present.
 * Default value otherwise.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedText: String?
  get() = textElement?.getLocalizedText()

/**
 * Localized value of [Questionnaire.QuestionnaireItemComponent.prefix] if translation is present.
 * Default value otherwise.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedPrefix: String?
  get() = prefixElement?.getLocalizedText()

/**
 * Whether the QuestionnaireItem should be hidden according to the hidden extension or lack thereof.
 */
internal val Questionnaire.QuestionnaireItemComponent.isHidden: Boolean
  get() {
    val extension = this.extension.singleOrNull { it.url == EXTENSION_HIDDEN_URL } ?: return false
    val value = extension.value
    if (value is BooleanType) {
      return value.booleanValue()
    }
    return false
  }

/**
 * Creates a [QuestionnaireResponse.QuestionnaireResponseItemComponent] from the provided
 * [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireResponseItem():
  QuestionnaireResponse.QuestionnaireResponseItemComponent {
  return QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
    linkId = this@createQuestionnaireResponseItem.linkId
    answer = createQuestionnaireResponseItemAnswers()
    if (hasNestedItemsWithinAnswers && answer.isNotEmpty()) {
      this.addNestedItemsToAnswer(this@createQuestionnaireResponseItem)
    } else if (this@createQuestionnaireResponseItem.type ==
        Questionnaire.QuestionnaireItemType.GROUP
    ) {
      this@createQuestionnaireResponseItem.item.forEach {
        this.addItem(it.createQuestionnaireResponseItem())
      }
    }
  }
}

/**
 * Returns a list of answers from the initial values of the questionnaire item. `null` if no intial
 * value.
 */
private fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireResponseItemAnswers():
  MutableList<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? {
  if (initial.isEmpty()) {
    return null
  }

  if (type == Questionnaire.QuestionnaireItemType.GROUP ||
      type == Questionnaire.QuestionnaireItemType.DISPLAY
  ) {
    throw IllegalArgumentException(
      "Questionnaire item $linkId has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
    )
  }

  if (initial.size > 1 && !repeats) {
    throw IllegalArgumentException(
      "Questionnaire item $linkId can only have multiple initial values for repeating items. See rule que-13 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
    )
  }

  return mutableListOf(
    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
      value = initial[0].value
    }
  )
}

/**
 * Add items within [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent] from the
 * provided parent [Questionnaire.QuestionnaireItemComponent] with nested items. The hierarchy and
 * order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
fun QuestionnaireResponse.QuestionnaireResponseItemComponent.addNestedItemsToAnswer(
  questionnaireItemComponent: Questionnaire.QuestionnaireItemComponent
) {
  if (answer.isNotEmpty()) {
    answer.first().item = questionnaireItemComponent.getNestedQuestionnaireResponseItems()
  }
}

/**
 * Creates a list of [QuestionnaireResponse.QuestionnaireResponseItemComponent]s from the nested
 * items in the [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
private inline fun Questionnaire.QuestionnaireItemComponent.getNestedQuestionnaireResponseItems() =
  item.map { it.createQuestionnaireResponseItem() }

/** The Attachment defined in the [EXTENSION_ITEM_IMAGE] extension where applicable */
internal val Questionnaire.QuestionnaireItemComponent.itemImage: Attachment?
  get() {
    val extension = this.extension.singleOrNull { it.url == EXTENSION_ITEM_IMAGE }
    return if (extension != null) extension.value as Attachment else null
  }

/** Whether the Attachment has a [Attachment.contentType] for an image */
val Attachment.isImage: Boolean
  get() = this.hasContentType() && contentType.startsWith("image")

/** Whether the Binary has a [Binary.contentType] for an image */
private fun Binary.isImage(): Boolean = this.hasContentType() && contentType.startsWith("image")

/** Decodes the Bitmap from the Base64 encoded string in [Bitmap.data] */
private fun Binary.getBitmap(): Bitmap? {
  return if (isImage()) {
    Base64.decode(this.dataElement.valueAsString, Base64.DEFAULT).let { byteArray ->
      BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
  } else {
    Log.e("Binary", "Binary does not have a contentType image")
    null
  }
}

/**
 * Returns the Bitmap defined in the attachment as inline Base64 encoded image, Binary resource
 * defined in the url or externally hosted image. Inline Base64 encoded image requires to have
 * contentType starting with image
 */
suspend fun Attachment.fetchBitmap(): Bitmap? {
  // Attachment's with data inline need the contentType property
  // Conversion to Bitmap should only be made if the contentType is image
  if (data != null) {
    if (isImage) {
      return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
    Log.e("Attachment", "Attachment is not of contentType image/**")
    return null
  } else if (url != null && (url.startsWith("https") || url.startsWith("http"))) {
    // Points to a Binary resource on a FHIR compliant server
    return if (url.contains("/Binary/")) {
      DataCaptureConfig.attachmentResolver?.run { resolveBinaryResource(url)?.getBitmap() }
    } else {
      DataCaptureConfig.attachmentResolver?.resolveImageUrl(url)
    }
  }

  Log.e("Attachment", "Could not determine the Bitmap in Attachment $id")
  return null
}
