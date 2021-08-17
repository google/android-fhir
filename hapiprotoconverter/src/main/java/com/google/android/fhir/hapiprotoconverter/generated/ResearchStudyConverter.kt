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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ResearchStudy
import com.google.fhir.r4.core.ResearchStudyStatusCode
import com.google.fhir.r4.core.String

object ResearchStudyConverter {
  fun ResearchStudy.toHapi(): org.hl7.fhir.r4.model.ResearchStudy {
    val hapiValue = org.hl7.fhir.r4.model.ResearchStudy()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (protocolCount > 0) {
      hapiValue.protocol = protocolList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasPrimaryPurposeType()) {
      hapiValue.primaryPurposeType = primaryPurposeType.toHapi()
    }
    if (hasPhase()) {
      hapiValue.phase = phase.toHapi()
    }
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (focusCount > 0) {
      hapiValue.focus = focusList.map { it.toHapi() }
    }
    if (conditionCount > 0) {
      hapiValue.condition = conditionList.map { it.toHapi() }
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (relatedArtifactCount > 0) {
      hapiValue.relatedArtifact = relatedArtifactList.map { it.toHapi() }
    }
    if (keywordCount > 0) {
      hapiValue.keyword = keywordList.map { it.toHapi() }
    }
    if (locationCount > 0) {
      hapiValue.location = locationList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (enrollmentCount > 0) {
      hapiValue.enrollment = enrollmentList.map { it.toHapi() }
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (hasSponsor()) {
      hapiValue.sponsor = sponsor.toHapi()
    }
    if (hasPrincipalInvestigator()) {
      hapiValue.principalInvestigator = principalInvestigator.toHapi()
    }
    if (siteCount > 0) {
      hapiValue.site = siteList.map { it.toHapi() }
    }
    if (hasReasonStopped()) {
      hapiValue.reasonStopped = reasonStopped.toHapi()
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (armCount > 0) {
      hapiValue.arm = armList.map { it.toHapi() }
    }
    if (objectiveCount > 0) {
      hapiValue.objective = objectiveList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ResearchStudy.toProto(): ResearchStudy {
    val protoValue = ResearchStudy.newBuilder()
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
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasProtocol()) {
      protoValue.addAllProtocol(protocol.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        ResearchStudy.StatusCode.newBuilder()
          .setValue(
            ResearchStudyStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasPrimaryPurposeType()) {
      protoValue.primaryPurposeType = primaryPurposeType.toProto()
    }
    if (hasPhase()) {
      protoValue.phase = phase.toProto()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasFocus()) {
      protoValue.addAllFocus(focus.map { it.toProto() })
    }
    if (hasCondition()) {
      protoValue.addAllCondition(condition.map { it.toProto() })
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasRelatedArtifact()) {
      protoValue.addAllRelatedArtifact(relatedArtifact.map { it.toProto() })
    }
    if (hasKeyword()) {
      protoValue.addAllKeyword(keyword.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.addAllLocation(location.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasEnrollment()) {
      protoValue.addAllEnrollment(enrollment.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasSponsor()) {
      protoValue.sponsor = sponsor.toProto()
    }
    if (hasPrincipalInvestigator()) {
      protoValue.principalInvestigator = principalInvestigator.toProto()
    }
    if (hasSite()) {
      protoValue.addAllSite(site.map { it.toProto() })
    }
    if (hasReasonStopped()) {
      protoValue.reasonStopped = reasonStopped.toProto()
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasArm()) {
      protoValue.addAllArm(arm.map { it.toProto() })
    }
    if (hasObjective()) {
      protoValue.addAllObjective(objective.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyArmComponent.toProto():
    ResearchStudy.Arm {
    val protoValue = ResearchStudy.Arm.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyObjectiveComponent.toProto():
    ResearchStudy.Objective {
    val protoValue = ResearchStudy.Objective.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    return protoValue.build()
  }

  private fun ResearchStudy.Arm.toHapi():
    org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyArmComponent {
    val hapiValue = org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyArmComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }

  private fun ResearchStudy.Objective.toHapi():
    org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyObjectiveComponent {
    val hapiValue = org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyObjectiveComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    return hapiValue
  }
}
