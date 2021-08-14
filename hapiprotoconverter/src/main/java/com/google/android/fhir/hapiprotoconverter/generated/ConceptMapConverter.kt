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
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(
      Enumerations.PublicationStatus.valueOf(
        status
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setSource(source.conceptMapSourceToHapi())
    hapiValue.setTarget(target.conceptMapTargetToHapi())
    hapiValue.setGroup(groupList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ConceptMap.toProto(): ConceptMap {
    val protoValue =
      ConceptMap.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setIdentifier(identifier.toProto())
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setStatus(
          ConceptMap.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(
                status
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
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
        .setSource(source.conceptMapSourceToProto())
        .setTarget(target.conceptMapTargetToProto())
        .addAllGroup(group.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent.toProto():
    ConceptMap.Group {
    val protoValue =
      ConceptMap.Group.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSource(sourceElement.toProto())
        .setSourceVersion(sourceVersionElement.toProto())
        .setTarget(targetElement.toProto())
        .setTargetVersion(targetVersionElement.toProto())
        .addAllElement(element.map { it.toProto() })
        .setUnmapped(unmapped.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent.toProto():
    ConceptMap.Group.SourceElement {
    val protoValue =
      ConceptMap.Group.SourceElement.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .setDisplay(displayElement.toProto())
        .addAllTarget(target.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent.toProto():
    ConceptMap.Group.SourceElement.TargetElement {
    val protoValue =
      ConceptMap.Group.SourceElement.TargetElement.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(codeElement.toProto())
        .setDisplay(displayElement.toProto())
        .setEquivalence(
          ConceptMap.Group.SourceElement.TargetElement.EquivalenceCode.newBuilder()
            .setValue(
              ConceptMapEquivalenceCode.Value.valueOf(
                equivalence
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setComment(commentElement.toProto())
        .addAllDependsOn(dependsOn.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent.toProto():
    ConceptMap.Group.SourceElement.TargetElement.OtherElement {
    val protoValue =
      ConceptMap.Group.SourceElement.TargetElement.OtherElement.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setProperty(propertyElement.toProto())
        .setSystem(systemElement.toProto())
        .setValue(valueElement.toProto())
        .setDisplay(displayElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent.toProto():
    ConceptMap.Group.Unmapped {
    val protoValue =
      ConceptMap.Group.Unmapped.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setMode(
          ConceptMap.Group.Unmapped.ModeCode.newBuilder()
            .setValue(
              ConceptMapGroupUnmappedModeCode.Value.valueOf(
                mode
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setCode(codeElement.toProto())
        .setDisplay(displayElement.toProto())
        .setUrl(urlElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun ConceptMap.Group.toHapi(): org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSourceElement(source.toHapi())
    hapiValue.setSourceVersionElement(sourceVersion.toHapi())
    hapiValue.setTargetElement(target.toHapi())
    hapiValue.setTargetVersionElement(targetVersion.toHapi())
    hapiValue.setElement(elementList.map { it.toHapi() })
    hapiValue.setUnmapped(unmapped.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.SourceElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setTarget(targetList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.SourceElement.TargetElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setEquivalence(
      Enumerations.ConceptMapEquivalence.valueOf(
        equivalence
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setDependsOn(dependsOnList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.SourceElement.TargetElement.OtherElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPropertyElement(property.toHapi())
    hapiValue.setSystemElement(system.toHapi())
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ConceptMap.Group.Unmapped.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setMode(
      org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedMode.valueOf(
        mode
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    return hapiValue
  }
}
