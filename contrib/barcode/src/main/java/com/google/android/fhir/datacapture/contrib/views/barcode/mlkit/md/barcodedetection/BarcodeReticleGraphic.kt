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

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.google.android.fhir.datacapture.contrib.views.barcode.R
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.CameraReticleAnimator
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.GraphicOverlay

/**
 * A camera reticle that locates at the center of canvas to indicate the system is active but has
 * not detected a barcode yet.
 */
internal class BarcodeReticleGraphic(
  overlay: GraphicOverlay,
  private val animator: CameraReticleAnimator,
) : BarcodeGraphicBase(overlay) {

  private val ripplePaint: Paint
  private val rippleSizeOffset: Int
  private val rippleStrokeWidth: Int
  private val rippleAlpha: Int

  init {
    val resources = overlay.resources
    ripplePaint = Paint()
    ripplePaint.style = Style.STROKE
    ripplePaint.color = ContextCompat.getColor(context, R.color.reticle_ripple)
    rippleSizeOffset = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_size_offset)
    rippleStrokeWidth =
      resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_stroke_width)
    rippleAlpha = ripplePaint.alpha
  }

  override fun draw(canvas: Canvas) {
    super.draw(canvas)
    // Draws the ripple to simulate the breathing animation effect.
    ripplePaint.alpha = (rippleAlpha * animator.rippleAlphaScale).toInt()
    ripplePaint.strokeWidth = rippleStrokeWidth * animator.rippleStrokeWidthScale
    val offset = rippleSizeOffset * animator.rippleSizeScale
    val rippleRect =
      RectF(
        boxRect.left - offset,
        boxRect.top - offset,
        boxRect.right + offset,
        boxRect.bottom + offset,
      )
    canvas.drawRoundRect(rippleRect, boxCornerRadius, boxCornerRadius, ripplePaint)
  }
}
