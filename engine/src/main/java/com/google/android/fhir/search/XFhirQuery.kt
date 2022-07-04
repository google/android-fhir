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

package com.google.android.fhir.search

import org.hl7.fhir.r4.model.ResourceType

const val XFHIR_QUERY_SORT_PARAM = "_sort"
const val XFHIR_QUERY_COUNT_PARAM = "_count"

data class XFhirQuery(
  var type: ResourceType,
  var search: Map<String, String?>,
  var sort: List<String>?,
  var count: Int
)
