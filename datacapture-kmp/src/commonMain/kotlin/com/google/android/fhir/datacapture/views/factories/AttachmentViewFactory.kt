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
import android_fhir.datacapture_kmp.generated.resources.take_photo
import android_fhir.datacapture_kmp.generated.resources.upload_audio
import android_fhir.datacapture_kmp.generated.resources.upload_document
import android_fhir.datacapture_kmp.generated.resources.upload_file
import android_fhir.datacapture_kmp.generated.resources.upload_photo
import android_fhir.datacapture_kmp.generated.resources.upload_video
import android_fhir.datacapture_kmp.generated.resources.uploaded
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.MediaCaptureResult
import com.google.android.fhir.datacapture.MediaHandler
import com.google.android.fhir.datacapture.extensions.DEFAULT_SIZE
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.data
import com.google.android.fhir.datacapture.extensions.hasMimeType
import com.google.android.fhir.datacapture.extensions.hasMimeTypeOnly
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.maxSizeInBytes
import com.google.android.fhir.datacapture.extensions.mimeTypes
import com.google.android.fhir.datacapture.rememberMediaHandler
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ErrorText
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal object AttachmentViewFactory : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
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
    val maxSupportedFileSizeBytes =
      remember(questionnaireItem) { questionnaireItem.maxSizeInBytes ?: DEFAULT_SIZE }
    val attachmentMediaHandler = rememberMediaHandler(maxSupportedFileSizeBytes, fileMimeTypes)
    val displayTakePhotoButton =
      remember(questionnaireItem) { questionnaireItem.hasMimeType(MimeType.IMAGE.value) }
    val isCameraAvailable =
      remember(attachmentMediaHandler) { attachmentMediaHandler.isCameraSupported() }
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
            enabled = isCameraAvailable && !readOnly,
            mediaHandler = attachmentMediaHandler,
            onFailure = { errorMessage = it },
          ) { attachment ->
            coroutineScope.launch {
              currentAttachment = attachment
              errorMessage = null

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
          enabled = !readOnly,
          mediaHandler = attachmentMediaHandler,
          uploadButtonIconResId = uploadButtonIconResId,
          uploadButtonTextResId = uploadButtonTextResId,
          mimeTypes = fileMimeTypes,
          onFailure = { errorMessage = it },
        ) {
          coroutineScope.launch {
            currentAttachment = it
            errorMessage = null

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
  enabled: Boolean,
  mediaHandler: MediaHandler,
  onFailure: (String) -> Unit,
  onSuccess: (Attachment) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(false) }

  OutlinedButton(
    modifier = Modifier.testTag(TAKE_PHOTO_BUTTON_TAG),
    onClick = {
      if (!isLoading) {
        isLoading = true
        coroutineScope
          .launch {
            when (val result = mediaHandler.capturePhoto()) {
              is MediaCaptureResult.Success -> onSuccess(result.attachment)
              is MediaCaptureResult.Error -> onFailure(result.error)
            }
          }
          .invokeOnCompletion { isLoading = false }
      }
    },
    enabled = enabled && !isLoading,
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
    Text(if (isLoading) "Loading..." else takePhotoText)
  }
}

@Composable
private fun UploadFileButton(
  enabled: Boolean,
  mediaHandler: MediaHandler,
  uploadButtonIconResId: DrawableResource,
  uploadButtonTextResId: StringResource,
  mimeTypes: Array<String>,
  onFailure: (String) -> Unit,
  onSuccess: (Attachment) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(false) }

  OutlinedButton(
    modifier = Modifier.testTag(UPLOAD_FILE_BUTTON_TAG),
    onClick = {
      if (!isLoading) {
        isLoading = true
        coroutineScope
          .launch {
            when (val result = mediaHandler.selectFile(mimeTypes)) {
              is MediaCaptureResult.Success -> onSuccess(result.attachment)
              is MediaCaptureResult.Error -> onFailure(result.error)
            }
          }
          .invokeOnCompletion { isLoading = false }
      }
    },
    enabled = enabled && !isLoading,
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
    Text(if (isLoading) "Loading..." else stringResource(uploadButtonTextResId))
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
          attachment.contentType?.value?.substringBefore("/")
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

private const val BYTES_IN_MB = 1048576L
private const val BUTTON_ICON_SPACING = 4

const val TAKE_PHOTO_BUTTON_TAG = "TakePhotoButton"
const val UPLOAD_FILE_BUTTON_TAG = "UploadFileButton"
const val ATTACHMENT_MEDIA_PREVIEW_TAG = "photo_preview"
