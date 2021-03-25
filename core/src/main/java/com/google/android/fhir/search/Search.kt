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

import com.google.android.fhir.search.filter.FilterCriterion
import com.google.android.fhir.search.sort.SortCriterion
import org.hl7.fhir.r4.model.Resource

/**
 * The interface that acts as an entry point for search.
 *
 * Example usage:
 * ```
 * search
 *   .of(...resource type...)        // Mandatory, must be the first function call, specify type
 *   .filter(...search criteria...)  // Optional, specify search criteria
 *   .sorted(...sorting criteria...) // Optional, specify sorting criteria
 *   .skip(...number of results to skip...)     // Optional, specify number of resources to skip
 *   .limit(...number of results to return...)  // Optional, specify number of resources to return
 *   .run();                         // Mandatory, must be the last function call
 * ```
 */
interface Search {
  /** Returns a [SearchSpecifications] object with the given [clazz]. */
  fun <R : Resource> of(clazz: Class<R>): SearchSpecifications

  /** The interface to specify the search criteria and to execute the search. */
  interface SearchSpecifications {
    /** Returns a [SearchSpecifications] object with the [filterCriterion]. */
    fun filter(filterCriterion: FilterCriterion): SearchSpecifications

    /** Returns a [SearchSpecifications] object with the [sortCriterion]. */
    fun sort(sortCriterion: SortCriterion): SearchSpecifications

    /** Returns a [SearchSpecifications] object that only includes the first [limit] results. */
    fun limit(limit: Int): SearchSpecifications

    /** Returns a [SearchSpecifications] object that skips the first [skip] results. */
    fun skip(skip: Int): SearchSpecifications

    /** Runs a search with the [SearchSpecifications]. */
    suspend fun <R : Resource> run(): List<R>
  }
}
