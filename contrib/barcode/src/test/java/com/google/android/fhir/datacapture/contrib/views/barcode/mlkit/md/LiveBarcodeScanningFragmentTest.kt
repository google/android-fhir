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

import android.hardware.Camera
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.testing.launchFragment
import com.google.android.fhir.datacapture.contrib.views.barcode.R
import com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera.CameraSource
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers.getField
import org.robolectric.util.ReflectionHelpers.setField

@RunWith(RobolectricTestRunner::class)
class LiveBarcodeScanningFragmentTest {

  private lateinit var liveBarcodeScanningFragment: LiveBarcodeScanningFragment
  private lateinit var fragmentActivity: FragmentActivity
  private lateinit var cameraSource: CameraSource

  @Before
  fun setUp() {
    mockUtils()
    cameraSource = mock()
    launchFragment<LiveBarcodeScanningFragment>(
        themeResId = com.google.android.fhir.datacapture.R.style.Theme_Questionnaire
      )
      .onFragment {
        liveBarcodeScanningFragment = spy(it)
        fragmentActivity = mock()
        setField(liveBarcodeScanningFragment, "cameraSource", cameraSource)
        `when`(liveBarcodeScanningFragment.requireActivity()).thenReturn(fragmentActivity)
      }
  }

  @Test
  fun shouldVerify_allScenarios_inOnClickMethod() {

    val view = mock<View>()

    // verify close button scenario
    val fragmentManager = mock<FragmentManager>()
    val fragmentTransaction = mock<FragmentTransaction>()

    `when`(fragmentActivity.supportFragmentManager).thenReturn(fragmentManager)
    `when`(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction)
    `when`(fragmentTransaction.remove(any())).thenReturn(fragmentTransaction)
    `when`(view.id).thenReturn(R.id.close_button)

    liveBarcodeScanningFragment.onClick(view)

    verify(fragmentTransaction, times(1)).commit()

    // verify camera flash button mode off scenario
    val flashButton = getField<View>(liveBarcodeScanningFragment, "flashButton")
    flashButton.isSelected = true

    `when`(view.id).thenReturn(R.id.flash_button)
    liveBarcodeScanningFragment.onClick(view)

    verify(cameraSource, times(1)).updateFlashMode(eq(Camera.Parameters.FLASH_MODE_OFF))

    // verify camera flash button mode torch scenario
    liveBarcodeScanningFragment.onClick(view)
    verify(cameraSource, times(1)).updateFlashMode(eq(Camera.Parameters.FLASH_MODE_TORCH))
  }

  private fun mockUtils() {
    val utils = mock<Utils>()

    `when`(utils.getBarcodeScanningClient()).thenReturn(mock())

    val field = Utils::class.java.getField("INSTANCE")
    field.isAccessible = true
    val modifiersField = Field::class.java.getDeclaredField("modifiers")
    modifiersField.isAccessible = true
    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
    field.set(null, utils)
  }
}
