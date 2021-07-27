package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.SampledData
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.SimpleQuantity

public fun SampledData.toHapi(): org.hl7.fhir.r4.model.SampledData {
  val hapiValue = org.hl7.fhir.r4.model.SampledData()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setOrigin(origin.toHapi())
  hapiValue.setPeriodElement(period.toHapi())
  hapiValue.setFactorElement(factor.toHapi())
  hapiValue.setLowerLimitElement(lowerLimit.toHapi())
  hapiValue.setUpperLimitElement(upperLimit.toHapi())
  hapiValue.setDimensionsElement(dimensions.toHapi())
  hapiValue.setDataElement(data.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.SampledData.toProto(): SampledData {
  val protoValue = SampledData.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setOrigin(( origin as SimpleQuantity ).toProto())
  .setPeriod(periodElement.toProto())
  .setFactor(factorElement.toProto())
  .setLowerLimit(lowerLimitElement.toProto())
  .setUpperLimit(upperLimitElement.toProto())
  .setDimensions(dimensionsElement.toProto())
  .setData(dataElement.toProto())
  .build()
  return protoValue
}
