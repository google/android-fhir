package com.google.android.fhir.hapiprotoconverter.generated

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.fhir.r4.core.Date
import java.time.Instant
import java.util.TimeZone
import org.hl7.fhir.r4.model.DateType

/**
 * contains functions that convert between the hapi and proto representations of date
 */
public object DateConverter {
  /**
   * returns the proto Date equivalent of the hapi DateType
   */
  public fun DateType.toProto(): Date {
    val protoValue = Date.newBuilder()
    if (timeZone.id!=null) protoValue.setTimezone(timeZone.id)
    if (value.time!=null) protoValue.setValueUs(value.time)
    if (precision.toProtoPrecision()!=null) protoValue.setPrecision(precision.toProtoPrecision())
    return protoValue.build()
  }

  /**
   * returns the hapi DateType equivalent of the proto Date
   */
  public fun Date.toHapi(): DateType {
    val hapiValue = DateType()
    hapiValue.timeZone = TimeZone.getTimeZone(timezone)
    hapiValue.value = java.util.Date.from(Instant.ofEpochMilli(valueUs))
    hapiValue.precision = precision.toHapiPrecision()
    return hapiValue
  }

  /**
   * converts the hapi temporal precision to Date.Precision
   */
  private fun TemporalPrecisionEnum.toProtoPrecision(): Date.Precision = when(this) {
    TemporalPrecisionEnum.YEAR -> Date.Precision.YEAR
    TemporalPrecisionEnum.MONTH -> Date.Precision.MONTH
    TemporalPrecisionEnum.DAY -> Date.Precision.DAY
    else ->Date.Precision.PRECISION_UNSPECIFIED
  }

  /**
   * converts the Date.Precision to hapi Temporal Precision
   */
  private fun Date.Precision.toHapiPrecision(): TemporalPrecisionEnum = when(this) {
    Date.Precision.YEAR -> TemporalPrecisionEnum.YEAR
    Date.Precision.MONTH -> TemporalPrecisionEnum.MONTH
    Date.Precision.DAY -> TemporalPrecisionEnum.DAY
    else ->TemporalPrecisionEnum.MILLI
  }
}
