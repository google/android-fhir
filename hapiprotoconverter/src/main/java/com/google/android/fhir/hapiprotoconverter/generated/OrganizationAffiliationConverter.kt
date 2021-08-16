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

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.OrganizationAffiliation
import kotlin.jvm.JvmStatic

object OrganizationAffiliationConverter {
  @JvmStatic
  fun OrganizationAffiliation.toHapi(): org.hl7.fhir.r4.model.OrganizationAffiliation {
    val hapiValue = org.hl7.fhir.r4.model.OrganizationAffiliation()
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
    if (hasActive()) {
        hapiValue.activeElement = active.toHapi()
    }
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (hasOrganization()) {
        hapiValue.organization = organization.toHapi()
    }
    if (hasParticipatingOrganization()) {
        hapiValue.participatingOrganization = participatingOrganization.toHapi()
    }
    if (networkCount > 0) {
        hapiValue.network = networkList.map { it.toHapi() }
    }
    if (codeCount > 0) {
        hapiValue.code = codeList.map { it.toHapi() }
    }
    if (specialtyCount > 0) {
        hapiValue.specialty = specialtyList.map { it.toHapi() }
    }
    if (locationCount > 0) {
        hapiValue.location = locationList.map { it.toHapi() }
    }
    if (healthcareServiceCount > 0) {
        hapiValue.healthcareService = healthcareServiceList.map { it.toHapi() }
    }
    if (telecomCount > 0) {
        hapiValue.telecom = telecomList.map { it.toHapi() }
    }
    if (endpointCount > 0) {
        hapiValue.endpoint = endpointList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.OrganizationAffiliation.toProto(): OrganizationAffiliation {
    val protoValue = OrganizationAffiliation.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasActive()) {
        protoValue.active = activeElement.toProto()
    }
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasOrganization()) {
        protoValue.organization = organization.toProto()
    }
    if (hasParticipatingOrganization()) {
        protoValue.participatingOrganization = participatingOrganization.toProto()
    }
    if (hasNetwork()) {
      protoValue.addAllNetwork(network.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasSpecialty()) {
      protoValue.addAllSpecialty(specialty.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.addAllLocation(location.map { it.toProto() })
    }
    if (hasHealthcareService()) {
      protoValue.addAllHealthcareService(healthcareService.map { it.toProto() })
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasEndpoint()) {
      protoValue.addAllEndpoint(endpoint.map { it.toProto() })
    }
    return protoValue.build()
  }
}
