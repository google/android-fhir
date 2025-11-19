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

package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4.Extension

fun Extension.readStringExtension(uri: String): String? {
  val ext = extension.single { it.url == uri }
  return ext.value?.asUri()?.value?.value
    ?: ext.value?.asCanonical()?.value?.value ?: ext.value?.asCode()?.value?.value
      ?: ext.value?.asInteger()?.value?.value?.toString() ?: ext.value?.asMarkdown()?.value?.value
      ?: ext.value?.asString()?.value?.value
}
