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

package com.google.android.fhir.search.filter

import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.SearchQuery
import java.lang.StringBuilder
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

/** Represents filter for a [IParam] */
internal interface FilterCriterion

/**
 * Contains a set of filter criteria sharing the same search parameter. e.g A
 * [StringParamFilterCriteria] may contain a list of [StringParamFilterCriterion] each with
 * different [StringParamFilterCriterion.value] and [StringParamFilterCriterion.modifier] to filter
 * results for a particular [StringClientParam] like [Patient.GIVEN].
 *
 * An api call like filter(Patient.GIVEN,{value = "John"},{value = "Jane"}) will create a
 * [StringParamFilterCriteria] with two [StringParamFilterCriterion] one with
 * [StringParamFilterCriterion.value] as "John" and other as "Jane."
 */
internal sealed class FilterCriteria(
  open val filters: List<FilterCriterion>,
  open val operation: Operation
) {
  /** Returns a [SearchQuery] for the [FilterCriteria] based on all the [FilterCriterion]. */
  abstract fun query(type: ResourceType): SearchQuery

  companion object {
    /** Based on [PrePost], joins the string with brackets around the each item. */
    fun <T> Collection<T>.joinToQueryString(
      buffer: Appendable = StringBuilder(),
      separator: CharSequence = ", ",
      prePost: PrePost = PrePost.EACH,
      transform: ((T) -> CharSequence)? = null,
    ): String {
      for ((count, element) in this.withIndex()) {
        if (count > 0) buffer.append(separator)
        if (transform != null) {
          when (prePost) {
            PrePost.NONE -> buffer.append(transform(element))
            PrePost.EACH ->
              if (size > 1) {
                buffer.append("(${transform(element)})")
              } else {
                buffer.append(transform(element))
              }
          }
        }
      }
      return buffer.toString()
    }
  }
}

internal enum class PrePost {
  NONE,
  EACH,
}
