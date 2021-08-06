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
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Organization
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object OrganizationConverter {
  @JvmStatic
  public fun Organization.toHapi(): org.hl7.fhir.r4.model.Organization {
    val hapiValue = org.hl7.fhir.r4.model.Organization()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setAlias(aliasList.map { it.toHapi() })
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setAddress(addressList.map { it.toHapi() })
    hapiValue.setPartOf(partOf.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Organization.toProto(): Organization {
    val protoValue =
      Organization.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setActive(activeElement.toProto())
        .addAllType(type.map { it.toProto() })
        .setName(nameElement.toProto())
        .addAllAlias(alias.map { it.toProto() })
        .addAllTelecom(telecom.map { it.toProto() })
        .addAllAddress(address.map { it.toProto() })
        .setPartOf(partOf.toProto())
        .addAllContact(contact.map { it.toProto() })
        .addAllEndpoint(endpoint.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Organization.OrganizationContactComponent.toProto():
    Organization.Contact {
    val protoValue =
      Organization.Contact.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPurpose(purpose.toProto())
        .setName(name.toProto())
        .addAllTelecom(telecom.map { it.toProto() })
        .setAddress(address.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Organization.Contact.toHapi():
    org.hl7.fhir.r4.model.Organization.OrganizationContactComponent {
    val hapiValue = org.hl7.fhir.r4.model.Organization.OrganizationContactComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPurpose(purpose.toHapi())
    hapiValue.setName(name.toHapi())
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setAddress(address.toHapi())
    return hapiValue
  }
}
