package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Narrative
import com.google.fhir.r4.core.NarrativeStatusCode
import com.google.fhir.r4.core.String

public fun Narrative.toHapi(): org.hl7.fhir.r4.model.Narrative {
  val hapiValue = org.hl7.fhir.r4.model.Narrative()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setStatus(org.hl7.fhir.r4.model.Narrative.NarrativeStatus.valueOf(status.value.name.replace("_","")))
  //hapiValue.setDiv(div.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Narrative.toProto(): Narrative {
  val protoValue = Narrative.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setStatus(Narrative.StatusCode.newBuilder().setValue(NarrativeStatusCode.Value.valueOf(status.toCode().replace("-",
      "_").toUpperCase())).build())
  //.setDiv(div.toProto())
  .build()
  return protoValue
}
