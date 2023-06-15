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

package com.google.android.fhir.sync.remote

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource

/** Interface to make HTTP requests to the FHIR server. */
internal interface FhirHttpService {

  /**
   * Makes a HTTP-GET method request to the server.
   * @return The server may return a particular [Resource], [Bundle] or [OperationOutcome] based on
   * the request processing.
   */
  suspend fun get(path: String, headers: Map<String, String>): Resource

  /**
   * Makes a HTTP-POST method request to the server with the [Bundle] as request-body.
   * @return The server may return [Bundle] or [OperationOutcome] based on the request processing.
   */
  suspend fun post(bundle: Bundle, headers: Map<String, String>): Resource
}
