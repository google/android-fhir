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

import android.text.TextUtils
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
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
object ResourceMapper {

  /**
   * Extract a FHIR resource from the `questionnaire` and `questionnaireResponse`.
   *
   * This method assumes there is only one FHIR resource to be extracted from the given
   * `questionnaire` and `questionnaireResponse`.
   */
  fun extract(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ): Resource {
    val className = questionnaire.itemContextNameToExpressionMap.values.first()
    val resource = Class.forName("org.hl7.fhir.r4.model.$className").newInstance() as Resource

    var questionnaireItemListIterator = questionnaire.item.iterator()
    var questionnaireResponseItemListIterator = questionnaireResponse.item.iterator()
    while (questionnaireItemListIterator.hasNext() &&
      questionnaireResponseItemListIterator.hasNext()) {
      val questionnaireItem = questionnaireItemListIterator.next()
      val questionnaireResponseItem = questionnaireResponseItemListIterator.next()

      if (questionnaireItem.definition == null) {
        questionnaireItemListIterator = questionnaireItem.item.iterator()
        questionnaireResponseItemListIterator = questionnaireResponseItem.item.iterator()
        continue
      }

      val targetFieldName = questionnaireItem.definition.substringAfterLast(".")
      if (targetFieldName.isEmpty()) {
        questionnaireItemListIterator = questionnaireItem.item.iterator()
        questionnaireResponseItemListIterator = questionnaireResponseItem.item.iterator()
        continue
      }

      if (questionnaireItem.type == Questionnaire.QuestionnaireItemType.GROUP) {
        // create a class for questionnaire item of type group and add to the resource
        val propertyType = questionnaireItem.inferPropertyResourceClass
        if (propertyType != null) {
          val innerClass: Class<*> = Class.forName("${propertyType.name}")
          val type: Type = innerClass.newInstance() as Type

          createInnerClassObject(type, questionnaireItem.item, questionnaireResponseItem.item)

          /*
          TODO: Update the methods to use add${targetFieldName} also for cases where the propertyType is
            parameterized. For cases where it's not, we should strictly use set${targetFieldName}Element
            or set${targetFieldName}
           */
          updateTypeOrResourceWithAnswer(resource, type, targetFieldName, propertyType)
          continue
        }
      }

      // get answer from questionnaireResponse or from initial value in questionnaire
      val ans = extractQuestionAnswer(questionnaireResponseItem, questionnaireItem) ?: continue

      var propertyType = questionnaireItem.inferPropertyResourceClass

      /*
      We have a org.hl7.fhir.r4.model.Enumerations class which contains inner classes of code-names and
      re-implements the classes in the org.hl7.fhir.r4.model.codesystems package
      The inner-classes in the Enumerations package are valid and not dependent on the classes in the codesystems package
      All enum classes in the org.hl7.fhir.r4.model package implement the fromCode(), toCode() methods among others
       */
      if (propertyType != null) {
        if (!propertyType.mainType.isEnum()) {
          // this is a low level type e.g. StringType
          updateTypeOrResourceWithAnswer(resource, ans, targetFieldName, propertyType)
        } else {
          // this is a high level type e.g. AdministrativeGender
          val dataTypeClass: Class<*> = Class.forName(propertyType.name)
          val fromCodeMethod: Method =
            dataTypeClass.getDeclaredMethod("fromCode", String::class.java)

          val stringValue = if (ans is Coding) ans.code else ans.toString()

          resource
            .javaClass
            .getMethod("set${targetFieldName.capitalize()}", Class.forName(propertyType.name))
            .invoke(resource, fromCodeMethod.invoke(dataTypeClass, stringValue))
        }
      }
    }
    return resource
  }
}

/**
 * Creates an object of Type and Extracts values for fields from the corresponding questions and
 * answers in [questionnaireItemList] and [questionnaireResponseItemList] and invokes setters on the
 * object.
 */
private fun createInnerClassObject(
  type: Type,
  questionnaireItemComponent: MutableList<Questionnaire.QuestionnaireItemComponent>,
  questionnaireResponseItemComponent:
    MutableList<QuestionnaireResponse.QuestionnaireResponseItemComponent>
) {
  val questionnaireItemListIterator = questionnaireItemComponent.iterator()
  val questionnaireResponseItemListIterator = questionnaireResponseItemComponent.iterator()

  while (questionnaireItemListIterator.hasNext() &&
    questionnaireResponseItemListIterator.hasNext()) {
    val questionnaireItem = questionnaireItemListIterator.next()
    val questionnaireResponseItem = questionnaireResponseItemListIterator.next()

    val targetFieldName = questionnaireItem.definition.substringAfterLast(".")

    // get answer from questionnaireResponse or from initial value in questionnaire
    val answer = extractQuestionAnswer(questionnaireResponseItem, questionnaireItem) ?: continue

    val propertyType = questionnaireItem.inferPropertyResourceClass
    if (propertyType != null) {
      if (!propertyType.mainType.isEnum()) {
        // call the set methods by providing the low level data types: StringType, DateType etc

        // TODO: Implement searching for the method based on the field type
        updateTypeOrResourceWithAnswer(type, answer, targetFieldName, propertyType)
      } else {
        // call the set methods by providing data type defined defined for the field e.g.
        // ContactPointSystem
        val dataTypeClass: Class<*> = Class.forName(propertyType.name)
        val fromCodeMethod: Method = dataTypeClass.getDeclaredMethod("fromCode", String::class.java)

        type
          .javaClass
          .getMethod("set${targetFieldName.capitalize()}", Class.forName(propertyType.name))
          .invoke(type, fromCodeMethod.invoke(dataTypeClass, answer.toString()))
      }
    }
  }
}

private fun updateTypeOrResourceWithAnswer(
  typeOrResource: Base,
  answer: Type,
  targetFieldName: String,
  fieldType: FieldType
) {
  val paramAns = generateAnswerWithCorrectType(answer, fieldType)

  try {
    typeOrResource
      .javaClass
      .getMethod("set${targetFieldName.capitalize()}Element", fieldType.getMethodParam())
      .invoke(typeOrResource, paramAns)
  } catch (e: NoSuchMethodException) {
    // some set methods expect a list of objects
    /*
    TODO: Eliminate dependence on code to convert a single item to list
      - But depend on the parameterized type
      - Use addField method where the answer is single i.e. does not match the collection type
     */
    typeOrResource
      .javaClass
      .getMethod("set${targetFieldName.capitalize()}", fieldType.getMethodParam())
      .invoke(
        typeOrResource,
        if (fieldType.isParameterized() && fieldType.isList()) listOf(paramAns) else paramAns
      )
  }
}

/**
 * This method enables us to perform an extra step to wrap the answer using the correct
 * type. This is useful in cases where a single question maps to a CompositeType such as CodeableConcept.
 * Normally, composite types are mapped using group questions which provide direct alignment
 * to the type elements in the group questions
 */
fun generateAnswerWithCorrectType(answer: Type, fieldType: FieldType): Type {
  when (fieldType.mainType) {
    CodeableConcept::class.java -> {
      if (answer is Coding) {
        val codeableConcept = CodeableConcept(answer)
        codeableConcept.setText(answer.display)

        return codeableConcept
      }
    }
  }

  return answer
}

private fun extractQuestionAnswer(
  questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent
) =
  if (!questionnaireResponseItem.answer.isEmpty()) questionnaireResponseItem.answer.first().value
  else if (!questionnaireItem.initial.isEmpty()) questionnaireItem.initial.first().value else null

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

private val Questionnaire.QuestionnaireItemComponent.inferPropertyResourceClass: FieldType?
  get() {
    val pathParts = this.definition.split("#")
    if (pathParts.size >= 2) {
      val modelAndField = pathParts[1].split(".")
      if (modelAndField.size >= 2 &&
          !TextUtils.isEmpty(modelAndField[0]) &&
          !TextUtils.isEmpty(modelAndField[1])
      ) {
        var currentClass: Class<*> = Class.forName("org.hl7.fhir.r4.model.${modelAndField.get(0)}")
        var parameterizedClass: Class<*>? = null

        for (i in 1 until modelAndField.size) {
          val declaredFields = currentClass.declaredFields
          for (declaredField in declaredFields) if (declaredField.name.equals(modelAndField[i])) {
            if (declaredField.genericType is ParameterizedType) {
              currentClass =
                (declaredField.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
              parameterizedClass = declaredField.type
            } else {
              currentClass = declaredField.type
              parameterizedClass = null
            }

            break
          }
        }

        return FieldType(currentClass, parameterizedClass, currentClass.name)
      }
    }

    return null
  }

/**
 * See
 * [Extension: item extraction context](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * .
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext"

data class FieldType(val mainType: Class<*>, val parameterizedType: Class<*>?, val name: String)

fun FieldType.isList(): Boolean = parameterizedType?.simpleName.equals(List::class.java.simpleName)

fun FieldType.isEnumeration(): Boolean =
  parameterizedType?.name.equals(Enumeration<*>::javaClass.name) // Enumeration::class.java.name

fun FieldType.isParameterized(): Boolean = parameterizedType != null

fun FieldType.getMethodParam(): Class<*> = if (isParameterized()) parameterizedType!! else mainType
