// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.fhirengine.index.impl

import android.util.Log
import ca.uhn.fhir.model.api.annotation.SearchParamDefinition
import com.google.fhirengine.index.*
import org.hl7.fhir.r4.model.*
import java.util.*
import javax.inject.Inject

/** Implementation of [FhirIndexer].  */
class FhirIndexerImpl @Inject constructor() : FhirIndexer {
    override fun <R : Resource> index(resource: R): ResourceIndices {
        return extractIndexValues(resource)
    }

    /** Extracts the values to be indexed for `resource`.  */
    private fun <R : Resource> extractIndexValues(resource: R): ResourceIndices {
        val resourceIndices = ResourceIndices(resource.resourceType, resource.id)
        resource.javaClass.fields.forEach { f ->
            val searchParamDefinition = f.getAnnotation(SearchParamDefinition::class.java)
            if (searchParamDefinition != null) {
                val path: String = searchParamDefinition.path
                if (path.hasDotNotationOnly()) {
                    when(searchParamDefinition.type) {
                        SEARCH_PARAM_DEFINITION_TYPE_STRING -> {
                            getStringValues(getValuesForPath(resource, path)).forEach { value ->
                                resourceIndices.addStringIndex(StringIndex(
                                        name = searchParamDefinition.name,
                                        path = searchParamDefinition.path,
                                        value = value
                                ))
                            }
                        }
                        SEARCH_PARAM_DEFINITION_TYPE_REFERENCE -> {
                            getReferenceValues(getValuesForPath(resource, path)).forEach { reference ->
                                reference.reference?.let { referenceValue ->
                                    if (referenceValue.isNotEmpty()) {
                                        resourceIndices.addReferenceIndex(ReferenceIndex(
                                                name = searchParamDefinition.name,
                                                path = searchParamDefinition.path,
                                                value = referenceValue
                                        ))
                                    }
                                }
                            }
                        }
                        SEARCH_PARAM_DEFINITION_TYPE_CODE -> {
                            getCodeValues(getValuesForPath(resource, path)).forEach { code ->
                                val system = code.system
                                val value = code.code
                                if (system?.isNotEmpty() == true && value?.isNotEmpty() == true) {
                                    resourceIndices.addCodeIndex(CodeIndex(
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
            }
        }
        return resourceIndices
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
    private fun getStringValues(objects: List<Any?>?): List<String> {
        return objects?.mapNotNull {
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
        } ?: emptyList()
    }

    /** Returns the reference values for the list of `objects`.  */
    private fun getReferenceValues(objects: List<Any?>?): List<Reference> {
        return objects?.filterIsInstance(Reference::class.java) ?: emptyList()
    }

    /** Returns the code values for the list of `objects`.  */
    private fun getCodeValues(objects: List<Any?>?): List<Coding> {
        return objects?.flatMap {
            if (it is CodeableConcept) {
                it.coding
            } else {
                emptyList()
            }
        } ?: emptyList()
    }

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
        /** Returns the list of values corresponding to the `path` in the `resource`.  */
        private fun getValuesForPath(resource: Resource, path: String): List<Any>? {
            val paths = path.split(SEPARATOR_REGEX).toTypedArray()
            if (paths.size <= 1) {
                return null
            }
            var objects: List<Any> = listOf(resource)
            for (i in 1 until paths.size) {
                objects = getFieldValues(objects, paths[i])
            }
            return objects
        }

        /**
         * Returns the list of field values for `fieldName` in each of the `objects`.
         *
         *
         * If the field is a [Collection], it will be expanded and each element of the [Collection]
         * will be added to the returned value.
         */
        private fun getFieldValues(objects: List<Any?>, fieldName: String): List<Any> {
            val fieldValues: MutableList<Any> = ArrayList()
            objects.mapNotNull {
                val value = try {
                    it?.javaClass?.getMethod(getGetterName(fieldName))?.invoke(it)
                } catch (error : Throwable) {
                    Log.w(TAG, error)
                    null
                }
                if (value is Collection<*>) {
                    value.forEach { subValue ->
                        if (subValue != null) {
                            fieldValues.add(subValue)
                        }
                    }
                } else if (value != null){
                    fieldValues.add(value)
                }
            }
            return fieldValues
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
    }
}
