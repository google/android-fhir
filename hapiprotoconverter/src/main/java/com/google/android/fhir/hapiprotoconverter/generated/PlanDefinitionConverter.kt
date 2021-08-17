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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PlanDefinition
import com.google.fhir.r4.core.PlanDefinition.Action
import com.google.fhir.r4.core.PlanDefinition.Action.Condition
import com.google.fhir.r4.core.PlanDefinition.Action.Participant
import com.google.fhir.r4.core.PlanDefinition.Action.RelatedAction
import com.google.fhir.r4.core.PlanDefinition.Goal
import com.google.fhir.r4.core.PlanDefinition.Goal.Target
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Age
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Duration
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

object PlanDefinitionConverter {
  private fun PlanDefinition.SubjectX.planDefinitionSubjectToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.subject[x]")
  }

  private fun Type.planDefinitionSubjectToProto(): PlanDefinition.SubjectX {
    val protoValue = PlanDefinition.SubjectX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun PlanDefinition.Goal.Target.DetailX.planDefinitionGoalTargetDetailToHapi(): Type {
    if (hasQuantity()) {
      return (this.quantity).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.goal.target.detail[x]")
  }

  private fun Type.planDefinitionGoalTargetDetailToProto(): PlanDefinition.Goal.Target.DetailX {
    val protoValue = PlanDefinition.Goal.Target.DetailX.newBuilder()
    if (this is Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  private fun PlanDefinition.Action.SubjectX.planDefinitionActionSubjectToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.subject[x]")
  }

  private fun Type.planDefinitionActionSubjectToProto(): PlanDefinition.Action.SubjectX {
    val protoValue = PlanDefinition.Action.SubjectX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun PlanDefinition.Action.RelatedAction.OffsetX.planDefinitionActionRelatedActionOffsetToHapi():
    Type {
    if (hasDuration()) {
      return (this.duration).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.relatedAction.offset[x]")
  }

  private fun Type.planDefinitionActionRelatedActionOffsetToProto():
    PlanDefinition.Action.RelatedAction.OffsetX {
    val protoValue = PlanDefinition.Action.RelatedAction.OffsetX.newBuilder()
    if (this is Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    return protoValue.build()
  }

  private fun PlanDefinition.Action.TimingX.planDefinitionActionTimingToHapi(): Type {
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasAge()) {
      return (this.age).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasDuration()) {
      return (this.duration).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.timing[x]")
  }

  private fun Type.planDefinitionActionTimingToProto(): PlanDefinition.Action.TimingX {
    val protoValue = PlanDefinition.Action.TimingX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is Age) {
      protoValue.age = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    if (this is Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    if (this is Timing) {
      protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  private fun PlanDefinition.Action.DefinitionX.planDefinitionActionDefinitionToHapi(): Type {
    if (hasCanonical()) {
      return (this.canonical).toHapi()
    }
    if (hasUri()) {
      return (this.uri).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for PlanDefinition.action.definition[x]")
  }

  private fun Type.planDefinitionActionDefinitionToProto(): PlanDefinition.Action.DefinitionX {
    val protoValue = PlanDefinition.Action.DefinitionX.newBuilder()
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    return protoValue.build()
  }

  fun PlanDefinition.toHapi(): org.hl7.fhir.r4.model.PlanDefinition {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasSubtitle()) {
      hapiValue.subtitleElement = subtitle.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.planDefinitionSubjectToHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasPublisher()) {
      hapiValue.publisherElement = publisher.toHapi()
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (useContextCount > 0) {
      hapiValue.useContext = useContextList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (hasUsage()) {
      hapiValue.usageElement = usage.toHapi()
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasApprovalDate()) {
      hapiValue.approvalDateElement = approvalDate.toHapi()
    }
    if (hasLastReviewDate()) {
      hapiValue.lastReviewDateElement = lastReviewDate.toHapi()
    }
    if (hasEffectivePeriod()) {
      hapiValue.effectivePeriod = effectivePeriod.toHapi()
    }
    if (topicCount > 0) {
      hapiValue.topic = topicList.map { it.toHapi() }
    }
    if (authorCount > 0) {
      hapiValue.author = authorList.map { it.toHapi() }
    }
    if (editorCount > 0) {
      hapiValue.editor = editorList.map { it.toHapi() }
    }
    if (reviewerCount > 0) {
      hapiValue.reviewer = reviewerList.map { it.toHapi() }
    }
    if (endorserCount > 0) {
      hapiValue.endorser = endorserList.map { it.toHapi() }
    }
    if (relatedArtifactCount > 0) {
      hapiValue.relatedArtifact = relatedArtifactList.map { it.toHapi() }
    }
    if (libraryCount > 0) {
      hapiValue.library = libraryList.map { it.toHapi() }
    }
    if (goalCount > 0) {
      hapiValue.goal = goalList.map { it.toHapi() }
    }
    if (actionCount > 0) {
      hapiValue.action = actionList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.PlanDefinition.toProto(): PlanDefinition {
    val protoValue = PlanDefinition.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasSubtitle()) {
      protoValue.subtitle = subtitleElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        PlanDefinition.StatusCode.newBuilder()
          .setValue(
            PublicationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasExperimental()) {
      protoValue.experimental = experimentalElement.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.planDefinitionSubjectToProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasPublisher()) {
      protoValue.publisher = publisherElement.toProto()
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasUsage()) {
      protoValue.usage = usageElement.toProto()
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasApprovalDate()) {
      protoValue.approvalDate = approvalDateElement.toProto()
    }
    if (hasLastReviewDate()) {
      protoValue.lastReviewDate = lastReviewDateElement.toProto()
    }
    if (hasEffectivePeriod()) {
      protoValue.effectivePeriod = effectivePeriod.toProto()
    }
    if (hasTopic()) {
      protoValue.addAllTopic(topic.map { it.toProto() })
    }
    if (hasAuthor()) {
      protoValue.addAllAuthor(author.map { it.toProto() })
    }
    if (hasEditor()) {
      protoValue.addAllEditor(editor.map { it.toProto() })
    }
    if (hasReviewer()) {
      protoValue.addAllReviewer(reviewer.map { it.toProto() })
    }
    if (hasEndorser()) {
      protoValue.addAllEndorser(endorser.map { it.toProto() })
    }
    if (hasRelatedArtifact()) {
      protoValue.addAllRelatedArtifact(relatedArtifact.map { it.toProto() })
    }
    if (hasLibrary()) {
      protoValue.addAllLibrary(library.map { it.toProto() })
    }
    if (hasGoal()) {
      protoValue.addAllGoal(goal.map { it.toProto() })
    }
    if (hasAction()) {
      protoValue.addAllAction(action.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalComponent.toProto():
    PlanDefinition.Goal {
    val protoValue = PlanDefinition.Goal.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasDescription()) {
      protoValue.description = description.toProto()
    }
    if (hasPriority()) {
      protoValue.priority = priority.toProto()
    }
    if (hasStart()) {
      protoValue.start = start.toProto()
    }
    if (hasAddresses()) {
      protoValue.addAllAddresses(addresses.map { it.toProto() })
    }
    if (hasDocumentation()) {
      protoValue.addAllDocumentation(documentation.map { it.toProto() })
    }
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalTargetComponent.toProto():
    PlanDefinition.Goal.Target {
    val protoValue = PlanDefinition.Goal.Target.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
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
      protoValue.detail = detail.planDefinitionGoalTargetDetailToProto()
    }
    if (hasDue()) {
      protoValue.due = due.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent.toProto():
    PlanDefinition.Action {
    val protoValue = PlanDefinition.Action.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPrefix()) {
      protoValue.prefix = prefixElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasTextEquivalent()) {
      protoValue.textEquivalent = textEquivalentElement.toProto()
    }
    if (hasPriority()) {
      protoValue.priority =
        PlanDefinition.Action.PriorityCode.newBuilder()
          .setValue(
            RequestPriorityCode.Value.valueOf(
              priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasReason()) {
      protoValue.addAllReason(reason.map { it.toProto() })
    }
    if (hasDocumentation()) {
      protoValue.addAllDocumentation(documentation.map { it.toProto() })
    }
    if (hasGoalId()) {
      protoValue.addAllGoalId(goalId.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.subject = subject.planDefinitionActionSubjectToProto()
    }
    if (hasTrigger()) {
      protoValue.addAllTrigger(trigger.map { it.toProto() })
    }
    if (hasCondition()) {
      protoValue.addAllCondition(condition.map { it.toProto() })
    }
    if (hasInput()) {
      protoValue.addAllInput(input.map { it.toProto() })
    }
    if (hasOutput()) {
      protoValue.addAllOutput(output.map { it.toProto() })
    }
    if (hasRelatedAction()) {
      protoValue.addAllRelatedAction(relatedAction.map { it.toProto() })
    }
    if (hasTiming()) {
      protoValue.timing = timing.planDefinitionActionTimingToProto()
    }
    if (hasParticipant()) {
      protoValue.addAllParticipant(participant.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasGroupingBehavior()) {
      protoValue.groupingBehavior =
        PlanDefinition.Action.GroupingBehaviorCode.newBuilder()
          .setValue(
            ActionGroupingBehaviorCode.Value.valueOf(
              groupingBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasSelectionBehavior()) {
      protoValue.selectionBehavior =
        PlanDefinition.Action.SelectionBehaviorCode.newBuilder()
          .setValue(
            ActionSelectionBehaviorCode.Value.valueOf(
              selectionBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasRequiredBehavior()) {
      protoValue.requiredBehavior =
        PlanDefinition.Action.RequiredBehaviorCode.newBuilder()
          .setValue(
            ActionRequiredBehaviorCode.Value.valueOf(
              requiredBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasPrecheckBehavior()) {
      protoValue.precheckBehavior =
        PlanDefinition.Action.PrecheckBehaviorCode.newBuilder()
          .setValue(
            ActionPrecheckBehaviorCode.Value.valueOf(
              precheckBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCardinalityBehavior()) {
      protoValue.cardinalityBehavior =
        PlanDefinition.Action.CardinalityBehaviorCode.newBuilder()
          .setValue(
            ActionCardinalityBehaviorCode.Value.valueOf(
              cardinalityBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasDefinition()) {
      protoValue.definition = definition.planDefinitionActionDefinitionToProto()
    }
    if (hasTransform()) {
      protoValue.transform = transformElement.toProto()
    }
    if (hasDynamicValue()) {
      protoValue.addAllDynamicValue(dynamicValue.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent.toProto():
    PlanDefinition.Action.Condition {
    val protoValue = PlanDefinition.Action.Condition.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasKind()) {
      protoValue.kind =
        PlanDefinition.Action.Condition.KindCode.newBuilder()
          .setValue(
            ActionConditionKindCode.Value.valueOf(
              kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasExpression()) {
      protoValue.expression = expression.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionRelatedActionComponent.toProto():
    PlanDefinition.Action.RelatedAction {
    val protoValue = PlanDefinition.Action.RelatedAction.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasActionId()) {
      protoValue.actionId = actionIdElement.toProto()
    }
    if (hasRelationship()) {
      protoValue.relationship =
        PlanDefinition.Action.RelatedAction.RelationshipCode.newBuilder()
          .setValue(
            ActionRelationshipTypeCode.Value.valueOf(
              relationship.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasOffset()) {
      protoValue.offset = offset.planDefinitionActionRelatedActionOffsetToProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionParticipantComponent.toProto():
    PlanDefinition.Action.Participant {
    val protoValue = PlanDefinition.Action.Participant.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type =
        PlanDefinition.Action.Participant.TypeCode.newBuilder()
          .setValue(
            ActionParticipantTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasRole()) {
      protoValue.role = role.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionDynamicValueComponent.toProto():
    PlanDefinition.Action.DynamicValue {
    val protoValue = PlanDefinition.Action.DynamicValue.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPath()) {
      protoValue.path = pathElement.toProto()
    }
    if (hasExpression()) {
      protoValue.expression = expression.toProto()
    }
    return protoValue.build()
  }

  private fun PlanDefinition.Goal.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasDescription()) {
      hapiValue.description = description.toHapi()
    }
    if (hasPriority()) {
      hapiValue.priority = priority.toHapi()
    }
    if (hasStart()) {
      hapiValue.start = start.toHapi()
    }
    if (addressesCount > 0) {
      hapiValue.addresses = addressesList.map { it.toHapi() }
    }
    if (documentationCount > 0) {
      hapiValue.documentation = documentationList.map { it.toHapi() }
    }
    if (targetCount > 0) {
      hapiValue.target = targetList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun PlanDefinition.Goal.Target.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalTargetComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionGoalTargetComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
      hapiValue.detail = detail.planDefinitionGoalTargetDetailToHapi()
    }
    if (hasDue()) {
      hapiValue.due = due.toHapi()
    }
    return hapiValue
  }

  private fun PlanDefinition.Action.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPrefix()) {
      hapiValue.prefixElement = prefix.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasTextEquivalent()) {
      hapiValue.textEquivalentElement = textEquivalent.toHapi()
    }
    if (hasPriority()) {
      hapiValue.priority =
        org.hl7.fhir.r4.model.PlanDefinition.RequestPriority.valueOf(
          priority.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (reasonCount > 0) {
      hapiValue.reason = reasonList.map { it.toHapi() }
    }
    if (documentationCount > 0) {
      hapiValue.documentation = documentationList.map { it.toHapi() }
    }
    if (goalIdCount > 0) {
      hapiValue.goalId = goalIdList.map { it.toHapi() }
    }
    if (hasSubject()) {
      hapiValue.subject = subject.planDefinitionActionSubjectToHapi()
    }
    if (triggerCount > 0) {
      hapiValue.trigger = triggerList.map { it.toHapi() }
    }
    if (conditionCount > 0) {
      hapiValue.condition = conditionList.map { it.toHapi() }
    }
    if (inputCount > 0) {
      hapiValue.input = inputList.map { it.toHapi() }
    }
    if (outputCount > 0) {
      hapiValue.output = outputList.map { it.toHapi() }
    }
    if (relatedActionCount > 0) {
      hapiValue.relatedAction = relatedActionList.map { it.toHapi() }
    }
    if (hasTiming()) {
      hapiValue.timing = timing.planDefinitionActionTimingToHapi()
    }
    if (participantCount > 0) {
      hapiValue.participant = participantList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasGroupingBehavior()) {
      hapiValue.groupingBehavior =
        org.hl7.fhir.r4.model.PlanDefinition.ActionGroupingBehavior.valueOf(
          groupingBehavior.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasSelectionBehavior()) {
      hapiValue.selectionBehavior =
        org.hl7.fhir.r4.model.PlanDefinition.ActionSelectionBehavior.valueOf(
          selectionBehavior.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasRequiredBehavior()) {
      hapiValue.requiredBehavior =
        org.hl7.fhir.r4.model.PlanDefinition.ActionRequiredBehavior.valueOf(
          requiredBehavior.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasPrecheckBehavior()) {
      hapiValue.precheckBehavior =
        org.hl7.fhir.r4.model.PlanDefinition.ActionPrecheckBehavior.valueOf(
          precheckBehavior.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasCardinalityBehavior()) {
      hapiValue.cardinalityBehavior =
        org.hl7.fhir.r4.model.PlanDefinition.ActionCardinalityBehavior.valueOf(
          cardinalityBehavior.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasDefinition()) {
      hapiValue.definition = definition.planDefinitionActionDefinitionToHapi()
    }
    if (hasTransform()) {
      hapiValue.transformElement = transform.toHapi()
    }
    if (dynamicValueCount > 0) {
      hapiValue.dynamicValue = dynamicValueList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun PlanDefinition.Action.Condition.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasKind()) {
      hapiValue.kind =
        org.hl7.fhir.r4.model.PlanDefinition.ActionConditionKind.valueOf(
          kind.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasExpression()) {
      hapiValue.expression = expression.toHapi()
    }
    return hapiValue
  }

  private fun PlanDefinition.Action.RelatedAction.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionRelatedActionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionRelatedActionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasActionId()) {
      hapiValue.actionIdElement = actionId.toHapi()
    }
    if (hasRelationship()) {
      hapiValue.relationship =
        org.hl7.fhir.r4.model.PlanDefinition.ActionRelationshipType.valueOf(
          relationship.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasOffset()) {
      hapiValue.offset = offset.planDefinitionActionRelatedActionOffsetToHapi()
    }
    return hapiValue
  }

  private fun PlanDefinition.Action.Participant.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionParticipantComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.PlanDefinition.ActionParticipantType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasRole()) {
      hapiValue.role = role.toHapi()
    }
    return hapiValue
  }

  private fun PlanDefinition.Action.DynamicValue.toHapi():
    org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionDynamicValueComponent {
    val hapiValue = org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionDynamicValueComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPath()) {
      hapiValue.pathElement = path.toHapi()
    }
    if (hasExpression()) {
      hapiValue.expression = expression.toHapi()
    }
    return hapiValue
  }
}
