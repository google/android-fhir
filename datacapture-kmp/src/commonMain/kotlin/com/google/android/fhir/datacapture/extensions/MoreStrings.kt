/*
 * Copyright 2025-2026 Google LLC
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

import androidx.compose.ui.text.AnnotatedString
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal

internal fun String.toAnnotatedString(): AnnotatedString {
  return AnnotatedString(this)
}

internal fun String.toBigDecimalOrNull(): BigDecimal? =
  try {
    this.toBigDecimal()
  } catch (_: NumberFormatException) {
    null
  } catch (_: ArithmeticException) {
    null
  }
