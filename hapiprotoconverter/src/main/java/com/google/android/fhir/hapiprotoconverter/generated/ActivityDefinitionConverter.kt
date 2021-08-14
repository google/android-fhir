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
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RelatedArtifactConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RelatedArtifactConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.ActionParticipantTypeCode
import com.google.fhir.r4.core.ActivityDefinition
import com.google.fhir.r4.core.ActivityDefinition.Participant
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Duration
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestIntentCode
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.RequestResourceTypeCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

public object ActivityDefinitionConverter {
  @JvmStatic
  private fun ActivityDefinition.SubjectX.activityDefinitionSubjectToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ActivityDefinition.subject[x]")
  }

  @JvmStatic
  private fun Type.activityDefinitionSubjectToProto(): ActivityDefinition.SubjectX {
    val protoValue = ActivityDefinition.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ActivityDefinition.TimingX.activityDefinitionTimingToHapi(): Type {
    if (this.getTiming() != Timing.newBuilder().defaultInstanceForType) {
      return (this.getTiming()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getDuration() != Duration.newBuilder().defaultInstanceForType) {
      return (this.getDuration()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ActivityDefinition.timing[x]")
  }

  @JvmStatic
  private fun Type.activityDefinitionTimingToProto(): ActivityDefinition.TimingX {
    val protoValue = ActivityDefinition.TimingX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Timing) {
      protoValue.setTiming(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Duration) {
      protoValue.setDuration(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ActivityDefinition.ProductX.activityDefinitionProductToHapi(): Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ActivityDefinition.product[x]")
  }

  @JvmStatic
  private fun Type.activityDefinitionProductToProto(): ActivityDefinition.ProductX {
    val protoValue = ActivityDefinition.ProductX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ActivityDefinition.toHapi(): org.hl7.fhir.r4.model.ActivityDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ActivityDefinition()
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
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    )
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setSubject(subject.activityDefinitionSubjectToHapi())
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
    hapiValue.setKind(
      org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setProfileElement(profile.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setIntent(
      org.hl7.fhir.r4.model.ActivityDefinition.RequestIntent.valueOf(
        intent.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setPriority(
      org.hl7.fhir.r4.model.ActivityDefinition.RequestPriority.valueOf(
        priority.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setDoNotPerformElement(doNotPerform.toHapi())
    hapiValue.setTiming(timing.activityDefinitionTimingToHapi())
    hapiValue.setLocation(location.toHapi())
    hapiValue.setParticipant(participantList.map { it.toHapi() })
    hapiValue.setProduct(product.activityDefinitionProductToHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setDosage(dosageList.map { it.toHapi() })
    hapiValue.setBodySite(bodySiteList.map { it.toHapi() })
    hapiValue.setSpecimenRequirement(specimenRequirementList.map { it.toHapi() })
    hapiValue.setObservationRequirement(observationRequirementList.map { it.toHapi() })
    hapiValue.setObservationResultRequirement(observationResultRequirementList.map { it.toHapi() })
    hapiValue.setTransformElement(transform.toHapi())
    hapiValue.setDynamicValue(dynamicValueList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ActivityDefinition.toProto(): ActivityDefinition {
    val protoValue =
      ActivityDefinition.newBuilder()
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
        .setStatus(
          ActivityDefinition.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setSubject(subject.activityDefinitionSubjectToProto())
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
        .setKind(
          ActivityDefinition.KindCode.newBuilder()
            .setValue(
              RequestResourceTypeCode.Value.valueOf(
                kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setProfile(profileElement.toProto())
        .setCode(code.toProto())
        .setIntent(
          ActivityDefinition.IntentCode.newBuilder()
            .setValue(
              RequestIntentCode.Value.valueOf(
                intent.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setPriority(
          ActivityDefinition.PriorityCode.newBuilder()
            .setValue(
              RequestPriorityCode.Value.valueOf(
                priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setDoNotPerform(doNotPerformElement.toProto())
        .setTiming(timing.activityDefinitionTimingToProto())
        .setLocation(location.toProto())
        .addAllParticipant(participant.map { it.toProto() })
        .setProduct(product.activityDefinitionProductToProto())
        .setQuantity((quantity as SimpleQuantity).toProto())
        .addAllDosage(dosage.map { it.toProto() })
        .addAllBodySite(bodySite.map { it.toProto() })
        .addAllSpecimenRequirement(specimenRequirement.map { it.toProto() })
        .addAllObservationRequirement(observationRequirement.map { it.toProto() })
        .addAllObservationResultRequirement(observationResultRequirement.map { it.toProto() })
        .setTransform(transformElement.toProto())
        .addAllDynamicValue(dynamicValue.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionParticipantComponent.toProto():
    ActivityDefinition.Participant {
    val protoValue =
      ActivityDefinition.Participant.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(
          ActivityDefinition.Participant.TypeCode.newBuilder()
            .setValue(
              ActionParticipantTypeCode.Value.valueOf(
                type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setRole(role.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionDynamicValueComponent.toProto():
    ActivityDefinition.DynamicValue {
    val protoValue =
      ActivityDefinition.DynamicValue.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPath(pathElement.toProto())
        .setExpression(expression.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun ActivityDefinition.Participant.toHapi():
    org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionParticipantComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionParticipantComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(
      org.hl7.fhir.r4.model.ActivityDefinition.ActivityParticipantType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setRole(role.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ActivityDefinition.DynamicValue.toHapi():
    org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionDynamicValueComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionDynamicValueComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPathElement(path.toHapi())
    hapiValue.setExpression(expression.toHapi())
    return hapiValue
  }
}
