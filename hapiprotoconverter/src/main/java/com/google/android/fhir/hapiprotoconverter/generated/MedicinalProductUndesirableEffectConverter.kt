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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductUndesirableEffect

public object MedicinalProductUndesirableEffectConverter {
  public fun MedicinalProductUndesirableEffect.toHapi():
      org.hl7.fhir.r4.model.MedicinalProductUndesirableEffect {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductUndesirableEffect()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSubject(subjectList.map{it.toHapi()})
    hapiValue.setSymptomConditionEffect(symptomConditionEffect.toHapi())
    hapiValue.setClassification(classification.toHapi())
    hapiValue.setFrequencyOfOccurrence(frequencyOfOccurrence.toHapi())
    hapiValue.setPopulation(populationList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductUndesirableEffect.toProto():
      MedicinalProductUndesirableEffect {
    val protoValue = MedicinalProductUndesirableEffect.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllSubject(subject.map{it.toProto()})
    .setSymptomConditionEffect(symptomConditionEffect.toProto())
    .setClassification(classification.toProto())
    .setFrequencyOfOccurrence(frequencyOfOccurrence.toProto())
    .addAllPopulation(population.map{it.toProto()})
    .build()
    return protoValue
  }
}
