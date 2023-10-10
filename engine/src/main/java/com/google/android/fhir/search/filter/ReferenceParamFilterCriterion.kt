/*
 * Copyright 2021-2023 Google LLC
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

package com.google.android.fhir.search.filter

import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import com.google.android.fhir.search.ConditionParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchDslMarker

/**
 * Represents a criterion for filtering [ReferenceClientParam]. e.g. filter(Observation.SUBJECT, {
 * value = "Patient/001" })
 */
@SearchDslMarker
data class ReferenceParamFilterCriterion(
  val parameter: ReferenceClientParam,
  var value: String? = null,
) : FilterCriterion {

  override fun getConditionalParams() = listOf(ConditionParam("index_value = ?", value!!))
}

internal data class ReferenceParamFilterCriteria(
  val parameter: ReferenceClientParam,
  override val filters: List<ReferenceParamFilterCriterion>,
  override val operation: Operation,
) : FilterCriteria(filters, operation, parameter, "ReferenceIndexEntity")
