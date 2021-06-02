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

package com.google.android.fhir.sync

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource

/**
 * Interface for an abstraction of retrieving static data from a network source. The data can be
 * retrieved in pages and each data retrieval is an expensive operation.
 */
interface DataSource {

  /**
   * Implement this method to load remote data based on a url [path]. A service base url is of the
   * form: `http{s}://server/{path}`
   */
  suspend fun loadData(path: String): Bundle

  /**
   * Implement this method to create a new instance level resource in the remote server. See
   * http://hl7.org/implement/standards/fhir/http.html#update for details.
   *
   * @param payload is the Resource in JSON form.
   */
  suspend fun insert(resourceType: String, resourceId: String, payload: String): Resource

  /**
   * Implement this method to update an existing resource on the remote server. See
   * http://hl7.org/implement/standards/fhir/http.html#patch for details.
   *
   * @param payload is a [JSON patch](https://tools.ietf.org/html/rfc6902)
   */
  suspend fun update(resourceType: String, resourceId: String, payload: String): OperationOutcome

  /**
   * Implement this method to delete a resource from the remote server. See
   * http://hl7.org/implement/standards/fhir/http.html#delete for details.
   */
  suspend fun delete(resourceType: String, resourceId: String): OperationOutcome
}
