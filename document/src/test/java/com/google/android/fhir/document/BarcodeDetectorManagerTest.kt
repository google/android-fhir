/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir.document

import androidx.camera.core.ImageProxy
import com.google.android.fhir.document.scan.BarcodeDetectorManager
import com.google.mlkit.vision.barcode.BarcodeScanner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BarcodeDetectorManagerTest {

  @Mock private lateinit var barcodeScanner: BarcodeScanner

  @Mock private lateinit var imageProxy: ImageProxy

  private lateinit var barcodeDetectorManager: BarcodeDetectorManager

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    barcodeDetectorManager = BarcodeDetectorManager(barcodeScanner)
  }

  @After
  fun tearDown() {
    barcodeDetectorManager.releaseBarcodeScanner()
  }

  @Test
  fun releaseBarcodeScanner() {
    barcodeDetectorManager.releaseBarcodeScanner()
    verify(barcodeScanner).close()
  }
}
