package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Population
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

public object PopulationConverter {
  public fun Population.AgeX.ageToHapi(): Type {
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Population.age[x]")
  }

  public fun Type.ageToProto(): Population.AgeX {
    val protoValue = Population.AgeX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  public fun Population.toHapi(): org.hl7.fhir.r4.model.Population {
    val hapiValue = org.hl7.fhir.r4.model.Population()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAge(age.ageToHapi())
    hapiValue.setGender(gender.toHapi())
    hapiValue.setRace(race.toHapi())
    hapiValue.setPhysiologicalCondition(physiologicalCondition.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Population.toProto(): Population {
    val protoValue = Population.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAge(age.ageToProto())
    .setGender(gender.toProto())
    .setRace(race.toProto())
    .setPhysiologicalCondition(physiologicalCondition.toProto())
    .build()
    return protoValue
  }
}
