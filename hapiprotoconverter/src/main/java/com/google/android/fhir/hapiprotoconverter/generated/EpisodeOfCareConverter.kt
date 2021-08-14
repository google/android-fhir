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

public object EpisodeOfCareConverter {
  @JvmStatic
  public fun EpisodeOfCare.toHapi(): org.hl7.fhir.r4.model.EpisodeOfCare {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus.valueOf(
        status
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setStatusHistory(statusHistoryList.map { it.toHapi() })
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setDiagnosis(diagnosisList.map { it.toHapi() })
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setManagingOrganization(managingOrganization.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setReferralRequest(referralRequestList.map { it.toHapi() })
    hapiValue.setCareManager(careManager.toHapi())
    hapiValue.setTeam(teamList.map { it.toHapi() })
    hapiValue.setAccount(accountList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.EpisodeOfCare.toProto(): EpisodeOfCare {
    val protoValue =
      EpisodeOfCare.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          EpisodeOfCare.StatusCode.newBuilder()
            .setValue(
              EpisodeOfCareStatusCode.Value.valueOf(
                status
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .addAllStatusHistory(statusHistory.map { it.toProto() })
        .addAllType(type.map { it.toProto() })
        .addAllDiagnosis(diagnosis.map { it.toProto() })
        .setPatient(patient.toProto())
        .setManagingOrganization(managingOrganization.toProto())
        .setPeriod(period.toProto())
        .addAllReferralRequest(referralRequest.map { it.toProto() })
        .setCareManager(careManager.toProto())
        .addAllTeam(team.map { it.toProto() })
        .addAllAccount(account.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent.toProto():
    EpisodeOfCare.StatusHistory {
    val protoValue =
      EpisodeOfCare.StatusHistory.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setStatus(
          EpisodeOfCare.StatusHistory.StatusCode.newBuilder()
            .setValue(
              EpisodeOfCareStatusCode.Value.valueOf(
                status
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setPeriod(period.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent.toProto():
    EpisodeOfCare.Diagnosis {
    val protoValue =
      EpisodeOfCare.Diagnosis.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCondition(condition.toProto())
        .setRole(role.toProto())
        .setRank(rankElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun EpisodeOfCare.StatusHistory.toHapi():
    org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus.valueOf(
        status
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun EpisodeOfCare.Diagnosis.toHapi():
    org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCondition(condition.toHapi())
    hapiValue.setRole(role.toHapi())
    hapiValue.setRankElement(rank.toHapi())
    return hapiValue
  }
}
