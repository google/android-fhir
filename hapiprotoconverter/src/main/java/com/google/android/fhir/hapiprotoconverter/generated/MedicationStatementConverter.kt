package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toProto
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
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationStatement
import com.google.fhir.r4.core.MedicationStatusCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object MedicationStatementConverter {
  public fun MedicationStatement.MedicationX.medicationStatementMedicationToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationStatement.medication[x]")
  }

  public fun Type.medicationStatementMedicationToProto(): MedicationStatement.MedicationX {
    val protoValue = MedicationStatement.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun MedicationStatement.EffectiveX.medicationStatementEffectiveToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationStatement.effective[x]")
  }

  public fun Type.medicationStatementEffectiveToProto(): MedicationStatement.EffectiveX {
    val protoValue = MedicationStatement.EffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  public fun MedicationStatement.toHapi(): org.hl7.fhir.r4.model.MedicationStatement {
    val hapiValue = org.hl7.fhir.r4.model.MedicationStatement()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setPartOf(partOfList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.MedicationStatement.MedicationStatementStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setStatusReason(statusReasonList.map{it.toHapi()})
    hapiValue.setCategory(category.toHapi())
    hapiValue.setMedication(medication.medicationStatementMedicationToHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setContext(context.toHapi())
    hapiValue.setEffective(effective.medicationStatementEffectiveToHapi())
    hapiValue.setDateAssertedElement(dateAsserted.toHapi())
    hapiValue.setInformationSource(informationSource.toHapi())
    hapiValue.setDerivedFrom(derivedFromList.map{it.toHapi()})
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setDosage(dosageList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicationStatement.toProto(): MedicationStatement {
    val protoValue = MedicationStatement.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllPartOf(partOf.map{it.toProto()})
    .setStatus(MedicationStatement.StatusCode.newBuilder().setValue(MedicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllStatusReason(statusReason.map{it.toProto()})
    .setCategory(category.toProto())
    .setMedication(medication.medicationStatementMedicationToProto())
    .setSubject(subject.toProto())
    .setContext(context.toProto())
    .setEffective(effective.medicationStatementEffectiveToProto())
    .setDateAsserted(dateAssertedElement.toProto())
    .setInformationSource(informationSource.toProto())
    .addAllDerivedFrom(derivedFrom.map{it.toProto()})
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .addAllDosage(dosage.map{it.toProto()})
    .build()
    return protoValue
  }
}
