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

object AccountConverter {
  fun Account.toHapi(): org.hl7.fhir.r4.model.Account {
    val hapiValue = org.hl7.fhir.r4.model.Account()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.Account.AccountStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (subjectCount > 0) {
      hapiValue.subject = subjectList.map { it.toHapi() }
    }
    if (hasServicePeriod()) {
      hapiValue.servicePeriod = servicePeriod.toHapi()
    }
    if (coverageCount > 0) {
      hapiValue.coverage = coverageList.map { it.toHapi() }
    }
    if (hasOwner()) {
      hapiValue.owner = owner.toHapi()
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (guarantorCount > 0) {
      hapiValue.guarantor = guarantorList.map { it.toHapi() }
    }
    if (hasPartOf()) {
      hapiValue.partOf = partOf.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Account.toProto(): Account {
    val protoValue = Account.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status =
        Account.StatusCode.newBuilder()
          .setValue(
            AccountStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasServicePeriod()) {
      protoValue.servicePeriod = servicePeriod.toProto()
    }
    if (hasCoverage()) {
      protoValue.addAllCoverage(coverage.map { it.toProto() })
    }
    if (hasOwner()) {
      protoValue.owner = owner.toProto()
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasGuarantor()) {
      protoValue.addAllGuarantor(guarantor.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.partOf = partOf.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Account.CoverageComponent.toProto(): Account.Coverage {
    val protoValue = Account.Coverage.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCoverage()) {
      protoValue.coverage = coverage.toProto()
    }
    if (hasPriority()) {
      protoValue.priority = priorityElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Account.GuarantorComponent.toProto(): Account.Guarantor {
    val protoValue = Account.Guarantor.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasParty()) {
      protoValue.party = party.toProto()
    }
    if (hasOnHold()) {
      protoValue.onHold = onHoldElement.toProto()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  private fun Account.Coverage.toHapi(): org.hl7.fhir.r4.model.Account.CoverageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Account.CoverageComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCoverage()) {
      hapiValue.coverage = coverage.toHapi()
    }
    if (hasPriority()) {
      hapiValue.priorityElement = priority.toHapi()
    }
    return hapiValue
  }

  private fun Account.Guarantor.toHapi(): org.hl7.fhir.r4.model.Account.GuarantorComponent {
    val hapiValue = org.hl7.fhir.r4.model.Account.GuarantorComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasParty()) {
      hapiValue.party = party.toHapi()
    }
    if (hasOnHold()) {
      hapiValue.onHoldElement = onHold.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    return hapiValue
  }
}
