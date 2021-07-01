package com.google.android.fhir.hapiprotoconverter.generated

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.fhir.r4.core.DateTime
import java.time.Instant
import java.util.Date
import java.util.TimeZone
import org.hl7.fhir.r4.model.DateTimeType

public object DateTimeConverter {
  public fun DateTimeType.toProto(): DateTime {
    val protoValue = DateTime.newBuilder()
    .setTimezone(timeZone.id)
    .setValueUs(value.time)
    .setPrecision(precision.toProtoPrecision())
    .build()
    return protoValue
  }

  public fun DateTime.toHapi(): DateTimeType {
    val hapiValue = DateTimeType()
    hapiValue.value = Date.from(Instant.ofEpochMilli(valueUs))
    hapiValue.timeZone = TimeZone.getTimeZone(timezone)
    return hapiValue
  }

  private fun TemporalPrecisionEnum.toProtoPrecision(): DateTime.Precision = when(this) {
    TemporalPrecisionEnum.YEAR -> DateTime.Precision.YEAR
    TemporalPrecisionEnum.MONTH -> DateTime.Precision.MONTH
    TemporalPrecisionEnum.DAY -> DateTime.Precision.DAY
    TemporalPrecisionEnum.SECOND -> DateTime.Precision.SECOND
    TemporalPrecisionEnum.MILLI -> DateTime.Precision.MILLISECOND
    else ->DateTime.Precision.PRECISION_UNSPECIFIED
  }

  private fun DateTime.Precision.toHapiPrecision(): TemporalPrecisionEnum = when(this) {
    DateTime.Precision.YEAR -> TemporalPrecisionEnum.YEAR
    DateTime.Precision.MONTH -> TemporalPrecisionEnum.MONTH
    DateTime.Precision.DAY -> TemporalPrecisionEnum.DAY
    DateTime.Precision.SECOND -> TemporalPrecisionEnum.SECOND
    DateTime.Precision.MILLISECOND-> TemporalPrecisionEnum.MILLI
    else ->TemporalPrecisionEnum.MILLI
  }
}
