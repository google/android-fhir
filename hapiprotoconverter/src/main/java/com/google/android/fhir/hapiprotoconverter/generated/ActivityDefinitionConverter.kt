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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.RequestIntentCode
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.RequestResourceTypeCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Age
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Duration
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Timing
import org.hl7.fhir.r4.model.Type

object ActivityDefinitionConverter {
  private fun ActivityDefinition.SubjectX.activityDefinitionSubjectToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ActivityDefinition.subject[x]")
  }

  private fun Type.activityDefinitionSubjectToProto(): ActivityDefinition.SubjectX {
    val protoValue = ActivityDefinition.SubjectX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun ActivityDefinition.TimingX.activityDefinitionTimingToHapi(): Type {
    if (hasTiming()) {
      return (this.timing).toHapi()
    }
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasAge()) {
      return (this.age).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    if (hasDuration()) {
      return (this.duration).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ActivityDefinition.timing[x]")
  }

  private fun Type.activityDefinitionTimingToProto(): ActivityDefinition.TimingX {
    val protoValue = ActivityDefinition.TimingX.newBuilder()
    if (this is Timing) {
      protoValue.timing = this.toProto()
    }
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is Age) {
      protoValue.age = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    if (this is Duration) {
      protoValue.duration = this.toProto()
    }
    return protoValue.build()
  }

  private fun ActivityDefinition.ProductX.activityDefinitionProductToHapi(): Type {
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ActivityDefinition.product[x]")
  }

  private fun Type.activityDefinitionProductToProto(): ActivityDefinition.ProductX {
    val protoValue = ActivityDefinition.ProductX.newBuilder()
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  fun ActivityDefinition.toHapi(): org.hl7.fhir.r4.model.ActivityDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ActivityDefinition()
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
    if (hasStatus()) {
      hapiValue.status =
        Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.activityDefinitionSubjectToHapi()
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
    if (hasKind()) {
      hapiValue.kind =
        org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionKind.valueOf(
          kind.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasProfile()) {
      hapiValue.profileElement = profile.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasIntent()) {
      hapiValue.intent =
        org.hl7.fhir.r4.model.ActivityDefinition.RequestIntent.valueOf(
          intent.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasPriority()) {
      hapiValue.priority =
        org.hl7.fhir.r4.model.ActivityDefinition.RequestPriority.valueOf(
          priority.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasDoNotPerform()) {
      hapiValue.doNotPerformElement = doNotPerform.toHapi()
    }
    if (hasTiming()) {
      hapiValue.timing = timing.activityDefinitionTimingToHapi()
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (participantCount > 0) {
      hapiValue.participant = participantList.map { it.toHapi() }
    }
    if (hasProduct()) {
      hapiValue.product = product.activityDefinitionProductToHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (dosageCount > 0) {
      hapiValue.dosage = dosageList.map { it.toHapi() }
    }
    if (bodySiteCount > 0) {
      hapiValue.bodySite = bodySiteList.map { it.toHapi() }
    }
    if (specimenRequirementCount > 0) {
      hapiValue.specimenRequirement = specimenRequirementList.map { it.toHapi() }
    }
    if (observationRequirementCount > 0) {
      hapiValue.observationRequirement = observationRequirementList.map { it.toHapi() }
    }
    if (observationResultRequirementCount > 0) {
      hapiValue.observationResultRequirement = observationResultRequirementList.map { it.toHapi() }
    }
    if (hasTransform()) {
      hapiValue.transformElement = transform.toHapi()
    }
    if (dynamicValueCount > 0) {
      hapiValue.dynamicValue = dynamicValueList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ActivityDefinition.toProto(): ActivityDefinition {
    val protoValue = ActivityDefinition.newBuilder()
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
    if (hasStatus()) {
      protoValue.status =
        ActivityDefinition.StatusCode.newBuilder()
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
      protoValue.subject = subject.activityDefinitionSubjectToProto()
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
    if (hasKind()) {
      protoValue.kind =
        ActivityDefinition.KindCode.newBuilder()
          .setValue(
            RequestResourceTypeCode.Value.valueOf(
              kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasProfile()) {
      protoValue.profile = profileElement.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasIntent()) {
      protoValue.intent =
        ActivityDefinition.IntentCode.newBuilder()
          .setValue(
            RequestIntentCode.Value.valueOf(
              intent.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasPriority()) {
      protoValue.priority =
        ActivityDefinition.PriorityCode.newBuilder()
          .setValue(
            RequestPriorityCode.Value.valueOf(
              priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasDoNotPerform()) {
      protoValue.doNotPerform = doNotPerformElement.toProto()
    }
    if (hasTiming()) {
      protoValue.timing = timing.activityDefinitionTimingToProto()
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasParticipant()) {
      protoValue.addAllParticipant(participant.map { it.toProto() })
    }
    if (hasProduct()) {
      protoValue.product = product.activityDefinitionProductToProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasDosage()) {
      protoValue.addAllDosage(dosage.map { it.toProto() })
    }
    if (hasBodySite()) {
      protoValue.addAllBodySite(bodySite.map { it.toProto() })
    }
    if (hasSpecimenRequirement()) {
      protoValue.addAllSpecimenRequirement(specimenRequirement.map { it.toProto() })
    }
    if (hasObservationRequirement()) {
      protoValue.addAllObservationRequirement(observationRequirement.map { it.toProto() })
    }
    if (hasObservationResultRequirement()) {
      protoValue.addAllObservationResultRequirement(
        observationResultRequirement.map { it.toProto() }
      )
    }
    if (hasTransform()) {
      protoValue.transform = transformElement.toProto()
    }
    if (hasDynamicValue()) {
      protoValue.addAllDynamicValue(dynamicValue.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionParticipantComponent.toProto():
    ActivityDefinition.Participant {
    val protoValue = ActivityDefinition.Participant.newBuilder()
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
        ActivityDefinition.Participant.TypeCode.newBuilder()
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

  private fun org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionDynamicValueComponent.toProto():
    ActivityDefinition.DynamicValue {
    val protoValue = ActivityDefinition.DynamicValue.newBuilder()
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

  private fun ActivityDefinition.Participant.toHapi():
    org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionParticipantComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionParticipantComponent()
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
        org.hl7.fhir.r4.model.ActivityDefinition.ActivityParticipantType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasRole()) {
      hapiValue.role = role.toHapi()
    }
    return hapiValue
  }

  private fun ActivityDefinition.DynamicValue.toHapi():
    org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionDynamicValueComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ActivityDefinition.ActivityDefinitionDynamicValueComponent()
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
