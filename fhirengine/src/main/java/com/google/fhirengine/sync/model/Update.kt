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

package com.google.fhirengine.sync.model

/**
 * Result of squashing local changes of a resource for sync with a remote server.
 *
 * [resourceId] The  id of the resource to be updated, e.g. "123"
 * [resourceType] The type of the resource to be updated, e.g. "Patient"
 * [payload] The body of HTTP request as per https://www.hl7.org/fhir/http.html
 * [type] The type of update, e.g. "INSERT" for a newly created resource
 */
data class Update(val resourceId: String,
                  val resourceType: String,
                  val payload: String,
                  val type: Type) {

    enum class Type(val value: Int) {
        INSERT(1), // create a new resource. payload is the entire resource json.
        UPDATE(2), // patch. payload is the json patch.
        DELETE(3); // delete. payload is empty string.

        companion object {
            fun from(input: Int): Type = values().first { it.value == input }
        }
    }
}
