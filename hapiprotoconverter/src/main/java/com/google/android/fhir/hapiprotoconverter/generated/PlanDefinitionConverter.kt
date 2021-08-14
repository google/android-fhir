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

import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DataRequirementConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExpressionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExpressionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RelatedArtifactConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RelatedArtifactConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TriggerDefinitionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TriggerDefinitionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.ActionCardinalityBehaviorCode
import com.google.fhir.r4.core.ActionConditionKindCode
import com.google.fhir.r4.core.ActionGroupingBehaviorCode
import com.google.fhir.r4.core.ActionParticipantTypeCode
import com.google.fhir.r4.core.ActionPrecheckBehaviorCode
import com.google.fhir.r4.core.ActionRelationshipTypeCode
import com.google.fhir.r4.core.ActionRequiredBehaviorCode
import com.google.fhir.r4.core.ActionSelectionBehaviorCode
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.PlanDefinition
import com.google.fhir.r4.core.PlanDefinition.Action
import com.google.fhir.r4.core.PlanDefinition.Action.Condition
import com.google.fhir.r4.core.PlanDefinition.Action.Participant
import com.google.fhir.r4.core.PlanDefinition.Action.RelatedAction
import com.google.fhir.r4.core.PlanDefinition.Goal
import com.google.fhir.r4.core.PlanDefinition.Goal.Target
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import com.google.fhir.r4.core.Uri
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

public object PlanDefinitionConverter {
  @JvmStatic
  private fun PlanDefinition.SubjectX.planDefinitionSubjectToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.subject[x]")
  }

  @JvmStatic
  private fun Type.planDefinitionSubjectToProto(): PlanDefinition.SubjectX {
    val protoValue = PlanDefinition.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PlanDefinition.Goal.Target.DetailX.planDefinitionGoalTargetDetailToHapi(): Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.goal.target.detail[x]")
  }

  @JvmStatic
  private fun Type.planDefinitionGoalTargetDetailToProto(): PlanDefinition.Goal.Target.DetailX {
    val protoValue = PlanDefinition.Goal.Target.DetailX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PlanDefinition.Action.SubjectX.planDefinitionActionSubjectToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.subject[x]")
  }

  @JvmStatic
  private fun Type.planDefinitionActionSubjectToProto(): PlanDefinition.Action.SubjectX {
    val protoValue = PlanDefinition.Action.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PlanDefinition.Action.RelatedAction.OffsetX.planDefinitionActionRelatedActionOffsetToHapi():
    Type {
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.relatedAction.offset[x]")
  }

  @JvmStatic
  private fun Type.planDefinitionActionRelatedActionOffsetToProto():
    PlanDefinition.Action.RelatedAction.OffsetX {
    val protoValue = PlanDefinition.Action.RelatedAction.OffsetX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PlanDefinition.Action.TimingX.planDefinitionActionTimingToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.timing[x]")
  }

  @JvmStatic
  private fun Type.planDefinitionActionTimingToProto(): PlanDefinition.Action.TimingX {
    val protoValue = PlanDefinition.Action.TimingX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PlanDefinition.Action.DefinitionX.planDefinitionActionDefinitionToHapi(): Type {
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.definition[x]")
  }

  @JvmStatic
  private fun Type.planDefinitionActionDefinitionToProto(): PlanDefinition.Action.DefinitionX {
    val protoValue = PlanDefinition.Action.DefinitionX.newBuilder()
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun PlanDefinition.toHapi(): org.hl7.fhir.r4.model.PlanDefinition {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setSubtitleElement(subtitle.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(
        status
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setSubject(subject.planDefinitionSubjectToHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setUsageElement(usage.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setApprovalDateElement(approvalDate.toHapi())
    hapiValue.setLastReviewDateElement(lastReviewDate.toHapi())
    hapiValue.setEffectivePeriod(effectivePeriod.toHapi())
    hapiValue.setTopic(topicList.map { it.toHapi() })
    hapiValue.setAuthor(authorList.map { it.toHapi() })
    hapiValue.setEditor(editorList.map { it.toHapi() })
    hapiValue.setReviewer(reviewerList.map { it.toHapi() })
    hapiValue.setEndorser(endorserList.map { it.toHapi() })
    hapiValue.setRelatedArtifact(relatedArtifactList.map { it.toHapi() })
    hapiValue.setLibrary(libraryList.map { it.toHapi() })
    hapiValue.setGoal(goalList.map { it.toHapi() })
    hapiValue.setAction(actionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.PlanDefinition.toProto(): PlanDefinition {
    val protoValue =
      PlanDefinition.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .addAllIdentifier(identifier.map { it.toProto() })
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setSubtitle(subtitleElement.toProto())
        .setType(type.toProto())
        .setStatus(
          PlanDefinition.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(
                status
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setSubject(subject.planDefinitionSubjectToProto())
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllUseContext(useContext.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setPurpose(purposeElement.toProto())
        .setUsage(usageElement.toProto())
        .setCopyright(copyrightElement.toProto())
        .setApprovalDate(approvalDateElement.toProto())
        .setLastReviewDate(lastReviewDateElement.toProto())
        .setEffectivePeriod(effectivePeriod.toProto())
        .addAllTopic(topic.map { it.toProto() })
        .addAllAuthor(author.map { it.toProto() })
        .addAllEditor(editor.map { it.toProto() })
        .addAllReviewer(reviewer.map { it.toProto() })
        .addAllEndorser(endorser.map { it.toProto() })
        .addAllRelatedArtifact(relatedArtifact.map { it.toProto() })
        .addAllLibrary(library.map { it.toProto() })
        .addAllGoal(goal.map { it.toProto() })
        .addAllAction(action.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalComponent.toProto():
    PlanDefinition.Goal {
    val protoValue =
      PlanDefinition.Goal.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCategory(category.toProto())
        .setDescription(description.toProto())
        .setPriority(priority.toProto())
        .setStart(start.toProto())
        .addAllAddresses(addresses.map { it.toProto() })
        .addAllDocumentation(documentation.map { it.toProto() })
        .addAllTarget(target.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalTargetComponent.toProto():
    PlanDefinition.Goal.Target {
    val protoValue =
      PlanDefinition.Goal.Target.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setMeasure(measure.toProto())
        .setDetail(detail.planDefinitionGoalTargetDetailToProto())
        .setDue(due.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent.toProto():
    PlanDefinition.Action {
    val protoValue =
      PlanDefinition.Action.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPrefix(prefixElement.toProto())
        .setTitle(titleElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setTextEquivalent(textEquivalentElement.toProto())
        .setPriority(
          PlanDefinition.Action.PriorityCode.newBuilder()
            .setValue(
              RequestPriorityCode.Value.valueOf(
                priority
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .addAllCode(code.map { it.toProto() })
        .addAllReason(reason.map { it.toProto() })
        .addAllDocumentation(documentation.map { it.toProto() })
        .addAllGoalId(goalId.map { it.toProto() })
        .setSubject(subject.planDefinitionActionSubjectToProto())
        .addAllTrigger(trigger.map { it.toProto() })
        .addAllCondition(condition.map { it.toProto() })
        .addAllInput(input.map { it.toProto() })
        .addAllOutput(output.map { it.toProto() })
        .addAllRelatedAction(relatedAction.map { it.toProto() })
        .setTiming(timing.planDefinitionActionTimingToProto())
        .addAllParticipant(participant.map { it.toProto() })
        .setType(type.toProto())
        .setGroupingBehavior(
          PlanDefinition.Action.GroupingBehaviorCode.newBuilder()
            .setValue(
              ActionGroupingBehaviorCode.Value.valueOf(
                groupingBehavior
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setSelectionBehavior(
          PlanDefinition.Action.SelectionBehaviorCode.newBuilder()
            .setValue(
              ActionSelectionBehaviorCode.Value.valueOf(
                selectionBehavior
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setRequiredBehavior(
          PlanDefinition.Action.RequiredBehaviorCode.newBuilder()
            .setValue(
              ActionRequiredBehaviorCode.Value.valueOf(
                requiredBehavior
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setPrecheckBehavior(
          PlanDefinition.Action.PrecheckBehaviorCode.newBuilder()
            .setValue(
              ActionPrecheckBehaviorCode.Value.valueOf(
                precheckBehavior
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setCardinalityBehavior(
          PlanDefinition.Action.CardinalityBehaviorCode.newBuilder()
            .setValue(
              ActionCardinalityBehaviorCode.Value.valueOf(
                cardinalityBehavior
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setDefinition(definition.planDefinitionActionDefinitionToProto())
        .setTransform(transformElement.toProto())
        .addAllDynamicValue(dynamicValue.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent.toProto():
    PlanDefinition.Action.Condition {
    val protoValue =
      PlanDefinition.Action.Condition.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setKind(
          PlanDefinition.Action.Condition.KindCode.newBuilder()
            .setValue(
              ActionConditionKindCode.Value.valueOf(
                kind
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setExpression(expression.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionRelatedActionComponent.toProto():
    PlanDefinition.Action.RelatedAction {
    val protoValue =
      PlanDefinition.Action.RelatedAction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setActionId(actionIdElement.toProto())
        .setRelationship(
          PlanDefinition.Action.RelatedAction.RelationshipCode.newBuilder()
            .setValue(
              ActionRelationshipTypeCode.Value.valueOf(
                relationship
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setOffset(offset.planDefinitionActionRelatedActionOffsetToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionParticipantComponent.toProto():
    PlanDefinition.Action.Participant {
    val protoValue =
      PlanDefinition.Action.Participant.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(
          PlanDefinition.Action.Participant.TypeCode.newBuilder()
            .setValue(
              ActionParticipantTypeCode.Value.valueOf(
                type
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setRole(role.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionDynamicValueComponent.toProto():
    PlanDefinition.Action.DynamicValue {
    val protoValue =
      PlanDefinition.Action.DynamicValue.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPath(pathElement.toProto())
        .setExpression(expression.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun PlanDefinition.Goal.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCategory(category.toHapi())
    hapiValue.setDescription(description.toHapi())
    hapiValue.setPriority(priority.toHapi())
    hapiValue.setStart(start.toHapi())
    hapiValue.setAddresses(addressesList.map { it.toHapi() })
    hapiValue.setDocumentation(documentationList.map { it.toHapi() })
    hapiValue.setTarget(targetList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun PlanDefinition.Goal.Target.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalTargetComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalTargetComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setMeasure(measure.toHapi())
    hapiValue.setDetail(detail.planDefinitionGoalTargetDetailToHapi())
    hapiValue.setDue(due.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun PlanDefinition.Action.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPrefixElement(prefix.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setTextEquivalentElement(textEquivalent.toHapi())
    hapiValue.setPriority(
      org.hl7.fhir.r4.model.PlanDefinition.RequestPriority.valueOf(
        priority
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setCode(codeList.map { it.toHapi() })
    hapiValue.setReason(reasonList.map { it.toHapi() })
    hapiValue.setDocumentation(documentationList.map { it.toHapi() })
    hapiValue.setGoalId(goalIdList.map { it.toHapi() })
    hapiValue.setSubject(subject.planDefinitionActionSubjectToHapi())
    hapiValue.setTrigger(triggerList.map { it.toHapi() })
    hapiValue.setCondition(conditionList.map { it.toHapi() })
    hapiValue.setInput(inputList.map { it.toHapi() })
    hapiValue.setOutput(outputList.map { it.toHapi() })
    hapiValue.setRelatedAction(relatedActionList.map { it.toHapi() })
    hapiValue.setTiming(timing.planDefinitionActionTimingToHapi())
    hapiValue.setParticipant(participantList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setGroupingBehavior(
      org.hl7.fhir.r4.model.PlanDefinition.ActionGroupingBehavior.valueOf(
        groupingBehavior
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setSelectionBehavior(
      org.hl7.fhir.r4.model.PlanDefinition.ActionSelectionBehavior.valueOf(
        selectionBehavior
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setRequiredBehavior(
      org.hl7.fhir.r4.model.PlanDefinition.ActionRequiredBehavior.valueOf(
        requiredBehavior
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setPrecheckBehavior(
      org.hl7.fhir.r4.model.PlanDefinition.ActionPrecheckBehavior.valueOf(
        precheckBehavior
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setCardinalityBehavior(
      org.hl7.fhir.r4.model.PlanDefinition.ActionCardinalityBehavior.valueOf(
        cardinalityBehavior
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setDefinition(definition.planDefinitionActionDefinitionToHapi())
    hapiValue.setTransformElement(transform.toHapi())
    hapiValue.setDynamicValue(dynamicValueList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun PlanDefinition.Action.Condition.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setKind(
      org.hl7.fhir.r4.model.PlanDefinition.ActionConditionKind.valueOf(
        kind
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setExpression(expression.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun PlanDefinition.Action.RelatedAction.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionRelatedActionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionRelatedActionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setActionIdElement(actionId.toHapi())
    hapiValue.setRelationship(
      org.hl7.fhir.r4.model.PlanDefinition.ActionRelationshipType.valueOf(
        relationship
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setOffset(offset.planDefinitionActionRelatedActionOffsetToHapi())
    return hapiValue
  }

  @JvmStatic
  private fun PlanDefinition.Action.Participant.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionParticipantComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(
      org.hl7.fhir.r4.model.PlanDefinition.ActionParticipantType.valueOf(
        type
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setRole(role.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun PlanDefinition.Action.DynamicValue.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionDynamicValueComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionDynamicValueComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPathElement(path.toHapi())
    hapiValue.setExpression(expression.toHapi())
    return hapiValue
  }
}
