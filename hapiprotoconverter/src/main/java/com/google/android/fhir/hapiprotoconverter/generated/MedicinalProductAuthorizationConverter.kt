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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductAuthorization
import com.google.fhir.r4.core.MedicinalProductAuthorization.Procedure
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object MedicinalProductAuthorizationConverter {
  private fun MedicinalProductAuthorization.Procedure.DateX.medicinalProductAuthorizationProcedureDateToHapi():
    Type {
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicinalProductAuthorization.procedure.date[x]"
    )
  }

  private fun Type.medicinalProductAuthorizationProcedureDateToProto():
    MedicinalProductAuthorization.Procedure.DateX {
    val protoValue = MedicinalProductAuthorization.Procedure.DateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    return protoValue.build()
  }

  fun MedicinalProductAuthorization.toHapi(): org.hl7.fhir.r4.model.MedicinalProductAuthorization {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductAuthorization()
    hapiValue.id = id.value
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
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (countryCount > 0) {
      hapiValue.country = countryList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status = status.toHapi()
    }
    if (hasStatusDate()) {
      hapiValue.statusDateElement = statusDate.toHapi()
    }
    if (hasRestoreDate()) {
      hapiValue.restoreDateElement = restoreDate.toHapi()
    }
    if (hasValidityPeriod()) {
      hapiValue.validityPeriod = validityPeriod.toHapi()
    }
    if (hasDataExclusivityPeriod()) {
      hapiValue.dataExclusivityPeriod = dataExclusivityPeriod.toHapi()
    }
    if (hasDateOfFirstAuthorization()) {
      hapiValue.dateOfFirstAuthorizationElement = dateOfFirstAuthorization.toHapi()
    }
    if (hasInternationalBirthDate()) {
      hapiValue.internationalBirthDateElement = internationalBirthDate.toHapi()
    }
    if (hasLegalBasis()) {
      hapiValue.legalBasis = legalBasis.toHapi()
    }
    if (jurisdictionalAuthorizationCount > 0) {
      hapiValue.jurisdictionalAuthorization = jurisdictionalAuthorizationList.map { it.toHapi() }
    }
    if (hasHolder()) {
      hapiValue.holder = holder.toHapi()
    }
    if (hasRegulator()) {
      hapiValue.regulator = regulator.toHapi()
    }
    if (hasProcedure()) {
      hapiValue.procedure = procedure.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.toProto(): MedicinalProductAuthorization {
    val protoValue = MedicinalProductAuthorization.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasCountry()) {
      protoValue.addAllCountry(country.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status = status.toProto()
    }
    if (hasStatusDate()) {
      protoValue.statusDate = statusDateElement.toProto()
    }
    if (hasRestoreDate()) {
      protoValue.restoreDate = restoreDateElement.toProto()
    }
    if (hasValidityPeriod()) {
      protoValue.validityPeriod = validityPeriod.toProto()
    }
    if (hasDataExclusivityPeriod()) {
      protoValue.dataExclusivityPeriod = dataExclusivityPeriod.toProto()
    }
    if (hasDateOfFirstAuthorization()) {
      protoValue.dateOfFirstAuthorization = dateOfFirstAuthorizationElement.toProto()
    }
    if (hasInternationalBirthDate()) {
      protoValue.internationalBirthDate = internationalBirthDateElement.toProto()
    }
    if (hasLegalBasis()) {
      protoValue.legalBasis = legalBasis.toProto()
    }
    if (hasJurisdictionalAuthorization()) {
      protoValue.addAllJurisdictionalAuthorization(jurisdictionalAuthorization.map { it.toProto() })
    }
    if (hasHolder()) {
      protoValue.holder = holder.toProto()
    }
    if (hasRegulator()) {
      protoValue.regulator = regulator.toProto()
    }
    if (hasProcedure()) {
      protoValue.procedure = procedure.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationJurisdictionalAuthorizationComponent.toProto():
    MedicinalProductAuthorization.JurisdictionalAuthorization {
    val protoValue =
      MedicinalProductAuthorization.JurisdictionalAuthorization.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasCountry()) {
      protoValue.country = country.toProto()
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasLegalStatusOfSupply()) {
      protoValue.legalStatusOfSupply = legalStatusOfSupply.toProto()
    }
    if (hasValidityPeriod()) {
      protoValue.validityPeriod = validityPeriod.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationProcedureComponent.toProto():
    MedicinalProductAuthorization.Procedure {
    val protoValue =
      MedicinalProductAuthorization.Procedure.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasDate()) {
      protoValue.date = date.medicinalProductAuthorizationProcedureDateToProto()
    }
    return protoValue.build()
  }

  private fun MedicinalProductAuthorization.JurisdictionalAuthorization.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationJurisdictionalAuthorizationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductAuthorization
        .MedicinalProductAuthorizationJurisdictionalAuthorizationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasCountry()) {
      hapiValue.country = country.toHapi()
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasLegalStatusOfSupply()) {
      hapiValue.legalStatusOfSupply = legalStatusOfSupply.toHapi()
    }
    if (hasValidityPeriod()) {
      hapiValue.validityPeriod = validityPeriod.toHapi()
    }
    return hapiValue
  }

  private fun MedicinalProductAuthorization.Procedure.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductAuthorization.MedicinalProductAuthorizationProcedureComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductAuthorization
        .MedicinalProductAuthorizationProcedureComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasDate()) {
      hapiValue.date = date.medicinalProductAuthorizationProcedureDateToHapi()
    }
    return hapiValue
  }
}
