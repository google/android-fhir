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

object MeasureConverter {
  @JvmStatic
  private fun Measure.SubjectX.measureSubjectToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Measure.subject[x]")
  }

  @JvmStatic
  private fun Type.measureSubjectToProto(): Measure.SubjectX {
    val protoValue = Measure.SubjectX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Measure.toHapi(): org.hl7.fhir.r4.model.Measure {
    val hapiValue = org.hl7.fhir.r4.model.Measure()
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
    if (hasSubtitle()) {
      hapiValue.subtitleElement = subtitle.toHapi()
    }
    hapiValue.status =
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.measureSubjectToHapi()
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
    if (hasDisclaimer()) {
      hapiValue.disclaimerElement = disclaimer.toHapi()
    }
    if (hasScoring()) {
      hapiValue.scoring = scoring.toHapi()
    }
    if (hasCompositeScoring()) {
      hapiValue.compositeScoring = compositeScoring.toHapi()
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasRiskAdjustment()) {
      hapiValue.riskAdjustmentElement = riskAdjustment.toHapi()
    }
    if (hasRateAggregation()) {
      hapiValue.rateAggregationElement = rateAggregation.toHapi()
    }
    if (hasRationale()) {
      hapiValue.rationaleElement = rationale.toHapi()
    }
    if (hasClinicalRecommendationStatement()) {
      hapiValue.clinicalRecommendationStatementElement = clinicalRecommendationStatement.toHapi()
    }
    if (hasImprovementNotation()) {
      hapiValue.improvementNotation = improvementNotation.toHapi()
    }
    if (definitionCount > 0) {
      hapiValue.definition = definitionList.map { it.toHapi() }
    }
    if (hasGuidance()) {
      hapiValue.guidanceElement = guidance.toHapi()
    }
    if (groupCount > 0) {
      hapiValue.group = groupList.map { it.toHapi() }
    }
    if (supplementalDataCount > 0) {
      hapiValue.supplementalData = supplementalDataList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Measure.toProto(): Measure {
    val protoValue = Measure.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      Measure.StatusCode.newBuilder()
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
      protoValue.subject = subject.measureSubjectToProto()
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
    if (hasDisclaimer()) {
      protoValue.disclaimer = disclaimerElement.toProto()
    }
    if (hasScoring()) {
      protoValue.scoring = scoring.toProto()
    }
    if (hasCompositeScoring()) {
      protoValue.compositeScoring = compositeScoring.toProto()
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasRiskAdjustment()) {
      protoValue.riskAdjustment = riskAdjustmentElement.toProto()
    }
    if (hasRateAggregation()) {
      protoValue.rateAggregation = rateAggregationElement.toProto()
    }
    if (hasRationale()) {
      protoValue.rationale = rationaleElement.toProto()
    }
    if (hasClinicalRecommendationStatement()) {
      protoValue.clinicalRecommendationStatement = clinicalRecommendationStatementElement.toProto()
    }
    if (hasImprovementNotation()) {
      protoValue.improvementNotation = improvementNotation.toProto()
    }
    if (hasDefinition()) {
      protoValue.addAllDefinition(definition.map { it.toProto() })
    }
    if (hasGuidance()) {
      protoValue.guidance = guidanceElement.toProto()
    }
    if (hasGroup()) {
      protoValue.addAllGroup(group.map { it.toProto() })
    }
    if (hasSupplementalData()) {
      protoValue.addAllSupplementalData(supplementalData.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupComponent.toProto(): Measure.Group {
    val protoValue = Measure.Group.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasPopulation()) {
      protoValue.addAllPopulation(population.map { it.toProto() })
    }
    if (hasStratifier()) {
      protoValue.addAllStratifier(stratifier.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupPopulationComponent.toProto():
    Measure.Group.Population {
    val protoValue = Measure.Group.Population.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasCriteria()) {
      protoValue.criteria = criteria.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponent.toProto():
    Measure.Group.Stratifier {
    val protoValue = Measure.Group.Stratifier.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasCriteria()) {
      protoValue.criteria = criteria.toProto()
    }
    if (hasComponent()) {
      protoValue.addAllComponent(component.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponentComponent.toProto():
    Measure.Group.Stratifier.Component {
    val protoValue =
      Measure.Group.Stratifier.Component.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasCriteria()) {
      protoValue.criteria = criteria.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Measure.MeasureSupplementalDataComponent.toProto():
    Measure.SupplementalData {
    val protoValue = Measure.SupplementalData.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasUsage()) {
      protoValue.addAllUsage(usage.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasCriteria()) {
      protoValue.criteria = criteria.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Measure.Group.toHapi(): org.hl7.fhir.r4.model.Measure.MeasureGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (populationCount > 0) {
      hapiValue.population = populationList.map { it.toHapi() }
    }
    if (stratifierCount > 0) {
      hapiValue.stratifier = stratifierList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Measure.Group.Population.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureGroupPopulationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupPopulationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasCriteria()) {
      hapiValue.criteria = criteria.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Measure.Group.Stratifier.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasCriteria()) {
      hapiValue.criteria = criteria.toHapi()
    }
    if (componentCount > 0) {
      hapiValue.component = componentList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Measure.Group.Stratifier.Component.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureGroupStratifierComponentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasCriteria()) {
      hapiValue.criteria = criteria.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Measure.SupplementalData.toHapi():
    org.hl7.fhir.r4.model.Measure.MeasureSupplementalDataComponent {
    val hapiValue = org.hl7.fhir.r4.model.Measure.MeasureSupplementalDataComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (usageCount > 0) {
      hapiValue.usage = usageList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasCriteria()) {
      hapiValue.criteria = criteria.toHapi()
    }
    return hapiValue
  }
}
