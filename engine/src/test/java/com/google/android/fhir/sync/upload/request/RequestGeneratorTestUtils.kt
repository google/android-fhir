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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.sync.upload.patch.Patch
import java.time.Instant
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

object RequestGeneratorTestUtils {

  fun LocalChange.toPatch() =
    Patch(
      resourceType = resourceType,
      resourceId = resourceId,
      versionId = versionId,
      timestamp = timestamp,
      payload = payload,
      type = Patch.Type.from(type.value),
    )

  val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  val insertionLocalChange =
    LocalChange(
      resourceType = ResourceType.Patient.name,
      resourceId = "Patient-001",
      type = LocalChange.Type.INSERT,
      payload =
        jsonParser.encodeResourceToString(
          Patient().apply {
            id = "Patient-001"
            addName(
              HumanName().apply {
                addGiven("John")
                family = "Doe"
              },
            )
          },
        ),
      timestamp = Instant.now(),
      token = LocalChangeToken(listOf(1L)),
    )
  val updateLocalChange =
    LocalChange(
      resourceType = ResourceType.Patient.name,
      resourceId = "Patient-001",
      type = LocalChange.Type.UPDATE,
      payload = "[{\"op\":\"replace\",\"path\":\"\\/name\\/0\\/given\\/0\",\"value\":\"Janet\"}]",
      timestamp = Instant.now(),
      token = LocalChangeToken(listOf(2L)),
      versionId = "v-p002-01",
    )
  val deleteLocalChange =
    LocalChange(
      resourceType = ResourceType.Patient.name,
      resourceId = "Patient-001",
      type = LocalChange.Type.DELETE,
      payload =
        jsonParser.encodeResourceToString(
          Patient().apply {
            id = "Patient-001"
            addName(
              HumanName().apply {
                addGiven("John")
                family = "Doe"
              },
            )
          },
        ),
      timestamp = Instant.now(),
      token = LocalChangeToken(listOf(2L)),
      versionId = "v-p003-01",
    )
}
