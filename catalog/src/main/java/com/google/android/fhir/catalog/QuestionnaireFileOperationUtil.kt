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

package com.google.android.fhir.catalog

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext

suspend fun getQuestionnaireJsonStringFromAssets(
  context: Context,
  backgroundContext: CoroutineContext,
  fileName: String
): String? {
  return withContext(backgroundContext) {
    if (fileName.isNotEmpty()) {
      context.assets.open(fileName).bufferedReader().use { it.readText() }
    } else {
      null
    }
  }
}

suspend fun getQuestionnaireJsonStringFromFileUri(
  context: Context,
  backgroundContext: CoroutineContext,
  uri: Uri
): String {
  return withContext(backgroundContext) {
    val reader = BufferedReader(context.contentResolver.openInputStream(uri)?.reader())
    reader.use { reader -> reader.readText() }
  }
}
