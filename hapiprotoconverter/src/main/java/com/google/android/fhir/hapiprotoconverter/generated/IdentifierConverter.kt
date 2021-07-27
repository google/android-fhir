package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Identifier
import com.google.fhir.r4.core.IdentifierUseCode
import com.google.fhir.r4.core.String

public fun Identifier.toHapi(): org.hl7.fhir.r4.model.Identifier {
  val hapiValue = org.hl7.fhir.r4.model.Identifier()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setUse(org.hl7.fhir.r4.model.Identifier.IdentifierUse.valueOf(use.value.name.replace("_","")))
  hapiValue.setType(type.toHapi())
  hapiValue.setSystemElement(system.toHapi())
  hapiValue.setValueElement(value.toHapi())
  hapiValue.setPeriod(period.toHapi())
  hapiValue.setAssigner(assigner.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Identifier.toProto(): Identifier {
  val protoValue = Identifier.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setUse(Identifier.UseCode.newBuilder().setValue(IdentifierUseCode.Value.valueOf(use.toCode().replace("-",
      "_").toUpperCase())).build())
  .setType(type.toProto())
  .setSystem(systemElement.toProto())
  .setValue(valueElement.toProto())
  .setPeriod(period.toProto())
  .setAssigner(assigner.toProto())
  .build()
  return protoValue
}
