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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DeviceUseStatement
import com.google.fhir.r4.core.DeviceUseStatementStatusCode
import com.google.fhir.r4.core.Id
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.Type

object DeviceUseStatementConverter {
  private fun DeviceUseStatement.TimingX.deviceUseStatementTimingToHapi(): Type {
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceUseStatement.timing[x]")
  }

  private fun Type.deviceUseStatementTimingToProto(): DeviceUseStatement.TimingX {
    val protoValue = DeviceUseStatement.TimingX.newBuilder()
    if (this is Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    return protoValue.build()
  }

  fun DeviceUseStatement.toHapi(): org.hl7.fhir.r4.model.DeviceUseStatement {
    val hapiValue = org.hl7.fhir.r4.model.DeviceUseStatement()
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
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.DeviceUseStatement.DeviceUseStatementStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (derivedFromCount > 0) {
      hapiValue.derivedFrom = derivedFromList.map { it.toHapi() }
    }
    if (hasTiming()) {
      hapiValue.timing = timing.deviceUseStatementTimingToHapi()
    }
    if (hasRecordedOn()) {
      hapiValue.recordedOnElement = recordedOn.toHapi()
    }
    if (hasSource()) {
      hapiValue.source = source.toHapi()
    }
    if (hasDevice()) {
      hapiValue.device = device.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (hasBodySite()) {
      hapiValue.bodySite = bodySite.toHapi()
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.DeviceUseStatement.toProto(): DeviceUseStatement {
    val protoValue = DeviceUseStatement.newBuilder()
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
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        DeviceUseStatement.StatusCode.newBuilder()
          .setValue(
            DeviceUseStatementStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasDerivedFrom()) {
      protoValue.addAllDerivedFrom(derivedFrom.map { it.toProto() })
    }
    if (hasTiming()) {
      protoValue.timing = timing.deviceUseStatementTimingToProto()
    }
    if (hasRecordedOn()) {
      protoValue.recordedOn = recordedOnElement.toProto()
    }
    if (hasSource()) {
      protoValue.source = source.toProto()
    }
    if (hasDevice()) {
      protoValue.device = device.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasBodySite()) {
      protoValue.bodySite = bodySite.toProto()
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }
}
