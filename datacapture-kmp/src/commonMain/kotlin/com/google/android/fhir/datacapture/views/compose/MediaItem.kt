/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.datacapture.views.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.extensions.MimeType
import com.google.android.fhir.datacapture.extensions.decodeToBitmap
import com.google.android.fhir.datacapture.extensions.fetchBitmapFromUrl
import com.google.android.fhir.datacapture.extensions.mimeType
import com.google.fhir.model.r4b.Attachment

@Composable
fun MediaItem(attachment: Attachment) {
  if (attachment.mimeType != MimeType.IMAGE.value) {
    // TODO support other media files. Log with Nappier or Kermit
    /*Timber.w(
      "${attachment.mimeType?.capitalize(Locale.ROOT)} attachment is not supported in Item Media extension yet",
    )*/
    return
  }

  ImageMediaItem(attachment)
}

@Composable
fun ImageMediaItem(imageAttachment: Attachment) {
  var bitmap by remember { mutableStateOf<Bitmap?>(null) }
  val context = LocalContext.current

  LaunchedEffect(imageAttachment) {
    bitmap =
      if (imageAttachment.data != null) {
        imageAttachment.decodeToBitmap()
      } else if (imageAttachment.url != null) {
        imageAttachment.fetchBitmapFromUrl(context)
      } else {
        null
      }
  }

  bitmap?.let {
    Image(
      modifier =
        Modifier.testTag("media-image")
          .padding(
            horizontal = 16.dp,
            vertical = 8.dp,
          )
          .sizeIn(
            maxHeight = 200.dp,
            maxWidth = 200.dp,
          ),
      bitmap = it.asImageBitmap(),
      contentDescription = imageAttachment.title?.value,
    )
  }
}
