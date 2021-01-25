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

package com.google.fhirengine.sync

import java.net.URLEncoder
import org.hl7.fhir.r4.model.ResourceType

fun SyncData.concatParams(): String {
    return this.params.entries.joinToString("&") { (key, value) ->
        "$key=${URLEncoder.encode(value, "UTF-8")}"
    }
}

/**
 * Class that holds what type of resources we need to synchronise and what are the parameters of
 * that type.
 * e.g. we only want to synchronise patients that live in United States
 *  `SyncData(ResourceType.Patient, mapOf("address-country" to "United States")`
 */
data class SyncData(
    val resourceType: ResourceType,
    val params: Map<String, String> = emptyMap()
) {
    companion object {
        const val SORT_KEY = "_sort"
        const val LAST_UPDATED_KEY = "_lastUpdated"
        const val ADDRESS_COUNTRY_KEY = "address-country"
        const val LAST_UPDATED_ASC_VALUE = "_lastUpdated"
    }
}
