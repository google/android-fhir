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

import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import androidx.camera.core.ImageProxy.PlaneProxy
import com.google.android.fhir.document.scan.BarcodeDetectorManager
import com.google.mlkit.common.sdkinternal.MlKitContext
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
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
  fun `test the barcode scanner can be correctly closed`() {
    barcodeDetectorManager.releaseBarcodeScanner()
    verify(barcodeScanner).close()
  }

  @Test
  fun `test processImage with detected barcode`() {
    // Arrange
    val mockBarcode = mock(Barcode::class.java)
    val mockCallback: (Barcode?) -> Unit = mock()
    val mockPlane = mock(PlaneProxy::class.java)

    // Mocking the behavior of plane.buffer
    val mockByteBuffer = mock(ByteBuffer::class.java)
    `when`(mockPlane.buffer).thenReturn(mockByteBuffer)

    // Setting up the capacity of the ByteBuffer
    `when`(mockByteBuffer.capacity()).thenReturn(3)

    `when`(imageProxy.planes).thenReturn(arrayOf(mockPlane))
    `when`(imageProxy.width).thenReturn(640)
    `when`(imageProxy.height).thenReturn(480)
    `when`(imageProxy.imageInfo).thenReturn(mock(ImageInfo::class.java))
    `when`(imageProxy.imageInfo.rotationDegrees).thenReturn(0)
    val mockMLKit = mock(MlKitContext::class.java)
    // `when`(MlKitContext.getInstance()).thenReturn(mockMLKit)

    // Mocking the behavior of plane.buffer.get
    `when`((mockByteBuffer as ByteBuffer).get(any<ByteArray>())).then {
      val byteArrayArgument = it.getArgument<ByteArray>(0)
      System.arraycopy(byteArrayOf(1, 2, 3), 0, byteArrayArgument, 0, byteArrayArgument.size)
      mock(ByteBuffer::class.java)
    }
    `when`(barcodeScanner.process(any())).thenAnswer {
      val listener = it.getArgument<(List<Barcode>) -> Unit>(0)
      listener.invoke(listOf(mockBarcode))
      mock(BarcodeScanner::class.java)
    }

    // Act
    barcodeDetectorManager.processImage(imageProxy, mockCallback)

    // Assert
    verify(mockCallback, times(1)).invoke(mockBarcode)
  }

  @Test
  fun `test processImage without detected barcode`() {
    val mockCallback: (Barcode?) -> Unit = mock()
    val mockPlane = mock(PlaneProxy::class.java)

    `when`(imageProxy.planes).thenReturn(arrayOf(mockPlane))
    `when`(imageProxy.width).thenReturn(640)
    `when`(imageProxy.height).thenReturn(480)
    `when`(imageProxy.imageInfo).thenReturn(mock(ImageInfo::class.java))
    `when`(imageProxy.imageInfo.rotationDegrees).thenReturn(0)

    val mockByteBuffer = mock(ByteBuffer::class.java)
    `when`(mockPlane.buffer).thenReturn(mockByteBuffer)
    `when`(mockByteBuffer.capacity()).thenReturn(3)

    `when`(barcodeScanner.process(any())).thenAnswer {
      val listener = it.getArgument<(List<Barcode>) -> Unit>(0)
      listener.invoke(emptyList())
      mock(BarcodeScanner::class.java)
    }

    // Act
    barcodeDetectorManager.processImage(imageProxy, mockCallback)

    // Assert
    verify(mockCallback, times(1)).invoke(null)
  }
}

fun mockCallback(result: Barcode?) {
  // This is where you can handle the result of the processImage function in your test
  println("Mock Callback Result: $result")
}
