package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.SpecimenContainedPreferenceCode
import com.google.fhir.r4.core.SpecimenDefinition
import com.google.fhir.r4.core.SpecimenDefinition.TypeTested
import com.google.fhir.r4.core.SpecimenDefinition.TypeTested.Container
import com.google.fhir.r4.core.SpecimenDefinition.TypeTested.Container.Additive
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object SpecimenDefinitionConverter {
  private
      fun SpecimenDefinition.TypeTested.Container.MinimumVolumeX.specimenDefinitionTypeTestedContainerMinimumVolumeToHapi():
      Type {
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for SpecimenDefinition.typeTested.container.minimumVolume[x]")
  }

  private fun Type.specimenDefinitionTypeTestedContainerMinimumVolumeToProto():
      SpecimenDefinition.TypeTested.Container.MinimumVolumeX {
    val protoValue = SpecimenDefinition.TypeTested.Container.MinimumVolumeX.newBuilder()
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  private
      fun SpecimenDefinition.TypeTested.Container.Additive.AdditiveX.specimenDefinitionTypeTestedContainerAdditiveAdditiveToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for SpecimenDefinition.typeTested.container.additive.additive[x]")
  }

  private fun Type.specimenDefinitionTypeTestedContainerAdditiveAdditiveToProto():
      SpecimenDefinition.TypeTested.Container.Additive.AdditiveX {
    val protoValue = SpecimenDefinition.TypeTested.Container.Additive.AdditiveX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun SpecimenDefinition.toHapi(): org.hl7.fhir.r4.model.SpecimenDefinition {
    val hapiValue = org.hl7.fhir.r4.model.SpecimenDefinition()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setTypeCollected(typeCollected.toHapi())
    hapiValue.setPatientPreparation(patientPreparationList.map{it.toHapi()})
    hapiValue.setTimeAspectElement(timeAspect.toHapi())
    hapiValue.setCollection(collectionList.map{it.toHapi()})
    hapiValue.setTypeTested(typeTestedList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SpecimenDefinition.toProto(): SpecimenDefinition {
    val protoValue = SpecimenDefinition.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifier.toProto())
    .setTypeCollected(typeCollected.toProto())
    .addAllPatientPreparation(patientPreparation.map{it.toProto()})
    .setTimeAspect(timeAspectElement.toProto())
    .addAllCollection(collection.map{it.toProto()})
    .addAllTypeTested(typeTested.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent.toProto():
      SpecimenDefinition.TypeTested {
    val protoValue = SpecimenDefinition.TypeTested.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIsDerived(isDerivedElement.toProto())
    .setType(type.toProto())
    .setPreference(SpecimenDefinition.TypeTested.PreferenceCode.newBuilder().setValue(SpecimenContainedPreferenceCode.Value.valueOf(preference.toCode().replace("-",
        "_").toUpperCase())).build())
    .setContainer(container.toProto())
    .setRequirement(requirementElement.toProto())
    .setRetentionTime(retentionTime.toProto())
    .addAllRejectionCriterion(rejectionCriterion.map{it.toProto()})
    .addAllHandling(handling.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerComponent.toProto():
      SpecimenDefinition.TypeTested.Container {
    val protoValue = SpecimenDefinition.TypeTested.Container.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMaterial(material.toProto())
    .setType(type.toProto())
    .setCap(cap.toProto())
    .setDescription(descriptionElement.toProto())
    .setCapacity(( capacity as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .setMinimumVolume(minimumVolume.specimenDefinitionTypeTestedContainerMinimumVolumeToProto())
    .addAllAdditive(additive.map{it.toProto()})
    .setPreparation(preparationElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerAdditiveComponent.toProto():
      SpecimenDefinition.TypeTested.Container.Additive {
    val protoValue = SpecimenDefinition.TypeTested.Container.Additive.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAdditive(additive.specimenDefinitionTypeTestedContainerAdditiveAdditiveToProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedHandlingComponent.toProto():
      SpecimenDefinition.TypeTested.Handling {
    val protoValue = SpecimenDefinition.TypeTested.Handling.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setTemperatureQualifier(temperatureQualifier.toProto())
    .setTemperatureRange(temperatureRange.toProto())
    .setMaxDuration(maxDuration.toProto())
    .setInstruction(instructionElement.toProto())
    .build()
    return protoValue
  }

  private fun SpecimenDefinition.TypeTested.toHapi():
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent {
    val hapiValue = org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIsDerivedElement(isDerived.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setPreference(org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenContainedPreference.valueOf(preference.value.name.replace("_","")))
    hapiValue.setContainer(container.toHapi())
    hapiValue.setRequirementElement(requirement.toHapi())
    hapiValue.setRetentionTime(retentionTime.toHapi())
    hapiValue.setRejectionCriterion(rejectionCriterionList.map{it.toHapi()})
    hapiValue.setHandling(handlingList.map{it.toHapi()})
    return hapiValue
  }

  private fun SpecimenDefinition.TypeTested.Container.toHapi():
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMaterial(material.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setCap(cap.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setCapacity(capacity.toHapi())
    hapiValue.setMinimumVolume(minimumVolume.specimenDefinitionTypeTestedContainerMinimumVolumeToHapi())
    hapiValue.setAdditive(additiveList.map{it.toHapi()})
    hapiValue.setPreparationElement(preparation.toHapi())
    return hapiValue
  }

  private fun SpecimenDefinition.TypeTested.Container.Additive.toHapi():
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerAdditiveComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerAdditiveComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAdditive(additive.specimenDefinitionTypeTestedContainerAdditiveAdditiveToHapi())
    return hapiValue
  }

  private fun SpecimenDefinition.TypeTested.Handling.toHapi():
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedHandlingComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedHandlingComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setTemperatureQualifier(temperatureQualifier.toHapi())
    hapiValue.setTemperatureRange(temperatureRange.toHapi())
    hapiValue.setMaxDuration(maxDuration.toHapi())
    hapiValue.setInstructionElement(instruction.toHapi())
    return hapiValue
  }
}
