package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.HumanName
import com.google.fhir.r4.core.NameUseCode
import com.google.fhir.r4.core.String

public fun HumanName.toHapi(): org.hl7.fhir.r4.model.HumanName {
  val hapiValue = org.hl7.fhir.r4.model.HumanName()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setUse(org.hl7.fhir.r4.model.HumanName.NameUse.valueOf(use.value.name.replace("_","")))
  hapiValue.setTextElement(text.toHapi())
  hapiValue.setFamilyElement(family.toHapi())
  hapiValue.setGiven(givenList.map{it.toHapi()})
  hapiValue.setPrefix(prefixList.map{it.toHapi()})
  hapiValue.setSuffix(suffixList.map{it.toHapi()})
  hapiValue.setPeriod(period.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.HumanName.toProto(): HumanName {
  val protoValue = HumanName.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setUse(HumanName.UseCode.newBuilder().setValue(NameUseCode.Value.valueOf(use.toCode().replace("-",
      "_").toUpperCase())).build())
  .setText(textElement.toProto())
  .setFamily(familyElement.toProto())
  .addAllGiven(given.map{it.toProto()})
  .addAllPrefix(prefix.map{it.toProto()})
  .addAllSuffix(suffix.map{it.toProto()})
  .setPeriod(period.toProto())
  .build()
  return protoValue
}
