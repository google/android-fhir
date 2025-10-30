/*
 * Copyright 2022-2025 Google LLC
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

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.extensions.hasMimeType
import com.google.android.fhir.datacapture.extensions.hasMimeTypeOnly
import com.google.android.fhir.datacapture.extensions.isGivenSizeOverLimit
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.maxSizeInMiBs
import com.google.android.fhir.datacapture.extensions.mimeTypes
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.attachment.CameraLauncherFragment
import com.google.android.fhir.datacapture.views.attachment.OpenDocumentLauncherFragment
import com.google.android.fhir.datacapture.views.compose.ErrorText
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import java.io.File
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object AttachmentViewHolderFactoryCompose : QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val activity = remember { context.tryUnwrapContext()!! }
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val questionnaireItem = questionnaireViewItem.questionnaireItem

        var attachmentState by
          remember(questionnaireViewItem.answers) {
            mutableStateOf(
              questionnaireViewItem.answers.firstOrNull()?.valueAttachment?.let { attachment ->
                AttachmentState(
                  type = getMimeType(attachment.contentType),
                  title = attachment.title,
                  data = attachment.data,
                )
              },
            )
          }

        var errorMessage by
          remember(questionnaireViewItem.validationResult) {
            mutableStateOf(
              getValidationErrorMessage(
                context,
                questionnaireViewItem,
                questionnaireViewItem.validationResult,
              ),
            )
          }

        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(
                horizontal = dimensionResource(R.dimen.item_margin_horizontal),
                vertical = dimensionResource(R.dimen.item_margin_vertical),
              ),
        ) {
          Header(questionnaireViewItem, showRequiredOrOptionalText = true)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          // Action buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            if (questionnaireItem.hasMimeType(MimeType.IMAGE.value)) {
              OutlinedButton(
                onClick = {
                  onTakePhotoClicked(
                    activity,
                    questionnaireItem,
                    questionnaireViewItem,
                    onSuccess = { type, title, data ->
                      attachmentState = AttachmentState(type, title, data)
                      errorMessage = null
                    },
                    onError = { msg -> errorMessage = msg },
                  )
                },
                enabled = !questionnaireItem.readOnly,
                modifier = Modifier.weight(1f),
              ) {
                Text(stringResource(R.string.take_photo))
              }
            }

            OutlinedButton(
              onClick = {
                onUploadClicked(
                  activity,
                  questionnaireItem,
                  questionnaireViewItem,
                  onSuccess = { type, title, data ->
                    attachmentState = AttachmentState(type, title, data)
                    errorMessage = null
                  },
                  onError = { msg -> errorMessage = msg },
                )
              },
              enabled = !questionnaireItem.readOnly,
              modifier = Modifier.weight(1f),
            ) {
              Text(getUploadButtonText(questionnaireItem, context))
            }
          }

          // Error message
          errorMessage?.let { error ->
            if (error.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              ErrorText(error)
            }
          }

          // Preview section
          attachmentState?.let { state ->
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = stringResource(R.string.uploaded),
              style = MaterialTheme.typography.labelMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AttachmentPreview(
              attachmentState = state,
              onDelete = {
                coroutineScope.launch {
                  questionnaireViewItem.clearAnswer()
                  attachmentState = null
                }
              },
              enabled = !questionnaireItem.readOnly,
            )
          }
        }
      }

      private fun getUploadButtonText(
        questionnaireItem: QuestionnaireItemComponent,
        context: Context,
      ): String {
        return when {
          questionnaireItem.hasMimeTypeOnly(MimeType.AUDIO.value) ->
            context.getString(R.string.upload_audio)
          questionnaireItem.hasMimeTypeOnly(MimeType.DOCUMENT.value) ->
            context.getString(R.string.upload_document)
          questionnaireItem.hasMimeTypeOnly(MimeType.IMAGE.value) ->
            context.getString(R.string.upload_photo)
          questionnaireItem.hasMimeTypeOnly(MimeType.VIDEO.value) ->
            context.getString(R.string.upload_video)
          else -> context.getString(R.string.upload_file)
        }
      }

      private fun onTakePhotoClicked(
        activity: AppCompatActivity,
        questionnaireItem: QuestionnaireItemComponent,
        questionnaireViewItem: QuestionnaireViewItem,
        onSuccess: (String, String, ByteArray) -> Unit,
        onError: (String) -> Unit,
      ) {
        val file = File.createTempFile("IMG_", ".jpeg", activity.cacheDir)
        val attachmentUri =
          FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)

        activity.supportFragmentManager.setFragmentResultListener(
          CameraLauncherFragment.CAMERA_RESULT_KEY,
          activity,
        ) { _, result ->
          val isSaved = result.getBoolean(CameraLauncherFragment.CAMERA_RESULT_KEY)
          if (!isSaved) return@setFragmentResultListener

          if (questionnaireItem.isGivenSizeOverLimit(file.length().toBigDecimal())) {
            onError(
              activity.getString(
                R.string.max_size_image_above_limit_validation_error_msg,
                questionnaireItem.maxSizeInMiBs,
              ),
            )
            file.delete()
            return@setFragmentResultListener
          }

          val attachmentMimeTypeWithSubType = activity.getMimeTypeFromUri(attachmentUri)
          val attachmentMimeType = getMimeType(attachmentMimeTypeWithSubType)
          if (!questionnaireItem.hasMimeType(attachmentMimeType)) {
            onError(activity.getString(R.string.mime_type_wrong_media_format_validation_error_msg))
            file.delete()
            return@setFragmentResultListener
          }

          val attachmentByteArray = activity.readBytesFromUri(attachmentUri)
          val answer =
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  contentType = attachmentMimeTypeWithSubType
                  data = attachmentByteArray
                  title = file.name
                  creation = Date()
                }
            }
          activity.lifecycleScope.launch {
            questionnaireViewItem.setAnswer(answer)
            onSuccess(attachmentMimeType, file.name, attachmentByteArray)
            file.delete()
          }
        }

        CameraLauncherFragment()
          .apply { arguments = bundleOf(EXTRA_SAVED_PHOTO_URI_KEY to attachmentUri) }
          .show(activity.supportFragmentManager, AttachmentViewHolderFactory.javaClass.simpleName)
      }

      private fun onUploadClicked(
        activity: AppCompatActivity,
        questionnaireItem: QuestionnaireItemComponent,
        questionnaireViewItem: QuestionnaireViewItem,
        onSuccess: (String, String, ByteArray) -> Unit,
        onError: (String) -> Unit,
      ) {
        activity.supportFragmentManager.setFragmentResultListener(
          OpenDocumentLauncherFragment.OPEN_DOCUMENT_RESULT_KEY,
          activity,
        ) { _, result ->
          val attachmentUri =
            (result.get(OpenDocumentLauncherFragment.OPEN_DOCUMENT_RESULT_KEY)
              ?: return@setFragmentResultListener)
              as Uri

          val attachmentByteArray = activity.readBytesFromUri(attachmentUri)
          if (questionnaireItem.isGivenSizeOverLimit(attachmentByteArray.size.toBigDecimal())) {
            onError(
              activity.getString(
                R.string.max_size_file_above_limit_validation_error_msg,
                questionnaireItem.maxSizeInMiBs,
              ),
            )
            return@setFragmentResultListener
          }

          val attachmentMimeTypeWithSubType = activity.getMimeTypeFromUri(attachmentUri)
          val attachmentMimeType = getMimeType(attachmentMimeTypeWithSubType)
          if (!questionnaireItem.hasMimeType(attachmentMimeType)) {
            onError(activity.getString(R.string.mime_type_wrong_media_format_validation_error_msg))
            return@setFragmentResultListener
          }

          val attachmentTitle = getFileName(activity, attachmentUri)
          val answer =
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  contentType = attachmentMimeTypeWithSubType
                  data = attachmentByteArray
                  title = attachmentTitle
                  creation = Date()
                }
            }
          activity.lifecycleScope.launch {
            questionnaireViewItem.setAnswer(answer)
            onSuccess(attachmentMimeType, attachmentTitle, attachmentByteArray)
          }
        }

        OpenDocumentLauncherFragment()
          .apply {
            arguments = bundleOf(EXTRA_MIME_TYPE_KEY to questionnaireItem.mimeTypes.toTypedArray())
          }
          .show(activity.supportFragmentManager, AttachmentViewHolderFactory.javaClass.simpleName)
      }

      private fun getFileName(context: Context, uri: Uri): String {
        var fileName = ""
        val columns = arrayOf(OpenableColumns.DISPLAY_NAME)
        context.contentResolver.query(uri, columns, null, null, null)?.use { cursor ->
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          cursor.moveToFirst()
          fileName = cursor.getString(nameIndex)
        }
        return fileName
      }
    }

  const val EXTRA_MIME_TYPE_KEY = "mime_type"
  const val EXTRA_SAVED_PHOTO_URI_KEY = "saved_photo_uri"
}

@Composable
private fun AttachmentPreview(
  attachmentState: AttachmentState,
  onDelete: () -> Unit,
  enabled: Boolean,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        when (attachmentState.type) {
          MimeType.IMAGE.value -> {
            attachmentState.data?.let { data ->
              val bitmap = remember(data) { BitmapFactory.decodeByteArray(data, 0, data.size) }
              bitmap?.let {
                Image(
                  bitmap = it.asImageBitmap(),
                  contentDescription = attachmentState.title,
                  modifier = Modifier.size(64.dp),
                  contentScale = ContentScale.Crop,
                )
              }
            }
          }
          MimeType.AUDIO.value -> {
            Icon(
              painter = painterResource(R.drawable.ic_audio_file),
              contentDescription = null,
              modifier = Modifier.size(48.dp),
            )
          }
          MimeType.VIDEO.value -> {
            Icon(
              painter = painterResource(R.drawable.ic_video_file),
              contentDescription = null,
              modifier = Modifier.size(48.dp),
            )
          }
          MimeType.DOCUMENT.value -> {
            Icon(
              painter = painterResource(R.drawable.ic_document_file),
              contentDescription = null,
              modifier = Modifier.size(48.dp),
            )
          }
        }

        Text(
          text = attachmentState.title,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.weight(1f),
        )
      }

      IconButton(onClick = onDelete, enabled = enabled) {
        Icon(
          painter = painterResource(android.R.drawable.ic_menu_delete),
          contentDescription = stringResource(R.string.delete),
          tint = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}

private data class AttachmentState(
  val type: String,
  val title: String,
  val data: ByteArray?,
)

/** Returns the main MIME type of a MIME type string (e.g. image/png returns image). */
private fun getMimeType(mimeType: String): String = mimeType.substringBefore("/")

private fun Context.readBytesFromUri(uri: Uri): ByteArray {
  return contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() } ?: ByteArray(0)
}

private fun Context.getMimeTypeFromUri(uri: Uri): String {
  return contentResolver.getType(uri) ?: "*/*"
}
