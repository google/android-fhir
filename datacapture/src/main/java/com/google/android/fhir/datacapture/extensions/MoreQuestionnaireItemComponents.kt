/*
 * Copyright 2023 Google LLC
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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.HtmlCompat
import ca.uhn.fhir.util.UrlUtil
import com.google.android.fhir.datacapture.DataCapture
import com.google.android.fhir.datacapture.QuestionnaireViewHolderType
import com.google.android.fhir.datacapture.fhirpath.evaluateToDisplay
import com.google.android.fhir.getLocalizedText
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
import timber.log.Timber

// Please note these URLs do not point to any FHIR Resource and are broken links. They are being
// used until we can engage the FHIR community to add these extensions officially.

internal const val EXTENSION_ITEM_CONTROL_URL_ANDROID_FHIR =
  "https://github.com/google/android-fhir/StructureDefinition/questionnaire-itemControl"

internal const val EXTENSION_ITEM_CONTROL_SYSTEM_ANDROID_FHIR =
  "https://github.com/google/android-fhir/questionnaire-item-control"

// Below URLs exist and are supported by HL7

internal const val EXTENSION_ANSWER_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression"

internal const val EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerOptionsToggleExpression"

internal const val EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION = "option"

internal const val EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION = "expression"

internal const val EXTENSION_CANDIDATE_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression"

internal const val EXTENSION_CALCULATED_EXPRESSION_URL =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression"

internal const val EXTENSION_CHOICE_ORIENTATION_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-choiceOrientation"

internal const val EXTENSION_CHOICE_COLUMN_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn"

internal const val EXTENSION_CQF_CALCULATED_VALUE_URL: String =
  "http://hl7.org/fhir/StructureDefinition/cqf-calculatedValue"

internal const val EXTENSION_DISPLAY_CATEGORY_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-displayCategory"

internal const val EXTENSION_DISPLAY_CATEGORY_SYSTEM =
  "http://hl7.org/fhir/questionnaire-display-category"

internal const val EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS = "instructions"

internal const val EXTENSION_ENABLE_WHEN_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression"

internal const val EXTENSION_ENTRY_FORMAT_URL =
  "http://hl7.org/fhir/StructureDefinition/entryFormat"

internal const val EXTENSION_HIDDEN_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-hidden"

internal const val EXTENSION_ITEM_CONTROL_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl"

internal const val EXTENSION_ITEM_CONTROL_SYSTEM = "http://hl7.org/fhir/questionnaire-item-control"

internal const val EXTENSION_ITEM_MEDIA =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemMedia"

internal const val EXTENSION_MAX_SIZE = "http://hl7.org/fhir/StructureDefinition/maxSize"

internal const val EXTENSION_MIME_TYPE = "http://hl7.org/fhir/StructureDefinition/mimeType"

/**
 * Extension for questionnaire items of integer and decimal types including a single unit to be
 * displayed.
 *
 * See https://hl7.org/fhir/extensions/StructureDefinition-questionnaire-unit.html.
 */
internal const val EXTENSION_QUESTIONNAIRE_UNIT_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-unit"

/**
 * Extension for questionnaire items of quantity type including unit options to choose from.
 *
 * See https://hl7.org/fhir/extensions/StructureDefinition-questionnaire-unitOption.html.
 */
internal const val EXTENSION_QUESTIONNAIRE_UNIT_OPTION_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption"

/**
 * Extension for questionnaire items of quantity type including a value set of unit options to
 * choose from.
 */
internal const val EXTENSION_QUESTIONNAIRE_UNIT_VALUE_SET_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-unitValueSet"

internal const val EXTENSION_SLIDER_STEP_VALUE_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-sliderStepValue"

internal const val EXTENSION_VARIABLE_URL = "http://hl7.org/fhir/StructureDefinition/variable"

internal const val ITEM_INITIAL_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression"

// ********************************************************************************************** //
//                                                                                                //
// Rendering extensions: item control, choice orientation, etc.                                   //
//                                                                                                //
// See https://build.fhir.org/ig/HL7/sdc/rendering.html.                                          //
//                                                                                                //
// ********************************************************************************************** //

/**
 * Item control types supported by the SDC library with `extensionCode` from the value set
 * http://hl7.org/fhir/R4/valueset-questionnaire-item-control.html and `viewHolderType` as the
 * [QuestionnaireViewHolderType] to be used to render the question.
 */
enum class ItemControlTypes(
  val extensionCode: String,
  val viewHolderType: QuestionnaireViewHolderType,
) {
  AUTO_COMPLETE("autocomplete", QuestionnaireViewHolderType.AUTO_COMPLETE),
  CHECK_BOX("check-box", QuestionnaireViewHolderType.CHECK_BOX_GROUP),
  DROP_DOWN("drop-down", QuestionnaireViewHolderType.DROP_DOWN),
  OPEN_CHOICE("open-choice", QuestionnaireViewHolderType.DIALOG_SELECT),
  RADIO_BUTTON("radio-button", QuestionnaireViewHolderType.RADIO_GROUP),
  SLIDER("slider", QuestionnaireViewHolderType.SLIDER),
  PHONE_NUMBER("phone-number", QuestionnaireViewHolderType.PHONE_NUMBER),
}

/**
 * The initial-expression extension on [QuestionnaireItemComponent] to allow dynamic selection of
 * default or initially selected answers
 */
val Questionnaire.QuestionnaireItemComponent.initialExpression: Expression?
  get() {
    return this.extension
      .firstOrNull { it.url == ITEM_INITIAL_EXPRESSION_URL }
      ?.let { it.value as Expression }
  }

/**
 * The [ItemControlTypes] of the questionnaire item if it is specified by the item control
 * extension, or `null`.
 *
 * See http://hl7.org/fhir/R4/extension-questionnaire-itemcontrol.html.
 */
val Questionnaire.QuestionnaireItemComponent.itemControl: ItemControlTypes?
  get() {
    val codeableConcept =
      this.extension
        .firstOrNull {
          it.url == EXTENSION_ITEM_CONTROL_URL || it.url == EXTENSION_ITEM_CONTROL_URL_ANDROID_FHIR
        }
        ?.value as CodeableConcept?
    val code =
      codeableConcept
        ?.coding
        ?.firstOrNull {
          it.system == EXTENSION_ITEM_CONTROL_SYSTEM ||
            it.system == EXTENSION_ITEM_CONTROL_SYSTEM_ANDROID_FHIR
        }
        ?.code
    return ItemControlTypes.values().firstOrNull { it.extensionCode == code }
  }

/**
 * The desired orientation for the list of choices.
 *
 * See http://hl7.org/fhir/R4/extension-questionnaire-choiceorientation.html.
 */
enum class ChoiceOrientationTypes(val extensionCode: String) {
  HORIZONTAL("horizontal"),
  VERTICAL("vertical"),
}

/** Desired orientation to render a list of choices. */
val Questionnaire.QuestionnaireItemComponent.choiceOrientation: ChoiceOrientationTypes?
  get() {
    val code =
      (this.extension.firstOrNull { it.url == EXTENSION_CHOICE_ORIENTATION_URL }?.value
          as CodeType?)
        ?.valueAsString
    return ChoiceOrientationTypes.values().firstOrNull { it.extensionCode == code }
  }

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
 * The entry format specified in the extension https://hl7.org/fhir/R4/extension-entryformat.html.
 */
val Questionnaire.QuestionnaireItemComponent.entryFormat: String?
  get() {
    val extension = extension.singleOrNull { it.url == EXTENSION_ENTRY_FORMAT_URL } ?: return null
    val value = extension.value
    if (value is StringType) {
      return value.toString()
    }
    return null
  }

/**
 * The date entry format for the questionnaire item component if one is specified, otherwise, the
 * system default date entry format.
 */
val Questionnaire.QuestionnaireItemComponent.dateEntryFormatOrSystemDefault: String
  get() {
    return if (isValidDateEntryFormat(entryFormat)) {
      entryFormat!!
    } else {
      getLocalizedDatePattern()
    }
  }

private fun isValidDateEntryFormat(entryFormat: String?): Boolean {
  return entryFormat?.let {
    try {
      val text = LocalDate.now().format(DateTimeFormatter.ofPattern(entryFormat))
      LocalDate.parse(text, DateTimeFormatter.ofPattern(entryFormat))
      true
    } catch (e: Exception) {
      Timber.w(e.message)
      false
    }
  }
    ?: false
}

/** Slider step extension value. */
val Questionnaire.QuestionnaireItemComponent.sliderStepValue: Int?
  get() {
    val extension =
      this.extension.singleOrNull { it.url == EXTENSION_SLIDER_STEP_VALUE_URL } ?: return null
    val value = extension.value
    if (value is IntegerType) {
      return value.value
    }
    return null
  }

// ********************************************************************************************** //
//                                                                                                //
// Additional display utilities: display item control, localized text spanned,                    //
// localized prefix spanned, localized instruction spanned, etc.                                  //
//                                                                                                //
// ********************************************************************************************** //

/** UI controls relevant to rendering questionnaire items. */
internal enum class DisplayItemControlType(val extensionCode: String) {
  FLYOVER("flyover"),
  PAGE("page"),
  HELP("help"),
}

/** Item control to show instruction text */
internal val Questionnaire.QuestionnaireItemComponent.displayItemControl: DisplayItemControlType?
  get() {
    val codeableConcept =
      this.extension.firstOrNull { it.url == EXTENSION_ITEM_CONTROL_URL }?.value as CodeableConcept?
    val code =
      codeableConcept?.coding?.firstOrNull { it.system == EXTENSION_ITEM_CONTROL_SYSTEM }?.code
    return DisplayItemControlType.values().firstOrNull { it.extensionCode == code }
  }

/** Whether any one of the nested display item has [DisplayItemControlType.HELP] control. */
val Questionnaire.QuestionnaireItemComponent.hasHelpButton: Boolean
  get() {
    return item.any { it.isHelpCode }
  }

/** Converts Text with HTML Tag to formatted text. */
internal fun String.toSpanned(): Spanned {
  return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
}

/**
 * Localized and spanned value of [Questionnaire.QuestionnaireItemComponent.text] if translation is
 * present. Default value otherwise.
 */
val Questionnaire.QuestionnaireItemComponent.localizedTextSpanned: Spanned?
  get() = textElement?.getLocalizedText()?.toSpanned()

/**
 * Localized and spanned value of [Questionnaire.QuestionnaireItemComponent.prefix] if translation
 * is present. Default value otherwise.
 */
val Questionnaire.QuestionnaireItemComponent.localizedPrefixSpanned: Spanned?
  get() = prefixElement?.getLocalizedText()?.toSpanned()

/**
 * A nested questionnaire item of type display with displayCategory extension with
 * [EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS] code is used as the instructions of the parent
 * question.
 */
val Questionnaire.QuestionnaireItemComponent.localizedInstructionsSpanned: Spanned
  get() = item.getLocalizedInstructionsSpanned()

/**
 * Returns a Spanned object that contains the localized instructions for all of the items in this
 * list that are of type `Questionnaire.QuestionnaireItemType.DISPLAY` and have the
 * `isInstructionsCode` flag set. The instructions are separated by newlines.
 */
fun List<Questionnaire.QuestionnaireItemComponent>.getLocalizedInstructionsSpanned(
  separator: String = "\n",
) =
  SpannableStringBuilder().apply {
    this@getLocalizedInstructionsSpanned.filter { questionnaireItem ->
        questionnaireItem.type == Questionnaire.QuestionnaireItemType.DISPLAY &&
          questionnaireItem.isInstructionsCode
      }
      .map { it.localizedTextSpanned }
      .joinTo(this, separator)
  }

/**
 * A nested questionnaire item of type display with code [DisplayItemControlType.FLYOVER] (if
 * present) is used as the fly-over text of the parent question.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedFlyoverSpanned: Spanned?
  get() = item.localizedFlyoverSpanned

/** [localizedFlyoverSpanned] over list of [Questionnaire.QuestionnaireItemComponent] */
val List<Questionnaire.QuestionnaireItemComponent>.localizedFlyoverSpanned: Spanned?
  get() =
    this.firstOrNull { questionnaireItem ->
        questionnaireItem.type == Questionnaire.QuestionnaireItemType.DISPLAY &&
          questionnaireItem.displayItemControl == DisplayItemControlType.FLYOVER
      }
      ?.localizedTextSpanned

/**
 * A nested questionnaire item of type display with displayCategory extension with
 * [EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS] code is used as the instructions of the parent
 * question.
 */
val Questionnaire.QuestionnaireItemComponent.localizedHelpSpanned: Spanned?
  get() = item.localizedHelpSpanned

/** [localizedHelpSpanned] over list of [Questionnaire.QuestionnaireItemComponent] */
val List<Questionnaire.QuestionnaireItemComponent>.localizedHelpSpanned: Spanned?
  get() {
    return this.firstOrNull { questionnaireItem -> questionnaireItem.isHelpCode }
      ?.localizedTextSpanned
  }

/** Returns `true` if extension is display category extension and contains 'instructions' code. */
internal val Questionnaire.QuestionnaireItemComponent.isInstructionsCode: Boolean
  get() {
    return when (type) {
      Questionnaire.QuestionnaireItemType.DISPLAY -> {
        val codeableConcept =
          this.extension.firstOrNull { it.url == EXTENSION_DISPLAY_CATEGORY_URL }?.value
            as CodeableConcept?
        val code =
          codeableConcept
            ?.coding
            ?.firstOrNull { it.system == EXTENSION_DISPLAY_CATEGORY_SYSTEM }
            ?.code
        code == EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS
      }
      else -> {
        false
      }
    }
  }

/**
 * Returns `true` if item type is display and [displayItemControl] is
 * [DisplayItemControlType.FLYOVER].
 */
internal val Questionnaire.QuestionnaireItemComponent.isFlyoverCode: Boolean
  get() {
    return when (type) {
      Questionnaire.QuestionnaireItemType.DISPLAY -> {
        displayItemControl == DisplayItemControlType.FLYOVER
      }
      else -> {
        false
      }
    }
  }

/** Whether item type is display and [displayItemControl] is [DisplayItemControlType.HELP]. */
internal val Questionnaire.QuestionnaireItemComponent.isHelpCode: Boolean
  get() {
    return when (type) {
      Questionnaire.QuestionnaireItemType.DISPLAY -> {
        displayItemControl == DisplayItemControlType.HELP
      }
      else -> {
        false
      }
    }
  }

/** Whether item type is display. */
internal val Questionnaire.QuestionnaireItemComponent.isDisplayItem: Boolean
  get() =
    (type == Questionnaire.QuestionnaireItemType.DISPLAY &&
      (isInstructionsCode || isFlyoverCode || isHelpCode))

// ********************************************************************************************** //
//                                                                                                //
// Form behavior: mime type, min size, max size, etc.                                             //
//                                                                                                //
// See https://build.fhir.org/ig/HL7/sdc/behavior.html.                                           //
//                                                                                                //
// ********************************************************************************************** //

/** Identifies the kinds of attachment allowed to be sent for an element. */
val Questionnaire.QuestionnaireItemComponent.mimeTypes: List<String>
  get() {
    return extension
      .filter { it.url == EXTENSION_MIME_TYPE }
      .map { (it.value as CodeType).valueAsString }
      .filter { !it.isNullOrEmpty() }
  }

/** Currently supported mime types. */
enum class MimeType(val value: String) {
  AUDIO("audio"),
  DOCUMENT("application"),
  IMAGE("image"),
  VIDEO("video"),
}

/** Returns the main MIME type of a MIME type string (e.g. image/png returns image). */
private fun getMimeType(mimeType: String): String = mimeType.substringBefore("/")

/** Returns true if at least one mime type matches the given type. */
fun Questionnaire.QuestionnaireItemComponent.hasMimeType(type: String): Boolean {
  return mimeTypes.any { it.substringBefore("/") == type }
}

/** Returns true if all mime types match the given type. */
fun Questionnaire.QuestionnaireItemComponent.hasMimeTypeOnly(type: String): Boolean {
  return mimeTypes.all { it.substringBefore("/") == type }
}

/** The maximum size of an attachment in Bytes. */
internal val Questionnaire.QuestionnaireItemComponent.maxSizeInBytes: BigDecimal?
  get() =
    (extension.firstOrNull { it.url == EXTENSION_MAX_SIZE }?.valueAsPrimitive as DecimalType?)
      ?.value

private val BYTES_PER_KIB = BigDecimal(1024)

/** The maximum size of an attachment in Kibibytes. */
internal val Questionnaire.QuestionnaireItemComponent.maxSizeInKiBs: BigDecimal?
  get() = maxSizeInBytes?.div(BYTES_PER_KIB)

private val BYTES_PER_MIB = BigDecimal(1048576)

/** The maximum size of an attachment in Mebibytes. */
internal val Questionnaire.QuestionnaireItemComponent.maxSizeInMiBs: BigDecimal?
  get() = maxSizeInBytes?.div(BYTES_PER_MIB)

/** The default maximum size of an attachment is 1 Mebibytes. */
private val DEFAULT_SIZE = BigDecimal(1048576)

/** Returns true if given size is above maximum size allowed. */
internal fun Questionnaire.QuestionnaireItemComponent.isGivenSizeOverLimit(
  size: BigDecimal,
): Boolean {
  return size > (maxSizeInBytes ?: DEFAULT_SIZE)
}

/** A media that is attached to a [Questionnaire.QuestionnaireItemComponent]. */
internal val Questionnaire.QuestionnaireItemComponent.itemMedia: Attachment?
  get() =
    (getExtensionByUrl(EXTENSION_ITEM_MEDIA)?.value as? Attachment)?.takeIf { it.hasContentType() }

/* TODO: unify the code path from itemAnswerMedia to use fetchBitmapFromUrl (github.com/google/android-fhir/issues/1876) */
/** Fetches the Bitmap representation of [Attachment.url]. */
internal suspend fun Attachment.fetchBitmapFromUrl(context: Context): Bitmap? {
  if (!hasUrl() || !UrlUtil.isValid(url) || !hasContentType()) return null

  if (getMimeType(contentType) != MimeType.IMAGE.value) return null

  val urlResolver = DataCapture.getConfiguration(context).urlResolver ?: return null

  return withContext(Dispatchers.IO) { urlResolver.resolveBitmapUrl(url) }
}

/** Decodes the Bitmap representation of [Attachment.data]. */
internal fun Attachment.decodeToBitmap(): Bitmap? {
  if (!hasContentType() || !hasData()) return null

  if (getMimeType(contentType) != MimeType.IMAGE.value) return null

  return data.decodeToBitmap()
}

/** Returns Bitmap if Byte Array is a valid Bitmap representation, otherwise null. */
private fun ByteArray.decodeToBitmap(): Bitmap? {
  val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)

  if (bitmap == null) Timber.w("Image could not be decoded")

  return bitmap
}

/**
 * The unit for the numerical question.
 *
 * See http://hl7.org/fhir/R4/extension-questionnaire-unit.html.
 */
internal val Questionnaire.QuestionnaireItemComponent.unit: Coding?
  get() {
    val extension =
      this.extension.singleOrNull { it.url == EXTENSION_QUESTIONNAIRE_UNIT_URL } ?: return null
    val value = extension.value
    if (value is Coding) {
      return value
    }
    return null
  }

/**
 * The unit options for the quantity question.
 *
 * See http://hl7.org/fhir/R4/extension-questionnaire-unitoption.html.
 */
internal val Questionnaire.QuestionnaireItemComponent.unitOption: List<Coding>
  get() {
    return this.extension
      .filter { it.url == EXTENSION_QUESTIONNAIRE_UNIT_OPTION_URL }
      .map { it.value as Coding }
  }

// ********************************************************************************************** //
//                                                                                                //
// Expressions: answer options toggle expression, variable expression, calculated expression,     //
// expression dependency.                                                                         //
//                                                                                                //
// See https://build.fhir.org/ig/HL7/sdc/expressions.html.                                        //
//                                                                                                //
// ********************************************************************************************** //

internal val Questionnaire.QuestionnaireItemComponent.answerOptionsToggleExpressions
  get() =
    this.extension
      .filter { it.url == EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL }
      .map { rootExtension ->
        val options =
          rootExtension.extension
            .filter { it.url == EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION }
            .map { it.value }
        if (options.isEmpty()) {
          throw IllegalArgumentException(
            "Questionnaire item $linkId with extension '$EXTENSION_ANSWER_EXPRESSION_URL' requires at least one option. See http://hl7.org/fhir/uv/sdc/STU3/StructureDefinition-sdc-questionnaire-answerOptionsToggleExpression.html.",
          )
        }
        val expression =
          rootExtension.extension
            .single { it.url == EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION }
            .let { it.castToExpression(it.value) }
        expression to options
      }

// Return expression if QuestionnaireItemComponent has ENABLE WHEN EXPRESSION URL
val Questionnaire.QuestionnaireItemComponent.enableWhenExpression: Expression?
  get() {
    return this.extension
      .firstOrNull { it.url == EXTENSION_ENABLE_WHEN_EXPRESSION_URL }
      ?.let { it.value as Expression }
  }

internal val Questionnaire.QuestionnaireItemComponent.variableExpressions: List<Expression>
  get() =
    this.extension.filter { it.url == EXTENSION_VARIABLE_URL }.map { it.castToExpression(it.value) }

/**
 * Finds the specific variable name [String] at the questionnaire item
 * [Questionnaire.QuestionnaireItemComponent]
 *
 * @param variableName the [String] to match the variable
 * @return an [Expression]
 */
internal fun Questionnaire.QuestionnaireItemComponent.findVariableExpression(
  variableName: String,
): Expression? {
  return variableExpressions.find { it.name == variableName }
}

/** Returns Calculated expression, or null */
internal val Questionnaire.QuestionnaireItemComponent.calculatedExpression: Expression?
  get() =
    this.getExtensionByUrl(EXTENSION_CALCULATED_EXPRESSION_URL)?.let {
      it.castToExpression(it.value)
    }

/** Returns list of extensions whose value is of type [Expression] */
internal val Questionnaire.QuestionnaireItemComponent.expressionBasedExtensions
  get() = this.extension.filter { it.value is Expression }

/**
 * Whether [item] has any expression directly referencing the current questionnaire item by link ID
 * (e.g. if [item] has an expression `%resource.item.where(linkId='this-question')` where
 * `this-question` is the link ID of the current questionnaire item).
 */
internal fun Questionnaire.QuestionnaireItemComponent.isReferencedBy(
  item: Questionnaire.QuestionnaireItemComponent,
) =
  item.expressionBasedExtensions.any {
    it
      .castToExpression(it.value)
      .expression
      .replace(" ", "")
      .contains(Regex(".*linkId='${this.linkId}'.*"))
  }

internal val Questionnaire.QuestionnaireItemComponent.answerExpression: Expression?
  get() =
    ToolingExtensions.getExtension(this, EXTENSION_ANSWER_EXPRESSION_URL)?.value?.let {
      it.castToExpression(it)
    }

internal val Questionnaire.QuestionnaireItemComponent.candidateExpression: Expression?
  get() =
    ToolingExtensions.getExtension(this, EXTENSION_CANDIDATE_EXPRESSION_URL)?.value?.let {
      it.castToExpression(it)
    }

// TODO implement full functionality of choice column
// https://github.com/google/android-fhir/issues/1495
/**
 * Choice column extension https://build.fhir.org/ig/HL7/sdc/examples.html#choiceColumn
 *
 * The extension choice-column defines its internal elements as nested extension with table
 * properties
 * - path -> the field in answerOption
 * - width -> the width of given column if widget generates a table; TBD in #1495
 * - label -> the label of given column of table or answerOption
 * - forDisplay -> if the column should be shown on UI
 */
internal val Questionnaire.QuestionnaireItemComponent.choiceColumn: List<ChoiceColumn>?
  get() =
    ToolingExtensions.getExtensions(this, EXTENSION_CHOICE_COLUMN_URL)?.map { extension ->
      extension.extension.let { nestedExtensions ->
        ChoiceColumn(
          path = nestedExtensions.find { it.url == "path" }!!.value.asStringValue(),
          label = nestedExtensions.find { it.url == "label" }?.value?.asStringValue(),
          forDisplay =
            nestedExtensions.any {
              it.url == "forDisplay" && it.castToBoolean(it.value).booleanValue()
            },
        )
      }
    }

/**
 * A choice column extracted from choice column extension contains following properties
 * - path -> the path or expression in evaluated answerOption or resources to extract value
 * - label -> the label of given column of table or answerOption
 * - forDisplay -> if the column should be shown on UI
 */
internal data class ChoiceColumn(val path: String, val label: String?, val forDisplay: Boolean)

// TODO implement full functionality of choice column
// https://github.com/google/android-fhir/issues/1495
/**
 * Apply and add each choice-column mapping to answer options
 * https://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-choiceColumn.html
 *
 * Control the information displayed in list.
 * - With reference it allows selection of fields from the resource for display and reference
 * - With other types it adds the options as is
 *
 * @param dataList the source data to extract the answer option values. The data could be list of
 *   resources [Resource], identifiers [Identifier] or codes [Coding]
 * @return list of answer options [Questionnaire.QuestionnaireItemAnswerOptionComponent]
 */
internal fun Questionnaire.QuestionnaireItemComponent.extractAnswerOptions(
  dataList: List<Base>,
): List<Questionnaire.QuestionnaireItemAnswerOptionComponent> {
  return when (this.type) {
    Questionnaire.QuestionnaireItemType.REFERENCE -> {
      require(dataList.all { it.isResource }) {
        "'${this.type.toCode()}' cannot be used to populate $EXTENSION_CHOICE_COLUMN_URL. Only Resources can be used to populate the choice columns."
      }

      dataList.map { data ->
        data as Resource
        Reference().apply {
          reference = "${data.resourceType}/${data.logicalId}"
          this@extractAnswerOptions.choiceColumn
            ?.filter { it.forDisplay }
            ?.map { it.path }
            ?.let { evaluateToDisplay(it, data) }
            ?.also { display = it }
        }
      }
    }
    else -> {
      require(dataList.all { !it.isResource }) {
        "$EXTENSION_CHOICE_COLUMN_URL not applicable for '${this.type.toCode()}'. Only type reference is allowed with resource."
      }

      dataList.map { it.castToType(it) }
    }
  }.map { Questionnaire.QuestionnaireItemAnswerOptionComponent(it) }
}

// ********************************************************************************************** //
//                                                                                                //
// Utilities: zip with questionnaire response item list, nested items, create response items,     //
// flattening, etc.                                                                               //
//                                                                                                //
// ********************************************************************************************** //

/**
 * Returns a list of values built from the elements of `this` and the
 * `questionnaireResponseItemList` with the same linkId using the provided `transform` function
 * applied to each pair of questionnaire item and questionnaire response item.
 *
 * In case of repeated group item, `questionnaireResponseItemList` will contain
 * QuestionnaireResponseItemComponent with same linkId. So these items are grouped with linkId and
 * associated with its questionnaire item linkId.
 */
internal inline fun <T> List<Questionnaire.QuestionnaireItemComponent>.zipByLinkId(
  questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
  transform:
    (
      Questionnaire.QuestionnaireItemComponent,
      QuestionnaireResponse.QuestionnaireResponseItemComponent,
    ) -> T,
): List<T> {
  val linkIdToQuestionnaireResponseItemListMap = questionnaireResponseItemList.groupBy { it.linkId }
  return flatMap { questionnaireItem ->
    linkIdToQuestionnaireResponseItemListMap[questionnaireItem.linkId]?.mapNotNull {
      questionnaireResponseItem ->
      transform(questionnaireItem, questionnaireResponseItem)
    }
      ?: emptyList()
  }
}

/**
 * Whether the corresponding [QuestionnaireResponse.QuestionnaireResponseItemComponent] should have
 * [QuestionnaireResponse.QuestionnaireResponseItemComponent]s nested under
 * [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent]s.
 *
 * This is true for the following two cases:
 * 1. Questions with nested items
 * 2. Repeated groups with nested items (Note that this is how repeated groups are organized in the
 *    [QuestionnaireViewModel], and that they will be flattened in the final
 *    [QuestionnaireResponse].)
 *
 * Non-repeated groups should have child items nested directly under the group itself.
 *
 * For background, see https://build.fhir.org/questionnaireresponse.html#link.
 */
internal val Questionnaire.QuestionnaireItemComponent.shouldHaveNestedItemsUnderAnswers: Boolean
  get() = item.isNotEmpty() && (type != Questionnaire.QuestionnaireItemType.GROUP || !repeats)

/**
 * Creates a list of [QuestionnaireResponse.QuestionnaireResponseItemComponent]s from the nested
 * items in the [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
fun Questionnaire.QuestionnaireItemComponent.getNestedQuestionnaireResponseItems() =
  item.map { it.createQuestionnaireResponseItem() }

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
    if (shouldHaveNestedItemsUnderAnswers && answer.isNotEmpty()) {
      this.addNestedItemsToAnswer(this@createQuestionnaireResponseItem)
    } else if (
      this@createQuestionnaireResponseItem.type == Questionnaire.QuestionnaireItemType.GROUP &&
        !repeats
    ) {
      this@createQuestionnaireResponseItem.item.forEach {
        this.addItem(it.createQuestionnaireResponseItem())
      }
    }
  }
}

/**
 * Returns a list of answers from the initial values of the questionnaire item. `null` if no initial
 * value.
 */
private fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireResponseItemAnswers():
  MutableList<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? {
  // TODO https://github.com/google/android-fhir/issues/2161
  // The rule can be by-passed if initial value was set by an initial-expression.
  // The [ResourceMapper] at L260 wrongfully sets the initial property of questionnaire after
  // evaluation of initial-expression.
  require(answerOption.isEmpty() || initial.isEmpty() || initialExpression != null) {
    "Questionnaire item $linkId has both initial value(s) and has answerOption. See rule que-11 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
  }

  // https://build.fhir.org/ig/HL7/sdc/behavior.html#initial
  // quantity given as initial without value is for unit reference purpose only. Answer conversion
  // not needed
  if (
    answerOption.initialSelected.isEmpty() &&
      (initial.isEmpty() ||
        (initialFirstRep.hasValueQuantity() && initialFirstRep.valueQuantity.value == null))
  ) {
    return null
  }

  if (
    type == Questionnaire.QuestionnaireItemType.GROUP ||
      type == Questionnaire.QuestionnaireItemType.DISPLAY
  ) {
    throw IllegalArgumentException(
      "Questionnaire item $linkId has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial.",
    )
  }

  if ((answerOption.initialSelected.size > 1 || initial.size > 1) && !repeats) {
    throw IllegalArgumentException(
      "Questionnaire item $linkId can only have multiple initial values for repeating items. See rule que-13 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial.",
    )
  }

  return initial
    .map { it.value }
    .plus(answerOption.initialSelected)
    .map { QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply { value = it } }
    .toMutableList()
}

/**
 * Flatten a nested list of [Questionnaire.QuestionnaireItemComponent] recursively and returns a
 * flat list of all items into list embedded at any level
 */
fun List<Questionnaire.QuestionnaireItemComponent>.flattened():
  List<Questionnaire.QuestionnaireItemComponent> =
  mutableListOf<Questionnaire.QuestionnaireItemComponent>().also { flattenInto(it) }

private fun List<Questionnaire.QuestionnaireItemComponent>.flattenInto(
  output: MutableList<Questionnaire.QuestionnaireItemComponent>,
) {
  forEach {
    output.add(it)
    it.item.flattenInto(output)
  }
}

// TODO: Move this elsewhere.
val Resource.logicalId: String
  get() {
    return this.idElement?.idPart.orEmpty()
  }
