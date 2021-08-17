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
import org.hl7.fhir.r4.model.Type

object MedicinalProductIndicationConverter {
  private fun MedicinalProductIndication.OtherTherapy.MedicationX.medicinalProductIndicationOtherTherapyMedicationToHapi():
    Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicinalProductIndication.otherTherapy.medication[x]"
    )
  }

  private fun Type.medicinalProductIndicationOtherTherapyMedicationToProto():
    MedicinalProductIndication.OtherTherapy.MedicationX {
    val protoValue = MedicinalProductIndication.OtherTherapy.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  fun MedicinalProductIndication.toHapi(): org.hl7.fhir.r4.model.MedicinalProductIndication {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductIndication()
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
    if (subjectCount > 0) {
      hapiValue.subject = subjectList.map { it.toHapi() }
    }
    if (hasDiseaseSymptomProcedure()) {
      hapiValue.diseaseSymptomProcedure = diseaseSymptomProcedure.toHapi()
    }
    if (hasDiseaseStatus()) {
      hapiValue.diseaseStatus = diseaseStatus.toHapi()
    }
    if (comorbidityCount > 0) {
      hapiValue.comorbidity = comorbidityList.map { it.toHapi() }
    }
    if (hasIntendedEffect()) {
      hapiValue.intendedEffect = intendedEffect.toHapi()
    }
    if (hasDuration()) {
      hapiValue.duration = duration.toHapi()
    }
    if (otherTherapyCount > 0) {
      hapiValue.otherTherapy = otherTherapyList.map { it.toHapi() }
    }
    if (undesirableEffectCount > 0) {
      hapiValue.undesirableEffect = undesirableEffectList.map { it.toHapi() }
    }
    if (populationCount > 0) {
      hapiValue.population = populationList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MedicinalProductIndication.toProto(): MedicinalProductIndication {
    val protoValue = MedicinalProductIndication.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasDiseaseSymptomProcedure()) {
      protoValue.diseaseSymptomProcedure = diseaseSymptomProcedure.toProto()
    }
    if (hasDiseaseStatus()) {
      protoValue.diseaseStatus = diseaseStatus.toProto()
    }
    if (hasComorbidity()) {
      protoValue.addAllComorbidity(comorbidity.map { it.toProto() })
    }
    if (hasIntendedEffect()) {
      protoValue.intendedEffect = intendedEffect.toProto()
    }
    if (hasDuration()) {
      protoValue.duration = duration.toProto()
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
      protoValue.therapyRelationshipType = therapyRelationshipType.toProto()
    }
    if (hasMedication()) {
      protoValue.medication = medication.medicinalProductIndicationOtherTherapyMedicationToProto()
    }
    return protoValue.build()
  }

  private fun MedicinalProductIndication.OtherTherapy.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIndication.MedicinalProductIndicationOtherTherapyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIndication
        .MedicinalProductIndicationOtherTherapyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTherapyRelationshipType()) {
      hapiValue.therapyRelationshipType = therapyRelationshipType.toHapi()
    }
    if (hasMedication()) {
      hapiValue.medication = medication.medicinalProductIndicationOtherTherapyMedicationToHapi()
    }
    return hapiValue
  }
}
