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
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
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
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.ConceptMap
import com.google.fhir.r4.core.ConceptMap.Group
import com.google.fhir.r4.core.ConceptMap.Group.SourceElement
import com.google.fhir.r4.core.ConceptMap.Group.SourceElement.TargetElement
import com.google.fhir.r4.core.ConceptMap.Group.Unmapped
import com.google.fhir.r4.core.ConceptMapEquivalenceCode
import com.google.fhir.r4.core.ConceptMapGroupUnmappedModeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Uri
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

public object ConceptMapConverter {
  @JvmStatic
  private fun ConceptMap.SourceX.conceptMapSourceToHapi(): Type {
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ConceptMap.source[x]")
  }

  @JvmStatic
  private fun Type.conceptMapSourceToProto(): ConceptMap.SourceX {
    val protoValue = ConceptMap.SourceX.newBuilder()
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ConceptMap.TargetX.conceptMapTargetToHapi(): Type {
    if (this.getUri() != Uri.newBuilder().defaultInstanceForType) {
      return (this.getUri()).toHapi()
    }
    if (this.getCanonical() != Canonical.newBuilder().defaultInstanceForType) {
      return (this.getCanonical()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ConceptMap.target[x]")
  }

  @JvmStatic
  private fun Type.conceptMapTargetToProto(): ConceptMap.TargetX {
    val protoValue = ConceptMap.TargetX.newBuilder()
    if (this is UriType) {
      protoValue.setUri(this.toProto())
    }
    if (this is CanonicalType) {
      protoValue.setCanonical(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ConceptMap.toHapi(): org.hl7.fhir.r4.model.ConceptMap {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap()
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
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
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
    if (hasSource()) {
      hapiValue.setSource(source.conceptMapSourceToHapi())
    }
    if (hasTarget()) {
      hapiValue.setTarget(target.conceptMapTargetToHapi())
    }
    if (groupCount > 0) {
      hapiValue.setGroup(groupList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ConceptMap.toProto(): ConceptMap {
    val protoValue = ConceptMap.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
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
      ConceptMap.StatusCode.newBuilder()
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
    if (hasSource()) {
      protoValue.setSource(source.conceptMapSourceToProto())
    }
    if (hasTarget()) {
      protoValue.setTarget(target.conceptMapTargetToProto())
    }
    if (hasGroup()) {
      protoValue.addAllGroup(group.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent.toProto():
    ConceptMap.Group {
    val protoValue = ConceptMap.Group.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.setSource(sourceElement.toProto())
    }
    if (hasSourceVersion()) {
      protoValue.setSourceVersion(sourceVersionElement.toProto())
    }
    if (hasTarget()) {
      protoValue.setTarget(targetElement.toProto())
    }
    if (hasTargetVersion()) {
      protoValue.setTargetVersion(targetVersionElement.toProto())
    }
    if (hasElement()) {
      protoValue.addAllElement(element.map { it.toProto() })
    }
    if (hasUnmapped()) {
      protoValue.setUnmapped(unmapped.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent.toProto():
    ConceptMap.Group.SourceElement {
    val protoValue =
      ConceptMap.Group.SourceElement.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(codeElement.toProto())
    }
    if (hasDisplay()) {
      protoValue.setDisplay(displayElement.toProto())
    }
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent.toProto():
    ConceptMap.Group.SourceElement.TargetElement {
    val protoValue =
      ConceptMap.Group.SourceElement.TargetElement.newBuilder()
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
    if (hasDisplay()) {
      protoValue.setDisplay(displayElement.toProto())
    }
    protoValue.setEquivalence(
      ConceptMap.Group.SourceElement.TargetElement.EquivalenceCode.newBuilder()
        .setValue(
          ConceptMapEquivalenceCode.Value.valueOf(
            equivalence.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    if (hasDependsOn()) {
      protoValue.addAllDependsOn(dependsOn.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent.toProto():
    ConceptMap.Group.SourceElement.TargetElement.OtherElement {
    val protoValue =
      ConceptMap.Group.SourceElement.TargetElement.OtherElement.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.setProperty(propertyElement.toProto())
    }
    if (hasSystem()) {
      protoValue.setSystem(systemElement.toProto())
    }
    if (hasValue()) {
      protoValue.setValue(valueElement.toProto())
    }
    if (hasDisplay()) {
      protoValue.setDisplay(displayElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent.toProto():
    ConceptMap.Group.Unmapped {
    val protoValue = ConceptMap.Group.Unmapped.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.setMode(
      ConceptMap.Group.Unmapped.ModeCode.newBuilder()
        .setValue(
          ConceptMapGroupUnmappedModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasCode()) {
      protoValue.setCode(codeElement.toProto())
    }
    if (hasDisplay()) {
      protoValue.setDisplay(displayElement.toProto())
    }
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ConceptMap.Group.toHapi(): org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSource()) {
      hapiValue.setSourceElement(source.toHapi())
    }
    if (hasSourceVersion()) {
      hapiValue.setSourceVersionElement(sourceVersion.toHapi())
    }
    if (hasTarget()) {
      hapiValue.setTargetElement(target.toHapi())
    }
    if (hasTargetVersion()) {
      hapiValue.setTargetVersionElement(targetVersion.toHapi())
    }
    if (elementCount > 0) {
      hapiValue.setElement(elementList.map { it.toHapi() })
    }
    if (hasUnmapped()) {
      hapiValue.setUnmapped(unmapped.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.SourceElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent()
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
    if (hasDisplay()) {
      hapiValue.setDisplayElement(display.toHapi())
    }
    if (targetCount > 0) {
      hapiValue.setTarget(targetList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.SourceElement.TargetElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent()
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
    if (hasDisplay()) {
      hapiValue.setDisplayElement(display.toHapi())
    }
    hapiValue.setEquivalence(
      Enumerations.ConceptMapEquivalence.valueOf(
        equivalence.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    if (dependsOnCount > 0) {
      hapiValue.setDependsOn(dependsOnList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.SourceElement.TargetElement.OtherElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasProperty()) {
      hapiValue.setPropertyElement(property.toHapi())
    }
    if (hasSystem()) {
      hapiValue.setSystemElement(system.toHapi())
    }
    if (hasValue()) {
      hapiValue.setValueElement(value.toHapi())
    }
    if (hasDisplay()) {
      hapiValue.setDisplayElement(display.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.Unmapped.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setMode(
      org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasCode()) {
      hapiValue.setCodeElement(code.toHapi())
    }
    if (hasDisplay()) {
      hapiValue.setDisplayElement(display.toHapi())
    }
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    return hapiValue
  }
}
