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

public object ProvenanceConverter {
  @JvmStatic
  private fun Provenance.OccurredX.provenanceOccurredToHapi(): Type {
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Provenance.occurred[x]")
  }

  @JvmStatic
  private fun Type.provenanceOccurredToProto(): Provenance.OccurredX {
    val protoValue = Provenance.OccurredX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun Provenance.toHapi(): org.hl7.fhir.r4.model.Provenance {
    val hapiValue = org.hl7.fhir.r4.model.Provenance()
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
    if (targetCount > 0) {
      hapiValue.setTarget(targetList.map { it.toHapi() })
    }
    if (hasOccurred()) {
      hapiValue.setOccurred(occurred.provenanceOccurredToHapi())
    }
    if (hasRecorded()) {
      hapiValue.setRecordedElement(recorded.toHapi())
    }
    if (policyCount > 0) {
      hapiValue.setPolicy(policyList.map { it.toHapi() })
    }
    if (hasLocation()) {
      hapiValue.setLocation(location.toHapi())
    }
    if (reasonCount > 0) {
      hapiValue.setReason(reasonList.map { it.toHapi() })
    }
    if (hasActivity()) {
      hapiValue.setActivity(activity.toHapi())
    }
    if (agentCount > 0) {
      hapiValue.setAgent(agentList.map { it.toHapi() })
    }
    if (entityCount > 0) {
      hapiValue.setEntity(entityList.map { it.toHapi() })
    }
    if (signatureCount > 0) {
      hapiValue.setSignature(signatureList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Provenance.toProto(): Provenance {
    val protoValue = Provenance.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasTarget()) {
      protoValue.addAllTarget(target.map { it.toProto() })
    }
    if (hasOccurred()) {
      protoValue.setOccurred(occurred.provenanceOccurredToProto())
    }
    if (hasRecorded()) {
      protoValue.setRecorded(recordedElement.toProto())
    }
    if (hasPolicy()) {
      protoValue.addAllPolicy(policy.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.setLocation(location.toProto())
    }
    if (hasReason()) {
      protoValue.addAllReason(reason.map { it.toProto() })
    }
    if (hasActivity()) {
      protoValue.setActivity(activity.toProto())
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
      protoValue.setType(type.toProto())
    }
    if (hasRole()) {
      protoValue.addAllRole(role.map { it.toProto() })
    }
    if (hasWho()) {
      protoValue.setWho(who.toProto())
    }
    if (hasOnBehalfOf()) {
      protoValue.setOnBehalfOf(onBehalfOf.toProto())
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
    protoValue.setRole(
      Provenance.Entity.RoleCode.newBuilder()
        .setValue(
          ProvenanceEntityRoleCode.Value.valueOf(
            role.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasWhat()) {
      protoValue.setWhat(what.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Provenance.Agent.toHapi(): org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent()
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
    if (hasOnBehalfOf()) {
      hapiValue.setOnBehalfOf(onBehalfOf.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Provenance.Entity.toHapi():
    org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    hapiValue.setRole(
      org.hl7.fhir.r4.model.Provenance.ProvenanceEntityRole.valueOf(
        role.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasWhat()) {
      hapiValue.setWhat(what.toHapi())
    }
    return hapiValue
  }
}
