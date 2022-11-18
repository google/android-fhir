/*
 * Copyright 2022 Google LLC
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

import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.utilities.evaluateToDisplay
import com.google.android.fhir.getLocalizedText
import com.google.android.fhir.logicalId
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions

/** UI controls relevant to capturing question data. */
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
  PHONE_NUMBER("phone-number", QuestionnaireItemViewHolderType.PHONE_NUMBER),
}

// Please note these URLs do not point to any FHIR Resource and are broken links. They are being
// used until we can engage the FHIR community to add these extensions officially.
internal const val EXTENSION_ITEM_CONTROL_URL_ANDROID_FHIR =
  "https://github.com/google/android-fhir/StructureDefinition/questionnaire-itemControl"
internal const val EXTENSION_ITEM_CONTROL_SYSTEM_ANDROID_FHIR =
  "https://github.com/google/android-fhir/questionnaire-item-control"

// Below URLs exist and are supported by HL7
internal const val EXTENSION_ITEM_CONTROL_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl"
internal const val EXTENSION_ITEM_CONTROL_SYSTEM = "http://hl7.org/fhir/questionnaire-item-control"

internal const val EXTENSION_HIDDEN_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-hidden"

internal const val EXTENSION_CALCULATED_EXPRESSION_URL =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression"

internal const val EXTENSION_ENTRY_FORMAT_URL =
  "http://hl7.org/fhir/StructureDefinition/entryFormat"

internal const val EXTENSION_DISPLAY_CATEGORY_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-displayCategory"
internal const val EXTENSION_DISPLAY_CATEGORY_SYSTEM =
  "http://hl7.org/fhir/questionnaire-display-category"

internal const val EXTENSION_ENABLE_WHEN_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression"

internal const val EXTENSION_ANSWER_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression"

internal const val EXTENSION_CHOICE_COLUMN_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn"

internal const val EXTENSION_VARIABLE_URL = "http://hl7.org/fhir/StructureDefinition/variable"

internal const val EXTENSION_CQF_CALCULATED_VALUE_URL: String =
  "http://hl7.org/fhir/StructureDefinition/cqf-calculatedValue"

internal const val EXTENSION_SLIDER_STEP_VALUE_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-sliderStepValue"

internal val Questionnaire.QuestionnaireItemComponent.variableExpressions: List<Expression>
  get() =
    this.extension.filter { it.url == EXTENSION_VARIABLE_URL }.map { it.castToExpression(it.value) }

/**
 * Finds the specific variable name [String] at the questionnaire item
 * [Questionnaire.QuestionnaireItemComponent]
 *
 * @param variableName the [String] to match the variable
 *
 * @return an [Expression]
 */
internal fun Questionnaire.QuestionnaireItemComponent.findVariableExpression(
  variableName: String
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
  item: Questionnaire.QuestionnaireItemComponent
) =
  item.expressionBasedExtensions.any {
    it
      .castToExpression(it.value)
      .expression
      .replace(" ", "")
      .contains(Regex(".*linkId='${this.linkId}'.*"))
  }

// Item control code, or null
internal val Questionnaire.QuestionnaireItemComponent.itemControl: ItemControlTypes?
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

internal enum class ChoiceOrientationTypes(val extensionCode: String) {
  HORIZONTAL("horizontal"),
  VERTICAL("vertical")
}

internal const val EXTENSION_CHOICE_ORIENTATION_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-choiceOrientation"

/** Desired orientation to render a list of choices. */
internal val Questionnaire.QuestionnaireItemComponent.choiceOrientation: ChoiceOrientationTypes?
  get() {
    val code =
      (this.extension.firstOrNull { it.url == EXTENSION_CHOICE_ORIENTATION_URL }?.value
          as CodeType?)
        ?.valueAsString
    return ChoiceOrientationTypes.values().firstOrNull { it.extensionCode == code }
  }

internal const val EXTENSION_MIME_TYPE = "http://hl7.org/fhir/StructureDefinition/mimeType"

/** Identifies the kinds of attachment allowed to be sent for an element. */
internal val Questionnaire.QuestionnaireItemComponent.mimeTypes: List<String>
  get() {
    return extension
      .filter {
        it.url == EXTENSION_MIME_TYPE && !(it.value as CodeType).valueAsString.isNullOrEmpty()
      }
      .map { (it.value as CodeType).valueAsString }
  }

/** UI controls relevant to rendering questionnaire items. */
internal enum class DisplayItemControlType(val extensionCode: String) {
  FLYOVER("flyover"),
  PAGE("page"),
  HELP("help")
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
internal val Questionnaire.QuestionnaireItemComponent.hasHelpButton: Boolean
  get() {
    return item.any { it.isHelpCode }
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

/**
 * Whether the corresponding [QuestionnaireResponse.QuestionnaireResponseItemComponent] should have
 * nested items within [QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent](s).
 */
internal val Questionnaire.QuestionnaireItemComponent.hasNestedItemsWithinAnswers: Boolean
  get() = item.isNotEmpty() && type != Questionnaire.QuestionnaireItemType.GROUP

/** Converts Text with HTML Tag to formated text. */
private fun String.toSpanned(): Spanned {
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
 * A nested questionnaire item of type display with displayCategory extension with [INSTRUCTIONS]
 * code is used as the instructions of the parent question.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedInstructionsSpanned: Spanned?
  get() {
    return item
      .firstOrNull { questionnaireItem ->
        questionnaireItem.type == Questionnaire.QuestionnaireItemType.DISPLAY &&
          questionnaireItem.isInstructionsCode
      }
      ?.localizedTextSpanned
  }

/**
 * A nested questionnaire item of type display with code [DisplayItemControlType.FLYOVER] (if
 * present) is used as the fly-over text of the parent question.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedFlyoverSpanned: Spanned?
  get() =
    item
      .firstOrNull { questionnaireItem ->
        questionnaireItem.type == Questionnaire.QuestionnaireItemType.DISPLAY &&
          questionnaireItem.displayItemControl == DisplayItemControlType.FLYOVER
      }
      ?.localizedTextSpanned

/**
 * A nested questionnaire item of type display with displayCategory extension with [INSTRUCTIONS]
 * code is used as the instructions of the parent question.
 */
internal val Questionnaire.QuestionnaireItemComponent.localizedHelpSpanned: Spanned?
  get() {
    return item
      .firstOrNull { questionnaireItem -> questionnaireItem.isHelpCode }
      ?.localizedTextSpanned
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

/** Whether the QuestionnaireItem should have entry format string. */
val Questionnaire.QuestionnaireItemComponent.entryFormat: String?
  get() {
    val extension = extension.singleOrNull { it.url == EXTENSION_ENTRY_FORMAT_URL } ?: return null
    val value = extension.value
    if (value is StringType) {
      return value.toString()
    }
    return null
  }

internal const val INSTRUCTIONS = "instructions"

/** Returns [true] if extension is display category extension and contains 'instructions' code. */
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
        code == INSTRUCTIONS
      }
      else -> {
        false
      }
    }
  }

/**
 * Returns [true] if item type is display and [displayItemControl] is
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

/** Slider step extension value. */
internal val Questionnaire.QuestionnaireItemComponent.sliderStepValue: Int?
  get() {
    val extension =
      this.extension.singleOrNull { it.url == EXTENSION_SLIDER_STEP_VALUE_URL } ?: return null
    val value = extension.value
    if (value is IntegerType) {
      return value.value
    }
    return null
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

// Return expression if QuestionnaireItemComponent has ENABLE WHEN EXPRESSION URL
val Questionnaire.QuestionnaireItemComponent.enableWhenExpression: Expression?
  get() {
    return this.extension
      .firstOrNull { it.url == EXTENSION_ENABLE_WHEN_EXPRESSION_URL }
      ?.let { it.value as Expression }
  }

/**
 * Returns a list of answers from the initial values of the questionnaire item. `null` if no intial
 * value.
 */
private fun Questionnaire.QuestionnaireItemComponent.createQuestionnaireResponseItemAnswers():
  MutableList<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>? {
  // https://build.fhir.org/ig/HL7/sdc/behavior.html#initial
  // quantity given as initial without value is for unit reference purpose only. Answer conversion
  // not needed
  if (initial.isEmpty() ||
      (initialFirstRep.hasValueQuantity() && initialFirstRep.valueQuantity.value == null)
  ) {
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

internal val Questionnaire.QuestionnaireItemComponent.answerExpression: Expression?
  get() =
    ToolingExtensions.getExtension(this, EXTENSION_ANSWER_EXPRESSION_URL)?.value?.let {
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
    ToolingExtensions.getExtensions(this, EXTENSION_CHOICE_COLUMN_URL)?.map {
      it.extension.let {
        ChoiceColumn(
          path = it.find { it.url == "path" }!!.value.asStringValue(),
          label = it.find { it.url == "label" }?.value?.asStringValue(),
          forDisplay =
            it.any { it.url == "forDisplay" && it.castToBoolean(it.value).booleanValue() }
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
 * resources [Resource], identifiers [Identifier] or codes [Coding]
 * @return list of answer options [Questionnaire.QuestionnaireItemAnswerOptionComponent]
 */
internal fun Questionnaire.QuestionnaireItemComponent.extractAnswerOptions(
  dataList: List<Base>
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

/**
 * Flatten a nested list of [Questionnaire.QuestionnaireItemComponent] recursively and returns a
 * flat list of all items into list embedded at any level
 */
fun List<Questionnaire.QuestionnaireItemComponent>.flattened():
  List<Questionnaire.QuestionnaireItemComponent> {
  return this + this.flatMap { it.item.flattened() }
}

/**
 * Creates a list of [QuestionnaireResponse.QuestionnaireResponseItemComponent]s from the nested
 * items in the [Questionnaire.QuestionnaireItemComponent].
 *
 * The hierarchy and order of child items will be retained as specified in the standard. See
 * https://www.hl7.org/fhir/questionnaireresponse.html#notes for more details.
 */
fun Questionnaire.QuestionnaireItemComponent.getNestedQuestionnaireResponseItems() =
  item.map { it.createQuestionnaireResponseItem() }
