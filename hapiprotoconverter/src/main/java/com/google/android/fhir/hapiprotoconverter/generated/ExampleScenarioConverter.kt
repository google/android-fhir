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
import org.hl7.fhir.r4.model.Enumerations

object ExampleScenarioConverter {
  fun ExampleScenario.toHapi(): org.hl7.fhir.r4.model.ExampleScenario {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
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
    if (useContextCount > 0) {
      hapiValue.useContext = useContextList.map { it.toHapi() }
    }
    if (jurisdictionCount > 0) {
      hapiValue.jurisdiction = jurisdictionList.map { it.toHapi() }
    }
    if (hasCopyright()) {
      hapiValue.copyrightElement = copyright.toHapi()
    }
    if (hasPurpose()) {
      hapiValue.purposeElement = purpose.toHapi()
    }
    if (actorCount > 0) {
      hapiValue.actor = actorList.map { it.toHapi() }
    }
    if (instanceCount > 0) {
      hapiValue.instance = instanceList.map { it.toHapi() }
    }
    if (processCount > 0) {
      hapiValue.process = processList.map { it.toHapi() }
    }
    if (workflowCount > 0) {
      hapiValue.workflow = workflowList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ExampleScenario.toProto(): ExampleScenario {
    val protoValue = ExampleScenario.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    protoValue.status =
      ExampleScenario.StatusCode.newBuilder()
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
    if (hasUseContext()) {
      protoValue.addAllUseContext(useContext.map { it.toProto() })
    }
    if (hasJurisdiction()) {
      protoValue.addAllJurisdiction(jurisdiction.map { it.toProto() })
    }
    if (hasCopyright()) {
      protoValue.copyright = copyrightElement.toProto()
    }
    if (hasPurpose()) {
      protoValue.purpose = purposeElement.toProto()
    }
    if (hasActor()) {
      protoValue.addAllActor(actor.map { it.toProto() })
    }
    if (hasInstance()) {
      protoValue.addAllInstance(instance.map { it.toProto() })
    }
    if (hasProcess()) {
      protoValue.addAllProcess(process.map { it.toProto() })
    }
    if (hasWorkflow()) {
      protoValue.addAllWorkflow(workflow.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorComponent.toProto():
    ExampleScenario.Actor {
    val protoValue = ExampleScenario.Actor.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasActorId()) {
      protoValue.actorId = actorIdElement.toProto()
    }
    protoValue.type =
      ExampleScenario.Actor.TypeCode.newBuilder()
        .setValue(
          ExampleScenarioActorTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceComponent.toProto():
    ExampleScenario.Instance {
    val protoValue = ExampleScenario.Instance.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasResourceId()) {
      protoValue.resourceId = resourceIdElement.toProto()
    }
    protoValue.resourceType =
      ExampleScenario.Instance.ResourceTypeCode.newBuilder()
        .setValue(
          ResourceTypeCode.Value.valueOf(
            resourceType.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasVersion()) {
      protoValue.addAllVersion(version.map { it.toProto() })
    }
    if (hasContainedInstance()) {
      protoValue.addAllContainedInstance(containedInstance.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceVersionComponent.toProto():
    ExampleScenario.Instance.Version {
    val protoValue =
      ExampleScenario.Instance.Version.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasVersionId()) {
      protoValue.versionId = versionIdElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceContainedInstanceComponent.toProto():
    ExampleScenario.Instance.ContainedInstance {
    val protoValue =
      ExampleScenario.Instance.ContainedInstance.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasResourceId()) {
      protoValue.resourceId = resourceIdElement.toProto()
    }
    if (hasVersionId()) {
      protoValue.versionId = versionIdElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessComponent.toProto():
    ExampleScenario.Process {
    val protoValue = ExampleScenario.Process.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasPreConditions()) {
      protoValue.preConditions = preConditionsElement.toProto()
    }
    if (hasPostConditions()) {
      protoValue.postConditions = postConditionsElement.toProto()
    }
    if (hasStep()) {
      protoValue.addAllStep(step.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepComponent.toProto():
    ExampleScenario.Process.Step {
    val protoValue =
      ExampleScenario.Process.Step.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPause()) {
      protoValue.pause = pauseElement.toProto()
    }
    if (hasOperation()) {
      protoValue.operation = operation.toProto()
    }
    if (hasAlternative()) {
      protoValue.addAllAlternative(alternative.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepOperationComponent.toProto():
    ExampleScenario.Process.Step.Operation {
    val protoValue =
      ExampleScenario.Process.Step.Operation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasNumber()) {
      protoValue.number = numberElement.toProto()
    }
    if (hasType()) {
      protoValue.type = typeElement.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasInitiator()) {
      protoValue.initiator = initiatorElement.toProto()
    }
    if (hasReceiver()) {
      protoValue.receiver = receiverElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasInitiatorActive()) {
      protoValue.initiatorActive = initiatorActiveElement.toProto()
    }
    if (hasReceiverActive()) {
      protoValue.receiverActive = receiverActiveElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepAlternativeComponent.toProto():
    ExampleScenario.Process.Step.Alternative {
    val protoValue =
      ExampleScenario.Process.Step.Alternative.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    return protoValue.build()
  }

  private fun ExampleScenario.Actor.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasActorId()) {
      hapiValue.actorIdElement = actorId.toHapi()
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioActorType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }

  private fun ExampleScenario.Instance.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasResourceId()) {
      hapiValue.resourceIdElement = resourceId.toHapi()
    }
    hapiValue.resourceType =
      org.hl7.fhir.r4.model.ExampleScenario.FHIRResourceType.valueOf(
        resourceType.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (versionCount > 0) {
      hapiValue.version = versionList.map { it.toHapi() }
    }
    if (containedInstanceCount > 0) {
      hapiValue.containedInstance = containedInstanceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ExampleScenario.Instance.Version.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceVersionComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceVersionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasVersionId()) {
      hapiValue.versionIdElement = versionId.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }

  private fun ExampleScenario.Instance.ContainedInstance.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceContainedInstanceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioInstanceContainedInstanceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasResourceId()) {
      hapiValue.resourceIdElement = resourceId.toHapi()
    }
    if (hasVersionId()) {
      hapiValue.versionIdElement = versionId.toHapi()
    }
    return hapiValue
  }

  private fun ExampleScenario.Process.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasPreConditions()) {
      hapiValue.preConditionsElement = preConditions.toHapi()
    }
    if (hasPostConditions()) {
      hapiValue.postConditionsElement = postConditions.toHapi()
    }
    if (stepCount > 0) {
      hapiValue.step = stepList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ExampleScenario.Process.Step.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepComponent {
    val hapiValue = org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPause()) {
      hapiValue.pauseElement = pause.toHapi()
    }
    if (hasOperation()) {
      hapiValue.operation = operation.toHapi()
    }
    if (alternativeCount > 0) {
      hapiValue.alternative = alternativeList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ExampleScenario.Process.Step.Operation.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepOperationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepOperationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasNumber()) {
      hapiValue.numberElement = number.toHapi()
    }
    if (hasType()) {
      hapiValue.typeElement = type.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasInitiator()) {
      hapiValue.initiatorElement = initiator.toHapi()
    }
    if (hasReceiver()) {
      hapiValue.receiverElement = receiver.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasInitiatorActive()) {
      hapiValue.initiatorActiveElement = initiatorActive.toHapi()
    }
    if (hasReceiverActive()) {
      hapiValue.receiverActiveElement = receiverActive.toHapi()
    }
    return hapiValue
  }

  private fun ExampleScenario.Process.Step.Alternative.toHapi():
    org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepAlternativeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ExampleScenario.ExampleScenarioProcessStepAlternativeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    return hapiValue
  }
}
