package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.QuantityComparatorCode
import com.google.fhir.r4.core.String

public fun Quantity.toHapi(): org.hl7.fhir.r4.model.Quantity {
  val hapiValue = org.hl7.fhir.r4.model.Quantity()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setValueElement(value.toHapi())
  hapiValue.setComparator(org.hl7.fhir.r4.model.Quantity.QuantityComparator.valueOf(comparator.value.name.replace("_","")))
  hapiValue.setUnitElement(unit.toHapi())
  hapiValue.setSystemElement(system.toHapi())
  hapiValue.setCodeElement(code.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Quantity.toProto(): Quantity {
  val protoValue = Quantity.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setValue(valueElement.toProto())
  .setComparator(Quantity.ComparatorCode.newBuilder().setValue(QuantityComparatorCode.Value.valueOf(comparator.toCode().replace("-",
      "_").toUpperCase())).build())
  .setUnit(unitElement.toProto())
  .setSystem(systemElement.toProto())
  .setCode(codeElement.toProto())
  .build()
  return protoValue
}
