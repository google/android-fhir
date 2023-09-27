/*
 * Copyright 2021-2023 Google LLC
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

package com.google.android.fhir

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.util.Calendar
import java.util.Locale
import kotlin.test.assertFailsWith
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreTypesTest {

  @Test
  fun equals_differentTypes_shouldReturnFalse() {
    assertThat(equals(BooleanType(true), DecimalType(1.1))).isFalse()
  }

  @Test
  fun equals_sameObject_shouldReturnTrue() {
    val value = BooleanType(true)
    assertThat(equals(value, value)).isTrue()
  }

  @Test
  fun equals_samePrimitiveValue_shouldReturnTrue() {
    assertThat(equals(DecimalType(1.1), DecimalType(1.1))).isTrue()
  }

  @Test
  fun equals_differentPrimitiveValues_shouldReturnFalse() {
    assertThat(equals(DecimalType(1.1), DecimalType(1.2))).isFalse()
  }

  @Test
  fun equals_coding_differentSystems_shouldReturnFalse() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("otherSystem", "code", "display")),
      )
      .isFalse()
  }

  @Test
  fun equals_coding_differentCodes_shouldReturnFalse() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("system", "otherCode", "display")),
      )
      .isFalse()
  }

  @Test
  fun equals_coding_differentDisplays_shouldReturnTrue() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("system", "code", "otherDisplay")),
      )
      .isTrue()
  }

  @Test
  fun equals_attachment_shouldThrowException() {
    val exception =
      assertThrows(NotImplementedError::class.java) { equals(Attachment(), Attachment()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Attachment::class.java} not supported.")
  }

  @Test
  fun equals_quantity_shouldThrowException() {
    val exception = assertThrows(NotImplementedError::class.java) { equals(Quantity(), Quantity()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Quantity::class.java} not supported.")
  }

  @Test
  fun equals_reference_shouldThrowException() {
    val exception =
      assertThrows(NotImplementedError::class.java) { equals(Reference(), Reference()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Reference::class.java} not supported.")
  }

  @Test
  fun compareTo_int_shouldReturnPositiveValue() {
    val integerValue = IntegerType()
    integerValue.value = 20
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 19
    assertThat(integerValue.compareTo(integerValueToBeCompared)).isEqualTo(1)
  }

  @Test
  fun compareTo_int_shouldReturnZero() {
    val integerValue = IntegerType()
    integerValue.value = 20
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 20
    assertThat(integerValue.compareTo(integerValueToBeCompared)).isEqualTo(0)
  }

  @Test
  fun compareTo_int_shouldReturnNegativeValue() {
    val integerValue = IntegerType()
    integerValue.value = 19
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 20
    assertThat(integerValue.compareTo(integerValueToBeCompared)).isEqualTo(-1)
  }

  @Test
  fun compareTo_decimal_shouldReturnPositiveValue() {
    val decimalValue = DecimalType()
    decimalValue.setValue(20.2)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(19.21)
    assertThat(decimalValue.compareTo(decimalValueToBeCompared)).isEqualTo(1)
  }

  @Test
  fun compareTo_decimal_shouldReturnZero() {
    val decimalValue = DecimalType()
    decimalValue.setValue(20.2)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(20.2)
    assertThat(decimalValue.compareTo(decimalValueToBeCompared)).isEqualTo(0)
  }

  @Test
  fun compareTo_decimal_shouldReturnNegativeValue() {
    val decimalValue = DecimalType()
    decimalValue.setValue(19.21)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(20.2)
    assertThat(decimalValue.compareTo(decimalValueToBeCompared)).isEqualTo(-1)
  }

  @Test
  fun compareTo_dateTime_shouldReturnPositiveValue() {
    val dateValue = DateType()
    dateValue.value = Calendar.getInstance().time
    val dateValueToBeCompared = DateType()
    val calendarInstance = Calendar.getInstance()
    calendarInstance.set(1994, 5, 6)
    dateValueToBeCompared.value = calendarInstance.time
    assertThat(dateValue.compareTo(dateValueToBeCompared)).isEqualTo(1)
  }

  @Test
  fun compareTo_dateTime_shouldReturnZero() {
    val dateValue = DateType()
    dateValue.value = Calendar.getInstance().time
    val dateValueToBeCompared = DateType()
    dateValueToBeCompared.value = Calendar.getInstance().time
    assertThat(dateValue.compareTo(dateValueToBeCompared)).isEqualTo(0)
  }

  @Test
  fun compareTo_dateTime_shouldReturnNegativeValue() {
    val dateValue = DateType()
    val calendarInstance = Calendar.getInstance()
    calendarInstance.set(1994, 5, 6)
    dateValue.value = calendarInstance.time
    val dateValueToBeCompared = DateType()
    dateValueToBeCompared.value = Calendar.getInstance().time
    assertThat(dateValue.compareTo(dateValueToBeCompared)).isEqualTo(-1)
  }

  @Test
  fun compareTo_shouldThrowExceptionInCaseOfTypeMismatch() {
    val decimalValue = DecimalType()
    decimalValue.setValue(19.21)
    val integerValue = IntegerType()
    integerValue.value = 19
    val exception =
      assertThrows(IllegalArgumentException::class.java) { decimalValue > integerValue }
    assertThat(exception.message)
      .isEqualTo("Cannot compare different data types: decimal and integer")
  }

  @Test
  fun compareTo_quantityType_shouldReturnPositiveValue() {
    val value = Quantity().setCode("h").setValue(10)
    val otherValue = Quantity().setCode("h").setValue(5)

    assertThat(value.compareTo(otherValue)).isEqualTo(1)
  }

  @Test
  fun compareTo_quantityType_shouldReturnZero() {
    val value = Quantity().setCode("h").setValue(10)
    val otherValue = Quantity().setCode("h").setValue(10)

    assertThat(value.compareTo(otherValue)).isEqualTo(0)
  }

  @Test
  fun compareTo_quantityType_shouldReturnNegativeValue() {
    val value = Quantity().setCode("h").setValue(10)
    val otherValue = Quantity().setCode("h").setValue(20)

    assertThat(value.compareTo(otherValue)).isEqualTo(-1)
  }

  @Test
  fun compareTo_quantityWithDifferentCodes_shouldFail() {
    assertFailsWith<IllegalArgumentException> {
      val value = Quantity().setCode("h").setValue(10)
      val otherValue = Quantity().setCode("kg").setValue(5)
      value.compareTo(otherValue)
    }
  }

  @Test
  fun localizedText_swahiliTranslation_shouldReturnTranslatedText() {
    Locale.setDefault(Locale.forLanguageTag("sw"))

    val displayElement =
      StringType("Man").apply {
        addExtension(
          Extension(ToolingExtensions.EXT_TRANSLATION).apply {
            addExtension(Extension("lang", StringType("sw")))
            addExtension(Extension("content", StringType("Mwanaume")))
          },
        )
      }

    assertThat(displayElement.getLocalizedText()).isEqualTo("Mwanaume")
  }

  @Test
  fun localizedText_swahiliTranslationWithLocale_shouldReturnTranslatedText() {
    Locale.setDefault(Locale.forLanguageTag("sw-KE"))

    val displayElement =
      StringType("Woman").apply {
        addExtension(
          Extension(ToolingExtensions.EXT_TRANSLATION).apply {
            addExtension(Extension("lang", StringType("sw-KE")))
            addExtension(Extension("content", StringType("Mwanamke")))
          },
        )
      }

    assertThat(displayElement.getLocalizedText()).isEqualTo("Mwanamke")
  }

  @Test
  fun localizedText_noMatchingLocaleTranslation_shouldReturnDefaultText() {
    Locale.setDefault(Locale.forLanguageTag("sw-KE"))

    val displayElement =
      StringType("Woman").apply {
        addExtension(
          Extension(ToolingExtensions.EXT_TRANSLATION).apply {
            addExtension(Extension("lang", StringType("fr-FR")))
            addExtension(Extension("content", StringType("Femme")))
          },
        )
      }

    assertThat(displayElement.getLocalizedText()).isEqualTo("Woman")
  }

  @Test
  fun localizedText_localeTagWithCountry_shouldReturnTranslatedText() {
    Locale.setDefault(Locale.forLanguageTag("fr-FR"))

    val displayElement =
      StringType("Woman").apply {
        addExtension(
          Extension(ToolingExtensions.EXT_TRANSLATION).apply {
            addExtension(Extension("lang", StringType("fr")))
            addExtension(Extension("content", StringType("Femme")))
          },
        )
      }

    assertThat(displayElement.getLocalizedText()).isEqualTo("Femme")
  }
}
