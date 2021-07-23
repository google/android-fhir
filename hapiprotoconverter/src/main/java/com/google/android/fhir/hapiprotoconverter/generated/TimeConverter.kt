package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.Int
import kotlin.String
import org.hl7.fhir.r4.model.TimeType

/**
 * contains functions that convert between the hapi and proto representations of time
 */
public object TimeConverter {
  /**
   * returns the proto Time equivalent of the hapi TimeType
   */
  public fun TimeType.toProto(): Time {
    val protoValue = Time.newBuilder()
    if (value!=null) protoValue.setValueUs(LocalTime.parse(value).toNanoOfDay()/1000)
    if (value!=null) protoValue.setPrecisionValue(getTimePrecision(value))
    return protoValue.build()
  }

  /**
   * returns the hapi TimeType equivalent of the proto Time
   */
  public fun Time.toHapi(): TimeType {
    val hapiValue = TimeType()
    hapiValue.value = LocalTime.ofNanoOfDay(valueUs*1000).format(DateTimeFormatter.ISO_LOCAL_TIME)
    return hapiValue
  }

  /**
   * generates Time.Precision for the hapi TimeType
   */
  private fun getTimePrecision(timeString: String): Int = when(timeString.length) {
    8 -> Time.Precision.SECOND_VALUE
    12 -> Time.Precision.MILLISECOND_VALUE
    else -> -1
  }
}
