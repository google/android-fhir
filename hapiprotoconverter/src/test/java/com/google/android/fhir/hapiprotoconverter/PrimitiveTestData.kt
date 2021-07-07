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
import java.util.TimeZone
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

object PrimitiveTestData {

  val BASE64BINARY_DATA =
    listOf(
      arrayOf(
        Base64BinaryType("QmFzZTY0Cg=="),
        Base64Binary.newBuilder()
          .setValue(ByteString.copyFrom("QmFzZTY0Cg==".toByteArray()))
          .build()
      )
    )

  val BOOLEAN_DATA =
    listOf(arrayOf(BooleanType(false), Boolean.newBuilder().setValue(false).build()))

  val CANONICAL_DATA =
    listOf(
      arrayOf(CanonicalType("Library/3"), Canonical.newBuilder().setValue("Library/3").build())
    )

  val CODE_DATA =
    listOf(
      arrayOf(
        CodeType().apply { value = "something" },
        Code.newBuilder().setValue("something").build()
      )
    )

  val DATE_DATA =
    listOf(
      arrayOf(
        DateType("2001-01-01").apply { timeZone = TimeZone.getTimeZone("UTC") },
        Date.newBuilder()
          .setValueUs(DateType("2001-01-01").value.time)
          .setPrecision(Date.Precision.DAY)
          .setTimezone("UTC")
          .build()
      )
    )

  val DATETIME_DATA =
    listOf(
      arrayOf(
        DateTimeType("2001-01-01T10:00:00+05:30"),
        DateTime.newBuilder()
          .setValueUs(DateTimeType("2001-01-01T10:00:00+05:30").value.time)
          .setPrecision(DateTime.Precision.SECOND)
          .setTimezone(DateTimeType("2001-01-01T10:00:00+05:30").timeZone.id)
          .build()
      )
    )

  val DECIMAL_DATA =
    listOf(arrayOf(DecimalType("100.10"), Decimal.newBuilder().setValue("100.10").build()))

  val ID_DATA = listOf(arrayOf(IdType("ID"), Id.newBuilder().setValue("ID").build()))

  val INSTANT_DATA =
    listOf(
      arrayOf(
        InstantType("2001-01-01T10:00:00.000+05:30"),
        Instant.newBuilder()
          .setValueUs(InstantType("2001-01-01T10:00:00.000+05:30").value.time)
          .setPrecision(Instant.Precision.MILLISECOND)
          .setTimezone(InstantType("2001-01-01T10:00:00.000+05:30").timeZone.id)
          .build()
      )
    )

  val INTEGER_DATA = listOf(arrayOf(IntegerType(9), Integer.newBuilder().setValue(9).build()))

  val MARKDOWN_DATA =
    listOf(arrayOf(MarkdownType("**bold**"), Markdown.newBuilder().setValue("**bold**").build()))

  val OID_DATA =
    listOf(
      arrayOf(OidType("urn:oid:1.2.3.4.5"), Oid.newBuilder().setValue("urn:oid:1.2.3.4.5").build())
    )

  val POSITIVEINT_DATA =
    listOf(arrayOf(PositiveIntType(200), PositiveInt.newBuilder().setValue(200).build()))

  val STRING_DATA =
    listOf(arrayOf(StringType("String"), String.newBuilder().setValue("String").build()))

  val TIME_DATA =
    listOf(
      arrayOf(
        TimeType("10:00:00"),
        Time.newBuilder()
          .setValueUs(LocalTime.parse("10:00:00").toNanoOfDay() / 1000)
          .setPrecision(Time.Precision.SECOND)
          .build()
      )
    )

  val UNSIGNEDINT_DATA =
    listOf(arrayOf(UnsignedIntType(0), UnsignedInt.newBuilder().setValue(0).build()))

  val URI_DATA =
    listOf(
      arrayOf(
        UriType("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7"),
        Uri.newBuilder().setValue("urn:uuid:53fefa32-fcbb-4ff8-8a92-55ee120877b7").build()
      )
    )

  val URL_DATA =
    listOf(
      arrayOf(
        UrlType("https://example.com"),
        Url.newBuilder().setValue("https://example.com").build()
      )
    )

  val UUID_DATA =
    listOf(
      arrayOf(
        UuidType("urn:uuid:c757873d-ec9a-4326-a141-556f43239520"),
        Uuid.newBuilder().setValue("urn:uuid:c757873d-ec9a-4326-a141-556f43239520").build()
      )
    )
}
