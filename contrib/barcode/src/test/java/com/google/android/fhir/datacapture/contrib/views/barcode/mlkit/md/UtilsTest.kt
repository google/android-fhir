/*
 * Copyright 2022 Google LLC
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

import android.app.Application
import android.content.Context
import android.hardware.Camera
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O])
class UtilsTest {

  val context: Context by lazy { spy(ApplicationProvider.getApplicationContext<Application>()) }

  @Test
  fun isPortraitMode_shouldReturnTrue() {
    assertThat(Utils.isPortraitMode(context)).isTrue()
  }

  @Test
  fun generateValidPreviewSizeList_shouldReturn_oneCameraSizePair() {
    val camera: Camera = mock()
    val parameters: Camera.Parameters = mock()
    val supportedPreviewSize: Camera.Size = mock()
    val supportedPictureSize: Camera.Size = mock()

    `when`(camera.parameters).thenReturn(parameters)
    `when`(parameters.supportedPreviewSizes).thenReturn(mutableListOf(supportedPreviewSize))
    `when`(parameters.supportedPictureSizes).thenReturn(mutableListOf(supportedPictureSize))
    supportedPreviewSize.width = 3
    supportedPreviewSize.height = 3
    supportedPictureSize.width = 5
    supportedPictureSize.height = 5

    val result = Utils.generateValidPreviewSizeList(camera)
    assertThat(result).hasSize(1)
    assertThat(result[0].preview.width).isEqualTo(3)
    assertThat(result[0].preview.height).isEqualTo(3)
    assertThat(result[0].picture?.width).isEqualTo(5)
    assertThat(result[0].picture?.height).isEqualTo(5)
  }
}
