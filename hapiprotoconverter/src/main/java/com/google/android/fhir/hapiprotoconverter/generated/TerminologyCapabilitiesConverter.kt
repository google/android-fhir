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

object TerminologyCapabilitiesConverter {
  @JvmStatic
  fun TerminologyCapabilities.toHapi(): org.hl7.fhir.r4.model.TerminologyCapabilities {
    val hapiValue = org.hl7.fhir.r4.model.TerminologyCapabilities()
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
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    hapiValue.status =
      Enumerations.PublicationStatus.valueOf(status.value.name.hapiCodeCheck().replace("_", ""))
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
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    hapiValue.kind =
      org.hl7.fhir.r4.model.TerminologyCapabilities.CapabilityStatementKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasSoftware()) {
      hapiValue.software = software.toHapi()
    }
    if (hasImplementation()) {
      hapiValue.implementation = implementation.toHapi()
    }
    if (hasLockedDate()) {
      hapiValue.lockedDateElement = lockedDate.toHapi()
    }
    if (codeSystemCount > 0) {
      hapiValue.codeSystem = codeSystemList.map { it.toHapi() }
    }
    if (hasExpansion()) {
      hapiValue.expansion = expansion.toHapi()
    }
    hapiValue.codeSearch =
      org.hl7.fhir.r4.model.TerminologyCapabilities.CodeSearchSupport.valueOf(
        codeSearch.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasValidateCode()) {
      hapiValue.validateCode = validateCode.toHapi()
    }
    if (hasTranslation()) {
      hapiValue.translation = translation.toHapi()
    }
    if (hasClosure()) {
      hapiValue.closure = closure.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.TerminologyCapabilities.toProto(): TerminologyCapabilities {
    val protoValue = TerminologyCapabilities.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    protoValue.status =
      TerminologyCapabilities.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
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
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    protoValue.kind =
      TerminologyCapabilities.KindCode.newBuilder()
        .setValue(
          CapabilityStatementKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasSoftware()) {
      protoValue.software = software.toProto()
    }
    if (hasImplementation()) {
      protoValue.implementation = implementation.toProto()
    }
    if (hasLockedDate()) {
      protoValue.lockedDate = lockedDateElement.toProto()
    }
    if (hasCodeSystem()) {
      protoValue.addAllCodeSystem(codeSystem.map { it.toProto() })
    }
    if (hasExpansion()) {
      protoValue.expansion = expansion.toProto()
    }
    protoValue.codeSearch =
      TerminologyCapabilities.CodeSearchCode.newBuilder()
        .setValue(
          CodeSearchSupportCode.Value.valueOf(
            codeSearch.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasValidateCode()) {
      protoValue.validateCode = validateCode.toProto()
    }
    if (hasTranslation()) {
      protoValue.translation = translation.toProto()
    }
    if (hasClosure()) {
      protoValue.closure = closure.toProto()
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
      protoValue.name = nameElement.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
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
      protoValue.description = descriptionElement.toProto()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
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
      protoValue.uri = uriElement.toProto()
    }
    if (hasVersion()) {
      protoValue.addAllVersion(version.map { it.toProto() })
    }
    if (hasSubsumption()) {
      protoValue.subsumption = subsumptionElement.toProto()
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
      protoValue.code = codeElement.toProto()
    }
    if (hasIsDefault()) {
      protoValue.isDefault = isDefaultElement.toProto()
    }
    if (hasCompositional()) {
      protoValue.compositional = compositionalElement.toProto()
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
      protoValue.code = codeElement.toProto()
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
      protoValue.hierarchical = hierarchicalElement.toProto()
    }
    if (hasPaging()) {
      protoValue.paging = pagingElement.toProto()
    }
    if (hasIncomplete()) {
      protoValue.incomplete = incompleteElement.toProto()
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    if (hasTextFilter()) {
      protoValue.textFilter = textFilterElement.toProto()
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
      protoValue.name = nameElement.toProto()
    }
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
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
      protoValue.translations = translationsElement.toProto()
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
      protoValue.needsMap = needsMapElement.toProto()
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
      protoValue.translation = translationElement.toProto()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasUri()) {
      hapiValue.uriElement = uri.toHapi()
    }
    if (versionCount > 0) {
      hapiValue.version = versionList.map { it.toHapi() }
    }
    if (hasSubsumption()) {
      hapiValue.subsumptionElement = subsumption.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasIsDefault()) {
      hapiValue.isDefaultElement = isDefault.toHapi()
    }
    if (hasCompositional()) {
      hapiValue.compositionalElement = compositional.toHapi()
    }
    if (languageCount > 0) {
      hapiValue.language = languageList.map { it.toHapi() }
    }
    if (filterCount > 0) {
      hapiValue.filter = filterList.map { it.toHapi() }
    }
    if (propertyCount > 0) {
      hapiValue.property = propertyList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (opCount > 0) {
      hapiValue.op = opList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasHierarchical()) {
      hapiValue.hierarchicalElement = hierarchical.toHapi()
    }
    if (hasPaging()) {
      hapiValue.pagingElement = paging.toHapi()
    }
    if (hasIncomplete()) {
      hapiValue.incompleteElement = incomplete.toHapi()
    }
    if (parameterCount > 0) {
      hapiValue.parameter = parameterList.map { it.toHapi() }
    }
    if (hasTextFilter()) {
      hapiValue.textFilterElement = textFilter.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTranslations()) {
      hapiValue.translationsElement = translations.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasNeedsMap()) {
      hapiValue.needsMapElement = needsMap.toHapi()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTranslation()) {
      hapiValue.translationElement = translation.toHapi()
    }
    return hapiValue
  }
}
