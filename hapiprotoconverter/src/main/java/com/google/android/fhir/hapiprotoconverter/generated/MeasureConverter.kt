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
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Measure
import com.google.fhir.r4.core.Measure.Group
import com.google.fhir.r4.core.Measure.Group.Stratifier
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type

public object MeasureConverter {
  @JvmStatic
  private fun Measure.SubjectX.measureSubjectToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Measure.subject[x]")
  }

  @JvmStatic
  private fun Type.measureSubjectToProto(): Measure.SubjectX {
    val protoValue = Measure.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun Measure.toHapi(): org.hl7.fhir.r4.model.Measure {
    val hapiValue = org.hl7.fhir.r4.model.Measure()
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
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setSubject(subject.measureSubjectToHapi())
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
    hapiValue.setDisclaimerElement(disclaimer.toHapi())
    hapiValue.setScoring(scoring.toHapi())
    hapiValue.setCompositeScoring(compositeScoring.toHapi())
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setRiskAdjustmentElement(riskAdjustment.toHapi())
    hapiValue.setRateAggregationElement(rateAggregation.toHapi())
    hapiValue.setRationaleElement(rationale.toHapi())
    hapiValue.setClinicalRecommendationStatementElement(clinicalRecommendationStatement.toHapi())
    hapiValue.setImprovementNotation(improvementNotation.toHapi())
    hapiValue.setDefinition(definitionList.map { it.toHapi() })
    hapiValue.setGuidanceElement(guidance.toHapi())
    hapiValue.setGroup(groupList.map { it.toHapi() })
    hapiValue.setSupplementalData(supplementalDataList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Measure.toProto(): Measure {
    val protoValue =
      Measure.newBuilder()
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
          Measure.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setSubject(subject.measureSubjectToProto())
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
        .setDisclaimer(disclaimerElement.toProto())
        .setScoring(scoring.toProto())
        .setCompositeScoring(compositeScoring.toProto())
        .addAllType(type.map { it.toProto() })
        .setRiskAdjustment(riskAdjustmentElement.toProto())
        .setRateAggregation(rateAggregationElement.toProto())
        .setRationale(rationaleElement.toProto())
        .setClinicalRecommendationStatement(clinicalRecommendationStatementElement.toProto())
        .setImprovementNotation(improvementNotation.toProto())
        .addAllDefinition(definition.map { it.toProto() })
        .setGuidance(guidanceElement.toProto())
        .addAllGroup(group.map { it.toProto() })
        .addAllSupplementalData(supplementalData.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupComponent.toProto(): Measure.Group {
    val protoValue =
      Measure.Group.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setDescription(descriptionElement.toProto())
        .addAllPopulation(population.map { it.toProto() })
        .addAllStratifier(stratifier.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupPopulationComponent.toProto():
    Measure.Group.Population {
    val protoValue =
      Measure.Group.Population.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setDescription(descriptionElement.toProto())
        .setCriteria(criteria.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponent.toProto():
    Measure.Group.Stratifier {
    val protoValue =
      Measure.Group.Stratifier.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setDescription(descriptionElement.toProto())
        .setCriteria(criteria.toProto())
        .addAllComponent(component.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponentComponent.toProto():
    Measure.Group.Stratifier.Component {
    val protoValue =
      Measure.Group.Stratifier.Component.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setDescription(descriptionElement.toProto())
        .setCriteria(criteria.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureSupplementalDataComponent.toProto():
    Measure.SupplementalData {
    val protoValue =
      Measure.SupplementalData.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .addAllUsage(usage.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setCriteria(criteria.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Measure.Group.toHapi(): org.hl7.fhir.r4.model.Measure.MeasureGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setPopulation(populationList.map { it.toHapi() })
    hapiValue.setStratifier(stratifierList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Measure.Group.Population.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureGroupPopulationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupPopulationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setCriteria(criteria.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Measure.Group.Stratifier.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setCriteria(criteria.toHapi())
    hapiValue.setComponent(componentList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Measure.Group.Stratifier.Component.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setCriteria(criteria.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Measure.SupplementalData.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureSupplementalDataComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureSupplementalDataComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setUsage(usageList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setCriteria(criteria.toHapi())
    return hapiValue
  }
}
