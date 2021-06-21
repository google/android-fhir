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
import com.google.fhir.r4.core.Uuid
import com.google.fhir.shaded.protobuf.ByteString
import java.time.LocalTime
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

/** Contains tests for PrimitiveConverter.kt */
class PrimitiveConverterTest {

  @Test
  fun test_primitive_converter_boolean() {
    val hapi = BooleanType(false)
    val proto = Boolean.newBuilder().setValue(false).build()
    Truth.assertThat(convert(hapi, Boolean::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, BooleanType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_integer() {
    val hapi = IntegerType(9)
    val proto = Integer.newBuilder().setValue(9).build()
    Truth.assertThat(convert(hapi, Integer::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, IntegerType::class.java).value).isEqualTo(hapi.value)
  }
  @Test
  fun test_primitive_converter_string() {
    val hapi = StringType("String")
    val proto = String.newBuilder().setValue("String").build()
    Truth.assertThat(convert(hapi, String::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, StringType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_decimal() {
    val hapi = DecimalType("100.10")
    val proto = Decimal.newBuilder().setValue("100.10").build()
    Truth.assertThat(convert(hapi, Decimal::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, DecimalType::class.java).valueAsString).isEqualTo(proto.value)
  }

  @Test
  fun test_primitive_converter_uri() {
    val hapi = UriType("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7")
    val proto = Uri.newBuilder().setValue("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7").build()
    Truth.assertThat(convert(hapi, Uri::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, UriType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_url() {
    val hapi = UrlType("https://example.com")
    val proto = Url.newBuilder().setValue("https://example.com").build()
    Truth.assertThat(convert(hapi, Url::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, UrlType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_canonical() {
    val hapi = CanonicalType("Library/3")
    val proto = Canonical.newBuilder().setValue("Library/3").build()
    Truth.assertThat(convert(hapi, Canonical::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, CanonicalType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_base64Binary() {
    val hapi = Base64BinaryType("QmFzZTY0Cg==")
    val proto =
      Base64Binary.newBuilder().setValue(ByteString.copyFrom("QmFzZTY0Cg==".toByteArray())).build()
    Truth.assertThat(convert(hapi, Base64Binary::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, Base64BinaryType::class.java).valueAsString)
      .isEqualTo(hapi.valueAsString)
  }

  @Test
  fun test_primitive_converter_Instant() {
    val hapi = InstantType("2001-01-01T10:00:00.000+05:30")
    val proto =
      Instant.newBuilder()
        .setValueUs(hapi.value.time)
        .setPrecision(Instant.Precision.MILLISECOND)
        .setTimezone(hapi.timeZone.id)
        .build()
    Truth.assertThat(convert(hapi, Instant::class.java)).isEqualTo(proto)

    val converted = convert(proto, InstantType::class.java)
    Truth.assertThat(converted.value.time).isEqualTo(hapi.value.time)
    Truth.assertThat(converted.timeZone).isEqualTo(hapi.timeZone)
    Truth.assertThat(converted.precision).isEqualTo(hapi.precision)
  }

  @Test
  fun test_primitive_converter_date() {
    val hapi = DateType("2001-01-01")
    val proto =
      Date.newBuilder().setValueUs(hapi.value.time).setPrecision(Date.Precision.DAY).build()
    Truth.assertThat(convert(hapi, Date::class.java)).isEqualTo(proto)

    val converted = convert(proto, DateType::class.java)
    Truth.assertThat(converted.value.time).isEqualTo(hapi.value.time)
    Truth.assertThat(converted.precision).isEqualTo(hapi.precision)
  }

  @Test
  fun test_primitive_converter_dateTime() {
    val hapi = DateTimeType("2001-01-01T10:00:00+05:30")
    val proto =
      DateTime.newBuilder()
        .setValueUs(hapi.value.time)
        .setPrecision(DateTime.Precision.SECOND)
        .setTimezone(hapi.timeZone.id)
        .build()

    val converted = convert(proto, DateTimeType::class.java)
    Truth.assertThat(convert(hapi, DateTime::class.java)).isEqualTo(proto)
    Truth.assertThat(converted.value.time).isEqualTo(hapi.value.time)
    Truth.assertThat(converted.timeZone).isEqualTo(hapi.timeZone)
    Truth.assertThat(converted.precision).isEqualTo(hapi.precision)
  }

  @Test
  fun test_primitive_converter_time() {
    val hapi = TimeType("10:00:00")
    val proto =
      Time.newBuilder()
        .setValueUs(LocalTime.parse("10:00:00").toNanoOfDay() / 1000)
        .setPrecision(Time.Precision.SECOND)
        .build()
    Truth.assertThat(convert(hapi, Time::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, TimeType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_code() {
    val hapi = CodeType().apply { value = "something" }
    val proto = Code.newBuilder().setValue("something").build()
    Truth.assertThat(convert(hapi, Code::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, CodeType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_oid() {
    val hapi = OidType("urn:oid:1.2.3.4.5")
    val proto = Oid.newBuilder().setValue("urn:oid:1.2.3.4.5").build()
    Truth.assertThat(convert(hapi, Oid::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, OidType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_Id() {
    val hapi = IdType("ID")
    val proto = Id.newBuilder().setValue("ID").build()
    Truth.assertThat(convert(hapi, Id::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, IdType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_markdown() {
    val hapi = MarkdownType("**bold**")
    val proto = Markdown.newBuilder().setValue("**bold**").build()
    Truth.assertThat(convert(hapi, Markdown::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, MarkdownType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_unsignedInt() {
    val hapi = UnsignedIntType(200)
    val proto = UnsignedInt.newBuilder().setValue(200).build()
    Truth.assertThat(convert(hapi, UnsignedInt::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, UnsignedIntType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_positiveInt() {
    val hapi = PositiveIntType(200)
    val proto = PositiveInt.newBuilder().setValue(200).build()
    Truth.assertThat(convert(hapi, PositiveInt::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, PositiveIntType::class.java).value).isEqualTo(hapi.value)
  }

  @Test
  fun test_primitive_converter_UUID() {
    val hapi = UuidType("urn:uuid:c757873d-ec9a-4326-a141-556f43239520")
    val proto = Uuid.newBuilder().setValue("urn:uuid:c757873d-ec9a-4326-a141-556f43239520").build()
    Truth.assertThat(convert(hapi, Uuid::class.java)).isEqualTo(proto)
    Truth.assertThat(convert(proto, UuidType::class.java).value).isEqualTo(hapi.value)
  }
}
