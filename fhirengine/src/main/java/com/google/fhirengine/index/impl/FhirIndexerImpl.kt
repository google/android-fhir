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

package com.google.fhirengine.index.impl

import android.util.Log
import ca.uhn.fhir.model.api.annotation.SearchParamDefinition
import com.google.fhirengine.index.CodeIndex
import com.google.fhirengine.index.DateIndex
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.index.NumberIndex
import com.google.fhirengine.index.QuantityIndex
import com.google.fhirengine.index.ReferenceIndex
import com.google.fhirengine.index.ResourceIndices
import com.google.fhirengine.index.StringIndex
import com.google.fhirengine.index.UriIndex
import java.math.BigDecimal
import java.util.Locale
import org.hl7.fhir.instance.model.api.IBaseDatatype
import org.hl7.fhir.r4.model.BaseDateTimeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Money
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Ratio
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.UriType

/** Implementation of [FhirIndexer].  */
internal class FhirIndexerImpl constructor() : FhirIndexer {
    override fun <R : Resource> index(resource: R): ResourceIndices {
        return extractIndexValues(resource)
    }

    /** Extracts the values to be indexed for `resource`.  */
    private fun <R : Resource> extractIndexValues(resource: R): ResourceIndices {
        val indexBuilder = ResourceIndices.Builder(resource.resourceType, resource.id)
        resource.javaClass.fields.asSequence().mapNotNull {
            it.getAnnotation(SearchParamDefinition::class.java)
        }.filter {
            it.path.hasDotNotationOnly()
        }.forEach { searchParamDefinition ->
            when (searchParamDefinition.type) {
                SEARCH_PARAM_DEFINITION_TYPE_STRING -> {
                    resource.valuesForPath(searchParamDefinition).stringValues().forEach { value ->
                        indexBuilder.addStringIndex(StringIndex(
                                name = searchParamDefinition.name,
                                path = searchParamDefinition.path,
                                value = value
                        ))
                    }
                }
                SEARCH_PARAM_DEFINITION_TYPE_REFERENCE -> {
                    resource.valuesForPath(searchParamDefinition)
                            .referenceValues()
                            .forEach { reference ->
                                if (reference.reference?.isNotEmpty() == true) {
                                    indexBuilder.addReferenceIndex(ReferenceIndex(
                                            name = searchParamDefinition.name,
                                            path = searchParamDefinition.path,
                                            value = reference.reference
                                    ))
                                }
                            }
                }
                SEARCH_PARAM_DEFINITION_TYPE_CODE -> {
                    resource.valuesForPath(searchParamDefinition).codeValues().forEach { code ->
                        val system = code.system
                        val value = code.code
                        if (system?.isNotEmpty() == true && value?.isNotEmpty() == true) {
                            indexBuilder.addCodeIndex(CodeIndex(
                                    name = searchParamDefinition.name,
                                    path = searchParamDefinition.path,
                                    system = system,
                                    value = value
                            ))
                        }
                    }
                }
                SEARCH_PARAM_DEFINITION_TYPE_QUANTITY -> {
                    resource.valuesForPath(searchParamDefinition)
                            .quantityValues()
                            .forEach { quantity ->

                        val system: String
                        val unit: String
                        val value: BigDecimal

                        if (quantity is Quantity) {
                            system = quantity.system
                            unit = quantity.unit
                            value = quantity.value
                        } else if (quantity is Money) {
                            system = FHIR_CURRENCY_SYSTEM
                            unit = quantity.currency
                            value = quantity.value
                        } else {
                            throw IllegalArgumentException(
                                    "$quantity is of unknown type ${quantity.javaClass.simpleName}")
                        }

                        indexBuilder.addQuantityIndex(QuantityIndex(
                                name = searchParamDefinition.name,
                                path = searchParamDefinition.path,
                                system = system,
                                unit = unit,
                                value = value
                        ))
                    }
                }
                SEARCH_PARAM_DEFINITION_TYPE_URI -> {
                    resource.valuesForPath(searchParamDefinition)
                            .uriValues()
                            .forEach { uri ->
                                indexBuilder.addUriIndex(UriIndex(
                                        name = searchParamDefinition.name,
                                        path = searchParamDefinition.path,
                                        uri = uri
                                ))
                            }
                }
                SEARCH_PARAM_DEFINITION_TYPE_DATE -> {
                    resource.valuesForPath(searchParamDefinition).dateValues().forEach { date ->
                        indexBuilder.addDateIndex(DateIndex(
                                name = searchParamDefinition.name,
                                path = searchParamDefinition.path,
                                tsHigh = date.value.time,
                                tsLow = date.value.time,
                                temporalPrecision = date.precision))
                    }
                }
                SEARCH_PARAM_DEFINITION_TYPE_NUMBER -> {
                    resource.valuesForPath(searchParamDefinition).numberValues().forEach { number ->
                        indexBuilder.addNumberIndex(NumberIndex(
                                name = searchParamDefinition.name,
                                path = searchParamDefinition.path,
                                value = number))
                    }
                }

                // TODO: Implement token, composite and special search parameter types.
            }
        }
        // For all resources,
        // add 'last updated' timestamp to date index
        if (resource.meta.hasLastUpdated()) {
            val lastUpdatedElement = resource.meta.lastUpdatedElement
            indexBuilder.addDateIndex(DateIndex(
                    name = "lastUpdated",
                    path = arrayOf(resource.fhirType(), "meta", "lastUpdated")
                            .joinToString(separator = "."),
                    tsHigh = lastUpdatedElement.value.time,
                    tsLow = lastUpdatedElement.value.time,
                    temporalPrecision = lastUpdatedElement.precision
            ))
        }
        return indexBuilder.build()
    }

    /**
     * Returns the representative string values for the list of `objects`.
     *
     * If an object in the list is a Java [String], the returned list will contain the value of
     * the Java [String]. If an object in the list is a FHIR [StringType], the returned
     * list will contain the value of the FHIR [StringType]. If an object in the list matches a
     * server defined search type (HumanName, Address, etc), the returned list will contain the
     * string value representative of the type.
     */
    private fun Sequence<Any>.stringValues(): Sequence<String> {
        return mapNotNull {
            when (it) {
                is String -> {
                    it
                }
                is StringType -> {
                    it.value
                }
                else -> {
                    // TODO: Implement the server defined search parameters. According to
                    //  https://www.hl7.org/fhir/searchparameter-registry.html, name, device name,
                    //  and address are defined by the server
                    //  (the FHIR Engine library in this case).
                    null
                }
            }
        }
    }

    /** Returns the reference values for the list of `objects`.  */
    private fun Sequence<Any>.referenceValues(): Sequence<Reference> {
        return filterIsInstance(Reference::class.java)
    }

    /** Returns the code values for the list of `objects`.  */
    private fun Sequence<Any>.codeValues(): Sequence<Coding> {
        return flatMap {
            if (it is CodeableConcept) {
                it.coding.asSequence()
            } else {
                emptySequence()
            }
        }
    }

    /** Returns the quantity values for the list of `objects`.  */
    private fun Sequence<Any>.quantityValues(): Sequence<IBaseDatatype> {
        return flatMap {
            when (it) {
                is Money -> sequenceOf(it).filter { it.hasCurrency() }
                is Quantity -> sequenceOf(it).filter { it.hasSystem() && it.hasCode() }
                is Range -> sequenceOf(it.low, it.high).filter { it.hasSystem() && it.hasCode() }
                is Ratio -> sequenceOf(it.numerator, it.denominator)
                        .filter { it.hasSystem() && it.hasCode() }
                // TODO: Find other FHIR datatypes types the "quantity" type maps to.
                //  See: http://hl7.org/fhir/datatypes.html#quantity
                // TODO: Add tests for Range and Ratio types
                else -> emptySequence()
            }
        }
    }

    /** Returns the uri values for the list of `objects`.  */
    private fun Sequence<Any>.uriValues(): Sequence<String> {
        return flatMap {
            when (it) {
                is UriType -> sequenceOf(it.value)
                is String -> sequenceOf(it)
                else -> emptySequence()
            }
        }
    }

    /** Returns the Date values for a list of `objects`. */
    private fun Sequence<Any>.dateValues(): Sequence<BaseDateTimeType> {
        return flatMap {
            /** BaseDateTimeType wraps around [java.util.Date] which is what we use to extract the
             * timestamp. Some implementations return the timestamp in local (device) timezone.
             * Additionally, time zones are likely to be missing from health data. Date indexing
             * is a work in progress.
             */
            if (it is BaseDateTimeType) {
                sequenceOf(it)
            } else {
                emptySequence()
            }
        }.filter { it.value != null }
    }

    /** Returns the number values for a list of `objects`. */
    private fun Sequence<Any>.numberValues(): Sequence<BigDecimal> {
        return flatMap {
            when {
                it is Integer -> sequenceOf(it.toInt().toBigDecimal())
                it is BigDecimal -> sequenceOf(it)
                else -> emptySequence()
            }
        }.filterNotNull()
    }

    /** Returns the list of values corresponding to the `path` in the `resource`.  */
    private fun Resource.valuesForPath(definition: SearchParamDefinition): Sequence<Any> {
        val paths = definition.path.split(SEPARATOR_REGEX)
        if (paths.size <= 1) {
            return emptySequence()
        }
        return paths.asSequence().drop(1).fold(sequenceOf<Any>(this)) { acc, next ->
            getFieldValues(acc, next, definition.type)
        }
    }

    /**
     * Returns the list of field values for `fieldName` in each of the `objects`.
     *
     * If the field is a [Collection], it will be expanded and each element of the [Collection]
     * will be added to the returned value.
     */
    private fun getFieldValues(
      objects: Sequence<Any>,
      fieldName: String,
      type: String
    ): Sequence<Any> {
        return objects.asSequence().flatMap {
            val value = try {
                /* TODO
                 * Upstream HAPI FHIR returns FHIR date* types from getxxDate methods as
                 * java.util.Date. For HAPI FHIR BaseDateTimeTypes, which support TimeZones
                 * we need to invoke() getxxDateElement. Hence we need to pass in search parameter
                 * type to getGetterName below.
                 */
                it.javaClass.getMethod(getGetterName(fieldName, type)).invoke(it)
            } catch (error: Throwable) {
                Log.w(TAG, error)
                null
            }
            if (value is Collection<*>) {
                value.asSequence()
            } else {
                sequenceOf(value)
            }
        }.filterNotNull()
    }

    /** Returns the name of the method to retrieve the field `fieldName`.  */
    private fun getGetterName(fieldName: String, type: String): String {
        val baseGetter = GETTER_PREFIX +
                fieldName.substring(0, 1).toUpperCase(Locale.US) +
                fieldName.substring(1)
        when (type) {
            "date" -> return baseGetter + GETTER_SUFFIX_DATE
            else -> return baseGetter
        }
    }

    /**
     * Returns whether the given path only uses a dot notation with no additional expressions such
     * as where() or exists().
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.hasDotNotationOnly() = matches(DOT_NOTATION_REGEX)

    companion object {
        /** The prefix of getter methods for retrieving field values.  */
        private const val GETTER_PREFIX = "get"
        /** The suffix of getter methods for retrieving a date 'Element'.  */
        private const val GETTER_SUFFIX_DATE = "Element"
        /** The regular expression for the separator  */
        private val SEPARATOR_REGEX = "\\.".toRegex()
        /** The string representing the string search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_STRING = "string"
        /** The string representing the reference search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_REFERENCE = "reference"
        /** The string representing the code search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_CODE = "token"
        /** The string representing the quantity search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_QUANTITY = "quantity"
        /** The string representing the uri search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_URI = "uri"
        /** The string representing the date search parameter type. */
        private const val SEARCH_PARAM_DEFINITION_TYPE_DATE = "date"
        /** The string representing the number search parameter type. */
        private const val SEARCH_PARAM_DEFINITION_TYPE_NUMBER = "number"

        /** The string for FHIR currency system */
        // See: https://bit.ly/30YB3ML
        // See: https://www.hl7.org/fhir/valueset-currencies.html
        private const val FHIR_CURRENCY_SYSTEM = "urn:iso:std:iso:4217"

        /** Tag for logging.  */
        private const val TAG = "FhirIndexerImpl"
        private val DOT_NOTATION_REGEX = "^[a-zA-Z0-9.]+$".toRegex()
    }
}
