/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import android.os.Build
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.TimeZone
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.MarkdownType
import org.hl7.fhir.r4.model.OidType
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UnsignedIntType
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.model.UrlType
import org.hl7.fhir.r4.model.UuidType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreTypesTest {

  @Test
  fun instant_shouldReturnExpectedStringValue() {
    val value =
      InstantType(
        Date.from(Instant.ofEpochMilli(1609459200000)),
        TemporalPrecisionEnum.SECOND,
        TimeZone.getTimeZone(ZoneId.of("GMT"))
      )
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun time_shouldReturnExpectedStringValue() {
    val value = TimeType("18:00:59")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun dateTime_shouldReturnExpectedStringValue() {
    val value =
      DateTimeType(
        Date.from(Instant.ofEpochMilli(1609459200000)),
        TemporalPrecisionEnum.SECOND,
        TimeZone.getTimeZone(ZoneId.of("GMT"))
      )
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun base64Binary_shouldReturnExpectedStringValue() {
    val value = Base64BinaryType("FHIR")
    value.asStringValue()
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun decimal_shouldReturnExpectedStringValue() {
    val value = DecimalType(3.14)
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun boolean_shouldReturnExpectedStringValue() {
    val value = BooleanType(true)
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun code_shouldReturnExpectedStringValue() {
    val value = CodeType("val pi = 3.14")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun markDown_shouldReturnExpectedStringValue() {
    val value = MarkdownType("`val pi = 3.14`")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun id_shouldReturnExpectedStringValue() {
    val value = IdType("12345")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun integer_shouldReturnExpectedStringValue() {
    val value = IntegerType(-12345)
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun unsignedInt_shouldReturnExpectedStringValue() {
    val value = UnsignedIntType(12345)
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun positiveInt_shouldReturnExpectedStringValue() {
    val value = PositiveIntType(12345)
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun uri_shouldReturnExpectedStringValue() {
    val value = UriType("https://www.google.com")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun url_shouldReturnExpectedStringValue() {
    val value = UrlType("https://www.google.com")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun canonical_shouldReturnExpectedStringValue() {
    val value = CanonicalType("urn:oid:0.1.2.3.4")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun uuid_shouldReturnExpectedStringValue() {
    val value = UuidType("UUID")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun oid_shouldReturnExpectedStringValue() {
    val value = OidType("urn:oid:0.1.2.3.4")
    assertThat((value as Type).asStringValue()).isEqualTo(value.asStringValue())
  }

  @Test
  fun nonPrimitive_shouldReturnEmptyString() {
    val value = Quantity(1234567.89)
    assertThat((value as Type).asStringValue()).isEqualTo("")
  }
  @Test
  fun stringType_toUriType() {
    val uri = StringType("fakeUri").toUriType()
    assertThat(uri.equalsDeep(UriType("fakeUri"))).isTrue()
  }

  @Test
  fun stringType_toCodeType() {
    val code = StringType("fakeCode").toCodeType()
    assertThat(code.equalsDeep(CodeType("fakeCode"))).isTrue()
  }

  @Test
  fun stringType_toIdType() {
    val id = StringType("fakeId").toIdType()
    assertThat(id.equalsDeep(IdType("fakeId"))).isTrue()
  }

  @Test
  fun coding_toCodeType() {
    val code = Coding("fakeSystem", "fakeCode", "fakeDisplay").toCodeType()
    assertThat(code.equalsDeep(CodeType("fakeCode"))).isTrue()
  }

  @Test
  fun `should return calculated value for cqf expression`() {
    val today = LocalDate.now().toString()
    val type =
      DateType().apply {
        extension =
          listOf(
            Extension(
              EXTENSION_CQF_CALCULATED_VALUE_URL,
              Expression().apply {
                language = "text/fhirpath"
                expression = "today()"
              }
            )
          )
      }
    assertThat((type.valueOrCalculateValue() as DateType).valueAsString).isEqualTo(today)
  }

  @Test
  fun `should return calculated value for a non-cqf extension`() {
    LocalDate.now().toString()
    val type =
      DateType().apply {
        extension =
          listOf(
            Extension(
              "http://hl7.org/fhir/StructureDefinition/my-own-expression",
              Expression().apply {
                language = "text/fhirpath"
                expression = "today()"
              }
            )
          )
      }
    assertThat((type.valueOrCalculateValue() as DateType).valueAsString).isEqualTo(null)
  }

  @Test
  fun `should return entered value when no cqf expression is defined`() {
    val type = IntegerType().apply { value = 500 }
    assertThat((type.valueOrCalculateValue() as IntegerType).value).isEqualTo(500)
  }
}
