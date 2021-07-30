package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Count
import com.google.fhir.r4.core.QuantityComparatorCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Quantity

public object CountConverter {
  public fun Count.toHapi(): org.hl7.fhir.r4.model.Count {
    val hapiValue = org.hl7.fhir.r4.model.Count()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setComparator(Quantity.QuantityComparator.valueOf(comparator.value.name.replace("_","")))
    hapiValue.setUnitElement(unit.toHapi())
    hapiValue.setSystemElement(system.toHapi())
    hapiValue.setCodeElement(code.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Count.toProto(): Count {
    val protoValue = Count.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setValue(valueElement.toProto())
    .setComparator(Count.ComparatorCode.newBuilder().setValue(QuantityComparatorCode.Value.valueOf(comparator.toCode().replace("-",
        "_").toUpperCase())).build())
    .setUnit(unitElement.toProto())
    .setSystem(systemElement.toProto())
    .setCode(codeElement.toProto())
    .build()
    return protoValue
  }
}
