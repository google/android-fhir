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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object GoalConverter {
  @JvmStatic
  private fun Goal.StartX.goalStartToHapi(): Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Goal.start[x]")
  }

  @JvmStatic
  private fun Type.goalStartToProto(): Goal.StartX {
    val protoValue = Goal.StartX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Goal.Target.DetailX.goalTargetDetailToHapi(): Type {
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Goal.target.detail[x]")
  }

  @JvmStatic
  private fun Type.goalTargetDetailToProto(): Goal.Target.DetailX {
    val protoValue = Goal.Target.DetailX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is IntegerType) {
      protoValue.integer = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.ratio = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Goal.Target.DueX.goalTargetDueToHapi(): Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Goal.target.due[x]")
  }

  @JvmStatic
  private fun Type.goalTargetDueToProto(): Goal.Target.DueX {
    val protoValue = Goal.Target.DueX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Goal.toHapi(): org.hl7.fhir.r4.model.Goal {
    val hapiValue = org.hl7.fhir.r4.model.Goal()
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
    hapiValue.lifecycleStatus =
      org.hl7.fhir.r4.model.Goal.GoalLifecycleStatus.valueOf(
        lifecycleStatus.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasAchievementStatus()) {
      hapiValue.achievementStatus = achievementStatus.toHapi()
    }
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasPriority()) {
      hapiValue.priority = priority.toHapi()
    }
    if (hasDescription()) {
      hapiValue.description = description.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasStart()) {
      hapiValue.start = start.goalStartToHapi()
    }
    if (targetCount > 0) {
      hapiValue.target = targetList.map { it.toHapi() }
    }
    if (hasStatusDate()) {
      hapiValue.statusDateElement = statusDate.toHapi()
    }
    if (hasStatusReason()) {
      hapiValue.statusReasonElement = statusReason.toHapi()
    }
    if (hasExpressedBy()) {
      hapiValue.expressedBy = expressedBy.toHapi()
    }
    if (addressesCount > 0) {
      hapiValue.addresses = addressesList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (outcomeCodeCount > 0) {
      hapiValue.outcomeCode = outcomeCodeList.map { it.toHapi() }
    }
    if (outcomeReferenceCount > 0) {
      hapiValue.outcomeReference = outcomeReferenceList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Goal.toProto(): Goal {
    val protoValue = Goal.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.lifecycleStatus =
      Goal.LifecycleStatusCode.newBuilder()
        .setValue(
          GoalLifecycleStatusCode.Value.valueOf(
            lifecycleStatus.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasAchievementStatus()) {
      protoValue.achievementStatus = achievementStatus.toProto()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasPriority()) {
      protoValue.priority = priority.toProto()
    }
    if (hasDescription()) {
      protoValue.description = description.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasStart()) {
      protoValue.start = start.goalStartToProto()
    }
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    if (hasStatusDate()) {
      protoValue.statusDate = statusDateElement.toProto()
    }
    if (hasStatusReason()) {
      protoValue.statusReason = statusReasonElement.toProto()
    }
    if (hasExpressedBy()) {
      protoValue.expressedBy = expressedBy.toProto()
    }
    if (hasAddresses()) {
      protoValue.addAllAddresses(addresses.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasOutcomeCode()) {
      protoValue.addAllOutcomeCode(outcomeCode.map { it.toProto() })
    }
    if (hasOutcomeReference()) {
      protoValue.addAllOutcomeReference(outcomeReference.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Goal.GoalTargetComponent.toProto(): Goal.Target {
    val protoValue = Goal.Target.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMeasure()) {
      protoValue.measure = measure.toProto()
    }
    if (hasDetail()) {
      protoValue.detail = detail.goalTargetDetailToProto()
    }
    if (hasDue()) {
      protoValue.due = due.goalTargetDueToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Goal.Target.toHapi(): org.hl7.fhir.r4.model.Goal.GoalTargetComponent {
    val hapiValue = org.hl7.fhir.r4.model.Goal.GoalTargetComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMeasure()) {
      hapiValue.measure = measure.toHapi()
    }
    if (hasDetail()) {
      hapiValue.detail = detail.goalTargetDetailToHapi()
    }
    if (hasDue()) {
      hapiValue.due = due.goalTargetDueToHapi()
    }
    return hapiValue
  }
}
