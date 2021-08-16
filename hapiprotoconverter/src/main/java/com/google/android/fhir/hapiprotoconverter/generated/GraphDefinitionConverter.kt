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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
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
import com.google.fhir.r4.core.CompartmentTypeCode
import com.google.fhir.r4.core.GraphCompartmentRuleCode
import com.google.fhir.r4.core.GraphCompartmentUseCode
import com.google.fhir.r4.core.GraphDefinition
import com.google.fhir.r4.core.GraphDefinition.Link
import com.google.fhir.r4.core.GraphDefinition.Link.Target
import com.google.fhir.r4.core.GraphDefinition.Link.Target.Compartment
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object GraphDefinitionConverter {
  @JvmStatic
  fun GraphDefinition.toHapi(): org.hl7.fhir.r4.model.GraphDefinition {
    val hapiValue = org.hl7.fhir.r4.model.GraphDefinition()
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
      hapiValue.start = start.value.name
    if (hasProfile()) {
        hapiValue.profileElement = profile.toHapi()
    }
    if (linkCount > 0) {
        hapiValue.link = linkList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.GraphDefinition.toProto(): GraphDefinition {
    val protoValue = GraphDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = GraphDefinition.StatusCode.newBuilder()
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
      protoValue.start =
          GraphDefinition.StartCode.newBuilder().setValue(ResourceTypeCode.Value.valueOf(start)).build()
    if (hasProfile()) {
        protoValue.profile = profileElement.toProto()
    }
    if (hasLink()) {
      protoValue.addAllLink(link.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkComponent.toProto():
    GraphDefinition.Link {
    val protoValue = GraphDefinition.Link.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPath()) {
        protoValue.path = pathElement.toProto()
    }
    if (hasSliceName()) {
        protoValue.sliceName = sliceNameElement.toProto()
    }
    if (hasMin()) {
        protoValue.min = minElement.toProto()
    }
    if (hasMax()) {
        protoValue.max = maxElement.toProto()
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetComponent.toProto():
    GraphDefinition.Link.Target {
    val protoValue =
      GraphDefinition.Link.Target.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.type = GraphDefinition.Link.Target.TypeCode.newBuilder()
          .setValue(ResourceTypeCode.Value.valueOf(type))
          .build()
    if (hasParams()) {
        protoValue.params = paramsElement.toProto()
    }
    if (hasProfile()) {
        protoValue.profile = profileElement.toProto()
    }
    if (hasCompartment()) {
      protoValue.addAllCompartment(compartment.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetCompartmentComponent.toProto():
    GraphDefinition.Link.Target.Compartment {
    val protoValue =
      GraphDefinition.Link.Target.Compartment.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.use = GraphDefinition.Link.Target.Compartment.UseCode.newBuilder()
          .setValue(
              GraphCompartmentUseCode.Value.valueOf(
                  use.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.code = GraphDefinition.Link.Target.Compartment.CodeType.newBuilder()
          .setValue(
              CompartmentTypeCode.Value.valueOf(
                  code.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.rule = GraphDefinition.Link.Target.Compartment.RuleCode.newBuilder()
          .setValue(
              GraphCompartmentRuleCode.Value.valueOf(
                  rule.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasExpression()) {
        protoValue.expression = expressionElement.toProto()
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun GraphDefinition.Link.toHapi():
    org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPath()) {
        hapiValue.pathElement = path.toHapi()
    }
    if (hasSliceName()) {
        hapiValue.sliceNameElement = sliceName.toHapi()
    }
    if (hasMin()) {
        hapiValue.minElement = min.toHapi()
    }
    if (hasMax()) {
        hapiValue.maxElement = max.toHapi()
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (targetCount > 0) {
        hapiValue.target = targetList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun GraphDefinition.Link.Target.toHapi():
    org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetComponent {
    val hapiValue = org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.type = type.value.name
    if (hasParams()) {
        hapiValue.paramsElement = params.toHapi()
    }
    if (hasProfile()) {
        hapiValue.profileElement = profile.toHapi()
    }
    if (compartmentCount > 0) {
        hapiValue.compartment = compartmentList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun GraphDefinition.Link.Target.Compartment.toHapi():
    org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetCompartmentComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.GraphDefinition.GraphDefinitionLinkTargetCompartmentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.use = org.hl7.fhir.r4.model.GraphDefinition.GraphCompartmentUse.valueOf(
          use.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.code = org.hl7.fhir.r4.model.GraphDefinition.CompartmentCode.valueOf(
          code.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.rule = org.hl7.fhir.r4.model.GraphDefinition.GraphCompartmentRule.valueOf(
          rule.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasExpression()) {
        hapiValue.expressionElement = expression.toHapi()
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }
}
