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
import androidx.camera.core.ImageProxy.PlaneProxy
import com.google.android.fhir.document.scan.BarcodeDetectorManager
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import java.nio.ByteBuffer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class BarcodeDetectorManagerTest {

  @Mock private lateinit var barcodeScanner: BarcodeScanner

  @Mock private lateinit var imageProxy: ImageProxy
  @Mock private lateinit var planeProxy: PlaneProxy

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
  fun `test the barcode scanner can be correctly closed`() {
    barcodeDetectorManager.releaseBarcodeScanner()
    verify(barcodeScanner).close()
  }
  @Test
  fun `test processImage`() {
    // Initialize the necessary objects before calling processImage
    val mockPlaneProxy = mock(PlaneProxy::class.java)
    val planes = arrayOf(mockPlaneProxy)
    `when`(imageProxy.planes).thenReturn(planes)
    `when`(any<Array<PlaneProxy>>()[0]).thenReturn(mockPlaneProxy)
    `when`(mockPlaneProxy.buffer).thenReturn(ByteBuffer.allocate(3))
    `when`(mockPlaneProxy.buffer.capacity()).thenReturn(3)
    `when`(ByteArray(7)).thenReturn(ByteArray(7))

    // Call the processImage method with the initialized objects
    barcodeDetectorManager.processImage(imageProxy, ::mockCallback)

    // Verify that barcodeScanner.close() is called
    verify(barcodeScanner).close()
  }
}

fun mockCallback(result: Barcode?) {
  // This is where you can handle the result of the processImage function in your test
  println("Mock Callback Result: $result")
}
