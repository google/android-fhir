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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.ClaimProcessingCode
import com.google.fhir.r4.core.CoverageEligibilityResponse
import com.google.fhir.r4.core.CoverageEligibilityResponse.Insurance
import com.google.fhir.r4.core.CoverageEligibilityResponse.Insurance.Items
import com.google.fhir.r4.core.CoverageEligibilityResponse.Insurance.Items.Benefit
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.EligibilityResponsePurposeCode
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.UnsignedInt
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UnsignedIntType

public object CoverageEligibilityResponseConverter {
  @JvmStatic
  private fun CoverageEligibilityResponse.ServicedX.coverageEligibilityResponseServicedToHapi():
    Type {
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CoverageEligibilityResponse.serviced[x]")
  }

  @JvmStatic
  private fun Type.coverageEligibilityResponseServicedToProto():
    CoverageEligibilityResponse.ServicedX {
    val protoValue = CoverageEligibilityResponse.ServicedX.newBuilder()
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.Benefit.AllowedX.coverageEligibilityResponseInsuranceItemBenefitAllowedToHapi():
    Type {
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType) {
      return (this.getMoney()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for CoverageEligibilityResponse.insurance.item.benefit.allowed[x]"
    )
  }

  @JvmStatic
  private fun Type.coverageEligibilityResponseInsuranceItemBenefitAllowedToProto():
    CoverageEligibilityResponse.Insurance.Items.Benefit.AllowedX {
    val protoValue = CoverageEligibilityResponse.Insurance.Items.Benefit.AllowedX.newBuilder()
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.Benefit.UsedX.coverageEligibilityResponseInsuranceItemBenefitUsedToHapi():
    Type {
    if (this.getUnsignedInt() != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.getUnsignedInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType) {
      return (this.getMoney()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for CoverageEligibilityResponse.insurance.item.benefit.used[x]"
    )
  }

  @JvmStatic
  private fun Type.coverageEligibilityResponseInsuranceItemBenefitUsedToProto():
    CoverageEligibilityResponse.Insurance.Items.Benefit.UsedX {
    val protoValue = CoverageEligibilityResponse.Insurance.Items.Benefit.UsedX.newBuilder()
    if (this is UnsignedIntType) {
      protoValue.setUnsignedInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun CoverageEligibilityResponse.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.CoverageEligibilityResponse.EligibilityResponseStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    purposeList.forEach {
      hapiValue.addPurpose(
        org.hl7.fhir.r4.model.CoverageEligibilityResponse.EligibilityResponsePurpose.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasPatient()) {
      hapiValue.setPatient(patient.toHapi())
    }
    if (hasServiced()) {
      hapiValue.setServiced(serviced.coverageEligibilityResponseServicedToHapi())
    }
    if (hasCreated()) {
      hapiValue.setCreatedElement(created.toHapi())
    }
    if (hasRequestor()) {
      hapiValue.setRequestor(requestor.toHapi())
    }
    if (hasRequest()) {
      hapiValue.setRequest(request.toHapi())
    }
    hapiValue.setOutcome(
      Enumerations.RemittanceOutcome.valueOf(outcome.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasDisposition()) {
      hapiValue.setDispositionElement(disposition.toHapi())
    }
    if (hasInsurer()) {
      hapiValue.setInsurer(insurer.toHapi())
    }
    if (insuranceCount > 0) {
      hapiValue.setInsurance(insuranceList.map { it.toHapi() })
    }
    if (hasPreAuthRef()) {
      hapiValue.setPreAuthRefElement(preAuthRef.toHapi())
    }
    if (hasForm()) {
      hapiValue.setForm(form.toHapi())
    }
    if (errorCount > 0) {
      hapiValue.setError(errorList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.CoverageEligibilityResponse.toProto():
    CoverageEligibilityResponse {
    val protoValue = CoverageEligibilityResponse.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
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
    protoValue.setStatus(
      CoverageEligibilityResponse.StatusCode.newBuilder()
        .setValue(
          FinancialResourceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.addAllPurpose(
      purpose.map {
        CoverageEligibilityResponse.PurposeCode.newBuilder()
          .setValue(
            EligibilityResponsePurposeCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasPatient()) {
      protoValue.setPatient(patient.toProto())
    }
    if (hasServiced()) {
      protoValue.setServiced(serviced.coverageEligibilityResponseServicedToProto())
    }
    if (hasCreated()) {
      protoValue.setCreated(createdElement.toProto())
    }
    if (hasRequestor()) {
      protoValue.setRequestor(requestor.toProto())
    }
    if (hasRequest()) {
      protoValue.setRequest(request.toProto())
    }
    protoValue.setOutcome(
      CoverageEligibilityResponse.OutcomeCode.newBuilder()
        .setValue(
          ClaimProcessingCode.Value.valueOf(
            outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasDisposition()) {
      protoValue.setDisposition(dispositionElement.toProto())
    }
    if (hasInsurer()) {
      protoValue.setInsurer(insurer.toProto())
    }
    if (hasInsurance()) {
      protoValue.addAllInsurance(insurance.map { it.toProto() })
    }
    if (hasPreAuthRef()) {
      protoValue.setPreAuthRef(preAuthRefElement.toProto())
    }
    if (hasForm()) {
      protoValue.setForm(form.toProto())
    }
    if (hasError()) {
      protoValue.addAllError(error.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CoverageEligibilityResponse.InsuranceComponent.toProto():
    CoverageEligibilityResponse.Insurance {
    val protoValue =
      CoverageEligibilityResponse.Insurance.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCoverage()) {
      protoValue.setCoverage(coverage.toProto())
    }
    if (hasInforce()) {
      protoValue.setInforce(inforceElement.toProto())
    }
    if (hasBenefitPeriod()) {
      protoValue.setBenefitPeriod(benefitPeriod.toProto())
    }
    if (hasItem()) {
      protoValue.addAllItem(item.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CoverageEligibilityResponse.ItemsComponent.toProto():
    CoverageEligibilityResponse.Insurance.Items {
    val protoValue =
      CoverageEligibilityResponse.Insurance.Items.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.setCategory(category.toProto())
    }
    if (hasProductOrService()) {
      protoValue.setProductOrService(productOrService.toProto())
    }
    if (hasModifier()) {
      protoValue.addAllModifier(modifier.map { it.toProto() })
    }
    if (hasProvider()) {
      protoValue.setProvider(provider.toProto())
    }
    if (hasExcluded()) {
      protoValue.setExcluded(excludedElement.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasNetwork()) {
      protoValue.setNetwork(network.toProto())
    }
    if (hasUnit()) {
      protoValue.setUnit(unit.toProto())
    }
    if (hasTerm()) {
      protoValue.setTerm(term.toProto())
    }
    if (hasBenefit()) {
      protoValue.addAllBenefit(benefit.map { it.toProto() })
    }
    if (hasAuthorizationRequired()) {
      protoValue.setAuthorizationRequired(authorizationRequiredElement.toProto())
    }
    if (hasAuthorizationSupporting()) {
      protoValue.addAllAuthorizationSupporting(authorizationSupporting.map { it.toProto() })
    }
    if (hasAuthorizationUrl()) {
      protoValue.setAuthorizationUrl(authorizationUrlElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CoverageEligibilityResponse.BenefitComponent.toProto():
    CoverageEligibilityResponse.Insurance.Items.Benefit {
    val protoValue =
      CoverageEligibilityResponse.Insurance.Items.Benefit.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasAllowed()) {
      protoValue.setAllowed(allowed.coverageEligibilityResponseInsuranceItemBenefitAllowedToProto())
    }
    if (hasUsed()) {
      protoValue.setUsed(used.coverageEligibilityResponseInsuranceItemBenefitUsedToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.CoverageEligibilityResponse.ErrorsComponent.toProto():
    CoverageEligibilityResponse.Errors {
    val protoValue =
      CoverageEligibilityResponse.Errors.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.InsuranceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCoverage()) {
      hapiValue.setCoverage(coverage.toHapi())
    }
    if (hasInforce()) {
      hapiValue.setInforceElement(inforce.toHapi())
    }
    if (hasBenefitPeriod()) {
      hapiValue.setBenefitPeriod(benefitPeriod.toHapi())
    }
    if (itemCount > 0) {
      hapiValue.setItem(itemList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.ItemsComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.ItemsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCategory()) {
      hapiValue.setCategory(category.toHapi())
    }
    if (hasProductOrService()) {
      hapiValue.setProductOrService(productOrService.toHapi())
    }
    if (modifierCount > 0) {
      hapiValue.setModifier(modifierList.map { it.toHapi() })
    }
    if (hasProvider()) {
      hapiValue.setProvider(provider.toHapi())
    }
    if (hasExcluded()) {
      hapiValue.setExcludedElement(excluded.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasNetwork()) {
      hapiValue.setNetwork(network.toHapi())
    }
    if (hasUnit()) {
      hapiValue.setUnit(unit.toHapi())
    }
    if (hasTerm()) {
      hapiValue.setTerm(term.toHapi())
    }
    if (benefitCount > 0) {
      hapiValue.setBenefit(benefitList.map { it.toHapi() })
    }
    if (hasAuthorizationRequired()) {
      hapiValue.setAuthorizationRequiredElement(authorizationRequired.toHapi())
    }
    if (authorizationSupportingCount > 0) {
      hapiValue.setAuthorizationSupporting(authorizationSupportingList.map { it.toHapi() })
    }
    if (hasAuthorizationUrl()) {
      hapiValue.setAuthorizationUrlElement(authorizationUrl.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.Benefit.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.BenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.BenefitComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasAllowed()) {
      hapiValue.setAllowed(allowed.coverageEligibilityResponseInsuranceItemBenefitAllowedToHapi())
    }
    if (hasUsed()) {
      hapiValue.setUsed(used.coverageEligibilityResponseInsuranceItemBenefitUsedToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Errors.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.ErrorsComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.ErrorsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    return hapiValue
  }
}
