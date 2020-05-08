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
import com.google.fhirengine.index.FhirIndexer
import com.google.fhirengine.index.ReferenceIndex
import com.google.fhirengine.index.ResourceIndices
import com.google.fhirengine.index.StringIndex
import java.util.Locale
import javax.inject.Inject
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType

/** Implementation of [FhirIndexer].  */
internal class FhirIndexerImpl @Inject constructor() : FhirIndexer {
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
                    resource.valuesForPath(searchParamDefinition).referenceValues().forEach { reference ->
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
                // TODO: Implement number, date, token, reference, composite, quantity, URI,
                //  and special search parameter types.
            }
        }
        return indexBuilder.build()
    }

    /**
     * Returns the representative string values for the list of `objects`.
     *
     *
     * If an object in the list is a Java [String], the returned list will contain the value of
     * the Java [String]. If an object in the list is a FHIR [StringType], the returned
     * list will contain the value of the FHIR [StringType]. If an object in the list matches a
     * server defined search type (HumanName, Address, etc), the returned list will contain the string
     * value representative of the type.
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
                    //  https://www.hl7.org/fhir/searchparameter-registry.html, name, device name, and
                    //  address are defined by the server (the FHIR Engine library in this case).
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

    /** Returns the list of values corresponding to the `path` in the `resource`.  */
    private fun Resource.valuesForPath(definition: SearchParamDefinition): Sequence<Any> {
        val paths = definition.path.split(SEPARATOR_REGEX)
        if (paths.size <= 1) {
            return emptySequence()
        }
        return paths.asSequence().drop(1).fold(sequenceOf<Any>(this)) { acc, next ->
            getFieldValues(acc, next)
        }
    }

    /**
     * Returns the list of field values for `fieldName` in each of the `objects`.
     *
     * If the field is a [Collection], it will be expanded and each element of the [Collection]
     * will be added to the returned value.
     */
    private fun getFieldValues(objects: Sequence<Any>, fieldName: String): Sequence<Any> {
        return objects.asSequence().flatMap {
            val value = try {
                it.javaClass.getMethod(getGetterName(fieldName)).invoke(it)
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
    private fun getGetterName(fieldName: String): String {
        // TODO replace w/ capitalize once the localized version of it is not experimental
        return GETTER_PREFIX +
                fieldName.substring(0, 1).toUpperCase(Locale.US) +
                fieldName.substring(1)
    }

    /**
     * Returns whether the given path only uses a dot notation with no additional expressions such as
     * where() or exists().
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun String.hasDotNotationOnly() = matches(DOT_NOTATION_REGEX)

    companion object {
        /** The prefix of getter methods for retrieving field values.  */
        private const val GETTER_PREFIX = "get"
        /** The regular expression for the separator  */
        private val SEPARATOR_REGEX = "\\.".toRegex()
        /** The string representing the string search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_STRING = "string"
        /** The string representing the reference search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_REFERENCE = "reference"
        /** The string representing the code search parameter type.  */
        private const val SEARCH_PARAM_DEFINITION_TYPE_CODE = "token"
        /** Tag for logging.  */
        private const val TAG = "FhirIndexerImpl"
        private val DOT_NOTATION_REGEX = "^[a-zA-Z0-9.]+$".toRegex()
    }
}
