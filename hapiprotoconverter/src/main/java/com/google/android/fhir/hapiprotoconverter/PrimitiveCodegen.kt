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
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.shaded.common.collect.ImmutableList
import com.google.fhir.shaded.common.collect.ImmutableMap
import com.google.fhir.shaded.protobuf.ByteString
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.time.Instant
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

object PrimitiveCodegen {

  private const val protoPackage = "com.google.fhir.r4.core"
  private const val hapiPackage = "org.hl7.fhir.r4.model"

  private val TIME_LIKE_PRECISION_MAP: ImmutableMap<String, List<String>> =
    ImmutableMap.of(
      "date",
      ImmutableList.of("YEAR", "MONTH", "DAY"),
      "dateTime",
      ImmutableList.of("YEAR", "MONTH", "DAY", "SECOND", "MILLISECOND"),
      "instant",
      ImmutableList.of("SECOND", "MILLISECOND")
    )

  @SuppressLint("DefaultLocale")
  fun generate(def: StructureDefinition, outLocation: File? = null) {
    //    require(def.kind.value == StructureDefinitionKindCode.Value.PRIMITIVE_TYPE) {
    //      "structure definition needs to be of type primitive"
    //    }
    val hapiName = "${def.id.value.capitalize()}Type"
    val protoName = def.id.value.capitalize()

    val fileBuilder =
      FileSpec.builder(
        "com.google.android.fhir.hapiprotoconverter.generated",
        "${protoName}Converter"
      )
    val primitiveConverterClass =
      ClassName(
        "com.google.android.fhir.hapiprotoconverter.generated",
        def.id.value.capitalize() + "Converter"
      )

    val functionsList = mutableListOf<FunSpec>()

    val toProtoBuilder =
      FunSpec.builder("toProto")
        .receiver(Class.forName("$hapiPackage.$hapiName"))
        .returns(Class.forName("$protoPackage.$protoName"))
        .addStatement("val protoValue = %T.newBuilder()", Class.forName("$protoPackage.$protoName"))

    val toHapiBuilder =
      FunSpec.builder("toHapi")
        .receiver(Class.forName("$protoPackage.$protoName"))
        .returns(Class.forName("$hapiPackage.$hapiName"))
        .addStatement("val hapiValue = %T()", Class.forName("$hapiPackage.$hapiName"))

    if (def.id.value in TIME_LIKE_PRECISION_MAP.keys) {
      toProtoBuilder.addStatement(".setTimezone(timeZone.id)")
      toProtoBuilder.addStatement(".setValueUs(value.time)")
      toHapiBuilder.addStatement(
        "hapiValue.value = %T.from(%T.ofEpochMilli(valueUs))",
        Date::class,
        Instant::class
      )
      if (def.id.value == "instant") {
        fileBuilder.addAliasedImport(Instant::class, "InstantUtil")
      }
      toHapiBuilder.addStatement("hapiValue.timeZone = %T.getTimeZone(timezone)", TimeZone::class)
    }

    when (def.id.value) {
      in TIME_LIKE_PRECISION_MAP.keys -> {
        toProtoBuilder.addStatement(".setPrecision(precision.toProtoPrecision())")
        val precisionToProtoFunc =
          FunSpec.builder("toProtoPrecision")
            .addModifiers(KModifier.PRIVATE)
            .receiver(TemporalPrecisionEnum::class)
            .returns(Class.forName("$protoPackage.$protoName\$Precision"))
            .beginControlFlow("return when(this)")

        val precisionToHapiFunc =
          FunSpec.builder("toHapiPrecision")
            .addModifiers(KModifier.PRIVATE)
            .receiver(Class.forName("$protoPackage.$protoName\$Precision"))
            .returns(TemporalPrecisionEnum::class)
            .beginControlFlow("return when(this)")

        for (value in TIME_LIKE_PRECISION_MAP[def.id.value]!!) {
          if (value == "MILLISECOND") {
            precisionToProtoFunc.addStatement(
              "TemporalPrecisionEnum.${value.removeSuffix("SECOND")} -> $protoName.Precision.$value"
            )
            precisionToHapiFunc.addStatement(
              "$protoName.Precision.$value-> TemporalPrecisionEnum.${value.removeSuffix("SECOND")}"
            )
          } else {
            precisionToProtoFunc.addStatement(
              "TemporalPrecisionEnum.$value -> $protoName.Precision.$value"
            )
            precisionToHapiFunc.addStatement(
              "$protoName.Precision.$value -> TemporalPrecisionEnum.$value"
            )
          }
        }

        precisionToProtoFunc.addStatement("else ->$protoName.Precision.PRECISION_UNSPECIFIED")
        precisionToHapiFunc.addStatement("else ->TemporalPrecisionEnum.MILLI")
        precisionToProtoFunc.endControlFlow()
        precisionToHapiFunc.endControlFlow()
        functionsList.add(precisionToProtoFunc.build())
        functionsList.add(precisionToHapiFunc.build())
      }
      "time" -> {
        toProtoBuilder.addStatement(
          ".setValueUs(%T.parse(value).toNanoOfDay()/1000)",
          LocalTime::class
        )
        toHapiBuilder.addStatement(
          "hapiValue.value = %T.ofNanoOfDay(valueUs).format(%T.ISO_LOCAL_TIME)",
          LocalTime::class,
          DateTimeFormatter::class
        )
        toProtoBuilder.addStatement(".setPrecisionValue(getTimePrecision(value))")
        val precisionToProtoFunc =
          FunSpec.builder("getTimePrecision")
            .addModifiers(KModifier.PRIVATE)
            .addParameter("timeString", String::class)
            .returns(Integer.TYPE)
            .beginControlFlow("return when(timeString.length)")
            .addStatement("8 -> Time.Precision.SECOND_VALUE")
            .addStatement("11-> Time.Precision.MILLISECOND_VALUE")
            .addStatement("else -> -1")
            .endControlFlow()
        functionsList.add(precisionToProtoFunc.build())
      }
      "base64Binary" -> {
        toProtoBuilder.addStatement(
          ".setValue( %T.copyFrom((valueAsString).toByteArray()))",
          ByteString::class
        )
        toHapiBuilder.addStatement("hapiValue.value = value.toStringUtf8().toByteArray()")
      }
      "decimal" -> {
        toProtoBuilder.addStatement(".setValue(valueAsString)")
        toHapiBuilder.addStatement("hapiValue.valueAsString = value")
      }
      else -> {
        toProtoBuilder.addStatement(".setValue(value)")
        toHapiBuilder.addStatement("hapiValue.value = value")
      }
    }
    toProtoBuilder.addStatement(".build()")
    toProtoBuilder.addStatement("return protoValue")
    toHapiBuilder.addStatement("return hapiValue")
    functionsList.add(0, toProtoBuilder.build())
    functionsList.add(1, toHapiBuilder.build())

    fileBuilder
      .addType(
        TypeSpec.objectBuilder(primitiveConverterClass.simpleName)
          .addFunctions(functionsList)
          .build()
      )
      .build()
      // Write to System.out for now
       .writeTo(outLocation!!)
      //.writeTo(System.out)
  }
}

