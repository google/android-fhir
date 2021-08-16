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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.HumanNameConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.InsurancePlan
import com.google.fhir.r4.core.InsurancePlan.Coverage
import com.google.fhir.r4.core.InsurancePlan.Coverage.CoverageBenefit
import com.google.fhir.r4.core.InsurancePlan.Plan
import com.google.fhir.r4.core.InsurancePlan.Plan.SpecificCost
import com.google.fhir.r4.core.InsurancePlan.Plan.SpecificCost.PlanBenefit
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object InsurancePlanConverter {
  @JvmStatic
  public fun InsurancePlan.toHapi(): org.hl7.fhir.r4.model.InsurancePlan {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan()
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
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (typeCount > 0) {
      hapiValue.setType(typeList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (aliasCount > 0) {
      hapiValue.setAlias(aliasList.map { it.toHapi() })
    }
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    if (hasOwnedBy()) {
      hapiValue.setOwnedBy(ownedBy.toHapi())
    }
    if (hasAdministeredBy()) {
      hapiValue.setAdministeredBy(administeredBy.toHapi())
    }
    if (coverageAreaCount > 0) {
      hapiValue.setCoverageArea(coverageAreaList.map { it.toHapi() })
    }
    if (contactCount > 0) {
      hapiValue.setContact(contactList.map { it.toHapi() })
    }
    if (endpointCount > 0) {
      hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    }
    if (networkCount > 0) {
      hapiValue.setNetwork(networkList.map { it.toHapi() })
    }
    if (coverageCount > 0) {
      hapiValue.setCoverage(coverageList.map { it.toHapi() })
    }
    if (planCount > 0) {
      hapiValue.setPlan(planList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.InsurancePlan.toProto(): InsurancePlan {
    val protoValue = InsurancePlan.newBuilder().setId(Id.newBuilder().setValue(id))
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
      InsurancePlan.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    if (hasOwnedBy()) {
      protoValue.setOwnedBy(ownedBy.toProto())
    }
    if (hasAdministeredBy()) {
      protoValue.setAdministeredBy(administeredBy.toProto())
    }
    if (hasCoverageArea()) {
      protoValue.addAllCoverageArea(coverageArea.map { it.toProto() })
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasEndpoint()) {
      protoValue.addAllEndpoint(endpoint.map { it.toProto() })
    }
    if (hasNetwork()) {
      protoValue.addAllNetwork(network.map { it.toProto() })
    }
    if (hasCoverage()) {
      protoValue.addAllCoverage(coverage.map { it.toProto() })
    }
    if (hasPlan()) {
      protoValue.addAllPlan(plan.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent.toProto():
    InsurancePlan.Contact {
    val protoValue = InsurancePlan.Contact.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.setPurpose(purpose.toProto())
    }
    if (hasName()) {
      protoValue.setName(name.toProto())
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasAddress()) {
      protoValue.setAddress(address.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent.toProto():
    InsurancePlan.Coverage {
    val protoValue = InsurancePlan.Coverage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasNetwork()) {
      protoValue.addAllNetwork(network.map { it.toProto() })
    }
    if (hasBenefit()) {
      protoValue.addAllBenefit(benefit.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent.toProto():
    InsurancePlan.Coverage.CoverageBenefit {
    val protoValue =
      InsurancePlan.Coverage.CoverageBenefit.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasRequirement()) {
      protoValue.setRequirement(requirementElement.toProto())
    }
    if (hasLimit()) {
      protoValue.addAllLimit(limit.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent.toProto():
    InsurancePlan.Coverage.CoverageBenefit.Limit {
    val protoValue =
      InsurancePlan.Coverage.CoverageBenefit.Limit.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.setValue(value.toProto())
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent.toProto():
    InsurancePlan.Plan {
    val protoValue = InsurancePlan.Plan.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasCoverageArea()) {
      protoValue.addAllCoverageArea(coverageArea.map { it.toProto() })
    }
    if (hasNetwork()) {
      protoValue.addAllNetwork(network.map { it.toProto() })
    }
    if (hasGeneralCost()) {
      protoValue.addAllGeneralCost(generalCost.map { it.toProto() })
    }
    if (hasSpecificCost()) {
      protoValue.addAllSpecificCost(specificCost.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent.toProto():
    InsurancePlan.Plan.GeneralCost {
    val protoValue =
      InsurancePlan.Plan.GeneralCost.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasGroupSize()) {
      protoValue.setGroupSize(groupSizeElement.toProto())
    }
    if (hasCost()) {
      protoValue.setCost(cost.toProto())
    }
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent.toProto():
    InsurancePlan.Plan.SpecificCost {
    val protoValue =
      InsurancePlan.Plan.SpecificCost.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.setCategory(category.toProto())
    }
    if (hasBenefit()) {
      protoValue.addAllBenefit(benefit.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent.toProto():
    InsurancePlan.Plan.SpecificCost.PlanBenefit {
    val protoValue =
      InsurancePlan.Plan.SpecificCost.PlanBenefit.newBuilder()
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
    if (hasCost()) {
      protoValue.addAllCost(cost.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent.toProto():
    InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost {
    val protoValue =
      InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost.newBuilder()
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
    if (hasApplicability()) {
      protoValue.setApplicability(applicability.toProto())
    }
    if (hasQualifiers()) {
      protoValue.addAllQualifiers(qualifiers.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.setValue(value.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun InsurancePlan.Contact.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasPurpose()) {
      hapiValue.setPurpose(purpose.toHapi())
    }
    if (hasName()) {
      hapiValue.setName(name.toHapi())
    }
    if (telecomCount > 0) {
      hapiValue.setTelecom(telecomList.map { it.toHapi() })
    }
    if (hasAddress()) {
      hapiValue.setAddress(address.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Coverage.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent()
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
    if (networkCount > 0) {
      hapiValue.setNetwork(networkList.map { it.toHapi() })
    }
    if (benefitCount > 0) {
      hapiValue.setBenefit(benefitList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Coverage.CoverageBenefit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent()
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
    if (hasRequirement()) {
      hapiValue.setRequirementElement(requirement.toHapi())
    }
    if (limitCount > 0) {
      hapiValue.setLimit(limitList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Coverage.CoverageBenefit.Limit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasValue()) {
      hapiValue.setValue(value.toHapi())
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Plan.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (coverageAreaCount > 0) {
      hapiValue.setCoverageArea(coverageAreaList.map { it.toHapi() })
    }
    if (networkCount > 0) {
      hapiValue.setNetwork(networkList.map { it.toHapi() })
    }
    if (generalCostCount > 0) {
      hapiValue.setGeneralCost(generalCostList.map { it.toHapi() })
    }
    if (specificCostCount > 0) {
      hapiValue.setSpecificCost(specificCostList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Plan.GeneralCost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent()
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
    if (hasGroupSize()) {
      hapiValue.setGroupSizeElement(groupSize.toHapi())
    }
    if (hasCost()) {
      hapiValue.setCost(cost.toHapi())
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Plan.SpecificCost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent()
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
    if (benefitCount > 0) {
      hapiValue.setBenefit(benefitList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Plan.SpecificCost.PlanBenefit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent()
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
    if (costCount > 0) {
      hapiValue.setCost(costList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent()
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
    if (hasApplicability()) {
      hapiValue.setApplicability(applicability.toHapi())
    }
    if (qualifiersCount > 0) {
      hapiValue.setQualifiers(qualifiersList.map { it.toHapi() })
    }
    if (hasValue()) {
      hapiValue.setValue(value.toHapi())
    }
    return hapiValue
  }
}
