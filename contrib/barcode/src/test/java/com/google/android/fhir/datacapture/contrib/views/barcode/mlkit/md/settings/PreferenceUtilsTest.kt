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

package com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.settings

import android.app.Application
import android.graphics.Rect
import android.os.Build
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.contrib.views.barcode.R
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.GraphicOverlay
import com.google.android.gms.common.images.Size
import com.google.common.truth.Truth.assertThat
import com.google.mlkit.vision.barcode.Barcode
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class PreferenceUtilsTest {

  private val context by lazy { ApplicationProvider.getApplicationContext<Application>() }

  @Test
  fun verify_savePreferences_keyValue() {
    PreferenceUtils.saveStringPreference(
      context,
      R.string.pref_key_rear_camera_preview_size,
      "result",
    )
    val result =
      PreferenceManager.getDefaultSharedPreferences(context)
        .getString(context.getString(R.string.pref_key_rear_camera_preview_size), null)
    assertThat(result).isEqualTo("result")
  }

  @Test
  fun getProgressToMeetBarcodeSizeRequirement_shouldReturn_calculatedValue() {
    val graphicOverride = mock<GraphicOverlay> { on { context } doReturn context }
    val value = PreferenceUtils.getProgressToMeetBarcodeSizeRequirement(graphicOverride, mock())
    assertThat(value).isEqualTo(1f)
  }

  @Test
  fun getProgressToMeetBarcodeSizeRequirement_shouldReturn_defaultValue() {
    val graphicOverride =
      mock<GraphicOverlay> {
        on { context } doReturn context
        on { width } doReturn 100
        on { height } doReturn 100
      }

    val barcode = mock<Barcode> { on { boundingBox } doReturn Rect(0, 0, 100, 100) }

    saveBarcodeSizeCheck()

    val value = PreferenceUtils.getProgressToMeetBarcodeSizeRequirement(graphicOverride, mock())
    assertThat(value).isEqualTo(0f)
  }

  @Test
  fun shouldDelayLoadingBarcodeResult_returnTrue_ifKeyNotExists() {
    assertThat(PreferenceUtils.shouldDelayLoadingBarcodeResult(context)).isTrue()
  }

  @Test
  fun getUserSpecifiedPreviewSize_shouldReturn_nonNull_cameraSizePair() {
    val previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size)
    val pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size)
    PreferenceManager.getDefaultSharedPreferences(context)
      .edit()
      .putString(previewSizePrefKey, "3x3")
      .putString(pictureSizePrefKey, "5x5")
      .apply()

    val cameraSizePair = PreferenceUtils.getUserSpecifiedPreviewSize(context)

    assertThat(cameraSizePair).isNotNull()
    assertThat(cameraSizePair?.preview).isEqualTo(Size.parseSize("3x3"))
    assertThat(cameraSizePair?.picture).isEqualTo(Size.parseSize("5x5"))
  }

  private fun saveBarcodeSizeCheck() {
    PreferenceManager.getDefaultSharedPreferences(context)
      .edit()
      .putBoolean(context.getString(R.string.pref_key_enable_barcode_size_check), true)
      .apply()
  }
}
