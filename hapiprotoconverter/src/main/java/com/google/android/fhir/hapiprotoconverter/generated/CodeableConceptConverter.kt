package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.String

public object CodeableConceptConverter {
  public fun CodeableConcept.toHapi(): org.hl7.fhir.r4.model.CodeableConcept {
    val hapiValue = org.hl7.fhir.r4.model.CodeableConcept()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setCoding(codingList.map{it.toHapi()})
    hapiValue.setTextElement(text.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.CodeableConcept.toProto(): CodeableConcept {
    val protoValue = CodeableConcept.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllCoding(coding.map{it.toProto()})
    .setText(textElement.toProto())
    .build()
    return protoValue
  }
}
