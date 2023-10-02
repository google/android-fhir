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

package com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md

import android.graphics.Bitmap
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.FrameMetadata
import java.nio.ByteBuffer

interface InputInfo {
  fun getBitmap(): Bitmap
}

class CameraInputInfo(
  private val frameByteBuffer: ByteBuffer,
  private val frameMetadata: FrameMetadata,
) : InputInfo {

  private var bitmap: Bitmap? = null

  @Synchronized
  override fun getBitmap(): Bitmap {
    return bitmap
      ?: let {
        bitmap =
          Utils.convertToBitmap(
            frameByteBuffer,
            frameMetadata.width,
            frameMetadata.height,
            frameMetadata.rotation,
          )
        bitmap!!
      }
  }
}

class BitmapInputInfo(private val bitmap: Bitmap) : InputInfo {
  override fun getBitmap(): Bitmap {
    return bitmap
  }
}
