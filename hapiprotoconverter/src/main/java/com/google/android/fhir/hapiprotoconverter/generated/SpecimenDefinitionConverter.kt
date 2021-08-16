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

object SpecimenDefinitionConverter {
  @JvmStatic
  private fun SpecimenDefinition.TypeTested.Container.MinimumVolumeX.specimenDefinitionTypeTestedContainerMinimumVolumeToHapi():
    Type {
    if (this.quantity != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
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
      protoValue.quantity = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SpecimenDefinition.TypeTested.Container.Additive.AdditiveX.specimenDefinitionTypeTestedContainerAdditiveAdditiveToHapi():
    Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
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
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun SpecimenDefinition.toHapi(): org.hl7.fhir.r4.model.SpecimenDefinition {
    val hapiValue = org.hl7.fhir.r4.model.SpecimenDefinition()
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
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasTypeCollected()) {
      hapiValue.typeCollected = typeCollected.toHapi()
    }
    if (patientPreparationCount > 0) {
      hapiValue.patientPreparation = patientPreparationList.map { it.toHapi() }
    }
    if (hasTimeAspect()) {
      hapiValue.timeAspectElement = timeAspect.toHapi()
    }
    if (collectionCount > 0) {
      hapiValue.collection = collectionList.map { it.toHapi() }
    }
    if (typeTestedCount > 0) {
      hapiValue.typeTested = typeTestedList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.SpecimenDefinition.toProto(): SpecimenDefinition {
    val protoValue = SpecimenDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.identifier = identifier.toProto()
    }
    if (hasTypeCollected()) {
      protoValue.typeCollected = typeCollected.toProto()
    }
    if (hasPatientPreparation()) {
      protoValue.addAllPatientPreparation(patientPreparation.map { it.toProto() })
    }
    if (hasTimeAspect()) {
      protoValue.timeAspect = timeAspectElement.toProto()
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
      protoValue.isDerived = isDerivedElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    protoValue.preference =
      SpecimenDefinition.TypeTested.PreferenceCode.newBuilder()
        .setValue(
          SpecimenContainedPreferenceCode.Value.valueOf(
            preference.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasContainer()) {
      protoValue.container = container.toProto()
    }
    if (hasRequirement()) {
      protoValue.requirement = requirementElement.toProto()
    }
    if (hasRetentionTime()) {
      protoValue.retentionTime = retentionTime.toProto()
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
      protoValue.material = material.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasCap()) {
      protoValue.cap = cap.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasCapacity()) {
      protoValue.capacity = (capacity as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
    }
    if (hasMinimumVolume()) {
      protoValue.minimumVolume =
        minimumVolume.specimenDefinitionTypeTestedContainerMinimumVolumeToProto()
    }
    if (hasAdditive()) {
      protoValue.addAllAdditive(additive.map { it.toProto() })
    }
    if (hasPreparation()) {
      protoValue.preparation = preparationElement.toProto()
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
      protoValue.additive = additive.specimenDefinitionTypeTestedContainerAdditiveAdditiveToProto()
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
      protoValue.temperatureQualifier = temperatureQualifier.toProto()
    }
    if (hasTemperatureRange()) {
      protoValue.temperatureRange = temperatureRange.toProto()
    }
    if (hasMaxDuration()) {
      protoValue.maxDuration = maxDuration.toProto()
    }
    if (hasInstruction()) {
      protoValue.instruction = instructionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SpecimenDefinition.TypeTested.toHapi():
    org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent {
    val hapiValue = org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenDefinitionTypeTestedComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasIsDerived()) {
      hapiValue.isDerivedElement = isDerived.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    hapiValue.preference =
      org.hl7.fhir.r4.model.SpecimenDefinition.SpecimenContainedPreference.valueOf(
        preference.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasContainer()) {
      hapiValue.container = container.toHapi()
    }
    if (hasRequirement()) {
      hapiValue.requirementElement = requirement.toHapi()
    }
    if (hasRetentionTime()) {
      hapiValue.retentionTime = retentionTime.toHapi()
    }
    if (rejectionCriterionCount > 0) {
      hapiValue.rejectionCriterion = rejectionCriterionList.map { it.toHapi() }
    }
    if (handlingCount > 0) {
      hapiValue.handling = handlingList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMaterial()) {
      hapiValue.material = material.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasCap()) {
      hapiValue.cap = cap.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasCapacity()) {
      hapiValue.capacity = capacity.toHapi()
    }
    if (hasMinimumVolume()) {
      hapiValue.minimumVolume =
        minimumVolume.specimenDefinitionTypeTestedContainerMinimumVolumeToHapi()
    }
    if (additiveCount > 0) {
      hapiValue.additive = additiveList.map { it.toHapi() }
    }
    if (hasPreparation()) {
      hapiValue.preparationElement = preparation.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAdditive()) {
      hapiValue.additive = additive.specimenDefinitionTypeTestedContainerAdditiveAdditiveToHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTemperatureQualifier()) {
      hapiValue.temperatureQualifier = temperatureQualifier.toHapi()
    }
    if (hasTemperatureRange()) {
      hapiValue.temperatureRange = temperatureRange.toHapi()
    }
    if (hasMaxDuration()) {
      hapiValue.maxDuration = maxDuration.toHapi()
    }
    if (hasInstruction()) {
      hapiValue.instructionElement = instruction.toHapi()
    }
    return hapiValue
  }
}
