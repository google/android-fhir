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
import com.google.fhir.r4.core.EpisodeOfCare
import com.google.fhir.r4.core.EpisodeOfCare.StatusHistory
import com.google.fhir.r4.core.EpisodeOfCareStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object EpisodeOfCareConverter {
  @JvmStatic
  fun EpisodeOfCare.toHapi(): org.hl7.fhir.r4.model.EpisodeOfCare {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (statusHistoryCount > 0) {
      hapiValue.statusHistory = statusHistoryList.map { it.toHapi() }
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (diagnosisCount > 0) {
      hapiValue.diagnosis = diagnosisList.map { it.toHapi() }
    }
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasManagingOrganization()) {
      hapiValue.managingOrganization = managingOrganization.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (referralRequestCount > 0) {
      hapiValue.referralRequest = referralRequestList.map { it.toHapi() }
    }
    if (hasCareManager()) {
      hapiValue.careManager = careManager.toHapi()
    }
    if (teamCount > 0) {
      hapiValue.team = teamList.map { it.toHapi() }
    }
    if (accountCount > 0) {
      hapiValue.account = accountList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.EpisodeOfCare.toProto(): EpisodeOfCare {
    val protoValue = EpisodeOfCare.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      EpisodeOfCare.StatusCode.newBuilder()
        .setValue(
          EpisodeOfCareStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasStatusHistory()) {
      protoValue.addAllStatusHistory(statusHistory.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasDiagnosis()) {
      protoValue.addAllDiagnosis(diagnosis.map { it.toProto() })
    }
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasManagingOrganization()) {
      protoValue.managingOrganization = managingOrganization.toProto()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasReferralRequest()) {
      protoValue.addAllReferralRequest(referralRequest.map { it.toProto() })
    }
    if (hasCareManager()) {
      protoValue.careManager = careManager.toProto()
    }
    if (hasTeam()) {
      protoValue.addAllTeam(team.map { it.toProto() })
    }
    if (hasAccount()) {
      protoValue.addAllAccount(account.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent.toProto():
    EpisodeOfCare.StatusHistory {
    val protoValue =
      EpisodeOfCare.StatusHistory.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.status =
      EpisodeOfCare.StatusHistory.StatusCode.newBuilder()
        .setValue(
          EpisodeOfCareStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent.toProto():
    EpisodeOfCare.Diagnosis {
    val protoValue = EpisodeOfCare.Diagnosis.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCondition()) {
      protoValue.condition = condition.toProto()
    }
    if (hasRole()) {
      protoValue.role = role.toProto()
    }
    if (hasRank()) {
      protoValue.rank = rankElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun EpisodeOfCare.StatusHistory.toHapi():
    org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun EpisodeOfCare.Diagnosis.toHapi():
    org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCondition()) {
      hapiValue.condition = condition.toHapi()
    }
    if (hasRole()) {
      hapiValue.role = role.toHapi()
    }
    if (hasRank()) {
      hapiValue.rankElement = rank.toHapi()
    }
    return hapiValue
  }
}
