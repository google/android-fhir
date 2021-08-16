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

import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.AuditEvent
import com.google.fhir.r4.core.AuditEvent.Agent
import com.google.fhir.r4.core.AuditEvent.Agent.Network
import com.google.fhir.r4.core.AuditEvent.Entity
import com.google.fhir.r4.core.AuditEvent.Entity.Detail
import com.google.fhir.r4.core.AuditEventActionCode
import com.google.fhir.r4.core.AuditEventAgentNetworkTypeCode
import com.google.fhir.r4.core.AuditEventOutcomeCode
import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object AuditEventConverter {
  @JvmStatic
  private fun AuditEvent.Entity.Detail.ValueX.auditEventEntityDetailValueToHapi(): Type {
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.base64Binary != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.base64Binary).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for AuditEvent.entity.detail.value[x]")
  }

  @JvmStatic
  private fun Type.auditEventEntityDetailValueToProto(): AuditEvent.Entity.Detail.ValueX {
    val protoValue = AuditEvent.Entity.Detail.ValueX.newBuilder()
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    if (this is Base64BinaryType) {
        protoValue.base64Binary = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun AuditEvent.toHapi(): org.hl7.fhir.r4.model.AuditEvent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent()
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
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (subtypeCount > 0) {
        hapiValue.subtype = subtypeList.map { it.toHapi() }
    }
      hapiValue.action = org.hl7.fhir.r4.model.AuditEvent.AuditEventAction.valueOf(
          action.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (hasRecorded()) {
        hapiValue.recordedElement = recorded.toHapi()
    }
      hapiValue.outcome = org.hl7.fhir.r4.model.AuditEvent.AuditEventOutcome.valueOf(
          outcome.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasOutcomeDesc()) {
        hapiValue.outcomeDescElement = outcomeDesc.toHapi()
    }
    if (purposeOfEventCount > 0) {
        hapiValue.purposeOfEvent = purposeOfEventList.map { it.toHapi() }
    }
    if (agentCount > 0) {
        hapiValue.agent = agentList.map { it.toHapi() }
    }
    if (hasSource()) {
        hapiValue.source = source.toHapi()
    }
    if (entityCount > 0) {
        hapiValue.entity = entityList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.AuditEvent.toProto(): AuditEvent {
    val protoValue = AuditEvent.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasSubtype()) {
      protoValue.addAllSubtype(subtype.map { it.toProto() })
    }
      protoValue.action = AuditEvent.ActionCode.newBuilder()
          .setValue(
              AuditEventActionCode.Value.valueOf(
                  action.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasRecorded()) {
        protoValue.recorded = recordedElement.toProto()
    }
      protoValue.outcome = AuditEvent.OutcomeCode.newBuilder()
          .setValue(
              AuditEventOutcomeCode.Value.valueOf(
                  outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasOutcomeDesc()) {
        protoValue.outcomeDesc = outcomeDescElement.toProto()
    }
    if (hasPurposeOfEvent()) {
      protoValue.addAllPurposeOfEvent(purposeOfEvent.map { it.toProto() })
    }
    if (hasAgent()) {
      protoValue.addAllAgent(agent.map { it.toProto() })
    }
    if (hasSource()) {
        protoValue.source = source.toProto()
    }
    if (hasEntity()) {
      protoValue.addAllEntity(entity.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent.toProto():
    AuditEvent.Agent {
    val protoValue = AuditEvent.Agent.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasRole()) {
      protoValue.addAllRole(role.map { it.toProto() })
    }
    if (hasWho()) {
        protoValue.who = who.toProto()
    }
    if (hasAltId()) {
        protoValue.altId = altIdElement.toProto()
    }
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
    if (hasRequestor()) {
        protoValue.requestor = requestorElement.toProto()
    }
    if (hasLocation()) {
        protoValue.location = location.toProto()
    }
    if (hasPolicy()) {
      protoValue.addAllPolicy(policy.map { it.toProto() })
    }
    if (hasMedia()) {
        protoValue.media = media.toProto()
    }
    if (hasNetwork()) {
        protoValue.network = network.toProto()
    }
    if (hasPurposeOfUse()) {
      protoValue.addAllPurposeOfUse(purposeOfUse.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent.toProto():
    AuditEvent.Agent.Network {
    val protoValue = AuditEvent.Agent.Network.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAddress()) {
        protoValue.address = addressElement.toProto()
    }
      protoValue.type = AuditEvent.Agent.Network.TypeCode.newBuilder()
          .setValue(
              AuditEventAgentNetworkTypeCode.Value.valueOf(
                  type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent.toProto():
    AuditEvent.Source {
    val protoValue = AuditEvent.Source.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSite()) {
        protoValue.site = siteElement.toProto()
    }
    if (hasObserver()) {
        protoValue.observer = observer.toProto()
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent.toProto():
    AuditEvent.Entity {
    val protoValue = AuditEvent.Entity.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasWhat()) {
        protoValue.what = what.toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasRole()) {
        protoValue.role = role.toProto()
    }
    if (hasLifecycle()) {
        protoValue.lifecycle = lifecycle.toProto()
    }
    if (hasSecurityLabel()) {
      protoValue.addAllSecurityLabel(securityLabel.map { it.toProto() })
    }
    if (hasName()) {
        protoValue.name = nameElement.toProto()
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasQuery()) {
        protoValue.query = queryElement.toProto()
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent.toProto():
    AuditEvent.Entity.Detail {
    val protoValue = AuditEvent.Entity.Detail.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
        protoValue.type = typeElement.toProto()
    }
    if (hasValue()) {
        protoValue.value = value.auditEventEntityDetailValueToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun AuditEvent.Agent.toHapi(): org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (roleCount > 0) {
        hapiValue.role = roleList.map { it.toHapi() }
    }
    if (hasWho()) {
        hapiValue.who = who.toHapi()
    }
    if (hasAltId()) {
        hapiValue.altIdElement = altId.toHapi()
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    if (hasRequestor()) {
        hapiValue.requestorElement = requestor.toHapi()
    }
    if (hasLocation()) {
        hapiValue.location = location.toHapi()
    }
    if (policyCount > 0) {
        hapiValue.policy = policyList.map { it.toHapi() }
    }
    if (hasMedia()) {
        hapiValue.media = media.toHapi()
    }
    if (hasNetwork()) {
        hapiValue.network = network.toHapi()
    }
    if (purposeOfUseCount > 0) {
        hapiValue.purposeOfUse = purposeOfUseList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Agent.Network.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAddress()) {
        hapiValue.addressElement = address.toHapi()
    }
      hapiValue.type = org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkType.valueOf(
          type.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Source.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSite()) {
        hapiValue.siteElement = site.toHapi()
    }
    if (hasObserver()) {
        hapiValue.observer = observer.toHapi()
    }
    if (typeCount > 0) {
        hapiValue.type = typeList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Entity.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasWhat()) {
        hapiValue.what = what.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasRole()) {
        hapiValue.role = role.toHapi()
    }
    if (hasLifecycle()) {
        hapiValue.lifecycle = lifecycle.toHapi()
    }
    if (securityLabelCount > 0) {
        hapiValue.securityLabel = securityLabelList.map { it.toHapi() }
    }
    if (hasName()) {
        hapiValue.nameElement = name.toHapi()
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (hasQuery()) {
        hapiValue.queryElement = query.toHapi()
    }
    if (detailCount > 0) {
        hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Entity.Detail.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
        hapiValue.typeElement = type.toHapi()
    }
    if (hasValue()) {
        hapiValue.value = value.auditEventEntityDetailValueToHapi()
    }
    return hapiValue
  }
}
