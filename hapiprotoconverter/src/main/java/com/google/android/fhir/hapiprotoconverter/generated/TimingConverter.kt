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
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.fhir.r4.core.DaysOfWeekCode
import com.google.fhir.r4.core.EventTimingValueSet
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Timing
import com.google.fhir.r4.core.Timing.Repeat
import com.google.fhir.r4.core.UnitsOfTimeValueSet
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Duration
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Type

object TimingConverter {
  private fun Timing.Repeat.BoundsX.timingRepeatBoundsToHapi(): Type {
    if (hasDuration()) {
      return (this.duration).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Timing.repeat.bounds[x]")
  }

  private fun Type.timingRepeatBoundsToProto(): Timing.Repeat.BoundsX {
    val protoValue = Timing.Repeat.BoundsX.newBuilder()
    if (this is Duration) {
      protoValue.duration = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  fun Timing.toHapi(): org.hl7.fhir.r4.model.Timing {
    val hapiValue = org.hl7.fhir.r4.model.Timing()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (eventCount > 0) {
      hapiValue.event = eventList.map { it.toHapi() }
    }
    if (hasRepeat()) {
      hapiValue.repeat = repeat.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Timing.toProto(): Timing {
    val protoValue = Timing.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasEvent()) {
      protoValue.addAllEvent(event.map { it.toProto() })
    }
    if (hasRepeat()) {
      protoValue.repeat = repeat.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Timing.TimingRepeatComponent.toProto(): Timing.Repeat {
    val protoValue = Timing.Repeat.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasBounds()) {
      protoValue.bounds = bounds.timingRepeatBoundsToProto()
    }
    if (hasCount()) {
      protoValue.count = countElement.toProto()
    }
    if (hasCountMax()) {
      protoValue.countMax = countMaxElement.toProto()
    }
    if (hasDuration()) {
      protoValue.duration = durationElement.toProto()
    }
    if (hasDurationMax()) {
      protoValue.durationMax = durationMaxElement.toProto()
    }
    if (hasDurationUnit()) {
      protoValue.durationUnit =
        Timing.Repeat.DurationUnitCode.newBuilder()
          .setValue(
            UnitsOfTimeValueSet.Value.valueOf(
              durationUnit.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasFrequency()) {
      protoValue.frequency = frequencyElement.toProto()
    }
    if (hasFrequencyMax()) {
      protoValue.frequencyMax = frequencyMaxElement.toProto()
    }
    if (hasPeriod()) {
      protoValue.period = periodElement.toProto()
    }
    if (hasPeriodMax()) {
      protoValue.periodMax = periodMaxElement.toProto()
    }
    if (hasPeriodUnit()) {
      protoValue.periodUnit =
        Timing.Repeat.PeriodUnitCode.newBuilder()
          .setValue(
            UnitsOfTimeValueSet.Value.valueOf(
              periodUnit.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasDayOfWeek()) {
      protoValue.addAllDayOfWeek(
        dayOfWeek.map {
          Timing.Repeat.DayOfWeekCode.newBuilder()
            .setValue(
              DaysOfWeekCode.Value.valueOf(
                it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        }
      )
    }
    if (hasTimeOfDay()) {
      protoValue.addAllTimeOfDay(timeOfDay.map { it.toProto() })
    }
    if (hasWhen()) {
      protoValue.addAllWhen(
        `when`.map {
          Timing.Repeat.WhenCode.newBuilder()
            .setValue(
              EventTimingValueSet.Value.valueOf(
                it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        }
      )
    }
    if (hasOffset()) {
      protoValue.offset = offsetElement.toProto()
    }
    return protoValue.build()
  }

  private fun Timing.Repeat.toHapi(): org.hl7.fhir.r4.model.Timing.TimingRepeatComponent {
    val hapiValue = org.hl7.fhir.r4.model.Timing.TimingRepeatComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasBounds()) {
      hapiValue.bounds = bounds.timingRepeatBoundsToHapi()
    }
    if (hasCount()) {
      hapiValue.countElement = count.toHapi()
    }
    if (hasCountMax()) {
      hapiValue.countMaxElement = countMax.toHapi()
    }
    if (hasDuration()) {
      hapiValue.durationElement = duration.toHapi()
    }
    if (hasDurationMax()) {
      hapiValue.durationMaxElement = durationMax.toHapi()
    }
    if (hasDurationUnit()) {
      hapiValue.durationUnit =
        org.hl7.fhir.r4.model.Timing.UnitsOfTime.valueOf(
          durationUnit.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasFrequency()) {
      hapiValue.frequencyElement = frequency.toHapi()
    }
    if (hasFrequencyMax()) {
      hapiValue.frequencyMaxElement = frequencyMax.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.periodElement = period.toHapi()
    }
    if (hasPeriodMax()) {
      hapiValue.periodMaxElement = periodMax.toHapi()
    }
    if (hasPeriodUnit()) {
      hapiValue.periodUnit =
        org.hl7.fhir.r4.model.Timing.UnitsOfTime.valueOf(
          periodUnit.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (dayOfWeekCount > 0) {
      dayOfWeekList.forEach {
        hapiValue.addDayOfWeek(
          org.hl7.fhir.r4.model.Timing.DayOfWeek.valueOf(
            it.value.name.hapiCodeCheck().replace("_", "")
          )
        )
      }
    }
    if (timeOfDayCount > 0) {
      hapiValue.timeOfDay = timeOfDayList.map { it.toHapi() }
    }
    if (whenCount > 0) {
      whenList.forEach {
        hapiValue.addWhen(
          org.hl7.fhir.r4.model.Timing.EventTiming.valueOf(
            it.value.name.hapiCodeCheck().replace("_", "")
          )
        )
      }
    }
    if (hasOffset()) {
      hapiValue.offsetElement = offset.toHapi()
    }
    return hapiValue
  }
}
