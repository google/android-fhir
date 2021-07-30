package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.SimpleQuantity

public object RangeConverter {
  public fun Range.toHapi(): org.hl7.fhir.r4.model.Range {
    val hapiValue = org.hl7.fhir.r4.model.Range()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setLow(low.toHapi())
    hapiValue.setHigh(high.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Range.toProto(): Range {
    val protoValue = Range.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setLow(( low as SimpleQuantity ).toProto())
    .setHigh(( high as SimpleQuantity ).toProto())
    .build()
    return protoValue
  }
}
