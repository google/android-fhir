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

import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.FrameMetadata
import com.google.common.truth.Truth.assertThat
import java.nio.ByteBuffer
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InputInfoTest {

  @Test
  fun cameraInput_toBitmap() {
    val byteBuffer = ByteBuffer.allocate(24)
    val frameMetaData = FrameMetadata(10, 10, 0)
    val inputInfo = CameraInputInfo(byteBuffer, frameMetaData)
    assertThat(inputInfo.getBitmap()).isNotNull()
  }
}
