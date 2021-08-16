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

public object AuditEventConverter {
  @JvmStatic
  private fun AuditEvent.Entity.Detail.ValueX.auditEventEntityDetailValueToHapi(): Type {
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.getBase64Binary()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for AuditEvent.entity.detail.value[x]")
  }

  @JvmStatic
  private fun Type.auditEventEntityDetailValueToProto(): AuditEvent.Entity.Detail.ValueX {
    val protoValue = AuditEvent.Entity.Detail.ValueX.newBuilder()
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is Base64BinaryType) {
      protoValue.setBase64Binary(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun AuditEvent.toHapi(): org.hl7.fhir.r4.model.AuditEvent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent()
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
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (subtypeCount > 0) {
      hapiValue.setSubtype(subtypeList.map { it.toHapi() })
    }
    hapiValue.setAction(
      org.hl7.fhir.r4.model.AuditEvent.AuditEventAction.valueOf(
        action.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    if (hasRecorded()) {
      hapiValue.setRecordedElement(recorded.toHapi())
    }
    hapiValue.setOutcome(
      org.hl7.fhir.r4.model.AuditEvent.AuditEventOutcome.valueOf(
        outcome.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasOutcomeDesc()) {
      hapiValue.setOutcomeDescElement(outcomeDesc.toHapi())
    }
    if (purposeOfEventCount > 0) {
      hapiValue.setPurposeOfEvent(purposeOfEventList.map { it.toHapi() })
    }
    if (agentCount > 0) {
      hapiValue.setAgent(agentList.map { it.toHapi() })
    }
    if (hasSource()) {
      hapiValue.setSource(source.toHapi())
    }
    if (entityCount > 0) {
      hapiValue.setEntity(entityList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.AuditEvent.toProto(): AuditEvent {
    val protoValue = AuditEvent.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSubtype()) {
      protoValue.addAllSubtype(subtype.map { it.toProto() })
    }
    protoValue.setAction(
      AuditEvent.ActionCode.newBuilder()
        .setValue(
          AuditEventActionCode.Value.valueOf(
            action.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    if (hasRecorded()) {
      protoValue.setRecorded(recordedElement.toProto())
    }
    protoValue.setOutcome(
      AuditEvent.OutcomeCode.newBuilder()
        .setValue(
          AuditEventOutcomeCode.Value.valueOf(
            outcome.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasOutcomeDesc()) {
      protoValue.setOutcomeDesc(outcomeDescElement.toProto())
    }
    if (hasPurposeOfEvent()) {
      protoValue.addAllPurposeOfEvent(purposeOfEvent.map { it.toProto() })
    }
    if (hasAgent()) {
      protoValue.addAllAgent(agent.map { it.toProto() })
    }
    if (hasSource()) {
      protoValue.setSource(source.toProto())
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
      protoValue.setType(type.toProto())
    }
    if (hasRole()) {
      protoValue.addAllRole(role.map { it.toProto() })
    }
    if (hasWho()) {
      protoValue.setWho(who.toProto())
    }
    if (hasAltId()) {
      protoValue.setAltId(altIdElement.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasRequestor()) {
      protoValue.setRequestor(requestorElement.toProto())
    }
    if (hasLocation()) {
      protoValue.setLocation(location.toProto())
    }
    if (hasPolicy()) {
      protoValue.addAllPolicy(policy.map { it.toProto() })
    }
    if (hasMedia()) {
      protoValue.setMedia(media.toProto())
    }
    if (hasNetwork()) {
      protoValue.setNetwork(network.toProto())
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
      protoValue.setAddress(addressElement.toProto())
    }
    protoValue.setType(
      AuditEvent.Agent.Network.TypeCode.newBuilder()
        .setValue(
          AuditEventAgentNetworkTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
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
      protoValue.setSite(siteElement.toProto())
    }
    if (hasObserver()) {
      protoValue.setObserver(observer.toProto())
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
      protoValue.setWhat(what.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasRole()) {
      protoValue.setRole(role.toProto())
    }
    if (hasLifecycle()) {
      protoValue.setLifecycle(lifecycle.toProto())
    }
    if (hasSecurityLabel()) {
      protoValue.addAllSecurityLabel(securityLabel.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasQuery()) {
      protoValue.setQuery(queryElement.toProto())
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
      protoValue.setType(typeElement.toProto())
    }
    if (hasValue()) {
      protoValue.setValue(value.auditEventEntityDetailValueToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun AuditEvent.Agent.toHapi(): org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (roleCount > 0) {
      hapiValue.setRole(roleList.map { it.toHapi() })
    }
    if (hasWho()) {
      hapiValue.setWho(who.toHapi())
    }
    if (hasAltId()) {
      hapiValue.setAltIdElement(altId.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasRequestor()) {
      hapiValue.setRequestorElement(requestor.toHapi())
    }
    if (hasLocation()) {
      hapiValue.setLocation(location.toHapi())
    }
    if (policyCount > 0) {
      hapiValue.setPolicy(policyList.map { it.toHapi() })
    }
    if (hasMedia()) {
      hapiValue.setMedia(media.toHapi())
    }
    if (hasNetwork()) {
      hapiValue.setNetwork(network.toHapi())
    }
    if (purposeOfUseCount > 0) {
      hapiValue.setPurposeOfUse(purposeOfUseList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Agent.Network.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasAddress()) {
      hapiValue.setAddressElement(address.toHapi())
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentNetworkType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Source.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSite()) {
      hapiValue.setSiteElement(site.toHapi())
    }
    if (hasObserver()) {
      hapiValue.setObserver(observer.toHapi())
    }
    if (typeCount > 0) {
      hapiValue.setType(typeList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Entity.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasWhat()) {
      hapiValue.setWhat(what.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasRole()) {
      hapiValue.setRole(role.toHapi())
    }
    if (hasLifecycle()) {
      hapiValue.setLifecycle(lifecycle.toHapi())
    }
    if (securityLabelCount > 0) {
      hapiValue.setSecurityLabel(securityLabelList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasQuery()) {
      hapiValue.setQueryElement(query.toHapi())
    }
    if (detailCount > 0) {
      hapiValue.setDetail(detailList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun AuditEvent.Entity.Detail.toHapi():
    org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent {
    val hapiValue = org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityDetailComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setTypeElement(type.toHapi())
    }
    if (hasValue()) {
      hapiValue.setValue(value.auditEventEntityDetailValueToHapi())
    }
    return hapiValue
  }
}
