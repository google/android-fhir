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

package com.google.android.fhir.sync.upload.consolidator

import com.google.android.fhir.sync.upload.UploadSyncResult

/**
 * Represents a mechanism to consolidate resources after they are uploaded.
 *
 * INTERNAL ONLY. This interface should NEVER be exposed as an external API because it works
 * together with other components in the upload package to fulfill a specific upload strategy. After
 * a resource is uploaded to a remote FHIR server and a response is returned, we need to consolidate
 * any changes in the database, Examples of this would be, updating the lastUpdated timestamp field,
 * or deleting the local change from the database, or updating the resource IDs and payloads to
 * correspond with the server’s feedback.
 */
internal fun interface ResourceConsolidator {

  /** Consolidates the local change token with the provided response from the FHIR server. */
  suspend fun consolidate(uploadSyncResult: UploadSyncResult)
}
