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

import android.animation.ValueAnimator
import android.graphics.Canvas
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.barcodedetection.BarcodeGraphicBase
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.barcodedetection.BarcodeLoadingGraphic
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.GraphicOverlay
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

class BarcodeLoadingGraphicTest : BarcodeGraphicBaseTest() {

  private lateinit var barcodeLoadingGraphic: BarcodeLoadingGraphic

  override fun startVerifications(canvas: Canvas) {
    super.startVerifications(canvas)

    barcodeLoadingGraphic.draw(canvas)
    verify(canvas).drawPath(any(), eq(pathPaint))
  }

  override fun getBarcodeGraphic(graphicOverlay: GraphicOverlay): BarcodeGraphicBase {
    barcodeLoadingGraphic = BarcodeLoadingGraphic(graphicOverlay, ValueAnimator.ofFloat(0f, 1f))
    return barcodeLoadingGraphic
  }
}
