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
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setKind(
      org.hl7.fhir.r4.model.OperationDefinition.OperationKind.valueOf(
        kind.value.name.replace("_", "")
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
    hapiValue.setAffectsStateElement(affectsState.toHapi())
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setBaseElement(base.toHapi())
    resourceList.forEach { hapiValue.addResource(it.value.name) }
    hapiValue.setSystemElement(system.toHapi())
    hapiValue.setTypeElement(type.toHapi())
    hapiValue.setInstanceElement(instance.toHapi())
    hapiValue.setInputProfileElement(inputProfile.toHapi())
    hapiValue.setOutputProfileElement(outputProfile.toHapi())
    hapiValue.setParameter(parameterList.map { it.toHapi() })
    hapiValue.setOverload(overloadList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.OperationDefinition.toProto(): OperationDefinition {
    val protoValue =
      OperationDefinition.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setTitle(titleElement.toProto())
        .setStatus(
          OperationDefinition.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setKind(
          OperationDefinition.KindCode.newBuilder()
            .setValue(
              OperationKindCode.Value.valueOf(kind.toCode().replace("-", "_").toUpperCase())
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
        .setAffectsState(affectsStateElement.toProto())
        .setCode(codeElement.toProto())
        .setComment(commentElement.toProto())
        .setBase(baseElement.toProto())
        .addAllResource(
          resource.map {
            OperationDefinition.ResourceCode.newBuilder()
              .setValue(ResourceTypeCode.Value.valueOf(it.valueAsString))
              .build()
          }
        )
        .setSystem(systemElement.toProto())
        .setType(typeElement.toProto())
        .setInstance(instanceElement.toProto())
        .setInputProfile(inputProfileElement.toProto())
        .setOutputProfile(outputProfileElement.toProto())
        .addAllParameter(parameter.map { it.toProto() })
        .addAllOverload(overload.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterComponent.toProto():
    OperationDefinition.Parameter {
    val protoValue =
      OperationDefinition.Parameter.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setUse(
          OperationDefinition.Parameter.UseCode.newBuilder()
            .setValue(
              OperationParameterUseCode.Value.valueOf(use.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setMin(minElement.toProto())
        .setMax(maxElement.toProto())
        .setDocumentation(documentationElement.toProto())
        .setType(
          OperationDefinition.Parameter.TypeCode.newBuilder()
            .setValue(FHIRAllTypesValueSet.Value.valueOf(type))
            .build()
        )
        .addAllTargetProfile(targetProfile.map { it.toProto() })
        .setSearchType(
          OperationDefinition.Parameter.SearchTypeCode.newBuilder()
            .setValue(
              SearchParamTypeCode.Value.valueOf(searchType.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setBinding(binding.toProto())
        .addAllReferencedFrom(referencedFrom.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterBindingComponent.toProto():
    OperationDefinition.Parameter.Binding {
    val protoValue =
      OperationDefinition.Parameter.Binding.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setStrength(
          OperationDefinition.Parameter.Binding.StrengthCode.newBuilder()
            .setValue(
              BindingStrengthCode.Value.valueOf(strength.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setValueSet(valueSetElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterReferencedFromComponent.toProto():
    OperationDefinition.Parameter.ReferencedFrom {
    val protoValue =
      OperationDefinition.Parameter.ReferencedFrom.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSource(sourceElement.toProto())
        .setSourceId(sourceIdElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent.toProto():
    OperationDefinition.Overload {
    val protoValue =
      OperationDefinition.Overload.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllParameterName(parameterName.map { it.toProto() })
        .setComment(commentElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun OperationDefinition.Parameter.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setUse(
      org.hl7.fhir.r4.model.OperationDefinition.OperationParameterUse.valueOf(
        use.value.name.replace("_", "")
      )
    )
    hapiValue.setMinElement(min.toHapi())
    hapiValue.setMaxElement(max.toHapi())
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setType(type.value.name)
    hapiValue.setTargetProfile(targetProfileList.map { it.toHapi() })
    hapiValue.setSearchType(
      Enumerations.SearchParamType.valueOf(searchType.value.name.replace("_", ""))
    )
    hapiValue.setBinding(binding.toHapi())
    hapiValue.setReferencedFrom(referencedFromList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun OperationDefinition.Parameter.Binding.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterBindingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterBindingComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setStrength(
      Enumerations.BindingStrength.valueOf(strength.value.name.replace("_", ""))
    )
    hapiValue.setValueSetElement(valueSet.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun OperationDefinition.Parameter.ReferencedFrom.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionParameterReferencedFromComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.OperationDefinition
        .OperationDefinitionParameterReferencedFromComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSourceElement(source.toHapi())
    hapiValue.setSourceIdElement(sourceId.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun OperationDefinition.Overload.toHapi():
    org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent {
    val hapiValue = org.hl7.fhir.r4.model.OperationDefinition.OperationDefinitionOverloadComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setParameterName(parameterNameList.map { it.toHapi() })
    hapiValue.setCommentElement(comment.toHapi())
    return hapiValue
  }
}
