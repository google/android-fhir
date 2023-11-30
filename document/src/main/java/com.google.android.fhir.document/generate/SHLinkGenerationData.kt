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

package com.google.android.fhir.document.generate

import com.google.android.fhir.document.IPSDocument
import java.time.Instant

/**
 * Represents a SHL data structure, which stores information needed to generate a SHL.
 *
 * SHLs, or Smart Health Links, are a standardized format for securely sharing health-related
 * information. For official documentation and specifications, see
 * [SHL Documentation](https://docs.smarthealthit.org/smart-health-links/).
 *
 * @property label A label describing the SHL data.
 * @property expirationTime The expiration time of the SHL data, if any.
 * @property ipsDoc The IPS document linked to by the SHL.
 */
data class SHLinkGenerationData(
  val label: String,
  val expirationTime: Instant?,
  val ipsDoc: IPSDocument,
)
