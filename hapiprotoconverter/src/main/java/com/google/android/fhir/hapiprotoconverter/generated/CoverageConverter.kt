package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.FinancialResourceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

public object CoverageConverter {
  public fun Coverage.CostToBeneficiary.ValueX.coverageCostToBeneficiaryValueToHapi(): Type {
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getMoney() != Money.newBuilder().defaultInstanceForType ) {
      return (this.getMoney()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Coverage.costToBeneficiary.value[x]")
  }

  public fun Type.coverageCostToBeneficiaryValueToProto(): Coverage.CostToBeneficiary.ValueX {
    val protoValue = Coverage.CostToBeneficiary.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Money) {
      protoValue.setMoney(this.toProto())
    }
    return protoValue.build()
  }

  public fun Coverage.toHapi(): org.hl7.fhir.r4.model.Coverage {
    val hapiValue = org.hl7.fhir.r4.model.Coverage()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Coverage.CoverageStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setType(type.toHapi())
    hapiValue.setPolicyHolder(policyHolder.toHapi())
    hapiValue.setSubscriber(subscriber.toHapi())
    hapiValue.setSubscriberIdElement(subscriberId.toHapi())
    hapiValue.setBeneficiary(beneficiary.toHapi())
    hapiValue.setDependentElement(dependent.toHapi())
    hapiValue.setRelationship(relationship.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setPayor(payorList.map{it.toHapi()})
    hapiValue.setClass(classList.map{it.toHapi()})
    hapiValue.setOrderElement(order.toHapi())
    hapiValue.setNetworkElement(network.toHapi())
    hapiValue.setCostToBeneficiary(costToBeneficiaryList.map{it.toHapi()})
    hapiValue.setSubrogationElement(subrogation.toHapi())
    hapiValue.setContract(contractList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Coverage.toProto(): Coverage {
    val protoValue = Coverage.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(Coverage.StatusCode.newBuilder().setValue(FinancialResourceStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setType(type.toProto())
    .setPolicyHolder(policyHolder.toProto())
    .setSubscriber(subscriber.toProto())
    .setSubscriberId(subscriberIdElement.toProto())
    .setBeneficiary(beneficiary.toProto())
    .setDependent(dependentElement.toProto())
    .setRelationship(relationship.toProto())
    .setPeriod(period.toProto())
    .addAllPayor(payor.map{it.toProto()})
    .addAllClass(class.map{it.toProto()})
    .setOrder(orderElement.toProto())
    .setNetwork(networkElement.toProto())
    .addAllCostToBeneficiary(costToBeneficiary.map{it.toProto()})
    .setSubrogation(subrogationElement.toProto())
    .addAllContract(contract.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Coverage.ClassComponent.toProto(): Coverage.Class {
    val protoValue = Coverage.Class.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setValue(valueElement.toProto())
    .setName(nameElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Coverage.CostToBeneficiaryComponent.toProto():
      Coverage.CostToBeneficiary {
    val protoValue = Coverage.CostToBeneficiary.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setValue(value.coverageCostToBeneficiaryValueToProto())
    .addAllExemption(exemption.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Coverage.ExemptionComponent.toProto():
      Coverage.CostToBeneficiary.Exemption {
    val protoValue = Coverage.CostToBeneficiary.Exemption.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun Coverage.Class.toHapi(): org.hl7.fhir.r4.model.Coverage.ClassComponent {
    val hapiValue = org.hl7.fhir.r4.model.Coverage.ClassComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setNameElement(name.toHapi())
    return hapiValue
  }

  public fun Coverage.CostToBeneficiary.toHapi():
      org.hl7.fhir.r4.model.Coverage.CostToBeneficiaryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Coverage.CostToBeneficiaryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setValue(value.coverageCostToBeneficiaryValueToHapi())
    hapiValue.setExemption(exemptionList.map{it.toHapi()})
    return hapiValue
  }

  public fun Coverage.CostToBeneficiary.Exemption.toHapi():
      org.hl7.fhir.r4.model.Coverage.ExemptionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Coverage.ExemptionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }
}
