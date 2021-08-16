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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SampledDataConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SampledDataConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Instant
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Observation
import com.google.fhir.r4.core.Observation.Component
import com.google.fhir.r4.core.ObservationStatusCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.SampledData
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Time
import com.google.fhir.r4.core.Timing
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type

object ObservationConverter {
  @JvmStatic
  private fun Observation.EffectiveX.observationEffectiveToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.timing != Timing.newBuilder().defaultInstanceForType) {
      return (this.timing).toHapi()
    }
    if (this.instant != Instant.newBuilder().defaultInstanceForType) {
      return (this.instant).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Observation.effective[x]")
  }

  @JvmStatic
  private fun Type.observationEffectiveToProto(): Observation.EffectiveX {
    val protoValue = Observation.EffectiveX.newBuilder()
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Timing) {
        protoValue.timing = this.toProto()
    }
    if (this is InstantType) {
        protoValue.instant = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Observation.ValueX.observationValueToHapi(): Type {
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.sampledData != SampledData.newBuilder().defaultInstanceForType) {
      return (this.sampledData).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Observation.value[x]")
  }

  @JvmStatic
  private fun Type.observationValueToProto(): Observation.ValueX {
    val protoValue = Observation.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
        protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    if (this is BooleanType) {
        protoValue.boolean = this.toProto()
    }
    if (this is IntegerType) {
        protoValue.integer = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
        protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
        protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
        protoValue.sampledData = this.toProto()
    }
    if (this is TimeType) {
        protoValue.time = this.toProto()
    }
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Observation.Component.ValueX.observationComponentValueToHapi(): Type {
    if (this.quantity != Quantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.integer != Integer.newBuilder().defaultInstanceForType) {
      return (this.integer).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.sampledData != SampledData.newBuilder().defaultInstanceForType) {
      return (this.sampledData).toHapi()
    }
    if (this.time != Time.newBuilder().defaultInstanceForType) {
      return (this.time).toHapi()
    }
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Observation.component.value[x]")
  }

  @JvmStatic
  private fun Type.observationComponentValueToProto(): Observation.Component.ValueX {
    val protoValue = Observation.Component.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
        protoValue.quantity = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
        protoValue.codeableConcept = this.toProto()
    }
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    if (this is BooleanType) {
        protoValue.boolean = this.toProto()
    }
    if (this is IntegerType) {
        protoValue.integer = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
        protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
        protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SampledData) {
        protoValue.sampledData = this.toProto()
    }
    if (this is TimeType) {
        protoValue.time = this.toProto()
    }
    if (this is DateTimeType) {
        protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
        protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Observation.toHapi(): org.hl7.fhir.r4.model.Observation {
    val hapiValue = org.hl7.fhir.r4.model.Observation()
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
    if (basedOnCount > 0) {
        hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
        hapiValue.partOf = partOfList.map { it.toHapi() }
    }
      hapiValue.status = org.hl7.fhir.r4.model.Observation.ObservationStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (categoryCount > 0) {
        hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasCode()) {
        hapiValue.code = code.toHapi()
    }
    if (hasSubject()) {
        hapiValue.subject = subject.toHapi()
    }
    if (focusCount > 0) {
        hapiValue.focus = focusList.map { it.toHapi() }
    }
    if (hasEncounter()) {
        hapiValue.encounter = encounter.toHapi()
    }
    if (hasEffective()) {
        hapiValue.effective = effective.observationEffectiveToHapi()
    }
    if (hasIssued()) {
        hapiValue.issuedElement = issued.toHapi()
    }
    if (performerCount > 0) {
        hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (hasValue()) {
        hapiValue.value = value.observationValueToHapi()
    }
    if (hasDataAbsentReason()) {
        hapiValue.dataAbsentReason = dataAbsentReason.toHapi()
    }
    if (interpretationCount > 0) {
        hapiValue.interpretation = interpretationList.map { it.toHapi() }
    }
    if (noteCount > 0) {
        hapiValue.note = noteList.map { it.toHapi() }
    }
    if (hasBodySite()) {
        hapiValue.bodySite = bodySite.toHapi()
    }
    if (hasMethod()) {
        hapiValue.method = method.toHapi()
    }
    if (hasSpecimen()) {
        hapiValue.specimen = specimen.toHapi()
    }
    if (hasDevice()) {
        hapiValue.device = device.toHapi()
    }
    if (referenceRangeCount > 0) {
        hapiValue.referenceRange = referenceRangeList.map { it.toHapi() }
    }
    if (hasMemberCount > 0) {
        hapiValue.hasMember = hasMemberList.map { it.toHapi() }
    }
    if (derivedFromCount > 0) {
        hapiValue.derivedFrom = derivedFromList.map { it.toHapi() }
    }
    if (componentCount > 0) {
        hapiValue.component = componentList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Observation.toProto(): Observation {
    val protoValue = Observation.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
      protoValue.status = Observation.StatusCode.newBuilder()
          .setValue(
              ObservationStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasCode()) {
        protoValue.code = code.toProto()
    }
    if (hasSubject()) {
        protoValue.subject = subject.toProto()
    }
    if (hasFocus()) {
      protoValue.addAllFocus(focus.map { it.toProto() })
    }
    if (hasEncounter()) {
        protoValue.encounter = encounter.toProto()
    }
    if (hasEffective()) {
        protoValue.effective = effective.observationEffectiveToProto()
    }
    if (hasIssued()) {
        protoValue.issued = issuedElement.toProto()
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasValue()) {
        protoValue.value = value.observationValueToProto()
    }
    if (hasDataAbsentReason()) {
        protoValue.dataAbsentReason = dataAbsentReason.toProto()
    }
    if (hasInterpretation()) {
      protoValue.addAllInterpretation(interpretation.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasBodySite()) {
        protoValue.bodySite = bodySite.toProto()
    }
    if (hasMethod()) {
        protoValue.method = method.toProto()
    }
    if (hasSpecimen()) {
        protoValue.specimen = specimen.toProto()
    }
    if (hasDevice()) {
        protoValue.device = device.toProto()
    }
    if (hasReferenceRange()) {
      protoValue.addAllReferenceRange(referenceRange.map { it.toProto() })
    }
    if (hasHasMember()) {
      protoValue.addAllHasMember(hasMember.map { it.toProto() })
    }
    if (hasDerivedFrom()) {
      protoValue.addAllDerivedFrom(derivedFrom.map { it.toProto() })
    }
    if (hasComponent()) {
      protoValue.addAllComponent(component.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent.toProto():
    Observation.ReferenceRange {
    val protoValue = Observation.ReferenceRange.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLow()) {
        protoValue.low = (low as SimpleQuantity).toProto()
    }
    if (hasHigh()) {
        protoValue.high = (high as SimpleQuantity).toProto()
    }
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasAppliesTo()) {
      protoValue.addAllAppliesTo(appliesTo.map { it.toProto() })
    }
    if (hasAge()) {
        protoValue.age = age.toProto()
    }
    if (hasText()) {
        protoValue.text = textElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Observation.ObservationComponentComponent.toProto():
    Observation.Component {
    val protoValue = Observation.Component.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
        protoValue.code = code.toProto()
    }
    if (hasValue()) {
        protoValue.value = value.observationComponentValueToProto()
    }
    if (hasDataAbsentReason()) {
        protoValue.dataAbsentReason = dataAbsentReason.toProto()
    }
    if (hasInterpretation()) {
      protoValue.addAllInterpretation(interpretation.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Observation.ReferenceRange.toHapi():
    org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent {
    val hapiValue = org.hl7.fhir.r4.model.Observation.ObservationReferenceRangeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLow()) {
        hapiValue.low = low.toHapi()
    }
    if (hasHigh()) {
        hapiValue.high = high.toHapi()
    }
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (appliesToCount > 0) {
        hapiValue.appliesTo = appliesToList.map { it.toHapi() }
    }
    if (hasAge()) {
        hapiValue.age = age.toHapi()
    }
    if (hasText()) {
        hapiValue.textElement = text.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Observation.Component.toHapi():
    org.hl7.fhir.r4.model.Observation.ObservationComponentComponent {
    val hapiValue = org.hl7.fhir.r4.model.Observation.ObservationComponentComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
        hapiValue.code = code.toHapi()
    }
    if (hasValue()) {
        hapiValue.value = value.observationComponentValueToHapi()
    }
    if (hasDataAbsentReason()) {
        hapiValue.dataAbsentReason = dataAbsentReason.toHapi()
    }
    if (interpretationCount > 0) {
        hapiValue.interpretation = interpretationList.map { it.toHapi() }
    }
    return hapiValue
  }
}
