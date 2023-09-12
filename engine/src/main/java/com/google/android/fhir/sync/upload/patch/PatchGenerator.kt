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

package com.google.android.fhir.sync.upload.patch

import com.google.android.fhir.LocalChange

/**
 * Generates [Patch]es from [LocalChange]s.
 *
 * INTERNAL ONLY. This interface should NEVER been exposed as an external API because it works
 * together with other components in the upload package to fulfill a specific upload strategy.
 * Application-specific implementations of this interface are unlikely to catch all the edge cases
 * and work with other components in the upload package seamlessly. Should there be a genuine need
 * to control the [Patch]es to be uploaded to the server, more granulated control mechanisms should
 * be opened up to applications to guarantee correctness.
 */
internal interface PatchGenerator {
  /**
   * NOTE: different implementations may have requirements on the size of [localChanges] and output
   * certain numbers of [Patch]es.
   */
  fun generate(localChanges: List<LocalChange>): List<Patch>
}
