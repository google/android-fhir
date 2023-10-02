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

package com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.barcodedetection

import android.app.Application
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.GraphicOverlay
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.settings.PreferenceUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers.getField

@RunWith(RobolectricTestRunner::class)
abstract class BarcodeGraphicBaseTest {

  private lateinit var barcodeGraphic: BarcodeGraphicBase
  protected var boxCornerRadius: Float = 0f
  protected lateinit var pathPaint: Paint
  protected lateinit var boxRect: RectF
  protected lateinit var prefsUtils: PreferenceUtils

  @Test
  fun verify_canvasDrawing() {
    mockPrefsUtils()

    val context = ApplicationProvider.getApplicationContext<Application>()
    val attrs = Robolectric.buildAttributeSet()
    val graphicOverlay = GraphicOverlay(context, attrs.build())

    barcodeGraphic = getBarcodeGraphic(graphicOverlay)

    boxCornerRadius = getField(barcodeGraphic, "boxCornerRadius")
    pathPaint = getField(barcodeGraphic, "pathPaint")
    boxRect = getField(barcodeGraphic, "boxRect")

    val canvas: Canvas = mock {
      on { width } doReturn 100
      on { height } doReturn 100
    }

    startVerifications(canvas)

    val scrimPaint = getField<Paint>(barcodeGraphic, "scrimPaint")

    verify(canvas).drawRect(eq(0f), eq(0f), eq(100f), eq(100f), eq(scrimPaint))
    verify(canvas, atLeast(3))
      .drawRoundRect(eq(boxRect), eq(boxCornerRadius), eq(boxCornerRadius), any())
  }

  protected open fun startVerifications(canvas: Canvas) {}

  inline fun <reified T : Any> argumentCaptor(): ArgumentCaptor<T> =
    ArgumentCaptor.forClass(T::class.java)

  private fun mockPrefsUtils() {
    prefsUtils = mock() { on { getBarcodeReticleBox(any()) } doReturn RectF(0f, 0f, 100f, 100f) }

    val field = PreferenceUtils::class.java.getField("INSTANCE")
    field.isAccessible = true
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
    field.set(null, prefsUtils)
  }

  internal abstract fun getBarcodeGraphic(graphicOverlay: GraphicOverlay): BarcodeGraphicBase
}
