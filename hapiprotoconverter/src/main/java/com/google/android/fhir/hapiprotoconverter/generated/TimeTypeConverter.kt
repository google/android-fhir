package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.Int
import kotlin.String
import org.hl7.fhir.r4.model.TimeType

public object TimeConverter {
  public fun TimeType.toProto(): Time {
    val protoValue = Time.newBuilder()
    .setValueUs(LocalTime.parse(value).toNanoOfDay()/1000)
    .setPrecisionValue(getTimePrecision(value))
    .build()
    return protoValue
  }

  public fun Time.toHapi(): TimeType {
    val hapiValue = TimeType()
    hapiValue.value = LocalTime.ofNanoOfDay(valueUs).format(DateTimeFormatter.ISO_LOCAL_TIME)
    return hapiValue
  }

  private fun getTimePrecision(timeString: String): Int = when(timeString.length) {
    8 -> Time.Precision.SECOND_VALUE
    11-> Time.Precision.MILLISECOND_VALUE
    else -> -1
  }
}
