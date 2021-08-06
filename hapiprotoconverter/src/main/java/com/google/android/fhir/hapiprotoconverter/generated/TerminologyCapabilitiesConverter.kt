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
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.CapabilityStatementKindCode
import com.google.fhir.r4.core.CodeSearchSupportCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.TerminologyCapabilities
import com.google.fhir.r4.core.TerminologyCapabilities.CodeSystem
import com.google.fhir.r4.core.TerminologyCapabilities.CodeSystem.Version
import com.google.fhir.r4.core.TerminologyCapabilities.Expansion
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object TerminologyCapabilitiesConverter {
  @JvmStatic
  public fun TerminologyCapabilities.toHapi(): org.hl7.fhir.r4.model.TerminologyCapabilities {
    val hapiValue = org.hl7.fhir.r4.model.TerminologyCapabilities()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setKind(
      org.hl7.fhir.r4.model.TerminologyCapabilities.CapabilityStatementKind.valueOf(
        kind.value.name.replace("_", "")
      )
    )
    hapiValue.setSoftware(software.toHapi())
    hapiValue.setImplementation(implementation.toHapi())
    hapiValue.setLockedDateElement(lockedDate.toHapi())
    hapiValue.setCodeSystem(codeSystemList.map { it.toHapi() })
    hapiValue.setExpansion(expansion.toHapi())
    hapiValue.setCodeSearch(
      org.hl7.fhir.r4.model.TerminologyCapabilities.CodeSearchSupport.valueOf(
        codeSearch.value.name.replace("_", "")
      )
    )
    hapiValue.setValidateCode(validateCode.toHapi())
    hapiValue.setTranslation(translation.toHapi())
    hapiValue.setClosure(closure.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.TerminologyCapabilities.toProto(): TerminologyCapabilities {
    val protoValue =
      TerminologyCapabilities.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setStatus(
          TerminologyCapabilities.StatusCode.newBuilder()
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
        .setCopyright(copyrightElement.toProto())
        .setKind(
          TerminologyCapabilities.KindCode.newBuilder()
            .setValue(
              CapabilityStatementKindCode.Value.valueOf(
                kind.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setSoftware(software.toProto())
        .setImplementation(implementation.toProto())
        .setLockedDate(lockedDateElement.toProto())
        .addAllCodeSystem(codeSystem.map { it.toProto() })
        .setExpansion(expansion.toProto())
        .setCodeSearch(
          TerminologyCapabilities.CodeSearchCode.newBuilder()
            .setValue(
              CodeSearchSupportCode.Value.valueOf(
                codeSearch.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setValidateCode(validateCode.toProto())
        .setTranslation(translation.toProto())
        .setClosure(closure.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesSoftwareComponent.toProto():
    TerminologyCapabilities.Software {
    val protoValue =
      TerminologyCapabilities.Software.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setVersion(versionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesImplementationComponent.toProto():
    TerminologyCapabilities.Implementation {
    val protoValue =
      TerminologyCapabilities.Implementation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setUrl(urlElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent.toProto():
    TerminologyCapabilities.CodeSystem {
    val protoValue =
      TerminologyCapabilities.CodeSystem.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUri(uriElement.toProto())
        .addAllVersion(version.map { it.toProto() })
        .setSubsumption(subsumptionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionComponent.toProto():
    TerminologyCapabilities.CodeSystem.Version {
    val protoValue =
      TerminologyCapabilities.CodeSystem.Version.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .setIsDefault(isDefaultElement.toProto())
        .setCompositional(compositionalElement.toProto())
        .addAllLanguage(language.map { it.toProto() })
        .addAllFilter(filter.map { it.toProto() })
        .addAllProperty(property.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionFilterComponent.toProto():
    TerminologyCapabilities.CodeSystem.Version.Filter {
    val protoValue =
      TerminologyCapabilities.CodeSystem.Version.Filter.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .addAllOp(op.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionComponent.toProto():
    TerminologyCapabilities.Expansion {
    val protoValue =
      TerminologyCapabilities.Expansion.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setHierarchical(hierarchicalElement.toProto())
        .setPaging(pagingElement.toProto())
        .setIncomplete(incompleteElement.toProto())
        .addAllParameter(parameter.map { it.toProto() })
        .setTextFilter(textFilterElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionParameterComponent.toProto():
    TerminologyCapabilities.Expansion.Parameter {
    val protoValue =
      TerminologyCapabilities.Expansion.Parameter.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setDocumentation(documentationElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesValidateCodeComponent.toProto():
    TerminologyCapabilities.ValidateCode {
    val protoValue =
      TerminologyCapabilities.ValidateCode.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setTranslations(translationsElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesTranslationComponent.toProto():
    TerminologyCapabilities.Translation {
    val protoValue =
      TerminologyCapabilities.Translation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setNeedsMap(needsMapElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesClosureComponent.toProto():
    TerminologyCapabilities.Closure {
    val protoValue =
      TerminologyCapabilities.Closure.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setTranslation(translationElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Software.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesSoftwareComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesSoftwareComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Implementation.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesImplementationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesImplementationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.CodeSystem.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUriElement(uri.toHapi())
    hapiValue.setVersion(versionList.map { it.toHapi() })
    hapiValue.setSubsumptionElement(subsumption.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.CodeSystem.Version.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities
        .TerminologyCapabilitiesCodeSystemVersionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setIsDefaultElement(isDefault.toHapi())
    hapiValue.setCompositionalElement(compositional.toHapi())
    hapiValue.setLanguage(languageList.map { it.toHapi() })
    hapiValue.setFilter(filterList.map { it.toHapi() })
    hapiValue.setProperty(propertyList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.CodeSystem.Version.Filter.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionFilterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities
        .TerminologyCapabilitiesCodeSystemVersionFilterComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setOp(opList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Expansion.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setHierarchicalElement(hierarchical.toHapi())
    hapiValue.setPagingElement(paging.toHapi())
    hapiValue.setIncompleteElement(incomplete.toHapi())
    hapiValue.setParameter(parameterList.map { it.toHapi() })
    hapiValue.setTextFilterElement(textFilter.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Expansion.Parameter.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionParameterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities
        .TerminologyCapabilitiesExpansionParameterComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDocumentationElement(documentation.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.ValidateCode.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesValidateCodeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesValidateCodeComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTranslationsElement(translations.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Translation.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesTranslationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesTranslationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNeedsMapElement(needsMap.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Closure.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesClosureComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesClosureComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTranslationElement(translation.toHapi())
    return hapiValue
  }
}
