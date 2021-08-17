/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationStatement
import com.google.fhir.r4.core.MedicationStatementStatusCodes
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Type

object MedicationStatementConverter {
  private fun MedicationStatement.MedicationX.medicationStatementMedicationToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationStatement.medication[x]")
  }

  private fun Type.medicationStatementMedicationToProto(): MedicationStatement.MedicationX {
    val protoValue = MedicationStatement.MedicationX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationStatement.EffectiveX.medicationStatementEffectiveToHapi(): Type {
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationStatement.effective[x]")
  }

  private fun Type.medicationStatementEffectiveToProto(): MedicationStatement.EffectiveX {
    val protoValue = MedicationStatement.EffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  fun MedicationStatement.toHapi(): org.hl7.fhir.r4.model.MedicationStatement {
    val hapiValue = org.hl7.fhir.r4.model.MedicationStatement()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (hasMeta()) {
      hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
      hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasText()) {
      hapiValue.text = text.toHapi()
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.MedicationStatement.MedicationStatementStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (statusReasonCount > 0) {
      hapiValue.statusReason = statusReasonList.map { it.toHapi() }
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasMedication()) {
      hapiValue.medication = medication.medicationStatementMedicationToHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasContext()) {
      hapiValue.context = context.toHapi()
    }
    if (hasEffective()) {
      hapiValue.effective = effective.medicationStatementEffectiveToHapi()
    }
    if (hasDateAsserted()) {
      hapiValue.dateAssertedElement = dateAsserted.toHapi()
    }
    if (hasInformationSource()) {
      hapiValue.informationSource = informationSource.toHapi()
    }
    if (derivedFromCount > 0) {
      hapiValue.derivedFrom = derivedFromList.map { it.toHapi() }
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (dosageCount > 0) {
      hapiValue.dosage = dosageList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MedicationStatement.toProto(): MedicationStatement {
    val protoValue = MedicationStatement.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
    if (hasMeta()) {
      protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
      protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasText()) {
      protoValue.text = text.toProto()
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        MedicationStatement.StatusCode.newBuilder()
          .setValue(
            MedicationStatementStatusCodes.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasStatusReason()) {
      protoValue.addAllStatusReason(statusReason.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasMedication()) {
      protoValue.medication = medication.medicationStatementMedicationToProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasContext()) {
      protoValue.context = context.toProto()
    }
    if (hasEffective()) {
      protoValue.effective = effective.medicationStatementEffectiveToProto()
    }
    if (hasDateAsserted()) {
      protoValue.dateAsserted = dateAssertedElement.toProto()
    }
    if (hasInformationSource()) {
      protoValue.informationSource = informationSource.toProto()
    }
    if (hasDerivedFrom()) {
      protoValue.addAllDerivedFrom(derivedFrom.map { it.toProto() })
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasDosage()) {
      protoValue.addAllDosage(dosage.map { it.toProto() })
    }
    return protoValue.build()
  }
}
