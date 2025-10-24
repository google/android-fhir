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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.DEFAULT_SIZE
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.hasMimeType
import com.google.android.fhir.datacapture.extensions.hasMimeTypeOnly
import com.google.android.fhir.datacapture.extensions.isGivenSizeOverLimit
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.maxSizeInBytes
import com.google.android.fhir.datacapture.extensions.maxSizeInMiBs
import com.google.android.fhir.datacapture.extensions.mimeTypes
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.attachment.CameraLauncherFragment
import com.google.android.fhir.datacapture.views.attachment.OpenDocumentLauncherFragment
import com.google.android.fhir.datacapture.views.compose.ErrorText
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.math.BigDecimal
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import timber.log.Timber

internal object AttachmentViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {
      lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var header: HeaderView
      private lateinit var errorTextView: TextView
      private lateinit var takePhotoButton: Button
      private lateinit var uploadPhotoButton: Button
      private lateinit var uploadAudioButton: Button
      private lateinit var uploadVideoButton: Button
      private lateinit var uploadDocumentButton: Button
      private lateinit var uploadFileButton: Button
      private lateinit var divider: MaterialDivider
      private lateinit var labelUploaded: TextView
      private lateinit var photoPreview: ConstraintLayout
      private lateinit var photoThumbnail: ImageView
      private lateinit var photoTitle: TextView
      private lateinit var photoDeleteButton: Button
      private lateinit var filePreview: ConstraintLayout
      private lateinit var fileIcon: ImageView
      private lateinit var fileTitle: TextView
      private lateinit var fileDeleteButton: Button
      private lateinit var context: AppCompatActivity

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
        val displayTakePhoto =
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
            if (displayTakePhoto) {
              TakePhotoButton(
                context,
                enabled = !readOnly,
                maxFileSizeLimitInBytes = questionnaireItem.maxSizeInBytes ?: DEFAULT_SIZE,
                supportedMimeType = questionnaireItem::hasMimeType,
                onFailure = { errorMessage = it },
              ) { uri, file ->
                coroutineScope.launch {
                  val attachmentMimeTypeWithSubType = context.getMimeTypeFromUri(uri)
                  val attachmentByteArray = context.readBytesFromUri(uri)
                  currentAttachment =
                    Attachment().apply {
                      contentType = attachmentMimeTypeWithSubType
                      data = attachmentByteArray
                      title = file.name
                      creation = Date()
                    }

                  val answer =
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = currentAttachment
                    }
                  questionnaireViewItem.setAnswer(answer)

                  displayUploadedText = true
                  //                      todo:
                  //                      divider.visibility = View.VISIBLE
                  //                      labelUploaded.visibility = View.VISIBLE
                  //                      displayPreview(
                  //                          attachmentType = attachmentMimeType,
                  //                          attachmentTitle = file.name,
                  //                          attachmentUri = attachmentUri,
                  //                      )
                  //                      displaySnackbarOnUpload(view, attachmentMimeType)
                  file.delete()
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
                val attachmentMimeTypeWithSubType = context.getMimeTypeFromUri(it)
                val attachmentByteArray = context.readBytesFromUri(it)
                currentAttachment =
                  Attachment().apply {
                    contentType = attachmentMimeTypeWithSubType
                    data = attachmentByteArray
                    title = getFileName(context, it)
                    creation = Date()
                  }

                val answer =
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = currentAttachment
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
                val mimeType = remember(it.contentType) { getMimeType(it.contentType) }

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
                              R.dimen
                                .attachment_preview_photo_and_preview_file_icon_background_width,
                            ),
                          )
                          .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp),
                          ),
                    ) {
                      Icon(
                        painterResource(iconRes),
                        contentDescription = stringResource(R.string.cd_file_icon_preview),
                      )
                    }
                  }
                  MimeType.IMAGE.value -> {
                    val bitmap =
                      remember(it.data) { BitmapFactory.decodeByteArray(it.data, 0, it.data.size) }
                    bitmap?.let {
                      Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.cd_photo_preview),
                        modifier =
                          Modifier.size(
                              dimensionResource(
                                R.dimen
                                  .attachment_preview_photo_and_preview_file_icon_background_width,
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
                  text = it.title,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.weight(1f),
                )
              }

              OutlinedButton(
                onClick = {
                  currentAttachment = null
                  displayUploadedText = false
                  coroutineScope.launch { questionnaireViewItem.clearAnswer() }
                },
                enabled = !readOnly,
              ) {
                Icon(
                  painter = painterResource(R.drawable.ic_delete),
                  tint = MaterialTheme.colorScheme.error,
                  contentDescription = stringResource(R.string.delete),
                )
                Spacer(modifier = Modifier)
                Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
              }
            }

            Spacer(
              modifier =
                Modifier.height(dimensionResource(R.dimen.attachment_preview_divider_margin_top)),
            )
            HorizontalDivider()
          }
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
        onSuccess: (Uri) -> Unit,
      ) {
        val bytesInMB = remember { BigDecimal(1048576) }
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
                onFailure("Error: No file selected")
                return@rememberLauncherForActivityResult
              }

              val attachmentByteArray = context.readBytesFromUri(uri)
              if (attachmentByteArray.size.toBigDecimal() > maxFileSizeLimitInBytes) {
                onFailure(maxSizeImageLimitErrorMessage)
                return@rememberLauncherForActivityResult
              }

              val attachmentMimeTypeWithSubType = context.getMimeTypeFromUri(uri)
              val attachmentMimeType = getMimeType(attachmentMimeTypeWithSubType)
              if (!supportedMimeType(attachmentMimeType)) {
                onFailure(wrongMediaFormatErrorMessage)
                return@rememberLauncherForActivityResult
              }

              onSuccess(uri)
            },
          )

        OutlinedButton(modifier = Modifier.testTag(UPLOAD_FILE_BUTTON_TAG), onClick = { openDocumentLauncher.launch(mimeTypes) }, enabled = enabled) {
          Icon(
            painterResource(uploadButtonIconResId),
            contentDescription = stringResource(uploadButtonTextResId),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(dimensionResource(R.dimen.attachment_action_button_icon_size)),
          )
          Spacer(modifier = Modifier)
          Text(stringResource(uploadButtonTextResId))
        }
      }

      fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        errorTextView = itemView.findViewById(R.id.error)
        takePhotoButton = itemView.findViewById(R.id.take_photo)
        uploadPhotoButton = itemView.findViewById(R.id.upload_photo)
        uploadAudioButton = itemView.findViewById(R.id.upload_audio)
        uploadVideoButton = itemView.findViewById(R.id.upload_video)
        uploadDocumentButton = itemView.findViewById(R.id.upload_document)
        uploadFileButton = itemView.findViewById(R.id.upload_file)
        divider = itemView.findViewById(R.id.divider)
        labelUploaded = itemView.findViewById(R.id.label_uploaded)
        photoPreview = itemView.findViewById(R.id.photo_preview)
        photoThumbnail = itemView.findViewById(R.id.photo_thumbnail)
        photoTitle = itemView.findViewById(R.id.photo_title)
        photoDeleteButton = itemView.findViewById(R.id.photo_delete)
        filePreview = itemView.findViewById(R.id.file_preview)
        fileIcon = itemView.findViewById(R.id.file_icon)
        fileTitle = itemView.findViewById(R.id.file_title)
        fileDeleteButton = itemView.findViewById(R.id.file_delete)
        context = itemView.context.tryUnwrapContext()!!
      }

      fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        header.bind(questionnaireViewItem, showRequiredOrOptionalText = true)
        val questionnaireItem = questionnaireViewItem.questionnaireItem
        displayOrClearInitialPreview()
        displayTakePhotoButton(questionnaireItem)
        displayUploadButton(questionnaireItem)
        takePhotoButton.setOnClickListener { view -> onTakePhotoClicked(view, questionnaireItem) }
        uploadPhotoButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadAudioButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadVideoButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadDocumentButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadFileButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        photoDeleteButton.setOnClickListener { view -> onDeleteClicked(view) }
        fileDeleteButton.setOnClickListener { view -> onDeleteClicked(view) }
        displayValidationResult(questionnaireViewItem.validationResult)
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        when (validationResult) {
          is NotValidated,
          Valid, -> errorTextView.visibility = View.GONE
          is Invalid -> {
            errorTextView.text = validationResult.getSingleStringValidationMessage()
            errorTextView.visibility = View.VISIBLE
          }
        }
      }

      fun setReadOnly(isReadOnly: Boolean) {
        takePhotoButton.isEnabled = !isReadOnly
        uploadPhotoButton.isEnabled = !isReadOnly
        uploadAudioButton.isEnabled = !isReadOnly
        uploadVideoButton.isEnabled = !isReadOnly
        uploadDocumentButton.isEnabled = !isReadOnly
        uploadFileButton.isEnabled = !isReadOnly
        photoDeleteButton.isEnabled = !isReadOnly
        fileDeleteButton.isEnabled = !isReadOnly
      }

      private fun displayOrClearInitialPreview() {
        val answer = questionnaireViewItem.answers.firstOrNull()

        // Clear preview if there is no answer to prevent showing old previews in views that have
        // been recycled.
        if (answer == null) {
          clearPhotoPreview()
          clearFilePreview()
          return
        }

        answer.valueAttachment?.let { attachment ->
          displayPreview(
            attachmentType = getMimeType(attachment.contentType),
            attachmentTitle = attachment.title,
            attachmentByteArray = attachment.data,
          )
        }
      }

      private fun displayTakePhotoButton(questionnaireItem: QuestionnaireItemComponent) {
        if (questionnaireItem.hasMimeType(MimeType.IMAGE.value)) {
          takePhotoButton.visibility = View.VISIBLE
        }
      }

      private fun displayUploadButton(questionnaireItem: QuestionnaireItemComponent) {
        when {
          questionnaireItem.hasMimeTypeOnly(MimeType.AUDIO.value) -> {
            uploadAudioButton.visibility = View.VISIBLE
          }
          questionnaireItem.hasMimeTypeOnly(MimeType.DOCUMENT.value) -> {
            uploadDocumentButton.visibility = View.VISIBLE
          }
          questionnaireItem.hasMimeTypeOnly(MimeType.IMAGE.value) -> {
            uploadPhotoButton.visibility = View.VISIBLE
          }
          questionnaireItem.hasMimeTypeOnly(MimeType.VIDEO.value) -> {
            uploadVideoButton.visibility = View.VISIBLE
          }
          else -> {
            uploadFileButton.visibility = View.VISIBLE
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
        onSuccess: (Uri, File) -> Unit,
      ) {
        val file = File.createTempFile("IMG_", ".jpeg", context.cacheDir)
        val attachmentUri by lazy {
          FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }

        val bytesInMB = remember { BigDecimal(1048576) }
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
              when {
                !success -> {
                  onFailure(mediaNotSavedError)
                  file.delete()
                }
                file.length().toBigDecimal() > maxFileSizeLimitInBytes -> {
                  onFailure(maxSizeImageLimitErrorMessage)
                  file.delete()
                }
                !supportedMimeType(getMimeType(context.getMimeTypeFromUri(attachmentUri))) -> {
                  onFailure(wrongMediaFormatErrorMessage)
                  file.delete()
                }
                else -> {
                  onSuccess(attachmentUri, file)
                }
              }
            },
          )

        val requestPermissionLauncher =
          rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
          ) { isGranted: Boolean ->
            if (isGranted) {
              Timber.d("Camera permission granted")
              takePictureLauncher.launch(attachmentUri)
            } else {
              Timber.d("Camera permission not granted")
              onFailure("Camera permission not granted")
            }
          }

        val launcherAction = {
          if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
              PackageManager.PERMISSION_GRANTED
          ) {
            takePictureLauncher.launch(attachmentUri)
          } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
          }
        }

        OutlinedButton(
          modifier = Modifier.testTag(TAKE_PHOTO_BUTTON_TAG),
          onClick = { launcherAction.invoke() },
          enabled = enabled,
        ) {
          val takePhotoText = stringResource(R.string.take_photo)
          Icon(
            painterResource(R.drawable.ic_camera),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = takePhotoText,
            modifier = Modifier.size(dimensionResource(R.dimen.attachment_action_button_icon_size)),
          )
          Spacer(modifier = Modifier)
          Text(takePhotoText)
        }
      }

      private fun onTakePhotoClicked(view: View, questionnaireItem: QuestionnaireItemComponent) {
        val file = File.createTempFile("IMG_", ".jpeg", context.cacheDir)
        val attachmentUri =
          FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        context.supportFragmentManager.setFragmentResultListener(
          CameraLauncherFragment.CAMERA_RESULT_KEY,
          context,
        ) { _, result ->
          val isSaved = result.getBoolean(CameraLauncherFragment.CAMERA_RESULT_KEY)
          if (!isSaved) return@setFragmentResultListener

          if (questionnaireItem.isGivenSizeOverLimit(file.length().toBigDecimal())) {
            displayError(
              R.string.max_size_image_above_limit_validation_error_msg,
              questionnaireItem.maxSizeInMiBs,
            )
            displaySnackbar(view, R.string.upload_failed)
            file.delete()
            return@setFragmentResultListener
          }

          val attachmentMimeTypeWithSubType = context.getMimeTypeFromUri(attachmentUri)
          val attachmentMimeType = getMimeType(attachmentMimeTypeWithSubType)
          if (!questionnaireItem.hasMimeType(attachmentMimeType)) {
            displayError(R.string.mime_type_wrong_media_format_validation_error_msg)
            displaySnackbar(view, R.string.upload_failed)
            file.delete()
            return@setFragmentResultListener
          }

          val attachmentByteArray = context.readBytesFromUri(attachmentUri)
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
          context.lifecycleScope.launch {
            questionnaireViewItem.setAnswer(answer)

            divider.visibility = View.VISIBLE
            labelUploaded.visibility = View.VISIBLE
            displayPreview(
              attachmentType = attachmentMimeType,
              attachmentTitle = file.name,
              attachmentUri = attachmentUri,
            )
            displaySnackbarOnUpload(view, attachmentMimeType)
            file.delete()
          }
        }

        CameraLauncherFragment()
          .apply { arguments = bundleOf(EXTRA_SAVED_PHOTO_URI_KEY to attachmentUri) }
          .show(context.supportFragmentManager, AttachmentViewHolderFactory.javaClass.simpleName)
      }

      private fun onUploadClicked(view: View, questionnaireItem: QuestionnaireItemComponent) {
        context.supportFragmentManager.setFragmentResultListener(
          OpenDocumentLauncherFragment.OPEN_DOCUMENT_RESULT_KEY,
          context,
        ) { _, result ->
          val attachmentUri =
            (result.get(OpenDocumentLauncherFragment.OPEN_DOCUMENT_RESULT_KEY)
              ?: return@setFragmentResultListener)
              as Uri

          val attachmentByteArray = context.readBytesFromUri(attachmentUri)
          if (questionnaireItem.isGivenSizeOverLimit(attachmentByteArray.size.toBigDecimal())) {
            displayError(
              R.string.max_size_file_above_limit_validation_error_msg,
              questionnaireItem.maxSizeInMiBs,
            )
            displaySnackbar(view, R.string.upload_failed)
            return@setFragmentResultListener
          }

          val attachmentMimeTypeWithSubType = context.getMimeTypeFromUri(attachmentUri)
          val attachmentMimeType = getMimeType(attachmentMimeTypeWithSubType)
          if (!questionnaireItem.hasMimeType(attachmentMimeType)) {
            displayError(R.string.mime_type_wrong_media_format_validation_error_msg)
            displaySnackbar(view, R.string.upload_failed)
            return@setFragmentResultListener
          }

          val attachmentTitle = getFileName(context, attachmentUri)
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
          context.lifecycleScope.launch {
            questionnaireViewItem.setAnswer(answer)

            divider.visibility = View.VISIBLE
            labelUploaded.visibility = View.VISIBLE
            displayPreview(
              attachmentType = attachmentMimeType,
              attachmentTitle = attachmentTitle,
              attachmentUri = attachmentUri,
            )
            displaySnackbarOnUpload(view, attachmentMimeType)
          }
        }

        OpenDocumentLauncherFragment()
          .apply {
            arguments = bundleOf(EXTRA_MIME_TYPE_KEY to questionnaireItem.mimeTypes.toTypedArray())
          }
          .show(context.supportFragmentManager, AttachmentViewHolderFactory.javaClass.simpleName)
      }

      private fun displayPreview(
        attachmentType: String,
        attachmentTitle: String,
        attachmentByteArray: ByteArray? = null,
        attachmentUri: Uri? = null,
      ) {
        when (attachmentType) {
          MimeType.AUDIO.value -> {
            loadFilePreview(R.drawable.ic_audio_file, attachmentTitle)
            clearPhotoPreview()
          }
          MimeType.DOCUMENT.value -> {
            loadFilePreview(R.drawable.ic_document_file, attachmentTitle)
            clearPhotoPreview()
          }
          MimeType.IMAGE.value -> {
            if (attachmentByteArray != null) {
              loadPhotoPreview(attachmentByteArray, attachmentTitle)
            } else if (attachmentUri != null) {
              loadPhotoPreview(attachmentUri, attachmentTitle)
            }
            clearFilePreview()
          }
          MimeType.VIDEO.value -> {
            loadFilePreview(R.drawable.ic_video_file, attachmentTitle)
            clearPhotoPreview()
          }
        }
      }

      private fun loadFilePreview(@DrawableRes iconResource: Int, title: String) {
        filePreview.visibility = View.VISIBLE
        Glide.with(context).load(iconResource).into(fileIcon)
        fileTitle.text = title
      }

      private fun clearFilePreview() {
        filePreview.visibility = View.GONE
        Glide.with(context).clear(fileIcon)
        fileTitle.text = ""
      }

      private fun loadPhotoPreview(byteArray: ByteArray, title: String) {
        photoPreview.visibility = View.VISIBLE
        Glide.with(context).load(byteArray).into(photoThumbnail)
        photoTitle.text = title
      }

      private fun loadPhotoPreview(uri: Uri, title: String) {
        photoPreview.visibility = View.VISIBLE
        Glide.with(context).load(uri).into(photoThumbnail)
        photoTitle.text = title
      }

      private fun clearPhotoPreview() {
        photoPreview.visibility = View.GONE
        Glide.with(context).clear(photoThumbnail)
        photoTitle.text = ""
      }

      private fun onDeleteClicked(view: View) {
        context.lifecycleScope.launch {
          questionnaireViewItem.clearAnswer()
          divider.visibility = View.GONE
          labelUploaded.visibility = View.GONE
          clearPhotoPreview()
          clearFilePreview()
          displaySnackbarOnDelete(
            view,
            getMimeType(questionnaireViewItem.answers.first().valueAttachment.contentType),
          )
        }
      }

      private fun displaySnackbar(view: View, @StringRes textResource: Int) {
        Snackbar.make(view, context.getString(textResource), Snackbar.LENGTH_SHORT).show()
      }

      private fun displaySnackbarOnUpload(view: View, attachmentType: String) {
        when (attachmentType) {
          MimeType.AUDIO.value -> {
            displaySnackbar(view, R.string.audio_uploaded)
          }
          MimeType.DOCUMENT.value -> {
            displaySnackbar(view, R.string.file_uploaded)
          }
          MimeType.IMAGE.value -> {
            displaySnackbar(view, R.string.image_uploaded)
          }
          MimeType.VIDEO.value -> {
            displaySnackbar(view, R.string.video_uploaded)
          }
        }
      }

      private fun displaySnackbarOnDelete(view: View, attachmentType: String) {
        when (attachmentType) {
          MimeType.AUDIO.value -> {
            displaySnackbar(view, R.string.audio_deleted)
          }
          MimeType.DOCUMENT.value -> {
            displaySnackbar(view, R.string.file_deleted)
          }
          MimeType.IMAGE.value -> {
            displaySnackbar(view, R.string.image_deleted)
          }
          MimeType.VIDEO.value -> {
            displaySnackbar(view, R.string.video_deleted)
          }
        }
      }

      private fun displayError(@StringRes textResource: Int) {
        displayValidationResult(
          Invalid(
            listOf(
              context.getString(
                textResource,
              ),
            ),
          ),
        )
      }

      private fun displayError(@StringRes textResource: Int, vararg formatArgs: Any?) {
        displayValidationResult(Invalid(listOf(context.getString(textResource, *formatArgs))))
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

/** Returns the main MIME type of a MIME type string (e.g. image/png returns image). */
private fun getMimeType(mimeType: String): String = mimeType.substringBefore("/")

private fun Context.readBytesFromUri(uri: Uri): ByteArray {
  return contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() } ?: ByteArray(0)
}

private fun Context.getMimeTypeFromUri(uri: Uri): String {
  return contentResolver.getType(uri) ?: "*/*"
}

const val TAKE_PHOTO_BUTTON_TAG = "TakePhotoButton"
const val UPLOAD_FILE_BUTTON_TAG = "UploadFileButton"
const val ATTACHMENT_MEDIA_PREVIEW_TAG = "photo_preview"
