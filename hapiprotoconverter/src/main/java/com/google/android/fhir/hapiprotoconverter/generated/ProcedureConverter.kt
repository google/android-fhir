package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
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
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.EventStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Procedure
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object ProcedureConverter {
  public fun Procedure.PerformedX.procedurePerformedToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType ) {
      return (this.getAge()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    throw IllegalArgumentException("Procedure.performed[x]")
  }

  public fun Type.procedurePerformedToProto(): Procedure.PerformedX {
    val protoValue = Procedure.PerformedX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    return protoValue.build()
  }

  public fun Procedure.toHapi(): org.hl7.fhir.r4.model.Procedure {
    val hapiValue = org.hl7.fhir.r4.model.Procedure()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map{it.toHapi()})
    hapiValue.setInstantiatesUri(instantiatesUriList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setPartOf(partOfList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Procedure.ProcedureStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setStatusReason(statusReason.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setPerformed(performed.procedurePerformedToHapi())
    hapiValue.setRecorder(recorder.toHapi())
    hapiValue.setAsserter(asserter.toHapi())
    hapiValue.setPerformer(performerList.map{it.toHapi()})
    hapiValue.setLocation(location.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setBodySite(bodySiteList.map{it.toHapi()})
    hapiValue.setOutcome(outcome.toHapi())
    hapiValue.setReport(reportList.map{it.toHapi()})
    hapiValue.setComplication(complicationList.map{it.toHapi()})
    hapiValue.setComplicationDetail(complicationDetailList.map{it.toHapi()})
    hapiValue.setFollowUp(followUpList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setFocalDevice(focalDeviceList.map{it.toHapi()})
    hapiValue.setUsedReference(usedReferenceList.map{it.toHapi()})
    hapiValue.setUsedCode(usedCodeList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Procedure.toProto(): Procedure {
    val protoValue = Procedure.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllInstantiatesCanonical(instantiatesCanonical.map{it.toProto()})
    .addAllInstantiatesUri(instantiatesUri.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllPartOf(partOf.map{it.toProto()})
    .setStatus(Procedure.StatusCode.newBuilder().setValue(EventStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setStatusReason(statusReason.toProto())
    .setCategory(category.toProto())
    .setCode(code.toProto())
    .setSubject(subject.toProto())
    .setEncounter(encounter.toProto())
    .setPerformed(performed.procedurePerformedToProto())
    .setRecorder(recorder.toProto())
    .setAsserter(asserter.toProto())
    .addAllPerformer(performer.map{it.toProto()})
    .setLocation(location.toProto())
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .addAllBodySite(bodySite.map{it.toProto()})
    .setOutcome(outcome.toProto())
    .addAllReport(report.map{it.toProto()})
    .addAllComplication(complication.map{it.toProto()})
    .addAllComplicationDetail(complicationDetail.map{it.toProto()})
    .addAllFollowUp(followUp.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .addAllFocalDevice(focalDevice.map{it.toProto()})
    .addAllUsedReference(usedReference.map{it.toProto()})
    .addAllUsedCode(usedCode.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent.toProto():
      Procedure.Performer {
    val protoValue = Procedure.Performer.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setFunction(function.toProto())
    .setActor(actor.toProto())
    .setOnBehalfOf(onBehalfOf.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Procedure.ProcedureFocalDeviceComponent.toProto():
      Procedure.FocalDevice {
    val protoValue = Procedure.FocalDevice.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAction(action.toProto())
    .setManipulated(manipulated.toProto())
    .build()
    return protoValue
  }

  public fun Procedure.Performer.toHapi():
      org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setFunction(function.toHapi())
    hapiValue.setActor(actor.toHapi())
    hapiValue.setOnBehalfOf(onBehalfOf.toHapi())
    return hapiValue
  }

  public fun Procedure.FocalDevice.toHapi():
      org.hl7.fhir.r4.model.Procedure.ProcedureFocalDeviceComponent {
    val hapiValue = org.hl7.fhir.r4.model.Procedure.ProcedureFocalDeviceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAction(action.toHapi())
    hapiValue.setManipulated(manipulated.toHapi())
    return hapiValue
  }
}
