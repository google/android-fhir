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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.sync.UploadRequest

/**
 * Interface for generating upload requests from a list of local changes. This interface provides a
 * common contract for both transaction bundle generation and individual resource uploading.
 */
interface UploadRequestGenerator {
  /** Generates an [UploadRequest] on the provided [LocalChange]s */
  fun generateUploadRequest(localChanges: List<LocalChange>): UploadRequest
}
