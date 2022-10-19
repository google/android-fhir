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

package com.google.android.fhir.datacapture.mapping

import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.StructureDefinition

/**
 * This provides an interface to load profile/structure definition in SDK based on canonical url for
 * FHIR Resource conforms to different profile than standard FHIR profile
 */
interface LoadProfileCallback {
  /**
   * @param url : Canonical URL from list of Canonical urls defines as profiles in Resource meta
   * field (eg. questionnaire.meta.profile)
   * @return StructureDefinition resource conform to this StructureDefinition/Profile
   */
  fun loadProfile(url: CanonicalType): StructureDefinition
}
