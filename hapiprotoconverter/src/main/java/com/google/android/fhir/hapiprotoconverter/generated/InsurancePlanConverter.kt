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

public object InsurancePlanConverter {
  public fun InsurancePlan.toHapi(): org.hl7.fhir.r4.model.InsurancePlan {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setAlias(aliasList.map { it.toHapi() })
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setOwnedBy(ownedBy.toHapi())
    hapiValue.setAdministeredBy(administeredBy.toHapi())
    hapiValue.setCoverageArea(coverageAreaList.map { it.toHapi() })
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    hapiValue.setNetwork(networkList.map { it.toHapi() })
    hapiValue.setCoverage(coverageList.map { it.toHapi() })
    hapiValue.setPlan(planList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.InsurancePlan.toProto(): InsurancePlan {
    val protoValue =
      InsurancePlan.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          InsurancePlan.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .addAllType(type.map { it.toProto() })
        .setName(nameElement.toProto())
        .addAllAlias(alias.map { it.toProto() })
        .setPeriod(period.toProto())
        .setOwnedBy(ownedBy.toProto())
        .setAdministeredBy(administeredBy.toProto())
        .addAllCoverageArea(coverageArea.map { it.toProto() })
        .addAllContact(contact.map { it.toProto() })
        .addAllEndpoint(endpoint.map { it.toProto() })
        .addAllNetwork(network.map { it.toProto() })
        .addAllCoverage(coverage.map { it.toProto() })
        .addAllPlan(plan.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent.toProto():
    InsurancePlan.Contact {
    val protoValue =
      InsurancePlan.Contact.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPurpose(purpose.toProto())
        .setName(name.toProto())
        .addAllTelecom(telecom.map { it.toProto() })
        .setAddress(address.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent.toProto():
    InsurancePlan.Coverage {
    val protoValue =
      InsurancePlan.Coverage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .addAllNetwork(network.map { it.toProto() })
        .addAllBenefit(benefit.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent.toProto():
    InsurancePlan.Coverage.CoverageBenefit {
    val protoValue =
      InsurancePlan.Coverage.CoverageBenefit.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setRequirement(requirementElement.toProto())
        .addAllLimit(limit.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent.toProto():
    InsurancePlan.Coverage.CoverageBenefit.Limit {
    val protoValue =
      InsurancePlan.Coverage.CoverageBenefit.Limit.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setValue(value.toProto())
        .setCode(code.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent.toProto():
    InsurancePlan.Plan {
    val protoValue =
      InsurancePlan.Plan.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setType(type.toProto())
        .addAllCoverageArea(coverageArea.map { it.toProto() })
        .addAllNetwork(network.map { it.toProto() })
        .addAllGeneralCost(generalCost.map { it.toProto() })
        .addAllSpecificCost(specificCost.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent.toProto():
    InsurancePlan.Plan.GeneralCost {
    val protoValue =
      InsurancePlan.Plan.GeneralCost.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setGroupSize(groupSizeElement.toProto())
        .setCost(cost.toProto())
        .setComment(commentElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent.toProto():
    InsurancePlan.Plan.SpecificCost {
    val protoValue =
      InsurancePlan.Plan.SpecificCost.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCategory(category.toProto())
        .addAllBenefit(benefit.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent.toProto():
    InsurancePlan.Plan.SpecificCost.PlanBenefit {
    val protoValue =
      InsurancePlan.Plan.SpecificCost.PlanBenefit.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .addAllCost(cost.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent.toProto():
    InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost {
    val protoValue =
      InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .setApplicability(applicability.toProto())
        .addAllQualifiers(qualifiers.map { it.toProto() })
        .setValue(value.toProto())
        .build()
    return protoValue
  }

  private fun InsurancePlan.Contact.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanContactComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPurpose(purpose.toHapi())
    hapiValue.setName(name.toHapi())
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setAddress(address.toHapi())
    return hapiValue
  }

  private fun InsurancePlan.Coverage.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanCoverageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setNetwork(networkList.map { it.toHapi() })
    hapiValue.setBenefit(benefitList.map { it.toHapi() })
    return hapiValue
  }

  private fun InsurancePlan.Coverage.CoverageBenefit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setRequirementElement(requirement.toHapi())
    hapiValue.setLimit(limitList.map { it.toHapi() })
    return hapiValue
  }

  private fun InsurancePlan.Coverage.CoverageBenefit.Limit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.CoverageBenefitLimitComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setValue(value.toHapi())
    hapiValue.setCode(code.toHapi())
    return hapiValue
  }

  private fun InsurancePlan.Plan.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setCoverageArea(coverageAreaList.map { it.toHapi() })
    hapiValue.setNetwork(networkList.map { it.toHapi() })
    hapiValue.setGeneralCost(generalCostList.map { it.toHapi() })
    hapiValue.setSpecificCost(specificCostList.map { it.toHapi() })
    return hapiValue
  }

  private fun InsurancePlan.Plan.GeneralCost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanGeneralCostComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setGroupSizeElement(groupSize.toHapi())
    hapiValue.setCost(cost.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    return hapiValue
  }

  private fun InsurancePlan.Plan.SpecificCost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.InsurancePlanPlanSpecificCostComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCategory(category.toHapi())
    hapiValue.setBenefit(benefitList.map { it.toHapi() })
    return hapiValue
  }

  private fun InsurancePlan.Plan.SpecificCost.PlanBenefit.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setCost(costList.map { it.toHapi() })
    return hapiValue
  }

  private fun InsurancePlan.Plan.SpecificCost.PlanBenefit.Cost.toHapi():
    org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.InsurancePlan.PlanBenefitCostComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setApplicability(applicability.toHapi())
    hapiValue.setQualifiers(qualifiersList.map { it.toHapi() })
    hapiValue.setValue(value.toHapi())
    return hapiValue
  }
}
