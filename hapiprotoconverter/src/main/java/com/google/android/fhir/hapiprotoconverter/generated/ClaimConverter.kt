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

import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Address
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Claim
import com.google.fhir.r4.core.Claim.Accident
import com.google.fhir.r4.core.Claim.Diagnosis
import com.google.fhir.r4.core.Claim.Item
import com.google.fhir.r4.core.Claim.Item.Detail
import com.google.fhir.r4.core.Claim.Procedure
import com.google.fhir.r4.core.Claim.SupportingInformation
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.UseCode
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object ClaimConverter {
  private fun Claim.SupportingInformation.TimingX.claimSupportingInfoTimingToHapi(): Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.supportingInfo.timing[x]")
  }

  private fun Type.claimSupportingInfoTimingToProto(): Claim.SupportingInformation.TimingX {
    val protoValue = Claim.SupportingInformation.TimingX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  private fun Claim.SupportingInformation.ValueX.claimSupportingInfoValueToHapi(): Type {
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.attachment != Attachment.newBuilder().defaultInstanceForType) {
      return (this.attachment).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.supportingInfo.value[x]")
  }

  private fun Type.claimSupportingInfoValueToProto(): Claim.SupportingInformation.ValueX {
    val protoValue = Claim.SupportingInformation.ValueX.newBuilder()
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.attachment = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun Claim.Diagnosis.DiagnosisX.claimDiagnosisDiagnosisToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.diagnosis.diagnosis[x]")
  }

  private fun Type.claimDiagnosisDiagnosisToProto(): Claim.Diagnosis.DiagnosisX {
    val protoValue = Claim.Diagnosis.DiagnosisX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun Claim.Procedure.ProcedureX.claimProcedureProcedureToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.procedure.procedure[x]")
  }

  private fun Type.claimProcedureProcedureToProto(): Claim.Procedure.ProcedureX {
    val protoValue = Claim.Procedure.ProcedureX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun Claim.Accident.LocationX.claimAccidentLocationToHapi(): Type {
    if (this.address != Address.newBuilder().defaultInstanceForType) {
      return (this.address).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.accident.location[x]")
  }

  private fun Type.claimAccidentLocationToProto(): Claim.Accident.LocationX {
    val protoValue = Claim.Accident.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.address = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun Claim.Item.ServicedX.claimItemServicedToHapi(): Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.item.serviced[x]")
  }

  private fun Type.claimItemServicedToProto(): Claim.Item.ServicedX {
    val protoValue = Claim.Item.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  private fun Claim.Item.LocationX.claimItemLocationToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.address != Address.newBuilder().defaultInstanceForType) {
      return (this.address).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.item.location[x]")
  }

  private fun Type.claimItemLocationToProto(): Claim.Item.LocationX {
    val protoValue = Claim.Item.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.address = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  fun Claim.toHapi(): org.hl7.fhir.r4.model.Claim {
    val hapiValue = org.hl7.fhir.r4.model.Claim()
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
      org.hl7.fhir.r4.model.Claim.ClaimStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasSubType()) {
      hapiValue.subType = subType.toHapi()
    }
    hapiValue.use =
      org.hl7.fhir.r4.model.Claim.Use.valueOf(use.value.name.hapiCodeCheck().replace("_", ""))
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasBillablePeriod()) {
      hapiValue.billablePeriod = billablePeriod.toHapi()
    }
    if (hasCreated()) {
      hapiValue.createdElement = created.toHapi()
    }
    if (hasEnterer()) {
      hapiValue.enterer = enterer.toHapi()
    }
    if (hasInsurer()) {
      hapiValue.insurer = insurer.toHapi()
    }
    if (hasProvider()) {
      hapiValue.provider = provider.toHapi()
    }
    if (hasPriority()) {
      hapiValue.priority = priority.toHapi()
    }
    if (hasFundsReserve()) {
      hapiValue.fundsReserve = fundsReserve.toHapi()
    }
    if (relatedCount > 0) {
      hapiValue.related = relatedList.map { it.toHapi() }
    }
    if (hasPrescription()) {
      hapiValue.prescription = prescription.toHapi()
    }
    if (hasOriginalPrescription()) {
      hapiValue.originalPrescription = originalPrescription.toHapi()
    }
    if (hasPayee()) {
      hapiValue.payee = payee.toHapi()
    }
    if (hasReferral()) {
      hapiValue.referral = referral.toHapi()
    }
    if (hasFacility()) {
      hapiValue.facility = facility.toHapi()
    }
    if (careTeamCount > 0) {
      hapiValue.careTeam = careTeamList.map { it.toHapi() }
    }
    if (supportingInfoCount > 0) {
      hapiValue.supportingInfo = supportingInfoList.map { it.toHapi() }
    }
    if (diagnosisCount > 0) {
      hapiValue.diagnosis = diagnosisList.map { it.toHapi() }
    }
    if (procedureCount > 0) {
      hapiValue.procedure = procedureList.map { it.toHapi() }
    }
    if (insuranceCount > 0) {
      hapiValue.insurance = insuranceList.map { it.toHapi() }
    }
    if (hasAccident()) {
      hapiValue.accident = accident.toHapi()
    }
    if (itemCount > 0) {
      hapiValue.item = itemList.map { it.toHapi() }
    }
    if (hasTotal()) {
      hapiValue.total = total.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Claim.toProto(): Claim {
    val protoValue = Claim.newBuilder().setId(Id.newBuilder().setValue(id))
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
      Claim.StatusCode.newBuilder()
        .setValue(
          FinancialResourceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasSubType()) {
      protoValue.subType = subType.toProto()
    }
    protoValue.use =
      Claim.UseCode.newBuilder()
        .setValue(
          UseCode.Value.valueOf(use.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasBillablePeriod()) {
      protoValue.billablePeriod = billablePeriod.toProto()
    }
    if (hasCreated()) {
      protoValue.created = createdElement.toProto()
    }
    if (hasEnterer()) {
      protoValue.enterer = enterer.toProto()
    }
    if (hasInsurer()) {
      protoValue.insurer = insurer.toProto()
    }
    if (hasProvider()) {
      protoValue.provider = provider.toProto()
    }
    if (hasPriority()) {
      protoValue.priority = priority.toProto()
    }
    if (hasFundsReserve()) {
      protoValue.fundsReserve = fundsReserve.toProto()
    }
    if (hasRelated()) {
      protoValue.addAllRelated(related.map { it.toProto() })
    }
    if (hasPrescription()) {
      protoValue.prescription = prescription.toProto()
    }
    if (hasOriginalPrescription()) {
      protoValue.originalPrescription = originalPrescription.toProto()
    }
    if (hasPayee()) {
      protoValue.payee = payee.toProto()
    }
    if (hasReferral()) {
      protoValue.referral = referral.toProto()
    }
    if (hasFacility()) {
      protoValue.facility = facility.toProto()
    }
    if (hasCareTeam()) {
      protoValue.addAllCareTeam(careTeam.map { it.toProto() })
    }
    if (hasSupportingInfo()) {
      protoValue.addAllSupportingInfo(supportingInfo.map { it.toProto() })
    }
    if (hasDiagnosis()) {
      protoValue.addAllDiagnosis(diagnosis.map { it.toProto() })
    }
    if (hasProcedure()) {
      protoValue.addAllProcedure(procedure.map { it.toProto() })
    }
    if (hasInsurance()) {
      protoValue.addAllInsurance(insurance.map { it.toProto() })
    }
    if (hasAccident()) {
      protoValue.accident = accident.toProto()
    }
    if (hasItem()) {
      protoValue.addAllItem(item.map { it.toProto() })
    }
    if (hasTotal()) {
      protoValue.total = total.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.RelatedClaimComponent.toProto(): Claim.RelatedClaim {
    val protoValue = Claim.RelatedClaim.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRelationship()) {
      protoValue.relationship = relationship.toProto()
    }
    if (hasReference()) {
      protoValue.reference = reference.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.PayeeComponent.toProto(): Claim.Payee {
    val protoValue = Claim.Payee.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasParty()) {
      protoValue.party = party.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.CareTeamComponent.toProto(): Claim.CareTeam {
    val protoValue = Claim.CareTeam.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasProvider()) {
      protoValue.provider = provider.toProto()
    }
    if (hasResponsible()) {
      protoValue.responsible = responsibleElement.toProto()
    }
    if (hasRole()) {
      protoValue.role = role.toProto()
    }
    if (hasQualification()) {
      protoValue.qualification = qualification.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.SupportingInformationComponent.toProto():
    Claim.SupportingInformation {
    val protoValue =
      Claim.SupportingInformation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasTiming()) {
      protoValue.timing = timing.claimSupportingInfoTimingToProto()
    }
    if (hasValue()) {
      protoValue.value = value.claimSupportingInfoValueToProto()
    }
    if (hasReason()) {
      protoValue.reason = reason.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.DiagnosisComponent.toProto(): Claim.Diagnosis {
    val protoValue = Claim.Diagnosis.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasDiagnosis()) {
      protoValue.diagnosis = diagnosis.claimDiagnosisDiagnosisToProto()
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasOnAdmission()) {
      protoValue.onAdmission = onAdmission.toProto()
    }
    if (hasPackageCode()) {
      protoValue.packageCode = packageCode.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.ProcedureComponent.toProto(): Claim.Procedure {
    val protoValue = Claim.Procedure.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasProcedure()) {
      protoValue.procedure = procedure.claimProcedureProcedureToProto()
    }
    if (hasUdi()) {
      protoValue.addAllUdi(udi.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.InsuranceComponent.toProto(): Claim.Insurance {
    val protoValue = Claim.Insurance.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasFocal()) {
      protoValue.focal = focalElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasCoverage()) {
      protoValue.coverage = coverage.toProto()
    }
    if (hasBusinessArrangement()) {
      protoValue.businessArrangement = businessArrangementElement.toProto()
    }
    if (hasPreAuthRef()) {
      protoValue.addAllPreAuthRef(preAuthRef.map { it.toProto() })
    }
    if (hasClaimResponse()) {
      protoValue.claimResponse = claimResponse.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.AccidentComponent.toProto(): Claim.Accident {
    val protoValue = Claim.Accident.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasLocation()) {
      protoValue.location = location.claimAccidentLocationToProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.ItemComponent.toProto(): Claim.Item {
    val protoValue = Claim.Item.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasCareTeamSequence()) {
      protoValue.addAllCareTeamSequence(careTeamSequence.map { it.toProto() })
    }
    if (hasDiagnosisSequence()) {
      protoValue.addAllDiagnosisSequence(diagnosisSequence.map { it.toProto() })
    }
    if (hasProcedureSequence()) {
      protoValue.addAllProcedureSequence(procedureSequence.map { it.toProto() })
    }
    if (hasInformationSequence()) {
      protoValue.addAllInformationSequence(informationSequence.map { it.toProto() })
    }
    if (hasRevenue()) {
      protoValue.revenue = revenue.toProto()
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasProductOrService()) {
      protoValue.productOrService = productOrService.toProto()
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasProgramCode()) {
      protoValue.addAllProgramCode(programCode.map { it.toProto() })
    }
    if (hasServiced()) {
      protoValue.serviced = serviced.claimItemServicedToProto()
    }
    if (hasLocation()) {
      protoValue.location = location.claimItemLocationToProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
      protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasNet()) {
      protoValue.net = net.toProto()
    }
    if (hasUdi()) {
      protoValue.addAllUdi(udi.map { it.toProto() })
    }
    if (hasBodySite()) {
      protoValue.bodySite = bodySite.toProto()
    }
    if (hasSubSite()) {
      protoValue.addAllSubSite(subSite.map { it.toProto() })
    }
    if (hasEncounter()) {
      protoValue.addAllEncounter(encounter.map { it.toProto() })
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.DetailComponent.toProto(): Claim.Item.Detail {
    val protoValue = Claim.Item.Detail.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasRevenue()) {
      protoValue.revenue = revenue.toProto()
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasProductOrService()) {
      protoValue.productOrService = productOrService.toProto()
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasProgramCode()) {
      protoValue.addAllProgramCode(programCode.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
      protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasNet()) {
      protoValue.net = net.toProto()
    }
    if (hasUdi()) {
      protoValue.addAllUdi(udi.map { it.toProto() })
    }
    if (hasSubDetail()) {
      protoValue.addAllSubDetail(subDetail.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Claim.SubDetailComponent.toProto():
    Claim.Item.Detail.SubDetail {
    val protoValue =
      Claim.Item.Detail.SubDetail.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasRevenue()) {
      protoValue.revenue = revenue.toProto()
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasProductOrService()) {
      protoValue.productOrService = productOrService.toProto()
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasProgramCode()) {
      protoValue.addAllProgramCode(programCode.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
      protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasNet()) {
      protoValue.net = net.toProto()
    }
    if (hasUdi()) {
      protoValue.addAllUdi(udi.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun Claim.RelatedClaim.toHapi(): org.hl7.fhir.r4.model.Claim.RelatedClaimComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.RelatedClaimComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRelationship()) {
      hapiValue.relationship = relationship.toHapi()
    }
    if (hasReference()) {
      hapiValue.reference = reference.toHapi()
    }
    return hapiValue
  }

  private fun Claim.Payee.toHapi(): org.hl7.fhir.r4.model.Claim.PayeeComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.PayeeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasParty()) {
      hapiValue.party = party.toHapi()
    }
    return hapiValue
  }

  private fun Claim.CareTeam.toHapi(): org.hl7.fhir.r4.model.Claim.CareTeamComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.CareTeamComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasProvider()) {
      hapiValue.provider = provider.toHapi()
    }
    if (hasResponsible()) {
      hapiValue.responsibleElement = responsible.toHapi()
    }
    if (hasRole()) {
      hapiValue.role = role.toHapi()
    }
    if (hasQualification()) {
      hapiValue.qualification = qualification.toHapi()
    }
    return hapiValue
  }

  private fun Claim.SupportingInformation.toHapi():
    org.hl7.fhir.r4.model.Claim.SupportingInformationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.SupportingInformationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasTiming()) {
      hapiValue.timing = timing.claimSupportingInfoTimingToHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.claimSupportingInfoValueToHapi()
    }
    if (hasReason()) {
      hapiValue.reason = reason.toHapi()
    }
    return hapiValue
  }

  private fun Claim.Diagnosis.toHapi(): org.hl7.fhir.r4.model.Claim.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.DiagnosisComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasDiagnosis()) {
      hapiValue.diagnosis = diagnosis.claimDiagnosisDiagnosisToHapi()
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasOnAdmission()) {
      hapiValue.onAdmission = onAdmission.toHapi()
    }
    if (hasPackageCode()) {
      hapiValue.packageCode = packageCode.toHapi()
    }
    return hapiValue
  }

  private fun Claim.Procedure.toHapi(): org.hl7.fhir.r4.model.Claim.ProcedureComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.ProcedureComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasProcedure()) {
      hapiValue.procedure = procedure.claimProcedureProcedureToHapi()
    }
    if (udiCount > 0) {
      hapiValue.udi = udiList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun Claim.Insurance.toHapi(): org.hl7.fhir.r4.model.Claim.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.InsuranceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasFocal()) {
      hapiValue.focalElement = focal.toHapi()
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasCoverage()) {
      hapiValue.coverage = coverage.toHapi()
    }
    if (hasBusinessArrangement()) {
      hapiValue.businessArrangementElement = businessArrangement.toHapi()
    }
    if (preAuthRefCount > 0) {
      hapiValue.preAuthRef = preAuthRefList.map { it.toHapi() }
    }
    if (hasClaimResponse()) {
      hapiValue.claimResponse = claimResponse.toHapi()
    }
    return hapiValue
  }

  private fun Claim.Accident.toHapi(): org.hl7.fhir.r4.model.Claim.AccidentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.AccidentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasLocation()) {
      hapiValue.location = location.claimAccidentLocationToHapi()
    }
    return hapiValue
  }

  private fun Claim.Item.toHapi(): org.hl7.fhir.r4.model.Claim.ItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.ItemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (careTeamSequenceCount > 0) {
      hapiValue.careTeamSequence = careTeamSequenceList.map { it.toHapi() }
    }
    if (diagnosisSequenceCount > 0) {
      hapiValue.diagnosisSequence = diagnosisSequenceList.map { it.toHapi() }
    }
    if (procedureSequenceCount > 0) {
      hapiValue.procedureSequence = procedureSequenceList.map { it.toHapi() }
    }
    if (informationSequenceCount > 0) {
      hapiValue.informationSequence = informationSequenceList.map { it.toHapi() }
    }
    if (hasRevenue()) {
      hapiValue.revenue = revenue.toHapi()
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasProductOrService()) {
      hapiValue.productOrService = productOrService.toHapi()
    }
    if (modifierCount > 0) {
      hapiValue.modifier = modifierList.map { it.toHapi() }
    }
    if (programCodeCount > 0) {
      hapiValue.programCode = programCodeList.map { it.toHapi() }
    }
    if (hasServiced()) {
      hapiValue.serviced = serviced.claimItemServicedToHapi()
    }
    if (hasLocation()) {
      hapiValue.location = location.claimItemLocationToHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
      hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasNet()) {
      hapiValue.net = net.toHapi()
    }
    if (udiCount > 0) {
      hapiValue.udi = udiList.map { it.toHapi() }
    }
    if (hasBodySite()) {
      hapiValue.bodySite = bodySite.toHapi()
    }
    if (subSiteCount > 0) {
      hapiValue.subSite = subSiteList.map { it.toHapi() }
    }
    if (encounterCount > 0) {
      hapiValue.encounter = encounterList.map { it.toHapi() }
    }
    if (detailCount > 0) {
      hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun Claim.Item.Detail.toHapi(): org.hl7.fhir.r4.model.Claim.DetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.DetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasRevenue()) {
      hapiValue.revenue = revenue.toHapi()
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasProductOrService()) {
      hapiValue.productOrService = productOrService.toHapi()
    }
    if (modifierCount > 0) {
      hapiValue.modifier = modifierList.map { it.toHapi() }
    }
    if (programCodeCount > 0) {
      hapiValue.programCode = programCodeList.map { it.toHapi() }
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
      hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasNet()) {
      hapiValue.net = net.toHapi()
    }
    if (udiCount > 0) {
      hapiValue.udi = udiList.map { it.toHapi() }
    }
    if (subDetailCount > 0) {
      hapiValue.subDetail = subDetailList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun Claim.Item.Detail.SubDetail.toHapi(): org.hl7.fhir.r4.model.Claim.SubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.SubDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasRevenue()) {
      hapiValue.revenue = revenue.toHapi()
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasProductOrService()) {
      hapiValue.productOrService = productOrService.toHapi()
    }
    if (modifierCount > 0) {
      hapiValue.modifier = modifierList.map { it.toHapi() }
    }
    if (programCodeCount > 0) {
      hapiValue.programCode = programCodeList.map { it.toHapi() }
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
      hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasNet()) {
      hapiValue.net = net.toHapi()
    }
    if (udiCount > 0) {
      hapiValue.udi = udiList.map { it.toHapi() }
    }
    return hapiValue
  }
}
