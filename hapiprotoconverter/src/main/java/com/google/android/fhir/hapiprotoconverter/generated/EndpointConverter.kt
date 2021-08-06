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
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.fhir.r4.core.Endpoint
import com.google.fhir.r4.core.EndpointStatusCode
import com.google.fhir.r4.core.Id
import kotlin.jvm.JvmStatic

public object EndpointConverter {
  @JvmStatic
  public fun Endpoint.toHapi(): org.hl7.fhir.r4.model.Endpoint {
    val hapiValue = org.hl7.fhir.r4.model.Endpoint()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Endpoint.EndpointStatus.valueOf(status.value.name.replace("_", ""))
    )
    hapiValue.setConnectionType(connectionType.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setManagingOrganization(managingOrganization.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setPayloadType(payloadTypeList.map { it.toHapi() })
    payloadMimeTypeList.map { hapiValue.addPayloadMimeType(it.value) }
    hapiValue.setAddressElement(address.toHapi())
    hapiValue.setHeader(headerList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Endpoint.toProto(): Endpoint {
    val protoValue =
      Endpoint.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          Endpoint.StatusCode.newBuilder()
            .setValue(
              EndpointStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setConnectionType(connectionType.toProto())
        .setName(nameElement.toProto())
        .setManagingOrganization(managingOrganization.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setPeriod(period.toProto())
        .addAllPayloadType(payloadType.map { it.toProto() })
        .addAllPayloadMimeType(
          payloadMimeType.map {
            Endpoint.PayloadMimeTypeCode.newBuilder().setValue(it.value).build()
          }
        )
        .setAddress(addressElement.toProto())
        .addAllHeader(header.map { it.toProto() })
        .build()
    return protoValue
  }
}
