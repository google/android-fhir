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

import android.animation.ValueAnimator
import androidx.annotation.MainThread
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.InputInfo
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.Utils.getBarcodeScanningClient
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.CameraReticleAnimator
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.FrameProcessorBase
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.GraphicOverlay
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.WorkflowModel
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.WorkflowModel.WorkflowState
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.settings.PreferenceUtils
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import timber.log.Timber

/** A processor to run the barcode detector. */
class BarcodeProcessor(graphicOverlay: GraphicOverlay, private val workflowModel: WorkflowModel) :
  FrameProcessorBase<List<Barcode>>() {

  private val scanner = getBarcodeScanningClient()
  private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)

  override fun detectInImage(image: InputImage): Task<List<Barcode>> = scanner.process(image)

  @MainThread
  override fun onSuccess(
    inputInfo: InputInfo,
    results: List<Barcode>,
    graphicOverlay: GraphicOverlay,
  ) {
    if (!workflowModel.isCameraLive) return

    Timber.d("Barcode result size: ${results.size}")

    // Picks the barcode, if exists, that covers the center of graphic overlay.

    val barcodeInCenter =
      results.firstOrNull { barcode ->
        val boundingBox = barcode.boundingBox ?: return@firstOrNull false
        val box = graphicOverlay.translateRect(boundingBox)
        box.contains(graphicOverlay.width / 2f, graphicOverlay.height / 2f)
      }

    graphicOverlay.clear()
    if (barcodeInCenter == null) {
      cameraReticleAnimator.start()
      graphicOverlay.add(BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator))
      workflowModel.setWorkflowState(WorkflowState.DETECTING)
    } else {
      cameraReticleAnimator.cancel()
      val sizeProgress =
        PreferenceUtils.getProgressToMeetBarcodeSizeRequirement(graphicOverlay, barcodeInCenter)
      if (sizeProgress < 1) {
        // Barcode in the camera view is too small, so prompt user to move camera closer.
        graphicOverlay.add(BarcodeConfirmingGraphic(graphicOverlay, barcodeInCenter))
        workflowModel.setWorkflowState(WorkflowState.CONFIRMING)
      } else {
        // Barcode size in the camera view is sufficient.
        if (PreferenceUtils.shouldDelayLoadingBarcodeResult(graphicOverlay.context)) {
          val loadingAnimator = createLoadingAnimator(graphicOverlay, barcodeInCenter)
          loadingAnimator.start()
          graphicOverlay.add(BarcodeLoadingGraphic(graphicOverlay, loadingAnimator))
          workflowModel.setWorkflowState(WorkflowState.SEARCHING)
        } else {
          workflowModel.setWorkflowState(WorkflowState.DETECTED)
          workflowModel.detectedBarcode.setValue(barcodeInCenter)
        }
      }
    }
    graphicOverlay.invalidate()
  }

  private fun createLoadingAnimator(
    graphicOverlay: GraphicOverlay,
    barcode: Barcode,
  ): ValueAnimator {
    val endProgress = 1.1f
    return ValueAnimator.ofFloat(0f, endProgress).apply {
      duration = 2000
      addUpdateListener {
        if ((animatedValue as Float).compareTo(endProgress) >= 0) {
          graphicOverlay.clear()
          workflowModel.setWorkflowState(WorkflowState.SEARCHED)
          workflowModel.detectedBarcode.setValue(barcode)
        } else {
          graphicOverlay.invalidate()
        }
      }
    }
  }

  override fun onFailure(e: Exception) {
    Timber.e("Barcode detection failed!", e)
  }

  override fun stop() {
    super.stop()
    try {
      scanner.close()
    } catch (e: IOException) {
      Timber.e("Failed to close barcode detector!", e)
    }
  }
}
