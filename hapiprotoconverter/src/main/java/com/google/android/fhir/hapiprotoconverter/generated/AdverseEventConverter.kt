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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.AdverseEvent
import com.google.fhir.r4.core.AdverseEvent.SuspectEntity
import com.google.fhir.r4.core.AdverseEventActualityCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object AdverseEventConverter {
  @JvmStatic
  fun AdverseEvent.toHapi(): org.hl7.fhir.r4.model.AdverseEvent {
    val hapiValue = org.hl7.fhir.r4.model.AdverseEvent()
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
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    hapiValue.actuality =
      org.hl7.fhir.r4.model.AdverseEvent.AdverseEventActuality.valueOf(
        actuality.value.name.hapiCodeCheck().replace("_", "")
      )
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasEvent()) {
      hapiValue.event = event.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasDetected()) {
      hapiValue.detectedElement = detected.toHapi()
    }
    if (hasRecordedDate()) {
      hapiValue.recordedDateElement = recordedDate.toHapi()
    }
    if (resultingConditionCount > 0) {
      hapiValue.resultingCondition = resultingConditionList.map { it.toHapi() }
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (hasSeriousness()) {
      hapiValue.seriousness = seriousness.toHapi()
    }
    if (hasSeverity()) {
      hapiValue.severity = severity.toHapi()
    }
    if (hasOutcome()) {
      hapiValue.outcome = outcome.toHapi()
    }
    if (hasRecorder()) {
      hapiValue.recorder = recorder.toHapi()
    }
    if (contributorCount > 0) {
      hapiValue.contributor = contributorList.map { it.toHapi() }
    }
    if (suspectEntityCount > 0) {
      hapiValue.suspectEntity = suspectEntityList.map { it.toHapi() }
    }
    if (subjectMedicalHistoryCount > 0) {
      hapiValue.subjectMedicalHistory = subjectMedicalHistoryList.map { it.toHapi() }
    }
    if (referenceDocumentCount > 0) {
      hapiValue.referenceDocument = referenceDocumentList.map { it.toHapi() }
    }
    if (studyCount > 0) {
      hapiValue.study = studyList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.AdverseEvent.toProto(): AdverseEvent {
    val protoValue = AdverseEvent.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.identifier = identifier.toProto()
    }
    protoValue.actuality =
      AdverseEvent.ActualityCode.newBuilder()
        .setValue(
          AdverseEventActualityCode.Value.valueOf(
            actuality.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasEvent()) {
      protoValue.event = event.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasDetected()) {
      protoValue.detected = detectedElement.toProto()
    }
    if (hasRecordedDate()) {
      protoValue.recordedDate = recordedDateElement.toProto()
    }
    if (hasResultingCondition()) {
      protoValue.addAllResultingCondition(resultingCondition.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasSeriousness()) {
      protoValue.seriousness = seriousness.toProto()
    }
    if (hasSeverity()) {
      protoValue.severity = severity.toProto()
    }
    if (hasOutcome()) {
      protoValue.outcome = outcome.toProto()
    }
    if (hasRecorder()) {
      protoValue.recorder = recorder.toProto()
    }
    if (hasContributor()) {
      protoValue.addAllContributor(contributor.map { it.toProto() })
    }
    if (hasSuspectEntity()) {
      protoValue.addAllSuspectEntity(suspectEntity.map { it.toProto() })
    }
    if (hasSubjectMedicalHistory()) {
      protoValue.addAllSubjectMedicalHistory(subjectMedicalHistory.map { it.toProto() })
    }
    if (hasReferenceDocument()) {
      protoValue.addAllReferenceDocument(referenceDocument.map { it.toProto() })
    }
    if (hasStudy()) {
      protoValue.addAllStudy(study.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityComponent.toProto():
    AdverseEvent.SuspectEntity {
    val protoValue = AdverseEvent.SuspectEntity.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasInstance()) {
      protoValue.instance = instance.toProto()
    }
    if (hasCausality()) {
      protoValue.addAllCausality(causality.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityCausalityComponent.toProto():
    AdverseEvent.SuspectEntity.Causality {
    val protoValue =
      AdverseEvent.SuspectEntity.Causality.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAssessment()) {
      protoValue.assessment = assessment.toProto()
    }
    if (hasProductRelatedness()) {
      protoValue.productRelatedness = productRelatednessElement.toProto()
    }
    if (hasAuthor()) {
      protoValue.author = author.toProto()
    }
    if (hasMethod()) {
      protoValue.method = method.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun AdverseEvent.SuspectEntity.toHapi():
    org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasInstance()) {
      hapiValue.instance = instance.toHapi()
    }
    if (causalityCount > 0) {
      hapiValue.causality = causalityList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun AdverseEvent.SuspectEntity.Causality.toHapi():
    org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityCausalityComponent {
    val hapiValue = org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityCausalityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAssessment()) {
      hapiValue.assessment = assessment.toHapi()
    }
    if (hasProductRelatedness()) {
      hapiValue.productRelatednessElement = productRelatedness.toHapi()
    }
    if (hasAuthor()) {
      hapiValue.author = author.toHapi()
    }
    if (hasMethod()) {
      hapiValue.method = method.toHapi()
    }
    return hapiValue
  }
}
