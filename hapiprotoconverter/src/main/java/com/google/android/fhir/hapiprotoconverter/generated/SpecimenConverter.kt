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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
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
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.Specimen
import com.google.fhir.r4.core.Specimen.Collection
import com.google.fhir.r4.core.Specimen.Container
import com.google.fhir.r4.core.Specimen.Processing
import com.google.fhir.r4.core.SpecimenStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

object SpecimenConverter {
  @JvmStatic
  private fun Specimen.Collection.CollectedX.specimenCollectionCollectedToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.collection.collected[x]")
  }

  @JvmStatic
  private fun Type.specimenCollectionCollectedToProto(): Specimen.Collection.CollectedX {
    val protoValue = Specimen.Collection.CollectedX.newBuilder()
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Specimen.Collection.FastingStatusX.specimenCollectionFastingStatusToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.collection.fastingStatus[x]")
  }

  @JvmStatic
  private fun Type.specimenCollectionFastingStatusToProto(): Specimen.Collection.FastingStatusX {
    val protoValue = Specimen.Collection.FastingStatusX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
        protoValue.duration = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Specimen.Processing.TimeX.specimenProcessingTimeToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.processing.time[x]")
  }

  @JvmStatic
  private fun Type.specimenProcessingTimeToProto(): Specimen.Processing.TimeX {
    val protoValue = Specimen.Processing.TimeX.newBuilder()
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Specimen.Container.AdditiveX.specimenContainerAdditiveToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.container.additive[x]")
  }

  @JvmStatic
  private fun Type.specimenContainerAdditiveToProto(): Specimen.Container.AdditiveX {
    val protoValue = Specimen.Container.AdditiveX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
        protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Specimen.toHapi(): org.hl7.fhir.r4.model.Specimen {
    val hapiValue = org.hl7.fhir.r4.model.Specimen()
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
    if (identifierCount > 0) {
        hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasAccessionIdentifier()) {
        hapiValue.accessionIdentifier = accessionIdentifier.toHapi()
    }
      hapiValue.status = org.hl7.fhir.r4.model.Specimen.SpecimenStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasSubject()) {
        hapiValue.subject = subject.toHapi()
    }
    if (hasReceivedTime()) {
        hapiValue.receivedTimeElement = receivedTime.toHapi()
    }
    if (parentCount > 0) {
        hapiValue.parent = parentList.map { it.toHapi() }
    }
    if (requestCount > 0) {
        hapiValue.request = requestList.map { it.toHapi() }
    }
    if (hasCollection()) {
        hapiValue.collection = collection.toHapi()
    }
    if (processingCount > 0) {
        hapiValue.processing = processingList.map { it.toHapi() }
    }
    if (containerCount > 0) {
        hapiValue.container = containerList.map { it.toHapi() }
    }
    if (conditionCount > 0) {
        hapiValue.condition = conditionList.map { it.toHapi() }
    }
    if (noteCount > 0) {
        hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Specimen.toProto(): Specimen {
    val protoValue = Specimen.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasAccessionIdentifier()) {
        protoValue.accessionIdentifier = accessionIdentifier.toProto()
    }
      protoValue.status = Specimen.StatusCode.newBuilder()
          .setValue(
              SpecimenStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasSubject()) {
        protoValue.subject = subject.toProto()
    }
    if (hasReceivedTime()) {
        protoValue.receivedTime = receivedTimeElement.toProto()
    }
    if (hasParent()) {
      protoValue.addAllParent(parent.map { it.toProto() })
    }
    if (hasRequest()) {
      protoValue.addAllRequest(request.map { it.toProto() })
    }
    if (hasCollection()) {
        protoValue.collection = collection.toProto()
    }
    if (hasProcessing()) {
      protoValue.addAllProcessing(processing.map { it.toProto() })
    }
    if (hasContainer()) {
      protoValue.addAllContainer(container.map { it.toProto() })
    }
    if (hasCondition()) {
      protoValue.addAllCondition(condition.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent.toProto():
    Specimen.Collection {
    val protoValue = Specimen.Collection.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCollector()) {
        protoValue.collector = collector.toProto()
    }
    if (hasCollected()) {
        protoValue.collected = collected.specimenCollectionCollectedToProto()
    }
    if (hasDuration()) {
        protoValue.duration = duration.toProto()
    }
    if (hasQuantity()) {
        protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasMethod()) {
        protoValue.method = method.toProto()
    }
    if (hasBodySite()) {
        protoValue.bodySite = bodySite.toProto()
    }
    if (hasFastingStatus()) {
        protoValue.fastingStatus = fastingStatus.specimenCollectionFastingStatusToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent.toProto():
    Specimen.Processing {
    val protoValue = Specimen.Processing.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasProcedure()) {
        protoValue.procedure = procedure.toProto()
    }
    if (hasAdditive()) {
      protoValue.addAllAdditive(additive.map { it.toProto() })
    }
    if (hasTime()) {
        protoValue.time = time.specimenProcessingTimeToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent.toProto():
    Specimen.Container {
    val protoValue = Specimen.Container.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasCapacity()) {
        protoValue.capacity = (capacity as SimpleQuantity).toProto()
    }
    if (hasSpecimenQuantity()) {
        protoValue.specimenQuantity = (specimenQuantity as SimpleQuantity).toProto()
    }
    if (hasAdditive()) {
        protoValue.additive = additive.specimenContainerAdditiveToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Specimen.Collection.toHapi():
    org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCollector()) {
        hapiValue.collector = collector.toHapi()
    }
    if (hasCollected()) {
        hapiValue.collected = collected.specimenCollectionCollectedToHapi()
    }
    if (hasDuration()) {
        hapiValue.duration = duration.toHapi()
    }
    if (hasQuantity()) {
        hapiValue.quantity = quantity.toHapi()
    }
    if (hasMethod()) {
        hapiValue.method = method.toHapi()
    }
    if (hasBodySite()) {
        hapiValue.bodySite = bodySite.toHapi()
    }
    if (hasFastingStatus()) {
        hapiValue.fastingStatus = fastingStatus.specimenCollectionFastingStatusToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Specimen.Processing.toHapi():
    org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent {
    val hapiValue = org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (hasProcedure()) {
        hapiValue.procedure = procedure.toHapi()
    }
    if (additiveCount > 0) {
        hapiValue.additive = additiveList.map { it.toHapi() }
    }
    if (hasTime()) {
        hapiValue.time = time.specimenProcessingTimeToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Specimen.Container.toHapi():
    org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent {
    val hapiValue = org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
        hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasCapacity()) {
        hapiValue.capacity = capacity.toHapi()
    }
    if (hasSpecimenQuantity()) {
        hapiValue.specimenQuantity = specimenQuantity.toHapi()
    }
    if (hasAdditive()) {
        hapiValue.additive = additive.specimenContainerAdditiveToHapi()
    }
    return hapiValue
  }
}
