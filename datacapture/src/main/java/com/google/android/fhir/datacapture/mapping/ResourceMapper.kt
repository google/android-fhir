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

import com.google.android.fhir.datacapture.getValueForType
import java.util.Locale
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UrlType

/**
 * Maps [QuestionnaireResponse] s to FHIR resources and vice versa.
 *
 * The process of converting [QuestionnaireResponse] s to other FHIR resources is called
 * [extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html). The reverse process of converting
 * existing FHIR resources to [QuestionnaireResponse] s to be used to pre-fill the UI is called
 * [population](http://build.fhir.org/ig/HL7/sdc/populate.html).
 *
 * [Definition-based extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html#definition-based-extraction)
 * and
 * [expression-based population](http://build.fhir.org/ig/HL7/sdc/populate.html#expression-based-population)
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
  fun extract(questionnaire: Questionnaire, questionnaireResponse: QuestionnaireResponse): Base {
    return questionnaire
      .itemContextNameToExpressionMap
      .values
      .first()
      .let { Class.forName("org.hl7.fhir.r4.model.$it").newInstance() as Base }
      .apply { extractFields(questionnaire.item, questionnaireResponse.item) }
  }
}

/**
 * Extracts values for fields in the builder from the corresponding questions and answers in
 * [questionnaireItemList] and [questionnaireResponseItemList].
 */
private fun Base.extractFields(
  questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
  questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
) {
  val questionnaireItemListIterator = questionnaireItemList.iterator()
  val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
  while (questionnaireItemListIterator.hasNext() &&
    questionnaireResponseItemListIterator.hasNext()
  ) {
    val questionnaireItem = questionnaireItemListIterator.next()
    val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
    this.extractField(questionnaireItem, questionnaireResponseItem)
    extractFields(questionnaireItem.item, questionnaireResponseItem.item)
  }
}

/**
 * Extracts value for field in the builder from the corresponding question and answer in
 * [questionnaireItem] and [questionnaireResponseItem].
 *
 * NOTE: Nested fields are not handled. See https://github.com/google/android-fhir/issues/240.
 */
private fun Base.extractField(
  questionnaireItem: Questionnaire.QuestionnaireItemComponent,
  questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent
) {
  val targetFieldName = questionnaireItem.definitionFieldName ?: return
  if (targetFieldName.isEmpty()) {
    return
  }

  questionnaireItem.type.getClass()?.let {
    this.javaClass
      .getMethod("set${targetFieldName.capitalize(Locale.ROOT)}Element", it)
      .invoke(
        this, questionnaireResponseItem.answer.single().getValueForType(questionnaireItem.type)
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
private val Questionnaire.QuestionnaireItemComponent.definitionFieldName
  get() = this.definition?.substringAfterLast(".")

/**
 * Returns the [Class] for the answer to the [Questionnaire.QuestionnaireItemComponent].
 *
 * Used to retrieve the method to invoke to set the field in the extracted FHIR resource.
 */
private fun Questionnaire.QuestionnaireItemType.getClass(): Class<out Base>? =
  when (this) {
    Questionnaire.QuestionnaireItemType.DATE -> DateType::class.java
    Questionnaire.QuestionnaireItemType.BOOLEAN -> BooleanType::class.java
    Questionnaire.QuestionnaireItemType.DECIMAL -> DecimalType::class.java
    Questionnaire.QuestionnaireItemType.INTEGER -> IntegerType::class.java
    Questionnaire.QuestionnaireItemType.DATETIME -> DateTimeType::class.java
    Questionnaire.QuestionnaireItemType.TIME -> TimeType::class.java
    Questionnaire.QuestionnaireItemType.STRING, Questionnaire.QuestionnaireItemType.TEXT ->
      StringType::class.java
    Questionnaire.QuestionnaireItemType.URL -> UrlType::class.java
    else -> null
  }

/**
 * The map from the `name`s to `expression`s in the
 * [item extraction context extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * s.
 */
private val Questionnaire.itemContextNameToExpressionMap: Map<String, String>
  get() {
    return this.extension
      .filter { it.url == ITEM_CONTEXT_EXTENSION_URL }
      .map {
        val expression = it.value as Expression
        expression.name to expression.expression
      }
      .toMap()
  }

/**
 * See
 * [Extension: item extraction context](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * .
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext"
