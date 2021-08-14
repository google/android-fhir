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

public object MedicationAdministrationConverter {
  @JvmStatic
  private fun MedicationAdministration.MedicationX.medicationAdministrationMedicationToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationAdministration.medication[x]")
  }

  @JvmStatic
  private fun Type.medicationAdministrationMedicationToProto():
    MedicationAdministration.MedicationX {
    val protoValue = MedicationAdministration.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationAdministration.EffectiveX.medicationAdministrationEffectiveToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationAdministration.effective[x]")
  }

  @JvmStatic
  private fun Type.medicationAdministrationEffectiveToProto(): MedicationAdministration.EffectiveX {
    val protoValue = MedicationAdministration.EffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationAdministration.Dosage.RateX.medicationAdministrationDosageRateToHapi():
    Type {
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType) {
      return (this.getRatio()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationAdministration.dosage.rate[x]")
  }

  @JvmStatic
  private fun Type.medicationAdministrationDosageRateToProto():
    MedicationAdministration.Dosage.RateX {
    val protoValue = MedicationAdministration.Dosage.RateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicationAdministration.toHapi(): org.hl7.fhir.r4.model.MedicationAdministration {
    val hapiValue = org.hl7.fhir.r4.model.MedicationAdministration()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setInstantiates(instantiatesList.map { it.toHapi() })
    hapiValue.setPartOf(partOfList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setStatusReason(statusReasonList.map { it.toHapi() })
    hapiValue.setCategory(category.toHapi())
    hapiValue.setMedication(medication.medicationAdministrationMedicationToHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setContext(context.toHapi())
    hapiValue.setSupportingInformation(supportingInformationList.map { it.toHapi() })
    hapiValue.setEffective(effective.medicationAdministrationEffectiveToHapi())
    hapiValue.setPerformer(performerList.map { it.toHapi() })
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setRequest(request.toHapi())
    hapiValue.setDevice(deviceList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setDosage(dosage.toHapi())
    hapiValue.setEventHistory(eventHistoryList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicationAdministration.toProto(): MedicationAdministration {
    val protoValue =
      MedicationAdministration.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllInstantiates(instantiates.map { it.toProto() })
        .addAllPartOf(partOf.map { it.toProto() })
        .setStatus(
          MedicationAdministration.StatusCode.newBuilder()
            .setValue(
              MedicationAdministrationStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllStatusReason(statusReason.map { it.toProto() })
        .setCategory(category.toProto())
        .setMedication(medication.medicationAdministrationMedicationToProto())
        .setSubject(subject.toProto())
        .setContext(context.toProto())
        .addAllSupportingInformation(supportingInformation.map { it.toProto() })
        .setEffective(effective.medicationAdministrationEffectiveToProto())
        .addAllPerformer(performer.map { it.toProto() })
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .setRequest(request.toProto())
        .addAllDevice(device.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .setDosage(dosage.toProto())
        .addAllEventHistory(eventHistory.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent.toProto():
    MedicationAdministration.Performer {
    val protoValue =
      MedicationAdministration.Performer.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setFunction(function.toProto())
        .setActor(actor.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent.toProto():
    MedicationAdministration.Dosage {
    val protoValue =
      MedicationAdministration.Dosage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setText(textElement.toProto())
        .setSite(site.toProto())
        .setRoute(route.toProto())
        .setMethod(method.toProto())
        .setDose((dose as org.hl7.fhir.r4.model.SimpleQuantity).toProto())
        .setRate(rate.medicationAdministrationDosageRateToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun MedicationAdministration.Performer.toHapi():
    org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationPerformerComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setFunction(function.toHapi())
    hapiValue.setActor(actor.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicationAdministration.Dosage.toHapi():
    org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationAdministration.MedicationAdministrationDosageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setSite(site.toHapi())
    hapiValue.setRoute(route.toHapi())
    hapiValue.setMethod(method.toHapi())
    hapiValue.setDose(dose.toHapi())
    hapiValue.setRate(rate.medicationAdministrationDosageRateToHapi())
    return hapiValue
  }
}
