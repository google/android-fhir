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

import com.google.common.truth.Truth
import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Canonical
import com.google.fhir.r4.core.Code
import com.google.fhir.r4.core.Date
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Instant
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Markdown
import com.google.fhir.r4.core.Oid
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Time
import com.google.fhir.r4.core.UnsignedInt
import com.google.fhir.r4.core.Uri
import com.google.fhir.r4.core.Url
import com.google.fhir.shaded.protobuf.ByteString
import com.google.fhir.shaded.protobuf.GeneratedMessageV3
import com.google.fhir.stu3.proto.Base64Binary as Base64BinaryStu3
import com.google.fhir.stu3.proto.Boolean as BooleanStu3
import com.google.fhir.stu3.proto.Code as CodeStu3
import com.google.fhir.stu3.proto.Date as DateStu3
import com.google.fhir.stu3.proto.DateTime as DateTimeStu3
import com.google.fhir.stu3.proto.Decimal as DecimalStu3
import com.google.fhir.stu3.proto.Id as IdStu3
import com.google.fhir.stu3.proto.Instant as InstantStu3
import com.google.fhir.stu3.proto.Integer as IntegerStu3
import com.google.fhir.stu3.proto.Markdown as MarkdownStu3
import com.google.fhir.stu3.proto.Oid as OidStu3
import com.google.fhir.stu3.proto.PositiveInt as PositiveIntStu3
import com.google.fhir.stu3.proto.String as StringStu3
import com.google.fhir.stu3.proto.Time as TimeStu3
import com.google.fhir.stu3.proto.UnsignedInt as UnsignedIntStu3
import com.google.fhir.stu3.proto.Uri as UriStu3
import com.google.fhir.stu3.proto.Uuid as UuidStu3
import com.google.fhir.stu3.proto.Uuid
import java.time.LocalTime
import org.hl7.fhir.dstu3.model.Base64BinaryType as Base64BinaryTypeStu3
import org.hl7.fhir.dstu3.model.BooleanType as BooleanTypeStu3
import org.hl7.fhir.dstu3.model.CodeType as CodeTypeStu3
import org.hl7.fhir.dstu3.model.DateTimeType as DateTimeTypeStu3
import org.hl7.fhir.dstu3.model.DateType as DateTypeStu3
import org.hl7.fhir.dstu3.model.DecimalType as DecimalTypeStu3
import org.hl7.fhir.dstu3.model.IdType as IdTypeStu3
import org.hl7.fhir.dstu3.model.InstantType as InstantTypeStu3
import org.hl7.fhir.dstu3.model.IntegerType as IntegerTypeStu3
import org.hl7.fhir.dstu3.model.MarkdownType as MarkdownTypeStu3
import org.hl7.fhir.dstu3.model.OidType as OidTypeStu3
import org.hl7.fhir.dstu3.model.PositiveIntType as PositiveIntTypeStu3
import org.hl7.fhir.dstu3.model.StringType as StringTypeStu3
import org.hl7.fhir.dstu3.model.TimeType as TimeTypeStu3
import org.hl7.fhir.dstu3.model.UnsignedIntType as UnsignedIntTypeStu3
import org.hl7.fhir.dstu3.model.UriType as UriTypeStu3
import org.hl7.fhir.dstu3.model.UuidType as UuidTypeStu3
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.OidType
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UnsignedIntType
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.model.UrlType
import org.hl7.fhir.r4.model.UuidType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Contains tests for PrimitiveConverter.kt */
@RunWith(Parameterized::class)
class PrimitiveConverterTest {
  @Parameterized.Parameter(0) lateinit var hapi: IPrimitiveType<*>
  @Parameterized.Parameter(1) lateinit var proto: GeneratedMessageV3
  @Test
  fun test_primitive_converter() {
    Truth.assertThat(convert(hapi, proto::class.java)).isEqualTo(proto)
    val converted = convert(proto, hapi::class.java)

    when (hapi.fhirType()) {
      "instant", "dateTime", "date" -> {
        // Compare time
        Truth.assertThat(converted.value::class.java.getMethod("getTime").invoke(converted.value))
          .isEqualTo(hapi.value::class.java.getMethod("getTime").invoke(hapi.value))
        // Compare time zone
        Truth.assertThat(converted::class.java.getMethod("getTimeZone").invoke(converted))
          .isEqualTo(hapi::class.java.getMethod("getTimeZone").invoke(hapi))
        // compare precision
        Truth.assertThat(converted::class.java.getMethod("getPrecision").invoke(converted))
          .isEqualTo(hapi::class.java.getMethod("getPrecision").invoke(hapi))
      }
      "base64Binary" -> {
        // compare ValueAsString because value is a byteArray
        Truth.assertThat(converted.valueAsString).isEqualTo(hapi.valueAsString)
      }
      else -> {
        Truth.assertThat(converted.value).isEqualTo(hapi.value)
      }
    }
  }

  private companion object {
    @Parameterized.Parameters
    @JvmStatic
    fun data() =
      listOf(
        arrayOf(BooleanType(false), Boolean.newBuilder().setValue(false).build()),
        arrayOf(BooleanTypeStu3(false), BooleanStu3.newBuilder().setValue(false).build()),
        arrayOf(IntegerType(9), Integer.newBuilder().setValue(9).build()),
        arrayOf(IntegerTypeStu3(9), IntegerStu3.newBuilder().setValue(9).build()),
        arrayOf(StringType("String"), String.newBuilder().setValue("String").build()),
        arrayOf(StringTypeStu3("String"), StringStu3.newBuilder().setValue("String").build()),
        arrayOf(DecimalType("100.10"), Decimal.newBuilder().setValue("100.10").build()),
        arrayOf(DecimalTypeStu3("100.10"), DecimalStu3.newBuilder().setValue("100.10").build()),
        arrayOf(
          UriType("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7"),
          Uri.newBuilder().setValue("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7").build()
        ),
        arrayOf(
          UriTypeStu3("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7"),
          UriStu3.newBuilder().setValue("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7").build()
        ),
        arrayOf(
          UrlType("https://example.com"),
          Url.newBuilder().setValue("https://example.com").build()
        ),
        arrayOf(CanonicalType("Library/3"), Canonical.newBuilder().setValue("Library/3").build()),
        arrayOf(
          Base64BinaryType("QmFzZTY0Cg=="),
          Base64Binary.newBuilder()
            .setValue(ByteString.copyFrom("QmFzZTY0Cg==".toByteArray()))
            .build()
        ),
        arrayOf(
          Base64BinaryTypeStu3("QmFzZTY0Cg=="),
          Base64BinaryStu3.newBuilder()
            .setValue(ByteString.copyFrom("QmFzZTY0Cg==".toByteArray()))
            .build()
        ),
        arrayOf(
          InstantType("2001-01-01T10:00:00.000+05:30"),
          Instant.newBuilder()
            .setValueUs(InstantType("2001-01-01T10:00:00.000+05:30").value.time)
            .setPrecision(Instant.Precision.MILLISECOND)
            .setTimezone(InstantType("2001-01-01T10:00:00.000+05:30").timeZone.id)
            .build()
        ),
        arrayOf(
          InstantTypeStu3("2001-01-01T10:00:00.000+05:30"),
          InstantStu3.newBuilder()
            .setValueUs(InstantType("2001-01-01T10:00:00.000+05:30").value.time)
            .setPrecision(InstantStu3.Precision.MILLISECOND)
            .setTimezone(InstantType("2001-01-01T10:00:00.000+05:30").timeZone.id)
            .build()
        ),
        arrayOf(
          DateType("2001-01-01"),
          Date.newBuilder()
            .setValueUs(DateType("2001-01-01").value.time)
            .setPrecision(Date.Precision.DAY)
            .build()
        ),
        arrayOf(
          DateTypeStu3("2001-01-01"),
          DateStu3.newBuilder()
            .setValueUs(DateTypeStu3("2001-01-01").value.time)
            .setPrecision(DateStu3.Precision.DAY)
            .build()
        ),
        arrayOf(
          DateTimeType("2001-01-01T10:00:00+05:30"),
          DateTime.newBuilder()
            .setValueUs(DateTimeType("2001-01-01T10:00:00+05:30").value.time)
            .setPrecision(DateTime.Precision.SECOND)
            .setTimezone(DateTimeType("2001-01-01T10:00:00+05:30").timeZone.id)
            .build()
        ),
        arrayOf(
          DateTimeTypeStu3("2001-01-01T10:00:00+05:30"),
          DateTimeStu3.newBuilder()
            .setValueUs(DateTimeTypeStu3("2001-01-01T10:00:00+05:30").value.time)
            .setPrecision(DateTimeStu3.Precision.SECOND)
            .setTimezone(DateTimeType("2001-01-01T10:00:00+05:30").timeZone.id)
            .build()
        ),
        arrayOf(
          TimeType("10:00:00"),
          Time.newBuilder()
            .setValueUs(LocalTime.parse("10:00:00").toNanoOfDay() / 1000)
            .setPrecision(Time.Precision.SECOND)
            .build()
        ),
        arrayOf(
          TimeTypeStu3("10:00:00"),
          TimeStu3.newBuilder()
            .setValueUs(LocalTime.parse("10:00:00").toNanoOfDay() / 1000)
            .setPrecision(TimeStu3.Precision.SECOND)
            .build()
        ),
        arrayOf(
          CodeType().apply { value = "something" },
          Code.newBuilder().setValue("something").build()
        ),
        arrayOf(
          CodeTypeStu3().apply { value = "something" },
          CodeStu3.newBuilder().setValue("something").build()
        ),
        arrayOf(
          OidType("urn:oid:1.2.3.4.5"),
          Oid.newBuilder().setValue("urn:oid:1.2.3.4.5").build()
        ),
        arrayOf(
          OidTypeStu3("urn:oid:1.2.3.4.5"),
          OidStu3.newBuilder().setValue("urn:oid:1.2.3.4.5").build()
        ),
        arrayOf(IdType("ID"), Id.newBuilder().setValue("ID").build()),
        arrayOf(IdTypeStu3("ID"), IdStu3.newBuilder().setValue("ID").build()),
        arrayOf(MarkdownType("**bold**"), Markdown.newBuilder().setValue("**bold**").build()),
        arrayOf(
          MarkdownTypeStu3("**bold**"),
          MarkdownStu3.newBuilder().setValue("**bold**").build()
        ),
        arrayOf(UnsignedIntType(0), UnsignedInt.newBuilder().setValue(0).build()),
        arrayOf(UnsignedIntTypeStu3(0), UnsignedIntStu3.newBuilder().setValue(0).build()),
        arrayOf(PositiveIntType(200), PositiveInt.newBuilder().setValue(200).build()),
        arrayOf(PositiveIntTypeStu3(200), PositiveIntStu3.newBuilder().setValue(200).build()),
        arrayOf(
          UuidType("urn:uuid:c757873d-ec9a-4326-a141-556f43239520"),
          Uuid.newBuilder().setValue("urn:uuid:c757873d-ec9a-4326-a141-556f43239520").build()
        ),
        arrayOf(
          UuidTypeStu3("urn:uuid:c757873d-ec9a-4326-a141-556f43239520"),
          UuidStu3.newBuilder().setValue("urn:uuid:c757873d-ec9a-4326-a141-556f43239520").build()
        )
      )
  }
}
