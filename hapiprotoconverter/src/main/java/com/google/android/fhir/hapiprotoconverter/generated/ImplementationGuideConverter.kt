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
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UsageContextConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.FHIRVersionCode
import com.google.fhir.r4.core.GuidePageGenerationCode
import com.google.fhir.r4.core.GuideParameterCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ImplementationGuide
import com.google.fhir.r4.core.ImplementationGuide.Definition
import com.google.fhir.r4.core.ImplementationGuide.Definition.Page
import com.google.fhir.r4.core.ImplementationGuide.Definition.Parameter
import com.google.fhir.r4.core.ImplementationGuide.Definition.Resource
import com.google.fhir.r4.core.ImplementationGuide.Global
import com.google.fhir.r4.core.ImplementationGuide.Manifest
import com.google.fhir.r4.core.ImplementationGuide.Manifest.ManifestResource
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.SPDXLicenseCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Url
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UrlType

object ImplementationGuideConverter {
  @JvmStatic
  private fun ImplementationGuide.Definition.Resource.ExampleX.implementationGuideDefinitionResourceExampleToHapi():
    Type {
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.canonical != Canonical.newBuilder().defaultInstanceForType) {
      return (this.canonical).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ImplementationGuide.definition.resource.example[x]"
    )
  }

  @JvmStatic
  private fun Type.implementationGuideDefinitionResourceExampleToProto():
    ImplementationGuide.Definition.Resource.ExampleX {
    val protoValue = ImplementationGuide.Definition.Resource.ExampleX.newBuilder()
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ImplementationGuide.Definition.Page.NameX.implementationGuideDefinitionPageNameToHapi():
    Type {
    if (this.url != Url.newBuilder().defaultInstanceForType) {
      return (this.url).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImplementationGuide.definition.page.name[x]")
  }

  @JvmStatic
  private fun Type.implementationGuideDefinitionPageNameToProto():
    ImplementationGuide.Definition.Page.NameX {
    val protoValue = ImplementationGuide.Definition.Page.NameX.newBuilder()
    if (this is UrlType) {
      protoValue.url = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ImplementationGuide.Manifest.ManifestResource.ExampleX.implementationGuideManifestResourceExampleToHapi():
    Type {
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.canonical != Canonical.newBuilder().defaultInstanceForType) {
      return (this.canonical).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ImplementationGuide.manifest.resource.example[x]"
    )
  }

  @JvmStatic
  private fun Type.implementationGuideManifestResourceExampleToProto():
    ImplementationGuide.Manifest.ManifestResource.ExampleX {
    val protoValue = ImplementationGuide.Manifest.ManifestResource.ExampleX.newBuilder()
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun ImplementationGuide.toHapi(): org.hl7.fhir.r4.model.ImplementationGuide {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide()
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
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasPackageId()) {
      hapiValue.packageIdElement = packageId.toHapi()
    }
    hapiValue.license =
      org.hl7.fhir.r4.model.ImplementationGuide.SPDXLicense.valueOf(
        license.value.name.hapiCodeCheck().replace("_", "")
      )
    fhirVersionList.forEach {
      hapiValue.addFhirVersion(
        Enumerations.FHIRVersion.valueOf(it.value.name.hapiCodeCheck().replace("_", ""))
      )
    }
    if (dependsOnCount > 0) {
      hapiValue.dependsOn = dependsOnList.map { it.toHapi() }
    }
    if (globalCount > 0) {
      hapiValue.global = globalList.map { it.toHapi() }
    }
    if (hasDefinition()) {
      hapiValue.definition = definition.toHapi()
    }
    if (hasManifest()) {
      hapiValue.manifest = manifest.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.ImplementationGuide.toProto(): ImplementationGuide {
    val protoValue = ImplementationGuide.newBuilder().setId(Id.newBuilder().setValue(id))
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
      ImplementationGuide.StatusCode.newBuilder()
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
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasPackageId()) {
      protoValue.packageId = packageIdElement.toProto()
    }
    protoValue.license =
      ImplementationGuide.LicenseCode.newBuilder()
        .setValue(
          SPDXLicenseCode.Value.valueOf(
            license.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.addAllFhirVersion(
      fhirVersion.map {
        ImplementationGuide.FhirVersionCode.newBuilder()
          .setValue(
            FHIRVersionCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasDependsOn()) {
      protoValue.addAllDependsOn(dependsOn.map { it.toProto() })
    }
    if (hasGlobal()) {
      protoValue.addAllGlobal(global.map { it.toProto() })
    }
    if (hasDefinition()) {
      protoValue.definition = definition.toProto()
    }
    if (hasManifest()) {
      protoValue.manifest = manifest.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDependsOnComponent.toProto():
    ImplementationGuide.DependsOn {
    val protoValue =
      ImplementationGuide.DependsOn.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasUri()) {
      protoValue.uri = uriElement.toProto()
    }
    if (hasPackageId()) {
      protoValue.packageId = packageIdElement.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideGlobalComponent.toProto():
    ImplementationGuide.Global {
    val protoValue = ImplementationGuide.Global.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.type =
      ImplementationGuide.Global.TypeCode.newBuilder()
        .setValue(ResourceTypeCode.Value.valueOf(type))
        .build()
    if (hasProfile()) {
      protoValue.profile = profileElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionComponent.toProto():
    ImplementationGuide.Definition {
    val protoValue =
      ImplementationGuide.Definition.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasGrouping()) {
      protoValue.addAllGrouping(grouping.map { it.toProto() })
    }
    if (hasResource()) {
      protoValue.addAllResource(resource.map { it.toProto() })
    }
    if (hasPage()) {
      protoValue.page = page.toProto()
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    if (hasTemplate()) {
      protoValue.addAllTemplate(template.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionGroupingComponent.toProto():
    ImplementationGuide.Definition.Grouping {
    val protoValue =
      ImplementationGuide.Definition.Grouping.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionResourceComponent.toProto():
    ImplementationGuide.Definition.Resource {
    val protoValue =
      ImplementationGuide.Definition.Resource.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasReference()) {
      protoValue.reference = reference.toProto()
    }
    protoValue.addAllFhirVersion(
      fhirVersion.map {
        ImplementationGuide.Definition.Resource.FhirVersionCode.newBuilder()
          .setValue(
            FHIRVersionCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasExample()) {
      protoValue.example = example.implementationGuideDefinitionResourceExampleToProto()
    }
    if (hasGroupingId()) {
      protoValue.groupingId = groupingIdElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionPageComponent.toProto():
    ImplementationGuide.Definition.Page {
    val protoValue =
      ImplementationGuide.Definition.Page.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = name.implementationGuideDefinitionPageNameToProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    protoValue.generation =
      ImplementationGuide.Definition.Page.GenerationCode.newBuilder()
        .setValue(
          GuidePageGenerationCode.Value.valueOf(
            generation.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionParameterComponent.toProto():
    ImplementationGuide.Definition.Parameter {
    val protoValue =
      ImplementationGuide.Definition.Parameter.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.code =
      ImplementationGuide.Definition.Parameter.CodeType.newBuilder()
        .setValue(GuideParameterCode.Value.valueOf(code))
        .build()
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionTemplateComponent.toProto():
    ImplementationGuide.Definition.Template {
    val protoValue =
      ImplementationGuide.Definition.Template.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasSource()) {
      protoValue.source = sourceElement.toProto()
    }
    if (hasScope()) {
      protoValue.scope = scopeElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideManifestComponent.toProto():
    ImplementationGuide.Manifest {
    val protoValue =
      ImplementationGuide.Manifest.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRendering()) {
      protoValue.rendering = renderingElement.toProto()
    }
    if (hasResource()) {
      protoValue.addAllResource(resource.map { it.toProto() })
    }
    if (hasPage()) {
      protoValue.addAllPage(page.map { it.toProto() })
    }
    if (hasImage()) {
      protoValue.addAllImage(image.map { it.toProto() })
    }
    if (hasOther()) {
      protoValue.addAllOther(other.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ManifestResourceComponent.toProto():
    ImplementationGuide.Manifest.ManifestResource {
    val protoValue =
      ImplementationGuide.Manifest.ManifestResource.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasReference()) {
      protoValue.reference = reference.toProto()
    }
    if (hasExample()) {
      protoValue.example = example.implementationGuideManifestResourceExampleToProto()
    }
    if (hasRelativePath()) {
      protoValue.relativePath = relativePathElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ImplementationGuide.ManifestPageComponent.toProto():
    ImplementationGuide.Manifest.ManifestPage {
    val protoValue =
      ImplementationGuide.Manifest.ManifestPage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasAnchor()) {
      protoValue.addAllAnchor(anchor.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ImplementationGuide.DependsOn.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDependsOnComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDependsOnComponent()
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
    if (hasPackageId()) {
      hapiValue.packageIdElement = packageId.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Global.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideGlobalComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideGlobalComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.type = type.value.name
    if (hasProfile()) {
      hapiValue.profileElement = profile.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Definition.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (groupingCount > 0) {
      hapiValue.grouping = groupingList.map { it.toHapi() }
    }
    if (resourceCount > 0) {
      hapiValue.resource = resourceList.map { it.toHapi() }
    }
    if (hasPage()) {
      hapiValue.page = page.toHapi()
    }
    if (parameterCount > 0) {
      hapiValue.parameter = parameterList.map { it.toHapi() }
    }
    if (templateCount > 0) {
      hapiValue.template = templateList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Definition.Grouping.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionGroupingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionGroupingComponent()
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
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Definition.Resource.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionResourceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionResourceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasReference()) {
      hapiValue.reference = reference.toHapi()
    }
    fhirVersionList.forEach {
      hapiValue.addFhirVersion(
        Enumerations.FHIRVersion.valueOf(it.value.name.hapiCodeCheck().replace("_", ""))
      )
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasExample()) {
      hapiValue.example = example.implementationGuideDefinitionResourceExampleToHapi()
    }
    if (hasGroupingId()) {
      hapiValue.groupingIdElement = groupingId.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Definition.Page.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionPageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionPageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.name = name.implementationGuideDefinitionPageNameToHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    hapiValue.generation =
      org.hl7.fhir.r4.model.ImplementationGuide.GuidePageGeneration.valueOf(
        generation.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Definition.Parameter.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionParameterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionParameterComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.code = code.value.name
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Definition.Template.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionTemplateComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionTemplateComponent()
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
    if (hasSource()) {
      hapiValue.sourceElement = source.toHapi()
    }
    if (hasScope()) {
      hapiValue.scopeElement = scope.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Manifest.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideManifestComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideManifestComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRendering()) {
      hapiValue.renderingElement = rendering.toHapi()
    }
    if (resourceCount > 0) {
      hapiValue.resource = resourceList.map { it.toHapi() }
    }
    if (pageCount > 0) {
      hapiValue.page = pageList.map { it.toHapi() }
    }
    if (imageCount > 0) {
      hapiValue.image = imageList.map { it.toHapi() }
    }
    if (otherCount > 0) {
      hapiValue.other = otherList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Manifest.ManifestResource.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ManifestResourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ManifestResourceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasReference()) {
      hapiValue.reference = reference.toHapi()
    }
    if (hasExample()) {
      hapiValue.example = example.implementationGuideManifestResourceExampleToHapi()
    }
    if (hasRelativePath()) {
      hapiValue.relativePathElement = relativePath.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImplementationGuide.Manifest.ManifestPage.toHapi():
    org.hl7.fhir.r4.model.ImplementationGuide.ManifestPageComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ManifestPageComponent()
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
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (anchorCount > 0) {
      hapiValue.anchor = anchorList.map { it.toHapi() }
    }
    return hapiValue
  }
}
