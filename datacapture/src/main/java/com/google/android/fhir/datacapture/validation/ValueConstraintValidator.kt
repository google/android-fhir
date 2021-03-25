package com.google.android.fhir.datacapture.validation

import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

open class ValueConstraintValidator(
  val url: String,
  val predicate: (Int, Int) -> Boolean,
  val messageGenerator: (allowedValue: String) -> String
) : ConstraintValidator {
  override fun validate(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent
  ): ConstraintValidator.ConstraintValidationResult {
    if (questionnaireItem.hasExtension(url)) {
      val extension = questionnaireItem.getExtensionByUrl(url)
      val answer = questionnaireResponseItem.answer[0]
      when {
        extension.value.fhirType().equals("integer") && answer.hasValueIntegerType() -> {
          val answeredValue = answer.valueIntegerType.value
          if (predicate(answeredValue, extension.value.primitiveValue().toInt())) {
            return ConstraintValidator.ConstraintValidationResult(
              false,
              messageGenerator(extension.value.primitiveValue().toInt().toString())
            )
          }
        }
      }
    }
    return ConstraintValidator.ConstraintValidationResult(true, null)
  }
}

object MaxValueValidator : ValueConstraintValidator(
  url = "http://hl7.org/fhir/StructureDefinition/maxValue",
  predicate = { a: Int, b: Int -> a > b },
  { allowedValue: String -> "Maximum value allowed is:$allowedValue" })

object MinValueValidator : ValueConstraintValidator(
  url = "http://hl7.org/fhir/StructureDefinition/minValue",
  predicate = { a: Int, b: Int -> a < b },
  { allowedValue: String -> "Minimum value allowed is:$allowedValue" })
