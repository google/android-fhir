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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Account
import com.google.fhir.r4.core.AccountStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String

public object AccountConverter {
  public fun Account.toHapi(): org.hl7.fhir.r4.model.Account {
    val hapiValue = org.hl7.fhir.r4.model.Account()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Account.AccountStatus.valueOf(status.value.name.replace("_", ""))
    )
    hapiValue.setType(type.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setSubject(subjectList.map { it.toHapi() })
    hapiValue.setServicePeriod(servicePeriod.toHapi())
    hapiValue.setCoverage(coverageList.map { it.toHapi() })
    hapiValue.setOwner(owner.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setGuarantor(guarantorList.map { it.toHapi() })
    hapiValue.setPartOf(partOf.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Account.toProto(): Account {
    val protoValue =
      Account.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          Account.StatusCode.newBuilder()
            .setValue(
              AccountStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setType(type.toProto())
        .setName(nameElement.toProto())
        .addAllSubject(subject.map { it.toProto() })
        .setServicePeriod(servicePeriod.toProto())
        .addAllCoverage(coverage.map { it.toProto() })
        .setOwner(owner.toProto())
        .setDescription(descriptionElement.toProto())
        .addAllGuarantor(guarantor.map { it.toProto() })
        .setPartOf(partOf.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Account.CoverageComponent.toProto(): Account.Coverage {
    val protoValue =
      Account.Coverage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCoverage(coverage.toProto())
        .setPriority(priorityElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Account.GuarantorComponent.toProto(): Account.Guarantor {
    val protoValue =
      Account.Guarantor.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setParty(party.toProto())
        .setOnHold(onHoldElement.toProto())
        .setPeriod(period.toProto())
        .build()
    return protoValue
  }

  private fun Account.Coverage.toHapi(): org.hl7.fhir.r4.model.Account.CoverageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Account.CoverageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCoverage(coverage.toHapi())
    hapiValue.setPriorityElement(priority.toHapi())
    return hapiValue
  }

  private fun Account.Guarantor.toHapi(): org.hl7.fhir.r4.model.Account.GuarantorComponent {
    val hapiValue = org.hl7.fhir.r4.model.Account.GuarantorComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setParty(party.toHapi())
    hapiValue.setOnHoldElement(onHold.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }
}
