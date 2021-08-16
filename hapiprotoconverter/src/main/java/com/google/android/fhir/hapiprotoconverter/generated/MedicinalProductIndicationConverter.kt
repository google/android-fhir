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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PopulationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PopulationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductIndication
import com.google.fhir.r4.core.MedicinalProductIndication.OtherTherapy
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Type

public object MedicinalProductIndicationConverter {
  @JvmStatic
  private fun MedicinalProductIndication.OtherTherapy.MedicationX.medicinalProductIndicationOtherTherapyMedicationToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicinalProductIndication.otherTherapy.medication[x]"
    )
  }

  @JvmStatic
  private fun Type.medicinalProductIndicationOtherTherapyMedicationToProto():
    MedicinalProductIndication.OtherTherapy.MedicationX {
    val protoValue = MedicinalProductIndication.OtherTherapy.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicinalProductIndication.toHapi(): org.hl7.fhir.r4.model.MedicinalProductIndication {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductIndication()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (subjectCount > 0) {
      hapiValue.setSubject(subjectList.map { it.toHapi() })
    }
    if (hasDiseaseSymptomProcedure()) {
      hapiValue.setDiseaseSymptomProcedure(diseaseSymptomProcedure.toHapi())
    }
    if (hasDiseaseStatus()) {
      hapiValue.setDiseaseStatus(diseaseStatus.toHapi())
    }
    if (comorbidityCount > 0) {
      hapiValue.setComorbidity(comorbidityList.map { it.toHapi() })
    }
    if (hasIntendedEffect()) {
      hapiValue.setIntendedEffect(intendedEffect.toHapi())
    }
    if (hasDuration()) {
      hapiValue.setDuration(duration.toHapi())
    }
    if (otherTherapyCount > 0) {
      hapiValue.setOtherTherapy(otherTherapyList.map { it.toHapi() })
    }
    if (undesirableEffectCount > 0) {
      hapiValue.setUndesirableEffect(undesirableEffectList.map { it.toHapi() })
    }
    if (populationCount > 0) {
      hapiValue.setPopulation(populationList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicinalProductIndication.toProto():
    MedicinalProductIndication {
    val protoValue = MedicinalProductIndication.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasDiseaseSymptomProcedure()) {
      protoValue.setDiseaseSymptomProcedure(diseaseSymptomProcedure.toProto())
    }
    if (hasDiseaseStatus()) {
      protoValue.setDiseaseStatus(diseaseStatus.toProto())
    }
    if (hasComorbidity()) {
      protoValue.addAllComorbidity(comorbidity.map { it.toProto() })
    }
    if (hasIntendedEffect()) {
      protoValue.setIntendedEffect(intendedEffect.toProto())
    }
    if (hasDuration()) {
      protoValue.setDuration(duration.toProto())
    }
    if (hasOtherTherapy()) {
      protoValue.addAllOtherTherapy(otherTherapy.map { it.toProto() })
    }
    if (hasUndesirableEffect()) {
      protoValue.addAllUndesirableEffect(undesirableEffect.map { it.toProto() })
    }
    if (hasPopulation()) {
      protoValue.addAllPopulation(population.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductIndication.MedicinalProductIndicationOtherTherapyComponent.toProto():
    MedicinalProductIndication.OtherTherapy {
    val protoValue =
      MedicinalProductIndication.OtherTherapy.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTherapyRelationshipType()) {
      protoValue.setTherapyRelationshipType(therapyRelationshipType.toProto())
    }
    if (hasMedication()) {
      protoValue.setMedication(medication.medicinalProductIndicationOtherTherapyMedicationToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicinalProductIndication.OtherTherapy.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIndication.MedicinalProductIndicationOtherTherapyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIndication
        .MedicinalProductIndicationOtherTherapyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasTherapyRelationshipType()) {
      hapiValue.setTherapyRelationshipType(therapyRelationshipType.toHapi())
    }
    if (hasMedication()) {
      hapiValue.setMedication(medication.medicinalProductIndicationOtherTherapyMedicationToHapi())
    }
    return hapiValue
  }
}
