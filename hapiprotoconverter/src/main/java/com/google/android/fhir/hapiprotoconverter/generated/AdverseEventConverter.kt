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

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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

public object AdverseEventConverter {
  @JvmStatic
  public fun AdverseEvent.toHapi(): org.hl7.fhir.r4.model.AdverseEvent {
    val hapiValue = org.hl7.fhir.r4.model.AdverseEvent()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setActuality(
      org.hl7.fhir.r4.model.AdverseEvent.AdverseEventActuality.valueOf(
        actuality.value.name.replace("_", "")
      )
    )
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setEvent(event.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setDetectedElement(detected.toHapi())
    hapiValue.setRecordedDateElement(recordedDate.toHapi())
    hapiValue.setResultingCondition(resultingConditionList.map { it.toHapi() })
    hapiValue.setLocation(location.toHapi())
    hapiValue.setSeriousness(seriousness.toHapi())
    hapiValue.setSeverity(severity.toHapi())
    hapiValue.setOutcome(outcome.toHapi())
    hapiValue.setRecorder(recorder.toHapi())
    hapiValue.setContributor(contributorList.map { it.toHapi() })
    hapiValue.setSuspectEntity(suspectEntityList.map { it.toHapi() })
    hapiValue.setSubjectMedicalHistory(subjectMedicalHistoryList.map { it.toHapi() })
    hapiValue.setReferenceDocument(referenceDocumentList.map { it.toHapi() })
    hapiValue.setStudy(studyList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.AdverseEvent.toProto(): AdverseEvent {
    val protoValue =
      AdverseEvent.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentifier(identifier.toProto())
        .setActuality(
          AdverseEvent.ActualityCode.newBuilder()
            .setValue(
              AdverseEventActualityCode.Value.valueOf(
                actuality.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllCategory(category.map { it.toProto() })
        .setEvent(event.toProto())
        .setSubject(subject.toProto())
        .setEncounter(encounter.toProto())
        .setDate(dateElement.toProto())
        .setDetected(detectedElement.toProto())
        .setRecordedDate(recordedDateElement.toProto())
        .addAllResultingCondition(resultingCondition.map { it.toProto() })
        .setLocation(location.toProto())
        .setSeriousness(seriousness.toProto())
        .setSeverity(severity.toProto())
        .setOutcome(outcome.toProto())
        .setRecorder(recorder.toProto())
        .addAllContributor(contributor.map { it.toProto() })
        .addAllSuspectEntity(suspectEntity.map { it.toProto() })
        .addAllSubjectMedicalHistory(subjectMedicalHistory.map { it.toProto() })
        .addAllReferenceDocument(referenceDocument.map { it.toProto() })
        .addAllStudy(study.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityComponent.toProto():
    AdverseEvent.SuspectEntity {
    val protoValue =
      AdverseEvent.SuspectEntity.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setInstance(instance.toProto())
        .addAllCausality(causality.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityCausalityComponent.toProto():
    AdverseEvent.SuspectEntity.Causality {
    val protoValue =
      AdverseEvent.SuspectEntity.Causality.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setAssessment(assessment.toProto())
        .setProductRelatedness(productRelatednessElement.toProto())
        .setAuthor(author.toProto())
        .setMethod(method.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun AdverseEvent.SuspectEntity.toHapi():
    org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setInstance(instance.toHapi())
    hapiValue.setCausality(causalityList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun AdverseEvent.SuspectEntity.Causality.toHapi():
    org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityCausalityComponent {
    val hapiValue = org.hl7.fhir.r4.model.AdverseEvent.AdverseEventSuspectEntityCausalityComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAssessment(assessment.toHapi())
    hapiValue.setProductRelatednessElement(productRelatedness.toHapi())
    hapiValue.setAuthor(author.toHapi())
    hapiValue.setMethod(method.toHapi())
    return hapiValue
  }
}
