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

package com.google.android.fhir.datacapture.views

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.google.android.fhir.datacapture.MimeType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.hasMimeType
import com.google.android.fhir.datacapture.hasMimeTypeOnly
import com.google.android.fhir.datacapture.isGivenSizeOverLimit
import com.google.android.fhir.datacapture.maxSizeInMiBs
import com.google.android.fhir.datacapture.mimeTypes
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.attachment.CameraLauncherFragment
import com.google.android.fhir.datacapture.views.attachment.OpenDocumentLauncherFragment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.Date
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemAttachmentViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_attachment_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var errorTextView: TextView
      private lateinit var takePhotoButton: Button
      private lateinit var uploadPhotoButton: Button
      private lateinit var uploadAudioButton: Button
      private lateinit var uploadVideoButton: Button
      private lateinit var uploadDocumentButton: Button
      private lateinit var uploadFileButton: Button
      private lateinit var deleteButton: Button
      private lateinit var photoPreview: ImageView
      private lateinit var filePreview: LinearLayout
      private lateinit var iconFilePreview: ImageView
      private lateinit var titleFilePreview: TextView
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
        deleteButton = itemView.findViewById(R.id.delete)
        photoPreview = itemView.findViewById(R.id.photo_preview)
        filePreview = itemView.findViewById(R.id.file_preview)
        iconFilePreview = itemView.findViewById(R.id.icon_file_preview)
        titleFilePreview = itemView.findViewById(R.id.title_file_preview)
        context = itemView.context.tryUnwrapContext()!!
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        val questionnaireItem = questionnaireItemViewItem.questionnaireItem
        header.bind(questionnaireItem)
        displayInitialPreview()
        displayTakePhotoButton(questionnaireItem)
        displayUploadButton(questionnaireItem)
        takePhotoButton.setOnClickListener { view -> onTakePhotoClicked(view, questionnaireItem) }
        uploadPhotoButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadAudioButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadVideoButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadDocumentButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        uploadFileButton.setOnClickListener { view -> onUploadClicked(view, questionnaireItem) }
        deleteButton.setOnClickListener { view -> onDeleteClicked(view) }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
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
        deleteButton.isEnabled = !isReadOnly
      }

      private fun displayInitialPreview() {
        questionnaireItemViewItem.answers.firstOrNull()?.valueAttachment?.let { attachment ->
          displayPreview(
            attachmentType = attachment.contentType.type,
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

          val attachmentMimeType = context.getMimeTypeFromUri(attachmentUri)
          if (!questionnaireItem.hasMimeType(attachmentMimeType.type)) {
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
                  contentType = attachmentMimeType
                  data = attachmentByteArray
                  title = file.name
                  creation = Date()
                }
            }
          questionnaireItemViewItem.setAnswer(answer)

          loadPhotoPreview(attachmentUri)
          clearFilePreview()
          displayDeleteButton()
          displaySnackbarOnUpload(view, attachmentMimeType.type)
          file.delete()
        }

        CameraLauncherFragment()
          .apply { arguments = bundleOf(EXTRA_SAVED_PHOTO_URI_KEY to attachmentUri) }
          .show(
            context.supportFragmentManager,
            QuestionnaireItemAttachmentViewHolderFactory.javaClass.simpleName
          )
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

          val attachmentMimeType = context.getMimeTypeFromUri(attachmentUri)
          if (!questionnaireItem.hasMimeType(attachmentMimeType.type)) {
            displayError(R.string.mime_type_wrong_media_format_validation_error_msg)
            displaySnackbar(view, R.string.upload_failed)
            return@setFragmentResultListener
          }

          val attachmentTitle = getFileName(attachmentUri)
          val answer =
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  contentType = attachmentMimeType
                  data = attachmentByteArray
                  title = attachmentTitle
                  creation = Date()
                }
            }
          questionnaireItemViewItem.setAnswer(answer)

          displayPreview(
            attachmentType = attachmentMimeType.type,
            attachmentTitle = attachmentTitle,
            attachmentUri = attachmentUri
          )
          displaySnackbarOnUpload(view, attachmentMimeType.type)
        }

        OpenDocumentLauncherFragment()
          .apply {
            arguments = bundleOf(EXTRA_MIME_TYPE_KEY to questionnaireItem.mimeTypes.toTypedArray())
          }
          .show(
            context.supportFragmentManager,
            QuestionnaireItemAttachmentViewHolderFactory.javaClass.simpleName
          )
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
              loadPhotoPreview(attachmentByteArray)
            } else if (attachmentUri != null) {
              loadPhotoPreview(attachmentUri)
            }
            clearFilePreview()
          }
          MimeType.VIDEO.value -> {
            loadFilePreview(R.drawable.ic_video_file, attachmentTitle)
            clearPhotoPreview()
          }
        }
        displayDeleteButton()
      }

      private fun loadFilePreview(@DrawableRes iconResource: Int, title: String) {
        Glide.with(context).load(iconResource).into(iconFilePreview)
        titleFilePreview.text = title
        filePreview.visibility = View.VISIBLE
      }

      private fun clearFilePreview() {
        filePreview.visibility = View.GONE
        Glide.with(context).clear(iconFilePreview)
        titleFilePreview.text = ""
      }

      private fun loadPhotoPreview(byteArray: ByteArray) {
        Glide.with(context).load(byteArray).into(photoPreview)
        photoPreview.visibility = View.VISIBLE
      }

      private fun loadPhotoPreview(uri: Uri) {
        Glide.with(context).load(uri).into(photoPreview)
        photoPreview.visibility = View.VISIBLE
      }

      private fun clearPhotoPreview() {
        photoPreview.visibility = View.GONE
        Glide.with(context).clear(photoPreview)
      }

      private fun displayDeleteButton() {
        deleteButton.visibility = View.VISIBLE
      }

      private fun hideDeleteButton() {
        deleteButton.visibility = View.GONE
      }

      private fun onDeleteClicked(view: View) {
        questionnaireItemViewItem.clearAnswer()
        hideDeleteButton()
        clearPhotoPreview()
        clearFilePreview()
        displaySnackbarOnDelete(
          view,
          questionnaireItemViewItem.answers.first().valueAttachment.contentType.type
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

/** Only usable for a String known as mime type. */
private val String.type: String
  get() = this.substringBefore("/")

private fun Context.readBytesFromUri(uri: Uri): ByteArray {
  return contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() } ?: ByteArray(0)
}

private fun Context.getMimeTypeFromUri(uri: Uri): String {
  return contentResolver.getType(uri) ?: "*/*"
}
