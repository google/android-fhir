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

import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.Contributor
import com.google.fhir.r4.core.ContributorTypeCode
import com.google.fhir.r4.core.String

public object ContributorConverter {
  public fun Contributor.toHapi(): org.hl7.fhir.r4.model.Contributor {
    val hapiValue = org.hl7.fhir.r4.model.Contributor()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setType(
      org.hl7.fhir.r4.model.Contributor.ContributorType.valueOf(type.value.name.replace("_", ""))
    )
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Contributor.toProto(): Contributor {
    val protoValue =
      Contributor.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setType(
          Contributor.TypeCode.newBuilder()
            .setValue(
              ContributorTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setName(nameElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .build()
    return protoValue
  }
}
