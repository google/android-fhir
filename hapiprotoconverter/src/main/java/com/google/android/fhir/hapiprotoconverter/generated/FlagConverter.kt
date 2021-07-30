package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Flag
import com.google.fhir.r4.core.FlagStatusCode
import com.google.fhir.r4.core.Id

public object FlagConverter {
  public fun Flag.toHapi(): org.hl7.fhir.r4.model.Flag {
    val hapiValue = org.hl7.fhir.r4.model.Flag()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Flag.FlagStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setAuthor(author.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Flag.toProto(): Flag {
    val protoValue = Flag.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(Flag.StatusCode.newBuilder().setValue(FlagStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllCategory(category.map{it.toProto()})
    .setCode(code.toProto())
    .setSubject(subject.toProto())
    .setPeriod(period.toProto())
    .setEncounter(encounter.toProto())
    .setAuthor(author.toProto())
    .build()
    return protoValue
  }
}
