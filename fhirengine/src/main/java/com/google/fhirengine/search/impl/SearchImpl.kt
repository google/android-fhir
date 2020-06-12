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

package com.google.fhirengine.search.impl

import com.google.fhirengine.db.Database
import com.google.fhirengine.search.Search
import com.google.fhirengine.search.criteria.FilterCriterion
import org.hl7.fhir.r4.model.Resource

/** Implementation of the [Search] interface. */
class SearchImpl constructor(val database: Database) : Search {
    override fun <R : Resource> of(clazz: Class<R>) = SearchSpecificationImpl(clazz)

    /** Implementation of the [Search.SearchSpecifications] interface. */
    inner class SearchSpecificationImpl<R : Resource>(
      val clazz: Class<R>
    ) : Search.SearchSpecifications {
        lateinit var filterCriterion: FilterCriterion

        override fun filter(filterCriterion: FilterCriterion): Search.SearchSpecifications =
                apply { this.filterCriterion = filterCriterion }

        override fun <R : Resource> run(): List<R> {
            return database.search(filterCriterion.query(clazz).getSearchResourceQuery(clazz))
        }
    }
}
