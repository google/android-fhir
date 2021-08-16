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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object SearchParameterConverter {
  @JvmStatic
  public fun SearchParameter.toHapi(): org.hl7.fhir.r4.model.SearchParameter {
    val hapiValue = org.hl7.fhir.r4.model.SearchParameter()
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
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    if (hasVersion()) {
      hapiValue.setVersionElement(version.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasDerivedFrom()) {
      hapiValue.setDerivedFromElement(derivedFrom.toHapi())
    }
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasExperimental()) {
      hapiValue.setExperimentalElement(experimental.toHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    if (hasPublisher()) {
      hapiValue.setPublisherElement(publisher.toHapi())
    }
    if (contactCount > 0) {
      hapiValue.setContact(contactList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (useContextCount > 0) {
      hapiValue.setUseContext(useContextList.map { it.toHapi() })
    }
    if (jurisdictionCount > 0) {
      hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    }
    if (hasPurpose()) {
      hapiValue.setPurposeElement(purpose.toHapi())
    }
    if (hasCode()) {
      hapiValue.setCodeElement(code.toHapi())
    }
    baseList.forEach { hapiValue.addBase(it.value.name.hapiCodeCheck()) }
    hapiValue.setType(
      Enumerations.SearchParamType.valueOf(type.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasExpression()) {
      hapiValue.setExpressionElement(expression.toHapi())
    }
    if (hasXpath()) {
      hapiValue.setXpathElement(xpath.toHapi())
    }
    hapiValue.setXpathUsage(
      org.hl7.fhir.r4.model.SearchParameter.XPathUsageType.valueOf(
        xpathUsage.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    targetList.forEach { hapiValue.addTarget(it.value.name.hapiCodeCheck()) }
    if (hasMultipleOr()) {
      hapiValue.setMultipleOrElement(multipleOr.toHapi())
    }
    if (hasMultipleAnd()) {
      hapiValue.setMultipleAndElement(multipleAnd.toHapi())
    }
    comparatorList.forEach {
      hapiValue.addComparator(
        org.hl7.fhir.r4.model.SearchParameter.SearchComparator.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    modifierList.forEach {
      hapiValue.addModifier(
        org.hl7.fhir.r4.model.SearchParameter.SearchModifierCode.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (chainCount > 0) {
      hapiValue.setChain(chainList.map { it.toHapi() })
    }
    if (componentCount > 0) {
      hapiValue.setComponent(componentList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SearchParameter.toProto(): SearchParameter {
    val protoValue = SearchParameter.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    if (hasVersion()) {
      protoValue.setVersion(versionElement.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasDerivedFrom()) {
      protoValue.setDerivedFrom(derivedFromElement.toProto())
    }
    protoValue.setStatus(
      SearchParameter.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasExperimental()) {
      protoValue.setExperimental(experimentalElement.toProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    if (hasPublisher()) {
      protoValue.setPublisher(publisherElement.toProto())
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasPurpose()) {
      protoValue.setPurpose(purposeElement.toProto())
    }
    if (hasCode()) {
      protoValue.setCode(codeElement.toProto())
    }
    protoValue.addAllBase(
      base.map {
        SearchParameter.BaseCode.newBuilder()
          .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString.protoCodeCheck()))
          .build()
      }
    )
    protoValue.setType(
      SearchParameter.TypeCode.newBuilder()
        .setValue(
          SearchParamTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasExpression()) {
      protoValue.setExpression(expressionElement.toProto())
    }
    if (hasXpath()) {
      protoValue.setXpath(xpathElement.toProto())
    }
    protoValue.setXpathUsage(
      SearchParameter.XpathUsageCode.newBuilder()
        .setValue(
          XPathUsageTypeCode.Value.valueOf(
            xpathUsage.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.addAllTarget(
      target.map {
        SearchParameter.TargetCode.newBuilder()
          .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString.protoCodeCheck()))
          .build()
      }
    )
    if (hasMultipleOr()) {
      protoValue.setMultipleOr(multipleOrElement.toProto())
    }
    if (hasMultipleAnd()) {
      protoValue.setMultipleAnd(multipleAndElement.toProto())
    }
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
    if (hasChain()) {
      protoValue.addAllChain(chain.map { it.toProto() })
    }
    if (hasComponent()) {
      protoValue.addAllComponent(component.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent.toProto():
    SearchParameter.Component {
    val protoValue = SearchParameter.Component.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDefinition()) {
      protoValue.setDefinition(definitionElement.toProto())
    }
    if (hasExpression()) {
      protoValue.setExpression(expressionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SearchParameter.Component.toHapi():
    org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDefinition()) {
      hapiValue.setDefinitionElement(definition.toHapi())
    }
    if (hasExpression()) {
      hapiValue.setExpressionElement(expression.toHapi())
    }
    return hapiValue
  }
}
