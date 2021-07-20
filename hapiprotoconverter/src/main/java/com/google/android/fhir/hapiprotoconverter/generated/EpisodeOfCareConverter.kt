package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.EpisodeOfCareStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String

public object EpisodeOfCareConverter {
  public fun EpisodeOfCare.toHapi(): org.hl7.fhir.r4.model.EpisodeOfCare {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setStatusHistory(statusHistoryList.map{it.toHapi()})
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setDiagnosis(diagnosisList.map{it.toHapi()})
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setManagingOrganization(managingOrganization.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setReferralRequest(referralRequestList.map{it.toHapi()})
    hapiValue.setCareManager(careManager.toHapi())
    hapiValue.setTeam(teamList.map{it.toHapi()})
    hapiValue.setAccount(accountList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.EpisodeOfCare.toProto(): EpisodeOfCare {
    val protoValue = EpisodeOfCare.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(EpisodeOfCare.StatusCode.newBuilder().setValue(EpisodeOfCareStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllStatusHistory(statusHistory.map{it.toProto()})
    .addAllType(type.map{it.toProto()})
    .addAllDiagnosis(diagnosis.map{it.toProto()})
    .setPatient(patient.toProto())
    .setManagingOrganization(managingOrganization.toProto())
    .setPeriod(period.toProto())
    .addAllReferralRequest(referralRequest.map{it.toProto()})
    .setCareManager(careManager.toProto())
    .addAllTeam(team.map{it.toProto()})
    .addAllAccount(account.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent.toProto():
      EpisodeOfCare.StatusHistory {
    val protoValue = EpisodeOfCare.StatusHistory.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setStatus(EpisodeOfCare.StatusHistory.StatusCode.newBuilder().setValue(EpisodeOfCareStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent.toProto():
      EpisodeOfCare.Diagnosis {
    val protoValue = EpisodeOfCare.Diagnosis.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCondition(condition.toProto())
    .setRole(role.toProto())
    .setRank(rankElement.toProto())
    .build()
    return protoValue
  }

  public fun EpisodeOfCare.StatusHistory.toHapi():
      org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatusHistoryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.EpisodeOfCare.EpisodeOfCareStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  public fun EpisodeOfCare.Diagnosis.toHapi():
      org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.EpisodeOfCare.DiagnosisComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCondition(condition.toHapi())
    hapiValue.setRole(role.toHapi())
    hapiValue.setRankElement(rank.toHapi())
    return hapiValue
  }
}
