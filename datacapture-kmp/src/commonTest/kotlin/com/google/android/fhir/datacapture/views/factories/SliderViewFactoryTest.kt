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
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.runComposeUiTest
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_SLIDER_STEP_VALUE_URL
import com.google.android.fhir.datacapture.extensions.FhirR4Boolean
import com.google.android.fhir.datacapture.extensions.FhirR4Integer
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.IntegerAnswerValue
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionTextConfiguration
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_AT_HEADER_TEST_TAG
import com.google.android.fhir.datacapture.views.compose.ERROR_TEXT_TAG
import com.google.android.fhir.datacapture.views.compose.QUESTION_HEADER_TAG
import com.google.android.fhir.datacapture.views.compose.SLIDER_TAG
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.CodeableConcept
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Extension
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Uri
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalTestApi::class)
class SliderViewFactoryTest {
  @Composable
  fun QuestionnaireSliderView(questionnaireViewItem: QuestionnaireViewItem) {
    QuestionnaireTheme { SliderViewFactory.Content(questionnaireViewItem) }
  }

  @Test
  fun shouldSetQuestionHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          text = FhirR4String(value = "Question?"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question?")
  }

  @Test
  fun shouldSetSliderValue() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "slider-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = IntegerAnswerValue(value = FhirR4Integer(value = 10)),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 10f, range = 0f..100f, steps = 99))
  }

  @Test
  fun stepSizeShouldComeFromTheSliderStepValueExtension() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-step-value"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
              Extension(
                url = EXTENSION_SLIDER_STEP_VALUE_URL,
                value = Extension.Value.Integer(value = FhirR4Integer(value = 10)),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-step-value")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    val sliderStepsFromStepSize10: Int = 100.div(10) - 1

    onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(
        ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = sliderStepsFromStepSize10),
      )
  }

  @Test
  fun stepSizeShouldBe1IfTheSliderStepValueExtensionIsNotPresent() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-step-value"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-step-value")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    val sliderStepsWithStepSize1: Int = 100 - 1

    onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(
        ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = sliderStepsWithStepSize1),
      )
  }

  @Test
  fun sliderValueToShouldComeFromTheMaxValueExtension() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 200)),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0f, range = 0f..200f, steps = 199))
  }

  @Test
  fun sliderValueToShouldBeSetToDefaultValueIfMaxValueExtensionIsNotPresent() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = 99))
  }

  @Test
  fun sliderValueFromShouldComeFromTheMinValueExtension() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 50)),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 50f, range = 50f..100f, steps = 49))
  }

  @Test
  fun sliderValueFromShouldBeSetToDefaultValueIfMinValueExtensionIsNotPresent() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(SLIDER_TAG)
      .assertRangeInfoEquals(ProgressBarRangeInfo(current = 0f, range = 0f..100f, steps = 99))
  }

  @Test
  fun throwsExceptionIfMinValueIsGreaterThanMaxvalue() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 100)),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 50)),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )
    assertFailsWith<IllegalStateException> {
      setContent { QuestionnaireSliderView(questionnaireViewItem) }
    }
  }

  @Test
  fun shouldSetQuestionnaireResponseSliderAnswer() = runComposeUiTest {
    var answerHolder: List<QuestionnaireResponse.Item.Answer>? = null
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, answers, _ -> answerHolder = answers },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(SLIDER_TAG).performSemanticsAction(SemanticsActions.SetProgress) {
      it.invoke(20f)
    }

    waitUntil { answerHolder != null }
    answerHolder!!.single().value?.asInteger()?.value?.value.shouldBe(20)
  }

  @Test
  fun shouldSetSliderValueToDefaultWhenQuestionnaireResponseHasMultipleAnswers() =
    runComposeUiTest {
      val questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "slider-item"),
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
                                code = Code(value = "slider"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "slider-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = IntegerAnswerValue(value = FhirR4Integer(value = 10)),
                ),
                QuestionnaireResponse.Item.Answer(
                  value = IntegerAnswerValue(value = FhirR4Integer(value = 10)),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      setContent { QuestionnaireSliderView(questionnaireViewItem) }

      onNodeWithTag(SLIDER_TAG)
        .assertRangeInfoEquals(ProgressBarRangeInfo(0.0f, 0.0f..100f, steps = 99))
    }

  @Test
  fun displayValidationResult_noError_shouldShowNoErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 50)),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 100)),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "slider-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = IntegerAnswerValue(value = FhirR4Integer(value = 75)),
              ),
            ),
        ),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(ERROR_TEXT_TAG).assertDoesNotExist()
  }

  @Test
  fun displayValidationResult_error_shouldShowErrorMessage() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/minValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 50)),
              ),
              Extension(
                url = "http://hl7.org/fhir/StructureDefinition/maxValue",
                value = Extension.Value.Integer(value = FhirR4Integer(value = 100)),
              ),
            ),
        ),
        QuestionnaireResponse.Item(
          linkId = FhirR4String(value = "slider-item"),
          answer =
            listOf(
              QuestionnaireResponse.Item.Answer(
                value = IntegerAnswerValue(value = FhirR4Integer(value = 25)),
              ),
            ),
        ),
        validationResult = Invalid(listOf("Minimum value allowed is:50")),
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(ERROR_TEXT_TAG).assertTextEquals("Minimum value allowed is:50")
  }

  @Test
  fun hidesErrorTextviewInTheHeader() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(ERROR_TEXT_AT_HEADER_TEST_TAG).assertDoesNotExist()
  }

  @Test
  fun bind_readOnly_shouldDisableView() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          readOnly = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(SLIDER_TAG).assertIsNotEnabled()
  }

  @Test
  fun bindMultipleTimesWithDifferentQuestionnaireItemViewItemShouldShowProperSliderValue() =
    runComposeUiTest {
      var questionnaireViewItem by
        mutableStateOf(
          QuestionnaireViewItem(
            Questionnaire.Item(
              linkId = FhirR4String(value = "slider-item"),
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
                                  code = Code(value = "slider"),
                                ),
                              ),
                          ),
                      ),
                  ),
                ),
            ),
            QuestionnaireResponse.Item(
              linkId = FhirR4String(value = "slider-item"),
              answer =
                listOf(
                  QuestionnaireResponse.Item.Answer(
                    value = IntegerAnswerValue(value = FhirR4Integer(value = 10)),
                  ),
                ),
            ),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        )

      setContent { QuestionnaireSliderView(questionnaireViewItem) }

      onNodeWithTag(SLIDER_TAG)
        .assertRangeInfoEquals(ProgressBarRangeInfo(10f, 0f..100f, steps = 99))

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "slider-item"),
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
                                code = Code(value = "slider"),
                              ),
                            ),
                        ),
                    ),
                ),
              ),
          ),
          QuestionnaireResponse.Item(
            linkId = FhirR4String(value = "slider-item"),
            answer =
              listOf(
                QuestionnaireResponse.Item.Answer(
                  value = IntegerAnswerValue(value = FhirR4Integer(value = 12)),
                ),
              ),
          ),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(SLIDER_TAG)
        .assertRangeInfoEquals(ProgressBarRangeInfo(12f, 0f..100f, steps = 99))

      questionnaireViewItem =
        QuestionnaireViewItem(
          Questionnaire.Item(
            linkId = FhirR4String(value = "slider-item"),
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
                                code = Code(value = "slider"),
                              ),
                            ),
                        ),
                    ),
                ),
                Extension(
                  url = "http://hl7.org/fhir/StructureDefinition/minValue",
                  value = Extension.Value.Integer(value = FhirR4Integer(value = 50)),
                ),
              ),
          ),
          QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
          validationResult = NotValidated,
          answersChangedCallback = { _, _, _, _ -> },
        )

      onNodeWithTag(SLIDER_TAG)
        .assertRangeInfoEquals(ProgressBarRangeInfo(50f, 50f..100f, steps = 49))
    }

  @Test
  fun hidesAsterisk() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          text = FhirR4String(value = "Question"),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showAsterisk = false),
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithTag(QUESTION_HEADER_TAG).assertTextEquals("Question")
  }

  @Test
  fun showsRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = true),
      )
    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithText("Required").assertIsDisplayed()
  }

  @Test
  fun hidesRequiredText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          required = FhirR4Boolean(value = true),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showRequiredText = false),
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithText("Required").assertDoesNotExist()
  }

  @Test
  fun showsOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          text = FhirR4String(value = "Question"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = true),
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithText("Optional").assertIsDisplayed()
  }

  @Test
  fun hidesOptionalText() = runComposeUiTest {
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.Item(
          linkId = FhirR4String(value = "slider-item"),
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
                              code = Code(value = "slider"),
                            ),
                          ),
                      ),
                  ),
              ),
            ),
          text = FhirR4String(value = "Question"),
        ),
        QuestionnaireResponse.Item(linkId = FhirR4String(value = "slider-item")),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
        questionViewTextConfiguration = QuestionTextConfiguration(showOptionalText = false),
      )

    setContent { QuestionnaireSliderView(questionnaireViewItem) }

    onNodeWithText("Optional").assertDoesNotExist()
  }
}
