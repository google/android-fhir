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

package com.google.android.fhir.index

import com.google.android.fhir.FhirAdapter
import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.DateTimeIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import com.google.android.fhir.logicalId
import com.google.android.fhir.resourceType
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.instance.model.api.IBase

/**
 * Indexes a FHIR resource according to the
 * [search parameters](https://www.hl7.org/fhir/searchparameter-registry.html).
 */
internal object ResourceIndexer {

  private lateinit var fhirAdapter: FhirAdapter

  fun <R : IAnyResource> index(resource: R, fhirAdapter: FhirAdapter): ResourceIndices {
    this.fhirAdapter = fhirAdapter
    return extractIndexValues(resource)
  }

  private fun <R : IAnyResource> extractIndexValues(resource: R): ResourceIndices {
    val indexBuilder = ResourceIndices.Builder(resource.resourceType, resource.logicalId)
    getSearchParamList(resource.resourceType)
      .map { it to fhirAdapter.evaluateFunction(resource, it.path) }
      .flatMap { pair -> pair.second.map { pair.first to it } }
      .forEach { pair ->
        val (searchParam, value) = pair
        when (pair.first.type) {
          SearchParamType.NUMBER ->
            numberIndex(searchParam, value)?.also { indexBuilder.addNumberIndex(it) }
          SearchParamType.DATE ->
            if (value.fhirType() == "date") {
              dateIndex(searchParam, value).also { indexBuilder.addDateIndex(it) }
            } else {
              dateTimeIndex(searchParam, value)?.also { indexBuilder.addDateTimeIndex(it) }
            }
          SearchParamType.STRING ->
            stringIndex(searchParam, value)?.also { indexBuilder.addStringIndex(it) }
          SearchParamType.TOKEN ->
            tokenIndex(searchParam, value).forEach { indexBuilder.addTokenIndex(it) }
          SearchParamType.REFERENCE ->
            referenceIndex(searchParam, value)?.also { indexBuilder.addReferenceIndex(it) }
          SearchParamType.QUANTITY ->
            quantityIndex(searchParam, value)?.forEach { indexBuilder.addQuantityIndex(it) }
          SearchParamType.URI -> uriIndex(searchParam, value)?.also { indexBuilder.addUriIndex(it) }
          SearchParamType.SPECIAL -> specialIndex(value)?.also { indexBuilder.addPositionIndex(it) }
          // TODO: Handle composite type https://github.com/google/android-fhir/issues/292.
          else -> Unit
        }
      }

    addIndexesFromResourceClass(resource, indexBuilder)
    return indexBuilder.build()
  }

  /**
   * Manually add indexes for [SearchParameter]s defined in [Resource] class. This is because:
   * 1. There is no clear way defined in the search parameter definitions to figure out the class
   * hierarchy of the model classes in codegen.
   * 2. Common [SearchParameter]'s paths are defined for [Resource] class e.g even for the [Patient]
   * model, the [SearchParameter] expression for id would be `Resource.id` and
   * [FHIRPathEngine.evaluate] doesn't return anything when [Patient] is passed to the function.
   */
  private fun <R : IAnyResource> addIndexesFromResourceClass(
    resource: R,
    indexBuilder: ResourceIndices.Builder,
  ) {
    indexBuilder.addTokenIndex(
      TokenIndex(
        "_id",
        arrayOf(resource.fhirType(), "id").joinToString(separator = "."),
        null,
        resource.logicalId
      )
    )
    // Add 'lastUpdated' index to all resources.
    if (fhirAdapter.hasLastUpdated(resource.meta)) {
      val lastUpdatedElement = fhirAdapter.toLastUpdatedElement(resource.meta)
      indexBuilder.addDateTimeIndex(
        DateTimeIndex(
          name = "_lastUpdated",
          path = arrayOf(resource.fhirType(), "meta", "lastUpdated").joinToString(separator = "."),
          from = lastUpdatedElement.time,
          to = lastUpdatedElement.time
        )
      )
    }

    if (fhirAdapter.hasProfile(resource.meta)) {
      resource.meta.profile
        .filter { it.value != null && it.value.isNotEmpty() }
        .forEach {
          indexBuilder.addReferenceIndex(
            ReferenceIndex(
              "_profile",
              arrayOf(resource.fhirType(), "meta", "profile").joinToString(separator = "."),
              it.value
            )
          )
        }
    }

    if (fhirAdapter.hasTag(resource.meta)) {
      resource.meta.tag
        .filter { it.code != null && it.code!!.isNotEmpty() }
        .forEach {
          indexBuilder.addTokenIndex(
            TokenIndex(
              "_tag",
              arrayOf(resource.fhirType(), "meta", "tag").joinToString(separator = "."),
              it.system ?: "",
              it.code
            )
          )
        }
    }
  }

  private fun numberIndex(searchParam: SearchParamDefinition, value: IBase): NumberIndex? =
    fhirAdapter.toNumberIndex(searchParam, value)

  private fun dateIndex(searchParam: SearchParamDefinition, value: IBase): DateIndex =
    fhirAdapter.toDateIndex(searchParam, value)

  private fun dateTimeIndex(searchParam: SearchParamDefinition, value: IBase): DateTimeIndex? =
    fhirAdapter.toDateTimeIndex(searchParam, value)

  private fun stringIndex(searchParam: SearchParamDefinition, value: IBase): StringIndex? =
    fhirAdapter.toStringIndex(searchParam, value)

  private fun tokenIndex(searchParam: SearchParamDefinition, value: IBase): List<TokenIndex> =
    fhirAdapter.toTokenIndexList(searchParam, value)

  private fun referenceIndex(searchParam: SearchParamDefinition, value: IBase): ReferenceIndex? =
    fhirAdapter.toReferenceIndex(searchParam, value)

  private fun quantityIndex(searchParam: SearchParamDefinition, value: IBase): List<QuantityIndex> =
    fhirAdapter.toQuantityIndexList(searchParam, value)
  private fun uriIndex(searchParam: SearchParamDefinition, value: IBase?): UriIndex? =
    fhirAdapter.toUriIndex(searchParam, value)

  private fun specialIndex(value: IBase?): PositionIndex? = fhirAdapter.toPositionIndex(value)
}

data class SearchParamDefinition(val name: String, val type: SearchParamType, val path: String)
