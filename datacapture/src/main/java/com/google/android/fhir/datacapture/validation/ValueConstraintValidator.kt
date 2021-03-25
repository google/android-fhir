package com.google.android.fhir.datacapture.validation

import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.QuestionnaireResponse

class ValueConstraintValidator {
  companion object {
    fun valueConstraintValidator(
      extension: Extension,
      questionnaireResponseItemBuilder: QuestionnaireResponse.QuestionnaireResponseItemComponent,
      operator: String
    ): Boolean {
      val answer = questionnaireResponseItemBuilder.answer[0]
      when {
        extension.value.fhirType().equals("integer") && answer.hasValueIntegerType() -> {
          val answeredValue = answer.valueIntegerType.value
          when (operator) {
            ">" -> if (answeredValue > extension.value.primitiveValue().toInt()) {
              return true
            }
            "<" -> if (answeredValue < extension.value.primitiveValue().toInt()) {
              return true
            }
          }
        }
      }
      return false
    }
  }
}
