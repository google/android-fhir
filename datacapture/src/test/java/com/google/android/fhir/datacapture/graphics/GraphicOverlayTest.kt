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

package com.google.android.fhir.datacapture.graphics

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.CameraSource
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.GraphicOverlay
import com.google.android.gms.common.images.Size
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers.ClassParameter
import org.robolectric.util.ReflectionHelpers.callInstanceMethod
import org.robolectric.util.ReflectionHelpers.getField
import org.robolectric.util.ReflectionHelpers.setField

@RunWith(RobolectricTestRunner::class)
class GraphicOverlayTest {

  private val context: Application by lazy {
    spy(ApplicationProvider.getApplicationContext<Application>())
  }
  private lateinit var graphics: ArrayList<GraphicOverlay.Graphic>
  private lateinit var graphicOverlay: GraphicOverlay

  @Before
  fun setUp() {
    graphicOverlay = spy(GraphicOverlay(context, Robolectric.buildAttributeSet().build()))
    graphics = getField(graphicOverlay, "graphics")
  }

  @Test
  fun shouldClear_Graphics() {
    graphics.add(mock())
    graphicOverlay.clear()

    assertThat(graphics).hasSize(0)
    verify(graphicOverlay).postInvalidate()
  }

  @Test
  fun shouldAdd_MockGraphic() {
    graphicOverlay.add(mock())
    assertThat(graphics).hasSize(1)
  }

  @Test
  fun shouldValidate_cameraPreviewSize_inPortraitAndLandscapeModel() {
    val cameraSource: CameraSource = mock()
    val size: Size = Size.parseSize("10x20")
    `when`(cameraSource.previewSize).thenReturn(size)

    // set info on portrait mode
    graphicOverlay.setCameraInfo(cameraSource)

    assertThat(getField<Int>(graphicOverlay, "previewWidth")).isEqualTo(20)
    assertThat(getField<Int>(graphicOverlay, "previewHeight")).isEqualTo(10)

    // set info on landscape mode
    val resources: Resources = mock()
    val configuration: Configuration = mock()

    `when`(context.resources).thenReturn(resources)
    `when`(resources.configuration).thenReturn(configuration)
    configuration.orientation = Configuration.ORIENTATION_LANDSCAPE

    graphicOverlay.setCameraInfo(cameraSource)

    assertThat(getField<Int>(graphicOverlay, "previewWidth")).isEqualTo(10)
    assertThat(getField<Int>(graphicOverlay, "previewHeight")).isEqualTo(20)
  }

  @Test
  fun translateX_shouldReturn_validFloat() {
    assertThat(graphicOverlay.translateX(2f)).isEqualTo(2f)
  }

  @Test
  fun translateY_shouldReturn_validFloat() {
    assertThat(graphicOverlay.translateY(2f)).isEqualTo(2f)
  }

  @Test
  fun translateRect_shouldReturn_validRectF() {
    val rect = RectF(0f, 0f, 100f, 100f)
    assertThat(graphicOverlay.translateRect(Rect(0, 0, 100, 100))).isEqualTo(rect)
  }

  @Test
  fun onDraw_shouldCall_graphicDrawMethod() {
    `when`(graphicOverlay.width).thenReturn(100)
    `when`(graphicOverlay.height).thenReturn(100)

    setField(graphicOverlay, "previewWidth", 100)
    setField(graphicOverlay, "previewHeight", 100)

    graphics.addAll(listOf(mock(), mock(), mock(), mock(), mock()))
    val canvas: Canvas = mock()
    callInstanceMethod<Any>(graphicOverlay, "onDraw", ClassParameter(Canvas::class.java, canvas))

    graphics.forEach { verify(it).draw(eq(canvas)) }
  }
}
