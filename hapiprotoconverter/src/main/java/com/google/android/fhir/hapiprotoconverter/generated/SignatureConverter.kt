package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.fhir.r4.core.Signature
import com.google.fhir.r4.core.String

public object SignatureConverter {
  public fun Signature.toHapi(): org.hl7.fhir.r4.model.Signature {
    val hapiValue = org.hl7.fhir.r4.model.Signature()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setWhenElement(`when`.toHapi())
    hapiValue.setWho(who.toHapi())
    hapiValue.setOnBehalfOf(onBehalfOf.toHapi())
    hapiValue.setTargetFormat(targetFormat.value)
    hapiValue.setSigFormat(sigFormat.value)
    hapiValue.setDataElement(data.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Signature.toProto(): Signature {
    val protoValue = Signature.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllType(type.map{it.toProto()})
    .setWhen(whenElement.toProto())
    .setWho(who.toProto())
    .setOnBehalfOf(onBehalfOf.toProto())
    .setTargetFormat(Signature.TargetFormatCode.newBuilder().setValue(targetFormat).build())
    .setSigFormat(Signature.SigFormatCode.newBuilder().setValue(sigFormat).build())
    .setData(dataElement.toProto())
    .build()
    return protoValue
  }
}
