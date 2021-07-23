package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
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
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.DiagnosticReport
import com.google.fhir.r4.core.DiagnosticReportStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object DiagnosticReportConverter {
  public fun DiagnosticReport.EffectiveX.diagnosticReportEffectiveToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DiagnosticReport.effective[x]")
  }

  public fun Type.diagnosticReportEffectiveToProto(): DiagnosticReport.EffectiveX {
    val protoValue = DiagnosticReport.EffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public fun DiagnosticReport.toHapi(): org.hl7.fhir.r4.model.DiagnosticReport {
    val hapiValue = org.hl7.fhir.r4.model.DiagnosticReport()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setEffective(effective.diagnosticReportEffectiveToHapi())
    hapiValue.setIssuedElement(issued.toHapi())
    hapiValue.setPerformer(performerList.map{it.toHapi()})
    hapiValue.setResultsInterpreter(resultsInterpreterList.map{it.toHapi()})
    hapiValue.setSpecimen(specimenList.map{it.toHapi()})
    hapiValue.setResult(resultList.map{it.toHapi()})
    hapiValue.setImagingStudy(imagingStudyList.map{it.toHapi()})
    hapiValue.setMedia(mediaList.map{it.toHapi()})
    hapiValue.setConclusionElement(conclusion.toHapi())
    hapiValue.setConclusionCode(conclusionCodeList.map{it.toHapi()})
    hapiValue.setPresentedForm(presentedFormList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.DiagnosticReport.toProto(): DiagnosticReport {
    val protoValue = DiagnosticReport.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .setStatus(DiagnosticReport.StatusCode.newBuilder().setValue(DiagnosticReportStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllCategory(category.map{it.toProto()})
    .setCode(code.toProto())
    .setSubject(subject.toProto())
    .setEncounter(encounter.toProto())
    .setEffective(effective.diagnosticReportEffectiveToProto())
    .setIssued(issuedElement.toProto())
    .addAllPerformer(performer.map{it.toProto()})
    .addAllResultsInterpreter(resultsInterpreter.map{it.toProto()})
    .addAllSpecimen(specimen.map{it.toProto()})
    .addAllResult(result.map{it.toProto()})
    .addAllImagingStudy(imagingStudy.map{it.toProto()})
    .addAllMedia(media.map{it.toProto()})
    .setConclusion(conclusionElement.toProto())
    .addAllConclusionCode(conclusionCode.map{it.toProto()})
    .addAllPresentedForm(presentedForm.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent.toProto():
      DiagnosticReport.Media {
    val protoValue = DiagnosticReport.Media.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setComment(commentElement.toProto())
    .setLink(link.toProto())
    .build()
    return protoValue
  }

  public fun DiagnosticReport.Media.toHapi():
      org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent {
    val hapiValue = org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setLink(link.toHapi())
    return hapiValue
  }
}
