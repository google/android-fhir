/*
 * Copyright 2021 Google LLC
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

import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.Spanned
import androidx.core.text.HtmlCompat
import java.util.Locale
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
  SLIDER("slider", QuestionnaireItemViewHolderType.SLIDER),
  PHONE_NUMBER("phone-number", QuestionnaireItemViewHolderType.PHONE_NUMBER)
}

// Please note these URLs do not point to any FHIR Resource and are broken links. They are being used until we can engage the FHIR community to add these extensions officially.
internal const val EXTENSION_ITEM_CONTROL_URL_UNOFFICIAL =
  "https://github.com/google/android-fhir/StructureDefinition/questionnaire-itemControl"
internal const val EXTENSION_ITEM_CONTROL_SYSTEM_UNOFFICIAL = "https://github.com/google/android-fhir/questionnaire-item-control"

//Below URLs exist and are supported by HL7
internal const val EXTENSION_ITEM_CONTROL_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl"
internal const val EXTENSION_ITEM_CONTROL_SYSTEM = "http://hl7.org/fhir/questionnaire-item-control"
internal const val EXTENSION_HIDDEN_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-hidden"

// Item control code, or null
internal val Questionnaire.QuestionnaireItemComponent.itemControl: ItemControlTypes?
  get() {
    val codeableConcept =
      this.extension.firstOrNull { it.url == EXTENSION_ITEM_CONTROL_URL || it.url == EXTENSION_ITEM_CONTROL_URL_UNOFFICIAL }?.value as CodeableConcept?
    val code =
      codeableConcept?.coding?.firstOrNull { it.system == EXTENSION_ITEM_CONTROL_SYSTEM || it.system == EXTENSION_ITEM_CONTROL_SYSTEM_UNOFFICIAL }?.code
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

/** Converts Text with HTML Tag to formated text. */
private fun String.toSpanned(): Spanned {
  return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
}

/**
 * Localized and spanned value of [Questionnaire.QuestionnaireItemComponent.text] if translation is
 * present. Default value otherwise.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedTextSpanned: Spanned?
  get() = textElement?.getLocalizedText()?.toSpanned()

/**
 * Localized and spanned value of [Questionnaire.QuestionnaireItemComponent.prefix] if translation
 * is present. Default value otherwise.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedPrefixSpanned: Spanned?
  get() = prefixElement?.getLocalizedText()?.toSpanned()

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
