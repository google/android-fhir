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
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.DEFAULT_SIZE
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.hasMimeType
import com.google.android.fhir.datacapture.extensions.hasMimeTypeOnly
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.maxSizeInBytes
import com.google.android.fhir.datacapture.extensions.mimeTypes
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ErrorText
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import java.io.File
import java.math.BigDecimal
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.QuestionnaireResponse
import timber.log.Timber

internal object AttachmentViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val validationResult =
          remember(questionnaireViewItem.validationResult) {
            questionnaireViewItem.validationResult
          }
        var errorMessage by
          remember(validationResult) {
            mutableStateOf((validationResult as? Invalid)?.getSingleStringValidationMessage())
          }
        val questionnaireItem =
          remember(questionnaireViewItem.questionnaireItem) {
            questionnaireViewItem.questionnaireItem
          }
        val readOnly = remember(questionnaireItem) { questionnaireItem.readOnly }
        val fileMimeTypes =
          remember(questionnaireItem) { questionnaireItem.mimeTypes.toTypedArray() }
        var currentAttachment by
          remember(questionnaireViewItem.answers) {
            mutableStateOf(questionnaireViewItem.answers.singleOrNull()?.valueAttachment)
          }
        val displayTakePhotoButton =
          remember(questionnaireItem) { questionnaireItem.hasMimeType(MimeType.IMAGE.value) }
        val uploadButtonTextResId =
          remember(questionnaireItem) {
            when {
              questionnaireItem.hasMimeTypeOnly(MimeType.AUDIO.value) -> R.string.upload_audio
              questionnaireItem.hasMimeTypeOnly(MimeType.DOCUMENT.value) -> R.string.upload_document
              questionnaireItem.hasMimeTypeOnly(MimeType.IMAGE.value) -> R.string.upload_photo
              questionnaireItem.hasMimeTypeOnly(MimeType.VIDEO.value) -> R.string.upload_video
              else -> R.string.upload_file
            }
          }
        val uploadButtonIconResId =
          remember(questionnaireItem) {
            when {
              questionnaireItem.hasMimeTypeOnly(MimeType.AUDIO.value) -> R.drawable.ic_audio_file
              questionnaireItem.hasMimeTypeOnly(MimeType.DOCUMENT.value) ->
                R.drawable.ic_document_file
              questionnaireItem.hasMimeTypeOnly(MimeType.IMAGE.value) -> R.drawable.ic_image_file
              questionnaireItem.hasMimeTypeOnly(MimeType.VIDEO.value) -> R.drawable.ic_video_file
              else -> R.drawable.ic_file
            }
          }
        var displayUploadedText by
          remember(questionnaireViewItem.questionnaireItem) { mutableStateOf(false) }

        Column(
          modifier =
            Modifier.padding(
              horizontal = dimensionResource(R.dimen.item_margin_horizontal),
              vertical = dimensionResource(R.dimen.item_margin_vertical),
            ),
        ) {
          Header(questionnaireViewItem, showRequiredOrOptionalText = true)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          errorMessage?.takeIf { it.isNotBlank() }?.let { ErrorText(it) }

          Row(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.header_margin_bottom)),
            horizontalArrangement =
              Arrangement.spacedBy(dimensionResource(R.dimen.attachment_action_button_margin_end)),
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
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = currentAttachment
                    }
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
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = it
                  }
                questionnaireViewItem.setAnswer(answer)

                displayUploadedText = true
              }
            }
          }

          if (displayUploadedText) {
            Spacer(
              modifier = Modifier.height(dimensionResource(R.dimen.attachment_divider_margin_top)),
            )
            HorizontalDivider()
            Spacer(
              modifier =
                Modifier.height(dimensionResource(R.dimen.attachment_uploaded_label_margin_top)),
            )
            Text(stringResource(R.string.uploaded), style = MaterialTheme.typography.titleSmall)
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
                Modifier.height(dimensionResource(R.dimen.attachment_preview_divider_margin_top)),
            )
            HorizontalDivider()
          }
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

  val bytesInMB = remember { BigDecimal(BYTES_IN_MB) }
  val mediaNotSavedError = stringResource(R.string.media_not_saved_validation_error_msg)
  val maxSizeImageLimitErrorMessage =
    stringResource(
      R.string.max_size_image_above_limit_validation_error_msg,
      maxFileSizeLimitInBytes.div(bytesInMB),
    )
  val wrongMediaFormatErrorMessage =
    stringResource(R.string.mime_type_wrong_media_format_validation_error_msg)

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
      ButtonDefaults.outlinedButtonColors().copy(contentColor = MaterialTheme.colorScheme.primary),
  ) {
    val takePhotoText = stringResource(R.string.take_photo)
    Icon(
      painterResource(R.drawable.ic_camera),
      tint = MaterialTheme.colorScheme.primary,
      contentDescription = takePhotoText,
      modifier = Modifier.size(dimensionResource(R.dimen.attachment_action_button_icon_size)),
    )
    Spacer(modifier = Modifier.width(BUTTON_ICON_SPACING.dp))
    Text(takePhotoText)
  }
}

@Composable
private fun UploadFileButton(
  context: Context,
  enabled: Boolean,
  uploadButtonIconResId: Int,
  uploadButtonTextResId: Int,
  mimeTypes: Array<String>,
  maxFileSizeLimitInBytes: BigDecimal,
  supportedMimeType: (String) -> Boolean,
  onFailure: (String) -> Unit,
  onSuccess: (Attachment) -> Unit,
) {
  val bytesInMB = remember { BigDecimal(BYTES_IN_MB) }
  val maxSizeImageLimitErrorMessage =
    stringResource(
      R.string.max_size_file_above_limit_validation_error_msg,
      maxFileSizeLimitInBytes.div(bytesInMB),
    )
  val wrongMediaFormatErrorMessage =
    stringResource(R.string.mime_type_wrong_media_format_validation_error_msg)

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
      ButtonDefaults.outlinedButtonColors().copy(contentColor = MaterialTheme.colorScheme.primary),
  ) {
    Icon(
      painterResource(uploadButtonIconResId),
      contentDescription = stringResource(uploadButtonTextResId),
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(dimensionResource(R.dimen.attachment_action_button_icon_size)),
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
      val mimeType = remember(attachment.contentType) { getMimeType(attachment.contentType) }

      when (mimeType) {
        MimeType.AUDIO.value,
        MimeType.DOCUMENT.value,
        MimeType.VIDEO.value, -> {
          val iconRes =
            remember(mimeType) {
              when (mimeType) {
                MimeType.AUDIO.value -> R.drawable.ic_audio_file
                MimeType.DOCUMENT.value -> R.drawable.ic_document_file
                MimeType.VIDEO.value -> R.drawable.ic_video_file
                else -> R.drawable.ic_file
              }
            }
          Box(
            modifier =
              Modifier.size(
                  dimensionResource(
                    R.dimen.attachment_preview_photo_and_preview_file_icon_background_width,
                  ),
                )
                .background(
                  color = MaterialTheme.colorScheme.primaryContainer,
                  shape = RoundedCornerShape(8.dp),
                ),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              painterResource(iconRes),
              contentDescription = stringResource(R.string.cd_file_icon_preview),
              modifier =
                Modifier.padding(dimensionResource(R.dimen.attachment_preview_file_icon_margin)),
            )
          }
        }
        MimeType.IMAGE.value -> {
          val bitmap =
            remember(attachment.data) {
              BitmapFactory.decodeByteArray(
                attachment.data,
                0,
                attachment.data.size,
              )
            }
          bitmap?.let {
            Image(
              bitmap = bitmap.asImageBitmap(),
              contentDescription = stringResource(R.string.cd_photo_preview),
              modifier =
                Modifier.size(
                    dimensionResource(
                      R.dimen.attachment_preview_photo_and_preview_file_icon_background_width,
                    ),
                  )
                  .clip(
                    RoundedCornerShape(8.dp),
                  ),
              contentScale = ContentScale.Crop,
            )
          }
        }
      }

      Text(
        text = attachment.title,
        style = MaterialTheme.typography.bodyLarge,
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
        painter = painterResource(R.drawable.ic_delete),
        tint = MaterialTheme.colorScheme.error,
        contentDescription = stringResource(R.string.delete),
      )
      Spacer(modifier = Modifier.width(BUTTON_ICON_SPACING.dp))
      Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
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
