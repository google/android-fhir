/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.datacapture.mapping

import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.StructureDefinition

/** Loads `StructureDefinition` for profile based on canonical URL. */
interface ProfileLoader {
  /**
   * @param url the canonical URL for the [StructureDefinition] to be loaded. This may come from
   *   `resource.meta.profile` or as part of `questionnaire.item.definition` to inform extraction of
   *   values into fields defined in the profile.
   * @return a [StructureDefinition] with the specified canonical `url` or `null` if it cannot be
   *   found
   */
  fun loadProfile(url: CanonicalType): StructureDefinition?
}
