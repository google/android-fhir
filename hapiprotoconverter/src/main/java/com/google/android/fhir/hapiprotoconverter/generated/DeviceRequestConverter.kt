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
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.DeviceRequest
import com.google.fhir.r4.core.DeviceRequest.Parameter
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestIntentCode
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.RequestStatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object DeviceRequestConverter {
  @JvmStatic
  private fun DeviceRequest.CodeX.deviceRequestCodeToHapi(): Type {
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceRequest.code[x]")
  }

  @JvmStatic
  private fun Type.deviceRequestCodeToProto(): DeviceRequest.CodeX {
    val protoValue = DeviceRequest.CodeX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DeviceRequest.Parameter.ValueX.deviceRequestParameterValueToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceRequest.parameter.value[x]")
  }

  @JvmStatic
  private fun Type.deviceRequestParameterValueToProto(): DeviceRequest.Parameter.ValueX {
    val protoValue = DeviceRequest.Parameter.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DeviceRequest.OccurrenceX.deviceRequestOccurrenceToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceRequest.occurrence[x]")
  }

  @JvmStatic
  private fun Type.deviceRequestOccurrenceToProto(): DeviceRequest.OccurrenceX {
    val protoValue = DeviceRequest.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun DeviceRequest.toHapi(): org.hl7.fhir.r4.model.DeviceRequest {
    val hapiValue = org.hl7.fhir.r4.model.DeviceRequest()
    hapiValue.id = id.value
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
    if (instantiatesCanonicalCount > 0) {
      hapiValue.instantiatesCanonical = instantiatesCanonicalList.map { it.toHapi() }
    }
    if (instantiatesUriCount > 0) {
      hapiValue.instantiatesUri = instantiatesUriList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (priorRequestCount > 0) {
      hapiValue.priorRequest = priorRequestList.map { it.toHapi() }
    }
    if (hasGroupIdentifier()) {
      hapiValue.groupIdentifier = groupIdentifier.toHapi()
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.intent =
      org.hl7.fhir.r4.model.DeviceRequest.RequestIntent.valueOf(
        intent.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.priority =
      org.hl7.fhir.r4.model.DeviceRequest.RequestPriority.valueOf(
        priority.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCode()) {
      hapiValue.code = code.deviceRequestCodeToHapi()
    }
    if (parameterCount > 0) {
      hapiValue.parameter = parameterList.map { it.toHapi() }
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasOccurrence()) {
      hapiValue.occurrence = occurrence.deviceRequestOccurrenceToHapi()
    }
    if (hasAuthoredOn()) {
      hapiValue.authoredOnElement = authoredOn.toHapi()
    }
    if (hasRequester()) {
      hapiValue.requester = requester.toHapi()
    }
    if (hasPerformerType()) {
      hapiValue.performerType = performerType.toHapi()
    }
    if (hasPerformer()) {
      hapiValue.performer = performer.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (insuranceCount > 0) {
      hapiValue.insurance = insuranceList.map { it.toHapi() }
    }
    if (supportingInfoCount > 0) {
      hapiValue.supportingInfo = supportingInfoList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (relevantHistoryCount > 0) {
      hapiValue.relevantHistory = relevantHistoryList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.DeviceRequest.toProto(): DeviceRequest {
    val protoValue = DeviceRequest.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasInstantiatesCanonical()) {
      protoValue.addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
    }
    if (hasInstantiatesUri()) {
      protoValue.addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasPriorRequest()) {
      protoValue.addAllPriorRequest(priorRequest.map { it.toProto() })
    }
    if (hasGroupIdentifier()) {
      protoValue.groupIdentifier = groupIdentifier.toProto()
    }
    protoValue.status =
      DeviceRequest.StatusCode.newBuilder()
        .setValue(
          RequestStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.intent =
      DeviceRequest.IntentCode.newBuilder()
        .setValue(
          RequestIntentCode.Value.valueOf(
            intent.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.priority =
      DeviceRequest.PriorityCode.newBuilder()
        .setValue(
          RequestPriorityCode.Value.valueOf(
            priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCode()) {
      protoValue.code = code.deviceRequestCodeToProto()
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasOccurrence()) {
      protoValue.occurrence = occurrence.deviceRequestOccurrenceToProto()
    }
    if (hasAuthoredOn()) {
      protoValue.authoredOn = authoredOnElement.toProto()
    }
    if (hasRequester()) {
      protoValue.requester = requester.toProto()
    }
    if (hasPerformerType()) {
      protoValue.performerType = performerType.toProto()
    }
    if (hasPerformer()) {
      protoValue.performer = performer.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasInsurance()) {
      protoValue.addAllInsurance(insurance.map { it.toProto() })
    }
    if (hasSupportingInfo()) {
      protoValue.addAllSupportingInfo(supportingInfo.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasRelevantHistory()) {
      protoValue.addAllRelevantHistory(relevantHistory.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestParameterComponent.toProto():
    DeviceRequest.Parameter {
    val protoValue = DeviceRequest.Parameter.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasValue()) {
      protoValue.value = value.deviceRequestParameterValueToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DeviceRequest.Parameter.toHapi():
    org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestParameterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.deviceRequestParameterValueToHapi()
    }
    return hapiValue
  }
}
