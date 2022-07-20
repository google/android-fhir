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

package com.google.android.fhir.datacapture.utilities

import org.hl7.fhir.r4.model.Expression

/**
 * An expression which does not refer to a specific linkId or variable and derives value using a
 * generic expression
 */
internal val Expression.hasDynamicExpression
  get() =
    this.expression.replace(" ", "").contains("linkId='").not() ||
      this.expression.matches(Regex(".*%\\w+.*")).not()
