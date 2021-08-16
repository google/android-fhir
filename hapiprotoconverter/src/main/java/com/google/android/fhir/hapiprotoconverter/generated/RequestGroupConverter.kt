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
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.ActionCardinalityBehaviorCode
import com.google.fhir.r4.core.ActionConditionKindCode
import com.google.fhir.r4.core.ActionGroupingBehaviorCode
import com.google.fhir.r4.core.ActionPrecheckBehaviorCode
import com.google.fhir.r4.core.ActionRelationshipTypeCode
import com.google.fhir.r4.core.ActionRequiredBehaviorCode
import com.google.fhir.r4.core.ActionSelectionBehaviorCode
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.RequestGroup
import com.google.fhir.r4.core.RequestGroup.Action
import com.google.fhir.r4.core.RequestGroup.Action.Condition
import com.google.fhir.r4.core.RequestGroup.Action.RelatedAction
import com.google.fhir.r4.core.RequestIntentCode
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.RequestStatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object RequestGroupConverter {
  @JvmStatic
  private fun RequestGroup.Action.RelatedAction.OffsetX.requestGroupActionRelatedActionOffsetToHapi():
    Type {
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RequestGroup.action.relatedAction.offset[x]")
  }

  @JvmStatic
  private fun Type.requestGroupActionRelatedActionOffsetToProto():
    RequestGroup.Action.RelatedAction.OffsetX {
    val protoValue = RequestGroup.Action.RelatedAction.OffsetX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun RequestGroup.Action.TimingX.requestGroupActionTimingToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.age != Age.newBuilder().defaultInstanceForType) {
      return (this.age).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RequestGroup.action.timing[x]")
  }

  @JvmStatic
  private fun Type.requestGroupActionTimingToProto(): RequestGroup.Action.TimingX {
    val protoValue = RequestGroup.Action.TimingX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.age = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun RequestGroup.toHapi(): org.hl7.fhir.r4.model.RequestGroup {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup()
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
    if (instantiatesCanonicalCount > 0) {
      hapiValue.instantiatesCanonical = instantiatesCanonicalList.map { it.toHapi() }
    }
    if (instantiatesUriCount > 0) {
      hapiValue.instantiatesUri = instantiatesUriList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (replacesCount > 0) {
      hapiValue.replaces = replacesList.map { it.toHapi() }
    }
    if (hasGroupIdentifier()) {
      hapiValue.groupIdentifier = groupIdentifier.toHapi()
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.RequestGroup.RequestStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.intent =
      org.hl7.fhir.r4.model.RequestGroup.RequestIntent.valueOf(
        intent.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.priority =
      org.hl7.fhir.r4.model.RequestGroup.RequestPriority.valueOf(
        priority.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasAuthoredOn()) {
      hapiValue.authoredOnElement = authoredOn.toHapi()
    }
    if (hasAuthor()) {
      hapiValue.author = author.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (actionCount > 0) {
      hapiValue.action = actionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.RequestGroup.toProto(): RequestGroup {
    val protoValue = RequestGroup.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasInstantiatesCanonical()) {
      protoValue.addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
    }
    if (hasInstantiatesUri()) {
      protoValue.addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasReplaces()) {
      protoValue.addAllReplaces(replaces.map { it.toProto() })
    }
    if (hasGroupIdentifier()) {
      protoValue.groupIdentifier = groupIdentifier.toProto()
    }
    protoValue.status =
      RequestGroup.StatusCode.newBuilder()
        .setValue(
          RequestStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.intent =
      RequestGroup.IntentCode.newBuilder()
        .setValue(
          RequestIntentCode.Value.valueOf(
            intent.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.priority =
      RequestGroup.PriorityCode.newBuilder()
        .setValue(
          RequestPriorityCode.Value.valueOf(
            priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasAuthoredOn()) {
      protoValue.authoredOn = authoredOnElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.author = author.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasAction()) {
      protoValue.addAllAction(action.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent.toProto():
    RequestGroup.Action {
    val protoValue = RequestGroup.Action.newBuilder().setId(String.newBuilder().setValue(id))
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
    protoValue.priority =
      RequestGroup.Action.PriorityCode.newBuilder()
        .setValue(
          RequestPriorityCode.Value.valueOf(
            priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasDocumentation()) {
      protoValue.addAllDocumentation(documentation.map { it.toProto() })
    }
    if (hasCondition()) {
      protoValue.addAllCondition(condition.map { it.toProto() })
    }
    if (hasRelatedAction()) {
      protoValue.addAllRelatedAction(relatedAction.map { it.toProto() })
    }
    if (hasTiming()) {
      protoValue.timing = timing.requestGroupActionTimingToProto()
    }
    if (hasParticipant()) {
      protoValue.addAllParticipant(participant.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    protoValue.groupingBehavior =
      RequestGroup.Action.GroupingBehaviorCode.newBuilder()
        .setValue(
          ActionGroupingBehaviorCode.Value.valueOf(
            groupingBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.selectionBehavior =
      RequestGroup.Action.SelectionBehaviorCode.newBuilder()
        .setValue(
          ActionSelectionBehaviorCode.Value.valueOf(
            selectionBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.requiredBehavior =
      RequestGroup.Action.RequiredBehaviorCode.newBuilder()
        .setValue(
          ActionRequiredBehaviorCode.Value.valueOf(
            requiredBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.precheckBehavior =
      RequestGroup.Action.PrecheckBehaviorCode.newBuilder()
        .setValue(
          ActionPrecheckBehaviorCode.Value.valueOf(
            precheckBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.cardinalityBehavior =
      RequestGroup.Action.CardinalityBehaviorCode.newBuilder()
        .setValue(
          ActionCardinalityBehaviorCode.Value.valueOf(
            cardinalityBehavior.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasResource()) {
      protoValue.resource = resource.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionConditionComponent.toProto():
    RequestGroup.Action.Condition {
    val protoValue =
      RequestGroup.Action.Condition.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.kind =
      RequestGroup.Action.Condition.KindCode.newBuilder()
        .setValue(
          ActionConditionKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasExpression()) {
      protoValue.expression = expression.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionRelatedActionComponent.toProto():
    RequestGroup.Action.RelatedAction {
    val protoValue =
      RequestGroup.Action.RelatedAction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasActionId()) {
      protoValue.actionId = actionIdElement.toProto()
    }
    protoValue.relationship =
      RequestGroup.Action.RelatedAction.RelationshipCode.newBuilder()
        .setValue(
          ActionRelationshipTypeCode.Value.valueOf(
            relationship.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasOffset()) {
      protoValue.offset = offset.requestGroupActionRelatedActionOffsetToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun RequestGroup.Action.toHapi():
    org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent()
    hapiValue.id = id.value
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
    hapiValue.priority =
      org.hl7.fhir.r4.model.RequestGroup.RequestPriority.valueOf(
        priority.value.name.hapiCodeCheck().replace("_", "")
      )
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (documentationCount > 0) {
      hapiValue.documentation = documentationList.map { it.toHapi() }
    }
    if (conditionCount > 0) {
      hapiValue.condition = conditionList.map { it.toHapi() }
    }
    if (relatedActionCount > 0) {
      hapiValue.relatedAction = relatedActionList.map { it.toHapi() }
    }
    if (hasTiming()) {
      hapiValue.timing = timing.requestGroupActionTimingToHapi()
    }
    if (participantCount > 0) {
      hapiValue.participant = participantList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    hapiValue.groupingBehavior =
      org.hl7.fhir.r4.model.RequestGroup.ActionGroupingBehavior.valueOf(
        groupingBehavior.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.selectionBehavior =
      org.hl7.fhir.r4.model.RequestGroup.ActionSelectionBehavior.valueOf(
        selectionBehavior.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.requiredBehavior =
      org.hl7.fhir.r4.model.RequestGroup.ActionRequiredBehavior.valueOf(
        requiredBehavior.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.precheckBehavior =
      org.hl7.fhir.r4.model.RequestGroup.ActionPrecheckBehavior.valueOf(
        precheckBehavior.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.cardinalityBehavior =
      org.hl7.fhir.r4.model.RequestGroup.ActionCardinalityBehavior.valueOf(
        cardinalityBehavior.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasResource()) {
      hapiValue.resource = resource.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun RequestGroup.Action.Condition.toHapi():
    org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionConditionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionConditionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.kind =
      org.hl7.fhir.r4.model.RequestGroup.ActionConditionKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasExpression()) {
      hapiValue.expression = expression.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun RequestGroup.Action.RelatedAction.toHapi():
    org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionRelatedActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionRelatedActionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasActionId()) {
      hapiValue.actionIdElement = actionId.toHapi()
    }
    hapiValue.relationship =
      org.hl7.fhir.r4.model.RequestGroup.ActionRelationshipType.valueOf(
        relationship.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasOffset()) {
      hapiValue.offset = offset.requestGroupActionRelatedActionOffsetToHapi()
    }
    return hapiValue
  }
}
