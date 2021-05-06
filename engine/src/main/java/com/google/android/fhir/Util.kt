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

package com.google.android.fhir

import android.annotation.SuppressLint
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** Utility function to format a [Date] object using the system's default locale. */
@SuppressLint("NewApi")
internal fun Date.toTimeZoneString(): String {
  val simpleDateFormat =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
      .withZone(ZoneId.systemDefault())
  return simpleDateFormat.format(this.toInstant())
}

/**
 * The logical (unqualified) part of the ID. For example, if the ID is
 * "http://example.com/fhir/Patient/123/_history/456", then this value would be "123".
 */
val Resource.logicalId: String
  get() {
    return this.idElement?.idPart.orEmpty()
  }

/**
 * Determines if the upload operation was successful or not.
 *
 * Current HAPI FHIR implementation does not give any signal other than 'severity' level for
 * operation success/failure. TODO: pass along the HTTP result (or any other signal) to determine
 * the outcome of an instance level RESTful operation.
 */
fun Resource.isUploadSuccess(): Boolean {
  if (!this.resourceType.equals(ResourceType.OperationOutcome)) return false
  val outcome: OperationOutcome = this as OperationOutcome
  return outcome.issue.isNotEmpty() &&
    outcome.issue.all { it.severity.equals(OperationOutcome.IssueSeverity.INFORMATION) }
}

/**
 * Extension to expresses [HumanName] as a separated string using [separator]. See
 * https://www.hl7.org/fhir/patient.html#search
 */
fun HumanName.asString(separator: CharSequence = ", "): String {
  return listOfNotNull(
      prefix?.filter { it.value.isNotBlank() }?.joinToString(separator = separator) {
        it.valueNotNull
      },
      given?.filter { it.value.isNotBlank() }?.joinToString(separator = separator) {
        it.valueNotNull
      },
      family,
      suffix?.filter { it.value.isNotBlank() }?.joinToString(separator = separator) {
        it.valueNotNull
      },
      text
    )
    .filter { it.isNotBlank() }
    .joinToString(separator)
}
/**
 * Extension to expresses [Address] as a string using [separator]. See
 * https://www.hl7.org/fhir/patient.html#search
 */
fun Address.asString(separator: CharSequence = ", "): String {
  return listOfNotNull(
      line?.filter { it.value.isNotBlank() }?.joinToString(separator = separator) {
        it.valueNotNull
      },
      city,
      district,
      state,
      country,
      postalCode,
      text
    )
    .filter { it.isNotBlank() }
    .joinToString(separator)
}
