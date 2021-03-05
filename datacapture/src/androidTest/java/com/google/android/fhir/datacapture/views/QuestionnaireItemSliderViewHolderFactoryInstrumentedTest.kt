package com.google.android.fhir.datacapture.views

import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.datacapture.R
import com.google.common.truth.Truth
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.Integer
import com.google.fhir.r4.core.Questionnaire
import com.google.fhir.r4.core.QuestionnaireResponse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionnaireItemSliderViewHolderFactoryInstrumentedTest {
    private lateinit var context: ContextThemeWrapper
    private lateinit var parent: FrameLayout
    private lateinit var viewHolder: QuestionnaireItemViewHolder

    @Before
    fun setUp() {
        context = ContextThemeWrapper(
            InstrumentationRegistry.getInstrumentation().targetContext,
            R.style.Theme_MaterialComponents
        )
        parent = FrameLayout(context)
        viewHolder = QuestionnaireItemSliderViewHolderFactory.create(parent)
    }

    @Test
    fun shouldSetHeaderTextViewText() {
        viewHolder.bind(
            QuestionnaireItemViewItem(
                Questionnaire.Item.newBuilder().apply {
                    text = com.google.fhir.r4.core.String.newBuilder().setValue("Question?").build()
                }.build(),
                QuestionnaireResponse.Item.newBuilder()
            ) {}
        )

        Truth.assertThat(viewHolder.itemView.findViewById<TextView>(R.id.slider_header).text)
            .isEqualTo("Question?")
    }

    @Test
    fun singleAnswerOrNull_noAnswer_shouldReturnNull() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.getDefaultInstance(),
            QuestionnaireResponse.Item.newBuilder()
        ) {}
        Truth.assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
    }

    @Test
    fun singleAnswerOrNull_singleAnswer_shouldReturnSingleAnswer() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.getDefaultInstance(),
            QuestionnaireResponse.Item.newBuilder().apply {
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setInteger(Integer.newBuilder().setValue(5).build())
                            .build()
                    }
                )
            }
        ) {}
        Truth.assertThat(questionnaireItemViewItem.singleAnswerOrNull!!.value.integer.value).isEqualTo(5)
    }

    @Test
    fun singleAnswerOrNull_multipleAnswers_shouldReturnNull() {
        val questionnaireItemViewItem = QuestionnaireItemViewItem(
            Questionnaire.Item.getDefaultInstance(),
            QuestionnaireResponse.Item.newBuilder().apply {
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setBoolean(Boolean.newBuilder().setValue(true))
                            .build()
                    }
                )
                addAnswer(
                    QuestionnaireResponse.Item.Answer.newBuilder().apply {
                        value = QuestionnaireResponse.Item.Answer.ValueX.newBuilder()
                            .setBoolean(Boolean.newBuilder().setValue(true))
                            .build()
                    }
                )
            }
        ) {}
        Truth.assertThat(questionnaireItemViewItem.singleAnswerOrNull).isNull()
    }
}