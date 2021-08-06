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
import com.google.fhir.r4.core.ExampleScenario
import com.google.fhir.r4.core.ExampleScenario.Actor
import com.google.fhir.r4.core.ExampleScenario.Instance
import com.google.fhir.r4.core.ExampleScenario.Process
import com.google.fhir.r4.core.ExampleScenario.Process.Step
import com.google.fhir.r4.core.ExampleScenarioActorTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PublicationStatusCode
import com.google.fhir.r4.core.ResourceTypeCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Enumerations

public object ExampleScenarioConverter {
  @JvmStatic
  public fun ExampleScenario.toHapi(): org.hl7.fhir.r4.model.ExampleScenario {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setStatus(Enumerations.PublicationStatus.valueOf(status.value.name.replace("_", "")))
    hapiValue.setExperimentalElement(experimental.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setPublisherElement(publisher.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setUseContext(useContextList.map { it.toHapi() })
    hapiValue.setJurisdiction(jurisdictionList.map { it.toHapi() })
    hapiValue.setCopyrightElement(copyright.toHapi())
    hapiValue.setPurposeElement(purpose.toHapi())
    hapiValue.setActor(actorList.map { it.toHapi() })
    hapiValue.setInstance(instanceList.map { it.toHapi() })
    hapiValue.setProcess(processList.map { it.toHapi() })
    hapiValue.setWorkflow(workflowList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ExampleScenario.toProto(): ExampleScenario {
    val protoValue =
      ExampleScenario.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .addAllIdentifier(identifier.map { it.toProto() })
        .setVersion(versionElement.toProto())
        .setName(nameElement.toProto())
        .setStatus(
          ExampleScenario.StatusCode.newBuilder()
            .setValue(
              PublicationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setExperimental(experimentalElement.toProto())
        .setDate(dateElement.toProto())
        .setPublisher(publisherElement.toProto())
        .addAllContact(contact.map { it.toProto() })
        .addAllUseContext(useContext.map { it.toProto() })
        .addAllJurisdiction(jurisdiction.map { it.toProto() })
        .setCopyright(copyrightElement.toProto())
        .setPurpose(purposeElement.toProto())
        .addAllActor(actor.map { it.toProto() })
        .addAllInstance(instance.map { it.toProto() })
        .addAllProcess(process.map { it.toProto() })
        .addAllWorkflow(workflow.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorComponent.toProto():
    ExampleScenario.Actor {
    val protoValue =
      ExampleScenario.Actor.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setActorId(actorIdElement.toProto())
        .setType(
          ExampleScenario.Actor.TypeCode.newBuilder()
            .setValue(
              ExampleScenarioActorTypeCode.Value.valueOf(
                type.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setName(nameElement.toProto())
        .setDescription(descriptionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceComponent.toProto():
    ExampleScenario.Instance {
    val protoValue =
      ExampleScenario.Instance.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setResourceId(resourceIdElement.toProto())
        .setResourceType(
          ExampleScenario.Instance.ResourceTypeCode.newBuilder()
            .setValue(
              ResourceTypeCode.Value.valueOf(resourceType.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setName(nameElement.toProto())
        .setDescription(descriptionElement.toProto())
        .addAllVersion(version.map { it.toProto() })
        .addAllContainedInstance(containedInstance.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceVersionComponent.toProto():
    ExampleScenario.Instance.Version {
    val protoValue =
      ExampleScenario.Instance.Version.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setVersionId(versionIdElement.toProto())
        .setDescription(descriptionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceContainedInstanceComponent.toProto():
    ExampleScenario.Instance.ContainedInstance {
    val protoValue =
      ExampleScenario.Instance.ContainedInstance.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setResourceId(resourceIdElement.toProto())
        .setVersionId(versionIdElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessComponent.toProto():
    ExampleScenario.Process {
    val protoValue =
      ExampleScenario.Process.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setTitle(titleElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setPreConditions(preConditionsElement.toProto())
        .setPostConditions(postConditionsElement.toProto())
        .addAllStep(step.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepComponent.toProto():
    ExampleScenario.Process.Step {
    val protoValue =
      ExampleScenario.Process.Step.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPause(pauseElement.toProto())
        .setOperation(operation.toProto())
        .addAllAlternative(alternative.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepOperationComponent.toProto():
    ExampleScenario.Process.Step.Operation {
    val protoValue =
      ExampleScenario.Process.Step.Operation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setNumber(numberElement.toProto())
        .setType(typeElement.toProto())
        .setName(nameElement.toProto())
        .setInitiator(initiatorElement.toProto())
        .setReceiver(receiverElement.toProto())
        .setDescription(descriptionElement.toProto())
        .setInitiatorActive(initiatorActiveElement.toProto())
        .setReceiverActive(receiverActiveElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepAlternativeComponent.toProto():
    ExampleScenario.Process.Step.Alternative {
    val protoValue =
      ExampleScenario.Process.Step.Alternative.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setTitle(titleElement.toProto())
        .setDescription(descriptionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun ExampleScenario.Actor.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setActorIdElement(actorId.toHapi())
    hapiValue.setType(
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorType.valueOf(
        type.value.name.replace("_", "")
      )
    )
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ExampleScenario.Instance.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setResourceIdElement(resourceId.toHapi())
    hapiValue.setResourceType(
      org.hl7.fhir.r4.model.ExampleScenario.FHIRResourceType.valueOf(
        resourceType.value.name.replace("_", "")
      )
    )
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setVersion(versionList.map { it.toHapi() })
    hapiValue.setContainedInstance(containedInstanceList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun ExampleScenario.Instance.Version.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceVersionComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceVersionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setVersionIdElement(versionId.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ExampleScenario.Instance.ContainedInstance.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceContainedInstanceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceContainedInstanceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setResourceIdElement(resourceId.toHapi())
    hapiValue.setVersionIdElement(versionId.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ExampleScenario.Process.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setPreConditionsElement(preConditions.toHapi())
    hapiValue.setPostConditionsElement(postConditions.toHapi())
    hapiValue.setStep(stepList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun ExampleScenario.Process.Step.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPauseElement(pause.toHapi())
    hapiValue.setOperation(operation.toHapi())
    hapiValue.setAlternative(alternativeList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun ExampleScenario.Process.Step.Operation.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepOperationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepOperationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNumberElement(number.toHapi())
    hapiValue.setTypeElement(type.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setInitiatorElement(initiator.toHapi())
    hapiValue.setReceiverElement(receiver.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setInitiatorActiveElement(initiatorActive.toHapi())
    hapiValue.setReceiverActiveElement(receiverActive.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun ExampleScenario.Process.Step.Alternative.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepAlternativeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepAlternativeComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    return hapiValue
  }
}
