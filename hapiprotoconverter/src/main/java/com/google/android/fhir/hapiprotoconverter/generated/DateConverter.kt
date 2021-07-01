package com.google.android.fhir.hapiprotoconverter.generated

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.fhir.r4.core.Date
import java.time.Instant
import java.util.TimeZone
import org.hl7.fhir.r4.model.DateType

public object DateConverter {
  public fun DateType.toProto(): Date {
    val protoValue = Date.newBuilder()
    .setTimezone(timeZone.id)
    .setValueUs(value.time)
    .setPrecision(precision.toProtoPrecision())
    .build()
    return protoValue
  }

  public fun Date.toHapi(): DateType {
    val hapiValue = DateType()
    hapiValue.value = java.util.Date.from(Instant.ofEpochMilli(valueUs))
    hapiValue.timeZone = TimeZone.getTimeZone(timezone)
    return hapiValue
  }

  private fun TemporalPrecisionEnum.toProtoPrecision(): Date.Precision = when(this) {
    TemporalPrecisionEnum.YEAR -> Date.Precision.YEAR
    TemporalPrecisionEnum.MONTH -> Date.Precision.MONTH
    TemporalPrecisionEnum.DAY -> Date.Precision.DAY
    else ->Date.Precision.PRECISION_UNSPECIFIED
  }

  private fun Date.Precision.toHapiPrecision(): TemporalPrecisionEnum = when(this) {
    Date.Precision.YEAR -> TemporalPrecisionEnum.YEAR
    Date.Precision.MONTH -> TemporalPrecisionEnum.MONTH
    Date.Precision.DAY -> TemporalPrecisionEnum.DAY
    else ->TemporalPrecisionEnum.MILLI
  }
}
