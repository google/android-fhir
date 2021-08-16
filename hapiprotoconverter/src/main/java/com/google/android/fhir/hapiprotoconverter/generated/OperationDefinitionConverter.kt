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
import com.google.fhir.r4.core.BindingStrengthCode
import com.google.fhir.r4.core.FHIRAllTypesValueSet
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.OperationDefinition
import com.google.fhir.r4.core.OperationDefinition.Parameter
import com.google.fhir.r4.core.OperationDefinition.Parameter.Binding
import com.google.fhir.r4.core.OperationKindCode
import com.google.fhir.r4.core.OperationParameterUseCode
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.SearchParamTypeCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

object OperationDefinitionConverter {
  @JvmStatic
  fun OperationDefinition.toHapi(): org.hl7.fhir.r4.model.OperationDefinition {
    val hapiValue = org.hl7.fhir.r4.model.OperationDefinition()
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
    hapiValue.kind =
      org.hl7.fhir.r4.model.OperationDefinition.OperationKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
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
    if (hasAffectsState()) {
      hapiValue.affectsStateElement = affectsState.toHapi()
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (hasBase()) {
      hapiValue.baseElement = base.toHapi()
    }
    resourceList.forEach { hapiValue.addResource(it.value.name.hapiCodeCheck()) }
    if (hasSystem()) {
      hapiValue.systemElement = system.toHapi()
    }
    if (hasType()) {
      hapiValue.typeElement = type.toHapi()
    }
    if (hasInstance()) {
      hapiValue.instanceElement = instance.toHapi()
    }
    if (hasInputProfile()) {
      hapiValue.inputProfileElement = inputProfile.toHapi()
    }
    if (hasOutputProfile()) {
      hapiValue.outputProfileElement = outputProfile.toHapi()
    }
    if (parameterCount > 0) {
      hapiValue.parameter = parameterList.map { it.toHapi() }
    }
    if (overloadCount > 0) {
      hapiValue.overload = overloadList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.OperationDefinition.toProto(): OperationDefinition {
    val protoValue = OperationDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
      OperationDefinition.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.kind =
      OperationDefinition.KindCode.newBuilder()
        .setValue(
          OperationKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
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
    if (hasAffectsState()) {
      protoValue.affectsState = affectsStateElement.toProto()
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasBase()) {
      protoValue.base = baseElement.toProto()
    }
    protoValue.addAllResource(
      resource.map {
        OperationDefinition.ResourceCode.newBuilder()
          .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString.protoCodeCheck()))
          .build()
      }
    )
    if (hasSystem()) {
      protoValue.system = systemElement.toProto()
    }
    if (hasType()) {
      protoValue.type = typeElement.toProto()
    }
    if (hasInstance()) {
      protoValue.instance = instanceElement.toProto()
    }
    if (hasInputProfile()) {
      protoValue.inputProfile = inputProfileElement.toProto()
    }
    if (hasOutputProfile()) {
      protoValue.outputProfile = outputProfileElement.toProto()
    }
    if (hasParameter()) {
      protoValue.addAllParameter(parameter.map { it.toProto() })
    }
    if (hasOverload()) {
      protoValue.addAllOverload(overload.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterComponent.toProto():
    OperationDefinition.Parameter {
    val protoValue =
      OperationDefinition.Parameter.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    protoValue.use =
      OperationDefinition.Parameter.UseCode.newBuilder()
        .setValue(
          OperationParameterUseCode.Value.valueOf(
            use.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasMin()) {
      protoValue.min = minElement.toProto()
    }
    if (hasMax()) {
      protoValue.max = maxElement.toProto()
    }
    if (hasDocumentation()) {
      protoValue.documentation = documentationElement.toProto()
    }
    protoValue.type =
      OperationDefinition.Parameter.TypeCode.newBuilder()
        .setValue(FHIRAllTypesValueSet.Value.valueOf(type))
        .build()
    if (hasTargetProfile()) {
      protoValue.addAllTargetProfile(targetProfile.map { it.toProto() })
    }
    protoValue.searchType =
      OperationDefinition.Parameter.SearchTypeCode.newBuilder()
        .setValue(
          SearchParamTypeCode.Value.valueOf(
            searchType.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasBinding()) {
      protoValue.binding = binding.toProto()
    }
    if (hasReferencedFrom()) {
      protoValue.addAllReferencedFrom(referencedFrom.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterBindingComponent.toProto():
    OperationDefinition.Parameter.Binding {
    val protoValue =
      OperationDefinition.Parameter.Binding.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.strength =
      OperationDefinition.Parameter.Binding.StrengthCode.newBuilder()
        .setValue(
          BindingStrengthCode.Value.valueOf(
            strength.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasValueSet()) {
      protoValue.valueSet = valueSetElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterReferencedFromComponent.toProto():
    OperationDefinition.Parameter.ReferencedFrom {
    val protoValue =
      OperationDefinition.Parameter.ReferencedFrom.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.source = sourceElement.toProto()
    }
    if (hasSourceId()) {
      protoValue.sourceId = sourceIdElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent.toProto():
    OperationDefinition.Overload {
    val protoValue =
      OperationDefinition.Overload.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasParameterName()) {
      protoValue.addAllParameterName(parameterName.map { it.toProto() })
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun OperationDefinition.Parameter.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterComponent()
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
    hapiValue.use =
      org.hl7.fhir.r4.model.OperationDefinition.OperationParameterUse.valueOf(
        use.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasMin()) {
      hapiValue.minElement = min.toHapi()
    }
    if (hasMax()) {
      hapiValue.maxElement = max.toHapi()
    }
    if (hasDocumentation()) {
      hapiValue.documentationElement = documentation.toHapi()
    }
    hapiValue.type = type.value.name
    if (targetProfileCount > 0) {
      hapiValue.targetProfile = targetProfileList.map { it.toHapi() }
    }
    hapiValue.searchType =
      Enumerations.SearchParamType.valueOf(searchType.value.name.hapiCodeCheck().replace("_", ""))
    if (hasBinding()) {
      hapiValue.binding = binding.toHapi()
    }
    if (referencedFromCount > 0) {
      hapiValue.referencedFrom = referencedFromList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun OperationDefinition.Parameter.Binding.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterBindingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterBindingComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.strength =
      Enumerations.BindingStrength.valueOf(strength.value.name.hapiCodeCheck().replace("_", ""))
    if (hasValueSet()) {
      hapiValue.valueSetElement = valueSet.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun OperationDefinition.Parameter.ReferencedFrom.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterReferencedFromComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.OperationDefinition
        .OperationDefinitionParameterReferencedFromComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSource()) {
      hapiValue.sourceElement = source.toHapi()
    }
    if (hasSourceId()) {
      hapiValue.sourceIdElement = sourceId.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun OperationDefinition.Overload.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent {
    val hapiValue = org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (parameterNameCount > 0) {
      hapiValue.parameterName = parameterNameList.map { it.toHapi() }
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    return hapiValue
  }
}
