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
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object DeviceRequestConverter {
  private fun DeviceRequest.CodeX.deviceRequestCodeToHapi(): Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceRequest.code[x]")
  }

  private fun Type.deviceRequestCodeToProto(): DeviceRequest.CodeX {
    val protoValue = DeviceRequest.CodeX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  private fun DeviceRequest.Parameter.ValueX.deviceRequestParameterValueToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceRequest.parameter.value[x]")
  }

  private fun Type.deviceRequestParameterValueToProto(): DeviceRequest.Parameter.ValueX {
    val protoValue = DeviceRequest.Parameter.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    return protoValue.build()
  }

  private fun DeviceRequest.OccurrenceX.deviceRequestOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType ) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceRequest.occurrence[x]")
  }

  private fun Type.deviceRequestOccurrenceToProto(): DeviceRequest.OccurrenceX {
    val protoValue = DeviceRequest.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    return protoValue.build()
  }

  public fun DeviceRequest.toHapi(): org.hl7.fhir.r4.model.DeviceRequest {
    val hapiValue = org.hl7.fhir.r4.model.DeviceRequest()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map{it.toHapi()})
    hapiValue.setInstantiatesUri(instantiatesUriList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setPriorRequest(priorRequestList.map{it.toHapi()})
    hapiValue.setGroupIdentifier(groupIdentifier.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setIntent(org.hl7.fhir.r4.model.DeviceRequest.RequestIntent.valueOf(intent.value.name.replace("_","")))
    hapiValue.setPriority(org.hl7.fhir.r4.model.DeviceRequest.RequestPriority.valueOf(priority.value.name.replace("_","")))
    hapiValue.setCode(code.deviceRequestCodeToHapi())
    hapiValue.setParameter(parameterList.map{it.toHapi()})
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setOccurrence(occurrence.deviceRequestOccurrenceToHapi())
    hapiValue.setAuthoredOnElement(authoredOn.toHapi())
    hapiValue.setRequester(requester.toHapi())
    hapiValue.setPerformerType(performerType.toHapi())
    hapiValue.setPerformer(performer.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setInsurance(insuranceList.map{it.toHapi()})
    hapiValue.setSupportingInfo(supportingInfoList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setRelevantHistory(relevantHistoryList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.DeviceRequest.toProto(): DeviceRequest {
    val protoValue = DeviceRequest.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllInstantiatesCanonical(instantiatesCanonical.map{it.toProto()})
    .addAllInstantiatesUri(instantiatesUri.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllPriorRequest(priorRequest.map{it.toProto()})
    .setGroupIdentifier(groupIdentifier.toProto())
    .setStatus(DeviceRequest.StatusCode.newBuilder().setValue(RequestStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setIntent(DeviceRequest.IntentCode.newBuilder().setValue(RequestIntentCode.Value.valueOf(intent.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPriority(DeviceRequest.PriorityCode.newBuilder().setValue(RequestPriorityCode.Value.valueOf(priority.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCode(code.deviceRequestCodeToProto())
    .addAllParameter(parameter.map{it.toProto()})
    .setSubject(subject.toProto())
    .setEncounter(encounter.toProto())
    .setOccurrence(occurrence.deviceRequestOccurrenceToProto())
    .setAuthoredOn(authoredOnElement.toProto())
    .setRequester(requester.toProto())
    .setPerformerType(performerType.toProto())
    .setPerformer(performer.toProto())
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .addAllInsurance(insurance.map{it.toProto()})
    .addAllSupportingInfo(supportingInfo.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .addAllRelevantHistory(relevantHistory.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestParameterComponent.toProto():
      DeviceRequest.Parameter {
    val protoValue = DeviceRequest.Parameter.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(code.toProto())
    .setValue(value.deviceRequestParameterValueToProto())
    .build()
    return protoValue
  }

  private fun DeviceRequest.Parameter.toHapi():
      org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceRequest.DeviceRequestParameterComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setValue(value.deviceRequestParameterValueToHapi())
    return hapiValue
  }
}
