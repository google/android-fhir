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
import android.os.Environment
import android.util.Log
import com.google.android.fhir.datacapture.utilities.TarGzipUtility
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import org.apache.commons.compress.utils.IOUtils
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceFactory
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UrlType
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.hl7.fhir.utilities.npm.NpmPackage

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
   *
   * NpmPackage containing all the [org.hl7.fhir.r4.model.StructureDefinition]s takes around 20
   * seconds to load. Therefore, reloading for each extraction is not desirable. This should happen
   * once and cache the variable throughout the app's lifecycle.
   *
   * Call [loadNpmPackage] to load it. The method handles skips the operation if it's already
   * loaded.
   */
  lateinit var npmPackage: NpmPackage

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
    structureMapProvider: StructureMapProvider? = null,
    context: Context? = null
  ): Bundle {
    return if (questionnaire.targetStructureMap == null)
      extractByDefinitionBased(questionnaire, questionnaireResponse)
    else
      extractByStructureMapBased(
        questionnaire,
        questionnaireResponse,
        structureMapProvider,
        context
      )
  }

  /**
   * Extracts a FHIR resource from the [questionnaire] and [questionnaireResponse] using the
   * definition-based extraction methodology.
   *
   * It currently only supports extracting a single resource. It returns a [Bundle] with the
   * extracted resource. If the process completely fails, an error is thrown or a [Bundle]
   * containing empty [Resource] is returned
   */
  private fun extractByDefinitionBased(
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
   *
   * @return [Bundle] containing the extracted [Resource]s
   */
  private fun extractByStructureMapBased(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    structureMapProvider: StructureMapProvider?,
    context: Context?
  ): Bundle {
    if (structureMapProvider == null || context == null) return Bundle()

    // Load the npm package
    loadNpmPackage(context)

    val contextR4 =
      SimpleWorkerContext.fromPackage(npmPackage).apply { isCanRunWithoutTerminology = true }

    val structureMapUrl = questionnaire.targetStructureMap!!
    val structureMap = structureMapProvider.getStructureMap(structureMapUrl)

    val structureMapUtilities = StructureMapUtilities(contextR4)
    val targetResource: Resource =
      ResourceFactory.createResource(structureMapUtilities.getTargetType(structureMap).name)
    structureMapUtilities.transform(contextR4, questionnaireResponse, structureMap, targetResource)
    return targetResource as Bundle
  }

  /**
   * Decompresses the hl7.fhir.r4.core archived package into app storage and loads it into memory.
   * It loads the package into [npmPackage]. The method skips any unnecessary operations. This
   * method can be called during initial app installation and run in the background so as to reduce
   * the time it takes for the whole process.
   *
   * The whole process can take 1-3 minutes on a clean installation.
   */
  fun loadNpmPackage(context: Context): NpmPackage {
    setupNpmPackage(context)

    if (!this::npmPackage.isInitialized) {
      npmPackage = NpmPackage.fromFolder(getLocalFhirCorePackageDirectory(context))
    }

    return npmPackage
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
    .getMethod("set${field.name.capitalize()}", field.nonParameterizedType)
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
      .getMethod("set${field.name.capitalize()}Element", field.type)
      .invoke(this, answerValue)
  } catch (e: NoSuchMethodException) {
    // some set methods expect a list of objects
    javaClass
      .getMethod("set${field.name.capitalize()}", field.type)
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
 * The map from the `name`s to `expression`s in the
 * [item extraction context extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * s.
 */
val Questionnaire.targetStructureMap: String?
  get() {
    val extensionValue =
      this.extension.singleOrNull { it.url == TARGET_STRUCTURE_MAP }?.value ?: return null
    return if (extensionValue is CanonicalType) extensionValue.valueAsString else null
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
    val field: Field = resourceClass.getFieldOrNull(path[1]) ?: return null

    return path.drop(2).fold(field) { field: Field?, nestedFieldName: String ->
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
/**
 * See
 * [Extension: target structure map](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
 * .
 */
private const val TARGET_STRUCTURE_MAP: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap"

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

private fun setupNpmPackage(context: Context) {
  val filename = "packages.fhir.org-hl7.fhir.r4.core-4.0.1.tgz"
  val outDir = getLocalFhirCorePackageDirectory(context)

  if (File(outDir + "/package/package.json").exists()) {
    return
  }
  // Create any missing folders
  File(outDir).mkdirs()

  // Copy the tgz package to private app storage
  try {
    val inputStream = context.assets.open(filename)
    val outputStream = FileOutputStream(File(getLocalNpmPackagesDirectory(context) + filename))

    IOUtils.copy(inputStream, outputStream)
    IOUtils.closeQuietly(inputStream)
    IOUtils.closeQuietly(outputStream)
  } catch (e: IOException) {
    // Delete the folders
    val packageDirectory = File(outDir)
    if (packageDirectory.exists()) {
      packageDirectory.delete()
    }

    Log.e(ResourceMapper::class.java.name, e.stackTraceToString())
    throw NpmPackageInitializationError(
      "Could not copy archived package [${filename}] to app private storage",
      e
    )
  }

  // decompress the .tgz package
  TarGzipUtility.decompress(getLocalNpmPackagesDirectory(context) + filename, File(outDir))
}

/** Generate the path to the local npm packages directory */
private fun getLocalNpmPackagesDirectory(context: Context): String {
  val outDir =
    Environment.getDataDirectory().getAbsolutePath() +
      "/data/${context.applicationContext.packageName}/npm_packages/"
  return outDir
}

/** Generate the path to the local hl7.fhir.r4.core package */
private fun getLocalFhirCorePackageDirectory(context: Context): String {
  return getLocalNpmPackagesDirectory(context) + "hl7.fhir.r4.core#4.0.1"
}

interface StructureMapProvider {

  fun getStructureMap(fullUrl: String): StructureMap?
}
