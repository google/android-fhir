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
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.decodeToBitmap
import com.google.android.fhir.datacapture.extensions.fetchBitmapFromUrl
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Questionnaire
import timber.log.Timber

class MediaView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

  init {
    LayoutInflater.from(context).inflate(R.layout.media_view, this, true)
  }

  private var imageAttachment: ImageView = findViewById(R.id.image_attachment)

  fun bind(questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
    clearImage()
    val attachment = questionnaireItem.itemMedia ?: return
    val attachmentMimeType = getMimeType(attachment.contentType)
    when {
      attachment.hasData() -> {
        when (attachmentMimeType) {
          MimeType.IMAGE.value -> {
            val image = attachment.decodeToBitmap() ?: return
            loadImage(image)
            loadImageContentDescription(attachment.title)
          }
          MimeType.AUDIO.value -> {
            Timber.w("Audio attachment from data is not supported in Item Media extension yet")
          }
          MimeType.VIDEO.value -> {
            Timber.w("Video attachment from data not supported in Item Media extension yet")
          }
        }
      }
      attachment.hasUrl() -> {
        when (attachmentMimeType) {
          MimeType.IMAGE.value -> {
            val context = context.tryUnwrapContext()!!
            context.lifecycleScope.launch(Dispatchers.Main) {
              val image = attachment.fetchBitmapFromUrl(context) ?: return@launch
              loadImage(image)
              loadImageContentDescription(attachment.title)
            }
          }
          MimeType.AUDIO.value -> {
            Timber.w("Audio attachment from url is not supported in Item Media extension yet")
          }
          MimeType.VIDEO.value -> {
            Timber.w("Video attachment from url not supported in Item Media extension yet")
          }
        }
      }
      else -> return
    }
  }

  private fun loadImageContentDescription(contentDescription: String?) {
    if (contentDescription != null) imageAttachment.contentDescription = contentDescription
  }

  private fun loadImage(image: Bitmap) {
    Glide.with(context).load(image).into(imageAttachment)
    imageAttachment.visibility = View.VISIBLE
  }

  private fun clearImage() {
    Glide.with(context).clear(imageAttachment)
    imageAttachment.visibility = View.GONE
  }

  /** Returns the main MIME type of a MIME type string (e.g. image/png returns image). */
  private fun getMimeType(mimeType: String): String = mimeType.substringBefore("/")
}
