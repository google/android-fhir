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
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DataRequirement
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.EvidenceVariableTypeCode
import com.google.fhir.r4.core.Expression
import com.google.fhir.r4.core.GroupMeasureCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.ResearchElementDefinition
import com.google.fhir.r4.core.ResearchElementDefinition.Characteristic
import com.google.fhir.r4.core.ResearchElementTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type

object ResearchElementDefinitionConverter {
  private fun ResearchElementDefinition.SubjectX.researchElementDefinitionSubjectToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ResearchElementDefinition.subject[x]")
  }

  private fun Type.researchElementDefinitionSubjectToProto(): ResearchElementDefinition.SubjectX {
    val protoValue = ResearchElementDefinition.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun ResearchElementDefinition.Characteristic.DefinitionX.researchElementDefinitionCharacteristicDefinitionToHapi():
    Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.canonical != Canonical.newBuilder().defaultInstanceForType) {
      return (this.canonical).toHapi()
    }
    if (this.expression != Expression.newBuilder().defaultInstanceForType) {
      return (this.expression).toHapi()
    }
    if (this.dataRequirement != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.dataRequirement).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ResearchElementDefinition.characteristic.definition[x]"
    )
  }

  private fun Type.researchElementDefinitionCharacteristicDefinitionToProto():
    ResearchElementDefinition.Characteristic.DefinitionX {
    val protoValue = ResearchElementDefinition.Characteristic.DefinitionX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.expression = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.dataRequirement = this.toProto()
    }
    return protoValue.build()
  }

  private fun ResearchElementDefinition.Characteristic.StudyEffectiveX.researchElementDefinitionCharacteristicStudyEffectiveToHapi():
    Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ResearchElementDefinition.characteristic.studyEffective[x]"
    )
  }

  private fun Type.researchElementDefinitionCharacteristicStudyEffectiveToProto():
    ResearchElementDefinition.Characteristic.StudyEffectiveX {
    val protoValue = ResearchElementDefinition.Characteristic.StudyEffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  private fun ResearchElementDefinition.Characteristic.ParticipantEffectiveX.researchElementDefinitionCharacteristicParticipantEffectiveToHapi():
    Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.duration != Duration.newBuilder().defaultInstanceForType) {
      return (this.duration).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ResearchElementDefinition.characteristic.participantEffective[x]"
    )
  }

  private fun Type.researchElementDefinitionCharacteristicParticipantEffectiveToProto():
    ResearchElementDefinition.Characteristic.ParticipantEffectiveX {
    val protoValue = ResearchElementDefinition.Characteristic.ParticipantEffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.timing = this.toProto()
    }
    return protoValue.build()
  }

  fun ResearchElementDefinition.toHapi(): org.hl7.fhir.r4.model.ResearchElementDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ResearchElementDefinition()
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
    hapiValue.status =
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.researchElementDefinitionSubjectToHapi()
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
    if (commentCount > 0) {
      hapiValue.comment = commentList.map { it.toHapi() }
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
    hapiValue.type =
      org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.variableType =
      org.hl7.fhir.r4.model.ResearchElementDefinition.VariableType.valueOf(
        variableType.value.name.hapiCodeCheck().replace("_", "")
      )
    if (characteristicCount > 0) {
      hapiValue.characteristic = characteristicList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ResearchElementDefinition.toProto(): ResearchElementDefinition {
    val protoValue = ResearchElementDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      ResearchElementDefinition.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasExperimental()) {
      protoValue.experimental = experimentalElement.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.researchElementDefinitionSubjectToProto()
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
    if (hasComment()) {
      protoValue.addAllComment(comment.map { it.toProto() })
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
    protoValue.type =
      ResearchElementDefinition.TypeCode.newBuilder()
        .setValue(
          ResearchElementTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.variableType =
      ResearchElementDefinition.VariableTypeCode.newBuilder()
        .setValue(
          EvidenceVariableTypeCode.Value.valueOf(
            variableType.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCharacteristic()) {
      protoValue.addAllCharacteristic(characteristic.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementDefinitionCharacteristicComponent.toProto():
    ResearchElementDefinition.Characteristic {
    val protoValue =
      ResearchElementDefinition.Characteristic.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDefinition()) {
      protoValue.definition = definition.researchElementDefinitionCharacteristicDefinitionToProto()
    }
    if (hasUsageContext()) {
      protoValue.addAllUsageContext(usageContext.map { it.toProto() })
    }
    if (hasExclude()) {
      protoValue.exclude = excludeElement.toProto()
    }
    if (hasUnitOfMeasure()) {
      protoValue.unitOfMeasure = unitOfMeasure.toProto()
    }
    if (hasStudyEffectiveDescription()) {
      protoValue.studyEffectiveDescription = studyEffectiveDescriptionElement.toProto()
    }
    if (hasStudyEffective()) {
      protoValue.studyEffective =
        studyEffective.researchElementDefinitionCharacteristicStudyEffectiveToProto()
    }
    if (hasStudyEffectiveTimeFromStart()) {
      protoValue.studyEffectiveTimeFromStart = studyEffectiveTimeFromStart.toProto()
    }
    protoValue.studyEffectiveGroupMeasure =
      ResearchElementDefinition.Characteristic.StudyEffectiveGroupMeasureCode.newBuilder()
        .setValue(
          GroupMeasureCode.Value.valueOf(
            studyEffectiveGroupMeasure.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasParticipantEffectiveDescription()) {
      protoValue.participantEffectiveDescription = participantEffectiveDescriptionElement.toProto()
    }
    if (hasParticipantEffective()) {
      protoValue.participantEffective =
        participantEffective.researchElementDefinitionCharacteristicParticipantEffectiveToProto()
    }
    if (hasParticipantEffectiveTimeFromStart()) {
      protoValue.participantEffectiveTimeFromStart = participantEffectiveTimeFromStart.toProto()
    }
    protoValue.participantEffectiveGroupMeasure =
      ResearchElementDefinition.Characteristic.ParticipantEffectiveGroupMeasureCode.newBuilder()
        .setValue(
          GroupMeasureCode.Value.valueOf(
            participantEffectiveGroupMeasure
              .toCode()
              .protoCodeCheck()
              .replace("-", "_")
              .toUpperCase()
          )
        )
        .build()
    return protoValue.build()
  }

  private fun ResearchElementDefinition.Characteristic.toHapi():
    org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementDefinitionCharacteristicComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ResearchElementDefinition
        .ResearchElementDefinitionCharacteristicComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDefinition()) {
      hapiValue.definition = definition.researchElementDefinitionCharacteristicDefinitionToHapi()
    }
    if (usageContextCount > 0) {
      hapiValue.usageContext = usageContextList.map { it.toHapi() }
    }
    if (hasExclude()) {
      hapiValue.excludeElement = exclude.toHapi()
    }
    if (hasUnitOfMeasure()) {
      hapiValue.unitOfMeasure = unitOfMeasure.toHapi()
    }
    if (hasStudyEffectiveDescription()) {
      hapiValue.studyEffectiveDescriptionElement = studyEffectiveDescription.toHapi()
    }
    if (hasStudyEffective()) {
      hapiValue.studyEffective =
        studyEffective.researchElementDefinitionCharacteristicStudyEffectiveToHapi()
    }
    if (hasStudyEffectiveTimeFromStart()) {
      hapiValue.studyEffectiveTimeFromStart = studyEffectiveTimeFromStart.toHapi()
    }
    hapiValue.studyEffectiveGroupMeasure =
      org.hl7.fhir.r4.model.ResearchElementDefinition.GroupMeasure.valueOf(
        studyEffectiveGroupMeasure.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasParticipantEffectiveDescription()) {
      hapiValue.participantEffectiveDescriptionElement = participantEffectiveDescription.toHapi()
    }
    if (hasParticipantEffective()) {
      hapiValue.participantEffective =
        participantEffective.researchElementDefinitionCharacteristicParticipantEffectiveToHapi()
    }
    if (hasParticipantEffectiveTimeFromStart()) {
      hapiValue.participantEffectiveTimeFromStart = participantEffectiveTimeFromStart.toHapi()
    }
    hapiValue.participantEffectiveGroupMeasure =
      org.hl7.fhir.r4.model.ResearchElementDefinition.GroupMeasure.valueOf(
        participantEffectiveGroupMeasure.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }
}
