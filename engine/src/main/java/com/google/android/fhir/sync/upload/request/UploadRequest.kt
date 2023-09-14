/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.sync.upload.request

import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/**
 * Structure represents a request that can be made to upload resources/resource modifications to the
 * FHIR server.
 */
sealed class UploadRequest(
  open val url: String,
  open val headers: Map<String, String> = emptyMap(),
  open val resource: Resource,
)

/**
 * A FHIR [Bundle] based request for uploads. Multiple resources/resource modifications can be
 * uploaded as a single request using this.
 */
data class BundleUploadRequest(
  override val headers: Map<String, String> = emptyMap(),
  override val resource: Bundle,
) : UploadRequest(".", headers, resource)

/** A [url] based FHIR request to upload resources to the server. */
data class UrlUploadRequest(
  val httpVerb: HttpVerb,
  override val url: String,
  override val resource: Resource,
  override val headers: Map<String, String> = emptyMap(),
) : UploadRequest(url, headers, resource)
