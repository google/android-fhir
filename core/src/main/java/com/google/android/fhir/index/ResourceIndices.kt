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

package com.google.android.fhir.index

import com.google.android.fhir.index.entities.DateIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.QuantityIndex
import com.google.android.fhir.index.entities.ReferenceIndex
import com.google.android.fhir.index.entities.StringIndex
import com.google.android.fhir.index.entities.TokenIndex
import com.google.android.fhir.index.entities.UriIndex
import org.hl7.fhir.r4.model.ResourceType

/**
 * Indices extracted from the resource of [resourceType] and [resourceId]. Used to create index
 * records in the database to support search.
 *
 * See https://www.hl7.org/fhir/search.html.
 */
internal data class ResourceIndices(
    val resourceType: ResourceType,
    val resourceId: String,
    val numberIndices: List<NumberIndex>,
    val dateIndices: List<DateIndex>,
    val stringIndices: List<StringIndex>,
    val uriIndices: List<UriIndex>,
    val tokenIndices: List<TokenIndex>,
    val quantityIndices: List<QuantityIndex>,
    val referenceIndices: List<ReferenceIndex>
) {
    class Builder(private val resourceType: ResourceType, private val resourceId: String) {
        private val stringIndices = mutableListOf<StringIndex>()
        private val referenceIndices = mutableListOf<ReferenceIndex>()
        private val tokenIndices = mutableListOf<TokenIndex>()
        private val quantityIndices = mutableListOf<QuantityIndex>()
        private val uriIndices = mutableListOf<UriIndex>()
        private val dateIndices = mutableListOf<DateIndex>()
        private val numberIndices = mutableListOf<NumberIndex>()

        fun addNumberIndex(numberIndex: NumberIndex) = numberIndices.add(numberIndex)
        fun addDateIndex(dateIndex: DateIndex) = dateIndices.add(dateIndex)
        fun addStringIndex(stringIndex: StringIndex) = stringIndices.add(stringIndex)
        fun addUriIndex(uriIndex: UriIndex) = uriIndices.add(uriIndex)
        fun addTokenIndex(tokenIndex: TokenIndex) = tokenIndices.add(tokenIndex)
        fun addQuantityIndex(quantityIndex: QuantityIndex) = quantityIndices.add(quantityIndex)
        fun addReferenceIndex(referenceIndex: ReferenceIndex) = referenceIndices.add(referenceIndex)

        fun build() = ResourceIndices(
            resourceType = resourceType,
            resourceId = resourceId,
            numberIndices = numberIndices.toList(),
            dateIndices = dateIndices.toList(),
            stringIndices = stringIndices.toList(),
            uriIndices = uriIndices.toList(),
            tokenIndices = tokenIndices.toList(),
            quantityIndices = quantityIndices.toList(),
            referenceIndices = referenceIndices.toList()
        )
    }
}
