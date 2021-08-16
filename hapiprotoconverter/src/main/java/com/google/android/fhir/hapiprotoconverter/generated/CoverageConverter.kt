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
import com.google.fhir.r4.core.Coverage
import com.google.fhir.r4.core.Coverage.CostToBeneficiary
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Type

object CoverageConverter {
  @JvmStatic
  private fun Coverage.CostToBeneficiary.ValueX.coverageCostToBeneficiaryValueToHapi(): Type {
    if (this.quantity != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.money != Money.newBuilder().defaultInstanceForType) {
      return (this.money).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Coverage.costToBeneficiary.value[x]")
  }

  @JvmStatic
  private fun Type.coverageCostToBeneficiaryValueToProto(): Coverage.CostToBeneficiary.ValueX {
    val protoValue = Coverage.CostToBeneficiary.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
        protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Money) {
        protoValue.money = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Coverage.toHapi(): org.hl7.fhir.r4.model.Coverage {
    val hapiValue = org.hl7.fhir.r4.model.Coverage()
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
      hapiValue.status = org.hl7.fhir.r4.model.Coverage.CoverageStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasPolicyHolder()) {
        hapiValue.policyHolder = policyHolder.toHapi()
    }
    if (hasSubscriber()) {
        hapiValue.subscriber = subscriber.toHapi()
    }
    if (hasSubscriberId()) {
        hapiValue.subscriberIdElement = subscriberId.toHapi()
    }
    if (hasBeneficiary()) {
        hapiValue.beneficiary = beneficiary.toHapi()
    }
    if (hasDependent()) {
        hapiValue.dependentElement = dependent.toHapi()
    }
    if (hasRelationship()) {
        hapiValue.relationship = relationship.toHapi()
    }
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (payorCount > 0) {
        hapiValue.payor = payorList.map { it.toHapi() }
    }
    if (classValueCount > 0) {
        hapiValue.class_ = classValueList.map { it.toHapi() }
    }
    if (hasOrder()) {
        hapiValue.orderElement = order.toHapi()
    }
    if (hasNetwork()) {
        hapiValue.networkElement = network.toHapi()
    }
    if (costToBeneficiaryCount > 0) {
        hapiValue.costToBeneficiary = costToBeneficiaryList.map { it.toHapi() }
    }
    if (hasSubrogation()) {
        hapiValue.subrogationElement = subrogation.toHapi()
    }
    if (contractCount > 0) {
        hapiValue.contract = contractList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Coverage.toProto(): Coverage {
    val protoValue = Coverage.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = Coverage.StatusCode.newBuilder()
          .setValue(
              FinancialResourceStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasPolicyHolder()) {
        protoValue.policyHolder = policyHolder.toProto()
    }
    if (hasSubscriber()) {
        protoValue.subscriber = subscriber.toProto()
    }
    if (hasSubscriberId()) {
        protoValue.subscriberId = subscriberIdElement.toProto()
    }
    if (hasBeneficiary()) {
        protoValue.beneficiary = beneficiary.toProto()
    }
    if (hasDependent()) {
        protoValue.dependent = dependentElement.toProto()
    }
    if (hasRelationship()) {
        protoValue.relationship = relationship.toProto()
    }
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasPayor()) {
      protoValue.addAllPayor(payor.map { it.toProto() })
    }
    if (hasClass_()) {
      protoValue.addAllClassValue(class_.map { it.toProto() })
    }
    if (hasOrder()) {
        protoValue.order = orderElement.toProto()
    }
    if (hasNetwork()) {
        protoValue.network = networkElement.toProto()
    }
    if (hasCostToBeneficiary()) {
      protoValue.addAllCostToBeneficiary(costToBeneficiary.map { it.toProto() })
    }
    if (hasSubrogation()) {
        protoValue.subrogation = subrogationElement.toProto()
    }
    if (hasContract()) {
      protoValue.addAllContract(contract.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Coverage.ClassComponent.toProto(): Coverage.Class {
    val protoValue = Coverage.Class.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasValue()) {
        protoValue.value = valueElement.toProto()
    }
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Coverage.CostToBeneficiaryComponent.toProto():
    Coverage.CostToBeneficiary {
    val protoValue = Coverage.CostToBeneficiary.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasValue()) {
        protoValue.value = value.coverageCostToBeneficiaryValueToProto()
    }
    if (hasException()) {
      protoValue.addAllException(exception.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Coverage.ExemptionComponent.toProto():
    Coverage.CostToBeneficiary.Exemption {
    val protoValue =
      Coverage.CostToBeneficiary.Exemption.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Coverage.Class.toHapi(): org.hl7.fhir.r4.model.Coverage.ClassComponent {
    val hapiValue = org.hl7.fhir.r4.model.Coverage.ClassComponent()
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
    if (hasValue()) {
        hapiValue.valueElement = value.toHapi()
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Coverage.CostToBeneficiary.toHapi():
    org.hl7.fhir.r4.model.Coverage.CostToBeneficiaryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Coverage.CostToBeneficiaryComponent()
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
    if (hasValue()) {
        hapiValue.value = value.coverageCostToBeneficiaryValueToHapi()
    }
    if (exceptionCount > 0) {
        hapiValue.exception = exceptionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun Coverage.CostToBeneficiary.Exemption.toHapi():
    org.hl7.fhir.r4.model.Coverage.ExemptionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Coverage.ExemptionComponent()
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
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    return hapiValue
  }
}
