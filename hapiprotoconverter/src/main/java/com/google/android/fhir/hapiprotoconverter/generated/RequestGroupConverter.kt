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
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object RequestGroupConverter {
  private
      fun RequestGroup.Action.RelatedAction.OffsetX.requestGroupActionRelatedActionOffsetToHapi():
      Type {
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType ) {
      return (this.getDuration()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RequestGroup.action.relatedAction.offset[x]")
  }

  private fun Type.requestGroupActionRelatedActionOffsetToProto():
      RequestGroup.Action.RelatedAction.OffsetX {
    val protoValue = RequestGroup.Action.RelatedAction.OffsetX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    return protoValue.build()
  }

  private fun RequestGroup.Action.TimingX.requestGroupActionTimingToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType ) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType ) {
      return (this.getAge()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType ) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType ) {
      return (this.getDuration()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType ) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RequestGroup.action.timing[x]")
  }

  private fun Type.requestGroupActionTimingToProto(): RequestGroup.Action.TimingX {
    val protoValue = RequestGroup.Action.TimingX.newBuilder()
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

  public fun RequestGroup.toHapi(): org.hl7.fhir.r4.model.RequestGroup {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map{it.toHapi()})
    hapiValue.setInstantiatesUri(instantiatesUriList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setReplaces(replacesList.map{it.toHapi()})
    hapiValue.setGroupIdentifier(groupIdentifier.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.RequestGroup.RequestStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setIntent(org.hl7.fhir.r4.model.RequestGroup.RequestIntent.valueOf(intent.value.name.replace("_","")))
    hapiValue.setPriority(org.hl7.fhir.r4.model.RequestGroup.RequestPriority.valueOf(priority.value.name.replace("_","")))
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setAuthoredOnElement(authoredOn.toHapi())
    hapiValue.setAuthor(author.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setNote(noteList.map{it.toHapi()})
    hapiValue.setAction(actionList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.RequestGroup.toProto(): RequestGroup {
    val protoValue = RequestGroup.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllInstantiatesCanonical(instantiatesCanonical.map{it.toProto()})
    .addAllInstantiatesUri(instantiatesUri.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllReplaces(replaces.map{it.toProto()})
    .setGroupIdentifier(groupIdentifier.toProto())
    .setStatus(RequestGroup.StatusCode.newBuilder().setValue(RequestStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setIntent(RequestGroup.IntentCode.newBuilder().setValue(RequestIntentCode.Value.valueOf(intent.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPriority(RequestGroup.PriorityCode.newBuilder().setValue(RequestPriorityCode.Value.valueOf(priority.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCode(code.toProto())
    .setSubject(subject.toProto())
    .setEncounter(encounter.toProto())
    .setAuthoredOn(authoredOnElement.toProto())
    .setAuthor(author.toProto())
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .addAllNote(note.map{it.toProto()})
    .addAllAction(action.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent.toProto():
      RequestGroup.Action {
    val protoValue = RequestGroup.Action.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setPrefix(prefixElement.toProto())
    .setTitle(titleElement.toProto())
    .setDescription(descriptionElement.toProto())
    .setTextEquivalent(textEquivalentElement.toProto())
    .setPriority(RequestGroup.Action.PriorityCode.newBuilder().setValue(RequestPriorityCode.Value.valueOf(priority.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllCode(code.map{it.toProto()})
    .addAllDocumentation(documentation.map{it.toProto()})
    .addAllCondition(condition.map{it.toProto()})
    .addAllRelatedAction(relatedAction.map{it.toProto()})
    .setTiming(timing.requestGroupActionTimingToProto())
    .addAllParticipant(participant.map{it.toProto()})
    .setType(type.toProto())
    .setGroupingBehavior(RequestGroup.Action.GroupingBehaviorCode.newBuilder().setValue(ActionGroupingBehaviorCode.Value.valueOf(groupingBehavior.toCode().replace("-",
        "_").toUpperCase())).build())
    .setSelectionBehavior(RequestGroup.Action.SelectionBehaviorCode.newBuilder().setValue(ActionSelectionBehaviorCode.Value.valueOf(selectionBehavior.toCode().replace("-",
        "_").toUpperCase())).build())
    .setRequiredBehavior(RequestGroup.Action.RequiredBehaviorCode.newBuilder().setValue(ActionRequiredBehaviorCode.Value.valueOf(requiredBehavior.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPrecheckBehavior(RequestGroup.Action.PrecheckBehaviorCode.newBuilder().setValue(ActionPrecheckBehaviorCode.Value.valueOf(precheckBehavior.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCardinalityBehavior(RequestGroup.Action.CardinalityBehaviorCode.newBuilder().setValue(ActionCardinalityBehaviorCode.Value.valueOf(cardinalityBehavior.toCode().replace("-",
        "_").toUpperCase())).build())
    .setResource(resource.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionConditionComponent.toProto():
      RequestGroup.Action.Condition {
    val protoValue = RequestGroup.Action.Condition.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setKind(RequestGroup.Action.Condition.KindCode.newBuilder().setValue(ActionConditionKindCode.Value.valueOf(kind.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExpression(expression.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionRelatedActionComponent.toProto():
      RequestGroup.Action.RelatedAction {
    val protoValue = RequestGroup.Action.RelatedAction.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setActionId(actionIdElement.toProto())
    .setRelationship(RequestGroup.Action.RelatedAction.RelationshipCode.newBuilder().setValue(ActionRelationshipTypeCode.Value.valueOf(relationship.toCode().replace("-",
        "_").toUpperCase())).build())
    .setOffset(offset.requestGroupActionRelatedActionOffsetToProto())
    .build()
    return protoValue
  }

  private fun RequestGroup.Action.toHapi():
      org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setPrefixElement(prefix.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setTextEquivalentElement(textEquivalent.toHapi())
    hapiValue.setPriority(org.hl7.fhir.r4.model.RequestGroup.RequestPriority.valueOf(priority.value.name.replace("_","")))
    hapiValue.setCode(codeList.map{it.toHapi()})
    hapiValue.setDocumentation(documentationList.map{it.toHapi()})
    hapiValue.setCondition(conditionList.map{it.toHapi()})
    hapiValue.setRelatedAction(relatedActionList.map{it.toHapi()})
    hapiValue.setTiming(timing.requestGroupActionTimingToHapi())
    hapiValue.setParticipant(participantList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setGroupingBehavior(org.hl7.fhir.r4.model.RequestGroup.ActionGroupingBehavior.valueOf(groupingBehavior.value.name.replace("_","")))
    hapiValue.setSelectionBehavior(org.hl7.fhir.r4.model.RequestGroup.ActionSelectionBehavior.valueOf(selectionBehavior.value.name.replace("_","")))
    hapiValue.setRequiredBehavior(org.hl7.fhir.r4.model.RequestGroup.ActionRequiredBehavior.valueOf(requiredBehavior.value.name.replace("_","")))
    hapiValue.setPrecheckBehavior(org.hl7.fhir.r4.model.RequestGroup.ActionPrecheckBehavior.valueOf(precheckBehavior.value.name.replace("_","")))
    hapiValue.setCardinalityBehavior(org.hl7.fhir.r4.model.RequestGroup.ActionCardinalityBehavior.valueOf(cardinalityBehavior.value.name.replace("_","")))
    hapiValue.setResource(resource.toHapi())
    return hapiValue
  }

  private fun RequestGroup.Action.Condition.toHapi():
      org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionConditionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionConditionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setKind(org.hl7.fhir.r4.model.RequestGroup.ActionConditionKind.valueOf(kind.value.name.replace("_","")))
    hapiValue.setExpression(expression.toHapi())
    return hapiValue
  }

  private fun RequestGroup.Action.RelatedAction.toHapi():
      org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionRelatedActionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionRelatedActionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setActionIdElement(actionId.toHapi())
    hapiValue.setRelationship(org.hl7.fhir.r4.model.RequestGroup.ActionRelationshipType.valueOf(relationship.value.name.replace("_","")))
    hapiValue.setOffset(offset.requestGroupActionRelatedActionOffsetToHapi())
    return hapiValue
  }
}
