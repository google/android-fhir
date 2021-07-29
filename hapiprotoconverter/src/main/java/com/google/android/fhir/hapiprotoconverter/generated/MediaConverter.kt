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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.EventStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Media
import com.google.fhir.r4.core.Period
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object MediaConverter {
  private fun Media.CreatedX.mediaCreatedToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Media.created[x]")
  }

  private fun Type.mediaCreatedToProto(): Media.CreatedX {
    val protoValue = Media.CreatedX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public fun Media.toHapi(): org.hl7.fhir.r4.model.Media {
    val hapiValue = org.hl7.fhir.r4.model.Media()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setBasedOn(basedOnList.map { it.toHapi() })
    hapiValue.setPartOf(partOfList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Media.MediaStatus.valueOf(status.value.name.replace("_", ""))
    )
    hapiValue.setType(type.toHapi())
    hapiValue.setModality(modality.toHapi())
    hapiValue.setView(view.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setCreated(created.mediaCreatedToHapi())
    hapiValue.setIssuedElement(issued.toHapi())
    hapiValue.setOperator(operator.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setBodySite(bodySite.toHapi())
    hapiValue.setDeviceNameElement(deviceName.toHapi())
    hapiValue.setDevice(device.toHapi())
    hapiValue.setHeightElement(height.toHapi())
    hapiValue.setWidthElement(width.toHapi())
    hapiValue.setFramesElement(frames.toHapi())
    hapiValue.setDurationElement(duration.toHapi())
    hapiValue.setContent(content.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Media.toProto(): Media {
    val protoValue =
      Media.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllBasedOn(basedOn.map { it.toProto() })
        .addAllPartOf(partOf.map { it.toProto() })
        .setStatus(
          Media.StatusCode.newBuilder()
            .setValue(
              EventStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setType(type.toProto())
        .setModality(modality.toProto())
        .setView(view.toProto())
        .setSubject(subject.toProto())
        .setEncounter(encounter.toProto())
        .setCreated(created.mediaCreatedToProto())
        .setIssued(issuedElement.toProto())
        .setOperator(operator.toProto())
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .setBodySite(bodySite.toProto())
        .setDeviceName(deviceNameElement.toProto())
        .setDevice(device.toProto())
        .setHeight(heightElement.toProto())
        .setWidth(widthElement.toProto())
        .setFrames(framesElement.toProto())
        .setDuration(durationElement.toProto())
        .setContent(content.toProto())
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }
}
