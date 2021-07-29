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
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Type

public object ProvenanceConverter {
  private fun Provenance.OccurredX.provenanceOccurredToHapi(): Type {
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Provenance.occurred[x]")
  }

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

  public fun Provenance.toHapi(): org.hl7.fhir.r4.model.Provenance {
    val hapiValue = org.hl7.fhir.r4.model.Provenance()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setTarget(targetList.map { it.toHapi() })
    hapiValue.setOccurred(occurred.provenanceOccurredToHapi())
    hapiValue.setRecordedElement(recorded.toHapi())
    hapiValue.setPolicy(policyList.map { it.toHapi() })
    hapiValue.setLocation(location.toHapi())
    hapiValue.setReason(reasonList.map { it.toHapi() })
    hapiValue.setActivity(activity.toHapi())
    hapiValue.setAgent(agentList.map { it.toHapi() })
    hapiValue.setEntity(entityList.map { it.toHapi() })
    hapiValue.setSignature(signatureList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Provenance.toProto(): Provenance {
    val protoValue =
      Provenance.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllTarget(target.map { it.toProto() })
        .setOccurred(occurred.provenanceOccurredToProto())
        .setRecorded(recordedElement.toProto())
        .addAllPolicy(policy.map { it.toProto() })
        .setLocation(location.toProto())
        .addAllReason(reason.map { it.toProto() })
        .setActivity(activity.toProto())
        .addAllAgent(agent.map { it.toProto() })
        .addAllEntity(entity.map { it.toProto() })
        .addAllSignature(signature.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent.toProto():
    Provenance.Agent {
    val protoValue =
      Provenance.Agent.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .addAllRole(role.map { it.toProto() })
        .setWho(who.toProto())
        .setOnBehalfOf(onBehalfOf.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent.toProto():
    Provenance.Entity {
    val protoValue =
      Provenance.Entity.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setRole(
          Provenance.Entity.RoleCode.newBuilder()
            .setValue(
              ProvenanceEntityRoleCode.Value.valueOf(role.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setWhat(what.toProto())
        .build()
    return protoValue
  }

  private fun Provenance.Agent.toHapi(): org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Provenance.ProvenanceAgentComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setRole(roleList.map { it.toHapi() })
    hapiValue.setWho(who.toHapi())
    hapiValue.setOnBehalfOf(onBehalfOf.toHapi())
    return hapiValue
  }

  private fun Provenance.Entity.toHapi():
    org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent {
    val hapiValue = org.hl7.fhir.r4.model.Provenance.ProvenanceEntityComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setRole(
      org.hl7.fhir.r4.model.Provenance.ProvenanceEntityRole.valueOf(
        role.value.name.replace("_", "")
      )
    )
    hapiValue.setWhat(what.toHapi())
    return hapiValue
  }
}
