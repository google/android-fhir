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

public object OperationDefinitionConverter {
  @JvmStatic
  public fun OperationDefinition.toHapi(): org.hl7.fhir.r4.model.OperationDefinition {
    val hapiValue = org.hl7.fhir.r4.model.OperationDefinition()
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
    hapiValue.setKind(
      org.hl7.fhir.r4.model.OperationDefinition.OperationKind.valueOf(
        kind.value.name.hapiCodeCheck().replace("_", "")
      )
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
    if (hasAffectsState()) {
      hapiValue.setAffectsStateElement(affectsState.toHapi())
    }
    if (hasCode()) {
      hapiValue.setCodeElement(code.toHapi())
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    if (hasBase()) {
      hapiValue.setBaseElement(base.toHapi())
    }
    resourceList.forEach { hapiValue.addResource(it.value.name.hapiCodeCheck()) }
    if (hasSystem()) {
      hapiValue.setSystemElement(system.toHapi())
    }
    if (hasType()) {
      hapiValue.setTypeElement(type.toHapi())
    }
    if (hasInstance()) {
      hapiValue.setInstanceElement(instance.toHapi())
    }
    if (hasInputProfile()) {
      hapiValue.setInputProfileElement(inputProfile.toHapi())
    }
    if (hasOutputProfile()) {
      hapiValue.setOutputProfileElement(outputProfile.toHapi())
    }
    if (parameterCount > 0) {
      hapiValue.setParameter(parameterList.map { it.toHapi() })
    }
    if (overloadCount > 0) {
      hapiValue.setOverload(overloadList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.OperationDefinition.toProto(): OperationDefinition {
    val protoValue = OperationDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
      OperationDefinition.StatusCode.newBuilder()
        .setValue(
          PublicationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.setKind(
      OperationDefinition.KindCode.newBuilder()
        .setValue(
          OperationKindCode.Value.valueOf(
            kind.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
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
    if (hasAffectsState()) {
      protoValue.setAffectsState(affectsStateElement.toProto())
    }
    if (hasCode()) {
      protoValue.setCode(codeElement.toProto())
    }
    if (hasComment()) {
      protoValue.setComment(commentElement.toProto())
    }
    if (hasBase()) {
      protoValue.setBase(baseElement.toProto())
    }
    protoValue.addAllResource(
      resource.map {
        OperationDefinition.ResourceCode.newBuilder()
          .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString.protoCodeCheck()))
          .build()
      }
    )
    if (hasSystem()) {
      protoValue.setSystem(systemElement.toProto())
    }
    if (hasType()) {
      protoValue.setType(typeElement.toProto())
    }
    if (hasInstance()) {
      protoValue.setInstance(instanceElement.toProto())
    }
    if (hasInputProfile()) {
      protoValue.setInputProfile(inputProfileElement.toProto())
    }
    if (hasOutputProfile()) {
      protoValue.setOutputProfile(outputProfileElement.toProto())
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
      protoValue.setName(nameElement.toProto())
    }
    protoValue.setUse(
      OperationDefinition.Parameter.UseCode.newBuilder()
        .setValue(
          OperationParameterUseCode.Value.valueOf(
            use.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasMin()) {
      protoValue.setMin(minElement.toProto())
    }
    if (hasMax()) {
      protoValue.setMax(maxElement.toProto())
    }
    if (hasDocumentation()) {
      protoValue.setDocumentation(documentationElement.toProto())
    }
    protoValue.setType(
      OperationDefinition.Parameter.TypeCode.newBuilder()
        .setValue(FHIRAllTypesValueSet.Value.valueOf(type))
        .build()
    )
    if (hasTargetProfile()) {
      protoValue.addAllTargetProfile(targetProfile.map { it.toProto() })
    }
    protoValue.setSearchType(
      OperationDefinition.Parameter.SearchTypeCode.newBuilder()
        .setValue(
          SearchParamTypeCode.Value.valueOf(
            searchType.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasBinding()) {
      protoValue.setBinding(binding.toProto())
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
    protoValue.setStrength(
      OperationDefinition.Parameter.Binding.StrengthCode.newBuilder()
        .setValue(
          BindingStrengthCode.Value.valueOf(
            strength.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasValueSet()) {
      protoValue.setValueSet(valueSetElement.toProto())
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
      protoValue.setSource(sourceElement.toProto())
    }
    if (hasSourceId()) {
      protoValue.setSourceId(sourceIdElement.toProto())
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
      protoValue.setComment(commentElement.toProto())
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    hapiValue.setUse(
      org.hl7.fhir.r4.model.OperationDefinition.OperationParameterUse.valueOf(
        use.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasMin()) {
      hapiValue.setMinElement(min.toHapi())
    }
    if (hasMax()) {
      hapiValue.setMaxElement(max.toHapi())
    }
    if (hasDocumentation()) {
      hapiValue.setDocumentationElement(documentation.toHapi())
    }
    hapiValue.setType(type.value.name)
    if (targetProfileCount > 0) {
      hapiValue.setTargetProfile(targetProfileList.map { it.toHapi() })
    }
    hapiValue.setSearchType(
      Enumerations.SearchParamType.valueOf(searchType.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasBinding()) {
      hapiValue.setBinding(binding.toHapi())
    }
    if (referencedFromCount > 0) {
      hapiValue.setReferencedFrom(referencedFromList.map { it.toHapi() })
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setStrength(
      Enumerations.BindingStrength.valueOf(strength.value.name.hapiCodeCheck().replace("_", ""))
    )
    if (hasValueSet()) {
      hapiValue.setValueSetElement(valueSet.toHapi())
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSource()) {
      hapiValue.setSourceElement(source.toHapi())
    }
    if (hasSourceId()) {
      hapiValue.setSourceIdElement(sourceId.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun OperationDefinition.Overload.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent {
    val hapiValue = org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (parameterNameCount > 0) {
      hapiValue.setParameterName(parameterNameList.map { it.toHapi() })
    }
    if (hasComment()) {
      hapiValue.setCommentElement(comment.toHapi())
    }
    return hapiValue
  }
}
