/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.document

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.SHLData
import com.google.android.fhir.document.interfaces.SHLGenerator
import com.google.android.fhir.document.utils.GenerateShlUtils
import com.google.android.fhir.document.utils.QRGeneratorUtils
import java.security.SecureRandom
import java.util.Base64

class LinkGenerator : SHLGenerator {

  private val generateShlUtils = GenerateShlUtils(QRGeneratorUtils())

  @RequiresApi(Build.VERSION_CODES.O)
  override fun generateSHL(
    context: Context,
    shlData: SHLData,
    passcode: String,
    qrView: ImageView
  ) {
    generateShlUtils.generateAndPostPayload(passcode, shlData, context, qrView)
  }
}
