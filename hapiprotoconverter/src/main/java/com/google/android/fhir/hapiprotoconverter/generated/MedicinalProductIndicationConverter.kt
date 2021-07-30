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

public object MedicinalProductIndicationConverter {
  private
      fun MedicinalProductIndication.OtherTherapy.MedicationX.medicinalProductIndicationOtherTherapyMedicationToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for MedicinalProductIndication.otherTherapy.medication[x]")
  }

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

  public fun MedicinalProductIndication.toHapi(): org.hl7.fhir.r4.model.MedicinalProductIndication {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductIndication()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSubject(subjectList.map{it.toHapi()})
    hapiValue.setDiseaseSymptomProcedure(diseaseSymptomProcedure.toHapi())
    hapiValue.setDiseaseStatus(diseaseStatus.toHapi())
    hapiValue.setComorbidity(comorbidityList.map{it.toHapi()})
    hapiValue.setIntendedEffect(intendedEffect.toHapi())
    hapiValue.setDuration(duration.toHapi())
    hapiValue.setOtherTherapy(otherTherapyList.map{it.toHapi()})
    hapiValue.setUndesirableEffect(undesirableEffectList.map{it.toHapi()})
    hapiValue.setPopulation(populationList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductIndication.toProto():
      MedicinalProductIndication {
    val protoValue = MedicinalProductIndication.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllSubject(subject.map{it.toProto()})
    .setDiseaseSymptomProcedure(diseaseSymptomProcedure.toProto())
    .setDiseaseStatus(diseaseStatus.toProto())
    .addAllComorbidity(comorbidity.map{it.toProto()})
    .setIntendedEffect(intendedEffect.toProto())
    .setDuration(duration.toProto())
    .addAllOtherTherapy(otherTherapy.map{it.toProto()})
    .addAllUndesirableEffect(undesirableEffect.map{it.toProto()})
    .addAllPopulation(population.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicinalProductIndication.MedicinalProductIndicationOtherTherapyComponent.toProto():
      MedicinalProductIndication.OtherTherapy {
    val protoValue = MedicinalProductIndication.OtherTherapy.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setTherapyRelationshipType(therapyRelationshipType.toProto())
    .setMedication(medication.medicinalProductIndicationOtherTherapyMedicationToProto())
    .build()
    return protoValue
  }

  private fun MedicinalProductIndication.OtherTherapy.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductIndication.MedicinalProductIndicationOtherTherapyComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicinalProductIndication.MedicinalProductIndicationOtherTherapyComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setTherapyRelationshipType(therapyRelationshipType.toHapi())
    hapiValue.setMedication(medication.medicinalProductIndicationOtherTherapyMedicationToHapi())
    return hapiValue
  }
}
