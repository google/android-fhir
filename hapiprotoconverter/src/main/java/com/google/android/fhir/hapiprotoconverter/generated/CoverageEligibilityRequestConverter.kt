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

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.CoverageEligibilityRequest
import com.google.fhir.r4.core.CoverageEligibilityRequest.Details
import com.google.fhir.r4.core.CoverageEligibilityRequest.Details.Diagnosis
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.EligibilityRequestPurposeCode
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

object CoverageEligibilityRequestConverter {
  private fun CoverageEligibilityRequest.ServicedX.coverageEligibilityRequestServicedToHapi():
    Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CoverageEligibilityRequest.serviced[x]")
  }

  private fun Type.coverageEligibilityRequestServicedToProto():
    CoverageEligibilityRequest.ServicedX {
    val protoValue = CoverageEligibilityRequest.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.date = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  private fun CoverageEligibilityRequest.Details.Diagnosis.DiagnosisX.coverageEligibilityRequestItemDiagnosisDiagnosisToHapi():
    Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for CoverageEligibilityRequest.item.diagnosis.diagnosis[x]"
    )
  }

  private fun Type.coverageEligibilityRequestItemDiagnosisDiagnosisToProto():
    CoverageEligibilityRequest.Details.Diagnosis.DiagnosisX {
    val protoValue = CoverageEligibilityRequest.Details.Diagnosis.DiagnosisX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  fun CoverageEligibilityRequest.toHapi(): org.hl7.fhir.r4.model.CoverageEligibilityRequest {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest()
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
      org.hl7.fhir.r4.model.CoverageEligibilityRequest.EligibilityRequestStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPriority()) {
      hapiValue.priority = priority.toHapi()
    }
    purposeList.forEach {
      hapiValue.addPurpose(
        org.hl7.fhir.r4.model.CoverageEligibilityRequest.EligibilityRequestPurpose.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasServiced()) {
      hapiValue.serviced = serviced.coverageEligibilityRequestServicedToHapi()
    }
    if (hasCreated()) {
      hapiValue.createdElement = created.toHapi()
    }
    if (hasEnterer()) {
      hapiValue.enterer = enterer.toHapi()
    }
    if (hasProvider()) {
      hapiValue.provider = provider.toHapi()
    }
    if (hasInsurer()) {
      hapiValue.insurer = insurer.toHapi()
    }
    if (hasFacility()) {
      hapiValue.facility = facility.toHapi()
    }
    if (supportingInfoCount > 0) {
      hapiValue.supportingInfo = supportingInfoList.map { it.toHapi() }
    }
    if (insuranceCount > 0) {
      hapiValue.insurance = insuranceList.map { it.toHapi() }
    }
    if (itemCount > 0) {
      hapiValue.item = itemList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.toProto(): CoverageEligibilityRequest {
    val protoValue = CoverageEligibilityRequest.newBuilder().setId(Id.newBuilder().setValue(id))
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
      CoverageEligibilityRequest.StatusCode.newBuilder()
        .setValue(
          FinancialResourceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasPriority()) {
      protoValue.priority = priority.toProto()
    }
    protoValue.addAllPurpose(
      purpose.map {
        CoverageEligibilityRequest.PurposeCode.newBuilder()
          .setValue(
            EligibilityRequestPurposeCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasServiced()) {
      protoValue.serviced = serviced.coverageEligibilityRequestServicedToProto()
    }
    if (hasCreated()) {
      protoValue.created = createdElement.toProto()
    }
    if (hasEnterer()) {
      protoValue.enterer = enterer.toProto()
    }
    if (hasProvider()) {
      protoValue.provider = provider.toProto()
    }
    if (hasInsurer()) {
      protoValue.insurer = insurer.toProto()
    }
    if (hasFacility()) {
      protoValue.facility = facility.toProto()
    }
    if (hasSupportingInfo()) {
      protoValue.addAllSupportingInfo(supportingInfo.map { it.toProto() })
    }
    if (hasInsurance()) {
      protoValue.addAllInsurance(insurance.map { it.toProto() })
    }
    if (hasItem()) {
      protoValue.addAllItem(item.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.SupportingInformationComponent.toProto():
    CoverageEligibilityRequest.SupportingInformation {
    val protoValue =
      CoverageEligibilityRequest.SupportingInformation.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasInformation()) {
      protoValue.information = information.toProto()
    }
    if (hasAppliesToAll()) {
      protoValue.appliesToAll = appliesToAllElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.InsuranceComponent.toProto():
    CoverageEligibilityRequest.Insurance {
    val protoValue =
      CoverageEligibilityRequest.Insurance.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFocal()) {
      protoValue.focal = focalElement.toProto()
    }
    if (hasCoverage()) {
      protoValue.coverage = coverage.toProto()
    }
    if (hasBusinessArrangement()) {
      protoValue.businessArrangement = businessArrangementElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.DetailsComponent.toProto():
    CoverageEligibilityRequest.Details {
    val protoValue =
      CoverageEligibilityRequest.Details.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSupportingInfoSequence()) {
      protoValue.addAllSupportingInfoSequence(supportingInfoSequence.map { it.toProto() })
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
    if (hasProvider()) {
      protoValue.provider = provider.toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasUnitPrice()) {
      protoValue.unitPrice = unitPrice.toProto()
    }
    if (hasFacility()) {
      protoValue.facility = facility.toProto()
    }
    if (hasDiagnosis()) {
      protoValue.addAllDiagnosis(diagnosis.map { it.toProto() })
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.CoverageEligibilityRequest.DiagnosisComponent.toProto():
    CoverageEligibilityRequest.Details.Diagnosis {
    val protoValue =
      CoverageEligibilityRequest.Details.Diagnosis.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDiagnosis()) {
      protoValue.diagnosis = diagnosis.coverageEligibilityRequestItemDiagnosisDiagnosisToProto()
    }
    return protoValue.build()
  }

  private fun CoverageEligibilityRequest.SupportingInformation.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityRequest.SupportingInformationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.CoverageEligibilityRequest.SupportingInformationComponent()
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
    if (hasInformation()) {
      hapiValue.information = information.toHapi()
    }
    if (hasAppliesToAll()) {
      hapiValue.appliesToAllElement = appliesToAll.toHapi()
    }
    return hapiValue
  }

  private fun CoverageEligibilityRequest.Insurance.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityRequest.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest.InsuranceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFocal()) {
      hapiValue.focalElement = focal.toHapi()
    }
    if (hasCoverage()) {
      hapiValue.coverage = coverage.toHapi()
    }
    if (hasBusinessArrangement()) {
      hapiValue.businessArrangementElement = businessArrangement.toHapi()
    }
    return hapiValue
  }

  private fun CoverageEligibilityRequest.Details.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityRequest.DetailsComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest.DetailsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (supportingInfoSequenceCount > 0) {
      hapiValue.supportingInfoSequence = supportingInfoSequenceList.map { it.toHapi() }
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
    if (hasProvider()) {
      hapiValue.provider = provider.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasUnitPrice()) {
      hapiValue.unitPrice = unitPrice.toHapi()
    }
    if (hasFacility()) {
      hapiValue.facility = facility.toHapi()
    }
    if (diagnosisCount > 0) {
      hapiValue.diagnosis = diagnosisList.map { it.toHapi() }
    }
    if (detailCount > 0) {
      hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun CoverageEligibilityRequest.Details.Diagnosis.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityRequest.DiagnosisComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityRequest.DiagnosisComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDiagnosis()) {
      hapiValue.diagnosis = diagnosis.coverageEligibilityRequestItemDiagnosisDiagnosisToHapi()
    }
    return hapiValue
  }
}
