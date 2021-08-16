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

import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toProto
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
import com.google.fhir.r4.core.AdministrativeGenderCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.RelatedPerson
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object RelatedPersonConverter {
  @JvmStatic
  fun RelatedPerson.toHapi(): org.hl7.fhir.r4.model.RelatedPerson {
    val hapiValue = org.hl7.fhir.r4.model.RelatedPerson()
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
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (relationshipCount > 0) {
      hapiValue.relationship = relationshipList.map { it.toHapi() }
    }
    if (nameCount > 0) {
      hapiValue.name = nameList.map { it.toHapi() }
    }
    if (telecomCount > 0) {
      hapiValue.telecom = telecomList.map { it.toHapi() }
    }
    hapiValue.gender =
      Enumerations.AdministrativeGender.valueOf(gender.value.name.hapiCodeCheck().replace("_", ""))
    if (hasBirthDate()) {
      hapiValue.birthDateElement = birthDate.toHapi()
    }
    if (addressCount > 0) {
      hapiValue.address = addressList.map { it.toHapi() }
    }
    if (photoCount > 0) {
      hapiValue.photo = photoList.map { it.toHapi() }
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (communicationCount > 0) {
      hapiValue.communication = communicationList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.RelatedPerson.toProto(): RelatedPerson {
    val protoValue = RelatedPerson.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasRelationship()) {
      protoValue.addAllRelationship(relationship.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.addAllName(name.map { it.toProto() })
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    protoValue.gender =
      RelatedPerson.GenderCode.newBuilder()
        .setValue(
          AdministrativeGenderCode.Value.valueOf(
            gender.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasBirthDate()) {
      protoValue.birthDate = birthDateElement.toProto()
    }
    if (hasAddress()) {
      protoValue.addAllAddress(address.map { it.toProto() })
    }
    if (hasPhoto()) {
      protoValue.addAllPhoto(photo.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasCommunication()) {
      protoValue.addAllCommunication(communication.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RelatedPerson.RelatedPersonCommunicationComponent.toProto():
    RelatedPerson.Communication {
    val protoValue =
      RelatedPerson.Communication.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLanguage()) {
      protoValue.language = language.toProto()
    }
    if (hasPreferred()) {
      protoValue.preferred = preferredElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun RelatedPerson.Communication.toHapi():
    org.hl7.fhir.r4.model.RelatedPerson.RelatedPersonCommunicationComponent {
    val hapiValue = org.hl7.fhir.r4.model.RelatedPerson.RelatedPersonCommunicationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLanguage()) {
      hapiValue.language = language.toHapi()
    }
    if (hasPreferred()) {
      hapiValue.preferredElement = preferred.toHapi()
    }
    return hapiValue
  }
}
