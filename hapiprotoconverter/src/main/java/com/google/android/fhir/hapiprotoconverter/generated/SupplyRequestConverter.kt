package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
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
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SupplyRequest
import com.google.fhir.r4.core.SupplyRequestStatusCode
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object SupplyRequestConverter {
  public fun SupplyRequest.ItemX.supplyRequestItemToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("SupplyRequest.item[x]")
  }

  public fun Type.supplyRequestItemToProto(): SupplyRequest.ItemX {
    val protoValue = SupplyRequest.ItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun SupplyRequest.Parameter.ValueX.supplyRequestParameterValueToHapi(): Type {
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
    throw IllegalArgumentException("SupplyRequest.parameter.value[x]")
  }

  public fun Type.supplyRequestParameterValueToProto(): SupplyRequest.Parameter.ValueX {
    val protoValue = SupplyRequest.Parameter.ValueX.newBuilder()
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

  public fun SupplyRequest.OccurrenceX.supplyRequestOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType ) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("SupplyRequest.occurrence[x]")
  }

  public fun Type.supplyRequestOccurrenceToProto(): SupplyRequest.OccurrenceX {
    val protoValue = SupplyRequest.OccurrenceX.newBuilder()
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

  public fun SupplyRequest.toHapi(): org.hl7.fhir.r4.model.SupplyRequest {
    val hapiValue = org.hl7.fhir.r4.model.SupplyRequest()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setCategory(category.toHapi())
    hapiValue.setPriority(org.hl7.fhir.r4.model.SupplyRequest.RequestPriority.valueOf(priority.value.name.replace("_","")))
    hapiValue.setItem(item.supplyRequestItemToHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setParameter(parameterList.map{it.toHapi()})
    hapiValue.setOccurrence(occurrence.supplyRequestOccurrenceToHapi())
    hapiValue.setAuthoredOnElement(authoredOn.toHapi())
    hapiValue.setRequester(requester.toHapi())
    hapiValue.setSupplier(supplierList.map{it.toHapi()})
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setDeliverFrom(deliverFrom.toHapi())
    hapiValue.setDeliverTo(deliverTo.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SupplyRequest.toProto(): SupplyRequest {
    val protoValue = SupplyRequest.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(SupplyRequest.StatusCode.newBuilder().setValue(SupplyRequestStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCategory(category.toProto())
    .setPriority(SupplyRequest.PriorityCode.newBuilder().setValue(RequestPriorityCode.Value.valueOf(priority.toCode().replace("-",
        "_").toUpperCase())).build())
    .setItem(item.supplyRequestItemToProto())
    .setQuantity(quantity.toProto())
    .addAllParameter(parameter.map{it.toProto()})
    .setOccurrence(occurrence.supplyRequestOccurrenceToProto())
    .setAuthoredOn(authoredOnElement.toProto())
    .setRequester(requester.toProto())
    .addAllSupplier(supplier.map{it.toProto()})
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .setDeliverFrom(deliverFrom.toProto())
    .setDeliverTo(deliverTo.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestParameterComponent.toProto():
      SupplyRequest.Parameter {
    val protoValue = SupplyRequest.Parameter.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(code.toProto())
    .setValue(value.supplyRequestParameterValueToProto())
    .build()
    return protoValue
  }

  public fun SupplyRequest.Parameter.toHapi():
      org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestParameterComponent {
    val hapiValue = org.hl7.fhir.r4.model.SupplyRequest.SupplyRequestParameterComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setValue(value.supplyRequestParameterValueToHapi())
    return hapiValue
  }
}
