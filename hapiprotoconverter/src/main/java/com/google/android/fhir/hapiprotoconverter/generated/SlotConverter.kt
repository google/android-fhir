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

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Slot
import com.google.fhir.r4.core.SlotStatusCode

object SlotConverter {
  fun Slot.toHapi(): org.hl7.fhir.r4.model.Slot {
    val hapiValue = org.hl7.fhir.r4.model.Slot()
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
    if (serviceCategoryCount > 0) {
      hapiValue.serviceCategory = serviceCategoryList.map { it.toHapi() }
    }
    if (serviceTypeCount > 0) {
      hapiValue.serviceType = serviceTypeList.map { it.toHapi() }
    }
    if (specialtyCount > 0) {
      hapiValue.specialty = specialtyList.map { it.toHapi() }
    }
    if (hasAppointmentType()) {
      hapiValue.appointmentType = appointmentType.toHapi()
    }
    if (hasSchedule()) {
      hapiValue.schedule = schedule.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.Slot.SlotStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasStart()) {
      hapiValue.startElement = start.toHapi()
    }
    if (hasEnd()) {
      hapiValue.endElement = end.toHapi()
    }
    if (hasOverbooked()) {
      hapiValue.overbookedElement = overbooked.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Slot.toProto(): Slot {
    val protoValue = Slot.newBuilder()
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
    if (hasServiceCategory()) {
      protoValue.addAllServiceCategory(serviceCategory.map { it.toProto() })
    }
    if (hasServiceType()) {
      protoValue.addAllServiceType(serviceType.map { it.toProto() })
    }
    if (hasSpecialty()) {
      protoValue.addAllSpecialty(specialty.map { it.toProto() })
    }
    if (hasAppointmentType()) {
      protoValue.appointmentType = appointmentType.toProto()
    }
    if (hasSchedule()) {
      protoValue.schedule = schedule.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        Slot.StatusCode.newBuilder()
          .setValue(
            SlotStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasStart()) {
      protoValue.start = startElement.toProto()
    }
    if (hasEnd()) {
      protoValue.end = endElement.toProto()
    }
    if (hasOverbooked()) {
      protoValue.overbooked = overbookedElement.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    return protoValue.build()
  }
}
