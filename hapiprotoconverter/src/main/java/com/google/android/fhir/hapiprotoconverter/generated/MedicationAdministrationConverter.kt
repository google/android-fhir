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
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationAdministration
import com.google.fhir.r4.core.MedicationAdministration.Dosage
import com.google.fhir.r4.core.MedicationAdministrationStatusCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object MedicationAdministrationConverter {
  @JvmStatic
  private fun MedicationAdministration.MedicationX.medicationAdministrationMedicationToHapi():
    Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationAdministration.medication[x]")
  }

  @JvmStatic
  private fun Type.medicationAdministrationMedicationToProto():
    MedicationAdministration.MedicationX {
    val protoValue = MedicationAdministration.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationAdministration.EffectiveX.medicationAdministrationEffectiveToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationAdministration.effective[x]")
  }

  @JvmStatic
  private fun Type.medicationAdministrationEffectiveToProto(): MedicationAdministration.EffectiveX {
    val protoValue = MedicationAdministration.EffectiveX.newBuilder()
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationAdministration.Dosage.RateX.medicationAdministrationDosageRateToHapi():
    Type {
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.quantity != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationAdministration.dosage.rate[x]")
  }

  @JvmStatic
  private fun Type.medicationAdministrationDosageRateToProto():
    MedicationAdministration.Dosage.RateX {
    val protoValue = MedicationAdministration.Dosage.RateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Ratio) {
        protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
        protoValue.quantity = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun MedicationAdministration.toHapi(): org.hl7.fhir.r4.model.MedicationAdministration {
    val hapiValue = org.hl7.fhir.r4.model.MedicationAdministration()
    hapiValue.id = id.value
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
    if (instantiatesCount > 0) {
        hapiValue.instantiates = instantiatesList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
        hapiValue.partOf = partOfList.map { it.toHapi() }
    }
      hapiValue.status = org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (statusReasonCount > 0) {
        hapiValue.statusReason = statusReasonList.map { it.toHapi() }
    }
    if (hasCategory()) {
        hapiValue.category = category.toHapi()
    }
    if (hasMedication()) {
        hapiValue.medication = medication.medicationAdministrationMedicationToHapi()
    }
    if (hasSubject()) {
        hapiValue.subject = subject.toHapi()
    }
    if (hasContext()) {
        hapiValue.context = context.toHapi()
    }
    if (supportingInformationCount > 0) {
        hapiValue.supportingInformation = supportingInformationList.map { it.toHapi() }
    }
    if (hasEffective()) {
        hapiValue.effective = effective.medicationAdministrationEffectiveToHapi()
    }
    if (performerCount > 0) {
        hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (reasonCodeCount > 0) {
        hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
        hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (hasRequest()) {
        hapiValue.request = request.toHapi()
    }
    if (deviceCount > 0) {
        hapiValue.device = deviceList.map { it.toHapi() }
    }
    if (noteCount > 0) {
        hapiValue.note = noteList.map { it.toHapi() }
    }
    if (hasDosage()) {
        hapiValue.dosage = dosage.toHapi()
    }
    if (eventHistoryCount > 0) {
        hapiValue.eventHistory = eventHistoryList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.MedicationAdministration.toProto(): MedicationAdministration {
    val protoValue = MedicationAdministration.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasInstantiates()) {
      protoValue.addAllInstantiates(instantiates.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
      protoValue.status = MedicationAdministration.StatusCode.newBuilder()
          .setValue(
              MedicationAdministrationStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasStatusReason()) {
      protoValue.addAllStatusReason(statusReason.map { it.toProto() })
    }
    if (hasCategory()) {
        protoValue.category = category.toProto()
    }
    if (hasMedication()) {
        protoValue.medication = medication.medicationAdministrationMedicationToProto()
    }
    if (hasSubject()) {
        protoValue.subject = subject.toProto()
    }
    if (hasContext()) {
        protoValue.context = context.toProto()
    }
    if (hasSupportingInformation()) {
      protoValue.addAllSupportingInformation(supportingInformation.map { it.toProto() })
    }
    if (hasEffective()) {
        protoValue.effective = effective.medicationAdministrationEffectiveToProto()
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasRequest()) {
        protoValue.request = request.toProto()
    }
    if (hasDevice()) {
      protoValue.addAllDevice(device.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasDosage()) {
        protoValue.dosage = dosage.toProto()
    }
    if (hasEventHistory()) {
      protoValue.addAllEventHistory(eventHistory.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent.toProto():
    MedicationAdministration.Performer {
    val protoValue =
      MedicationAdministration.Performer.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFunction()) {
        protoValue.function = function.toProto()
    }
    if (hasActor()) {
        protoValue.actor = actor.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent.toProto():
    MedicationAdministration.Dosage {
    val protoValue =
      MedicationAdministration.Dosage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasText()) {
        protoValue.text = textElement.toProto()
    }
    if (hasSite()) {
        protoValue.site = site.toProto()
    }
    if (hasRoute()) {
        protoValue.route = route.toProto()
    }
    if (hasMethod()) {
        protoValue.method = method.toProto()
    }
    if (hasDose()) {
        protoValue.dose = (dose as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
    }
    if (hasRate()) {
        protoValue.rate = rate.medicationAdministrationDosageRateToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationAdministration.Performer.toHapi():
    org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFunction()) {
        hapiValue.function = function.toHapi()
    }
    if (hasActor()) {
        hapiValue.actor = actor.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationAdministration.Dosage.toHapi():
    org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasText()) {
        hapiValue.textElement = text.toHapi()
    }
    if (hasSite()) {
        hapiValue.site = site.toHapi()
    }
    if (hasRoute()) {
        hapiValue.route = route.toHapi()
    }
    if (hasMethod()) {
        hapiValue.method = method.toHapi()
    }
    if (hasDose()) {
        hapiValue.dose = dose.toHapi()
    }
    if (hasRate()) {
        hapiValue.rate = rate.medicationAdministrationDosageRateToHapi()
    }
    return hapiValue
  }
}
