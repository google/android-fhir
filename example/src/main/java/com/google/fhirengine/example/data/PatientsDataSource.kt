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

package com.google.fhirengine.example.data

import com.google.fhirengine.example.api.HapiFhirService
import com.google.fhirengine.sync.FhirDataSource
import com.google.fhirengine.sync.FhirLoadResult

class PatientsDataSource(
  private val country: String,
  private val service: HapiFhirService
) : FhirDataSource {

    // the following variables are only set after the first request
    private var pagesId: String? = null
    private var bundleType: String? = null
    private var pagesOffset = 50
    private var totalResources: Int = -1
    private var resourcesRetrieved: Int = 0

    override suspend fun loadData(): FhirLoadResult {
        if (totalResources == -1) {
            // first request
            val bundle = service.getPatients(country)
            pagesId = bundle.id
            bundleType = bundle.type.display // ?? what's the right type here?
            totalResources = bundle.total
            resourcesRetrieved = bundle.entry.size

            return FhirLoadResult(
                    canLoadMore = totalResources > resourcesRetrieved,
                    resource = bundle
            )
        }

        if (totalResources > resourcesRetrieved) {
            // request more
            val bundle = service.getMorePages(
                    pagesId = pagesId!!,
                    pagesOffset = pagesOffset,
                    pagesCount = PAGES_COUNT,
                    bundleType = bundleType!!
            )
            resourcesRetrieved += bundle.entry.size
            pagesOffset += PAGES_COUNT

            return FhirLoadResult(
                    canLoadMore = totalResources > resourcesRetrieved && bundle.entry.isNotEmpty(),
                    resource = bundle
            )
        }

        // case: totalResources == resourcesRetrieved
        // we finished loading
        return FhirLoadResult(canLoadMore = false, resource = null)
    }

    companion object {
        private const val PAGES_COUNT = 20 // 20 is the max value accepted
    }
}
