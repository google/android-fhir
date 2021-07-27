package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.UsageContext
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

private fun UsageContext.ValueX.usageContextValueToHapi(): Type {
  if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
    return (this.getCodeableConcept()).toHapi()
  }
  if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
    return (this.getQuantity()).toHapi()
  }
  if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
    return (this.getRange()).toHapi()
  }
  if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
    return (this.getReference()).toHapi()
  }
  throw IllegalArgumentException("Invalid Type for UsageContext.value[x]")
}

private fun Type.usageContextValueToProto(): UsageContext.ValueX {
  val protoValue = UsageContext.ValueX.newBuilder()
  if (this is org.hl7.fhir.r4.model.CodeableConcept) {
    protoValue.setCodeableConcept(this.toProto())
  }
  if (this is org.hl7.fhir.r4.model.Quantity) {
    protoValue.setQuantity(this.toProto())
  }
  if (this is org.hl7.fhir.r4.model.Range) {
    protoValue.setRange(this.toProto())
  }
  if (this is org.hl7.fhir.r4.model.Reference) {
    protoValue.setReference(this.toProto())
  }
  return protoValue.build()
}

public fun UsageContext.toHapi(): org.hl7.fhir.r4.model.UsageContext {
  val hapiValue = org.hl7.fhir.r4.model.UsageContext()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setCode(code.toHapi())
  hapiValue.setValue(value.usageContextValueToHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.UsageContext.toProto(): UsageContext {
  val protoValue = UsageContext.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setCode(code.toProto())
  .setValue(value.usageContextValueToProto())
  .build()
  return protoValue
}
