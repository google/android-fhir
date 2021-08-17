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
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.ChargeItemDefinition
import com.google.fhir.r4.core.ChargeItemDefinition.PropertyGroup
import com.google.fhir.r4.core.ChargeItemDefinition.PropertyGroup.PriceComponent
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.InvoicePriceComponentTypeCode
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Enumerations

object ChargeItemDefinitionConverter {
  fun ChargeItemDefinition.toHapi(): org.hl7.fhir.r4.model.ChargeItemDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ChargeItemDefinition()
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
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (derivedFromUriCount > 0) {
      hapiValue.derivedFromUri = derivedFromUriList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    if (replacesCount > 0) {
      hapiValue.replaces = replacesList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (hasExperimental()) {
      hapiValue.experimentalElement = experimental.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasPublisher()) {
      hapiValue.publisherElement = publisher.toHapi()
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (useContextCount > 0) {
      hapiValue.useContext = useContextList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasApprovalDate()) {
      hapiValue.approvalDateElement = approvalDate.toHapi()
    }
    if (hasLastReviewDate()) {
      hapiValue.lastReviewDateElement = lastReviewDate.toHapi()
    }
    if (hasEffectivePeriod()) {
      hapiValue.effectivePeriod = effectivePeriod.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (instanceCount > 0) {
      hapiValue.instance = instanceList.map { it.toHapi() }
    }
    if (applicabilityCount > 0) {
      hapiValue.applicability = applicabilityList.map { it.toHapi() }
    }
    if (propertyGroupCount > 0) {
      hapiValue.propertyGroup = propertyGroupList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ChargeItemDefinition.toProto(): ChargeItemDefinition {
    val protoValue = ChargeItemDefinition.newBuilder()
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
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasDerivedFromUri()) {
      protoValue.addAllDerivedFromUri(derivedFromUri.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    if (hasReplaces()) {
      protoValue.addAllReplaces(replaces.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        ChargeItemDefinition.StatusCode.newBuilder()
          .setValue(
            PublicationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasExperimental()) {
      protoValue.experimental = experimentalElement.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasPublisher()) {
      protoValue.publisher = publisherElement.toProto()
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasApprovalDate()) {
      protoValue.approvalDate = approvalDateElement.toProto()
    }
    if (hasLastReviewDate()) {
      protoValue.lastReviewDate = lastReviewDateElement.toProto()
    }
    if (hasEffectivePeriod()) {
      protoValue.effectivePeriod = effectivePeriod.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasInstance()) {
      protoValue.addAllInstance(instance.map { it.toProto() })
    }
    if (hasApplicability()) {
      protoValue.addAllApplicability(applicability.map { it.toProto() })
    }
    if (hasPropertyGroup()) {
      protoValue.addAllPropertyGroup(propertyGroup.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionApplicabilityComponent.toProto():
    ChargeItemDefinition.Applicability {
    val protoValue = ChargeItemDefinition.Applicability.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasLanguage()) {
      protoValue.language = languageElement.toProto()
    }
    if (hasExpression()) {
      protoValue.expression = expressionElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupComponent.toProto():
    ChargeItemDefinition.PropertyGroup {
    val protoValue = ChargeItemDefinition.PropertyGroup.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPriceComponent()) {
      protoValue.addAllPriceComponent(priceComponent.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupPriceComponentComponent.toProto():
    ChargeItemDefinition.PropertyGroup.PriceComponent {
    val protoValue = ChargeItemDefinition.PropertyGroup.PriceComponent.newBuilder()
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
      protoValue.type =
        ChargeItemDefinition.PropertyGroup.PriceComponent.TypeCode.newBuilder()
          .setValue(
            InvoicePriceComponentTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.toProto()
    }
    return protoValue.build()
  }

  private fun ChargeItemDefinition.Applicability.toHapi():
    org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionApplicabilityComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionApplicabilityComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasLanguage()) {
      hapiValue.languageElement = language.toHapi()
    }
    if (hasExpression()) {
      hapiValue.expressionElement = expression.toHapi()
    }
    return hapiValue
  }

  private fun ChargeItemDefinition.PropertyGroup.toHapi():
    org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (priceComponentCount > 0) {
      hapiValue.priceComponent = priceComponentList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ChargeItemDefinition.PropertyGroup.PriceComponent.toHapi():
    org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPropertyGroupPriceComponentComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ChargeItemDefinition
        .ChargeItemDefinitionPropertyGroupPriceComponentComponent()
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
      hapiValue.type =
        org.hl7.fhir.r4.model.ChargeItemDefinition.ChargeItemDefinitionPriceComponentType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    return hapiValue
  }
}
