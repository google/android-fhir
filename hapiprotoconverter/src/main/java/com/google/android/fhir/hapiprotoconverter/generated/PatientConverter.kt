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
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
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
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.LinkTypeCode
import com.google.fhir.r4.core.Patient
import com.google.fhir.r4.core.Patient.Contact
import com.google.fhir.r4.core.Patient.Link
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Type

public object PatientConverter {
  @JvmStatic
  private fun Patient.DeceasedX.patientDeceasedToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Patient.deceased[x]")
  }

  @JvmStatic
  private fun Type.patientDeceasedToProto(): Patient.DeceasedX {
    val protoValue = Patient.DeceasedX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Patient.MultipleBirthX.patientMultipleBirthToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType) {
      return (this.getInteger()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Patient.multipleBirth[x]")
  }

  @JvmStatic
  private fun Type.patientMultipleBirthToProto(): Patient.MultipleBirthX {
    val protoValue = Patient.MultipleBirthX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun Patient.toHapi(): org.hl7.fhir.r4.model.Patient {
    val hapiValue = org.hl7.fhir.r4.model.Patient()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setName(nameList.map { it.toHapi() })
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setGender(
      Enumerations.AdministrativeGender.valueOf(
        gender
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setBirthDateElement(birthDate.toHapi())
    hapiValue.setDeceased(deceased.patientDeceasedToHapi())
    hapiValue.setAddress(addressList.map { it.toHapi() })
    hapiValue.setMaritalStatus(maritalStatus.toHapi())
    hapiValue.setMultipleBirth(multipleBirth.patientMultipleBirthToHapi())
    hapiValue.setPhoto(photoList.map { it.toHapi() })
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setCommunication(communicationList.map { it.toHapi() })
    hapiValue.setGeneralPractitioner(generalPractitionerList.map { it.toHapi() })
    hapiValue.setManagingOrganization(managingOrganization.toHapi())
    hapiValue.setLink(linkList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Patient.toProto(): Patient {
    val protoValue =
      Patient.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setActive(activeElement.toProto())
        .addAllName(name.map { it.toProto() })
        .addAllTelecom(telecom.map { it.toProto() })
        .setGender(
          Patient.GenderCode.newBuilder()
            .setValue(
              AdministrativeGenderCode.Value.valueOf(
                gender
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setBirthDate(birthDateElement.toProto())
        .setDeceased(deceased.patientDeceasedToProto())
        .addAllAddress(address.map { it.toProto() })
        .setMaritalStatus(maritalStatus.toProto())
        .setMultipleBirth(multipleBirth.patientMultipleBirthToProto())
        .addAllPhoto(photo.map { it.toProto() })
        .addAllContact(contact.map { it.toProto() })
        .addAllCommunication(communication.map { it.toProto() })
        .addAllGeneralPractitioner(generalPractitioner.map { it.toProto() })
        .setManagingOrganization(managingOrganization.toProto())
        .addAllLink(link.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Patient.ContactComponent.toProto(): Patient.Contact {
    val protoValue =
      Patient.Contact.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllRelationship(relationship.map { it.toProto() })
        .setName(name.toProto())
        .addAllTelecom(telecom.map { it.toProto() })
        .setAddress(address.toProto())
        .setGender(
          Patient.Contact.GenderCode.newBuilder()
            .setValue(
              AdministrativeGenderCode.Value.valueOf(
                gender
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setOrganization(organization.toProto())
        .setPeriod(period.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent.toProto():
    Patient.Communication {
    val protoValue =
      Patient.Communication.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setLanguage(language.toProto())
        .setPreferred(preferredElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Patient.PatientLinkComponent.toProto(): Patient.Link {
    val protoValue =
      Patient.Link.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setOther(other.toProto())
        .setType(
          Patient.Link.TypeCode.newBuilder()
            .setValue(
              LinkTypeCode.Value.valueOf(
                type
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Patient.Contact.toHapi(): org.hl7.fhir.r4.model.Patient.ContactComponent {
    val hapiValue = org.hl7.fhir.r4.model.Patient.ContactComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setRelationship(relationshipList.map { it.toHapi() })
    hapiValue.setName(name.toHapi())
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setAddress(address.toHapi())
    hapiValue.setGender(
      Enumerations.AdministrativeGender.valueOf(
        gender
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setOrganization(organization.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Patient.Communication.toHapi():
    org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setLanguage(language.toHapi())
    hapiValue.setPreferredElement(preferred.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Patient.Link.toHapi(): org.hl7.fhir.r4.model.Patient.PatientLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.Patient.PatientLinkComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOther(other.toHapi())
    hapiValue.setType(
      org.hl7.fhir.r4.model.Patient.LinkType.valueOf(
        type
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    return hapiValue
  }
}
