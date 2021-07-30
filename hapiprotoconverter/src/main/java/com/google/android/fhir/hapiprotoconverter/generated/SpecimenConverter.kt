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
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

public object SpecimenConverter {
  private fun Specimen.Collection.CollectedX.specimenCollectionCollectedToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.collection.collected[x]")
  }

  private fun Type.specimenCollectionCollectedToProto(): Specimen.Collection.CollectedX {
    val protoValue = Specimen.Collection.CollectedX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  private fun Specimen.Collection.FastingStatusX.specimenCollectionFastingStatusToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType ) {
      return (this.getDuration()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.collection.fastingStatus[x]")
  }

  private fun Type.specimenCollectionFastingStatusToProto(): Specimen.Collection.FastingStatusX {
    val protoValue = Specimen.Collection.FastingStatusX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    return protoValue.build()
  }

  private fun Specimen.Processing.TimeX.specimenProcessingTimeToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.processing.time[x]")
  }

  private fun Type.specimenProcessingTimeToProto(): Specimen.Processing.TimeX {
    val protoValue = Specimen.Processing.TimeX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  private fun Specimen.Container.AdditiveX.specimenContainerAdditiveToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Specimen.container.additive[x]")
  }

  private fun Type.specimenContainerAdditiveToProto(): Specimen.Container.AdditiveX {
    val protoValue = Specimen.Container.AdditiveX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun Specimen.toHapi(): org.hl7.fhir.r4.model.Specimen {
    val hapiValue = org.hl7.fhir.r4.model.Specimen()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setAccessionIdentifier(accessionIdentifier.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.Specimen.SpecimenStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setType(type.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setReceivedTimeElement(receivedTime.toHapi())
    hapiValue.setParent(parentList.map{it.toHapi()})
    hapiValue.setRequest(requestList.map{it.toHapi()})
    hapiValue.setCollection(collection.toHapi())
    hapiValue.setProcessing(processingList.map{it.toHapi()})
    hapiValue.setContainer(containerList.map{it.toHapi()})
    hapiValue.setCondition(conditionList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Specimen.toProto(): Specimen {
    val protoValue = Specimen.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setAccessionIdentifier(accessionIdentifier.toProto())
    .setStatus(Specimen.StatusCode.newBuilder().setValue(SpecimenStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setType(type.toProto())
    .setSubject(subject.toProto())
    .setReceivedTime(receivedTimeElement.toProto())
    .addAllParent(parent.map{it.toProto()})
    .addAllRequest(request.map{it.toProto()})
    .setCollection(collection.toProto())
    .addAllProcessing(processing.map{it.toProto()})
    .addAllContainer(container.map{it.toProto()})
    .addAllCondition(condition.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent.toProto():
      Specimen.Collection {
    val protoValue = Specimen.Collection.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCollector(collector.toProto())
    .setCollected(collected.specimenCollectionCollectedToProto())
    .setDuration(duration.toProto())
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .setMethod(method.toProto())
    .setBodySite(bodySite.toProto())
    .setFastingStatus(fastingStatus.specimenCollectionFastingStatusToProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent.toProto():
      Specimen.Processing {
    val protoValue = Specimen.Processing.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .setProcedure(procedure.toProto())
    .addAllAdditive(additive.map{it.toProto()})
    .setTime(time.specimenProcessingTimeToProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent.toProto():
      Specimen.Container {
    val protoValue = Specimen.Container.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .setType(type.toProto())
    .setCapacity(( capacity as SimpleQuantity ).toProto())
    .setSpecimenQuantity(( specimenQuantity as SimpleQuantity ).toProto())
    .setAdditive(additive.specimenContainerAdditiveToProto())
    .build()
    return protoValue
  }

  private fun Specimen.Collection.toHapi():
      org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Specimen.SpecimenCollectionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCollector(collector.toHapi())
    hapiValue.setCollected(collected.specimenCollectionCollectedToHapi())
    hapiValue.setDuration(duration.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setMethod(method.toHapi())
    hapiValue.setBodySite(bodySite.toHapi())
    hapiValue.setFastingStatus(fastingStatus.specimenCollectionFastingStatusToHapi())
    return hapiValue
  }

  private fun Specimen.Processing.toHapi():
      org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent {
    val hapiValue = org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setProcedure(procedure.toHapi())
    hapiValue.setAdditive(additiveList.map{it.toHapi()})
    hapiValue.setTime(time.specimenProcessingTimeToHapi())
    return hapiValue
  }

  private fun Specimen.Container.toHapi():
      org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent {
    val hapiValue = org.hl7.fhir.r4.model.Specimen.SpecimenContainerComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setCapacity(capacity.toHapi())
    hapiValue.setSpecimenQuantity(specimenQuantity.toHapi())
    hapiValue.setAdditive(additive.specimenContainerAdditiveToHapi())
    return hapiValue
  }
}
