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

import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Type

/**
 * Returns whether two instances of the [Type] class are equal.
 *
 * Note this is not an operator because it is not possible to overload the equality operator as an
 * extension.
 */
internal fun equals(a: Type, b: Type): Boolean {
  if (a::class != b::class) return false

  if (a === b) return true

  if (a.isPrimitive) return a.primitiveValue() == b.primitiveValue()

  // Codes with the same system and code values are considered equal even if they have different
  // display values.
  if (a is Coding && b is Coding) return a.system == b.system && a.code == b.code

  throw NotImplementedError("Comparison for type ${a::class.java} not supported.")
}
