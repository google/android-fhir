package com.google.android.fhir.hapiprotoconverter.generated

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.fhir.r4.core.Instant
import java.util.Date
import java.util.TimeZone
import org.hl7.fhir.r4.model.InstantType
import java.time.Instant as InstantUtil

public object InstantConverter {
  public fun InstantType.toProto(): Instant {
    val protoValue = Instant.newBuilder()
    .setTimezone(timeZone.id)
    .setValueUs(value.time)
    .setPrecision(precision.toProtoPrecision())
    .build()
    return protoValue
  }

  public fun Instant.toHapi(): InstantType {
    val hapiValue = InstantType()
    hapiValue.value = Date.from(InstantUtil.ofEpochMilli(valueUs))
    hapiValue.timeZone = TimeZone.getTimeZone(timezone)
    return hapiValue
  }

  private fun TemporalPrecisionEnum.toProtoPrecision(): Instant.Precision = when(this) {
    TemporalPrecisionEnum.SECOND -> Instant.Precision.SECOND
    TemporalPrecisionEnum.MILLI -> Instant.Precision.MILLISECOND
    else ->Instant.Precision.PRECISION_UNSPECIFIED
  }

  private fun Instant.Precision.toHapiPrecision(): TemporalPrecisionEnum = when(this) {
    Instant.Precision.SECOND -> TemporalPrecisionEnum.SECOND
    Instant.Precision.MILLISECOND-> TemporalPrecisionEnum.MILLI
    else ->TemporalPrecisionEnum.MILLI
  }
}
