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

package com.google.android.fhir.hapiprotoconverter

import android.annotation.SuppressLint
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.fhir.shaded.protobuf.ByteString
import com.google.fhir.shaded.protobuf.GeneratedMessageV3
import java.lang.IllegalArgumentException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone
import org.hl7.fhir.instance.model.api.IPrimitiveType

@SuppressLint("DefaultLocale")
/**
 * Returns proto representation of @param hapiPrimitive
 * @param hapiPrimitive primitive type that needs to be converted to proto
 * @param protoClass corresponding proto class that the hapi primitive will be converted to
 */
fun <T : GeneratedMessageV3> convert(hapiPrimitive: IPrimitiveType<*>, protoClass: Class<T>): T {
  // Ensures that protoClass and hapiClass represent the same datatype
  require(protoClass.simpleName == hapiPrimitive::class.java.simpleName.removeSuffix("Type")) {
    "Cannot convert ${hapiPrimitive::class.java.name} to ${protoClass.name}"
  }

  // Creating builder for corresponding proto class
  val newBuilder =
    protoClass.getDeclaredMethod("newBuilder").invoke(null) as GeneratedMessageV3.Builder<*>

  val builderClass = newBuilder::class.java

  when (hapiPrimitive.fhirType()) {
    "date", "dateTime", "instant" -> {
      // To set value
      builderClass
        .getDeclaredMethod("setValueUs", java.lang.Long.TYPE)
        .invoke(newBuilder, (hapiPrimitive.value as Date).time)

      // To set TimeZone
      hapiPrimitive::class.java.getMethod("getTimeZone").invoke(hapiPrimitive)?.let {
        builderClass
          .getDeclaredMethod("setTimezone", java.lang.String::class.java)
          .invoke(newBuilder, it::class.java.getMethod("getID").invoke(it))
      }

      // To Set Precision
      builderClass
        .getDeclaredMethod("setPrecisionValue", Integer.TYPE)
        .invoke(
          newBuilder,
          getValueForDateTimeEnum(
            hapiPrimitive::class.java.getMethod("getPrecision").invoke(hapiPrimitive) as
              TemporalPrecisionEnum,
            hapiPrimitive.fhirType()
          )
        )
    }
    "time" -> {
      val (duration, precision) =
        getDurationPrecisionPairFromTimeString(hapiPrimitive.valueAsString)
      builderClass.getDeclaredMethod("setValueUs", java.lang.Long.TYPE).invoke(newBuilder, duration)

      builderClass
        .getDeclaredMethod("setPrecisionValue", Integer.TYPE)
        .invoke(newBuilder, precision)
    }
    "base64Binary" -> {
      builderClass
        .getDeclaredMethod("setValue", getProtoDataTypeFromHapi(hapiPrimitive))
        .invoke(newBuilder, ByteString.copyFrom((hapiPrimitive.valueAsString).toByteArray()))
    }
    "decimal" -> {
      builderClass
        .getDeclaredMethod("setValue", getProtoDataTypeFromHapi(hapiPrimitive))
        .invoke(newBuilder, hapiPrimitive.valueAsString)
    }
    else -> {
      builderClass
        .getDeclaredMethod("setValue", getProtoDataTypeFromHapi(hapiPrimitive))
        .invoke(newBuilder, hapiPrimitive.value)
    }
  }
  @Suppress("UNCHECKED_CAST") return newBuilder.build() as T
}

/**
 * Returns proto representation of @param primitiveProto
 * @param primitiveProto primitive type that needs to be converted to hapi
 * @param hapiClass corresponding hapi class that the proto primitive will be converted to
 */
fun <T : IPrimitiveType<*>> convert(primitiveProto: GeneratedMessageV3, hapiClass: Class<T>): T {
  // Ensures that protoClass and hapiClass represent the same datatype
  require(primitiveProto::class.java.simpleName == hapiClass.simpleName.removeSuffix("Type")) {
    "Cannot convert ${primitiveProto::class.java.name} to ${hapiClass.name}"
  }

  val primitive = hapiClass.newInstance()
  val protoClass = primitiveProto::class.java

  when (primitive.fhirType()) {
    "date", "dateTime", "instant" -> {
      // To set value
      primitive.value =
        Date(protoClass.getDeclaredMethod("getValueUs").invoke(primitiveProto) as Long)

      // Probably not the best thing to do?
      protoClass.getMethod("getTimezone").invoke(primitiveProto)?.let {
        primitive::class
          .java
          .getMethod("setTimeZone", TimeZone::class.java)
          .invoke(primitive, TimeZone.getTimeZone(it as String))
      }

      // To Set Precision
      primitive::class
        .java
        .getMethod("setPrecision", TemporalPrecisionEnum::class.java)
        .invoke(
          primitive,
          getValueForDateTimeEnum(
            protoClass.getMethod("getPrecisionValue").invoke(primitiveProto) as Int,
            primitive.fhirType()
          )
        )
    }
    "time" -> {
      primitive.valueAsString =
        getTimeStringFromDuration(
          protoClass.getDeclaredMethod("getValueUs").invoke(primitiveProto) as Long
        )
    }
    "base64Binary" -> {
      primitive.valueAsString =
        (protoClass.getMethod("getValue").invoke(primitiveProto) as ByteString).toStringUtf8()
    }
    "decimal" -> {
      primitive.valueAsString = protoClass.getMethod("getValue").invoke(primitiveProto) as String
    }
    else -> {
      primitive.value = protoClass.getMethod("getValue").invoke(primitiveProto)
    }
  }

  return primitive
}

/** returns proto value for precision of @param [precision] */
private fun getValueForDateTimeEnum(precision: TemporalPrecisionEnum, fhirType: String): Int {
  val value =
    when (precision) {
      TemporalPrecisionEnum.YEAR -> 1
      TemporalPrecisionEnum.MONTH -> 2
      TemporalPrecisionEnum.DAY -> 3
      TemporalPrecisionEnum.SECOND -> 4
      TemporalPrecisionEnum.MILLI -> 5
      else -> 0
    }
  return if (fhirType == "instant") value - 3 else value
}

/** returns temporalPrecisionEnum representation of @param [precision] */
private fun getValueForDateTimeEnum(precision: Int, fhirType: String): TemporalPrecisionEnum {

  return when (if (fhirType == "instant") precision - 3 else precision) {
    1 -> TemporalPrecisionEnum.YEAR
    2 -> TemporalPrecisionEnum.MONTH
    3 -> TemporalPrecisionEnum.DAY
    4 -> TemporalPrecisionEnum.SECOND
    else -> TemporalPrecisionEnum.MILLI
  }
}

/** returns javaClass of the value in the corresponding proto class of @param [hapiPrimitive] */
private fun getProtoDataTypeFromHapi(hapiPrimitive: IPrimitiveType<*>): Class<*> {
  return when (hapiPrimitive.fhirType()) {
    "integer", "positiveInt", "unsignedInt" -> Integer.TYPE
    "string", "id", "code", "uri", "decimal", "url", "canonical", "oid", "markdown", "uuid" ->
      String::class.java
    "date", "dateTime", "instant", "time" -> java.lang.Long.TYPE
    "boolean" -> java.lang.Boolean.TYPE
    "base64Binary" -> ByteString::class.java
    else ->
      throw IllegalArgumentException(
        "${hapiPrimitive::class.java.name} is not a valid primitive type"
      )
  }
}
/** returns duration (microseconds of the day) and precision from string representation of [time] */
private fun getDurationPrecisionPairFromTimeString(time: String): Pair<Long, Int> {
  return (LocalTime.parse(time).toNanoOfDay() / 1000 to
    when (time.length) {
      8 -> 1
      11 -> 2
      else -> 0
    })
}

/** returns string representation of [microOfDay] */
private fun getTimeStringFromDuration(microOfDay: Long): String {
  return LocalTime.ofNanoOfDay(microOfDay * 1000).format(DateTimeFormatter.ISO_LOCAL_TIME)
}
