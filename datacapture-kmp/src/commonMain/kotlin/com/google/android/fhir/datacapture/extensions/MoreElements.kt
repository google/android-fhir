/*
 * Copyright 2026 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.no
import android_fhir.datacapture_kmp.generated.resources.yes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.fhir.datacapture.getLocalDateTimeFormatter
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Element
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.Quantity
import com.google.fhir.model.r4.Reference
import com.google.fhir.model.r4.Time
import org.jetbrains.compose.resources.stringResource

@get:Composable
internal val Element.displayString: String?
  get() {
    return when (this) {
      is Coding -> remember(this) { display?.getLocalizedText() ?: code?.value }
      is DateTime -> {
        val localDateFormatter = getLocalDateTimeFormatter()
        remember(this) {
          val localDateTime = (value as? FhirDateTime.DateTime)?.dateTime
          "${localDateTime?.date?.let { localDateFormatter.format(it) }} ${
                        localDateTime?.time?.let {
                            localDateFormatter.localizedTimeString(
                                it,
                            )
                        }
                    }"
        }
      }
      is Date -> {
        val localDateFormatter = getLocalDateTimeFormatter()
        remember(this) {
          val localDate = (value as? FhirDate.Date)?.date
          localDate?.let { localDateFormatter.format(it) }
        }
      }
      is Time -> {
        val localDateFormatter = getLocalDateTimeFormatter()
        remember(this) { value?.let { localDateFormatter.localizedTimeString(it) } }
      }
      is FhirR4Integer -> remember(this) { value?.toString() }
      is Reference -> remember(this) { display?.value ?: reference?.value }
      is FhirR4String -> remember(this) { getLocalizedText() }
      is Attachment -> remember(this) { url?.value }
      is FhirR4Boolean -> {
        val yesStringText = stringResource(Res.string.yes)
        val noStringText = stringResource(Res.string.no)

        remember(this) { value?.let { if (it) yesStringText else noStringText } }
      }
      is Quantity -> remember(this) { value?.value?.toStringExpanded() }
      is Decimal -> remember(this) { value?.toStringExpanded() }
      else -> remember(this) { null }
    }
  }
