package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Meta
import com.google.fhir.r4.core.String

public fun Meta.toHapi(): org.hl7.fhir.r4.model.Meta {
  val hapiValue = org.hl7.fhir.r4.model.Meta()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setVersionIdElement(versionId.toHapi())
  hapiValue.setLastUpdatedElement(lastUpdated.toHapi())
  hapiValue.setSourceElement(source.toHapi())
  hapiValue.setProfile(profileList.map{it.toHapi()})
  hapiValue.setSecurity(securityList.map{it.toHapi()})
  hapiValue.setTag(tagList.map{it.toHapi()})
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Meta.toProto(): Meta {
  val protoValue = Meta.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setVersionId(versionIdElement.toProto())
  .setLastUpdated(lastUpdatedElement.toProto())
  .setSource(sourceElement.toProto())
  .addAllProfile(profile.map{it.toProto()})
  .addAllSecurity(security.map{it.toProto()})
  .addAllTag(tag.map{it.toProto()})
  .build()
  return protoValue
}
