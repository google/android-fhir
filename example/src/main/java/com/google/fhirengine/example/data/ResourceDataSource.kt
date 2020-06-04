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
import org.hl7.fhir.r4.model.ResourceType

data class NetworkSearchParameter(val key: String, val value: String)

fun List<NetworkSearchParameter>.concat(): String {
    var result = ""

    this.mapIndexed { index, param ->
        result += "${param.key}=${param.value}"
        if (index != this.size - 1) {
            result += "&"
        }
    }
    return result
}

class ResourceDataSource(
    resourceType: ResourceType,
    searchParams: List<NetworkSearchParameter> = emptyList(),
    private val service: HapiFhirService
) : FhirDataSource {

    private var nextUrl: String? =
        "${HapiFhirService.BASE_URL}${resourceType.name}?${searchParams.concat()}"

    override suspend fun loadData(): FhirLoadResult {
        val url = nextUrl
        return if (url != null) {
            val bundle = service.getResource(url)
            nextUrl = bundle.link.firstOrNull { component -> component.relation == "next" }?.url
            FhirLoadResult(
                canLoadMore = nextUrl != null,
                resource = bundle
            )
        } else {
            // nextUrl is null so we finished loading
            FhirLoadResult(canLoadMore = false, resource = null)
        }
    }
}
