package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.EnrollmentRequest
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id

public object EnrollmentRequestConverter {
  public fun EnrollmentRequest.toHapi(): org.hl7.fhir.r4.model.EnrollmentRequest {
    val hapiValue = org.hl7.fhir.r4.model.EnrollmentRequest()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.EnrollmentRequest.EnrollmentRequestStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setInsurer(insurer.toHapi())
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setCandidate(candidate.toHapi())
    hapiValue.setCoverage(coverage.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.EnrollmentRequest.toProto(): EnrollmentRequest {
    val protoValue = EnrollmentRequest.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(EnrollmentRequest.StatusCode.newBuilder().setValue(FinancialResourceStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCreated(createdElement.toProto())
    .setInsurer(insurer.toProto())
    .setProvider(provider.toProto())
    .setCandidate(candidate.toProto())
    .setCoverage(coverage.toProto())
    .build()
    return protoValue
  }
}
