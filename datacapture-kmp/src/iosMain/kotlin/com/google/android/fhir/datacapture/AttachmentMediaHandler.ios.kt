/*
 * Copyright 2026 Google LLC
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

package com.google.android.fhir.datacapture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.reinterpret
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraCaptureMode
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerImageURL
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

internal class IosMediaHandler(
  override val maxSupportedFileSizeBytes: BigDecimal,
  override val supportedMimeTypes: Array<String>,
) : MediaHandler {

  override suspend fun capturePhoto(): MediaCaptureResult {
    val isCameraPermissionGranted = requestCameraPermission()
    if (!isCameraPermissionGranted) {
      return MediaCaptureResult.Error("Error: Camera permission not granted")
    }
    val (name, photoByteArray) = takeCameraPhoto()
    return captureResult(photoByteArray, "image/jpeg", titleName = name)
  }

  override suspend fun selectFile(inputMimeTypes: Array<String>): MediaCaptureResult {
    val imageOnly = inputMimeTypes.all { it.startsWith("image/") }
    return when {
      imageOnly -> {
        val isPhotoAccessGranted = requestPhotoLibraryAccess()
        if (!isPhotoAccessGranted) {
          MediaCaptureResult.Error("Error: PhotoLibrary permission not granted")
        } else {
          val (name, photoByteArray) = pickPhoto()
          captureResult(photoByteArray, "image/jpeg", titleName = name)
        }
      }
      else -> {
        val (lastPathName, fileByteArray) = pickFile(inputMimeTypes)
        captureResult(
          fileByteArray,
          "application/octet-stream",
          titleName = lastPathName.substringBeforeLast("."),
        )
      }
    }
  }

  override fun isCameraAvailable(): Boolean =
    UIImagePickerController.isSourceTypeAvailable(
      UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera,
    )

  private suspend fun requestCameraPermission(): Boolean =
    suspendCancellableCoroutine { continuation ->
      val currentAuthorizedStatus =
        AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
      when (currentAuthorizedStatus) {
        AVAuthorizationStatusAuthorized -> continuation.resumeWith(Result.success(true)) // granted
        AVAuthorizationStatusNotDetermined -> {
          AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
            continuation.resumeWith(Result.success(isGranted))
          }
        }
        AVAuthorizationStatusDenied -> continuation.resumeWith(Result.success(false))
        else -> {
          Logger.e("unknown camera permission status $currentAuthorizedStatus")
          continuation.resumeWith(Result.success(false))
        }
      }
    }

  private suspend fun requestPhotoLibraryAccess(): Boolean =
    suspendCancellableCoroutine { continuation ->
      fun checkAccessStatus(authorizationStatus: PHAuthorizationStatus) {
        when (authorizationStatus) {
          PHAuthorizationStatusAuthorized -> continuation.resumeWith(Result.success(true))
          PHAuthorizationStatusNotDetermined -> {
            PHPhotoLibrary.requestAuthorization { newStatus -> checkAccessStatus(newStatus) }
          }
          PHAuthorizationStatusDenied -> continuation.resumeWith(Result.success(false))
          else -> {
            Logger.e("unknown photo library permission status $authorizationStatus")
            continuation.resumeWith(Result.success(false))
          }
        }
      }

      val currentAccessStatus = PHPhotoLibrary.authorizationStatus()
      checkAccessStatus(currentAccessStatus)
    }

  private suspend fun requestFilePickerAccess(): Boolean =
    suspendCancellableCoroutine { continuation ->
    }

  @OptIn(ExperimentalUuidApi::class)
  private suspend fun takeCameraPhoto(): Pair<String, ByteArray> =
    suspendCancellableCoroutine { continuation ->
      val cameraDelegate =
        object :
          NSObject(),
          UIImagePickerControllerDelegateProtocol,
          UINavigationControllerDelegateProtocol {
          override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>,
          ) {
            val image =
              didFinishPickingMediaWithInfo.getValue(UIImagePickerControllerEditedImage) as? UIImage
                ?: didFinishPickingMediaWithInfo.getValue(
                  UIImagePickerControllerOriginalImage,
                ) as? UIImage

            picker.dismissViewControllerAnimated(true, null)

            image?.toJPEGByteArray()?.let {
              val name = "IMG_${Uuid.random().toHexString()}.jpeg"
              continuation.resumeWith(Result.success(name to it))
            }
              ?: continuation.cancel()
          }

          override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
            continuation.cancel()
          }
        }

      val imagePicker = UIImagePickerController()
      imagePicker.setSourceType(
        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera,
      )
      imagePicker.setAllowsEditing(true)
      imagePicker.setCameraCaptureMode(
        UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto,
      )
      imagePicker.setDelegate(cameraDelegate)
      UIApplication.sharedApplication.keyWindow
        ?.rootViewController
        ?.presentViewController(
          imagePicker,
          true,
          null,
        )
    }

  @OptIn(ExperimentalUuidApi::class)
  private suspend fun pickPhoto(): Pair<String, ByteArray> =
    suspendCancellableCoroutine { continuation ->
      val galleryDelegate =
        object :
          NSObject(),
          UIImagePickerControllerDelegateProtocol,
          UINavigationControllerDelegateProtocol {
          override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>,
          ) {
            val image =
              didFinishPickingMediaWithInfo.getValue(
                UIImagePickerControllerEditedImage,
              ) as? UIImage
                ?: didFinishPickingMediaWithInfo.getValue(
                  UIImagePickerControllerOriginalImage,
                ) as? UIImage
            val imageURL =
              didFinishPickingMediaWithInfo.getValue(
                UIImagePickerControllerImageURL,
              ) as? NSURL
            val fileName =
              imageURL?.lastPathComponent?.substringBeforeLast(".")
                ?: "IMG_${Uuid.random().toHexString()}"

            picker.dismissViewControllerAnimated(true, null)
            image?.toJPEGByteArray()?.let {
              continuation.resumeWith(Result.success("$fileName.jpeg" to it))
            }
              ?: continuation.cancel()
          }

          override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
            continuation.cancel()
          }
        }

      val imagePicker = UIImagePickerController()
      imagePicker.setSourceType(
        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary,
      )
      imagePicker.setAllowsEditing(true)
      imagePicker.setDelegate(galleryDelegate)
      UIApplication.sharedApplication.keyWindow
        ?.rootViewController
        ?.presentViewController(
          imagePicker,
          true,
          null,
        )
    }

  @OptIn(ExperimentalUuidApi::class)
  private suspend fun pickFile(mimeTypes: Array<String>): Pair<String, ByteArray> =
    suspendCancellableCoroutine { continuation ->
      val fileDocumentDelegate =
        object : NSObject(), UIDocumentPickerDelegateProtocol {
          override fun documentPicker(
            controller: UIDocumentPickerViewController,
            didPickDocumentAtURL: NSURL,
          ) {
            didPickDocumentAtURL.startAccessingSecurityScopedResource()
            val data = NSData.dataWithContentsOfURL(didPickDocumentAtURL)
            val fileName = didPickDocumentAtURL.lastPathComponent ?: "selected_file"
            didPickDocumentAtURL.stopAccessingSecurityScopedResource()

            controller.dismissViewControllerAnimated(true, null)
            data?.toByteArray()?.let { continuation.resumeWith(Result.success(fileName to it)) }
              ?: continuation.cancel()
          }

          override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
            controller.dismissViewControllerAnimated(true, null)
            continuation.cancel()
          }
        }

      val documentPicker =
        UIDocumentPickerViewController(
          documentTypes = listOf("public.item"),
          inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen,
        )

      //        val picker = UIDocumentPickerViewController(
      //            forOpeningContentTypes = listOf(UTTypePDF), // Example: PDF
      //            asCopy = true
      //        )

      documentPicker.setDelegate(fileDocumentDelegate)
      UIApplication.sharedApplication.keyWindow
        ?.rootViewController
        ?.presentViewController(documentPicker, true, null)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.toJPEGByteArray(): ByteArray {
  val compressionQuality = 0.99
  val imageData =
    UIImageJPEGRepresentation(this, compressionQuality)
      ?: throw IllegalArgumentException("image data is null")
  val bytes = imageData.bytes ?: throw IllegalArgumentException("image bytes is null")
  val length = imageData.length

  val data: CPointer<ByteVar> = bytes.reinterpret()

  return ByteArray(length.toInt()) { index -> data[index] }
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray {
  val bytes = bytes?.reinterpret<ByteVar>()
  val length = length.toInt()
  return ByteArray(length) { index -> bytes!![index] }
}

@Composable
internal actual fun getMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler {
  return remember(maxSupportedFileSizeBytes, supportedMimeTypes) {
    IosMediaHandler(maxSupportedFileSizeBytes, supportedMimeTypes)
  }
}
