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

public object AppointmentResponseConverter {
  public fun AppointmentResponse.toHapi(): org.hl7.fhir.r4.model.AppointmentResponse {
    val hapiValue = org.hl7.fhir.r4.model.AppointmentResponse()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setAppointment(appointment.toHapi())
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    hapiValue.setParticipantType(participantTypeList.map { it.toHapi() })
    hapiValue.setActor(actor.toHapi())
    hapiValue.setParticipantStatus(
      org.hl7.fhir.r4.model.AppointmentResponse.ParticipantStatus.valueOf(
        participantStatus.value.name.replace("_", "")
      )
    )
    hapiValue.setCommentElement(comment.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.AppointmentResponse.toProto(): AppointmentResponse {
    val protoValue =
      AppointmentResponse.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setAppointment(appointment.toProto())
        .setStart(startElement.toProto())
        .setEnd(endElement.toProto())
        .addAllParticipantType(participantType.map { it.toProto() })
        .setActor(actor.toProto())
        .setParticipantStatus(
          AppointmentResponse.ParticipantStatusCode.newBuilder()
            .setValue(
              ParticipationStatusCode.Value.valueOf(
                participantStatus.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setComment(commentElement.toProto())
        .build()
    return protoValue
  }
}
