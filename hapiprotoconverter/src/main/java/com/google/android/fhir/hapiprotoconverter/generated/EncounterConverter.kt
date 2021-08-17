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
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Encounter
import com.google.fhir.r4.core.Encounter.Location
import com.google.fhir.r4.core.Encounter.StatusHistory
import com.google.fhir.r4.core.EncounterLocationStatusCode
import com.google.fhir.r4.core.EncounterStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String

object EncounterConverter {
  fun Encounter.toHapi(): org.hl7.fhir.r4.model.Encounter {
    val hapiValue = org.hl7.fhir.r4.model.Encounter()
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
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.Encounter.EncounterStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (statusHistoryCount > 0) {
      hapiValue.statusHistory = statusHistoryList.map { it.toHapi() }
    }
    if (hasClassValue()) {
      hapiValue.class_ = classValue.toHapi()
    }
    if (classHistoryCount > 0) {
      hapiValue.classHistory = classHistoryList.map { it.toHapi() }
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasServiceType()) {
      hapiValue.serviceType = serviceType.toHapi()
    }
    if (hasPriority()) {
      hapiValue.priority = priority.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (episodeOfCareCount > 0) {
      hapiValue.episodeOfCare = episodeOfCareList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (participantCount > 0) {
      hapiValue.participant = participantList.map { it.toHapi() }
    }
    if (appointmentCount > 0) {
      hapiValue.appointment = appointmentList.map { it.toHapi() }
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (hasLength()) {
      hapiValue.length = length.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (diagnosisCount > 0) {
      hapiValue.diagnosis = diagnosisList.map { it.toHapi() }
    }
    if (accountCount > 0) {
      hapiValue.account = accountList.map { it.toHapi() }
    }
    if (hasHospitalization()) {
      hapiValue.hospitalization = hospitalization.toHapi()
    }
    if (locationCount > 0) {
      hapiValue.location = locationList.map { it.toHapi() }
    }
    if (hasServiceProvider()) {
      hapiValue.serviceProvider = serviceProvider.toHapi()
    }
    if (hasPartOf()) {
      hapiValue.partOf = partOf.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Encounter.toProto(): Encounter {
    val protoValue = Encounter.newBuilder()
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
    if (hasStatus()) {
      protoValue.status =
        Encounter.StatusCode.newBuilder()
          .setValue(
            EncounterStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasStatusHistory()) {
      protoValue.addAllStatusHistory(statusHistory.map { it.toProto() })
    }
    if (hasClass_()) {
      protoValue.classValue = class_.toProto()
    }
    if (hasClassHistory()) {
      protoValue.addAllClassHistory(classHistory.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasServiceType()) {
      protoValue.serviceType = serviceType.toProto()
    }
    if (hasPriority()) {
      protoValue.priority = priority.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEpisodeOfCare()) {
      protoValue.addAllEpisodeOfCare(episodeOfCare.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasParticipant()) {
      protoValue.addAllParticipant(participant.map { it.toProto() })
    }
    if (hasAppointment()) {
      protoValue.addAllAppointment(appointment.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasLength()) {
      protoValue.length = length.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasDiagnosis()) {
      protoValue.addAllDiagnosis(diagnosis.map { it.toProto() })
    }
    if (hasAccount()) {
      protoValue.addAllAccount(account.map { it.toProto() })
    }
    if (hasHospitalization()) {
      protoValue.hospitalization = hospitalization.toProto()
    }
    if (hasLocation()) {
      protoValue.addAllLocation(location.map { it.toProto() })
    }
    if (hasServiceProvider()) {
      protoValue.serviceProvider = serviceProvider.toProto()
    }
    if (hasPartOf()) {
      protoValue.partOf = partOf.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Encounter.StatusHistoryComponent.toProto():
    Encounter.StatusHistory {
    val protoValue = Encounter.StatusHistory.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        Encounter.StatusHistory.StatusCode.newBuilder()
          .setValue(
            EncounterStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Encounter.ClassHistoryComponent.toProto():
    Encounter.ClassHistory {
    val protoValue = Encounter.ClassHistory.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasClass_()) {
      protoValue.classValue = class_.toProto()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent.toProto():
    Encounter.Participant {
    val protoValue = Encounter.Participant.newBuilder()
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
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasIndividual()) {
      protoValue.individual = individual.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Encounter.DiagnosisComponent.toProto(): Encounter.Diagnosis {
    val protoValue = Encounter.Diagnosis.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCondition()) {
      protoValue.condition = condition.toProto()
    }
    if (hasUse()) {
      protoValue.use = use.toProto()
    }
    if (hasRank()) {
      protoValue.rank = rankElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent.toProto():
    Encounter.Hospitalization {
    val protoValue = Encounter.Hospitalization.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPreAdmissionIdentifier()) {
      protoValue.preAdmissionIdentifier = preAdmissionIdentifier.toProto()
    }
    if (hasOrigin()) {
      protoValue.origin = origin.toProto()
    }
    if (hasAdmitSource()) {
      protoValue.admitSource = admitSource.toProto()
    }
    if (hasReAdmission()) {
      protoValue.reAdmission = reAdmission.toProto()
    }
    if (hasDietPreference()) {
      protoValue.addAllDietPreference(dietPreference.map { it.toProto() })
    }
    if (hasSpecialCourtesy()) {
      protoValue.addAllSpecialCourtesy(specialCourtesy.map { it.toProto() })
    }
    if (hasSpecialArrangement()) {
      protoValue.addAllSpecialArrangement(specialArrangement.map { it.toProto() })
    }
    if (hasDestination()) {
      protoValue.destination = destination.toProto()
    }
    if (hasDischargeDisposition()) {
      protoValue.dischargeDisposition = dischargeDisposition.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent.toProto():
    Encounter.Location {
    val protoValue = Encounter.Location.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        Encounter.Location.StatusCode.newBuilder()
          .setValue(
            EncounterLocationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasPhysicalType()) {
      protoValue.physicalType = physicalType.toProto()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  private fun Encounter.StatusHistory.toHapi():
    org.hl7.fhir.r4.model.Encounter.StatusHistoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.StatusHistoryComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.Encounter.EncounterStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    return hapiValue
  }

  private fun Encounter.ClassHistory.toHapi():
    org.hl7.fhir.r4.model.Encounter.ClassHistoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.ClassHistoryComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasClassValue()) {
      hapiValue.class_ = classValue.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    return hapiValue
  }

  private fun Encounter.Participant.toHapi():
    org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (hasIndividual()) {
      hapiValue.individual = individual.toHapi()
    }
    return hapiValue
  }

  private fun Encounter.Diagnosis.toHapi(): org.hl7.fhir.r4.model.Encounter.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.DiagnosisComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCondition()) {
      hapiValue.condition = condition.toHapi()
    }
    if (hasUse()) {
      hapiValue.use = use.toHapi()
    }
    if (hasRank()) {
      hapiValue.rankElement = rank.toHapi()
    }
    return hapiValue
  }

  private fun Encounter.Hospitalization.toHapi():
    org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPreAdmissionIdentifier()) {
      hapiValue.preAdmissionIdentifier = preAdmissionIdentifier.toHapi()
    }
    if (hasOrigin()) {
      hapiValue.origin = origin.toHapi()
    }
    if (hasAdmitSource()) {
      hapiValue.admitSource = admitSource.toHapi()
    }
    if (hasReAdmission()) {
      hapiValue.reAdmission = reAdmission.toHapi()
    }
    if (dietPreferenceCount > 0) {
      hapiValue.dietPreference = dietPreferenceList.map { it.toHapi() }
    }
    if (specialCourtesyCount > 0) {
      hapiValue.specialCourtesy = specialCourtesyList.map { it.toHapi() }
    }
    if (specialArrangementCount > 0) {
      hapiValue.specialArrangement = specialArrangementList.map { it.toHapi() }
    }
    if (hasDestination()) {
      hapiValue.destination = destination.toHapi()
    }
    if (hasDischargeDisposition()) {
      hapiValue.dischargeDisposition = dischargeDisposition.toHapi()
    }
    return hapiValue
  }

  private fun Encounter.Location.toHapi():
    org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.Encounter.EncounterLocationStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasPhysicalType()) {
      hapiValue.physicalType = physicalType.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    return hapiValue
  }
}
