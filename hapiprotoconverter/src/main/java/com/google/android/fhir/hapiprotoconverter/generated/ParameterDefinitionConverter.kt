package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.OperationParameterUseCode
import com.google.fhir.r4.core.ParameterDefinition
import com.google.fhir.r4.core.String

public fun ParameterDefinition.toHapi(): org.hl7.fhir.r4.model.ParameterDefinition {
  val hapiValue = org.hl7.fhir.r4.model.ParameterDefinition()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setNameElement(name.toHapi())
  hapiValue.setUse(org.hl7.fhir.r4.model.ParameterDefinition.ParameterUse.valueOf(use.value.name.replace("_","")))
  hapiValue.setMinElement(min.toHapi())
  hapiValue.setMaxElement(max.toHapi())
  hapiValue.setDocumentationElement(documentation.toHapi())
  //hapiValue.setTypeElement(type.toHapi())
  hapiValue.setProfileElement(profile.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.ParameterDefinition.toProto(): ParameterDefinition {
  val protoValue = ParameterDefinition.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setName(nameElement.toProto())
  .setUse(ParameterDefinition.UseCode.newBuilder().setValue(OperationParameterUseCode.Value.valueOf(use.toCode().replace("-",
      "_").toUpperCase())).build())
  .setMin(minElement.toProto())
  .setMax(maxElement.toProto())
  .setDocumentation(documentationElement.toProto())
  //.setType(typeElement.toProto())
  .setProfile(profileElement.toProto())
  .build()
  return protoValue
}
