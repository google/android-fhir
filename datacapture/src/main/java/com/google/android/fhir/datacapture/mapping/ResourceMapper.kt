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

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport
import com.google.android.fhir.datacapture.createQuestionnaireResponseItem
import com.google.android.fhir.datacapture.targetStructureMap
import com.google.android.fhir.datacapture.utilities.SimpleWorkerContextProvider
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.Locale
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UrlType
import org.hl7.fhir.r4.utils.FHIRPathEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities

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
object ResourceMapper {

  /**
   * Extract a FHIR resource from the [questionnaire] and [questionnaireResponse].
   *
   * This method supports both Definition-based and StructureMap-based extraction.
   *
   * StructureMap-based extraction will be invoked if the [Questionnaire] declares a
   * targetStructureMap extension otherwise Definition-based extraction is used. StructureMap-based
   * extraction will fail and an empty [Bundle] will be returned if the [structureMapProvider] is
   * not passed.
   *
   * @return [Bundle] containing the extracted [Resource]s or empty Bundle if the extraction fails.
   * An exception might also be thrown in a few cases
   */
  fun extract(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    structureMapProvider: ((String) -> StructureMap?)? = null,
    context: Context? = null
  ): Bundle {
    return if (questionnaire.targetStructureMap == null)
      extractByDefinitions(questionnaire, questionnaireResponse)
    else extractByStructureMap(questionnaire, questionnaireResponse, structureMapProvider, context)
  }

  /**
   * Extracts a FHIR resource from the [questionnaire] and [questionnaireResponse] using the
   * definition-based extraction methodology.
   *
   * It currently only supports extracting a single resource. It returns a [Bundle] with the
   * extracted resource. If the process completely fails, an error is thrown or a [Bundle]
   * containing empty [Resource] is returned
   */
  private fun extractByDefinitions(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ): Bundle {
    val className = questionnaire.itemContextNameToExpressionMap.values.first()
    val extractedResource =
      (Class.forName("org.hl7.fhir.r4.model.$className").newInstance() as Resource).apply {
        extractFields(questionnaire.item, questionnaireResponse.item)
      }

    return Bundle().apply {
      type = Bundle.BundleType.TRANSACTION
      addEntry(Bundle.BundleEntryComponent().apply { resource = extractedResource })
    }
  }

  /**
   * Extracts a FHIR resource from the [questionnaire], [questionnaireResponse] and
   * [structureMapProvider] using the StructureMap-based extraction methodology.
   *
   * The [StructureMapProvider] implementation passed should fetch the referenced [StructureMap]
   * either from persistence or a remote service. The [StructureMap] should strictly return a
   * [Bundle], failure to this an exception will be thrown. If a [StructureMapProvider] is not
   * passed, an empty [Bundle] object is returned
   */
  private fun extractByStructureMap(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    structureMapProvider: ((String) -> StructureMap?)?,
    context: Context?
  ): Bundle {
    if (structureMapProvider == null || context == null) return Bundle()
    val structureMap = structureMapProvider(questionnaire.targetStructureMap!!) ?: return Bundle()
    val simpleWorkerContext = SimpleWorkerContextProvider.loadSimpleWorkerContext(context)
    simpleWorkerContext.setExpansionProfile(Parameters())

    return Bundle().apply {
      StructureMapUtilities(simpleWorkerContext)
        .transform(simpleWorkerContext, questionnaireResponse, structureMap, this)
    }
  }

  /**
   * Returns a `QuestionnaireResponse` to the [questionnaire] that is pre-filled from the [resource]
   * See http://build.fhir.org/ig/HL7/sdc/populate.html#expression-based-population.
   */
  fun populate(questionnaire: Questionnaire, resource: Resource): QuestionnaireResponse {
    populateInitialValues(questionnaire.item, resource)
    return QuestionnaireResponse().apply {
      item = questionnaire.item.map { it.createQuestionnaireResponseItem() }
    }
  }

  private fun populateInitialValues(
    questionnaireItems: List<Questionnaire.QuestionnaireItemComponent>,
    resource: Resource
  ) {
    questionnaireItems.forEach { populateInitialValue(it, resource) }
  }

  private fun populateInitialValue(
    question: Questionnaire.QuestionnaireItemComponent,
    resource: Resource
  ) {
    val context = FhirContext.forR4()
    val fhirPathEngine =
      FHIRPathEngine(HapiWorkerContext(context, DefaultProfileValidationSupport(context)))
    if (question.type == Questionnaire.QuestionnaireItemType.GROUP) {
      populateInitialValues(question.item, resource)
    } else {
      question.fetchExpression?.let { exp ->
        val answerExtracted = fhirPathEngine.evaluate(resource, exp.expression)[0] as Type
        question.initial =
          mutableListOf(Questionnaire.QuestionnaireItemInitialComponent().setValue(answerExtracted))
      }
    }
  }

  private val Questionnaire.QuestionnaireItemComponent.fetchExpression: Expression?
    get() {
      return this.extension.firstOrNull { it.url == ITEM_INITIAL_EXPRESSION_URL }?.let {
        it.value as Expression
      }
    }

  /**
   * Extracts answer values from [questionnaireResponseItemList] and updates the fields defined in
   * the corresponding questions in [questionnaireItemList]. This method handles nested fields.
   */
  private fun Base.extractFields(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ) {
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    while (questionnaireItemListIterator.hasNext() &&
      questionnaireResponseItemListIterator.hasNext()) {
      extractField(
        questionnaireItemListIterator.next(),
        questionnaireResponseItemListIterator.next()
      )
    }
  }

  /**
   * Extracts the answer value from [questionnaireResponseItem] and updates the field defined in
   * [questionnaireItem]. This method handles nested fields.
   */
  private fun Base.extractField(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent
  ) {
    if (questionnaireItem.definition == null) {
      extractFields(questionnaireItem.item, questionnaireResponseItem.item)
      return
    }

    val targetFieldName = questionnaireItem.definitionFieldName ?: return
    if (targetFieldName.isEmpty()) {
      return
    }

    val definitionField = questionnaireItem.getDefinitionField ?: return
    if (questionnaireItem.type == Questionnaire.QuestionnaireItemType.GROUP) {
      val value: Base =
        (definitionField.nonParameterizedType.newInstance() as Base).apply {
          extractFields(questionnaireItem.item, questionnaireResponseItem.item)
        }

      updateField(definitionField, value)
    } else {
      if (questionnaireResponseItem.answer.isEmpty()) return
      val answer = questionnaireResponseItem.answer.first().value

      if (!definitionField.nonParameterizedType.isEnum) {
        // this is a low level type e.g. StringType
        updateField(definitionField, answer)
      } else {
        // this is a high level type e.g. AdministrativeGender
        updateFieldWithEnum(definitionField, answer)
      }
    }
  }
}

/**
 * Updates a field of name [field.name] on this object with the generated enum from [value] using
 * the declared setter. [field] helps to determine the field class type. The enum is generated by
 * calling fromCode method on the enum class
 */
private fun Base.updateFieldWithEnum(field: Field, value: Base) {
  /**
   * We have a org.hl7.fhir.r4.model.Enumerations class which contains inner classes of code-names
   * and re-implements the classes in the org.hl7.fhir.r4.model.codesystems package The
   * inner-classes in the Enumerations package are valid and not dependent on the classes in the
   * codesystems package All enum classes in the org.hl7.fhir.r4.model package implement the
   * fromCode(), toCode() methods among others
   */
  val dataTypeClass: Class<*> = field.nonParameterizedType
  val fromCodeMethod: Method = dataTypeClass.getDeclaredMethod("fromCode", String::class.java)

  val stringValue = if (value is Coding) value.code else value.toString()

  javaClass
    .getMethod("set${field.name.capitalize(Locale.ROOT)}", field.nonParameterizedType)
    .invoke(this, fromCodeMethod.invoke(dataTypeClass, stringValue))
}

/**
 * Updates a field of name [field.name] on this object with the value from [value] using the
 * declared setter. [field] helps to determine the field class type.
 */
private fun Base.updateField(field: Field, value: Base) {
  val answerValue = generateAnswerWithCorrectType(value, field)

  try {
    javaClass
      .getMethod("set${field.name.capitalize(Locale.ROOT)}Element", field.type)
      .invoke(this, answerValue)
  } catch (e: NoSuchMethodException) {
    // some set methods expect a list of objects
    javaClass
      .getMethod("set${field.name.capitalize(Locale.ROOT)}", field.type)
      .invoke(this, if (field.isParameterized && field.isList) listOf(answerValue) else answerValue)
  }
}

/**
 * This method enables us to perform an extra step to wrap the answer using the correct type. This
 * is useful in cases where a single question maps to a CompositeType such as [CodeableConcept] or
 * enum. Normally, composite types are mapped using group questions which provide direct alignment
 * to the type elements in the group questions
 */
private fun generateAnswerWithCorrectType(answer: Base, fieldType: Field): Base {
  when (fieldType.nonParameterizedType) {
    CodeableConcept::class.java -> {
      if (answer is Coding) {
        return CodeableConcept(answer).apply { text = answer.display }
      }
    }
    IdType::class.java -> {
      if (answer is StringType) {
        return IdType(answer.value)
      }
    }
  }

  return answer
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
 * Extracts a list containing the resource name followed by field names leading to the destination
 * field defined in the [definition] field, or `null` if the [definition] field is empty or invalid.
 *
 * For example, if the [definition] field is
 * "http://hl7.org/fhir/StructureDefinition/Patient#Patient.name", `listOf("Patient", "name")` will
 * be returned.
 */
private val Questionnaire.QuestionnaireItemComponent.definitionPath: List<String>?
  get() {
    val snapshotPath = definition.substringAfter('#', "")
    if (!"[a-zA-Z]+(\\.[a-zA-Z]+)+".toRegex().matches(snapshotPath)) return null

    return snapshotPath.split(".")
  }

/**
 * Retrieves details about the target field defined in
 * [org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent.definition] for easier searching
 * of the setter methods and converting the answer to the expected parameter type.
 */
private val Questionnaire.QuestionnaireItemComponent.getDefinitionField: Field?
  get() {
    val path = definitionPath ?: return null
    if (path.size < 2) return null
    val resourceClass: Class<*> = Class.forName("org.hl7.fhir.r4.model.${path[0]}")
    val definitionField: Field = resourceClass.getFieldOrNull(path[1]) ?: return null

    return path.drop(2).fold(definitionField) { field: Field?, nestedFieldName: String ->
      field?.nonParameterizedType?.getFieldOrNull(nestedFieldName)
    }
  }

/**
 * See
 * [Extension: item extraction context](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * .
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext"

private const val ITEM_INITIAL_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression"

private val Field.isList: Boolean
  get() = isParameterized && type == List::class.java

private val Field.isParameterized: Boolean
  get() = genericType is ParameterizedType

/** The non-parameterized type of this field (e.g. `String` for a field of type `List<String>`). */
private val Field.nonParameterizedType: Class<*>
  get() =
    if (isParameterized) (genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
    else type

private fun Class<*>.getFieldOrNull(name: String): Field? {
  return try {
    getDeclaredField(name)
  } catch (ex: NoSuchFieldException) {
    return null
  }
}
