package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.EncounterLocationStatusCode
import com.google.fhir.r4.core.EncounterStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String

public object EncounterConverter {
  public fun Encounter.toHapi(): org.hl7.fhir.r4.model.Encounter {
    val hapiValue = org.hl7.fhir.r4.model.Encounter()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Encounter.EncounterStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setStatusHistory(statusHistoryList.map{it.toHapi()})
    hapiValue.setClass(class.toHapi())
    hapiValue.setClassHistory(classHistoryList.map{it.toHapi()})
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setServiceType(serviceType.toHapi())
    hapiValue.setPriority(priority.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEpisodeOfCare(episodeOfCareList.map{it.toHapi()})
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setParticipant(participantList.map{it.toHapi()})
    hapiValue.setAppointment(appointmentList.map{it.toHapi()})
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setLength(length.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setDiagnosis(diagnosisList.map{it.toHapi()})
    hapiValue.setAccount(accountList.map{it.toHapi()})
    hapiValue.setHospitalization(hospitalization.toHapi())
    hapiValue.setLocation(locationList.map{it.toHapi()})
    hapiValue.setServiceProvider(serviceProvider.toHapi())
    hapiValue.setPartOf(partOf.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Encounter.toProto(): Encounter {
    val protoValue = Encounter.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(Encounter.StatusCode.newBuilder().setValue(EncounterStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllStatusHistory(statusHistory.map{it.toProto()})
    .setClass(class.toProto())
    .addAllClassHistory(classHistory.map{it.toProto()})
    .addAllType(type.map{it.toProto()})
    .setServiceType(serviceType.toProto())
    .setPriority(priority.toProto())
    .setSubject(subject.toProto())
    .addAllEpisodeOfCare(episodeOfCare.map{it.toProto()})
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllParticipant(participant.map{it.toProto()})
    .addAllAppointment(appointment.map{it.toProto()})
    .setPeriod(period.toProto())
    .setLength(length.toProto())
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .addAllDiagnosis(diagnosis.map{it.toProto()})
    .addAllAccount(account.map{it.toProto()})
    .setHospitalization(hospitalization.toProto())
    .addAllLocation(location.map{it.toProto()})
    .setServiceProvider(serviceProvider.toProto())
    .setPartOf(partOf.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Encounter.StatusHistoryComponent.toProto():
      Encounter.StatusHistory {
    val protoValue = Encounter.StatusHistory.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setStatus(Encounter.StatusHistory.StatusCode.newBuilder().setValue(EncounterStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Encounter.ClassHistoryComponent.toProto():
      Encounter.ClassHistory {
    val protoValue = Encounter.ClassHistory.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setClass(class.toProto())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent.toProto():
      Encounter.Participant {
    val protoValue = Encounter.Participant.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllType(type.map{it.toProto()})
    .setPeriod(period.toProto())
    .setIndividual(individual.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Encounter.DiagnosisComponent.toProto(): Encounter.Diagnosis {
    val protoValue = Encounter.Diagnosis.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCondition(condition.toProto())
    .setUse(use.toProto())
    .setRank(rankElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent.toProto():
      Encounter.Hospitalization {
    val protoValue = Encounter.Hospitalization.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setPreAdmissionIdentifier(preAdmissionIdentifier.toProto())
    .setOrigin(origin.toProto())
    .setAdmitSource(admitSource.toProto())
    .setReAdmission(reAdmission.toProto())
    .addAllDietPreference(dietPreference.map{it.toProto()})
    .addAllSpecialCourtesy(specialCourtesy.map{it.toProto()})
    .addAllSpecialArrangement(specialArrangement.map{it.toProto()})
    .setDestination(destination.toProto())
    .setDischargeDisposition(dischargeDisposition.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent.toProto():
      Encounter.Location {
    val protoValue = Encounter.Location.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setLocation(location.toProto())
    .setStatus(Encounter.Location.StatusCode.newBuilder().setValue(EncounterLocationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPhysicalType(physicalType.toProto())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun Encounter.StatusHistory.toHapi():
      org.hl7.fhir.r4.model.Encounter.StatusHistoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.StatusHistoryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Encounter.EncounterStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  public fun Encounter.ClassHistory.toHapi():
      org.hl7.fhir.r4.model.Encounter.ClassHistoryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.ClassHistoryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setClass(class.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  public fun Encounter.Participant.toHapi():
      org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setIndividual(individual.toHapi())
    return hapiValue
  }

  public fun Encounter.Diagnosis.toHapi(): org.hl7.fhir.r4.model.Encounter.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.DiagnosisComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCondition(condition.toHapi())
    hapiValue.setUse(use.toHapi())
    hapiValue.setRankElement(rank.toHapi())
    return hapiValue
  }

  public fun Encounter.Hospitalization.toHapi():
      org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setPreAdmissionIdentifier(preAdmissionIdentifier.toHapi())
    hapiValue.setOrigin(origin.toHapi())
    hapiValue.setAdmitSource(admitSource.toHapi())
    hapiValue.setReAdmission(reAdmission.toHapi())
    hapiValue.setDietPreference(dietPreferenceList.map{it.toHapi()})
    hapiValue.setSpecialCourtesy(specialCourtesyList.map{it.toHapi()})
    hapiValue.setSpecialArrangement(specialArrangementList.map{it.toHapi()})
    hapiValue.setDestination(destination.toHapi())
    hapiValue.setDischargeDisposition(dischargeDisposition.toHapi())
    return hapiValue
  }

  public fun Encounter.Location.toHapi():
      org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setLocation(location.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.Encounter.EncounterLocationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setPhysicalType(physicalType.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }
}
