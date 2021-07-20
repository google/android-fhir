package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.Condition
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object ConditionConverter {
  public fun Condition.OnsetX.conditionOnsetToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType ) {
      return (this.getAge()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Condition.onset[x]")
  }

  public fun Type.conditionOnsetToProto(): Condition.OnsetX {
    val protoValue = Condition.OnsetX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public fun Condition.AbatementX.conditionAbatementToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType ) {
      return (this.getAge()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Condition.abatement[x]")
  }

  public fun Type.conditionAbatementToProto(): Condition.AbatementX {
    val protoValue = Condition.AbatementX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public fun Condition.toHapi(): org.hl7.fhir.r4.model.Condition {
    val hapiValue = org.hl7.fhir.r4.model.Condition()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setClinicalStatus(clinicalStatus.toHapi())
    hapiValue.setVerificationStatus(verificationStatus.toHapi())
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setSeverity(severity.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setBodySite(bodySiteList.map{it.toHapi()})
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setOnset(onset.conditionOnsetToHapi())
    hapiValue.setAbatement(abatement.conditionAbatementToHapi())
    hapiValue.setRecordedDateElement(recordedDate.toHapi())
    hapiValue.setRecorder(recorder.toHapi())
    hapiValue.setAsserter(asserter.toHapi())
    hapiValue.setStage(stageList.map{it.toHapi()})
    hapiValue.setEvidence(evidenceList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Condition.toProto(): Condition {
    val protoValue = Condition.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setClinicalStatus(clinicalStatus.toProto())
    .setVerificationStatus(verificationStatus.toProto())
    .addAllCategory(category.map{it.toProto()})
    .setSeverity(severity.toProto())
    .setCode(code.toProto())
    .addAllBodySite(bodySite.map{it.toProto()})
    .setSubject(subject.toProto())
    .setEncounter(encounter.toProto())
    .setOnset(onset.conditionOnsetToProto())
    .setAbatement(abatement.conditionAbatementToProto())
    .setRecordedDate(recordedDateElement.toProto())
    .setRecorder(recorder.toProto())
    .setAsserter(asserter.toProto())
    .addAllStage(stage.map{it.toProto()})
    .addAllEvidence(evidence.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Condition.ConditionStageComponent.toProto(): Condition.Stage {
    val protoValue = Condition.Stage.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSummary(summary.toProto())
    .addAllAssessment(assessment.map{it.toProto()})
    .setType(type.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent.toProto():
      Condition.Evidence {
    val protoValue = Condition.Evidence.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllCode(code.map{it.toProto()})
    .addAllDetail(detail.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun Condition.Stage.toHapi(): org.hl7.fhir.r4.model.Condition.ConditionStageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Condition.ConditionStageComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSummary(summary.toHapi())
    hapiValue.setAssessment(assessmentList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    return hapiValue
  }

  public fun Condition.Evidence.toHapi():
      org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent {
    val hapiValue = org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(codeList.map{it.toHapi()})
    hapiValue.setDetail(detailList.map{it.toHapi()})
    return hapiValue
  }
}
