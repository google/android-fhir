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
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Element
import com.google.fhir.model.r4.Expression
import org.jetbrains.compose.resources.stringResource

internal const val EXTENSION_CQF_EXPRESSION_URL: String =
  "http://hl7.org/fhir/StructureDefinition/cqf-expression"

@get:Composable
internal val Element.displayString: String?
  get() {
    return when (this) {
      is Coding -> display?.getLocalizedText() ?: code?.value
      is com.google.fhir.model.r4.DateTime -> {
        TODO("Requires locale based formatting")
      }
      is com.google.fhir.model.r4.Date -> {
        TODO("Requires locale based formatting")
      }
      is com.google.fhir.model.r4.Time -> {
        TODO("Requires locale based formatting")
      }
      is FhirR4Integer -> value?.toString()
      is com.google.fhir.model.r4.Reference -> display?.value ?: reference?.value
      is FhirR4String -> getLocalizedText()
      is Attachment -> url?.value
      is FhirR4Boolean -> {
        value?.let { if (it) stringResource(Res.string.yes) else stringResource(Res.string.no) }
      }
      is com.google.fhir.model.r4.Quantity -> value?.value?.toStringExpanded()
      is com.google.fhir.model.r4.Decimal -> value?.toStringExpanded()
      else -> null
    }
  }

internal val Element.cqfExpression: Expression?
  get() =
    this.extension.find { it.url == EXTENSION_CQF_EXPRESSION_URL }?.value?.asExpression()?.value
