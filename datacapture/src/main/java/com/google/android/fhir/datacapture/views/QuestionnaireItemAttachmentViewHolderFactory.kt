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
import com.google.android.fhir.datacapture.GeneralMimeTypes
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.hasGeneralMimeType
import com.google.android.fhir.datacapture.hasGeneralMimeTypeOnly
import com.google.android.fhir.datacapture.maxSizeInB
import com.google.android.fhir.datacapture.maxSizeInMB
import com.google.android.fhir.datacapture.mimeTypes
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.attachment.CameraLauncherFragment
import com.google.android.fhir.datacapture.views.attachment.SelectFileLauncherFragment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.Date
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemAttachmentViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_attachment_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var error: TextView
      private lateinit var takePhoto: Button
      private lateinit var upload: Button
      private lateinit var delete: Button
      private lateinit var photoPreview: ImageView
      private lateinit var filePreview: ConstraintLayout
      private lateinit var iconFilePreview: ImageView
      private lateinit var titleFilePreview: TextView
      private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        error = itemView.findViewById(R.id.error)
        takePhoto = itemView.findViewById(R.id.take_photo)
        upload = itemView.findViewById(R.id.upload)
        delete = itemView.findViewById(R.id.delete)
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

        questionnaireItemViewItem.answers.firstOrNull()?.valueAttachment?.let { attachment ->
          when (attachment.contentType.getGeneralMimeType()) {
            GeneralMimeTypes.IMAGE.value -> {
              loadPhotoPreviewInBytes(attachment.data)
              clearFilePreview()
              loadDeleteButton(R.string.delete_image)
            }
            GeneralMimeTypes.DOCUMENT.value -> {
              loadFilePreview(R.drawable.file, attachment.title)
              clearPhotoPreview()
              loadDeleteButton(R.string.delete_file)
            }
            GeneralMimeTypes.VIDEO.value -> {
              loadFilePreview(R.drawable.video_file, attachment.title)
              clearPhotoPreview()
              loadDeleteButton(R.string.delete_file)
            }
            GeneralMimeTypes.AUDIO.value -> {
              loadFilePreview(R.drawable.audio_file, attachment.title)
              clearPhotoPreview()
              loadDeleteButton(R.string.delete_file)
            }
          }
        }

        if (questionnaireItem.hasGeneralMimeTypeOnly(GeneralMimeTypes.IMAGE.value)) {
          takePhoto.visibility = View.VISIBLE
          loadUploadButton(R.drawable.image_file, R.string.upload_photo)
        } else if (questionnaireItem.hasGeneralMimeType(GeneralMimeTypes.IMAGE.value)) {
          takePhoto.visibility = View.VISIBLE
          loadUploadButton(R.drawable.file, R.string.select_file)
        } else {
          takePhoto.visibility = View.GONE
          loadUploadButton(R.drawable.file, R.string.select_file)
        }

        takePhoto.setOnClickListener { onTakePhoto(questionnaireItem) }

        upload.setOnClickListener {
          context.supportFragmentManager.setFragmentResultListener(
            SelectFileLauncherFragment.RESULT_REQUEST_KEY,
            context
          ) { _, result ->
            val uri =
              (result.get(SelectFileLauncherFragment.RESULT_REQUEST_KEY)
                ?: return@setFragmentResultListener)
                as Uri

            val bytes =
              context.contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() }!!
            if (bytes.size.toBigDecimal() > questionnaireItem.maxSizeInB) {
              displayError(
                R.string.max_size_file_above_limit_validation_error_msg,
                questionnaireItem.maxSizeInMB
              )
              displaySnackbar(upload, R.string.upload_failed)
              return@setFragmentResultListener
            }

            val mimeType = context.contentResolver.getType(uri) ?: "*/*"
            if (!questionnaireItem.hasGeneralMimeType(mimeType.getGeneralMimeType())) {
              displayError(R.string.mime_type_wrong_media_format_validation_error_msg)
              displaySnackbar(upload, R.string.upload_failed)
              return@setFragmentResultListener
            }

            val fileName = getFileName(uri)
            val answer =
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value =
                  Attachment().apply {
                    contentType = mimeType
                    data = bytes
                    title = fileName
                    creation = Date()
                  }
              }
            questionnaireItemViewItem.setAnswer(answer)

            when (mimeType.getGeneralMimeType()) {
              GeneralMimeTypes.IMAGE.value -> {
                loadPhotoPreviewInUri(uri)
                clearFilePreview()
                loadDeleteButton(R.string.delete_image)
              }
              GeneralMimeTypes.DOCUMENT.value -> {
                loadFilePreview(R.drawable.file, fileName)
                clearPhotoPreview()
                loadDeleteButton(R.string.delete_file)
              }
              GeneralMimeTypes.VIDEO.value -> {
                loadFilePreview(R.drawable.video_file, fileName)
                clearPhotoPreview()
                loadDeleteButton(R.string.delete_file)
              }
              GeneralMimeTypes.AUDIO.value -> {
                loadFilePreview(R.drawable.audio_file, fileName)
                clearPhotoPreview()
                loadDeleteButton(R.string.delete_file)
              }
            }

            displaySnackbar(upload, R.string.file_uploaded)
          }

          SelectFileLauncherFragment()
            .apply { arguments = bundleOf(MIME_TYPE to questionnaireItem.mimeTypes.toTypedArray()) }
            .show(
              context.supportFragmentManager,
              QuestionnaireItemAttachmentViewHolderFactory.javaClass.simpleName
            )
        }

        delete.setOnClickListener {
          questionnaireItemViewItem.clearAnswer()
          clearDeleteButton()
          clearPhotoPreview()
          clearFilePreview()
        }
      }

      private fun onTakePhoto(questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
        val file = File.createTempFile("IMG_", ".jpeg", context.cacheDir)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        context.supportFragmentManager.setFragmentResultListener(
          CameraLauncherFragment.RESULT_REQUEST_KEY,
          context
        ) { _, result ->
          val isSaved = result.getBoolean(CameraLauncherFragment.RESULT_REQUEST_KEY)
          if (!isSaved) return@setFragmentResultListener

          if (file.length().toBigDecimal() > questionnaireItem.maxSizeInB) {
            displayError(
              R.string.max_size_image_above_limit_validation_error_msg,
              questionnaireItem.maxSizeInMB
            )
            displaySnackbar(takePhoto, R.string.upload_failed)
            file.delete()
            return@setFragmentResultListener
          }

          val mimeType = context.contentResolver.getType(uri) ?: "*/*"
          if (!questionnaireItem.hasGeneralMimeType(mimeType.getGeneralMimeType())) {
            displayError(R.string.mime_type_wrong_media_format_validation_error_msg)
            displaySnackbar(takePhoto, R.string.upload_failed)
            return@setFragmentResultListener
          }

          val bytes =
            context.contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() }
          val answer =
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                Attachment().apply {
                  contentType = mimeType
                  data = bytes
                  title = file.name
                  creation = Date()
                }
            }
          questionnaireItemViewItem.setAnswer(answer)

          loadPhotoPreviewInUri(uri)
          clearFilePreview()
          loadDeleteButton(R.string.delete_image)
          displaySnackbar(takePhoto, R.string.image_uploaded)
          file.delete()
        }

        CameraLauncherFragment()
          .apply { arguments = bundleOf(SAVED_PHOTO_URI to uri) }
          .show(
            context.supportFragmentManager,
            QuestionnaireItemAttachmentViewHolderFactory.javaClass.simpleName
          )
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        when (validationResult) {
          is NotValidated,
          Valid -> error.visibility = View.GONE
          is Invalid -> {
            error.text = validationResult.getSingleStringValidationMessage()
            error.visibility = View.VISIBLE
          }
        }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        if (isReadOnly) {
          takePhoto.isClickable = false
          takePhoto.alpha = 0.6F
          upload.isClickable = false
          upload.alpha = 0.6F
          delete.isClickable = false
          delete.alpha = 0.6F
        } else {
          takePhoto.isClickable = true
          takePhoto.alpha = 1F
          upload.isClickable = true
          upload.alpha = 1F
          delete.isClickable = true
          delete.alpha = 1F
        }
      }

      private fun loadUploadButton(@DrawableRes iconResource: Int, @StringRes textResource: Int) {
        upload.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0)
        upload.text = context.getString(textResource)
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

      private fun loadPhotoPreviewInBytes(photoBytes: ByteArray) {
        Glide.with(context).load(photoBytes).into(photoPreview)
        photoPreview.visibility = View.VISIBLE
      }

      private fun loadPhotoPreviewInUri(uri: Uri) {
        Glide.with(context).load(uri).into(photoPreview)
        photoPreview.visibility = View.VISIBLE
      }

      private fun clearPhotoPreview() {
        photoPreview.visibility = View.GONE
        Glide.with(context).clear(photoPreview)
      }

      private fun loadDeleteButton(@StringRes textResource: Int) {
        delete.visibility = View.VISIBLE
        delete.text = context.getString(textResource)
      }

      private fun clearDeleteButton() {
        delete.visibility = View.GONE
        delete.text = ""
      }

      private fun displaySnackbar(anchorView: Button, @StringRes textResource: Int) {
        Snackbar.make(anchorView, context.getString(textResource), Snackbar.LENGTH_SHORT).show()
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
        displayValidationResult(Invalid(listOf(context.getString(textResource, formatArgs))))
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

  const val MIME_TYPE = "mime_type"
  const val SAVED_PHOTO_URI = "saved_photo_uri"
}

private fun String.getGeneralMimeType(): String {
  return substringBefore("/")
}
