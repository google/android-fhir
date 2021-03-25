package com.google.android.fhir.datacapture.validation

import android.os.Build
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireResponseItemValidatorTest {

  @Test
  fun questionnaireResponseItemValidator_validate_shouldAggregateAllResultsOfChildValidators() {
    val extensionUrlMaxValue = "http://hl7.org/fhir/StructureDefinition/maxValue"
    val extensionUrlMinValue = "http://hl7.org/fhir/StructureDefinition/minValue"

    /**
     * Scenario 1 - answerValue is greater than maxValue
     */
    var minValue = 250
    var maxValue = 200000
    var answerValue = 200001
    val validateMaxValueViolation = validateAScenario(
      answerValue,
      maxValue,
      minValue,
      extensionUrlMaxValue,
      extensionUrlMinValue
    )
    assertThat(validateMaxValueViolation.isValid).isFalse()
    assertThat(validateMaxValueViolation.validationMessages.size == 1).isTrue()

    /**
     * Scenario 2 - answerValue is lesser than minValue
     */
    minValue = 250
    maxValue = 200000
    answerValue = 249
    val validateMinValueViolation = validateAScenario(
      answerValue,
      maxValue,
      minValue,
      extensionUrlMaxValue,
      extensionUrlMinValue
    )
    assertThat(validateMinValueViolation.isValid).isFalse()
    assertThat(validateMinValueViolation.validationMessages.size == 1).isTrue()

    /**
     * Scenario 3 - answerValue is in the range of maxValue & minValue
     */
    minValue = 250
    maxValue = 200000
    answerValue = 12354
    val validateRangeValueViolation = validateAScenario(
      answerValue,
      maxValue,
      minValue,
      extensionUrlMaxValue,
      extensionUrlMinValue
    )
    assertThat(validateRangeValueViolation.isValid).isTrue()
    assertThat(validateRangeValueViolation.validationMessages.size == 0).isTrue()
  }

  private fun validateAScenario(
    answerValue: Int,
    maxValue: Int,
    minValue: Int,
    extensionUrlMaxValue: String,
    extensionUrlMinValue: String,
  ): QuestionnaireResponseItemValidator.ValidationResult {
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val extensionMaxValue = Extension()
    val extensionMinValue = Extension()
    val questionnaireResponseItemAnswerComponent =
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
    questionnaireResponseItemAnswerComponent.value = IntegerType(answerValue)
    questionnaireResponseItem.addAnswer(questionnaireResponseItemAnswerComponent)
    extensionMaxValue.url = extensionUrlMaxValue
    extensionMaxValue.setValue(IntegerType(maxValue))
    extensionMinValue.url = extensionUrlMinValue
    extensionMinValue.setValue(IntegerType(minValue))
    questionnaireItem.apply {
      addExtension(extensionMaxValue)
      addExtension(extensionMinValue)
    }
    return QuestionnaireResponseItemValidator.validate(
      questionnaireItem,
      questionnaireResponseItem
    )
  }
}