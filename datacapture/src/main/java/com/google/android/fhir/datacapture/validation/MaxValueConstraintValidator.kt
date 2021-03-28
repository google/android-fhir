package com.google.android.fhir.datacapture.validation

internal object MaxValueConstraintValidator :
  ValueConstraintValidator(
    url = "http://hl7.org/fhir/StructureDefinition/maxValue",
    predicate = { a: Int, b: Int -> a > b },
    { allowedValue: String -> "Maximum value allowed is:$allowedValue" }
  )