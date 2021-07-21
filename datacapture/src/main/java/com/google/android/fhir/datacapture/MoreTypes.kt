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

  if (a.isPrimitive) return a.primitiveValue() == b.primitiveValue()

  // Codes with the same system and code values are considered equal even if they have different
  // display values.
  if (a is Coding && b is Coding) return a.system == b.system && a.code == b.code

  throw NotImplementedError("Comparison for type ${a::class.java} not supported.")
}
