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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ResearchSubject
import com.google.fhir.r4.core.ResearchSubjectStatusCode

object ResearchSubjectConverter {
  fun ResearchSubject.toHapi(): org.hl7.fhir.r4.model.ResearchSubject {
    val hapiValue = org.hl7.fhir.r4.model.ResearchSubject()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.ResearchSubject.ResearchSubjectStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    if (hasStudy()) {
      hapiValue.study = study.toHapi()
    }
    if (hasIndividual()) {
      hapiValue.individual = individual.toHapi()
    }
    if (hasAssignedArm()) {
      hapiValue.assignedArmElement = assignedArm.toHapi()
    }
    if (hasActualArm()) {
      hapiValue.actualArmElement = actualArm.toHapi()
    }
    if (hasConsent()) {
      hapiValue.consent = consent.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ResearchSubject.toProto(): ResearchSubject {
    val protoValue = ResearchSubject.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      ResearchSubject.StatusCode.newBuilder()
        .setValue(
          ResearchSubjectStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    if (hasStudy()) {
      protoValue.study = study.toProto()
    }
    if (hasIndividual()) {
      protoValue.individual = individual.toProto()
    }
    if (hasAssignedArm()) {
      protoValue.assignedArm = assignedArmElement.toProto()
    }
    if (hasActualArm()) {
      protoValue.actualArm = actualArmElement.toProto()
    }
    if (hasConsent()) {
      protoValue.consent = consent.toProto()
    }
    return protoValue.build()
  }
}
