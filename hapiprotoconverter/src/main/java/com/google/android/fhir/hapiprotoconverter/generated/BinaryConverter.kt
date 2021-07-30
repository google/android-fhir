package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Binary
import com.google.fhir.r4.core.Id

public object BinaryConverter {
  public fun Binary.toHapi(): org.hl7.fhir.r4.model.Binary {
    val hapiValue = org.hl7.fhir.r4.model.Binary()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setContentType(contentType.value)
    hapiValue.setSecurityContext(securityContext.toHapi())
    hapiValue.setDataElement(data.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Binary.toProto(): Binary {
    val protoValue = Binary.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setContentType(Binary.ContentTypeCode.newBuilder().setValue(contentType).build())
    .setSecurityContext(securityContext.toProto())
    .setData(dataElement.toProto())
    .build()
    return protoValue
  }
}
