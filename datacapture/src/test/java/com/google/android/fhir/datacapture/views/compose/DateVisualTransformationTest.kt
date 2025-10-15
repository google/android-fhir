/*
 * Copyright 2023-2025 Google LLC
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

package com.google.android.fhir.datacapture.views.compose

import androidx.compose.ui.text.AnnotatedString
import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test

class DateVisualTransformationTest {

  private val transformation =
    DateVisualTransformation(DateInputFormat("dd/MM/yyyy", delimiter = '/'))
  private val noDelimiterTransformation =
    DateVisualTransformation(DateInputFormat("ddMMyyyy", delimiter = '/'))

  @Test
  fun `filter should return empty annotated string when text is empty`() {
    val result = transformation.filter(AnnotatedString(""))
    assertThat(result.text.text).isEmpty()
  }

  @Test
  fun `filter should return empty annotated string when text is empty for input format with no delimiter`() {
    val result = noDelimiterTransformation.filter(AnnotatedString(""))
    assertThat(result.text.text).isEmpty()
  }

  @Test
  fun `filter should format partial date with day`() {
    val result = transformation.filter(AnnotatedString("12"))
    assertThat(result.text.text).isEqualTo("12/")
  }

  @Test
  fun `filter should format partial date with day for input format with no delimiter`() {
    val result = noDelimiterTransformation.filter(AnnotatedString("12"))
    assertThat(result.text.text).isEqualTo("12")
  }

  @Test
  fun `filter should format partial date with day and month`() {
    val result = transformation.filter(AnnotatedString("2812"))
    assertThat(result.text.text).isEqualTo("28/12/")
  }

  @Test
  fun `filter should format partial date with day and month for input format with no delimiter`() {
    val result = noDelimiterTransformation.filter(AnnotatedString("2812"))
    assertThat(result.text.text).isEqualTo("2812")
  }

  @Test
  fun `filter should format full date`() {
    val result = transformation.filter(AnnotatedString("28122023"))
    assertThat(result.text.text).isEqualTo("28/12/2023")
  }

  @Test
  fun `filter should format full date for input format with no delimiter`() {
    val result = noDelimiterTransformation.filter(AnnotatedString("28122023"))
    assertThat(result.text.text).isEqualTo("28122023")
  }

  @Test
  fun `filter should truncate and format date longer than 8 characters`() {
    val result = transformation.filter(AnnotatedString("311220231"))
    assertThat(result.text.text).isEqualTo("31/12/2023")
  }

  @Test
  fun `filter should truncate and format date longer than 8 characters for input format with no delimiter`() {
    val result = noDelimiterTransformation.filter(AnnotatedString("311220231"))
    assertThat(result.text.text).isEqualTo("31122023")
  }

  @Test
  fun testOriginalToTransformedMapping() {
    val originalText = AnnotatedString("28122023")
    val transformedText = transformation.filter(originalText)
    val offsetMapping = transformedText.offsetMapping

    assertEquals(0, offsetMapping.originalToTransformed(0))
    assertEquals(4, offsetMapping.originalToTransformed(3))
    assertEquals(5, offsetMapping.originalToTransformed(4))
    assertEquals(8, offsetMapping.originalToTransformed(6))
    assertEquals(10, offsetMapping.originalToTransformed(8))
  }

  @Test
  fun testTransformedToOriginalMapping() {
    val originalText = AnnotatedString("28122023")
    val transformedText = transformation.filter(originalText)
    val offsetMapping = transformedText.offsetMapping

    assertEquals(0, offsetMapping.transformedToOriginal(0))
    assertEquals(2, offsetMapping.transformedToOriginal(3))
    assertEquals(3, offsetMapping.transformedToOriginal(4))
    assertEquals(5, offsetMapping.transformedToOriginal(7))
    assertEquals(6, offsetMapping.transformedToOriginal(8))
    assertEquals(8, offsetMapping.transformedToOriginal(10))
  }
}
