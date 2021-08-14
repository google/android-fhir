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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object PersonConverter {
  @JvmStatic
  public fun Person.toHapi(): org.hl7.fhir.r4.model.Person {
    val hapiValue = org.hl7.fhir.r4.model.Person()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setName(nameList.map { it.toHapi() })
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setGender(
      Enumerations.AdministrativeGender.valueOf(gender.value.name.hapiCodeCheck().replace("_", ""))
    )
    hapiValue.setBirthDateElement(birthDate.toHapi())
    hapiValue.setAddress(addressList.map { it.toHapi() })
    hapiValue.setPhoto(photo.toHapi())
    hapiValue.setManagingOrganization(managingOrganization.toHapi())
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setLink(linkList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Person.toProto(): Person {
    val protoValue =
      Person.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllName(name.map { it.toProto() })
        .addAllTelecom(telecom.map { it.toProto() })
        .setGender(
          Person.GenderCode.newBuilder()
            .setValue(
              AdministrativeGenderCode.Value.valueOf(
                gender.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setBirthDate(birthDateElement.toProto())
        .addAllAddress(address.map { it.toProto() })
        .setPhoto(photo.toProto())
        .setManagingOrganization(managingOrganization.toProto())
        .setActive(activeElement.toProto())
        .addAllLink(link.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Person.PersonLinkComponent.toProto(): Person.Link {
    val protoValue =
      Person.Link.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setTarget(target.toProto())
        .setAssurance(
          Person.Link.AssuranceCode.newBuilder()
            .setValue(
              IdentityAssuranceLevelCode.Value.valueOf(
                assurance.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Person.Link.toHapi(): org.hl7.fhir.r4.model.Person.PersonLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.Person.PersonLinkComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTarget(target.toHapi())
    hapiValue.setAssurance(
      org.hl7.fhir.r4.model.Person.IdentityAssuranceLevel.valueOf(
        assurance.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    return hapiValue
  }
}
