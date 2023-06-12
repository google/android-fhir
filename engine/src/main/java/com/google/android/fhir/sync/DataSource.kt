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

package com.google.android.fhir.sync

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Bundle.BundleType
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource

/**
 * Interface for an abstraction of retrieving FHIR data from a network source. The network
 * operations are [Bundle] based to optimize network traffic.
 */
internal interface DataSource {
  /** @return [Bundle] on a successful operation, [OperationOutcome] otherwise. */
  suspend fun download(request: Request): Resource

  /**
   * @return [Bundle] of type [BundleType.TRANSACTIONRESPONSE] for a successful operation,
   * [OperationOutcome] otherwise. Call this api with the [Bundle] that needs to be uploaded to the
   * server.
   */
  suspend fun upload(request: BundleRequest): Resource
}
