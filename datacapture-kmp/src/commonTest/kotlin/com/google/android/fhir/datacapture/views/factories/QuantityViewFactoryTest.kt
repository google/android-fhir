/*
 * Copyright 2025-2026 Google LLC
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
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.QuantityAnswerValue
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.DROP_DOWN_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.components.EDIT_TEXT_FIELD_TEST_TAG
import com.google.android.fhir.datacapture.views.components.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.components.QUESTION_HEADER_TAG
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Uri
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun quantity(
  value: BigDecimal? = null,
  unit: String? = null,
  code: String? = null,
  system: String? = null,
) =
  com.google.fhir.model.r4.Quantity(
    value = Decimal(value = value),
    unit = FhirR4String(value = unit),
    code = Code(value = code),
    system = Uri(value = system),
  )

@OptIn(ExperimentalTestApi::class)
class QuantityViewFactoryTest {
  @Composable
  fun QuestionnaireQuantityView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { QuantityViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionText() = runComposeUiTest {
    val questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "quantity-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
            text = FhirR4String(value = "Question?"),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldSetInputDecimalValue() = runComposeUiTest {
    val questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "quantity-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "quantity-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = QuantityAnswerValue(value = quantity(value = "5".toBigDecimal())),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("5")
  }

  @Test
  fun shouldClearInputDecimalValue() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "quantity-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "quantity-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = QuantityAnswerValue(value = quantity(value = "5".toBigDecimal())),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("5")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("")
  }

  @Test
  fun shouldSetUnitValue() = runComposeUiTest {
    val questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "quantity-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "quantity-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = QuantityAnswerValue(value = quantity(unit = "kg")),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("kg")
  }

  @Test
  fun shouldSetUnitValueFromInitialWhenAnswerIsMissing() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          initial =
            listOf(
              Questionnaire.Item.Initial(
                value =
                  Questionnaire.Item.Initial.Value.Quantity(
                    value = quantity(unit = "kg", code = "kilo"),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("kg")
  }

  @Test
  fun shouldClearUnitValue() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "quantity-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "quantity-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = QuantityAnswerValue(value = quantity(unit = "kg")),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )
    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("kg")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("")
  }

  @Test
  fun shouldDisplayErrorMessageInValidationResult() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = Invalid(listOf("Missing answer for required field.")),
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithContentDescription("Error").assertIsDisplayed()
    onNodeWithText("Missing answer for required field.").assertIsDisplayed()
  }

  @Test
  fun shouldDisplayNoErrorMessageWhenValidationResultIsValid() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "quantity-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value =
                  QuantityAnswerValue(
                    value =
                      quantity(
                        value = 22.5.toBigDecimal(),
                      ),
                  ),
              ),
            ),
        ),
        validationResult = Valid,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithContentDescription("Error").assertDoesNotExist()
  }

  @Test
  fun shouldDisableTextInputInReadOnlyMode() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          readOnly = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertIsNotEnabled()
  }

  @Test
  fun shouldDisableUnitInputInReadOnlyMode() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          readOnly = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertIsNotEnabled()
  }

  @Test
  fun shouldAlwaysHideErrorTextviewInTheHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun shouldSetDraftWithUnit() {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem = createQuestionnaireViewItem { answers, draft ->
      answerHolder = answers
      draftHolder = draft
    }

    runComposeUiTest {
      setContent { QuestionnaireQuantityView(questionnaireViewItem) }
      onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).performClick()
      onNode(hasText("centimeter") and hasAnyAncestor(isPopup())).assertIsDisplayed().performClick()
      onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("centimeter")

      waitUntil { draftHolder != null }

      with(draftHolder as Coding) {
        assertEquals("http://unitofmeasure.com", system?.value)
        assertEquals("cm", code?.value)
        assertEquals("centimeter", display?.value)
      }
      assertTrue { answerHolder != null && answerHolder.isEmpty() }
    }
  }

  @Test
  fun shouldSetDraftWithDecimalValue() {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem = createQuestionnaireViewItem { answers, draft ->
      answerHolder = answers
      draftHolder = draft
    }

    runComposeUiTest {
      setContent { QuestionnaireQuantityView(questionnaireViewItem) }

      onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performClick().performTextInput("22")
      waitUntil { draftHolder != null }
      onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("22")

      assertEquals(22.toBigDecimal(), draftHolder as BigDecimal)
      assertTrue { answerHolder != null && answerHolder.isEmpty() }
    }
  }

  @Test
  fun draftWithUnit_shouldCompleteQuantity() {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      createQuestionnaireViewItem(
        Coding(
          system = Uri(value = "http://unitofmeasure.com"),
          code = Code(value = "cm"),
          display = FhirR4String(value = "centimeter"),
        ),
      ) { answers, draft,
        ->
        answerHolder = answers
        draftHolder = draft
      }

    runComposeUiTest {
      setContent { QuestionnaireQuantityView(questionnaireViewItem) }
      onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).performClick()

      onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).performClick().performTextInput("22")

      waitUntil { !answerHolder.isNullOrEmpty() }

      onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG).assertTextEquals("22")

      with(
        answerHolder!!.single().value?.asQuantity()?.value,
      ) {
        shouldBe(
          quantity(
            value = 22.0.toBigDecimal(),
            unit = "centimeter",
            code = "cm",
            system = "http://unitofmeasure.com",
          ),
        )
      }
      draftHolder.shouldBeNull()
    }
  }

  @Test
  fun draftWithDecimalValue_shouldCompleteQuantity() {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    var draftHolder: Any? = null

    val questionnaireViewItem =
      createQuestionnaireViewItem(draftAnswer = 22.toBigDecimal()) { answers, draft ->
        answerHolder = answers
        draftHolder = draft
      }

    runComposeUiTest {
      setContent { QuestionnaireQuantityView(questionnaireViewItem) }
      onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).performClick()

      onNode(hasText("centimeter") and hasAnyAncestor(isPopup())).assertIsDisplayed().performClick()
      onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).assertTextEquals("centimeter")

      waitUntil { !answerHolder.isNullOrEmpty() }

      with(
        answerHolder!!.single().value?.asQuantity()?.value,
      ) {
        shouldBe(
          quantity(
            value = 22.0.toBigDecimal(),
            unit = "centimeter",
            code = "cm",
            system = "http://unitofmeasure.com",
          ),
        )
      }
      draftHolder.shouldBeNull()
    }
  }

  @Test
  fun shouldShowAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          text = FhirR4String(value = "Question?"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = true),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }
    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question? *")
  }

  @Test
  fun shouldHideAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          text = FhirR4String(value = "Question?"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldShowRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithText("Required", substring = true).assertIsDisplayed()
  }

  @Test
  fun shouldHideRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      )
    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun shouldShowOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      )
    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithText("Optional", substring = true).assertIsDisplayed()
  }

  @Test
  fun shouldHideOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      )
    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithText("Optional").assertDoesNotExist()
  }

  @Test
  fun bindAgainShouldUpdateUIInputText() = runComposeUiTest {
    var questionnaireViewItem by
      mutableStateOf(
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "quantity-item"),
            type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "quantity-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = QuantityAnswerValue(value = quantity(value = "5".toBigDecimal())),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        ),
      )

    setContent { QuestionnaireQuantityView(questionnaireViewItem) }

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true).assertTextEquals("5")

    questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "quantity-item"),
          type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "quantity-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = QuantityAnswerValue(value = quantity(value = "7".toBigDecimal())),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    onNodeWithTag(EDIT_TEXT_FIELD_TEST_TAG, useUnmergedTree = true).assertTextEquals("7")
  }

  private fun createQuestionnaireViewItem(
    draftAnswer: Any? = null,
    answersChangedCallback: (List<QuestionnaireResponse.Item.Answer>, Any?) -> Unit,
  ): QuestionnaireViewItem =
    QuestionnaireViewItem(
      Questionnaire.Item(
        linkId = FhirR4String(value = "quantity-item"),
        type = Enumeration(value = Questionnaire.QuestionnaireItemType.Quantity),
        required = FhirR4Boolean(value = true),
        extension =
          listOf(
            Extension(
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption",
              value =
                Extension.Value.Coding(
                  value =
                    Coding(
                      code = Code(value = "cm"),
                      system = Uri(value = "http://unitofmeasure.com"),
                      display = FhirR4String(value = "centimeter"),
                    ),
                ),
            ),
            Extension(
              url = "http://hl7.org/fhir/StructureDefinition/questionnaire-unitOption",
              value =
                Extension.Value.Coding(
                  value =
                    Coding(
                      code = Code(value = "[in_i]"),
                      system = Uri(value = "http://unitofmeasure.com"),
                      display = FhirR4String(value = "inch"),
                    ),
                ),
            ),
          ),
      ),
      QuestionnaireResponse.Item(linkId = FhirR4String(value = "quantity-item")),
      validationResult = NotValidated,
      answersChangedCallback = { _, _, answers, draft -> answersChangedCallback(answers, draft) },
      draftAnswer = draftAnswer,
    )
}
