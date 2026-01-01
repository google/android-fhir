/*
 * Copyright 2023-2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.datacapture.views.factories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4Integer
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.StringAnswerValue
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.QUESTION_HEADER_TAG
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.CodeableConcept
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Uri
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class EditTextPhoneNumberViewFactoryTest {
  @Composable
  fun QuestionnaireEditTextPhoneNumberView(questionnaireViewItem: QuestionnaireViewItem) {
    EditTextPhoneNumberViewFactory.Content(questionnaireViewItem)
  }

  @Test
  fun shouldSetTextViewText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "phone-number-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldSetInputText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = StringAnswerValue(value = FhirR4String(value = "+12345678910")),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("+12345678910")
  }

  @Test
  fun shouldSetInputTextToEmpty() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "phone-number-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
            extension =
              listOf(
                Extension(
                  url = EXTENSION_ITEM_CONTROL_URL,
                  value =
                    Extension.Value.CodeableConcept(
                      value =
                        CodeableConcept(
                          coding =
                            listOf(
                              Coding(
                                system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                                code = Code(value = "phone-number"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "phone-number-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = StringAnswerValue(value = FhirR4String(value = "+12345678910")),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "phone-number-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("")
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswer() = runComposeUiTest {
    var answers: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "phone-number-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, newAnswers, _ -> answers = newAnswers },
      )
    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("+12345678910")

    waitUntil { answers != null }

    answers!!.single().value?.asString()?.value?.value.shouldBe("+12345678910")
  }

  @Test
  fun shouldSetQuestionnaireResponseItemAnswerToEmpty() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "phone-number-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }
    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performTextReplacement("")
    waitForIdle()
    questionnaireViewItem.answers.shouldBeEmpty()
  }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minLength",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 10)),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = StringAnswerValue(value = FhirR4String(value = "hello there")),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }

    onNodeWithContentDescription("Error").assertDoesNotExist()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          maxLength = FhirR4Integer(value = 10),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = StringAnswerValue(value = FhirR4String(value = "+1234567891011")),
              ),
            ),
        ),
        validationResult =
          Invalid(
            listOf("The maximum number of characters that are permitted in the answer is: 10"),
          ),
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }

    onNodeWithContentDescription("Error").assertIsDisplayed()

    onNodeWithText("The maximum number of characters that are permitted in the answer is: 10")
      .assertIsDisplayed()
  }

  @Test
  fun bind_readOnly_shouldDisableView() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "phone-number-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Integer),
          extension =
            listOf(
              Extension(
                url = EXTENSION_ITEM_CONTROL_URL,
                value =
                  Extension.Value.CodeableConcept(
                    value =
                      CodeableConcept(
                        coding =
                          listOf(
                            Coding(
                              system = Uri(value = EXTENSION_ITEM_CONTROL_SYSTEM),
                              code = Code(value = "phone-number"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          readOnly = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "phone-number-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireEditTextPhoneNumberView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertIsNotEnabled()
  }
}
