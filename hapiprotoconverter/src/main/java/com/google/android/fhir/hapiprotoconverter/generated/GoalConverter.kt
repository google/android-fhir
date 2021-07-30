package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.Goal
import com.google.fhir.r4.core.Goal.Target
import com.google.fhir.r4.core.GoalLifecycleStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object GoalConverter {
  private fun Goal.StartX.goalStartToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Goal.start[x]")
  }

  private fun Type.goalStartToProto(): Goal.StartX {
    val protoValue = Goal.StartX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  private fun Goal.Target.DetailX.goalTargetDetailToHapi(): Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getInteger() != Integer.newBuilder().defaultInstanceForType ) {
      return (this.getInteger()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType ) {
      return (this.getRatio()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Goal.target.detail[x]")
  }

  private fun Type.goalTargetDetailToProto(): Goal.Target.DetailX {
    val protoValue = Goal.Target.DetailX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is IntegerType) {
      protoValue.setInteger(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    return protoValue.build()
  }

  private fun Goal.Target.DueX.goalTargetDueToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType ) {
      return (this.getDate()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType ) {
      return (this.getDuration()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Goal.target.due[x]")
  }

  private fun Type.goalTargetDueToProto(): Goal.Target.DueX {
    val protoValue = Goal.Target.DueX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    return protoValue.build()
  }

  public fun Goal.toHapi(): org.hl7.fhir.r4.model.Goal {
    val hapiValue = org.hl7.fhir.r4.model.Goal()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setLifecycleStatus(org.hl7.fhir.r4.model.Goal.GoalLifecycleStatus.valueOf(lifecycleStatus.value.name.replace("_","")))
    hapiValue.setAchievementStatus(achievementStatus.toHapi())
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setPriority(priority.toHapi())
    hapiValue.setDescription(description.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setStart(start.goalStartToHapi())
    hapiValue.setTarget(targetList.map{it.toHapi()})
    hapiValue.setStatusDateElement(statusDate.toHapi())
    hapiValue.setStatusReasonElement(statusReason.toHapi())
    hapiValue.setExpressedBy(expressedBy.toHapi())
    hapiValue.setAddresses(addressesList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setOutcomeCode(outcomeCodeList.map{it.toHapi()})
    hapiValue.setOutcomeReference(outcomeReferenceList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Goal.toProto(): Goal {
    val protoValue = Goal.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setLifecycleStatus(Goal.LifecycleStatusCode.newBuilder().setValue(GoalLifecycleStatusCode.Value.valueOf(lifecycleStatus.toCode().replace("-",
        "_").toUpperCase())).build())
    .setAchievementStatus(achievementStatus.toProto())
    .addAllCategory(category.map{it.toProto()})
    .setPriority(priority.toProto())
    .setDescription(description.toProto())
    .setSubject(subject.toProto())
    .setStart(start.goalStartToProto())
    .addAllTarget(target.map{it.toProto()})
    .setStatusDate(statusDateElement.toProto())
    .setStatusReason(statusReasonElement.toProto())
    .setExpressedBy(expressedBy.toProto())
    .addAllAddresses(addresses.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .addAllOutcomeCode(outcomeCode.map{it.toProto()})
    .addAllOutcomeReference(outcomeReference.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Goal.GoalTargetComponent.toProto(): Goal.Target {
    val protoValue = Goal.Target.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setMeasure(measure.toProto())
    .setDetail(detail.goalTargetDetailToProto())
    .setDue(due.goalTargetDueToProto())
    .build()
    return protoValue
  }

  private fun Goal.Target.toHapi(): org.hl7.fhir.r4.model.Goal.GoalTargetComponent {
    val hapiValue = org.hl7.fhir.r4.model.Goal.GoalTargetComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setMeasure(measure.toHapi())
    hapiValue.setDetail(detail.goalTargetDetailToHapi())
    hapiValue.setDue(due.goalTargetDueToHapi())
    return hapiValue
  }
}
