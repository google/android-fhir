package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.RequestIntentCode
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.RequestStatusCode
import com.google.fhir.r4.core.ServiceRequest
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object ServiceRequestConverter {
  public fun ServiceRequest.QuantityX.serviceRequestQuantityToHapi(): Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType ) {
      return (this.getRatio()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    throw IllegalArgumentException("ServiceRequest.quantity[x]")
  }

  public fun Type.serviceRequestQuantityToProto(): ServiceRequest.QuantityX {
    val protoValue = ServiceRequest.QuantityX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    return protoValue.build()
  }

  public fun ServiceRequest.OccurrenceX.serviceRequestOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType ) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("ServiceRequest.occurrence[x]")
  }

  public fun Type.serviceRequestOccurrenceToProto(): ServiceRequest.OccurrenceX {
    val protoValue = ServiceRequest.OccurrenceX.newBuilder()
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

  public fun ServiceRequest.AsNeededX.serviceRequestAsNeededToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("ServiceRequest.asNeeded[x]")
  }

  public fun Type.serviceRequestAsNeededToProto(): ServiceRequest.AsNeededX {
    val protoValue = ServiceRequest.AsNeededX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  public fun ServiceRequest.toHapi(): org.hl7.fhir.r4.model.ServiceRequest {
    val hapiValue = org.hl7.fhir.r4.model.ServiceRequest()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map{it.toHapi()})
    hapiValue.setInstantiatesUri(instantiatesUriList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setReplaces(replacesList.map{it.toHapi()})
    hapiValue.setRequisition(requisition.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setIntent(org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestIntent.valueOf(intent.value.name.replace("_","")))
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setPriority(org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestPriority.valueOf(priority.value.name.replace("_","")))
    hapiValue.setDoNotPerformElement(doNotPerform.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setOrderDetail(orderDetailList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.serviceRequestQuantityToHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setOccurrence(occurrence.serviceRequestOccurrenceToHapi())
    hapiValue.setAsNeeded(asNeeded.serviceRequestAsNeededToHapi())
    hapiValue.setAuthoredOnElement(authoredOn.toHapi())
    hapiValue.setRequester(requester.toHapi())
    hapiValue.setPerformerType(performerType.toHapi())
    hapiValue.setPerformer(performerList.map{it.toHapi()})
    hapiValue.setLocationCode(locationCodeList.map{it.toHapi()})
    hapiValue.setLocationReference(locationReferenceList.map{it.toHapi()})
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setInsurance(insuranceList.map{it.toHapi()})
    hapiValue.setSupportingInfo(supportingInfoList.map{it.toHapi()})
    hapiValue.setSpecimen(specimenList.map{it.toHapi()})
    hapiValue.setBodySite(bodySiteList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setPatientInstructionElement(patientInstruction.toHapi())
    hapiValue.setRelevantHistory(relevantHistoryList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ServiceRequest.toProto(): ServiceRequest {
    val protoValue = ServiceRequest.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllInstantiatesCanonical(instantiatesCanonical.map{it.toProto()})
    .addAllInstantiatesUri(instantiatesUri.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllReplaces(replaces.map{it.toProto()})
    .setRequisition(requisition.toProto())
    .setStatus(ServiceRequest.StatusCode.newBuilder().setValue(RequestStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setIntent(ServiceRequest.IntentCode.newBuilder().setValue(RequestIntentCode.Value.valueOf(intent.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllCategory(category.map{it.toProto()})
    .setPriority(ServiceRequest.PriorityCode.newBuilder().setValue(RequestPriorityCode.Value.valueOf(priority.toCode().replace("-",
        "_").toUpperCase())).build())
    .setDoNotPerform(doNotPerformElement.toProto())
    .setCode(code.toProto())
    .addAllOrderDetail(orderDetail.map{it.toProto()})
    .setQuantity(quantity.serviceRequestQuantityToProto())
    .setSubject(subject.toProto())
    .setEncounter(encounter.toProto())
    .setOccurrence(occurrence.serviceRequestOccurrenceToProto())
    .setAsNeeded(asNeeded.serviceRequestAsNeededToProto())
    .setAuthoredOn(authoredOnElement.toProto())
    .setRequester(requester.toProto())
    .setPerformerType(performerType.toProto())
    .addAllPerformer(performer.map{it.toProto()})
    .addAllLocationCode(locationCode.map{it.toProto()})
    .addAllLocationReference(locationReference.map{it.toProto()})
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .addAllInsurance(insurance.map{it.toProto()})
    .addAllSupportingInfo(supportingInfo.map{it.toProto()})
    .addAllSpecimen(specimen.map{it.toProto()})
    .addAllBodySite(bodySite.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .setPatientInstruction(patientInstructionElement.toProto())
    .addAllRelevantHistory(relevantHistory.map{it.toProto()})
    .build()
    return protoValue
  }
}
