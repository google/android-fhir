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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Provenance
import com.google.fhir.r4.core.Provenance.Entity
import com.google.fhir.r4.core.ProvenanceEntityRoleCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

object ProvenanceConverter {
  @JvmStatic
  private fun Provenance.OccurredX.provenanceOccurredToHapi(): Type {
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Provenance.occurred[x]")
  }

  @JvmStatic
  private fun Type.provenanceOccurredToProto(): Provenance.OccurredX {
    val protoValue = Provenance.OccurredX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Provenance.toHapi(): org.hl7.fhir.r4.model.Provenance {
    val hapiValue = org.hl7.fhir.r4.model.Provenance()
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
    if (targetCount > 0) {
        hapiValue.target = targetList.map { it.toHapi() }
    }
    if (hasOccurred()) {
        hapiValue.occurred = occurred.provenanceOccurredToHapi()
    }
    if (hasRecorded()) {
        hapiValue.recordedElement = recorded.toHapi()
    }
    if (policyCount > 0) {
        hapiValue.policy = policyList.map { it.toHapi() }
    }
    if (hasLocation()) {
        hapiValue.location = location.toHapi()
    }
    if (reasonCount > 0) {
        hapiValue.reason = reasonList.map { it.toHapi() }
    }
    if (hasActivity()) {
        hapiValue.activity = activity.toHapi()
    }
    if (agentCount > 0) {
        hapiValue.agent = agentList.map { it.toHapi() }
    }
    if (entityCount > 0) {
        hapiValue.entity = entityList.map { it.toHapi() }
    }
    if (signatureCount > 0) {
        hapiValue.signature = signatureList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Provenance.toProto(): Provenance {
    val protoValue = Provenance.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    if (hasOccurred()) {
        protoValue.occurred = occurred.provenanceOccurredToProto()
    }
    if (hasRecorded()) {
        protoValue.recorded = recordedElement.toProto()
    }
    if (hasPolicy()) {
      protoValue.addAllPolicy(policy.map { it.toProto() })
    }
    if (hasLocation()) {
        protoValue.location = location.toProto()
    }
    if (hasReason()) {
      protoValue.addAllReason(reason.map { it.toProto() })
    }
    if (hasActivity()) {
        protoValue.activity = activity.toProto()
    }
    if (hasAgent()) {
      protoValue.addAllAgent(agent.map { it.toProto() })
    }
    if (hasEntity()) {
      protoValue.addAllEntity(entity.map { it.toProto() })
    }
    if (hasSignature()) {
      protoValue.addAllSignature(signature.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent.toProto():
    Provenance.Agent {
    val protoValue = Provenance.Agent.newBuilder().setId(String.newBuilder().setValue(id))
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
    if (hasOnBehalfOf()) {
        protoValue.onBehalfOf = onBehalfOf.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent.toProto():
    Provenance.Entity {
    val protoValue = Provenance.Entity.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
      protoValue.role = Provenance.Entity.RoleCode.newBuilder()
          .setValue(
              ProvenanceEntityRoleCode.Value.valueOf(
                  role.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasWhat()) {
        protoValue.what = what.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Provenance.Agent.toHapi(): org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent()
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
    if (hasOnBehalfOf()) {
        hapiValue.onBehalfOf = onBehalfOf.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Provenance.Entity.toHapi():
    org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
      hapiValue.role = org.hl7.fhir.r4.model.Provenance.ProvenanceEntityRole.valueOf(
          role.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasWhat()) {
        hapiValue.what = what.toHapi()
    }
    return hapiValue
  }
}
