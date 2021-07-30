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

import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.ContactDetail
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object ContactDetailConverter {
  @JvmStatic
  public fun ContactDetail.toHapi(): org.hl7.fhir.r4.model.ContactDetail {
    val hapiValue = org.hl7.fhir.r4.model.ContactDetail()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ContactDetail.toProto(): ContactDetail {
    val protoValue =
      ContactDetail.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setName(nameElement.toProto())
        .addAllTelecom(telecom.map { it.toProto() })
        .build()
    return protoValue
  }
}
