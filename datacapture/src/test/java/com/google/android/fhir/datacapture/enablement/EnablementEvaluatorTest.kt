package com.google.android.fhir.datacapture.enablement

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.EnableWhenBehaviorCode
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireItemOperatorCode
import com.google.fhir.r4.core.QuestionnaireResponse
import com.google.fhir.r4.core.String
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class EnablementEvaluatorTest {
    @Test
    fun evaluate_noEnableWhen_shouldReturnTrue() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder().build()) { null }
        ).isTrue()
    }

    @Test
    fun evaluate_missingQuestion_shouldReturnTrue() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                )
                .build()) { null }
        ).isTrue()
    }

    @Test
    fun evaluate_expectsAnswer_answerExists_shouldReturnTrue() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(true).build()
                            }
                        )
                )
                .build()
            ) {
                if (it == "q1") {
                    QuestionnaireResponse.Item.newBuilder()
                        .addAnswer(QuestionnaireResponse.Item.Answer.getDefaultInstance())
                        .build()
                } else {
                    null
                }
            }
        ).isTrue()
    }

    @Test
    fun evaluate_expectsAnswer_answerDoesNotExist_shouldReturnFalse() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(true).build()
                            }
                        )
                )
                .build()
            ) {
                if (it == "q1") {
                    QuestionnaireResponse.Item.getDefaultInstance()
                } else {
                    null
                }
            }
        ).isFalse()
    }

    @Test
    fun evaluate_expectsNoAnswer_answerExists_shouldReturnFalse() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(false).build()
                            }
                        )
                )
                .build()
            ) {
                if (it == "q1") {
                    QuestionnaireResponse.Item.newBuilder()
                        .addAnswer(QuestionnaireResponse.Item.Answer.getDefaultInstance())
                        .build()
                } else {
                    null
                }
            }
        ).isFalse()
    }

    @Test
    fun evaluate_expectsNoAnswer_answerDoesNotExist_shouldReturnTrue() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(false).build()
                            }
                        )
                )
                .build()
            ) {
                if (it == "q1") {
                    QuestionnaireResponse.Item.getDefaultInstance()
                } else {
                    null
                }
            }
        ).isTrue()
    }

    @Test
    fun evaluate_anyEnableWhens_noneSatisfied_shouldReturnFalse() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(true).build()
                            }
                        )
                )
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q2"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(true).build()
                            }
                        )
                )
                .setEnableBehavior(
                    Questionnaire.Item.EnableBehaviorCode.newBuilder()
                    .setValue(EnableWhenBehaviorCode.Value.ANY)
                )
                .build()
            ) {
                when (it) {
                    "q1" -> QuestionnaireResponse.Item.getDefaultInstance()
                    "q2" -> QuestionnaireResponse.Item.getDefaultInstance()
                    else -> null
                }
            }
        ).isFalse()
    }

    @Test
    fun evaluate_anyEnableWhens_oneSatisfied_shouldReturnTrue() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(false).build()
                            }
                        )
                )
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q2"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(true).build()
                            }
                        )
                )
                .setEnableBehavior(
                    Questionnaire.Item.EnableBehaviorCode.newBuilder()
                    .setValue(EnableWhenBehaviorCode.Value.ANY)
                )
                .build()
            ) {
                when (it) {
                    "q1" -> QuestionnaireResponse.Item.getDefaultInstance()
                    "q2" -> QuestionnaireResponse.Item.getDefaultInstance()
                    else -> null
                }
            }
        ).isTrue()
    }

    @Test
    fun evaluate_allEnableWhens_someSatisfied_shouldReturnFalse() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(false).build()
                            }
                        )
                )
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q2"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(true).build()
                            }
                        )
                )
                .setEnableBehavior(Questionnaire.Item.EnableBehaviorCode.newBuilder()
                    .setValue(EnableWhenBehaviorCode.Value.ALL))
                .build()
            ) {
                when (it) {
                    "q1" -> QuestionnaireResponse.Item.getDefaultInstance()
                    "q2" -> QuestionnaireResponse.Item.getDefaultInstance()
                    else -> null
                }
            }
        ).isFalse()
    }

    @Test
    fun evaluate_allEnableWhens_allSatisfied_shouldReturnTrue() {
        assertThat(
            EnablementEvaluator.evaluate(Questionnaire.Item.newBuilder()
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q1"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(false).build()
                            }
                        )
                )
                .addEnableWhen(
                    Questionnaire.Item.EnableWhen.newBuilder()
                        .setQuestion(String.newBuilder().setValue("q2"))
                        .setOperator(
                            Questionnaire.Item.EnableWhen.OperatorCode.newBuilder()
                                .setValue(QuestionnaireItemOperatorCode.Value.EXISTS)
                        )
                        .setAnswer(
                            Questionnaire.Item.EnableWhen.AnswerX.newBuilder().apply {
                                boolean = Boolean.newBuilder().setValue(false).build()
                            }
                        )
                )
                .setEnableBehavior(Questionnaire.Item.EnableBehaviorCode.newBuilder()
                    .setValue(EnableWhenBehaviorCode.Value.ALL))
                .build()
            ) {
                when (it) {
                    "q1" -> QuestionnaireResponse.Item.getDefaultInstance()
                    "q2" -> QuestionnaireResponse.Item.getDefaultInstance()
                    else -> null
                }
            }
        ).isTrue()
    }
}
