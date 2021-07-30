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
    hapiValue.setShortTitleElement(shortTitle.toHapi())
    hapiValue.setSubtitleElement(subtitle.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setSubject(subject.researchElementDefinitionSubjectToHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setComment(commentList.map { it.toHapi() })
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
    hapiValue.setType(
      org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementType.valueOf(
        type.value.name.replace("_", "")
      )
    )
    hapiValue.setVariableType(
      org.hl7.fhir.r4.model.ResearchElementDefinition.VariableType.valueOf(
        variableType.value.name.replace("_", "")
      )
    )
    hapiValue.setCharacteristic(characteristicList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ResearchElementDefinition.toProto(): ResearchElementDefinition {
    val protoValue =
      ResearchElementDefinition.newBuilder()
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
        .setShortTitle(shortTitleElement.toProto())
        .setSubtitle(subtitleElement.toProto())
        .setStatus(
          ResearchElementDefinition.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setSubject(subject.researchElementDefinitionSubjectToProto())
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllComment(comment.map { it.toProto() })
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
        .setType(
          ResearchElementDefinition.TypeCode.newBuilder()
            .setValue(
              ResearchElementTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setVariableType(
          ResearchElementDefinition.VariableTypeCode.newBuilder()
            .setValue(
              EvidenceVariableTypeCode.Value.valueOf(
                variableType.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllCharacteristic(characteristic.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementDefinitionCharacteristicComponent.toProto():
    ResearchElementDefinition.Characteristic {
    val protoValue =
      ResearchElementDefinition.Characteristic.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDefinition(definition.researchElementDefinitionCharacteristicDefinitionToProto())
        .addAllUsageContext(usageContext.map { it.toProto() })
        .setExclude(excludeElement.toProto())
        .setUnitOfMeasure(unitOfMeasure.toProto())
        .setStudyEffectiveDescription(studyEffectiveDescriptionElement.toProto())
        .setStudyEffective(
          studyEffective.researchElementDefinitionCharacteristicStudyEffectiveToProto()
        )
        .setStudyEffectiveTimeFromStart(studyEffectiveTimeFromStart.toProto())
        .setStudyEffectiveGroupMeasure(
          ResearchElementDefinition.Characteristic.StudyEffectiveGroupMeasureCode.newBuilder()
            .setValue(
              GroupMeasureCode.Value.valueOf(
                studyEffectiveGroupMeasure.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setParticipantEffectiveDescription(participantEffectiveDescriptionElement.toProto())
        .setParticipantEffective(
          participantEffective.researchElementDefinitionCharacteristicParticipantEffectiveToProto()
        )
        .setParticipantEffectiveTimeFromStart(participantEffectiveTimeFromStart.toProto())
        .setParticipantEffectiveGroupMeasure(
          ResearchElementDefinition.Characteristic.ParticipantEffectiveGroupMeasureCode.newBuilder()
            .setValue(
              GroupMeasureCode.Value.valueOf(
                participantEffectiveGroupMeasure.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .build()
    return protoValue
  }

  @JvmStatic
  private fun ResearchElementDefinition.Characteristic.toHapi():
    org.hl7.fhir.r4.model.ResearchElementDefinition.ResearchElementDefinitionCharacteristicComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ResearchElementDefinition
        .ResearchElementDefinitionCharacteristicComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDefinition(definition.researchElementDefinitionCharacteristicDefinitionToHapi())
    hapiValue.setUsageContext(usageContextList.map { it.toHapi() })
    hapiValue.setExcludeElement(exclude.toHapi())
    hapiValue.setUnitOfMeasure(unitOfMeasure.toHapi())
    hapiValue.setStudyEffectiveDescriptionElement(studyEffectiveDescription.toHapi())
    hapiValue.setStudyEffective(
      studyEffective.researchElementDefinitionCharacteristicStudyEffectiveToHapi()
    )
    hapiValue.setStudyEffectiveTimeFromStart(studyEffectiveTimeFromStart.toHapi())
    hapiValue.setStudyEffectiveGroupMeasure(
      org.hl7.fhir.r4.model.ResearchElementDefinition.GroupMeasure.valueOf(
        studyEffectiveGroupMeasure.value.name.replace("_", "")
      )
    )
    hapiValue.setParticipantEffectiveDescriptionElement(participantEffectiveDescription.toHapi())
    hapiValue.setParticipantEffective(
      participantEffective.researchElementDefinitionCharacteristicParticipantEffectiveToHapi()
    )
    hapiValue.setParticipantEffectiveTimeFromStart(participantEffectiveTimeFromStart.toHapi())
    hapiValue.setParticipantEffectiveGroupMeasure(
      org.hl7.fhir.r4.model.ResearchElementDefinition.GroupMeasure.valueOf(
        participantEffectiveGroupMeasure.value.name.replace("_", "")
      )
    )
    return hapiValue
  }
}
