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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.fhir.shaded.protobuf.ByteString
import com.google.fhir.shaded.protobuf.GeneratedMessageV3
import java.lang.IllegalArgumentException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone
import org.hl7.fhir.instance.model.api.IPrimitiveType

/**
 * Returns proto representation of @param hapiPrimitive
 * @param hapiPrimitive primitive type that needs to be converted to proto
 * @param protoClass corresponding proto class that the hapi primitive will be converted to
 */
fun <T : GeneratedMessageV3> convert(hapiPrimitive: IPrimitiveType<*>, protoClass: Class<T>): T {
  // Ensures that protoClass and hapiClass represent the same datatype
  require(
    protoClass.simpleName == hapiPrimitive::class.java.simpleName.removeSuffix(hapiPrimitiveSuffix)
  ) { "Cannot convert ${hapiPrimitive::class.java.name} to ${protoClass.name}" }

  // Creating builder for corresponding proto class
  val newBuilder =
    protoClass.getDeclaredMethod(newBuilderMethodName).invoke(null) as GeneratedMessageV3.Builder<*>

  val builderClass = newBuilder::class.java

  when (hapiPrimitive.fhirType()) {
    /* date , dateTime , instant also have a timeZone and Precision along with the value and thus needs to handle separately*/
    "date",
    "dateTime",
    "instant" -> {

      // To set value
      builderClass
        .getDeclaredMethod("setValueUs", getProtoDataTypeFromHapi(hapiPrimitive))
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
      // time also has precision along with the value and thus needs to handle separately however in
      // hapi time is a string type primitive and in fhir protos the time value is in long and so it
      // needs to be handled differently
      val (duration, precision) =
        getDurationPrecisionPairFromTimeString(hapiPrimitive.valueAsString)
      // To set Value
      builderClass
        .getDeclaredMethod("setValueUs", getProtoDataTypeFromHapi(hapiPrimitive))
        .invoke(newBuilder, duration)

      // To set Value
      builderClass
        .getDeclaredMethod("setPrecisionValue", Integer.TYPE)
        .invoke(newBuilder, precision)
    }
    "base64Binary" -> {
      // base64Binary in fhir expects a byte array and in fhir protos it expects a byteString
      builderClass
        .getDeclaredMethod(setValueMethodName, getProtoDataTypeFromHapi(hapiPrimitive))
        .invoke(newBuilder, ByteString.copyFrom((hapiPrimitive.valueAsString).toByteArray()))
    }
    "decimal" -> {
      // decimal value in fhir expects a long (however it can be set using a string by the
      // setValueAsString() method) and in fhir protos it expects a string
      builderClass
        .getDeclaredMethod(setValueMethodName, getProtoDataTypeFromHapi(hapiPrimitive))
        .invoke(newBuilder, hapiPrimitive.valueAsString)
    }
    else -> {
      // the remaining class have the same type for value in both hapi and fhir protos.
      builderClass
        .getDeclaredMethod(setValueMethodName, getProtoDataTypeFromHapi(hapiPrimitive))
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
  require(
    primitiveProto::class.java.simpleName == hapiClass.simpleName.removeSuffix(hapiPrimitiveSuffix)
  ) { "Cannot convert ${primitiveProto::class.java.name} to ${hapiClass.name}" }

  val primitive = hapiClass.newInstance()
  val protoClass = primitiveProto::class.java

  when (primitive.fhirType()) {
    "date", "dateTime", "instant" -> {

      /* date , dateTime , instant also have a timeZone and Precision along with the value and thus needs to handle separately*/

      // To set value
      primitive.value =
        Date(protoClass.getDeclaredMethod("getValueUs").invoke(primitiveProto) as Long)

      (protoClass.getMethod("getTimezone").invoke(primitiveProto) as String?)
        .takeUnless { it.isNullOrEmpty() }
        .let {
          primitive::class
            .java
            .getMethod("setTimeZone", TimeZone::class.java)
            // if timeZone is not set on proto then it should be null in the corresponding hapi
            // struct as well
            .invoke(primitive, if (it == null) null else TimeZone.getTimeZone(it))
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
      // time also has precision along with the value and thus needs to handle separately however in
      // hapi time is a string type primitive and in fhir protos the time value is in long and so it
      // needs to be handled differently
      primitive.valueAsString =
        getTimeStringFromDuration(
          protoClass.getDeclaredMethod("getValueUs").invoke(primitiveProto) as Long
        )
    }
    "base64Binary" -> {
      // base64Binary in fhir expects a byte array and in fhir protos it expects a byteString
      primitive.valueAsString =
        (protoClass.getMethod("getValue").invoke(primitiveProto) as ByteString).toStringUtf8()
    }
    "decimal" -> {
      // decimal value in fhir expects a long (however it can be set using a string by the
      // setValueAsString() method) and in fhir protos it expects a string
      primitive.valueAsString = protoClass.getMethod("getValue").invoke(primitiveProto) as String
    }
    else -> {
      // the remaining class have the same type for value in both hapi and fhir protos.
      primitive.value = protoClass.getMethod("getValue").invoke(primitiveProto)
    }
  }

  return primitive
}

/**
 * Suffix that needs to be removed from the hapi primitive so that the simple name matches the
 * corresponding proto primitive type
 *
 * For example StringType in hapi is equivalent to String in Fhir proto
 */
private const val hapiPrimitiveSuffix = "Type"

/**
 * Name of the method that is used to create a new builder of the proto class
 *
 * For example Patient.newBuilder() will return a new builder for a patient proto
 */
private const val newBuilderMethodName = "newBuilder"

/**
 * Name of the method of a primitive builder that is used to set value for a primitive proto
 *
 * For example String.newBuilder.setValue("") will set the value of the string
 */
private const val setValueMethodName = "setValue"
/**
 * returns proto value for precision of @param [precision] In protos the precision Enums are
 * different for different date types (Instant.Precision is not the same as Date.Precision) However
 * the values (integer corresponding to the Enum) are the same.
 *
 * For example Date.Precision.Year and DateTime.Precision.Year are the same.
 *
 * Note
 *
 * - value 0 maps to Precision.PRECISION_UNSPECIFIED
 *
 * - Precision.Minute is not present in fhir protos is mapped to Precision.UNRECOGNIZED
 *
 * - For instant proto only Second and Milli are valid precisions ( it also supports micro , however
 * micro isn't supported by hapi)
 */
private fun getValueForDateTimeEnum(precision: TemporalPrecisionEnum, fhirType: String): Int {
  val value =
    when (precision) {
      TemporalPrecisionEnum.YEAR -> 1
      TemporalPrecisionEnum.MONTH -> 2
      TemporalPrecisionEnum.DAY -> 3
      TemporalPrecisionEnum.SECOND -> 4
      TemporalPrecisionEnum.MILLI -> 5
      TemporalPrecisionEnum.MINUTE -> -1
      else -> 0
    }
  return if (fhirType == "instant") value - 3 else value
}

/**
 * returns temporalPrecisionEnum representation of @param [precision]
 *
 * @see getValueForDateTimeEnum
 *
 * Note when precision is unspecified it will be mapped to TemporalPrecisionEnum.Milli
 */
private fun getValueForDateTimeEnum(precision: Int, fhirType: String): TemporalPrecisionEnum {
  return when (if (fhirType == "instant") precision + 3 else precision) {
    1 -> TemporalPrecisionEnum.YEAR
    2 -> TemporalPrecisionEnum.MONTH
    3 -> TemporalPrecisionEnum.DAY
    4 -> TemporalPrecisionEnum.SECOND
    else -> TemporalPrecisionEnum.MILLI
  }
}

/**
 * returns javaClass of the value in the corresponding proto class of @param [hapiPrimitive]
 *
 * For example the setValue method of the base64Binary proto class expects a parameter of the type
 * ByteString Similarly the setValue method of the date proto class expects a parameter of the type
 * Long
 */
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
/**
 * returns duration (microseconds of the day) and precision from string representation of [time]
 * and the precision and value are used to set corresponding Time fhir proto.
 */
private fun getDurationPrecisionPairFromTimeString(time: String): Pair<Long, Int> {
  return (LocalTime.parse(time).toNanoOfDay() / 1000 to
    when (time.length) {
      SECONDS_PRECISION_STRING_LENGTH -> SECONDS_PRECISION_TIME
      MILLISECONDS_PRECISION_STRING_LENGTH -> MILLISECONDS_PRECISION_TIME
      else -> UNKNOWN_PRECISION_TIME
    })
}

/**
 * The length of the time string with precision as seconds
 *
 * For example when the time string is 10:00:00 the precision will be seconds
 */
private const val SECONDS_PRECISION_STRING_LENGTH = 8
/** The integer value that represent a Milliseconds precision in the Time.Precision Enum */
private const val SECONDS_PRECISION_TIME = 1

/**
 * The length of the time string with precision as milliseconds
 *
 * For example when the time string is 10:00:00.000 the precision will be milliseconds
 */
private const val MILLISECONDS_PRECISION_STRING_LENGTH = 11

/** The integer value that represent a Milliseconds precision in the Time.Precision Enum */
private const val MILLISECONDS_PRECISION_TIME = 2

private const val UNKNOWN_PRECISION_TIME = 0

/** returns string representation of [microOfDay] */
private fun getTimeStringFromDuration(microOfDay: Long): String {
  return LocalTime.ofNanoOfDay(microOfDay * 1000).format(DateTimeFormatter.ISO_LOCAL_TIME)
}
