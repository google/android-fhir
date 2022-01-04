/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.datacapture.utilities

import android.text.Spanned
import androidx.core.text.HtmlCompat
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.UriType

/** Converts StringType to toUriType. */
internal fun StringType.toUriType(): UriType {
  return UriType(value)
}

/** Converts StringType to CodeType. */
internal fun StringType.toCodeType(): CodeType {
  return CodeType(value)
}

/** Converts StringType to IdType. */
internal fun StringType.toIdType(): IdType {
  return IdType(value)
}

/** Converts Coding to CodeType. */
internal fun Coding.toCodeType(): CodeType {
  return CodeType(code)
}

/** Converts Text with HTML Tag to formated text. */
internal fun String.toSpanned(): Spanned {
  return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
}
