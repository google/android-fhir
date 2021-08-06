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

public object RiskEvidenceSynthesisConverter {
  @JvmStatic
  public fun RiskEvidenceSynthesis.toHapi(): org.hl7.fhir.r4.model.RiskEvidenceSynthesis {
    val hapiValue = org.hl7.fhir.r4.model.RiskEvidenceSynthesis()
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
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
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
    hapiValue.setSynthesisType(synthesisType.toHapi())
    hapiValue.setStudyType(studyType.toHapi())
    hapiValue.setPopulation(population.toHapi())
    hapiValue.setExposure(exposure.toHapi())
    hapiValue.setOutcome(outcome.toHapi())
    hapiValue.setSampleSize(sampleSize.toHapi())
    hapiValue.setRiskEstimate(riskEstimate.toHapi())
    hapiValue.setCertainty(certaintyList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.toProto(): RiskEvidenceSynthesis {
    val protoValue =
      RiskEvidenceSynthesis.newBuilder()
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
        .setStatus(
          RiskEvidenceSynthesis.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllNote(note.map { it.toProto() })
        .addAllUseContext(useContext.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
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
        .setSynthesisType(synthesisType.toProto())
        .setStudyType(studyType.toProto())
        .setPopulation(population.toProto())
        .setExposure(exposure.toProto())
        .setOutcome(outcome.toProto())
        .setSampleSize(sampleSize.toProto())
        .setRiskEstimate(riskEstimate.toProto())
        .addAllCertainty(certainty.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisSampleSizeComponent.toProto():
    RiskEvidenceSynthesis.SampleSize {
    val protoValue =
      RiskEvidenceSynthesis.SampleSize.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setNumberOfStudies(numberOfStudiesElement.toProto())
        .setNumberOfParticipants(numberOfParticipantsElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimateComponent.toProto():
    RiskEvidenceSynthesis.RiskEstimate {
    val protoValue =
      RiskEvidenceSynthesis.RiskEstimate.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setType(type.toProto())
        .setValue(valueElement.toProto())
        .setUnitOfMeasure(unitOfMeasure.toProto())
        .setDenominatorCount(denominatorCountElement.toProto())
        .setNumeratorCount(numeratorCountElement.toProto())
        .addAllPrecisionEstimate(precisionEstimate.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimatePrecisionEstimateComponent.toProto():
    RiskEvidenceSynthesis.RiskEstimate.PrecisionEstimate {
    val protoValue =
      RiskEvidenceSynthesis.RiskEstimate.PrecisionEstimate.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setLevel(levelElement.toProto())
        .setFrom(fromElement.toProto())
        .setTo(toElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyComponent.toProto():
    RiskEvidenceSynthesis.Certainty {
    val protoValue =
      RiskEvidenceSynthesis.Certainty.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllRating(rating.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .addAllCertaintySubcomponent(certaintySubcomponent.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyCertaintySubcomponentComponent.toProto():
    RiskEvidenceSynthesis.Certainty.CertaintySubcomponent {
    val protoValue =
      RiskEvidenceSynthesis.Certainty.CertaintySubcomponent.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .addAllRating(rating.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.SampleSize.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisSampleSizeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisSampleSizeComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setNumberOfStudiesElement(numberOfStudies.toHapi())
    hapiValue.setNumberOfParticipantsElement(numberOfParticipants.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.RiskEstimate.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimateComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimateComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setUnitOfMeasure(unitOfMeasure.toHapi())
    hapiValue.setDenominatorCountElement(denominatorCount.toHapi())
    hapiValue.setNumeratorCountElement(numeratorCount.toHapi())
    hapiValue.setPrecisionEstimate(precisionEstimateList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.RiskEstimate.PrecisionEstimate.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisRiskEstimatePrecisionEstimateComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis
        .RiskEvidenceSynthesisRiskEstimatePrecisionEstimateComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setLevelElement(level.toHapi())
    hapiValue.setFromElement(from.toHapi())
    hapiValue.setToElement(to.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.Certainty.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setRating(ratingList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setCertaintySubcomponent(certaintySubcomponentList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun RiskEvidenceSynthesis.Certainty.CertaintySubcomponent.toHapi():
    org.hl7.fhir.r4.model.RiskEvidenceSynthesis.RiskEvidenceSynthesisCertaintyCertaintySubcomponentComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.RiskEvidenceSynthesis
        .RiskEvidenceSynthesisCertaintyCertaintySubcomponentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setRating(ratingList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }
}
