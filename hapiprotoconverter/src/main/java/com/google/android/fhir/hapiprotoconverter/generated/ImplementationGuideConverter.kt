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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ImplementationGuide
import com.google.fhir.r4.core.ImplementationGuide.Definition
import com.google.fhir.r4.core.ImplementationGuide.Definition.Page
import com.google.fhir.r4.core.ImplementationGuide.Definition.Resource
import com.google.fhir.r4.core.ImplementationGuide.Manifest
import com.google.fhir.r4.core.ImplementationGuide.Manifest.ManifestResource
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.SPDXLicenseCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Url
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UrlType

public object ImplementationGuideConverter {
  private
      fun ImplementationGuide.Definition.Resource.ExampleX.implementationGuideDefinitionResourceExampleToHapi():
      Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType ) {
      return (this.getCanonical()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for ImplementationGuide.definition.resource.example[x]")
  }

  private fun Type.implementationGuideDefinitionResourceExampleToProto():
      ImplementationGuide.Definition.Resource.ExampleX {
    val protoValue = ImplementationGuide.Definition.Resource.ExampleX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    return protoValue.build()
  }

  private
      fun ImplementationGuide.Definition.Page.NameX.implementationGuideDefinitionPageNameToHapi():
      Type {
    if (this.getUrl() != Url.newBuilder().defaultInstanceForType ) {
      return (this.getUrl()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImplementationGuide.definition.page.name[x]")
  }

  private fun Type.implementationGuideDefinitionPageNameToProto():
      ImplementationGuide.Definition.Page.NameX {
    val protoValue = ImplementationGuide.Definition.Page.NameX.newBuilder()
    if (this is UrlType) {
      protoValue.setUrl(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  private
      fun ImplementationGuide.Manifest.ManifestResource.ExampleX.implementationGuideManifestResourceExampleToHapi():
      Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType ) {
      return (this.getCanonical()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for ImplementationGuide.manifest.resource.example[x]")
  }

  private fun Type.implementationGuideManifestResourceExampleToProto():
      ImplementationGuide.Manifest.ManifestResource.ExampleX {
    val protoValue = ImplementationGuide.Manifest.ManifestResource.ExampleX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    return protoValue.build()
  }

  public fun ImplementationGuide.toHapi(): org.hl7.fhir.r4.model.ImplementationGuide {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map{it.toHapi()})
    hapiValue.setJurisdiction(jurisdictionList.map{it.toHapi()})
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setPackageIdElement(packageId.toHapi())
    hapiValue.setLicense(org.hl7.fhir.r4.model.ImplementationGuide.SPDXLicense.valueOf(license.value.name.replace("_","")))
    fhirVersionList.map{hapiValue.addFhirVersion(Enumerations.FHIRVersion.valueOf(it.value.name.replace("_","")))}
    hapiValue.setDependsOn(dependsOnList.map{it.toHapi()})
    hapiValue.setGlobal(globalList.map{it.toHapi()})
    hapiValue.setDefinition(definition.toHapi())
    hapiValue.setManifest(manifest.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ImplementationGuide.toProto(): ImplementationGuide {
    val protoValue = ImplementationGuide.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setUrl(urlElement.toProto())
    .setVersion(versionElement.toProto())
    .setName(nameElement.toProto())
    .setTitle(titleElement.toProto())
    .setStatus(ImplementationGuide.StatusCode.newBuilder().setValue(PublicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setExperimental(experimentalElement.toProto())
    .setDate(dateElement.toProto())
    .setPublisher(publisherElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .addAllUseContext(useContext.map{it.toProto()})
    .addAllJurisdiction(jurisdiction.map{it.toProto()})
    .setCopyright(copyrightElement.toProto())
    .setPackageId(packageIdElement.toProto())
    .setLicense(ImplementationGuide.LicenseCode.newBuilder().setValue(SPDXLicenseCode.Value.valueOf(license.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllFhirVersion(fhirVersion.map{ImplementationGuide.FhirVersionCode.newBuilder().setValue(FHIRVersionCode.Value.valueOf(it.value.toCode().replace("-",
        "_").toUpperCase())).build()})
    .addAllDependsOn(dependsOn.map{it.toProto()})
    .addAllGlobal(global.map{it.toProto()})
    .setDefinition(definition.toProto())
    .setManifest(manifest.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDependsOnComponent.toProto():
      ImplementationGuide.DependsOn {
    val protoValue = ImplementationGuide.DependsOn.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setUri(uriElement.toProto())
    .setPackageId(packageIdElement.toProto())
    .setVersion(versionElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideGlobalComponent.toProto():
      ImplementationGuide.Global {
    val protoValue = ImplementationGuide.Global.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setProfile(profileElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionComponent.toProto():
      ImplementationGuide.Definition {
    val protoValue = ImplementationGuide.Definition.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllGrouping(grouping.map{it.toProto()})
    .addAllResource(resource.map{it.toProto()})
    .setPage(page.toProto())
    .addAllParameter(parameter.map{it.toProto()})
    .addAllTemplate(template.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionGroupingComponent.toProto():
      ImplementationGuide.Definition.Grouping {
    val protoValue = ImplementationGuide.Definition.Grouping.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setDescription(descriptionElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionResourceComponent.toProto():
      ImplementationGuide.Definition.Resource {
    val protoValue = ImplementationGuide.Definition.Resource.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setReference(reference.toProto())
    .addAllFhirVersion(fhirVersion.map{ImplementationGuide.Definition.Resource.FhirVersionCode.newBuilder().setValue(FHIRVersionCode.Value.valueOf(it.value.toCode().replace("-",
        "_").toUpperCase())).build()})
    .setName(nameElement.toProto())
    .setDescription(descriptionElement.toProto())
    .setExample(example.implementationGuideDefinitionResourceExampleToProto())
    .setGroupingId(groupingIdElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionPageComponent.toProto():
      ImplementationGuide.Definition.Page {
    val protoValue = ImplementationGuide.Definition.Page.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(name.implementationGuideDefinitionPageNameToProto())
    .setTitle(titleElement.toProto())
    .setGeneration(ImplementationGuide.Definition.Page.GenerationCode.newBuilder().setValue(GuidePageGenerationCode.Value.valueOf(generation.toCode().replace("-",
        "_").toUpperCase())).build())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionParameterComponent.toProto():
      ImplementationGuide.Definition.Parameter {
    val protoValue = ImplementationGuide.Definition.Parameter.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setValue(valueElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionTemplateComponent.toProto():
      ImplementationGuide.Definition.Template {
    val protoValue = ImplementationGuide.Definition.Template.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(codeElement.toProto())
    .setSource(sourceElement.toProto())
    .setScope(scopeElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideManifestComponent.toProto():
      ImplementationGuide.Manifest {
    val protoValue = ImplementationGuide.Manifest.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setRendering(renderingElement.toProto())
    .addAllResource(resource.map{it.toProto()})
    .addAllPage(page.map{it.toProto()})
    .addAllImage(image.map{it.toProto()})
    .addAllOther(other.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.ImplementationGuide.ManifestResourceComponent.toProto():
      ImplementationGuide.Manifest.ManifestResource {
    val protoValue = ImplementationGuide.Manifest.ManifestResource.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setReference(reference.toProto())
    .setExample(example.implementationGuideManifestResourceExampleToProto())
    .setRelativePath(relativePathElement.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.ImplementationGuide.ManifestPageComponent.toProto():
      ImplementationGuide.Manifest.ManifestPage {
    val protoValue = ImplementationGuide.Manifest.ManifestPage.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setName(nameElement.toProto())
    .setTitle(titleElement.toProto())
    .addAllAnchor(anchor.map{it.toProto()})
    .build()
    return protoValue
  }

  private fun ImplementationGuide.DependsOn.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDependsOnComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDependsOnComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setUriElement(uri.toHapi())
    hapiValue.setPackageIdElement(packageId.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    return hapiValue
  }

  private fun ImplementationGuide.Global.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideGlobalComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideGlobalComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setProfileElement(profile.toHapi())
    return hapiValue
  }

  private fun ImplementationGuide.Definition.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setGrouping(groupingList.map{it.toHapi()})
    hapiValue.setResource(resourceList.map{it.toHapi()})
    hapiValue.setPage(page.toHapi())
    hapiValue.setParameter(parameterList.map{it.toHapi()})
    hapiValue.setTemplate(templateList.map{it.toHapi()})
    return hapiValue
  }

  private fun ImplementationGuide.Definition.Grouping.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionGroupingComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionGroupingComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }

  private fun ImplementationGuide.Definition.Resource.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionResourceComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionResourceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setReference(reference.toHapi())
    fhirVersionList.map{hapiValue.addFhirVersion(Enumerations.FHIRVersion.valueOf(it.value.name.replace("_","")))}
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setExample(example.implementationGuideDefinitionResourceExampleToHapi())
    hapiValue.setGroupingIdElement(groupingId.toHapi())
    return hapiValue
  }

  private fun ImplementationGuide.Definition.Page.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionPageComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionPageComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setName(name.implementationGuideDefinitionPageNameToHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setGeneration(org.hl7.fhir.r4.model.ImplementationGuide.GuidePageGeneration.valueOf(generation.value.name.replace("_","")))
    return hapiValue
  }

  private fun ImplementationGuide.Definition.Parameter.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionParameterComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionParameterComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }

  private fun ImplementationGuide.Definition.Template.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionTemplateComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideDefinitionTemplateComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setSourceElement(source.toHapi())
    hapiValue.setScopeElement(scope.toHapi())
    return hapiValue
  }

  private fun ImplementationGuide.Manifest.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideManifestComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ImplementationGuideManifestComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setRenderingElement(rendering.toHapi())
    hapiValue.setResource(resourceList.map{it.toHapi()})
    hapiValue.setPage(pageList.map{it.toHapi()})
    hapiValue.setImage(imageList.map{it.toHapi()})
    hapiValue.setOther(otherList.map{it.toHapi()})
    return hapiValue
  }

  private fun ImplementationGuide.Manifest.ManifestResource.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ManifestResourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ManifestResourceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setReference(reference.toHapi())
    hapiValue.setExample(example.implementationGuideManifestResourceExampleToHapi())
    hapiValue.setRelativePathElement(relativePath.toHapi())
    return hapiValue
  }

  private fun ImplementationGuide.Manifest.ManifestPage.toHapi():
      org.hl7.fhir.r4.model.ImplementationGuide.ManifestPageComponent {
    val hapiValue = org.hl7.fhir.r4.model.ImplementationGuide.ManifestPageComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setAnchor(anchorList.map{it.toHapi()})
    return hapiValue
  }
}
