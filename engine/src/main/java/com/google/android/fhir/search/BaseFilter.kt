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

package com.google.android.fhir.search

import org.hl7.fhir.r4.model.ResourceType

abstract class BaseFilter : Filterable {
  override var operation: Operation = Operation.NONE

  fun getQuery(type: ResourceType): SearchQuery {
    return when (this) {
      is StringFilter -> query(type)
      is DateFilter -> query(type)
      is DateTimeFilter -> query(type)
      is NumberFilter -> query(type)
      is ReferenceFilter -> query(type)
      is TokenFilter -> query(type)
      is QuantityFilter -> query(type)
      else -> SearchQuery("", emptyList())
    }
  }

  infix fun or(other: BaseFilter): BaseFilter {
    operation = Operation.OR
    return other
  }

  infix fun and(other: BaseFilter): BaseFilter {
    operation = Operation.AND
    return other
  }
}
