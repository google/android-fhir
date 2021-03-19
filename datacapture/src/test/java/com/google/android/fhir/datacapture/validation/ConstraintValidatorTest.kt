package com.google.android.fhir.datacapture.validation

import android.os.Build
import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.r4.core.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ConstraintValidatorTest {

    @Test
    fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfAnswerBuilderListIsEmpty () {
        val questionnaireResponseItemBuilder = QuestionnaireResponse.Item.newBuilder()
        val questionnaireItemBuilder = Questionnaire.Item.newBuilder()
        val extensionBuilder = Extension.newBuilder()
        extensionBuilder.setUrl(Uri.newBuilder().setValue("http://hl7.org/fhir/StructureDefinition/maxValue"))
        questionnaireItemBuilder.apply { addExtension(extensionBuilder.build())  }
        val validate = QuestionnaireResponseItemValidator.validate(
            questionnaireItemBuilder.build(),
            questionnaireResponseItemBuilder
        )
        assertThat(validate.isEmpty()).isFalse()
        assertThat(validate[0].isValid).isTrue()
    }

    @Test
    fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfThereIsNoExtension () {
        val questionnaireResponseItemBuilder = QuestionnaireResponse.Item.newBuilder()
        val questionnaireItemBuilder = Questionnaire.Item.newBuilder()
        questionnaireResponseItemBuilder.addAnswer(QuestionnaireResponse.Item.Answer.newBuilder())
        val validate = QuestionnaireResponseItemValidator.validate(
            questionnaireItemBuilder.build(),
            questionnaireResponseItemBuilder
        )
        assertThat(validate.isEmpty()).isFalse()
        assertThat(validate[0].isValid).isTrue()
    }

    @Test
    fun questionnaireResponseItemValidator_validate_shouldReturnFalseIfAnswerIsGreaterThanMaxValue () {
        val maxValue = 200000
        val answerValue = 200001
        val extensionUrl = "http://hl7.org/fhir/StructureDefinition/maxValue"
        val validationMessage = "Maximum value allowed is:$maxValue"

        val questionnaireResponseItemBuilder = QuestionnaireResponse.Item.newBuilder()
        val questionnaireItemBuilder = Questionnaire.Item.newBuilder()
        val extensionBuilder = Extension.newBuilder()

        questionnaireResponseItemBuilder.addAnswer(QuestionnaireResponse.Item.Answer.newBuilder()
            .setValue(QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                .setInteger(Integer.newBuilder().setValue(answerValue))))
        extensionBuilder.setUrl(Uri.newBuilder().setValue(extensionUrl))
        extensionBuilder.setValue(Extension.ValueX.newBuilder().setInteger(Integer.newBuilder().setValue(maxValue)))
        questionnaireItemBuilder.apply { addExtension(extensionBuilder.build())  }

        val validate = QuestionnaireResponseItemValidator.validate(
            questionnaireItemBuilder.build(),
            questionnaireResponseItemBuilder
        )

        assertThat(validate.isEmpty()).isFalse()
        assertThat(validate[0].isValid).isFalse()
        assertThat(validate[0].message.equals(validationMessage)).isTrue()
    }

    @Test
    fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfAnswerIsLessThanMaxValue () {
        val maxValue = 200000
        val answerValue = 199999
        val extensionUrl = "http://hl7.org/fhir/StructureDefinition/maxValue"

        val questionnaireResponseItemBuilder = QuestionnaireResponse.Item.newBuilder()
        val questionnaireItemBuilder = Questionnaire.Item.newBuilder()
        val extensionBuilder = Extension.newBuilder()

        questionnaireResponseItemBuilder.addAnswer(QuestionnaireResponse.Item.Answer.newBuilder()
            .setValue(QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                .setInteger(Integer.newBuilder().setValue(answerValue))))
        extensionBuilder.setUrl(Uri.newBuilder().setValue(extensionUrl))
        extensionBuilder.setValue(Extension.ValueX.newBuilder().setInteger(Integer.newBuilder().setValue(maxValue)))
        questionnaireItemBuilder.apply { addExtension(extensionBuilder.build())  }

        val validate = QuestionnaireResponseItemValidator.validate(
            questionnaireItemBuilder.build(),
            questionnaireResponseItemBuilder
        )

        assertThat(validate.isEmpty()).isFalse()
        assertThat(validate[0].isValid).isTrue()
        assertThat(validate[0].message==null).isTrue()
    }

    @Test
    fun questionnaireResponseItemValidator_validate_shouldReturnFalseIfAnswerIsLessThanMinValue () {
        val minValue = 250
        val answerValue = 249
        val extensionUrl = "http://hl7.org/fhir/StructureDefinition/minValue"
        val validationMessage = "Minimum value allowed is:$minValue"

        val questionnaireResponseItemBuilder = QuestionnaireResponse.Item.newBuilder()
        val questionnaireItemBuilder = Questionnaire.Item.newBuilder()
        val extensionBuilder = Extension.newBuilder()

        questionnaireResponseItemBuilder.addAnswer(QuestionnaireResponse.Item.Answer.newBuilder()
            .setValue(QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                .setInteger(Integer.newBuilder().setValue(answerValue))))
        extensionBuilder.setUrl(Uri.newBuilder().setValue(extensionUrl))
        extensionBuilder.setValue(Extension.ValueX.newBuilder().setInteger(Integer.newBuilder().setValue(minValue)))
        questionnaireItemBuilder.apply { addExtension(extensionBuilder.build())  }

        val validate = QuestionnaireResponseItemValidator.validate(
            questionnaireItemBuilder.build(),
            questionnaireResponseItemBuilder
        )

        assertThat(validate.isEmpty()).isFalse()
        assertThat(validate[1].isValid).isFalse()
        assertThat(validate[1].message.equals(validationMessage)).isTrue()
    }

    @Test
    fun questionnaireResponseItemValidator_validate_shouldReturnTrueIfAnswerIsGreaterThanMinValue () {
        val minValue = 250
        val answerValue = 2251
        val extensionUrl = "http://hl7.org/fhir/StructureDefinition/minValue"

        val questionnaireResponseItemBuilder = QuestionnaireResponse.Item.newBuilder()
        val questionnaireItemBuilder = Questionnaire.Item.newBuilder()
        val extensionBuilder = Extension.newBuilder()

        questionnaireResponseItemBuilder.addAnswer(QuestionnaireResponse.Item.Answer.newBuilder()
            .setValue(QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                .setInteger(Integer.newBuilder().setValue(answerValue))))
        extensionBuilder.setUrl(Uri.newBuilder().setValue(extensionUrl))
        extensionBuilder.setValue(Extension.ValueX.newBuilder().setInteger(Integer.newBuilder().setValue(minValue)))
        questionnaireItemBuilder.apply { addExtension(extensionBuilder.build())  }

        val validate = QuestionnaireResponseItemValidator.validate(
            questionnaireItemBuilder.build(),
            questionnaireResponseItemBuilder
        )

        assertThat(validate.isEmpty()).isFalse()
        assertThat(validate[1].isValid).isTrue()
        assertThat(validate[1].message==null).isTrue()
    }
}