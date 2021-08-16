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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.RiskEvidenceSynthesis
import com.google.fhir.r4.core.RiskEvidenceSynthesis.Certainty
import com.google.fhir.r4.core.RiskEvidenceSynthesis.RiskEstimate
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object RiskEvidenceSynthesisConverter {
  @JvmStatic
  fun RiskEvidenceSynthesis.toHapi(): org.hl7.fhir.r4.model.RiskEvidenceSynthesis {
    val hapiValue = org.hl7.fhir.r4.model.RiskEvidenceSynthesis()
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
      hapiValue.status =
          Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
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
    if (hasSynthesisType()) {
        hapiValue.synthesisType = synthesisType.toHapi()
    }
    if (hasStudyType()) {
        hapiValue.studyType = studyType.toHapi()
    }
    if (hasPopulation()) {
        hapiValue.population = population.toHapi()
    }
    if (hasExposure()) {
        hapiValue.exposure = exposure.toHapi()
    }
    if (hasOutcome()) {
        hapiValue.outcome = outcome.toHapi()
    }
    if (hasSampleSize()) {
        hapiValue.sampleSize = sampleSize.toHapi()
    }
    if (hasRiskEstimate()) {
        hapiValue.riskEstimate = riskEstimate.toHapi()
    }
    if (certaintyCount > 0) {
        hapiValue.certainty = certaintyList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.toProto(): RiskEvidenceSynthesis {
    val protoValue = RiskEvidenceSynthesis.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = RiskEvidenceSynthesis.StatusCode.newBuilder()
          .setValue(
              PublicationStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
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
    if (hasSynthesisType()) {
        protoValue.synthesisType = synthesisType.toProto()
    }
    if (hasStudyType()) {
        protoValue.studyType = studyType.toProto()
    }
    if (hasPopulation()) {
        protoValue.population = population.toProto()
    }
    if (hasExposure()) {
        protoValue.exposure = exposure.toProto()
    }
    if (hasOutcome()) {
        protoValue.outcome = outcome.toProto()
    }
    if (hasSampleSize()) {
        protoValue.sampleSize = sampleSize.toProto()
    }
    if (hasRiskEstimate()) {
        protoValue.riskEstimate = riskEstimate.toProto()
    }
    if (hasCertainty()) {
      protoValue.addAllCertainty(certainty.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisSampleSizeComponent.toProto():
    RiskEvidenceSynthesis.SampleSize {
    val protoValue =
      RiskEvidenceSynthesis.SampleSize.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasNumberOfStudies()) {
        protoValue.numberOfStudies = numberOfStudiesElement.toProto()
    }
    if (hasNumberOfParticipants()) {
        protoValue.numberOfParticipants = numberOfParticipantsElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimateComponent.toProto():
    RiskEvidenceSynthesis.RiskEstimate {
    val protoValue =
      RiskEvidenceSynthesis.RiskEstimate.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasValue()) {
        protoValue.value = valueElement.toProto()
    }
    if (hasUnitOfMeasure()) {
        protoValue.unitOfMeasure = unitOfMeasure.toProto()
    }
    if (hasDenominatorCount()) {
        protoValue.denominatorCount = denominatorCountElement.toProto()
    }
    if (hasNumeratorCount()) {
        protoValue.numeratorCount = numeratorCountElement.toProto()
    }
    if (hasPrecisionEstimate()) {
      protoValue.addAllPrecisionEstimate(precisionEstimate.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimatePrecisionEstimateComponent.toProto():
    RiskEvidenceSynthesis.RiskEstimate.PrecisionEstimate {
    val protoValue =
      RiskEvidenceSynthesis.RiskEstimate.PrecisionEstimate.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasLevel()) {
        protoValue.level = levelElement.toProto()
    }
    if (hasFrom()) {
        protoValue.from = fromElement.toProto()
    }
    if (hasTo()) {
        protoValue.to = toElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyComponent.toProto():
    RiskEvidenceSynthesis.Certainty {
    val protoValue =
      RiskEvidenceSynthesis.Certainty.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRating()) {
      protoValue.addAllRating(rating.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasCertaintySubcomponent()) {
      protoValue.addAllCertaintySubcomponent(certaintySubcomponent.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyCertaintySubcomponentComponent.toProto():
    RiskEvidenceSynthesis.Certainty.CertaintySubcomponent {
    val protoValue =
      RiskEvidenceSynthesis.Certainty.CertaintySubcomponent.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasRating()) {
      protoValue.addAllRating(rating.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.SampleSize.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisSampleSizeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisSampleSizeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (hasNumberOfStudies()) {
        hapiValue.numberOfStudiesElement = numberOfStudies.toHapi()
    }
    if (hasNumberOfParticipants()) {
        hapiValue.numberOfParticipantsElement = numberOfParticipants.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.RiskEstimate.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimateComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimateComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasValue()) {
        hapiValue.valueElement = value.toHapi()
    }
    if (hasUnitOfMeasure()) {
        hapiValue.unitOfMeasure = unitOfMeasure.toHapi()
    }
    if (hasDenominatorCount()) {
        hapiValue.denominatorCountElement = denominatorCount.toHapi()
    }
    if (hasNumeratorCount()) {
        hapiValue.numeratorCountElement = numeratorCount.toHapi()
    }
    if (precisionEstimateCount > 0) {
        hapiValue.precisionEstimate = precisionEstimateList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.RiskEstimate.PrecisionEstimate.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimatePrecisionEstimateComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis
        .RiskEvidenceSynthesisRiskEstimatePrecisionEstimateComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasLevel()) {
        hapiValue.levelElement = level.toHapi()
    }
    if (hasFrom()) {
        hapiValue.fromElement = from.toHapi()
    }
    if (hasTo()) {
        hapiValue.toElement = to.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.Certainty.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (ratingCount > 0) {
        hapiValue.rating = ratingList.map { it.toHapi() }
    }
    if (noteCount > 0) {
        hapiValue.note = noteList.map { it.toHapi() }
    }
    if (certaintySubcomponentCount > 0) {
        hapiValue.certaintySubcomponent = certaintySubcomponentList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.Certainty.CertaintySubcomponent.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyCertaintySubcomponentComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis
        .RiskEvidenceSynthesisCertaintyCertaintySubcomponentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (ratingCount > 0) {
        hapiValue.rating = ratingList.map { it.toHapi() }
    }
    if (noteCount > 0) {
        hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }
}
