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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type

public object ResearchElementDefinitionConverter {
  @JvmStatic
  private fun ResearchElementDefinition.SubjectX.researchElementDefinitionSubjectToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ResearchElementDefinition.subject[x]")
  }

  @JvmStatic
  private fun Type.researchElementDefinitionSubjectToProto(): ResearchElementDefinition.SubjectX {
    val protoValue = ResearchElementDefinition.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ResearchElementDefinition.Characteristic.DefinitionX.researchElementDefinitionCharacteristicDefinitionToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    if (this.getExpression() != Expression.newBuilder().defaultInstanceForType) {
      return (this.getExpression()).toHapi()
    }
    if (this.getDataRequirement() != DataRequirement.newBuilder().defaultInstanceForType) {
      return (this.getDataRequirement()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ResearchElementDefinition.characteristic.definition[x]"
    )
  }

  @JvmStatic
  private fun Type.researchElementDefinitionCharacteristicDefinitionToProto():
    ResearchElementDefinition.Characteristic.DefinitionX {
    val protoValue = ResearchElementDefinition.Characteristic.DefinitionX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Expression) {
      protoValue.setExpression(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.DataRequirement) {
      protoValue.setDataRequirement(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ResearchElementDefinition.Characteristic.StudyEffectiveX.researchElementDefinitionCharacteristicStudyEffectiveToHapi():
    Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ResearchElementDefinition.characteristic.studyEffective[x]"
    )
  }

  @JvmStatic
  private fun Type.researchElementDefinitionCharacteristicStudyEffectiveToProto():
    ResearchElementDefinition.Characteristic.StudyEffectiveX {
    val protoValue = ResearchElementDefinition.Characteristic.StudyEffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ResearchElementDefinition.Characteristic.ParticipantEffectiveX.researchElementDefinitionCharacteristicParticipantEffectiveToHapi():
    Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ResearchElementDefinition.characteristic.participantEffective[x]"
    )
  }

  @JvmStatic
  private fun Type.researchElementDefinitionCharacteristicParticipantEffectiveToProto():
    ResearchElementDefinition.Characteristic.ParticipantEffectiveX {
    val protoValue = ResearchElementDefinition.Characteristic.ParticipantEffectiveX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ResearchElementDefinition.toHapi(): org.hl7.fhir.r4.model.ResearchElementDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ResearchElementDefinition()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (hasVersion()) {
      hapiValue.setVersionElement(version.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasTitle()) {
      hapiValue.setTitleElement(title.toHapi())
    }
    if (hasShortTitle()) {
      hapiValue.setShortTitleElement(shortTitle.toHapi())
    }
    if (hasSubtitle()) {
      hapiValue.setSubtitleElement(subtitle.toHapi())
    }
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasExperimental()) {
      hapiValue.setExperimentalElement(experimental.toHapi())
    }
    if (hasSubject()) {
      hapiValue.setSubject(subject.researchElementDefinitionSubjectToHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    if (hasPublisher()) {
      hapiValue.setPublisherElement(publisher.toHapi())
    }
    if (contactCount > 0) {
      hapiValue.setContact(contactList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (commentCount > 0) {
      hapiValue.setComment(commentList.map { it.toHapi() })
    }
    if (useContextCount > 0) {
      hapiValue.setUseContext(useContextList.map { it.toHapi() })
    }
    if (jurisdictionCount > 0) {
      hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    }
    if (hasPurpose()) {
      hapiValue.setPurposeElement(purpose.toHapi())
    }
    if (hasUsage()) {
      hapiValue.setUsageElement(usage.toHapi())
    }
    if (hasCopyright()) {
      hapiValue.setCopyrightElement(copyright.toHapi())
    }
    if (hasApprovalDate()) {
      hapiValue.setApprovalDateElement(approvalDate.toHapi())
    }
    if (hasLastReviewDate()) {
      hapiValue.setLastReviewDateElement(lastReviewDate.toHapi())
    }
    if (hasEffectivePeriod()) {
      hapiValue.setEffectivePeriod(effectivePeriod.toHapi())
    }
    if (topicCount > 0) {
      hapiValue.setTopic(topicList.map { it.toHapi() })
    }
    if (authorCount > 0) {
      hapiValue.setAuthor(authorList.map { it.toHapi() })
    }
    if (editorCount > 0) {
      hapiValue.setEditor(editorList.map { it.toHapi() })
    }
    if (reviewerCount > 0) {
      hapiValue.setReviewer(reviewerList.map { it.toHapi() })
    }
    if (endorserCount > 0) {
      hapiValue.setEndorser(endorserList.map { it.toHapi() })
    }
    if (relatedArtifactCount > 0) {
      hapiValue.setRelatedArtifact(relatedArtifactList.map { it.toHapi() })
    }
    if (libraryCount > 0) {
      hapiValue.setLibrary(libraryList.map { it.toHapi() })
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setVariableType(
      org.hl7.fhir.r4.model.ResearchElementDefinition.VariableType.valueOf(
        variableType.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (characteristicCount > 0) {
      hapiValue.setCharacteristic(characteristicList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ResearchElementDefinition.toProto(): ResearchElementDefinition {
    val protoValue = ResearchElementDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.setVersion(versionElement.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasTitle()) {
      protoValue.setTitle(titleElement.toProto())
    }
    if (hasShortTitle()) {
      protoValue.setShortTitle(shortTitleElement.toProto())
    }
    if (hasSubtitle()) {
      protoValue.setSubtitle(subtitleElement.toProto())
    }
    protoValue.setStatus(
      ResearchElementDefinition.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasExperimental()) {
      protoValue.setExperimental(experimentalElement.toProto())
    }
    if (hasSubject()) {
      protoValue.setSubject(subject.researchElementDefinitionSubjectToProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    if (hasPublisher()) {
      protoValue.setPublisher(publisherElement.toProto())
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
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
      protoValue.setPurpose(purposeElement.toProto())
    }
    if (hasUsage()) {
      protoValue.setUsage(usageElement.toProto())
    }
    if (hasCopyright()) {
      protoValue.setCopyright(copyrightElement.toProto())
    }
    if (hasApprovalDate()) {
      protoValue.setApprovalDate(approvalDateElement.toProto())
    }
    if (hasLastReviewDate()) {
      protoValue.setLastReviewDate(lastReviewDateElement.toProto())
    }
    if (hasEffectivePeriod()) {
      protoValue.setEffectivePeriod(effectivePeriod.toProto())
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
    protoValue.setType(
      ResearchElementDefinition.TypeCode.newBuilder()
        .setValue(
          ResearchElementTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.setVariableType(
      ResearchElementDefinition.VariableTypeCode.newBuilder()
        .setValue(
          EvidenceVariableTypeCode.Value.valueOf(
            variableType.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasCharacteristic()) {
      protoValue.addAllCharacteristic(characteristic.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
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
      protoValue.setDefinition(
        definition.researchElementDefinitionCharacteristicDefinitionToProto()
      )
    }
    if (hasUsageContext()) {
      protoValue.addAllUsageContext(usageContext.map { it.toProto() })
    }
    if (hasExclude()) {
      protoValue.setExclude(excludeElement.toProto())
    }
    if (hasUnitOfMeasure()) {
      protoValue.setUnitOfMeasure(unitOfMeasure.toProto())
    }
    if (hasStudyEffectiveDescription()) {
      protoValue.setStudyEffectiveDescription(studyEffectiveDescriptionElement.toProto())
    }
    if (hasStudyEffective()) {
      protoValue.setStudyEffective(
        studyEffective.researchElementDefinitionCharacteristicStudyEffectiveToProto()
      )
    }
    if (hasStudyEffectiveTimeFromStart()) {
      protoValue.setStudyEffectiveTimeFromStart(studyEffectiveTimeFromStart.toProto())
    }
    protoValue.setStudyEffectiveGroupMeasure(
      ResearchElementDefinition.Characteristic.StudyEffectiveGroupMeasureCode.newBuilder()
        .setValue(
          GroupMeasureCode.Value.valueOf(
            studyEffectiveGroupMeasure.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasParticipantEffectiveDescription()) {
      protoValue.setParticipantEffectiveDescription(
        participantEffectiveDescriptionElement.toProto()
      )
    }
    if (hasParticipantEffective()) {
      protoValue.setParticipantEffective(
        participantEffective.researchElementDefinitionCharacteristicParticipantEffectiveToProto()
      )
    }
    if (hasParticipantEffectiveTimeFromStart()) {
      protoValue.setParticipantEffectiveTimeFromStart(participantEffectiveTimeFromStart.toProto())
    }
    protoValue.setParticipantEffectiveGroupMeasure(
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
    )
    return protoValue.build()
  }

  @JvmStatic
  private fun ResearchElementDefinition.Characteristic.toHapi():
    org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementDefinitionCharacteristicComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ResearchElementDefinition
        .ResearchElementDefinitionCharacteristicComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDefinition()) {
      hapiValue.setDefinition(definition.researchElementDefinitionCharacteristicDefinitionToHapi())
    }
    if (usageContextCount > 0) {
      hapiValue.setUsageContext(usageContextList.map { it.toHapi() })
    }
    if (hasExclude()) {
      hapiValue.setExcludeElement(exclude.toHapi())
    }
    if (hasUnitOfMeasure()) {
      hapiValue.setUnitOfMeasure(unitOfMeasure.toHapi())
    }
    if (hasStudyEffectiveDescription()) {
      hapiValue.setStudyEffectiveDescriptionElement(studyEffectiveDescription.toHapi())
    }
    if (hasStudyEffective()) {
      hapiValue.setStudyEffective(
        studyEffective.researchElementDefinitionCharacteristicStudyEffectiveToHapi()
      )
    }
    if (hasStudyEffectiveTimeFromStart()) {
      hapiValue.setStudyEffectiveTimeFromStart(studyEffectiveTimeFromStart.toHapi())
    }
    hapiValue.setStudyEffectiveGroupMeasure(
      org.hl7.fhir.r4.model.ResearchElementDefinition.GroupMeasure.valueOf(
        studyEffectiveGroupMeasure.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasParticipantEffectiveDescription()) {
      hapiValue.setParticipantEffectiveDescriptionElement(participantEffectiveDescription.toHapi())
    }
    if (hasParticipantEffective()) {
      hapiValue.setParticipantEffective(
        participantEffective.researchElementDefinitionCharacteristicParticipantEffectiveToHapi()
      )
    }
    if (hasParticipantEffectiveTimeFromStart()) {
      hapiValue.setParticipantEffectiveTimeFromStart(participantEffectiveTimeFromStart.toHapi())
    }
    hapiValue.setParticipantEffectiveGroupMeasure(
      org.hl7.fhir.r4.model.ResearchElementDefinition.GroupMeasure.valueOf(
        participantEffectiveGroupMeasure.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    return hapiValue
  }
}
