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
    if (hasTitle()) {
      hapiValue.setTitleElement(title.toHapi())
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
    if (hasCopyright()) {
      hapiValue.setCopyrightElement(copyright.toHapi())
    }
    hapiValue.setKind(
      org.hl7.fhir.r4.model.TerminologyCapabilities.CapabilityStatementKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasSoftware()) {
      hapiValue.setSoftware(software.toHapi())
    }
    if (hasImplementation()) {
      hapiValue.setImplementation(implementation.toHapi())
    }
    if (hasLockedDate()) {
      hapiValue.setLockedDateElement(lockedDate.toHapi())
    }
    if (codeSystemCount > 0) {
      hapiValue.setCodeSystem(codeSystemList.map { it.toHapi() })
    }
    if (hasExpansion()) {
      hapiValue.setExpansion(expansion.toHapi())
    }
    hapiValue.setCodeSearch(
      org.hl7.fhir.r4.model.TerminologyCapabilities.CodeSearchSupport.valueOf(
        codeSearch.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasValidateCode()) {
      hapiValue.setValidateCode(validateCode.toHapi())
    }
    if (hasTranslation()) {
      hapiValue.setTranslation(translation.toHapi())
    }
    if (hasClosure()) {
      hapiValue.setClosure(closure.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.TerminologyCapabilities.toProto(): TerminologyCapabilities {
    val protoValue = TerminologyCapabilities.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasTitle()) {
      protoValue.setTitle(titleElement.toProto())
    }
    protoValue.setStatus(
      TerminologyCapabilities.StatusCode.newBuilder()
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
    if (hasCopyright()) {
      protoValue.setCopyright(copyrightElement.toProto())
    }
    protoValue.setKind(
      TerminologyCapabilities.KindCode.newBuilder()
        .setValue(
          CapabilityStatementKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasSoftware()) {
      protoValue.setSoftware(software.toProto())
    }
    if (hasImplementation()) {
      protoValue.setImplementation(implementation.toProto())
    }
    if (hasLockedDate()) {
      protoValue.setLockedDate(lockedDateElement.toProto())
    }
    if (hasCodeSystem()) {
      protoValue.addAllCodeSystem(codeSystem.map { it.toProto() })
    }
    if (hasExpansion()) {
      protoValue.setExpansion(expansion.toProto())
    }
    protoValue.setCodeSearch(
      TerminologyCapabilities.CodeSearchCode.newBuilder()
        .setValue(
          CodeSearchSupportCode.Value.valueOf(
            codeSearch.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasValidateCode()) {
      protoValue.setValidateCode(validateCode.toProto())
    }
    if (hasTranslation()) {
      protoValue.setTranslation(translation.toProto())
    }
    if (hasClosure()) {
      protoValue.setClosure(closure.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesSoftwareComponent.toProto():
    TerminologyCapabilities.Software {
    val protoValue =
      TerminologyCapabilities.Software.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasVersion()) {
      protoValue.setVersion(versionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesImplementationComponent.toProto():
    TerminologyCapabilities.Implementation {
    val protoValue =
      TerminologyCapabilities.Implementation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent.toProto():
    TerminologyCapabilities.CodeSystem {
    val protoValue =
      TerminologyCapabilities.CodeSystem.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUri()) {
      protoValue.setUri(uriElement.toProto())
    }
    if (hasVersion()) {
      protoValue.addAllVersion(version.map { it.toProto() })
    }
    if (hasSubsumption()) {
      protoValue.setSubsumption(subsumptionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionComponent.toProto():
    TerminologyCapabilities.CodeSystem.Version {
    val protoValue =
      TerminologyCapabilities.CodeSystem.Version.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(codeElement.toProto())
    }
    if (hasIsDefault()) {
      protoValue.setIsDefault(isDefaultElement.toProto())
    }
    if (hasCompositional()) {
      protoValue.setCompositional(compositionalElement.toProto())
    }
    if (hasLanguage()) {
      protoValue.addAllLanguage(language.map { it.toProto() })
    }
    if (hasFilter()) {
      protoValue.addAllFilter(filter.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.addAllProperty(property.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionFilterComponent.toProto():
    TerminologyCapabilities.CodeSystem.Version.Filter {
    val protoValue =
      TerminologyCapabilities.CodeSystem.Version.Filter.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(codeElement.toProto())
    }
    if (hasOp()) {
      protoValue.addAllOp(op.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionComponent.toProto():
    TerminologyCapabilities.Expansion {
    val protoValue =
      TerminologyCapabilities.Expansion.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasHierarchical()) {
      protoValue.setHierarchical(hierarchicalElement.toProto())
    }
    if (hasPaging()) {
      protoValue.setPaging(pagingElement.toProto())
    }
    if (hasIncomplete()) {
      protoValue.setIncomplete(incompleteElement.toProto())
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    if (hasTextFilter()) {
      protoValue.setTextFilter(textFilterElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionParameterComponent.toProto():
    TerminologyCapabilities.Expansion.Parameter {
    val protoValue =
      TerminologyCapabilities.Expansion.Parameter.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesValidateCodeComponent.toProto():
    TerminologyCapabilities.ValidateCode {
    val protoValue =
      TerminologyCapabilities.ValidateCode.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTranslations()) {
      protoValue.setTranslations(translationsElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesTranslationComponent.toProto():
    TerminologyCapabilities.Translation {
    val protoValue =
      TerminologyCapabilities.Translation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasNeedsMap()) {
      protoValue.setNeedsMap(needsMapElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesClosureComponent.toProto():
    TerminologyCapabilities.Closure {
    val protoValue =
      TerminologyCapabilities.Closure.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTranslation()) {
      protoValue.setTranslation(translationElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun TerminologyCapabilities.Software.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesSoftwareComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesSoftwareComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasVersion()) {
      hapiValue.setVersionElement(version.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Implementation.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesImplementationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesImplementationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.CodeSystem.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasUri()) {
      hapiValue.setUriElement(uri.toHapi())
    }
    if (versionCount > 0) {
      hapiValue.setVersion(versionList.map { it.toHapi() })
    }
    if (hasSubsumption()) {
      hapiValue.setSubsumptionElement(subsumption.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.CodeSystem.Version.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities
        .TerminologyCapabilitiesCodeSystemVersionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCodeElement(code.toHapi())
    }
    if (hasIsDefault()) {
      hapiValue.setIsDefaultElement(isDefault.toHapi())
    }
    if (hasCompositional()) {
      hapiValue.setCompositionalElement(compositional.toHapi())
    }
    if (languageCount > 0) {
      hapiValue.setLanguage(languageList.map { it.toHapi() })
    }
    if (filterCount > 0) {
      hapiValue.setFilter(filterList.map { it.toHapi() })
    }
    if (propertyCount > 0) {
      hapiValue.setProperty(propertyList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.CodeSystem.Version.Filter.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesCodeSystemVersionFilterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities
        .TerminologyCapabilitiesCodeSystemVersionFilterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCodeElement(code.toHapi())
    }
    if (opCount > 0) {
      hapiValue.setOp(opList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Expansion.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasHierarchical()) {
      hapiValue.setHierarchicalElement(hierarchical.toHapi())
    }
    if (hasPaging()) {
      hapiValue.setPagingElement(paging.toHapi())
    }
    if (hasIncomplete()) {
      hapiValue.setIncompleteElement(incomplete.toHapi())
    }
    if (parameterCount > 0) {
      hapiValue.setParameter(parameterList.map { it.toHapi() })
    }
    if (hasTextFilter()) {
      hapiValue.setTextFilterElement(textFilter.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Expansion.Parameter.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesExpansionParameterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities
        .TerminologyCapabilitiesExpansionParameterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.ValidateCode.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesValidateCodeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesValidateCodeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasTranslations()) {
      hapiValue.setTranslationsElement(translations.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Translation.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesTranslationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesTranslationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasNeedsMap()) {
      hapiValue.setNeedsMapElement(needsMap.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun TerminologyCapabilities.Closure.toHapi():
    org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesClosureComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.TerminologyCapabilities.TerminologyCapabilitiesClosureComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasTranslation()) {
      hapiValue.setTranslationElement(translation.toHapi())
    }
    return hapiValue
  }
}
