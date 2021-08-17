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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.AdministrativeGenderCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.IdentityAssuranceLevelCode
import com.google.fhir.r4.core.Person
import com.google.fhir.r4.core.Person.Link
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

object PersonConverter {
  fun Person.toHapi(): org.hl7.fhir.r4.model.Person {
    val hapiValue = org.hl7.fhir.r4.model.Person()
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
    if (nameCount > 0) {
      hapiValue.name = nameList.map { it.toHapi() }
    }
    if (telecomCount > 0) {
      hapiValue.telecom = telecomList.map { it.toHapi() }
    }
    if (hasGender()) {
      hapiValue.gender =
        Enumerations.AdministrativeGender.valueOf(
          gender.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasBirthDate()) {
      hapiValue.birthDateElement = birthDate.toHapi()
    }
    if (addressCount > 0) {
      hapiValue.address = addressList.map { it.toHapi() }
    }
    if (hasPhoto()) {
      hapiValue.photo = photo.toHapi()
    }
    if (hasManagingOrganization()) {
      hapiValue.managingOrganization = managingOrganization.toHapi()
    }
    if (hasActive()) {
      hapiValue.activeElement = active.toHapi()
    }
    if (linkCount > 0) {
      hapiValue.link = linkList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Person.toProto(): Person {
    val protoValue = Person.newBuilder()
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
    if (hasName()) {
      protoValue.addAllName(name.map { it.toProto() })
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasGender()) {
      protoValue.gender =
        Person.GenderCode.newBuilder()
          .setValue(
            AdministrativeGenderCode.Value.valueOf(
              gender.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasBirthDate()) {
      protoValue.birthDate = birthDateElement.toProto()
    }
    if (hasAddress()) {
      protoValue.addAllAddress(address.map { it.toProto() })
    }
    if (hasPhoto()) {
      protoValue.photo = photo.toProto()
    }
    if (hasManagingOrganization()) {
      protoValue.managingOrganization = managingOrganization.toProto()
    }
    if (hasActive()) {
      protoValue.active = activeElement.toProto()
    }
    if (hasLink()) {
      protoValue.addAllLink(link.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Person.PersonLinkComponent.toProto(): Person.Link {
    val protoValue = Person.Link.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTarget()) {
      protoValue.target = target.toProto()
    }
    if (hasAssurance()) {
      protoValue.assurance =
        Person.Link.AssuranceCode.newBuilder()
          .setValue(
            IdentityAssuranceLevelCode.Value.valueOf(
              assurance.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    return protoValue.build()
  }

  private fun Person.Link.toHapi(): org.hl7.fhir.r4.model.Person.PersonLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.Person.PersonLinkComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTarget()) {
      hapiValue.target = target.toHapi()
    }
    if (hasAssurance()) {
      hapiValue.assurance =
        org.hl7.fhir.r4.model.Person.IdentityAssuranceLevel.valueOf(
          assurance.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    return hapiValue
  }
}
