/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.cd_file_icon_preview
import android_fhir.datacapture_kmp.generated.resources.cd_photo_preview
import android_fhir.datacapture_kmp.generated.resources.delete
import android_fhir.datacapture_kmp.generated.resources.ic_audio_file
import android_fhir.datacapture_kmp.generated.resources.ic_camera
import android_fhir.datacapture_kmp.generated.resources.ic_delete
import android_fhir.datacapture_kmp.generated.resources.ic_document_file
import android_fhir.datacapture_kmp.generated.resources.ic_file
import android_fhir.datacapture_kmp.generated.resources.ic_image_file
import android_fhir.datacapture_kmp.generated.resources.ic_video_file
import android_fhir.datacapture_kmp.generated.resources.max_size_file_above_limit_validation_error_msg
import android_fhir.datacapture_kmp.generated.resources.max_size_image_above_limit_validation_error_msg
import android_fhir.datacapture_kmp.generated.resources.media_not_saved_validation_error_msg
import android_fhir.datacapture_kmp.generated.resources.mime_type_wrong_media_format_validation_error_msg
import android_fhir.datacapture_kmp.generated.resources.take_photo
import android_fhir.datacapture_kmp.generated.resources.upload_audio
import android_fhir.datacapture_kmp.generated.resources.upload_document
import android_fhir.datacapture_kmp.generated.resources.upload_file
import android_fhir.datacapture_kmp.generated.resources.upload_photo
import android_fhir.datacapture_kmp.generated.resources.upload_video
import android_fhir.datacapture_kmp.generated.resources.uploaded
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.QuestionnaireTheme.dimensions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.DEFAULT_SIZE
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.data
import com.google.android.fhir.datacapture.extensions.hasMimeType
import com.google.android.fhir.datacapture.extensions.hasMimeTypeOnly
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.maxSizeInBytes
import com.google.android.fhir.datacapture.extensions.mimeTypes
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ErrorText
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import java.io.File
import java.math.BigDecimal
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import timber.log.Timber

internal object AttachmentViewFactory : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val validationResult =
      remember(questionnaireViewItem.validationResult) { questionnaireViewItem.validationResult }
    var errorMessage by
      remember(validationResult) {
        mutableStateOf((validationResult as? Invalid)?.getSingleStringValidationMessage())
      }
    val questionnaireItem =
      remember(questionnaireViewItem.questionnaireItem) { questionnaireViewItem.questionnaireItem }
    val readOnly = remember(questionnaireItem) { questionnaireItem.readOnly?.value ?: false }
    val fileMimeTypes = remember(questionnaireItem) { questionnaireItem.mimeTypes.toTypedArray() }
    var currentAttachment by
      remember(questionnaireViewItem.answers) {
        mutableStateOf(questionnaireViewItem.answers.singleOrNull()?.value?.asAttachment()?.value)
      }
    val displayTakePhotoButton =
      remember(questionnaireItem) { questionnaireItem.hasMimeType(MimeType.IMAGE.value) }
    val uploadButtonTextResId =
      remember(questionnaireItem) {
        when {
          questionnaireItem.hasMimeTypeOnly(MimeType.AUDIO.value) -> Res.string.upload_audio
          questionnaireItem.hasMimeTypeOnly(MimeType.DOCUMENT.value) -> Res.string.upload_document
          questionnaireItem.hasMimeTypeOnly(MimeType.IMAGE.value) -> Res.string.upload_photo
          questionnaireItem.hasMimeTypeOnly(MimeType.VIDEO.value) -> Res.string.upload_video
          else -> Res.string.upload_file
        }
      }
    val uploadButtonIconResId =
      remember(questionnaireItem) {
        when {
          questionnaireItem.hasMimeTypeOnly(MimeType.AUDIO.value) -> Res.drawable.ic_audio_file
          questionnaireItem.hasMimeTypeOnly(MimeType.DOCUMENT.value) ->
            Res.drawable.ic_document_file
          questionnaireItem.hasMimeTypeOnly(MimeType.IMAGE.value) -> Res.drawable.ic_image_file
          questionnaireItem.hasMimeTypeOnly(MimeType.VIDEO.value) -> Res.drawable.ic_video_file
          else -> Res.drawable.ic_file
        }
      }
    var displayUploadedText by
      remember(questionnaireViewItem.questionnaireItem) { mutableStateOf(false) }

    Column(
      modifier =
        Modifier.padding(
          horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
          vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
        ),
    ) {
      Header(questionnaireViewItem, showRequiredOrOptionalText = true)
      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

      errorMessage?.takeIf { it.isNotBlank() }?.let { ErrorText(it) }

      Row(
        modifier = Modifier.padding(top = QuestionnaireTheme.dimensions.headerMarginBottom),
        horizontalArrangement =
          Arrangement.spacedBy(QuestionnaireTheme.dimensions.attachmentActionButtonMarginEnd),
      ) {
        if (displayTakePhotoButton) {
          TakePhotoButton(
            context,
            enabled = !readOnly,
            maxFileSizeLimitInBytes = questionnaireItem.maxSizeInBytes ?: DEFAULT_SIZE,
            supportedMimeType = questionnaireItem::hasMimeType,
            onFailure = { errorMessage = it },
          ) { attachment ->
            coroutineScope.launch {
              currentAttachment = attachment

              val answer =
                QuestionnaireResponse.Item.Answer(
                  value = QuestionnaireResponse.Item.Answer.Value.Attachment(value = attachment),
                )
              questionnaireViewItem.setAnswer(answer)

              displayUploadedText = true
            }
          }
        }

        UploadFileButton(
          context,
          enabled = !readOnly,
          uploadButtonIconResId,
          uploadButtonTextResId,
          fileMimeTypes,
          maxFileSizeLimitInBytes = questionnaireItem.maxSizeInBytes ?: DEFAULT_SIZE,
          supportedMimeType = questionnaireItem::hasMimeType,
          onFailure = { errorMessage = it },
        ) {
          coroutineScope.launch {
            currentAttachment = it

            val answer =
              QuestionnaireResponse.Item.Answer(
                value = QuestionnaireResponse.Item.Answer.Value.Attachment(value = it),
              )
            questionnaireViewItem.setAnswer(answer)

            displayUploadedText = true
          }
        }
      }

      if (displayUploadedText) {
        Spacer(
          modifier = Modifier.height(QuestionnaireTheme.dimensions.attachmentDividerMarginTop),
        )
        HorizontalDivider()
        Spacer(
          modifier =
            Modifier.height(QuestionnaireTheme.dimensions.attachmentUploadedLabelMarginTop),
        )
        Text(stringResource(Res.string.uploaded), style = QuestionnaireTheme.typography.titleSmall)
      }

      currentAttachment?.let {
        Spacer(modifier = Modifier.height(8.dp))
        AttachmentPreview(
          it,
          deleteEnabled = !readOnly,
        ) {
          currentAttachment = null
          displayUploadedText = false
          coroutineScope.launch { questionnaireViewItem.clearAnswer() }
        }

        Spacer(
          modifier =
            Modifier.height(QuestionnaireTheme.dimensions.attachmentPreviewDividerMarginTop),
        )
        HorizontalDivider()
      }
    }
  }
}

@Composable
private fun TakePhotoButton(
  context: Context,
  enabled: Boolean,
  maxFileSizeLimitInBytes: BigDecimal,
  supportedMimeType: (String) -> Boolean,
  onFailure: (String) -> Unit,
  onSuccess: (Attachment) -> Unit,
) {
  val file = remember { File.createTempFile("IMG_", ".jpeg", context.cacheDir) }
  var attachmentUri by remember(file) { mutableStateOf<Uri?>(null) }

  DisposableEffect(file) {
    onDispose {
      if (file.exists()) {
        file.delete()
      }
    }
  }

  val bytesInMB = remember { BYTES_IN_MB.toBigDecimal() }
  val mediaNotSavedError = stringResource(Res.string.media_not_saved_validation_error_msg)
  val maxSizeImageLimitErrorMessage =
    stringResource(
      Res.string.max_size_image_above_limit_validation_error_msg,
      maxFileSizeLimitInBytes.div(bytesInMB),
    )
  val wrongMediaFormatErrorMessage =
    stringResource(Res.string.mime_type_wrong_media_format_validation_error_msg)

  val takePictureLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.TakePicture(),
      onResult = { success ->
        if (!success) {
          onFailure(mediaNotSavedError)
          return@rememberLauncherForActivityResult
        }

        val uri =
          attachmentUri
            ?: run {
              onFailure(mediaNotSavedError)
              return@rememberLauncherForActivityResult
            }

        val attachment =
          createValidatedAttachmentOrReportFailure(
            context = context,
            uri = uri,
            fileName = file.name,
            maxFileSizeLimitInBytes = maxFileSizeLimitInBytes,
            supportedMimeType = supportedMimeType,
            maxSizeErrorMessage = maxSizeImageLimitErrorMessage,
            wrongMediaFormatErrorMessage = wrongMediaFormatErrorMessage,
            onFailure = onFailure,
          )
            ?: return@rememberLauncherForActivityResult

        onSuccess(attachment)
      },
    )

  val cameraPermissionDeniedError = stringResource(R.string.camera_permission_denied_error_msg)

  val requestPermissionLauncher =
    rememberLauncherForActivityResult(
      ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
      if (isGranted) {
        Timber.d("Camera permission granted")
        attachmentUri =
          FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        takePictureLauncher.launch(attachmentUri!!)
      } else {
        Timber.d("Camera permission not granted")
        onFailure(cameraPermissionDeniedError)
      }
    }

  val launcherAction = {
    if (
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED
    ) {
      attachmentUri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      takePictureLauncher.launch(attachmentUri!!)
    } else {
      requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  OutlinedButton(
    modifier = Modifier.testTag(TAKE_PHOTO_BUTTON_TAG),
    onClick = { launcherAction.invoke() },
    enabled = enabled,
    colors =
      ButtonDefaults.outlinedButtonColors()
        .copy(contentColor = QuestionnaireTheme.colorScheme.primary),
  ) {
    val takePhotoText = stringResource(Res.string.take_photo)
    Icon(
      painterResource(Res.drawable.ic_camera),
      tint = QuestionnaireTheme.colorScheme.primary,
      contentDescription = takePhotoText,
      modifier = Modifier.size(QuestionnaireTheme.dimensions.attachmentActionButtonIconSize),
    )
    Spacer(modifier = Modifier.width(BUTTON_ICON_SPACING.dp))
    Text(takePhotoText)
  }
}

@Composable
private fun UploadFileButton(
  context: Context,
  enabled: Boolean,
  uploadButtonIconResId: DrawableResource,
  uploadButtonTextResId: StringResource,
  mimeTypes: Array<String>,
  maxFileSizeLimitInBytes: BigDecimal,
  supportedMimeType: (String) -> Boolean,
  onFailure: (String) -> Unit,
  onSuccess: (Attachment) -> Unit,
) {
  val bytesInMB = remember { BYTES_IN_MB.toBigDecimal() }
  val maxSizeImageLimitErrorMessage =
    stringResource(
      Res.string.max_size_file_above_limit_validation_error_msg,
      maxFileSizeLimitInBytes.div(bytesInMB),
    )
  val wrongMediaFormatErrorMessage =
    stringResource(Res.string.mime_type_wrong_media_format_validation_error_msg)

  val openDocumentLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.OpenDocument(),
      onResult = { uri ->
        if (uri == null) {
          return@rememberLauncherForActivityResult
        }

        val attachment =
          createValidatedAttachmentOrReportFailure(
            context = context,
            uri = uri,
            fileName = getFileName(context.contentResolver, uri),
            maxFileSizeLimitInBytes = maxFileSizeLimitInBytes,
            supportedMimeType = supportedMimeType,
            maxSizeErrorMessage = maxSizeImageLimitErrorMessage,
            wrongMediaFormatErrorMessage = wrongMediaFormatErrorMessage,
            onFailure = onFailure,
          )
            ?: return@rememberLauncherForActivityResult

        onSuccess(attachment)
      },
    )

  OutlinedButton(
    modifier = Modifier.testTag(UPLOAD_FILE_BUTTON_TAG),
    onClick = { openDocumentLauncher.launch(mimeTypes) },
    enabled = enabled,
    colors =
      ButtonDefaults.outlinedButtonColors()
        .copy(contentColor = QuestionnaireTheme.colorScheme.primary),
  ) {
    Icon(
      painterResource(uploadButtonIconResId),
      contentDescription = stringResource(uploadButtonTextResId),
      tint = QuestionnaireTheme.colorScheme.primary,
      modifier = Modifier.size(QuestionnaireTheme.dimensions.attachmentActionButtonIconSize),
    )
    Spacer(modifier = Modifier.width(BUTTON_ICON_SPACING.dp))
    Text(stringResource(uploadButtonTextResId))
  }
}

@Composable
private fun AttachmentPreview(
  attachment: Attachment,
  deleteEnabled: Boolean,
  onDeleteClick: () -> Unit,
) {
  Row(
    modifier = Modifier.testTag(ATTACHMENT_MEDIA_PREVIEW_TAG).fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val mimeType =
        remember(attachment.contentType?.value) {
          attachment.contentType?.value?.let { getMimeType(it) }
        }

      when (mimeType) {
        MimeType.IMAGE.value -> {
          val imageBitmap =
            remember(attachment.data) { attachment.data?.data?.decodeToImageBitmap() }
          imageBitmap?.let {
            Image(
              bitmap = it,
              contentDescription = stringResource(Res.string.cd_photo_preview),
              modifier =
                Modifier.size(
                    QuestionnaireTheme.dimensions.attachmentPreviewPhotoWidth,
                  )
                  .clip(
                    RoundedCornerShape(8.dp),
                  ),
              contentScale = ContentScale.Crop,
            )
          }
        }
        else -> {
          val iconRes =
            remember(mimeType) {
              when (mimeType) {
                MimeType.AUDIO.value -> Res.drawable.ic_audio_file
                MimeType.DOCUMENT.value -> Res.drawable.ic_document_file
                MimeType.VIDEO.value -> Res.drawable.ic_video_file
                else -> Res.drawable.ic_file
              }
            }
          Box(
            modifier =
              Modifier.size(QuestionnaireTheme.dimensions.attachmentPreviewPhotoWidth)
                .background(
                  color = QuestionnaireTheme.colorScheme.primaryContainer,
                  shape = RoundedCornerShape(8.dp),
                ),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              painterResource(iconRes),
              contentDescription = stringResource(Res.string.cd_file_icon_preview),
              modifier =
                Modifier.padding(QuestionnaireTheme.dimensions.attachmentPreviewFileIconMargin),
            )
          }
        }
      }

      Text(
        text = attachment.title?.value ?: "",
        style = QuestionnaireTheme.textStyles.attachmentPreviewTitle,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier.weight(1f),
      )
    }

    OutlinedButton(
      onClick = { onDeleteClick.invoke() },
      enabled = deleteEnabled,
    ) {
      Icon(
        painter = painterResource(Res.drawable.ic_delete),
        tint = QuestionnaireTheme.colorScheme.error,
        contentDescription = stringResource(Res.string.delete),
      )
      Spacer(modifier = Modifier.width(BUTTON_ICON_SPACING.dp))
      Text(stringResource(Res.string.delete), color = QuestionnaireTheme.colorScheme.error)
    }
  }
}

private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
  var fileName = ""
  val columns = arrayOf(OpenableColumns.DISPLAY_NAME)
  contentResolver.query(uri, columns, null, null, null)?.use { cursor ->
    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    if (cursor.moveToFirst() && nameIndex >= 0) {
      fileName = cursor.getString(nameIndex) ?: ""
    }
  }
  return fileName
}

private fun createValidatedAttachmentOrReportFailure(
  context: Context,
  uri: Uri,
  fileName: String,
  maxFileSizeLimitInBytes: BigDecimal,
  supportedMimeType: (String) -> Boolean,
  maxSizeErrorMessage: String,
  wrongMediaFormatErrorMessage: String,
  onFailure: (String) -> Unit,
): Attachment? {
  val attachmentByteArray = context.readBytesFromUri(uri)
  if (attachmentByteArray.size.toBigDecimal() > maxFileSizeLimitInBytes) {
    onFailure(maxSizeErrorMessage)
    return null
  }

  val attachmentMimeTypeWithSubType = context.getMimeTypeFromUri(uri)
  val attachmentMimeType = getMimeType(attachmentMimeTypeWithSubType)
  if (!supportedMimeType(attachmentMimeType)) {
    onFailure(wrongMediaFormatErrorMessage)
    return null
  }

  return Attachment().apply {
    contentType = attachmentMimeTypeWithSubType
    data = attachmentByteArray
    title = fileName
    creation = Date()
  }
}

/** Returns the main MIME type of a MIME type string (e.g. image/png returns image). */
private fun getMimeType(mimeType: String): String = mimeType.substringBefore("/")

private fun Context.readBytesFromUri(uri: Uri): ByteArray {
  return contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() } ?: ByteArray(0)
}

private fun Context.getMimeTypeFromUri(uri: Uri): String {
  return contentResolver.getType(uri) ?: "*/*"
}

private const val BYTES_IN_MB = 1048576L
private const val BUTTON_ICON_SPACING = 4

const val TAKE_PHOTO_BUTTON_TAG = "TakePhotoButton"
const val UPLOAD_FILE_BUTTON_TAG = "UploadFileButton"
const val ATTACHMENT_MEDIA_PREVIEW_TAG = "photo_preview"
