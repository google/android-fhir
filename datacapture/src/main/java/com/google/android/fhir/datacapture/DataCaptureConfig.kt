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

package com.google.android.fhir.datacapture

import android.app.Application
import android.graphics.Bitmap
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Coding

/**
 * The clients may use [DataCaptureConfig] to provide [ExternalAnswerValueSetResolver] to the
 * library. The clients should set the configuration in [Application.onCreate] as it would retain it
 * across configuration changes.
 */
object DataCaptureConfig {
  /**
   * An [ExternalAnswerValueSetResolver] may be set to provide answer options dynamically for
   * `choice` and `open-choice` type questions.
   */
  var valueSetResolverExternal: ExternalAnswerValueSetResolver? = null

  var attachmentResolver: AttachmentResolver? = null
}

/**
 * Resolves external answer value sets not defined in the questionnaire's `contained` element. This
 * allows the library to render answer options to `choice` and `open-choice` type questions more
 * dynamically.
 *
 * NOTE: The result of the resolution may be cached to improve performance. In other words, the
 * resolver may be called only once after which the same answer value set may be used multiple times
 * in the UI to populate answer options.
 */
interface ExternalAnswerValueSetResolver {
  suspend fun resolve(uri: String): List<Coding>
}

interface AttachmentResolver {

  suspend fun resolveBinaryResource(uri: String): Binary?

  suspend fun resolveImageUrl(uri: String): Bitmap?
}
