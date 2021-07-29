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

import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.FamilyHistoryStatusCode
import com.google.fhir.r4.core.FamilyMemberHistory
import com.google.fhir.r4.core.FamilyMemberHistory.Condition
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object FamilyMemberHistoryConverter {
  private fun FamilyMemberHistory.BornX.familyMemberHistoryBornToHapi(): Type {
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for FamilyMemberHistory.born[x]")
  }

  private fun Type.familyMemberHistoryBornToProto(): FamilyMemberHistory.BornX {
    val protoValue = FamilyMemberHistory.BornX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  private fun FamilyMemberHistory.AgeX.familyMemberHistoryAgeToHapi(): Type {
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for FamilyMemberHistory.age[x]")
  }

  private fun Type.familyMemberHistoryAgeToProto(): FamilyMemberHistory.AgeX {
    val protoValue = FamilyMemberHistory.AgeX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  private fun FamilyMemberHistory.DeceasedX.familyMemberHistoryDeceasedToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getDate() != Date.newBuilder().defaultInstanceForType) {
      return (this.getDate()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for FamilyMemberHistory.deceased[x]")
  }

  private fun Type.familyMemberHistoryDeceasedToProto(): FamilyMemberHistory.DeceasedX {
    val protoValue = FamilyMemberHistory.DeceasedX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is DateType) {
      protoValue.setDate(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  private fun FamilyMemberHistory.Condition.OnsetX.familyMemberHistoryConditionOnsetToHapi(): Type {
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for FamilyMemberHistory.condition.onset[x]")
  }

  private fun Type.familyMemberHistoryConditionOnsetToProto():
    FamilyMemberHistory.Condition.OnsetX {
    val protoValue = FamilyMemberHistory.Condition.OnsetX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public fun FamilyMemberHistory.toHapi(): org.hl7.fhir.r4.model.FamilyMemberHistory {
    val hapiValue = org.hl7.fhir.r4.model.FamilyMemberHistory()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map { it.toHapi() })
    hapiValue.setInstantiatesUri(instantiatesUriList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyHistoryStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setDataAbsentReason(dataAbsentReason.toHapi())
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setRelationship(relationship.toHapi())
    hapiValue.setSex(sex.toHapi())
    hapiValue.setBorn(born.familyMemberHistoryBornToHapi())
    hapiValue.setAge(age.familyMemberHistoryAgeToHapi())
    hapiValue.setEstimatedAgeElement(estimatedAge.toHapi())
    hapiValue.setDeceased(deceased.familyMemberHistoryDeceasedToHapi())
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setCondition(conditionList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.FamilyMemberHistory.toProto(): FamilyMemberHistory {
    val protoValue =
      FamilyMemberHistory.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
        .addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
        .setStatus(
          FamilyMemberHistory.StatusCode.newBuilder()
            .setValue(
              FamilyHistoryStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setDataAbsentReason(dataAbsentReason.toProto())
        .setPatient(patient.toProto())
        .setDate(dateElement.toProto())
        .setName(nameElement.toProto())
        .setRelationship(relationship.toProto())
        .setSex(sex.toProto())
        .setBorn(born.familyMemberHistoryBornToProto())
        .setAge(age.familyMemberHistoryAgeToProto())
        .setEstimatedAge(estimatedAgeElement.toProto())
        .setDeceased(deceased.familyMemberHistoryDeceasedToProto())
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .addAllCondition(condition.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent.toProto():
    FamilyMemberHistory.Condition {
    val protoValue =
      FamilyMemberHistory.Condition.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setOutcome(outcome.toProto())
        .setContributedToDeath(contributedToDeathElement.toProto())
        .setOnset(onset.familyMemberHistoryConditionOnsetToProto())
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun FamilyMemberHistory.Condition.toHapi():
    org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.FamilyMemberHistory.FamilyMemberHistoryConditionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setOutcome(outcome.toHapi())
    hapiValue.setContributedToDeathElement(contributedToDeath.toHapi())
    hapiValue.setOnset(onset.familyMemberHistoryConditionOnsetToHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }
}
