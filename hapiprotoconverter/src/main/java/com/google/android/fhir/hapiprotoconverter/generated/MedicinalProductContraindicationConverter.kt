package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductContraindication
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

public object MedicinalProductContraindicationConverter {
  public
      fun MedicinalProductContraindication.OtherTherapy.MedicationX.medicinalProductContraindicationOtherTherapyMedicationToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for MedicinalProductContraindication.otherTherapy.medication[x]")
  }

  public fun Type.medicinalProductContraindicationOtherTherapyMedicationToProto():
      MedicinalProductContraindication.OtherTherapy.MedicationX {
    val protoValue = MedicinalProductContraindication.OtherTherapy.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun MedicinalProductContraindication.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductContraindication {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductContraindication()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSubject(subjectList.map{it.toHapi()})
    hapiValue.setDisease(disease.toHapi())
    hapiValue.setDiseaseStatus(diseaseStatus.toHapi())
    hapiValue.setComorbidity(comorbidityList.map{it.toHapi()})
    hapiValue.setTherapeuticIndication(therapeuticIndicationList.map{it.toHapi()})
    hapiValue.setOtherTherapy(otherTherapyList.map{it.toHapi()})
    hapiValue.setPopulation(populationList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductContraindication.toProto():
      MedicinalProductContraindication {
    val protoValue = MedicinalProductContraindication.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllSubject(subject.map{it.toProto()})
    .setDisease(disease.toProto())
    .setDiseaseStatus(diseaseStatus.toProto())
    .addAllComorbidity(comorbidity.map{it.toProto()})
    .addAllTherapeuticIndication(therapeuticIndication.map{it.toProto()})
    .addAllOtherTherapy(otherTherapy.map{it.toProto()})
    .addAllPopulation(population.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.MedicinalProductContraindication.MedicinalProductContraindicationOtherTherapyComponent.toProto():
      MedicinalProductContraindication.OtherTherapy {
    val protoValue = MedicinalProductContraindication.OtherTherapy.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setTherapyRelationshipType(therapyRelationshipType.toProto())
    .setMedication(medication.medicinalProductContraindicationOtherTherapyMedicationToProto())
    .build()
    return protoValue
  }

  public fun MedicinalProductContraindication.OtherTherapy.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductContraindication.MedicinalProductContraindicationOtherTherapyComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicinalProductContraindication.MedicinalProductContraindicationOtherTherapyComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setTherapyRelationshipType(therapyRelationshipType.toHapi())
    hapiValue.setMedication(medication.medicinalProductContraindicationOtherTherapyMedicationToHapi())
    return hapiValue
  }
}
