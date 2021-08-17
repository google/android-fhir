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
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.SearchComparatorCode
import com.google.fhir.r4.core.SearchModifierCode
import com.google.fhir.r4.core.SearchParamTypeCode
import com.google.fhir.r4.core.SearchParameter
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.XPathUsageTypeCode
import org.hl7.fhir.r4.model.Enumerations

object SearchParameterConverter {
  fun SearchParameter.toHapi(): org.hl7.fhir.r4.model.SearchParameter {
    val hapiValue = org.hl7.fhir.r4.model.SearchParameter()
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
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDerivedFrom()) {
      hapiValue.derivedFromElement = derivedFrom.toHapi()
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
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (baseCount > 0) {
      baseList.forEach { hapiValue.addBase(it.value.name.hapiCodeCheck()) }
    }
    if (hasType()) {
      hapiValue.type =
        Enumerations.SearchParamType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    }
    if (hasExpression()) {
      hapiValue.expressionElement = expression.toHapi()
    }
    if (hasXpath()) {
      hapiValue.xpathElement = xpath.toHapi()
    }
    if (hasXpathUsage()) {
      hapiValue.xpathUsage =
        org.hl7.fhir.r4.model.SearchParameter.XPathUsageType.valueOf(
          xpathUsage.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (targetCount > 0) {
      targetList.forEach { hapiValue.addTarget(it.value.name.hapiCodeCheck()) }
    }
    if (hasMultipleOr()) {
      hapiValue.multipleOrElement = multipleOr.toHapi()
    }
    if (hasMultipleAnd()) {
      hapiValue.multipleAndElement = multipleAnd.toHapi()
    }
    if (comparatorCount > 0) {
      comparatorList.forEach {
        hapiValue.addComparator(
          org.hl7.fhir.r4.model.SearchParameter.SearchComparator.valueOf(
            it.value.name.hapiCodeCheck().replace("_", "")
          )
        )
      }
    }
    if (modifierCount > 0) {
      modifierList.forEach {
        hapiValue.addModifier(
          org.hl7.fhir.r4.model.SearchParameter.SearchModifierCode.valueOf(
            it.value.name.hapiCodeCheck().replace("_", "")
          )
        )
      }
    }
    if (chainCount > 0) {
      hapiValue.chain = chainList.map { it.toHapi() }
    }
    if (componentCount > 0) {
      hapiValue.component = componentList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.SearchParameter.toProto(): SearchParameter {
    val protoValue = SearchParameter.newBuilder()
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
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasDerivedFrom()) {
      protoValue.derivedFrom = derivedFromElement.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        SearchParameter.StatusCode.newBuilder()
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
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasBase()) {
      protoValue.addAllBase(
        base.map {
          SearchParameter.BaseCode.newBuilder()
            .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString.protoCodeCheck()))
            .build()
        }
      )
    }
    if (hasType()) {
      protoValue.type =
        SearchParameter.TypeCode.newBuilder()
          .setValue(
            SearchParamTypeCode.Value.valueOf(
              type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasExpression()) {
      protoValue.expression = expressionElement.toProto()
    }
    if (hasXpath()) {
      protoValue.xpath = xpathElement.toProto()
    }
    if (hasXpathUsage()) {
      protoValue.xpathUsage =
        SearchParameter.XpathUsageCode.newBuilder()
          .setValue(
            XPathUsageTypeCode.Value.valueOf(
              xpathUsage.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasTarget()) {
      protoValue.addAllTarget(
        target.map {
          SearchParameter.TargetCode.newBuilder()
            .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString.protoCodeCheck()))
            .build()
        }
      )
    }
    if (hasMultipleOr()) {
      protoValue.multipleOr = multipleOrElement.toProto()
    }
    if (hasMultipleAnd()) {
      protoValue.multipleAnd = multipleAndElement.toProto()
    }
    if (hasComparator()) {
      protoValue.addAllComparator(
        comparator.map {
          SearchParameter.ComparatorCode.newBuilder()
            .setValue(
              SearchComparatorCode.Value.valueOf(
                it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        }
      )
    }
    if (hasModifier()) {
      protoValue.addAllModifier(
        modifier.map {
          SearchParameter.ModifierCode.newBuilder()
            .setValue(
              SearchModifierCode.Value.valueOf(
                it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        }
      )
    }
    if (hasChain()) {
      protoValue.addAllChain(chain.map { it.toProto() })
    }
    if (hasComponent()) {
      protoValue.addAllComponent(component.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent.toProto():
    SearchParameter.Component {
    val protoValue = SearchParameter.Component.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDefinition()) {
      protoValue.definition = definitionElement.toProto()
    }
    if (hasExpression()) {
      protoValue.expression = expressionElement.toProto()
    }
    return protoValue.build()
  }

  private fun SearchParameter.Component.toHapi():
    org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDefinition()) {
      hapiValue.definitionElement = definition.toHapi()
    }
    if (hasExpression()) {
      hapiValue.expressionElement = expression.toHapi()
    }
    return hapiValue
  }
}
