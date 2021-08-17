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
import com.google.fhir.r4.core.EvidenceVariable
import com.google.fhir.r4.core.EvidenceVariable.Characteristic
import com.google.fhir.r4.core.EvidenceVariableTypeCode
import com.google.fhir.r4.core.GroupMeasureCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DataRequirement
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Duration
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.TriggerDefinition
import org.hl7.fhir.r4.model.Type

object EvidenceVariableConverter {
  private fun EvidenceVariable.Characteristic.DefinitionX.evidenceVariableCharacteristicDefinitionToHapi():
    Type {
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    if (hasCanonical()) {
      return (this.canonical).toHapi()
    }
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasExpression()) {
      return (this.expression).toHapi()
    }
    if (hasDataRequirement()) {
      return (this.dataRequirement).toHapi()
    }
    if (hasTriggerDefinition()) {
      return (this.triggerDefinition).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for EvidenceVariable.characteristic.definition[x]")
  }

  private fun Type.evidenceVariableCharacteristicDefinitionToProto():
    EvidenceVariable.Characteristic.DefinitionX {
    val protoValue = EvidenceVariable.Characteristic.DefinitionX.newBuilder()
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Expression) {
      protoValue.expression = this.toProto()
    }
    if (this is DataRequirement) {
      protoValue.dataRequirement = this.toProto()
    }
    if (this is TriggerDefinition) {
      protoValue.triggerDefinition = this.toProto()
    }
    return protoValue.build()
  }

  private fun EvidenceVariable.Characteristic.ParticipantEffectiveX.evidenceVariableCharacteristicParticipantEffectiveToHapi():
    Type {
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasDuration()) {
      return (this.duration).toHapi()
    }
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for EvidenceVariable.characteristic.participantEffective[x]"
    )
  }

  private fun Type.evidenceVariableCharacteristicParticipantEffectiveToProto():
    EvidenceVariable.Characteristic.ParticipantEffectiveX {
    val protoValue = EvidenceVariable.Characteristic.ParticipantEffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    if (this is Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is Timing) {
      protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  fun EvidenceVariable.toHapi(): org.hl7.fhir.r4.model.EvidenceVariable {
    val hapiValue = org.hl7.fhir.r4.model.EvidenceVariable()
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
    if (hasShortTitle()) {
      hapiValue.shortTitleElement = shortTitle.toHapi()
    }
    if (hasSubtitle()) {
      hapiValue.subtitleElement = subtitle.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
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
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (useContextCount > 0) {
      hapiValue.useContext = useContextList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
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
    if (hasType()) {
      hapiValue.type =
        org.hl7.fhir.r4.model.EvidenceVariable.EvidenceVariableType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (characteristicCount > 0) {
      hapiValue.characteristic = characteristicList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.EvidenceVariable.toProto(): EvidenceVariable {
    val protoValue = EvidenceVariable.newBuilder()
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
    if (hasShortTitle()) {
      protoValue.shortTitle = shortTitleElement.toProto()
    }
    if (hasSubtitle()) {
      protoValue.subtitle = subtitleElement.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        EvidenceVariable.StatusCode.newBuilder()
          .setValue(
            PublicationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
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
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
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
    if (hasType()) {
      protoValue.type =
        EvidenceVariable.TypeCode.newBuilder()
          .setValue(
            EvidenceVariableTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCharacteristic()) {
      protoValue.addAllCharacteristic(characteristic.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.EvidenceVariable.EvidenceVariableCharacteristicComponent.toProto():
    EvidenceVariable.Characteristic {
    val protoValue = EvidenceVariable.Characteristic.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasDefinition()) {
      protoValue.definition = definition.evidenceVariableCharacteristicDefinitionToProto()
    }
    if (hasUsageContext()) {
      protoValue.addAllUsageContext(usageContext.map { it.toProto() })
    }
    if (hasExclude()) {
      protoValue.exclude = excludeElement.toProto()
    }
    if (hasParticipantEffective()) {
      protoValue.participantEffective =
        participantEffective.evidenceVariableCharacteristicParticipantEffectiveToProto()
    }
    if (hasTimeFromStart()) {
      protoValue.timeFromStart = timeFromStart.toProto()
    }
    if (hasGroupMeasure()) {
      protoValue.groupMeasure =
        EvidenceVariable.Characteristic.GroupMeasureCode.newBuilder()
          .setValue(
            GroupMeasureCode.Value.valueOf(
              groupMeasure.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    return protoValue.build()
  }

  private fun EvidenceVariable.Characteristic.toHapi():
    org.hl7.fhir.r4.model.EvidenceVariable.EvidenceVariableCharacteristicComponent {
    val hapiValue = org.hl7.fhir.r4.model.EvidenceVariable.EvidenceVariableCharacteristicComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasDefinition()) {
      hapiValue.definition = definition.evidenceVariableCharacteristicDefinitionToHapi()
    }
    if (usageContextCount > 0) {
      hapiValue.usageContext = usageContextList.map { it.toHapi() }
    }
    if (hasExclude()) {
      hapiValue.excludeElement = exclude.toHapi()
    }
    if (hasParticipantEffective()) {
      hapiValue.participantEffective =
        participantEffective.evidenceVariableCharacteristicParticipantEffectiveToHapi()
    }
    if (hasTimeFromStart()) {
      hapiValue.timeFromStart = timeFromStart.toHapi()
    }
    if (hasGroupMeasure()) {
      hapiValue.groupMeasure =
        org.hl7.fhir.r4.model.EvidenceVariable.GroupMeasure.valueOf(
          groupMeasure.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    return hapiValue
  }
}
