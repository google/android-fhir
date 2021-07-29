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

public object ResearchStudyConverter {
  public fun ResearchStudy.toHapi(): org.hl7.fhir.r4.model.ResearchStudy {
    val hapiValue = org.hl7.fhir.r4.model.ResearchStudy()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setProtocol(protocolList.map { it.toHapi() })
    hapiValue.setPartOf(partOfList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setPrimaryPurposeType(primaryPurposeType.toHapi())
    hapiValue.setPhase(phase.toHapi())
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setFocus(focusList.map { it.toHapi() })
    hapiValue.setCondition(conditionList.map { it.toHapi() })
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setRelatedArtifact(relatedArtifactList.map { it.toHapi() })
    hapiValue.setKeyword(keywordList.map { it.toHapi() })
    hapiValue.setLocation(locationList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setEnrollment(enrollmentList.map { it.toHapi() })
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setSponsor(sponsor.toHapi())
    hapiValue.setPrincipalInvestigator(principalInvestigator.toHapi())
    hapiValue.setSite(siteList.map { it.toHapi() })
    hapiValue.setReasonStopped(reasonStopped.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setArm(armList.map { it.toHapi() })
    hapiValue.setObjective(objectiveList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ResearchStudy.toProto(): ResearchStudy {
    val protoValue =
      ResearchStudy.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setTitle(titleElement.toProto())
        .addAllProtocol(protocol.map { it.toProto() })
        .addAllPartOf(partOf.map { it.toProto() })
        .setStatus(
          ResearchStudy.StatusCode.newBuilder()
            .setValue(
              ResearchStudyStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setPrimaryPurposeType(primaryPurposeType.toProto())
        .setPhase(phase.toProto())
        .addAllCategory(category.map { it.toProto() })
        .addAllFocus(focus.map { it.toProto() })
        .addAllCondition(condition.map { it.toProto() })
        .addAllContact(contact.map { it.toProto() })
        .addAllRelatedArtifact(relatedArtifact.map { it.toProto() })
        .addAllKeyword(keyword.map { it.toProto() })
        .addAllLocation(location.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllEnrollment(enrollment.map { it.toProto() })
        .setPeriod(period.toProto())
        .setSponsor(sponsor.toProto())
        .setPrincipalInvestigator(principalInvestigator.toProto())
        .addAllSite(site.map { it.toProto() })
        .setReasonStopped(reasonStopped.toProto())
        .addAllNote(note.map { it.toProto() })
        .addAllArm(arm.map { it.toProto() })
        .addAllObjective(objective.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyArmComponent.toProto():
    ResearchStudy.Arm {
    val protoValue =
      ResearchStudy.Arm.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setType(type.toProto())
        .setDescription(descriptionElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyObjectiveComponent.toProto():
    ResearchStudy.Objective {
    val protoValue =
      ResearchStudy.Objective.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setType(type.toProto())
        .build()
    return protoValue
  }

  private fun ResearchStudy.Arm.toHapi():
    org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyArmComponent {
    val hapiValue = org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyArmComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }

  private fun ResearchStudy.Objective.toHapi():
    org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyObjectiveComponent {
    val hapiValue = org.hl7.fhir.r4.model.ResearchStudy.ResearchStudyObjectiveComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setType(type.toHapi())
    return hapiValue
  }
}
