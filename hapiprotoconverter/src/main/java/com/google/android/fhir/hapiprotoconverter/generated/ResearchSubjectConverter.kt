package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ResearchSubject
import com.google.fhir.r4.core.ResearchSubjectStatusCode

public object ResearchSubjectConverter {
  public fun ResearchSubject.toHapi(): org.hl7.fhir.r4.model.ResearchSubject {
    val hapiValue = org.hl7.fhir.r4.model.ResearchSubject()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.ResearchSubject.ResearchSubjectStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setStudy(study.toHapi())
    hapiValue.setIndividual(individual.toHapi())
    hapiValue.setAssignedArmElement(assignedArm.toHapi())
    hapiValue.setActualArmElement(actualArm.toHapi())
    hapiValue.setConsent(consent.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ResearchSubject.toProto(): ResearchSubject {
    val protoValue = ResearchSubject.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(ResearchSubject.StatusCode.newBuilder().setValue(ResearchSubjectStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPeriod(period.toProto())
    .setStudy(study.toProto())
    .setIndividual(individual.toProto())
    .setAssignedArm(assignedArmElement.toProto())
    .setActualArm(actualArmElement.toProto())
    .setConsent(consent.toProto())
    .build()
    return protoValue
  }
}
