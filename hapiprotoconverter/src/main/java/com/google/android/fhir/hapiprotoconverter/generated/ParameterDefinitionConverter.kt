package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.OperationParameterUseCode
import com.google.fhir.r4.core.ParameterDefinition
import com.google.fhir.r4.core.String

public object ParameterDefinitionConverter {
  public fun ParameterDefinition.toHapi(): org.hl7.fhir.r4.model.ParameterDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ParameterDefinition()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setUse(org.hl7.fhir.r4.model.ParameterDefinition.ParameterUse.valueOf(use.value.name.replace("_","")))
    hapiValue.setMinElement(min.toHapi())
    hapiValue.setMaxElement(max.toHapi())
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setType(type.value)
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
    .setType(ParameterDefinition.TypeCode.newBuilder().setValue(type).build())
    .setProfile(profileElement.toProto())
    .build()
    return protoValue
  }
}
