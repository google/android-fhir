/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.reference

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.datacapture.AttachmentResolver
import com.google.android.fhir.reference.api.HapiFhirService
import org.hl7.fhir.r4.model.Binary

class ReferenceAttachmentResolver(val context: Context) : AttachmentResolver {

  override suspend fun resolveBinaryResource(uri: String): Binary? {
    return uri.substringAfter("Binary/").substringBefore("/").run {
      FhirApplication.fhirEngine(context).load(Binary::class.java, this)
    }
  }

  override suspend fun resolveImageUrl(uri: String): Bitmap? {
    return HapiFhirService.create(FhirContext.forR4().newJsonParser())
      .fetchImage(uri)
      .execute()
      .run {
        if (this.body() != null) {
          BitmapFactory.decodeStream(this.body()?.byteStream())
        } else {
          null
        }
      }
  }
}
