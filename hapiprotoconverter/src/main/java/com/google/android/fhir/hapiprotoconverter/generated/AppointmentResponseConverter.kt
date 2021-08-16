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
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
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
import com.google.fhir.r4.core.AppointmentResponse
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ParticipationStatusCode
import kotlin.jvm.JvmStatic

public object AppointmentResponseConverter {
  @JvmStatic
  public fun AppointmentResponse.toHapi(): org.hl7.fhir.r4.model.AppointmentResponse {
    val hapiValue = org.hl7.fhir.r4.model.AppointmentResponse()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (hasAppointment()) {
      hapiValue.setAppointment(appointment.toHapi())
    }
    if (hasStart()) {
      hapiValue.setStartElement(start.toHapi())
    }
    if (hasEnd()) {
      hapiValue.setEndElement(end.toHapi())
    }
    if (participantTypeCount > 0) {
      hapiValue.setParticipantType(participantTypeList.map { it.toHapi() })
    }
    if (hasActor()) {
      hapiValue.setActor(actor.toHapi())
    }
    hapiValue.setParticipantStatus(
      org.hl7.fhir.r4.model.AppointmentResponse.ParticipantStatus.valueOf(
        participantStatus.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.AppointmentResponse.toProto(): AppointmentResponse {
    val protoValue = AppointmentResponse.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
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
    if (hasAppointment()) {
      protoValue.setAppointment(appointment.toProto())
    }
    if (hasStart()) {
      protoValue.setStart(startElement.toProto())
    }
    if (hasEnd()) {
      protoValue.setEnd(endElement.toProto())
    }
    if (hasParticipantType()) {
      protoValue.addAllParticipantType(participantType.map { it.toProto() })
    }
    if (hasActor()) {
      protoValue.setActor(actor.toProto())
    }
    protoValue.setParticipantStatus(
      AppointmentResponse.ParticipantStatusCode.newBuilder()
        .setValue(
          ParticipationStatusCode.Value.valueOf(
            participantStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    return protoValue.build()
  }
}
