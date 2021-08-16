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

object CoverageEligibilityResponseConverter {
  @JvmStatic
  private fun CoverageEligibilityResponse.ServicedX.coverageEligibilityResponseServicedToHapi():
    Type {
    if (this.date != Date.newBuilder().defaultInstanceForType) {
      return (this.date).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for CoverageEligibilityResponse.serviced[x]")
  }

  @JvmStatic
  private fun Type.coverageEligibilityResponseServicedToProto():
    CoverageEligibilityResponse.ServicedX {
    val protoValue = CoverageEligibilityResponse.ServicedX.newBuilder()
    if (this is DateType) {
        protoValue.date = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.Benefit.AllowedX.coverageEligibilityResponseInsuranceItemBenefitAllowedToHapi():
    Type {
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.money != Money.newBuilder().defaultInstanceForType) {
      return (this.money).toHapi()
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
        protoValue.unsignedInt = this.toProto()
    }
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Money) {
        protoValue.money = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.Benefit.UsedX.coverageEligibilityResponseInsuranceItemBenefitUsedToHapi():
    Type {
    if (this.unsignedInt != UnsignedInt.newBuilder().defaultInstanceForType) {
      return (this.unsignedInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.money != Money.newBuilder().defaultInstanceForType) {
      return (this.money).toHapi()
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
        protoValue.unsignedInt = this.toProto()
    }
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Money) {
        protoValue.money = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun CoverageEligibilityResponse.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse()
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
      hapiValue.status = org.hl7.fhir.r4.model.CoverageEligibilityResponse.EligibilityResponseStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    purposeList.forEach {
      hapiValue.addPurpose(
        org.hl7.fhir.r4.model.CoverageEligibilityResponse.EligibilityResponsePurpose.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasPatient()) {
        hapiValue.patient = patient.toHapi()
    }
    if (hasServiced()) {
        hapiValue.serviced = serviced.coverageEligibilityResponseServicedToHapi()
    }
    if (hasCreated()) {
        hapiValue.createdElement = created.toHapi()
    }
    if (hasRequestor()) {
        hapiValue.requestor = requestor.toHapi()
    }
    if (hasRequest()) {
        hapiValue.request = request.toHapi()
    }
      hapiValue.outcome =
          Enumerations.RemittanceOutcome.valueOf(outcome.value.name.hapiCodeCheck().replace("_", ""))
    if (hasDisposition()) {
        hapiValue.dispositionElement = disposition.toHapi()
    }
    if (hasInsurer()) {
        hapiValue.insurer = insurer.toHapi()
    }
    if (insuranceCount > 0) {
        hapiValue.insurance = insuranceList.map { it.toHapi() }
    }
    if (hasPreAuthRef()) {
        hapiValue.preAuthRefElement = preAuthRef.toHapi()
    }
    if (hasForm()) {
        hapiValue.form = form.toHapi()
    }
    if (errorCount > 0) {
        hapiValue.error = errorList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.CoverageEligibilityResponse.toProto():
    CoverageEligibilityResponse {
    val protoValue = CoverageEligibilityResponse.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = CoverageEligibilityResponse.StatusCode.newBuilder()
          .setValue(
              FinancialResourceStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
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
        protoValue.patient = patient.toProto()
    }
    if (hasServiced()) {
        protoValue.serviced = serviced.coverageEligibilityResponseServicedToProto()
    }
    if (hasCreated()) {
        protoValue.created = createdElement.toProto()
    }
    if (hasRequestor()) {
        protoValue.requestor = requestor.toProto()
    }
    if (hasRequest()) {
        protoValue.request = request.toProto()
    }
      protoValue.outcome = CoverageEligibilityResponse.OutcomeCode.newBuilder()
          .setValue(
              ClaimProcessingCode.Value.valueOf(
                  outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasDisposition()) {
        protoValue.disposition = dispositionElement.toProto()
    }
    if (hasInsurer()) {
        protoValue.insurer = insurer.toProto()
    }
    if (hasInsurance()) {
      protoValue.addAllInsurance(insurance.map { it.toProto() })
    }
    if (hasPreAuthRef()) {
        protoValue.preAuthRef = preAuthRefElement.toProto()
    }
    if (hasForm()) {
        protoValue.form = form.toProto()
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
        protoValue.coverage = coverage.toProto()
    }
    if (hasInforce()) {
        protoValue.inforce = inforceElement.toProto()
    }
    if (hasBenefitPeriod()) {
        protoValue.benefitPeriod = benefitPeriod.toProto()
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
    if (hasExcluded()) {
        protoValue.excluded = excludedElement.toProto()
    }
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasNetwork()) {
        protoValue.network = network.toProto()
    }
    if (hasUnit()) {
        protoValue.unit = unit.toProto()
    }
    if (hasTerm()) {
        protoValue.term = term.toProto()
    }
    if (hasBenefit()) {
      protoValue.addAllBenefit(benefit.map { it.toProto() })
    }
    if (hasAuthorizationRequired()) {
        protoValue.authorizationRequired = authorizationRequiredElement.toProto()
    }
    if (hasAuthorizationSupporting()) {
      protoValue.addAllAuthorizationSupporting(authorizationSupporting.map { it.toProto() })
    }
    if (hasAuthorizationUrl()) {
        protoValue.authorizationUrl = authorizationUrlElement.toProto()
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
        protoValue.type = type.toProto()
    }
    if (hasAllowed()) {
        protoValue.allowed = allowed.coverageEligibilityResponseInsuranceItemBenefitAllowedToProto()
    }
    if (hasUsed()) {
        protoValue.used = used.coverageEligibilityResponseInsuranceItemBenefitUsedToProto()
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
        protoValue.code = code.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.InsuranceComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.InsuranceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCoverage()) {
        hapiValue.coverage = coverage.toHapi()
    }
    if (hasInforce()) {
        hapiValue.inforceElement = inforce.toHapi()
    }
    if (hasBenefitPeriod()) {
        hapiValue.benefitPeriod = benefitPeriod.toHapi()
    }
    if (itemCount > 0) {
        hapiValue.item = itemList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.ItemsComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.ItemsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
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
    if (hasExcluded()) {
        hapiValue.excludedElement = excluded.toHapi()
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (hasNetwork()) {
        hapiValue.network = network.toHapi()
    }
    if (hasUnit()) {
        hapiValue.unit = unit.toHapi()
    }
    if (hasTerm()) {
        hapiValue.term = term.toHapi()
    }
    if (benefitCount > 0) {
        hapiValue.benefit = benefitList.map { it.toHapi() }
    }
    if (hasAuthorizationRequired()) {
        hapiValue.authorizationRequiredElement = authorizationRequired.toHapi()
    }
    if (authorizationSupportingCount > 0) {
        hapiValue.authorizationSupporting = authorizationSupportingList.map { it.toHapi() }
    }
    if (hasAuthorizationUrl()) {
        hapiValue.authorizationUrlElement = authorizationUrl.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Insurance.Items.Benefit.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.BenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.BenefitComponent()
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
    if (hasAllowed()) {
        hapiValue.allowed = allowed.coverageEligibilityResponseInsuranceItemBenefitAllowedToHapi()
    }
    if (hasUsed()) {
        hapiValue.used = used.coverageEligibilityResponseInsuranceItemBenefitUsedToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun CoverageEligibilityResponse.Errors.toHapi():
    org.hl7.fhir.r4.model.CoverageEligibilityResponse.ErrorsComponent {
    val hapiValue = org.hl7.fhir.r4.model.CoverageEligibilityResponse.ErrorsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
        hapiValue.code = code.toHapi()
    }
    return hapiValue
  }
}
