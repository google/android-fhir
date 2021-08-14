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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object ClaimConverter {
  @JvmStatic
  private fun Claim.SupportingInformation.TimingX.claimSupportingInfoTimingToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.supportingInfo.timing[x]")
  }

  @JvmStatic
  private fun Type.claimSupportingInfoTimingToProto(): Claim.SupportingInformation.TimingX {
    val protoValue = Claim.SupportingInformation.TimingX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Claim.SupportingInformation.ValueX.claimSupportingInfoValueToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getAttachment() != Attachment.newBuilder().defaultInstanceForType) {
      return (this.getAttachment()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.supportingInfo.value[x]")
  }

  @JvmStatic
  private fun Type.claimSupportingInfoValueToProto(): Claim.SupportingInformation.ValueX {
    val protoValue = Claim.SupportingInformation.ValueX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Attachment) {
      protoValue.setAttachment(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Claim.Diagnosis.DiagnosisX.claimDiagnosisDiagnosisToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.diagnosis.diagnosis[x]")
  }

  @JvmStatic
  private fun Type.claimDiagnosisDiagnosisToProto(): Claim.Diagnosis.DiagnosisX {
    val protoValue = Claim.Diagnosis.DiagnosisX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Claim.Procedure.ProcedureX.claimProcedureProcedureToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.procedure.procedure[x]")
  }

  @JvmStatic
  private fun Type.claimProcedureProcedureToProto(): Claim.Procedure.ProcedureX {
    val protoValue = Claim.Procedure.ProcedureX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Claim.Accident.LocationX.claimAccidentLocationToHapi(): Type {
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType) {
      return (this.getAddress()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.accident.location[x]")
  }

  @JvmStatic
  private fun Type.claimAccidentLocationToProto(): Claim.Accident.LocationX {
    val protoValue = Claim.Accident.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Claim.Item.ServicedX.claimItemServicedToHapi(): Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.item.serviced[x]")
  }

  @JvmStatic
  private fun Type.claimItemServicedToProto(): Claim.Item.ServicedX {
    val protoValue = Claim.Item.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Claim.Item.LocationX.claimItemLocationToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getAddress() != Address.newBuilder().defaultInstanceForType) {
      return (this.getAddress()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Claim.item.location[x]")
  }

  @JvmStatic
  private fun Type.claimItemLocationToProto(): Claim.Item.LocationX {
    val protoValue = Claim.Item.LocationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Address) {
      protoValue.setAddress(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun Claim.toHapi(): org.hl7.fhir.r4.model.Claim {
    val hapiValue = org.hl7.fhir.r4.model.Claim()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Claim.ClaimStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setType(type.toHapi())
    hapiValue.setSubType(subType.toHapi())
    hapiValue.setUse(
      org.hl7.fhir.r4.model.Claim.Use.valueOf(use.value.name.hapiCodeCheck().replace("_", ""))
    )
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setBillablePeriod(billablePeriod.toHapi())
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setEnterer(enterer.toHapi())
    hapiValue.setInsurer(insurer.toHapi())
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setPriority(priority.toHapi())
    hapiValue.setFundsReserve(fundsReserve.toHapi())
    hapiValue.setRelated(relatedList.map { it.toHapi() })
    hapiValue.setPrescription(prescription.toHapi())
    hapiValue.setOriginalPrescription(originalPrescription.toHapi())
    hapiValue.setPayee(payee.toHapi())
    hapiValue.setReferral(referral.toHapi())
    hapiValue.setFacility(facility.toHapi())
    hapiValue.setCareTeam(careTeamList.map { it.toHapi() })
    hapiValue.setSupportingInfo(supportingInfoList.map { it.toHapi() })
    hapiValue.setDiagnosis(diagnosisList.map { it.toHapi() })
    hapiValue.setProcedure(procedureList.map { it.toHapi() })
    hapiValue.setInsurance(insuranceList.map { it.toHapi() })
    hapiValue.setAccident(accident.toHapi())
    hapiValue.setItem(itemList.map { it.toHapi() })
    hapiValue.setTotal(total.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Claim.toProto(): Claim {
    val protoValue =
      Claim.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          Claim.StatusCode.newBuilder()
            .setValue(
              FinancialResourceStatusCode.Value.valueOf(
                status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setType(type.toProto())
        .setSubType(subType.toProto())
        .setUse(
          Claim.UseCode.newBuilder()
            .setValue(
              UseCode.Value.valueOf(use.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setPatient(patient.toProto())
        .setBillablePeriod(billablePeriod.toProto())
        .setCreated(createdElement.toProto())
        .setEnterer(enterer.toProto())
        .setInsurer(insurer.toProto())
        .setProvider(provider.toProto())
        .setPriority(priority.toProto())
        .setFundsReserve(fundsReserve.toProto())
        .addAllRelated(related.map { it.toProto() })
        .setPrescription(prescription.toProto())
        .setOriginalPrescription(originalPrescription.toProto())
        .setPayee(payee.toProto())
        .setReferral(referral.toProto())
        .setFacility(facility.toProto())
        .addAllCareTeam(careTeam.map { it.toProto() })
        .addAllSupportingInfo(supportingInfo.map { it.toProto() })
        .addAllDiagnosis(diagnosis.map { it.toProto() })
        .addAllProcedure(procedure.map { it.toProto() })
        .addAllInsurance(insurance.map { it.toProto() })
        .setAccident(accident.toProto())
        .addAllItem(item.map { it.toProto() })
        .setTotal(total.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.RelatedClaimComponent.toProto(): Claim.RelatedClaim {
    val protoValue =
      Claim.RelatedClaim.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setRelationship(relationship.toProto())
        .setReference(reference.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.PayeeComponent.toProto(): Claim.Payee {
    val protoValue =
      Claim.Payee.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setParty(party.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.CareTeamComponent.toProto(): Claim.CareTeam {
    val protoValue =
      Claim.CareTeam.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .setProvider(provider.toProto())
        .setResponsible(responsibleElement.toProto())
        .setRole(role.toProto())
        .setQualification(qualification.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.SupportingInformationComponent.toProto():
    Claim.SupportingInformation {
    val protoValue =
      Claim.SupportingInformation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .setCategory(category.toProto())
        .setCode(code.toProto())
        .setTiming(timing.claimSupportingInfoTimingToProto())
        .setValue(value.claimSupportingInfoValueToProto())
        .setReason(reason.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.DiagnosisComponent.toProto(): Claim.Diagnosis {
    val protoValue =
      Claim.Diagnosis.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .setDiagnosis(diagnosis.claimDiagnosisDiagnosisToProto())
        .addAllType(type.map { it.toProto() })
        .setOnAdmission(onAdmission.toProto())
        .setPackageCode(packageCode.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.ProcedureComponent.toProto(): Claim.Procedure {
    val protoValue =
      Claim.Procedure.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .addAllType(type.map { it.toProto() })
        .setDate(dateElement.toProto())
        .setProcedure(procedure.claimProcedureProcedureToProto())
        .addAllUdi(udi.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.InsuranceComponent.toProto(): Claim.Insurance {
    val protoValue =
      Claim.Insurance.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .setFocal(focalElement.toProto())
        .setIdentifier(identifier.toProto())
        .setCoverage(coverage.toProto())
        .setBusinessArrangement(businessArrangementElement.toProto())
        .addAllPreAuthRef(preAuthRef.map { it.toProto() })
        .setClaimResponse(claimResponse.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.AccidentComponent.toProto(): Claim.Accident {
    val protoValue =
      Claim.Accident.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDate(dateElement.toProto())
        .setType(type.toProto())
        .setLocation(location.claimAccidentLocationToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.ItemComponent.toProto(): Claim.Item {
    val protoValue =
      Claim.Item.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .addAllCareTeamSequence(careTeamSequence.map { it.toProto() })
        .addAllDiagnosisSequence(diagnosisSequence.map { it.toProto() })
        .addAllProcedureSequence(procedureSequence.map { it.toProto() })
        .addAllInformationSequence(informationSequence.map { it.toProto() })
        .setRevenue(revenue.toProto())
        .setCategory(category.toProto())
        .setProductOrService(productOrService.toProto())
        .addAllModifier(modifier.map { it.toProto() })
        .addAllProgramCode(programCode.map { it.toProto() })
        .setServiced(serviced.claimItemServicedToProto())
        .setLocation(location.claimItemLocationToProto())
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setUnitPrice(unitPrice.toProto())
        .setFactor(factorElement.toProto())
        .setNet(net.toProto())
        .addAllUdi(udi.map { it.toProto() })
        .setBodySite(bodySite.toProto())
        .addAllSubSite(subSite.map { it.toProto() })
        .addAllEncounter(encounter.map { it.toProto() })
        .addAllDetail(detail.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.DetailComponent.toProto(): Claim.Item.Detail {
    val protoValue =
      Claim.Item.Detail.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .setRevenue(revenue.toProto())
        .setCategory(category.toProto())
        .setProductOrService(productOrService.toProto())
        .addAllModifier(modifier.map { it.toProto() })
        .addAllProgramCode(programCode.map { it.toProto() })
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setUnitPrice(unitPrice.toProto())
        .setFactor(factorElement.toProto())
        .setNet(net.toProto())
        .addAllUdi(udi.map { it.toProto() })
        .addAllSubDetail(subDetail.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Claim.SubDetailComponent.toProto():
    Claim.Item.Detail.SubDetail {
    val protoValue =
      Claim.Item.Detail.SubDetail.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .setRevenue(revenue.toProto())
        .setCategory(category.toProto())
        .setProductOrService(productOrService.toProto())
        .addAllModifier(modifier.map { it.toProto() })
        .addAllProgramCode(programCode.map { it.toProto() })
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setUnitPrice(unitPrice.toProto())
        .setFactor(factorElement.toProto())
        .setNet(net.toProto())
        .addAllUdi(udi.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Claim.RelatedClaim.toHapi(): org.hl7.fhir.r4.model.Claim.RelatedClaimComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.RelatedClaimComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setRelationship(relationship.toHapi())
    hapiValue.setReference(reference.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Payee.toHapi(): org.hl7.fhir.r4.model.Claim.PayeeComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.PayeeComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setParty(party.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Claim.CareTeam.toHapi(): org.hl7.fhir.r4.model.Claim.CareTeamComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.CareTeamComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setProvider(provider.toHapi())
    hapiValue.setResponsibleElement(responsible.toHapi())
    hapiValue.setRole(role.toHapi())
    hapiValue.setQualification(qualification.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Claim.SupportingInformation.toHapi():
    org.hl7.fhir.r4.model.Claim.SupportingInformationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.SupportingInformationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setTiming(timing.claimSupportingInfoTimingToHapi())
    hapiValue.setValue(value.claimSupportingInfoValueToHapi())
    hapiValue.setReason(reason.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Diagnosis.toHapi(): org.hl7.fhir.r4.model.Claim.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.DiagnosisComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setDiagnosis(diagnosis.claimDiagnosisDiagnosisToHapi())
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setOnAdmission(onAdmission.toHapi())
    hapiValue.setPackageCode(packageCode.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Procedure.toHapi(): org.hl7.fhir.r4.model.Claim.ProcedureComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.ProcedureComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setProcedure(procedure.claimProcedureProcedureToHapi())
    hapiValue.setUdi(udiList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Insurance.toHapi(): org.hl7.fhir.r4.model.Claim.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.InsuranceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setFocalElement(focal.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setCoverage(coverage.toHapi())
    hapiValue.setBusinessArrangementElement(businessArrangement.toHapi())
    hapiValue.setPreAuthRef(preAuthRefList.map { it.toHapi() })
    hapiValue.setClaimResponse(claimResponse.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Accident.toHapi(): org.hl7.fhir.r4.model.Claim.AccidentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.AccidentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setLocation(location.claimAccidentLocationToHapi())
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Item.toHapi(): org.hl7.fhir.r4.model.Claim.ItemComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.ItemComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setCareTeamSequence(careTeamSequenceList.map { it.toHapi() })
    hapiValue.setDiagnosisSequence(diagnosisSequenceList.map { it.toHapi() })
    hapiValue.setProcedureSequence(procedureSequenceList.map { it.toHapi() })
    hapiValue.setInformationSequence(informationSequenceList.map { it.toHapi() })
    hapiValue.setRevenue(revenue.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map { it.toHapi() })
    hapiValue.setProgramCode(programCodeList.map { it.toHapi() })
    hapiValue.setServiced(serviced.claimItemServicedToHapi())
    hapiValue.setLocation(location.claimItemLocationToHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setUdi(udiList.map { it.toHapi() })
    hapiValue.setBodySite(bodySite.toHapi())
    hapiValue.setSubSite(subSiteList.map { it.toHapi() })
    hapiValue.setEncounter(encounterList.map { it.toHapi() })
    hapiValue.setDetail(detailList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Item.Detail.toHapi(): org.hl7.fhir.r4.model.Claim.DetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.DetailComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setRevenue(revenue.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map { it.toHapi() })
    hapiValue.setProgramCode(programCodeList.map { it.toHapi() })
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setUdi(udiList.map { it.toHapi() })
    hapiValue.setSubDetail(subDetailList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun Claim.Item.Detail.SubDetail.toHapi(): org.hl7.fhir.r4.model.Claim.SubDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.Claim.SubDetailComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setRevenue(revenue.toHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setProductOrService(productOrService.toHapi())
    hapiValue.setModifier(modifierList.map { it.toHapi() })
    hapiValue.setProgramCode(programCodeList.map { it.toHapi() })
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setUnitPrice(unitPrice.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setNet(net.toHapi())
    hapiValue.setUdi(udiList.map { it.toHapi() })
    return hapiValue
  }
}
