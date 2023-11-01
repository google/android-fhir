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

package com.google.android.fhir.document.generate

import android.content.Context
import android.widget.ImageView
import com.google.android.fhir.document.dataClasses.SHLData
import kotlinx.coroutines.CoroutineScope

interface SHLinkGenerator {

  /* Returns the newly generated SHLink - which is also stored in the SHLData argument */
  suspend fun generateSHLink(
    context: Context,
    shlData: SHLData,
    passcode: String,
  ) : String
}
