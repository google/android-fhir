/*
 * Copyright 2022 Google LLC
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

import org.hl7.fhir.r4.model.Element
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.utils.ToolingExtensions

internal const val EXTENSION_CQF_EXPRESSION_URL: String =
  "http://hl7.org/fhir/StructureDefinition/cqf-expression"

internal val Element.cqfExpression: Expression?
  get() =
    ToolingExtensions.getExtension(this, EXTENSION_CQF_EXPRESSION_URL)?.value?.let {
      it.castToExpression(it)
    }
