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
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDerivedFromElement(derivedFrom.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCodeElement(code.toHapi())
    baseList.forEach { hapiValue.addBase(it.value.name) }
    hapiValue.setType(Enumerations.SearchParamType.valueOf(type.value.name.replace("_", "")))
    hapiValue.setExpressionElement(expression.toHapi())
    hapiValue.setXpathElement(xpath.toHapi())
    hapiValue.setXpathUsage(
      org.hl7.fhir.r4.model.SearchParameter.XPathUsageType.valueOf(
        xpathUsage.value.name.replace("_", "")
      )
    )
    targetList.forEach { hapiValue.addTarget(it.value.name) }
    hapiValue.setMultipleOrElement(multipleOr.toHapi())
    hapiValue.setMultipleAndElement(multipleAnd.toHapi())
    comparatorList.forEach {
      hapiValue.addComparator(
        org.hl7.fhir.r4.model.SearchParameter.SearchComparator.valueOf(
          it.value.name.replace("_", "")
        )
      )
    }
    modifierList.forEach {
      hapiValue.addModifier(
        org.hl7.fhir.r4.model.SearchParameter.SearchModifierCode.valueOf(
          it.value.name.replace("_", "")
        )
      )
    }
    hapiValue.setChain(chainList.map { it.toHapi() })
    hapiValue.setComponent(componentList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SearchParameter.toProto(): SearchParameter {
    val protoValue =
      SearchParameter.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setDerivedFrom(derivedFromElement.toProto())
        .setStatus(
          SearchParameter.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllUseContext(useContext.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setPurpose(purposeElement.toProto())
        .setCode(codeElement.toProto())
        .addAllBase(
          base.map {
            SearchParameter.BaseCode.newBuilder()
              .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString))
              .build()
          }
        )
        .setType(
          SearchParameter.TypeCode.newBuilder()
            .setValue(
              SearchParamTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExpression(expressionElement.toProto())
        .setXpath(xpathElement.toProto())
        .setXpathUsage(
          SearchParameter.XpathUsageCode.newBuilder()
            .setValue(
              XPathUsageTypeCode.Value.valueOf(xpathUsage.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .addAllTarget(
          target.map {
            SearchParameter.TargetCode.newBuilder()
              .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString))
              .build()
          }
        )
        .setMultipleOr(multipleOrElement.toProto())
        .setMultipleAnd(multipleAndElement.toProto())
        .addAllComparator(
          comparator.map {
            SearchParameter.ComparatorCode.newBuilder()
              .setValue(
                SearchComparatorCode.Value.valueOf(
                  it.value.toCode().replace("-", "_").toUpperCase()
                )
              )
              .build()
          }
        )
        .addAllModifier(
          modifier.map {
            SearchParameter.ModifierCode.newBuilder()
              .setValue(
                SearchModifierCode.Value.valueOf(it.value.toCode().replace("-", "_").toUpperCase())
              )
              .build()
          }
        )
        .addAllChain(chain.map { it.toProto() })
        .addAllComponent(component.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent.toProto():
    SearchParameter.Component {
    val protoValue =
      SearchParameter.Component.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDefinition(definitionElement.toProto())
        .setExpression(expressionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun SearchParameter.Component.toHapi():
    org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.SearchParameter.SearchParameterComponentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDefinitionElement(definition.toHapi())
    hapiValue.setExpressionElement(expression.toHapi())
    return hapiValue
  }
}
