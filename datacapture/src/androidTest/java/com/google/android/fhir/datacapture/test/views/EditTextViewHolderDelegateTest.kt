package com.google.android.fhir.datacapture.test.views

import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.printToLog
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.factories.EditTextStringViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.EditTextViewHolderDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.StringType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class EditTextViewHolderDelegateTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun externalUpdateOfQuestionnaireViewItemDoesNotUpdateEditTextAndOverrideKeyboardInput() {
        var pendingTextChange = 0
        val handlingTextIdlingResource = object : IdlingResource {
            override val isIdleNow: Boolean
                get() = pendingTextChange == 0
        }
        composeTestRule.registerIdlingResource(handlingTextIdlingResource)
        var questionnaireViewItem by mutableStateOf(QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent(),
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                answer =
                    listOf(
                        QuestionnaireResponseItemAnswerComponent().apply { value = StringType("") },
                    )
            },
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
        ))

        val editTextViewHolderDelegate = EditTextViewHolderDelegate(
            keyboardOptions = KeyboardOptions.Default,
            uiInputText = { it.answers.single().valueStringType.value },
            uiValidationMessage = { q, context ->
                if (q.draftAnswer != null) {
                    context.getString(
                        com.google.android.fhir.datacapture.R.string.decimal_format_validation_error_msg,
                    )
                } else {
                    getValidationErrorMessage(
                        context,
                        q,
                        q.validationResult,
                    )
                }

            },
            handleInput = { text, q ->
                println("Hello => START")
                questionnaireViewItem = q.copy(
                    questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                        answer =
                            listOf(
                                QuestionnaireResponseItemAnswerComponent().apply { value = StringType(text) },
                            )
                    }
                )
                println("Hello => END!!")
                println("Pending text change: $$pendingTextChange")
                pendingTextChange -= if (pendingTextChange > 0 ) 1 else 0
            }
        )

        composeTestRule.setContent {
            editTextViewHolderDelegate.Content(questionnaireViewItem)
        }
        composeTestRule.onRoot().printToLog("EditTextViewHolderDelegateTest")
        composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("Yellow")

//        composeTestRule.onRoot().printToLog("EditTextViewHolderDelegateTest")
        composeTestRule.onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("Yellow")

        println(questionnaireViewItem.answers.single().valueStringType.value)
        composeTestRule.unregisterIdlingResource(handlingTextIdlingResource)
    }
}