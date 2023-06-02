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

package com.google.android.fhir.datacapture.views.factories

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.hasMimeType
import com.google.android.fhir.datacapture.extensions.hasMimeTypeOnly
import com.google.android.fhir.datacapture.extensions.isGivenSizeOverLimit
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
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.Date
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object AttachmentViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.attachment_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
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

      override fun init(itemView: View) {
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

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        header.bind(questionnaireViewItem)
        header.showRequiredOrOptionalTextInHeaderView(questionnaireViewItem)
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
          Valid -> errorTextView.visibility = View.GONE
          is Invalid -> {
            errorTextView.text = validationResult.getSingleStringValidationMessage()
            errorTextView.visibility = View.VISIBLE
          }
        }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
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
            attachmentByteArray = attachment.data
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

      private fun onTakePhotoClicked(view: View, questionnaireItem: QuestionnaireItemComponent) {
        val file = File.createTempFile("IMG_", ".jpeg", context.cacheDir)
        val attachmentUri =
          FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        context.supportFragmentManager.setFragmentResultListener(
          CameraLauncherFragment.CAMERA_RESULT_KEY,
          context
        ) { _, result ->
          val isSaved = result.getBoolean(CameraLauncherFragment.CAMERA_RESULT_KEY)
          if (!isSaved) return@setFragmentResultListener

          if (questionnaireItem.isGivenSizeOverLimit(file.length().toBigDecimal())) {
            displayError(
              R.string.max_size_image_above_limit_validation_error_msg,
              questionnaireItem.maxSizeInMiBs
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
          questionnaireViewItem.setAnswer(answer)

          divider.visibility = View.VISIBLE
          labelUploaded.visibility = View.VISIBLE
          displayPreview(
            attachmentType = attachmentMimeType,
            attachmentTitle = file.name,
            attachmentUri = attachmentUri
          )
          displaySnackbarOnUpload(view, attachmentMimeType)
          file.delete()
        }

        CameraLauncherFragment()
          .apply { arguments = bundleOf(EXTRA_SAVED_PHOTO_URI_KEY to attachmentUri) }
          .show(context.supportFragmentManager, AttachmentViewHolderFactory.javaClass.simpleName)
      }

      private fun onUploadClicked(view: View, questionnaireItem: QuestionnaireItemComponent) {
        context.supportFragmentManager.setFragmentResultListener(
          OpenDocumentLauncherFragment.OPEN_DOCUMENT_RESULT_KEY,
          context
        ) { _, result ->
          val attachmentUri =
            (result.get(OpenDocumentLauncherFragment.OPEN_DOCUMENT_RESULT_KEY)
              ?: return@setFragmentResultListener)
              as Uri

          val attachmentByteArray = context.readBytesFromUri(attachmentUri)
          if (questionnaireItem.isGivenSizeOverLimit(attachmentByteArray.size.toBigDecimal())) {
            displayError(
              R.string.max_size_file_above_limit_validation_error_msg,
              questionnaireItem.maxSizeInMiBs
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

          val attachmentTitle = getFileName(attachmentUri)
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
          questionnaireViewItem.setAnswer(answer)

          divider.visibility = View.VISIBLE
          labelUploaded.visibility = View.VISIBLE
          displayPreview(
            attachmentType = attachmentMimeType,
            attachmentTitle = attachmentTitle,
            attachmentUri = attachmentUri
          )
          displaySnackbarOnUpload(view, attachmentMimeType)
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
        attachmentUri: Uri? = null
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
        questionnaireViewItem.clearAnswer()
        divider.visibility = View.GONE
        labelUploaded.visibility = View.GONE
        clearPhotoPreview()
        clearFilePreview()
        displaySnackbarOnDelete(
          view,
          getMimeType(questionnaireViewItem.answers.first().valueAttachment.contentType)
        )
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
              )
            )
          )
        )
      }

      private fun displayError(@StringRes textResource: Int, vararg formatArgs: Any?) {
        displayValidationResult(Invalid(listOf(context.getString(textResource, *formatArgs))))
      }

      private fun getFileName(uri: Uri): String {
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
