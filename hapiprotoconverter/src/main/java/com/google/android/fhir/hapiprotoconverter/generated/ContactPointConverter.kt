package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.ContactPoint
import com.google.fhir.r4.core.ContactPointSystemCode
import com.google.fhir.r4.core.ContactPointUseCode
import com.google.fhir.r4.core.String

public object ContactPointConverter {
  public fun ContactPoint.toHapi(): org.hl7.fhir.r4.model.ContactPoint {
    val hapiValue = org.hl7.fhir.r4.model.ContactPoint()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setSystem(org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem.valueOf(system.value.name.replace("_","")))
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setUse(org.hl7.fhir.r4.model.ContactPoint.ContactPointUse.valueOf(use.value.name.replace("_","")))
    hapiValue.setRankElement(rank.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ContactPoint.toProto(): ContactPoint {
    val protoValue = ContactPoint.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setSystem(ContactPoint.SystemCode.newBuilder().setValue(ContactPointSystemCode.Value.valueOf(system.toCode().replace("-",
        "_").toUpperCase())).build())
    .setValue(valueElement.toProto())
    .setUse(ContactPoint.UseCode.newBuilder().setValue(ContactPointUseCode.Value.valueOf(use.toCode().replace("-",
        "_").toUpperCase())).build())
    .setRank(rankElement.toProto())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }
}
