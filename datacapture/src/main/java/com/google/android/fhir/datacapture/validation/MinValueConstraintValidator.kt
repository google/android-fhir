package com.google.android.fhir.datacapture.validation

internal object MinValueConstraintValidator :
  ValueConstraintValidator(
    url = "http://hl7.org/fhir/StructureDefinition/minValue",
    predicate = { a: Int, b: Int -> a < b },
    { allowedValue: String -> "Minimum value allowed is:$allowedValue" }
  )