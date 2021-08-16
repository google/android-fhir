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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object SpecimenDefinitionConverter {
  @JvmStatic
  private fun SpecimenDefinition.TypeTested.Container.MinimumVolumeX.specimenDefinitionTypeTestedContainerMinimumVolumeToHapi():
    Type {
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for SpecimenDefinition.typeTested.container.minimumVolume[x]"
    )
  }

  @JvmStatic
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

  @JvmStatic
  private fun SpecimenDefinition.TypeTested.Container.Additive.AdditiveX.specimenDefinitionTypeTestedContainerAdditiveAdditiveToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for SpecimenDefinition.typeTested.container.additive.additive[x]"
    )
  }

  @JvmStatic
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

  @JvmStatic
  public fun SpecimenDefinition.toHapi(): org.hl7.fhir.r4.model.SpecimenDefinition {
    val hapiValue = org.hl7.fhir.r4.model.SpecimenDefinition()
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
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    if (hasTypeCollected()) {
      hapiValue.setTypeCollected(typeCollected.toHapi())
    }
    if (patientPreparationCount > 0) {
      hapiValue.setPatientPreparation(patientPreparationList.map { it.toHapi() })
    }
    if (hasTimeAspect()) {
      hapiValue.setTimeAspectElement(timeAspect.toHapi())
    }
    if (collectionCount > 0) {
      hapiValue.setCollection(collectionList.map { it.toHapi() })
    }
    if (typeTestedCount > 0) {
      hapiValue.setTypeTested(typeTestedList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SpecimenDefinition.toProto(): SpecimenDefinition {
    val protoValue = SpecimenDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasTypeCollected()) {
      protoValue.setTypeCollected(typeCollected.toProto())
    }
    if (hasPatientPreparation()) {
      protoValue.addAllPatientPreparation(patientPreparation.map { it.toProto() })
    }
    if (hasTimeAspect()) {
      protoValue.setTimeAspect(timeAspectElement.toProto())
    }
    if (hasCollection()) {
      protoValue.addAllCollection(collection.map { it.toProto() })
    }
    if (hasTypeTested()) {
      protoValue.addAllTypeTested(typeTested.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent.toProto():
    SpecimenDefinition.TypeTested {
    val protoValue =
      SpecimenDefinition.TypeTested.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIsDerived()) {
      protoValue.setIsDerived(isDerivedElement.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    protoValue.setPreference(
      SpecimenDefinition.TypeTested.PreferenceCode.newBuilder()
        .setValue(
          SpecimenContainedPreferenceCode.Value.valueOf(
            preference.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasContainer()) {
      protoValue.setContainer(container.toProto())
    }
    if (hasRequirement()) {
      protoValue.setRequirement(requirementElement.toProto())
    }
    if (hasRetentionTime()) {
      protoValue.setRetentionTime(retentionTime.toProto())
    }
    if (hasRejectionCriterion()) {
      protoValue.addAllRejectionCriterion(rejectionCriterion.map { it.toProto() })
    }
    if (hasHandling()) {
      protoValue.addAllHandling(handling.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerComponent.toProto():
    SpecimenDefinition.TypeTested.Container {
    val protoValue =
      SpecimenDefinition.TypeTested.Container.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMaterial()) {
      protoValue.setMaterial(material.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasCap()) {
      protoValue.setCap(cap.toProto())
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasCapacity()) {
      protoValue.setCapacity((capacity as org.hl7.fhir.r4.model.SimpleQuantity).toProto())
    }
    if (hasMinimumVolume()) {
      protoValue.setMinimumVolume(
        minimumVolume.specimenDefinitionTypeTestedContainerMinimumVolumeToProto()
      )
    }
    if (hasAdditive()) {
      protoValue.addAllAdditive(additive.map { it.toProto() })
    }
    if (hasPreparation()) {
      protoValue.setPreparation(preparationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerAdditiveComponent.toProto():
    SpecimenDefinition.TypeTested.Container.Additive {
    val protoValue =
      SpecimenDefinition.TypeTested.Container.Additive.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAdditive()) {
      protoValue.setAdditive(
        additive.specimenDefinitionTypeTestedContainerAdditiveAdditiveToProto()
      )
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedHandlingComponent.toProto():
    SpecimenDefinition.TypeTested.Handling {
    val protoValue =
      SpecimenDefinition.TypeTested.Handling.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTemperatureQualifier()) {
      protoValue.setTemperatureQualifier(temperatureQualifier.toProto())
    }
    if (hasTemperatureRange()) {
      protoValue.setTemperatureRange(temperatureRange.toProto())
    }
    if (hasMaxDuration()) {
      protoValue.setMaxDuration(maxDuration.toProto())
    }
    if (hasInstruction()) {
      protoValue.setInstruction(instructionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SpecimenDefinition.TypeTested.toHapi():
    org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent {
    val hapiValue = org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasIsDerived()) {
      hapiValue.setIsDerivedElement(isDerived.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    hapiValue.setPreference(
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenContainedPreference.valueOf(
        preference.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasContainer()) {
      hapiValue.setContainer(container.toHapi())
    }
    if (hasRequirement()) {
      hapiValue.setRequirementElement(requirement.toHapi())
    }
    if (hasRetentionTime()) {
      hapiValue.setRetentionTime(retentionTime.toHapi())
    }
    if (rejectionCriterionCount > 0) {
      hapiValue.setRejectionCriterion(rejectionCriterionList.map { it.toHapi() })
    }
    if (handlingCount > 0) {
      hapiValue.setHandling(handlingList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun SpecimenDefinition.TypeTested.Container.toHapi():
    org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasMaterial()) {
      hapiValue.setMaterial(material.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasCap()) {
      hapiValue.setCap(cap.toHapi())
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasCapacity()) {
      hapiValue.setCapacity(capacity.toHapi())
    }
    if (hasMinimumVolume()) {
      hapiValue.setMinimumVolume(
        minimumVolume.specimenDefinitionTypeTestedContainerMinimumVolumeToHapi()
      )
    }
    if (additiveCount > 0) {
      hapiValue.setAdditive(additiveList.map { it.toHapi() })
    }
    if (hasPreparation()) {
      hapiValue.setPreparationElement(preparation.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SpecimenDefinition.TypeTested.Container.Additive.toHapi():
    org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedContainerAdditiveComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SpecimenDefinition
        .SpecimenDefinitionTypeTestedContainerAdditiveComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasAdditive()) {
      hapiValue.setAdditive(additive.specimenDefinitionTypeTestedContainerAdditiveAdditiveToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun SpecimenDefinition.TypeTested.Handling.toHapi():
    org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedHandlingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedHandlingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasTemperatureQualifier()) {
      hapiValue.setTemperatureQualifier(temperatureQualifier.toHapi())
    }
    if (hasTemperatureRange()) {
      hapiValue.setTemperatureRange(temperatureRange.toHapi())
    }
    if (hasMaxDuration()) {
      hapiValue.setMaxDuration(maxDuration.toHapi())
    }
    if (hasInstruction()) {
      hapiValue.setInstructionElement(instruction.toHapi())
    }
    return hapiValue
  }
}
