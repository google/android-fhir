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
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType

object ConceptMapConverter {
  private fun ConceptMap.SourceX.conceptMapSourceToHapi(): Type {
    if (hasUri()) {
      return (this.uri).toHapi()
    }
    if (hasCanonical()) {
      return (this.canonical).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ConceptMap.source[x]")
  }

  private fun Type.conceptMapSourceToProto(): ConceptMap.SourceX {
    val protoValue = ConceptMap.SourceX.newBuilder()
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    return protoValue.build()
  }

  private fun ConceptMap.TargetX.conceptMapTargetToHapi(): Type {
    if (hasUri()) {
      return (this.uri).toHapi()
    }
    if (hasCanonical()) {
      return (this.canonical).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ConceptMap.target[x]")
  }

  private fun Type.conceptMapTargetToProto(): ConceptMap.TargetX {
    val protoValue = ConceptMap.TargetX.newBuilder()
    if (this is UriType) {
      protoValue.uri = this.toProto()
    }
    if (this is CanonicalType) {
      protoValue.canonical = this.toProto()
    }
    return protoValue.build()
  }

  fun ConceptMap.toHapi(): org.hl7.fhir.r4.model.ConceptMap {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap()
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
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
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
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasSource()) {
      hapiValue.source = source.conceptMapSourceToHapi()
    }
    if (hasTarget()) {
      hapiValue.target = target.conceptMapTargetToHapi()
    }
    if (groupCount > 0) {
      hapiValue.group = groupList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ConceptMap.toProto(): ConceptMap {
    val protoValue = ConceptMap.newBuilder()
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
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
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
    if (hasStatus()) {
      protoValue.status =
        ConceptMap.StatusCode.newBuilder()
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
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasSource()) {
      protoValue.source = source.conceptMapSourceToProto()
    }
    if (hasTarget()) {
      protoValue.target = target.conceptMapTargetToProto()
    }
    if (hasGroup()) {
      protoValue.addAllGroup(group.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent.toProto():
    ConceptMap.Group {
    val protoValue = ConceptMap.Group.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.source = sourceElement.toProto()
    }
    if (hasSourceVersion()) {
      protoValue.sourceVersion = sourceVersionElement.toProto()
    }
    if (hasTarget()) {
      protoValue.target = targetElement.toProto()
    }
    if (hasTargetVersion()) {
      protoValue.targetVersion = targetVersionElement.toProto()
    }
    if (hasElement()) {
      protoValue.addAllElement(element.map { it.toProto() })
    }
    if (hasUnmapped()) {
      protoValue.unmapped = unmapped.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent.toProto():
    ConceptMap.Group.SourceElement {
    val protoValue = ConceptMap.Group.SourceElement.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasDisplay()) {
      protoValue.display = displayElement.toProto()
    }
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent.toProto():
    ConceptMap.Group.SourceElement.TargetElement {
    val protoValue = ConceptMap.Group.SourceElement.TargetElement.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasDisplay()) {
      protoValue.display = displayElement.toProto()
    }
    if (hasEquivalence()) {
      protoValue.equivalence =
        ConceptMap.Group.SourceElement.TargetElement.EquivalenceCode.newBuilder()
          .setValue(
            ConceptMapEquivalenceCode.Value.valueOf(
              equivalence.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasDependsOn()) {
      protoValue.addAllDependsOn(dependsOn.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent.toProto():
    ConceptMap.Group.SourceElement.TargetElement.OtherElement {
    val protoValue = ConceptMap.Group.SourceElement.TargetElement.OtherElement.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.property = propertyElement.toProto()
    }
    if (hasSystem()) {
      protoValue.system = systemElement.toProto()
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    if (hasDisplay()) {
      protoValue.display = displayElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent.toProto():
    ConceptMap.Group.Unmapped {
    val protoValue = ConceptMap.Group.Unmapped.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasMode()) {
      protoValue.mode =
        ConceptMap.Group.Unmapped.ModeCode.newBuilder()
          .setValue(
            ConceptMapGroupUnmappedModeCode.Value.valueOf(
              mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasDisplay()) {
      protoValue.display = displayElement.toProto()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    return protoValue.build()
  }

  private fun ConceptMap.Group.toHapi(): org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSource()) {
      hapiValue.sourceElement = source.toHapi()
    }
    if (hasSourceVersion()) {
      hapiValue.sourceVersionElement = sourceVersion.toHapi()
    }
    if (hasTarget()) {
      hapiValue.targetElement = target.toHapi()
    }
    if (hasTargetVersion()) {
      hapiValue.targetVersionElement = targetVersion.toHapi()
    }
    if (elementCount > 0) {
      hapiValue.element = elementList.map { it.toHapi() }
    }
    if (hasUnmapped()) {
      hapiValue.unmapped = unmapped.toHapi()
    }
    return hapiValue
  }

  private fun ConceptMap.Group.SourceElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.SourceElementComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasDisplay()) {
      hapiValue.displayElement = display.toHapi()
    }
    if (targetCount > 0) {
      hapiValue.target = targetList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ConceptMap.Group.SourceElement.TargetElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.TargetElementComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasDisplay()) {
      hapiValue.displayElement = display.toHapi()
    }
    if (hasEquivalence()) {
      hapiValue.equivalence =
        Enumerations.ConceptMapEquivalence.valueOf(
          equivalence.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (dependsOnCount > 0) {
      hapiValue.dependsOn = dependsOnList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ConceptMap.Group.SourceElement.TargetElement.OtherElement.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.OtherElementComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasProperty()) {
      hapiValue.propertyElement = property.toHapi()
    }
    if (hasSystem()) {
      hapiValue.systemElement = system.toHapi()
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    if (hasDisplay()) {
      hapiValue.displayElement = display.toHapi()
    }
    return hapiValue
  }

  private fun ConceptMap.Group.Unmapped.toHapi():
    org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent {
    val hapiValue = org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasMode()) {
      hapiValue.mode =
        org.hl7.fhir.r4.model.ConceptMap.ConceptMapGroupUnmappedMode.valueOf(
          mode.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasDisplay()) {
      hapiValue.displayElement = display.toHapi()
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    return hapiValue
  }
}
