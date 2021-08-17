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

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.ClaimProcessingCode
import com.google.fhir.r4.core.EnrollmentResponse
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import org.hl7.fhir.r4.model.Enumerations

object EnrollmentResponseConverter {
  fun EnrollmentResponse.toHapi(): org.hl7.fhir.r4.model.EnrollmentResponse {
    val hapiValue = org.hl7.fhir.r4.model.EnrollmentResponse()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (hasMeta()) {
      hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
      hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasText()) {
      hapiValue.text = text.toHapi()
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.EnrollmentResponse.EnrollmentResponseStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasRequest()) {
      hapiValue.request = request.toHapi()
    }
    if (hasOutcome()) {
      hapiValue.outcome =
        Enumerations.RemittanceOutcome.valueOf(outcome.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (hasDisposition()) {
      hapiValue.dispositionElement = disposition.toHapi()
    }
    if (hasCreated()) {
      hapiValue.createdElement = created.toHapi()
    }
    if (hasOrganization()) {
      hapiValue.organization = organization.toHapi()
    }
    if (hasRequestProvider()) {
      hapiValue.requestProvider = requestProvider.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.EnrollmentResponse.toProto(): EnrollmentResponse {
    val protoValue = EnrollmentResponse.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
    if (hasMeta()) {
      protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
      protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasText()) {
      protoValue.text = text.toProto()
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        EnrollmentResponse.StatusCode.newBuilder()
          .setValue(
            FinancialResourceStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasRequest()) {
      protoValue.request = request.toProto()
    }
    if (hasOutcome()) {
      protoValue.outcome =
        EnrollmentResponse.OutcomeCode.newBuilder()
          .setValue(
            ClaimProcessingCode.Value.valueOf(
              outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasDisposition()) {
      protoValue.disposition = dispositionElement.toProto()
    }
    if (hasCreated()) {
      protoValue.created = createdElement.toProto()
    }
    if (hasOrganization()) {
      protoValue.organization = organization.toProto()
    }
    if (hasRequestProvider()) {
      protoValue.requestProvider = requestProvider.toProto()
    }
    return protoValue.build()
  }
}
