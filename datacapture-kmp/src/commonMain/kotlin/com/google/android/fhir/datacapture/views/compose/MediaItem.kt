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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.extensions.imageData
import com.google.fhir.model.r4.Attachment
import org.jetbrains.compose.resources.decodeToImageBitmap

@Composable
fun MediaItem(attachment: Attachment) {
  var attachmentBitmap: ImageBitmap? by remember(attachment) { mutableStateOf(null) }
  LaunchedEffect(attachmentBitmap) {
    attachmentBitmap = attachment.imageData()?.decodeToImageBitmap()
  }
  attachmentBitmap?.let { ImageMediaItem(it, attachment.title?.value) }
}

@Composable
fun ImageMediaItem(imageBitmap: ImageBitmap, imageName: String?) {
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
    bitmap = imageBitmap,
    contentDescription = imageName,
  )
}
