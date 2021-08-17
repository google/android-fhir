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
import org.hl7.fhir.r4.model.Enumerations

object InsurancePlanConverter {
  fun InsurancePlan.toHapi(): org.hl7.fhir.r4.model.InsurancePlan {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (hasStatus()) {
      hapiValue.status =
        Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (aliasCount > 0) {
      hapiValue.alias = aliasList.map { it.toHapi() }
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (hasOwnedBy()) {
      hapiValue.ownedBy = ownedBy.toHapi()
    }
    if (hasAdministeredBy()) {
      hapiValue.administeredBy = administeredBy.toHapi()
    }
    if (coverageAreaCount > 0) {
      hapiValue.coverageArea = coverageAreaList.map { it.toHapi() }
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (endpointCount > 0) {
      hapiValue.endpoint = endpointList.map { it.toHapi() }
    }
    if (networkCount > 0) {
      hapiValue.network = networkList.map { it.toHapi() }
    }
    if (coverageCount > 0) {
      hapiValue.coverage = coverageList.map { it.toHapi() }
    }
    if (planCount > 0) {
      hapiValue.plan = planList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.InsurancePlan.toProto(): InsurancePlan {
    val protoValue = InsurancePlan.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasStatus()) {
      protoValue.status =
        InsurancePlan.StatusCode.newBuilder()
          .setValue(
            PublicationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasOwnedBy()) {
      protoValue.ownedBy = ownedBy.toProto()
    }
    if (hasAdministeredBy()) {
      protoValue.administeredBy = administeredBy.toProto()
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

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent.toProto():
    InsurancePlan.Contact {
    val protoValue = InsurancePlan.Contact.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.purpose = purpose.toProto()
    }
    if (hasName()) {
      protoValue.name = name.toProto()
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasAddress()) {
      protoValue.address = address.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent.toProto():
    InsurancePlan.Coverage {
    val protoValue = InsurancePlan.Coverage.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasNetwork()) {
      protoValue.addAllNetwork(network.map { it.toProto() })
    }
    if (hasBenefit()) {
      protoValue.addAllBenefit(benefit.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent.toProto():
    InsurancePlan.Coverage.CoverageBenefit {
    val protoValue = InsurancePlan.Coverage.CoverageBenefit.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasRequirement()) {
      protoValue.requirement = requirementElement.toProto()
    }
    if (hasLimit()) {
      protoValue.addAllLimit(limit.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent.toProto():
    InsurancePlan.Coverage.CoverageBenefit.Limit {
    val protoValue = InsurancePlan.Coverage.CoverageBenefit.Limit.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.value = value.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent.toProto():
    InsurancePlan.Plan {
    val protoValue = InsurancePlan.Plan.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
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
    if (hasType()) {
      protoValue.type = type.toProto()
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

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent.toProto():
    InsurancePlan.Plan.GeneralCost {
    val protoValue = InsurancePlan.Plan.GeneralCost.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasGroupSize()) {
      protoValue.groupSize = groupSizeElement.toProto()
    }
    if (hasCost()) {
      protoValue.cost = cost.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent.toProto():
    InsurancePlan.Plan.SpecificCost {
    val protoValue = InsurancePlan.Plan.SpecificCost.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasBenefit()) {
      protoValue.addAllBenefit(benefit.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent.toProto():
    InsurancePlan.Plan.SpecificCost.PlanBenefit {
    val protoValue = InsurancePlan.Plan.SpecificCost.PlanBenefit.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasCost()) {
      protoValue.addAllCost(cost.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent.toProto():
    InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost {
    val protoValue = InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasApplicability()) {
      protoValue.applicability = applicability.toProto()
    }
    if (hasQualifiers()) {
      protoValue.addAllQualifiers(qualifiers.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.value = value.toProto()
    }
    return protoValue.build()
  }

  private fun InsurancePlan.Contact.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPurpose()) {
      hapiValue.purpose = purpose.toHapi()
    }
    if (hasName()) {
      hapiValue.name = name.toHapi()
    }
    if (telecomCount > 0) {
      hapiValue.telecom = telecomList.map { it.toHapi() }
    }
    if (hasAddress()) {
      hapiValue.address = address.toHapi()
    }
    return hapiValue
  }

  private fun InsurancePlan.Coverage.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (networkCount > 0) {
      hapiValue.network = networkList.map { it.toHapi() }
    }
    if (benefitCount > 0) {
      hapiValue.benefit = benefitList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun InsurancePlan.Coverage.CoverageBenefit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasRequirement()) {
      hapiValue.requirementElement = requirement.toHapi()
    }
    if (limitCount > 0) {
      hapiValue.limit = limitList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun InsurancePlan.Coverage.CoverageBenefit.Limit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasValue()) {
      hapiValue.value = value.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    return hapiValue
  }

  private fun InsurancePlan.Plan.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent()
    if (hasId()) {
      hapiValue.id = id.value
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
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (coverageAreaCount > 0) {
      hapiValue.coverageArea = coverageAreaList.map { it.toHapi() }
    }
    if (networkCount > 0) {
      hapiValue.network = networkList.map { it.toHapi() }
    }
    if (generalCostCount > 0) {
      hapiValue.generalCost = generalCostList.map { it.toHapi() }
    }
    if (specificCostCount > 0) {
      hapiValue.specificCost = specificCostList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun InsurancePlan.Plan.GeneralCost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasGroupSize()) {
      hapiValue.groupSizeElement = groupSize.toHapi()
    }
    if (hasCost()) {
      hapiValue.cost = cost.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    return hapiValue
  }

  private fun InsurancePlan.Plan.SpecificCost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (benefitCount > 0) {
      hapiValue.benefit = benefitList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun InsurancePlan.Plan.SpecificCost.PlanBenefit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (costCount > 0) {
      hapiValue.cost = costList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasApplicability()) {
      hapiValue.applicability = applicability.toHapi()
    }
    if (qualifiersCount > 0) {
      hapiValue.qualifiers = qualifiersList.map { it.toHapi() }
    }
    if (hasValue()) {
      hapiValue.value = value.toHapi()
    }
    return hapiValue
  }
}
