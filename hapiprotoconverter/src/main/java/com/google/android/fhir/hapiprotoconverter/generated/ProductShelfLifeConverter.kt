package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.ProductShelfLife
import com.google.fhir.r4.core.String

public fun ProductShelfLife.toHapi(): org.hl7.fhir.r4.model.ProductShelfLife {
  val hapiValue = org.hl7.fhir.r4.model.ProductShelfLife()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
  hapiValue.setIdentifier(identifier.toHapi())
  hapiValue.setType(type.toHapi())
  hapiValue.setPeriod(period.toHapi())
  hapiValue.setSpecialPrecautionsForStorage(specialPrecautionsForStorageList.map{it.toHapi()})
  return hapiValue
}

public fun org.hl7.fhir.r4.model.ProductShelfLife.toProto(): ProductShelfLife {
  val protoValue = ProductShelfLife.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .addAllModifierExtension(modifierExtension.map{it.toProto()})
  .setIdentifier(identifier.toProto())
  .setType(type.toProto())
  .setPeriod(period.toProto())
  .addAllSpecialPrecautionsForStorage(specialPrecautionsForStorage.map{it.toProto()})
  .build()
  return protoValue
}
