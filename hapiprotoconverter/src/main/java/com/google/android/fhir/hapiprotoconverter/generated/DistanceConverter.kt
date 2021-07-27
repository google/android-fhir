package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Distance
import com.google.fhir.r4.core.QuantityComparatorCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Quantity

public fun Distance.toHapi(): org.hl7.fhir.r4.model.Distance {
  val hapiValue = org.hl7.fhir.r4.model.Distance()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setValueElement(value.toHapi())
  hapiValue.setComparator(Quantity.QuantityComparator.valueOf(comparator.value.name.replace("_","")))
  hapiValue.setUnitElement(unit.toHapi())
  hapiValue.setSystemElement(system.toHapi())
  hapiValue.setCodeElement(code.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Distance.toProto(): Distance {
  val protoValue = Distance.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setValue(valueElement.toProto())
  .setComparator(Distance.ComparatorCode.newBuilder().setValue(QuantityComparatorCode.Value.valueOf(comparator.toCode().replace("-",
      "_").toUpperCase())).build())
  .setUnit(unitElement.toProto())
  .setSystem(systemElement.toProto())
  .setCode(codeElement.toProto())
  .build()
  return protoValue
}
