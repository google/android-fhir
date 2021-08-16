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
import com.google.fhir.r4.core.EffectEvidenceSynthesis
import com.google.fhir.r4.core.EffectEvidenceSynthesis.Certainty
import com.google.fhir.r4.core.EffectEvidenceSynthesis.EffectEstimate
import com.google.fhir.r4.core.EffectEvidenceSynthesis.ResultsByExposure
import com.google.fhir.r4.core.ExposureStateCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object EffectEvidenceSynthesisConverter {
  @JvmStatic
  fun EffectEvidenceSynthesis.toHapi(): org.hl7.fhir.r4.model.EffectEvidenceSynthesis {
    val hapiValue = org.hl7.fhir.r4.model.EffectEvidenceSynthesis()
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
    if (hasExposureAlternative()) {
        hapiValue.exposureAlternative = exposureAlternative.toHapi()
    }
    if (hasOutcome()) {
        hapiValue.outcome = outcome.toHapi()
    }
    if (hasSampleSize()) {
        hapiValue.sampleSize = sampleSize.toHapi()
    }
    if (resultsByExposureCount > 0) {
        hapiValue.resultsByExposure = resultsByExposureList.map { it.toHapi() }
    }
    if (effectEstimateCount > 0) {
        hapiValue.effectEstimate = effectEstimateList.map { it.toHapi() }
    }
    if (certaintyCount > 0) {
        hapiValue.certainty = certaintyList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.EffectEvidenceSynthesis.toProto(): EffectEvidenceSynthesis {
    val protoValue = EffectEvidenceSynthesis.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = EffectEvidenceSynthesis.StatusCode.newBuilder()
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
    if (hasExposureAlternative()) {
        protoValue.exposureAlternative = exposureAlternative.toProto()
    }
    if (hasOutcome()) {
        protoValue.outcome = outcome.toProto()
    }
    if (hasSampleSize()) {
        protoValue.sampleSize = sampleSize.toProto()
    }
    if (hasResultsByExposure()) {
      protoValue.addAllResultsByExposure(resultsByExposure.map { it.toProto() })
    }
    if (hasEffectEstimate()) {
      protoValue.addAllEffectEstimate(effectEstimate.map { it.toProto() })
    }
    if (hasCertainty()) {
      protoValue.addAllCertainty(certainty.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisSampleSizeComponent.toProto():
    EffectEvidenceSynthesis.SampleSize {
    val protoValue =
      EffectEvidenceSynthesis.SampleSize.newBuilder().setId(String.newBuilder().setValue(id))
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
  private fun org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisResultsByExposureComponent.toProto():
    EffectEvidenceSynthesis.ResultsByExposure {
    val protoValue =
      EffectEvidenceSynthesis.ResultsByExposure.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
      protoValue.exposureState = EffectEvidenceSynthesis.ResultsByExposure.ExposureStateCode.newBuilder()
          .setValue(
              ExposureStateCode.Value.valueOf(
                  exposureState.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasVariantState()) {
        protoValue.variantState = variantState.toProto()
    }
    if (hasRiskEvidenceSynthesis()) {
        protoValue.riskEvidenceSynthesis = riskEvidenceSynthesis.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisEffectEstimateComponent.toProto():
    EffectEvidenceSynthesis.EffectEstimate {
    val protoValue =
      EffectEvidenceSynthesis.EffectEstimate.newBuilder().setId(String.newBuilder().setValue(id))
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
    if (hasVariantState()) {
        protoValue.variantState = variantState.toProto()
    }
    if (hasValue()) {
        protoValue.value = valueElement.toProto()
    }
    if (hasUnitOfMeasure()) {
        protoValue.unitOfMeasure = unitOfMeasure.toProto()
    }
    if (hasPrecisionEstimate()) {
      protoValue.addAllPrecisionEstimate(precisionEstimate.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisEffectEstimatePrecisionEstimateComponent.toProto():
    EffectEvidenceSynthesis.EffectEstimate.PrecisionEstimate {
    val protoValue =
      EffectEvidenceSynthesis.EffectEstimate.PrecisionEstimate.newBuilder()
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
  private fun org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisCertaintyComponent.toProto():
    EffectEvidenceSynthesis.Certainty {
    val protoValue =
      EffectEvidenceSynthesis.Certainty.newBuilder().setId(String.newBuilder().setValue(id))
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
  private fun org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisCertaintyCertaintySubcomponentComponent.toProto():
    EffectEvidenceSynthesis.Certainty.CertaintySubcomponent {
    val protoValue =
      EffectEvidenceSynthesis.Certainty.CertaintySubcomponent.newBuilder()
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
  private fun EffectEvidenceSynthesis.SampleSize.toHapi():
    org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisSampleSizeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisSampleSizeComponent()
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
  private fun EffectEvidenceSynthesis.ResultsByExposure.toHapi():
    org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisResultsByExposureComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.EffectEvidenceSynthesis
        .EffectEvidenceSynthesisResultsByExposureComponent()
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
      hapiValue.exposureState = org.hl7.fhir.r4.model.EffectEvidenceSynthesis.ExposureState.valueOf(
          exposureState.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasVariantState()) {
        hapiValue.variantState = variantState.toHapi()
    }
    if (hasRiskEvidenceSynthesis()) {
        hapiValue.riskEvidenceSynthesis = riskEvidenceSynthesis.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun EffectEvidenceSynthesis.EffectEstimate.toHapi():
    org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisEffectEstimateComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisEffectEstimateComponent()
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
    if (hasVariantState()) {
        hapiValue.variantState = variantState.toHapi()
    }
    if (hasValue()) {
        hapiValue.valueElement = value.toHapi()
    }
    if (hasUnitOfMeasure()) {
        hapiValue.unitOfMeasure = unitOfMeasure.toHapi()
    }
    if (precisionEstimateCount > 0) {
        hapiValue.precisionEstimate = precisionEstimateList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun EffectEvidenceSynthesis.EffectEstimate.PrecisionEstimate.toHapi():
    org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisEffectEstimatePrecisionEstimateComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.EffectEvidenceSynthesis
        .EffectEvidenceSynthesisEffectEstimatePrecisionEstimateComponent()
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
  private fun EffectEvidenceSynthesis.Certainty.toHapi():
    org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisCertaintyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisCertaintyComponent()
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
  private fun EffectEvidenceSynthesis.Certainty.CertaintySubcomponent.toHapi():
    org.hl7.fhir.r4.model.EffectEvidenceSynthesis.EffectEvidenceSynthesisCertaintyCertaintySubcomponentComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.EffectEvidenceSynthesis
        .EffectEvidenceSynthesisCertaintyCertaintySubcomponentComponent()
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
