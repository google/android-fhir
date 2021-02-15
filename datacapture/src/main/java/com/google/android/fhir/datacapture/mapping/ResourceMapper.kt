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

package com.google.android.fhir.datacapture.mapping

import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemTypeCode
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.r4.core.Time
import com.google.fhir.r4.core.Url
import com.google.fhir.shaded.protobuf.Message

/**
 * Maps [QuestionnaireResponse]s to FHIR resources and vice versa.
 *
 * The process of converting [QuestionnaireResponse]s to other FHIR resources is called
 * [extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html). The reverse process of converting
 * existing FHIR resources to [QuestionnaireResponse]s to be used to pre-fill the UI is called
 * [population](http://build.fhir.org/ig/HL7/sdc/populate.html).
 *
 * [Definition-based extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html#definition-based-extraction)
 * and [expression-based population](http://build.fhir.org/ig/HL7/sdc/populate.html#expression-based-population)
 * are used because these approaches are generic enough to work with any FHIR resource types, and at
 * the same time relatively easy to implement.
 *
 * WARNING: This is not production-ready.
 */
internal object ResourceMapper {

    /**
     * Extract a FHIR resource from the `questionnaire` and `questionnaireResponse`.
     *
     * This method assumes there is only one FHIR resource to be extracted from the given
     * `questionnaire` and `questionnaireResponse`.
     */
    fun extract(
        questionnaire: Questionnaire,
        questionnaireResponse: QuestionnaireResponse
    ): Message {
        val builder = questionnaire.itemContextNameToExpressionMap.values.first().let {
            Class.forName("com.google.fhir.r4.core.$it")
                .getMethod("newBuilder")
                .invoke(null) as Message.Builder
        }
        return builder
            .extractFields(questionnaire.itemList, questionnaireResponse.itemList)
            .build()
    }
}

/**
 * Extracts values for fields in the builder from the corresponding questions and answers in
 * [questionnaireItemList] and [questionnaireResponseItemList].
 */
private fun Message.Builder.extractFields(
    questionnaireItemList: List<Questionnaire.Item>,
    questionnaireResponseItemList: List<QuestionnaireResponse.Item>
): Message.Builder {
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    while (
        questionnaireItemListIterator.hasNext() &&
        questionnaireResponseItemListIterator.hasNext()
    ) {
        val questionnaireItem = questionnaireItemListIterator.next()
        val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
        this.extractField(questionnaireItem, questionnaireResponseItem)
        extractFields(questionnaireItem.itemList, questionnaireResponseItem.itemList)
    }
    return this
}

/**
 * Extracts value for field in the builder from the corresponding question and answer in
 * [questionnaireItem] and [questionnaireResponseItem].
 *
 * NOTE: Nested fields are not handled. See https://github.com/google/android-fhir/issues/240.
 */
private fun Message.Builder.extractField(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItem: QuestionnaireResponse.Item
) {
    val targetFieldName = questionnaireItem.definitionFieldName
    if (targetFieldName.isEmpty()) {
        return
    }

    questionnaireItem.type.getClass()?.let {
        this.javaClass.getMethod("set${targetFieldName.capitalize()}", it).invoke(
            this,
            questionnaireResponseItem.answerList.single().getValueForType(questionnaireItem.type)
        )
    }
}

/**
 * Returns the field name for the [Questionnaire.Item]'s definition.
 *
 * For example, if the definition URI is
 * `"http://hl7.org/fhir/StructureDefinition/Patient#Patient.birthDate"`, this function will return
 * `"birthDate"`.
 */
private val Questionnaire.Item.definitionFieldName
    get() = this.definition.value.substringAfterLast(".")

/**
 * Returns the [Class] for the answer to the [Questionnaire.Item].
 *
 * Used to retrieve the method to invoke to set the field in the extracted FHIR resource.
 */
private fun Questionnaire.Item.TypeCode.getClass(): Class<out Message>? = when (this.value) {
    QuestionnaireItemTypeCode.Value.DATE -> Date::class.java
    QuestionnaireItemTypeCode.Value.BOOLEAN -> Boolean::class.java
    QuestionnaireItemTypeCode.Value.DECIMAL -> Decimal::class.java
    QuestionnaireItemTypeCode.Value.INTEGER -> Integer::class.java
    QuestionnaireItemTypeCode.Value.DATE_TIME -> DateTime::class.java
    QuestionnaireItemTypeCode.Value.TIME -> Time::class.java
    QuestionnaireItemTypeCode.Value.STRING, QuestionnaireItemTypeCode.Value.TEXT ->
        com.google.fhir.r4.core.String::class.java
    QuestionnaireItemTypeCode.Value.URL -> Url::class.java
    else -> null
}

/**
 * Returns the value of the [QuestionnaireResponse.Item.Answer] for the [type].
 *
 * Used to retrieve the value to set the field in the extracted FHIR resource.
 *
 * @throws IllegalArgumentException if [type] is not supported (for example, questions of type
 * [QuestionnaireItemTypeCode.Value.GROUP] do not collect any answer).
 */
private fun QuestionnaireResponse.Item.Answer.getValueForType(
    type: Questionnaire.Item.TypeCode
): Message = when (val value = type.value) {
    QuestionnaireItemTypeCode.Value.DATE -> this.value.date
    QuestionnaireItemTypeCode.Value.BOOLEAN -> this.value.boolean
    QuestionnaireItemTypeCode.Value.DECIMAL -> this.value.decimal
    QuestionnaireItemTypeCode.Value.INTEGER -> this.value.integer
    QuestionnaireItemTypeCode.Value.DATE_TIME -> this.value.dateTime
    QuestionnaireItemTypeCode.Value.TIME -> this.value.time
    QuestionnaireItemTypeCode.Value.STRING, QuestionnaireItemTypeCode.Value.TEXT ->
        this.value.stringValue
    QuestionnaireItemTypeCode.Value.URL -> this.value.uri
    else -> throw IllegalArgumentException("Unsupported value type $value")
}

/**
 * The map from the `name`s to `expression`s in the [item extraction context extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)s.
 */
private val Questionnaire.itemContextNameToExpressionMap: Map<String, String>
    get() {
        return this.extensionList.filter {
            it.url.value == ITEM_CONTEXT_EXTENSION_URL
        }.map {
            val expression = it.value.expression
            expression.name.value to expression.expression.value
        }.toMap()
    }

/**
 * See [Extension: item extraction context](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html).
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext"
