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
import com.google.android.fhir.index.entities.DateTimeIndex
import com.google.android.fhir.index.entities.NumberIndex
import com.google.android.fhir.index.entities.PositionIndex
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
  val dateTimeIndices: List<DateTimeIndex>,
  val stringIndices: List<StringIndex>,
  val uriIndices: List<UriIndex>,
  val tokenIndices: List<TokenIndex>,
  val quantityIndices: List<QuantityIndex>,
  val referenceIndices: List<ReferenceIndex>,
  val positionIndices: List<PositionIndex>
) {
  class Builder(private val resourceType: ResourceType, private val resourceId: String) {
    private val stringIndices = mutableListOf<StringIndex>()
    private val referenceIndices = mutableListOf<ReferenceIndex>()
    private val tokenIndices = mutableListOf<TokenIndex>()
    private val quantityIndices = mutableListOf<QuantityIndex>()
    private val uriIndices = mutableListOf<UriIndex>()
    private val dateIndices = mutableListOf<DateIndex>()
    private val dateTimeIndices = mutableListOf<DateTimeIndex>()
    private val numberIndices = mutableListOf<NumberIndex>()
    private val positionIndices = mutableListOf<PositionIndex>()

    fun addNumberIndex(numberIndex: NumberIndex) {
      if (numberIndices.contains(numberIndex)) {
        return
      }
      numberIndices.add(numberIndex)
    }

    fun addDateIndex(dateIndex: DateIndex) {
      if (dateIndices.contains(dateIndex)) {
        return
      }
      dateIndices.add(dateIndex)
    }

    fun addDateTimeIndex(dateTimeIndex: DateTimeIndex) {
      if (dateTimeIndices.contains(dateTimeIndex)) {
        return
      }
      dateTimeIndices.add(dateTimeIndex)
    }

    fun addStringIndex(stringIndex: StringIndex) {
      if (stringIndices.contains(stringIndex)) {
        return
      }
      stringIndices.add(stringIndex)
    }

    fun addUriIndex(uriIndex: UriIndex) {
      if (uriIndices.contains(uriIndex)) {
        return
      }
      uriIndices.add(uriIndex)
    }

    fun addTokenIndex(tokenIndex: TokenIndex) {
      if (tokenIndices.contains(tokenIndex)) {
        return
      }
      tokenIndices.add(tokenIndex)
    }

    fun addQuantityIndex(quantityIndex: QuantityIndex) {
      if (quantityIndices.contains(quantityIndex)) {
        return
      }
      quantityIndices.add(quantityIndex)
    }

    fun addReferenceIndex(referenceIndex: ReferenceIndex) {
      if (referenceIndices.contains(referenceIndex)) {
        return
      }
      referenceIndices.add(referenceIndex)
    }

    fun addPositionIndex(positionIndex: PositionIndex) {
      if (positionIndices.contains(positionIndex)) {
        return
      }
      positionIndices.add(positionIndex)
    }

    fun build() =
      ResourceIndices(
        resourceType = resourceType,
        resourceId = resourceId,
        numberIndices = numberIndices.toList(),
        dateIndices = dateIndices.toList(),
        dateTimeIndices = dateTimeIndices.toList(),
        stringIndices = stringIndices.toList(),
        uriIndices = uriIndices.toList(),
        tokenIndices = tokenIndices.toList(),
        quantityIndices = quantityIndices.toList(),
        referenceIndices = referenceIndices.toList(),
        positionIndices = positionIndices.toList()
      )
  }
}
