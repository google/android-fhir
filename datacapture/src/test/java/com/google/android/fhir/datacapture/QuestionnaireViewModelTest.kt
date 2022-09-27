/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_ENABLE_REVIEW_PAGE
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_URI
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_REVIEW_PAGE_FIRST
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import org.robolectric.util.ReflectionHelpers

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class QuestionnaireViewModelTest(
  private val questionnaireSource: QuestionnaireSource,
  private val questionnaireResponseSource: QuestionnaireResponseSource
) {
  private lateinit var state: SavedStateHandle
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Before
  fun setUp() {
    state = SavedStateHandle()
    check(
      ApplicationProvider.getApplicationContext<DataCaptureTestApplication>() is
        DataCaptureConfig.Provider
    ) { "Few tests require a custom application class that implements DataCaptureConfig.Provider" }
    ReflectionHelpers.setStaticField(DataCapture::class.java, "configuration", null)
  }

  @Test
  fun stateHasNoQuestionnaire_shouldThrow() {
    val errorMessage =
      assertFailsWith<IllegalStateException> { QuestionnaireViewModel(context, state) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Neither EXTRA_QUESTIONNAIRE_JSON_URI nor EXTRA_QUESTIONNAIRE_JSON_STRING is supplied."
      )
  }

  @Test
  fun stateHasNoQuestionnaireResponse_shouldCopyQuestionnaireUrl() {
    val questionnaire =
      Questionnaire().apply {
        url = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        this.questionnaire = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
      }
    )
  }

  @Test
  fun stateHasNoQuestionnaireResponse_shouldCopyQuestion() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Yes or no?"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Yes or no?"
          }
        )
      }
    )
  }

  @Test
  fun stateHasNoQuestionnaireResponse_shouldCopyQuestionnaireStructure() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Name?"
                type = Questionnaire.QuestionnaireItemType.STRING
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "another-link-id"
                text = "Name?"
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun stateHasQuestionnaireResponse_nestedItemsWithinGroupItems_shouldNotThrowException() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Is this true?"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                addItem(
                  Questionnaire.QuestionnaireItemComponent().apply {
                    linkId = "yet-another-link-id"
                    text = "Name?"
                    type = Questionnaire.QuestionnaireItemType.STRING
                  }
                )
              }
            )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "another-link-id"
                text = "Is this true?"
                addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = BooleanType(true)
                    addItem(
                      QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                        linkId = "yet-another-link-id"
                        text = "Name?"
                        addAnswer(
                          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                            value = StringType("a-name")
                          }
                        )
                      }
                    )
                  }
                )
              }
            )
          }
        )
      }

    createQuestionnaireViewModel(questionnaire, questionnaireResponse)
  }

  @Test
  fun stateHasQuestionnaireResponse_nestedItemsWithinNonGroupItems_shouldNotThrowException() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Is this true?"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Name?"
                type = Questionnaire.QuestionnaireItemType.STRING
              }
            )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Is this true?"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-link-id"
                    text = "Name?"
                    addAnswer(
                      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                        value = StringType("a-name")
                      }
                    )
                  }
                )
              }
            )
          }
        )
      }

    createQuestionnaireViewModel(questionnaire, questionnaireResponse)
  }

  @Test
  fun stateHasQuestionnaireResponse_nonPrimitiveType_shouldNotThrowError() {
    val testOption1 = Coding("test", "option", "1")
    val testOption2 = Coding("test", "option", "2")

    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            answerOption =
              listOf(
                Questionnaire.QuestionnaireItemAnswerOptionComponent(testOption1),
                Questionnaire.QuestionnaireItemAnswerOptionComponent(testOption2)
              )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = testOption1
              }
            )
          }
        )
      }

    createQuestionnaireViewModel(questionnaire, questionnaireResponse)
  }

  @Test
  fun stateHasQuestionnaireResponse_questionnaireUrlMatches_shouldNotThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        url = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        this.questionnaire = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
      }

    createQuestionnaireViewModel(questionnaire, questionnaireResponse)
  }

  @Test
  fun stateHasQuestionnaireResponse_questionnaireUrlDoesNotMatch_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        url = "http://www.sample-org/FHIR/Resources/Questionnaire/questionnaire-1"
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        this.questionnaire = "Questionnaire/a-questionnaire"
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> {
          createQuestionnaireViewModel(questionnaire, questionnaireResponse)
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Mismatching Questionnaire http://www.sample-org/FHIR/Resources/Questionnaire/questionnaire-1 and QuestionnaireResponse (for Questionnaire Questionnaire/a-questionnaire)"
      )
  }

  @Test
  fun stateHasQuestionnaireResponse_wrongLinkId_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-different-link-id"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> {
          createQuestionnaireViewModel(questionnaire, questionnaireResponse)
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo("Missing questionnaire item for questionnaire response item a-different-link-id")
  }

  @Test
  fun stateHasQuestionnaireResponse_lessItemsInQuestionnaireResponse_shouldAddTheMissingItem() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(BooleanType(true)))
          }
        )
      }
    val questionnaireResponse = QuestionnaireResponse().apply { id = "a-questionnaire-response" }
    val questionnaireResponseWithMissingItem =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            answer =
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
          }
        )
      }

    val questionnaireViewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)
    val questionnaireItemViewItem = questionnaireViewModel.questionnaireStateFlow.first()
    assertThat(questionnaireItemViewItem.items.first().questionnaireItem.linkId)
      .isEqualTo(questionnaireResponseWithMissingItem.item.first().linkId)
    assertThat(
        questionnaireItemViewItem.items.first().answers.first().valueBooleanType.booleanValue()
      )
      .isEqualTo(
        questionnaireResponseWithMissingItem
          .item
          .first()
          .answer
          .first()
          .valueBooleanType
          .booleanValue()
      )
  }

  @Test
  fun stateHasQuestionnaireResponse_wrongType_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("true")
              }
            )
          }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> {
          createQuestionnaireViewModel(questionnaire, questionnaireResponse)
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo("Mismatching question type BOOLEAN and answer type string for a-link-id")
  }

  @Test
  fun stateHasQuestionnaireResponse_repeatsTrueWithMultipleAnswers_shouldNotThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question which allows multiple answers"
            type = Questionnaire.QuestionnaireItemType.STRING
            repeats = true
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question which allows multiple answers"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("string 1")
              }
            )
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("string 2")
              }
            )
          }
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    assertResourceEquals(questionnaireResponse, viewModel.getQuestionnaireResponse())
  }

  @Test
  fun stateHasQuestionnaireResponse_repeatsFalseWithMultipleAnswers_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            repeats = false
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> {
          createQuestionnaireViewModel(questionnaire, questionnaireResponse)
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo("Multiple answers for non-repeat questionnaire item a-link-id")
  }

  @Test
  fun questionnaireHasInitialValue_shouldSetAnswerValueInQuestionnaireResponse() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().apply {
                  value = BooleanType(false)
                }
              )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun questionnaireHasMultipleInitialValuesForRepeatingCase_shouldSetFirstAnswerValueInQuestionnaireResponse() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            repeats = true
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true))
              )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun questionnaireHasInitialValueButQuestionnaireResponseAsEmpty_shouldSetEmptyAnswer() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().apply { value = valueCoding }
              )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("")
              }
            )
          }
        )
      }
    createQuestionnaireViewModel(questionnaire, questionnaireResponse)
  }

  @Test
  fun `getQuestionnaireResponse() should have text in questionnaire response item`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial(
              Questionnaire.QuestionnaireItemInitialComponent().apply { value = BooleanType(false) }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `getQuestionnaireResponse() should have translated text in questionnaire response item`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            textElement.apply {
              addExtension(
                Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                  addExtension(Extension("lang", StringType("en-US")))
                  addExtension(Extension("content", StringType("Basic Question")))
                }
              )
            }
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial(
              Questionnaire.QuestionnaireItemInitialComponent().apply { value = BooleanType(false) }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic Question"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `getQuestionnaireResponse() should have text in questionnaire response item for nested items`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "a.1-link-id"
                text = "Basic Nested question"
                type = Questionnaire.QuestionnaireItemType.STRING
                initial =
                  mutableListOf(
                    Questionnaire.QuestionnaireItemInitialComponent().apply {
                      value = StringType("Test Value")
                    }
                  )
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "a.1-link-id"
                text = "Basic Nested question"
                answer =
                  listOf(
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = StringType("Test Value")
                    }
                  )
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `createQuestionnaireViewModel() should throw IllegalArgumentException for multiple initial values with non repeating item`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            repeats = false
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true))
              )
          }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { createQuestionnaireViewModel(questionnaire) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Questionnaire item a-link-id can only have multiple initial values for repeating items. See rule que-13 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
      )
  }

  @Test
  fun questionnaireItemMissingType_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
          }
        )
      }

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { linkId = "a-link-id" }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalStateException> {
          createQuestionnaireViewModel(questionnaire, questionnaireResponse)
        }
        .localizedMessage

    assertThat(errorMessage).isEqualTo("Questionnaire item must have type")
  }

  @Test
  fun questionnaireHasInitialValueAndGroupType_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true))
              )
          }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { createQuestionnaireViewModel(questionnaire) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Questionnaire item a-link-id has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
      )
  }

  @Test
  fun questionnaireHasInitialValueAndDisplayType_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.DISPLAY
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true))
              )
          }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { createQuestionnaireViewModel(questionnaire) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Questionnaire item a-link-id has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
      )
  }

  @Test
  fun stateHasQuestionnaireResponse_moreItemsInQuestionnaireResponse_shouldThrowError() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        )
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-different-link-id"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> {
          createQuestionnaireViewModel(questionnaire, questionnaireResponse)
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo("Missing questionnaire item for questionnaire response item a-different-link-id")
  }

  @Test
  fun `should emit questionnaire state flow`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Name?"
                type = Questionnaire.QuestionnaireItemType.STRING
              }
            )
          }
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)

    val questionnaireItemViewItemList = viewModel.getQuestionnaireItemViewItemList()
    assertThat(questionnaireItemViewItemList).hasSize(2)

    val firstQuestionnaireItem = questionnaireItemViewItemList[0].questionnaireItem
    assertThat(firstQuestionnaireItem.linkId).isEqualTo("a-link-id")
    assertThat(firstQuestionnaireItem.text).isEqualTo("Basic questions")
    assertThat(firstQuestionnaireItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.GROUP)

    val secondQuestionnaireItem = questionnaireItemViewItemList[1].questionnaireItem
    assertThat(secondQuestionnaireItem.linkId).isEqualTo("another-link-id")
    assertThat(secondQuestionnaireItem.text).isEqualTo("Name?")
    assertThat(secondQuestionnaireItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.STRING)
  }

  @Test
  fun `should emit questionnaire state flow without initial validation`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "link-id"
            text = "Name?"
            type = Questionnaire.QuestionnaireItemType.STRING
            required = true
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    val questionnaireItemViewItemList = viewModel.getQuestionnaireItemViewItemList()
    assertThat(questionnaireItemViewItemList.single().validationResult).isEqualTo(NotValidated)
  }

  @Test
  fun `should emit questionnaire state flow with validation for modified items`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "link-id"
            text = "Name?"
            type = Questionnaire.QuestionnaireItemType.STRING
            required = true
          }
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    var questionnaireItemViewItem: QuestionnaireItemViewItem? = null

    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { questionnaireItemViewItem = it.items.single() }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      questionnaireItemViewItem!!.clearAnswer()

      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      assertThat(questionnaireItemViewItem!!.validationResult)
        .isEqualTo(Invalid(listOf("Missing answer for required field.")))
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should emit questionnaire state flow without disabled questions`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(false) }
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertThat(viewModel.getQuestionnaireItemViewItemList().single().questionnaireItem.linkId)
      .isEqualTo("question-1")
  }

  @Test
  fun `should emit questionnaire state flow with enabled questions`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(true) }
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    val questionnaireItemViewItemList = viewModel.getQuestionnaireItemViewItemList()

    assertThat(questionnaireItemViewItemList).hasSize(2)
    assertThat(questionnaireItemViewItemList[0].questionnaireItem.linkId).isEqualTo("question-1")
    assertThat(questionnaireItemViewItemList[1].questionnaireItem.linkId).isEqualTo("question-2")
  }

  @Test
  fun questionnaireHasNestedItem_ofTypeGroup_shouldNestItemWithinItem() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-group-item"
            text = "Group question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "a-nested-item"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-group-item"
            text = "Group question"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "a-nested-item"
                text = "Basic question"
                addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    this.value = valueBooleanType.setValue(false)
                  }
                )
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    viewModel.getQuestionnaireItemViewItemList()[1].setAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        this.value = valueBooleanType.setValue(false)
      }
    )

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  @Ignore("https://github.com/google/android-fhir/issues/487")
  fun questionnaireHasNestedItem_notOfTypeGroup_shouldNestItemWithinAnswerItem() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item"
            text = "Parent question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "a-nested-boolean-item"
                text = "Nested question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
      }

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-boolean-item"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                this.value = valueBooleanType.setValue(false)
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "a-nested-boolean-item"
                    addAnswer(
                      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                        this.value = valueBooleanType.setValue(false)
                      }
                    )
                  }
                )
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    viewModel.getQuestionnaireItemViewItemList()[0].setAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        this.value = valueBooleanType.setValue(false)
      }
    )
    viewModel.getQuestionnaireItemViewItemList()[1].setAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        this.value = valueBooleanType.setValue(false)
      }
    )

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun `should show questionnaire items in the active page in a paginated questionnaire`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    val state = viewModel.questionnaireStateFlow.first()
    assertThat(state.pagination)
      .isEqualTo(
        QuestionnairePagination(
          isPaginated = true,
          pages = listOf(QuestionnairePage(0, true), QuestionnairePage(1, true)),
          currentPageIndex = 0
        )
      )
    assertThat(state.items).hasSize(2)
    state.items[0].questionnaireItem.let { groupItem ->
      assertThat(groupItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.GROUP)
      assertThat(groupItem.linkId).isEqualTo("page1")
    }
    state.items[1].questionnaireItem.let { questionItem ->
      assertThat(questionItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.BOOLEAN)
      assertThat(questionItem.linkId).isEqualTo("page1-1")
      assertThat(questionItem.text).isEqualTo("Question on page 1")
    }
  }

  @Test
  fun `should go to next page in a paginated questionnaire`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null

    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()

      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = listOf(QuestionnairePage(0, true), QuestionnairePage(1, true)),
            currentPageIndex = 1
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should go to previous page in a paginated questionnaire`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null

    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = listOf(QuestionnairePage(0, true), QuestionnairePage(1, true)),
            currentPageIndex = 0
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should skip disabled page in a paginated questionnaire`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "page1-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page3"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page3-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 3"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null

    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()

      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, true),
                QuestionnairePage(1, false),
                QuestionnairePage(2, true)
              ),
            currentPageIndex = 2
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should allow user to move forward using prior entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("prior-edit"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.PRIOR_EDIT)
      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should allow user to move forward and back using prior entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("prior-edit"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.PRIOR_EDIT)
      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should not allow user to move forward using prior entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("prior-edit"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                required = true
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should allow user to move forward using random entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("random"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.goToNextPage()

    assertThat(questionnaire.entryMode).isEqualTo(EntryMode.RANDOM)
    assertTrue(viewModel.currentPageIndexFlow.value == viewModel.pages?.last()?.index)
  }

  @Test
  fun `should allow user to move forward and back using random entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("random"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.goToNextPage()
    viewModel.goToPreviousPage()

    assertThat(questionnaire.entryMode).isEqualTo(EntryMode.RANDOM)
    assertTrue(viewModel.currentPageIndexFlow.value == viewModel.pages?.first()?.index)
  }

  @Test
  fun `should allow user to move forward when no entry-mode is defined`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.goToNextPage()

    assertThat(viewModel.entryMode).isEqualTo(EntryMode.RANDOM)
    assertTrue(viewModel.currentPageIndexFlow.value == viewModel.pages?.last()?.index)
  }

  @Test
  fun `should allow user to move forward and back when no entry-mode is defined`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.goToNextPage()
    viewModel.goToPreviousPage()

    assertThat(viewModel.entryMode).isEqualTo(EntryMode.RANDOM)
    assertTrue(viewModel.currentPageIndexFlow.value == viewModel.pages?.first()?.index)
  }

  @Test
  fun `should allow user to move forward only using sequential entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("sequential"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.SEQUENTIAL)
      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should not allow user to move forward using sequential entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("sequential"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                required = true
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `should not user to move backward only using sequential entry-mode`() = runBlocking {
    val entryModeExtension =
      Extension().apply {
        url = EXTENSION_ENTRY_MODE_URL
        setValue(StringType("sequential"))
      }
    val questionnaire =
      Questionnaire().apply {
        addExtension(entryModeExtension)
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1
          )
        )
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun questionnaire_resolveContainedAnswerValueSet() = runBlocking {
    val valueSetId = "yesnodontknow"
    val questionnaire =
      Questionnaire().apply {
        addContained(
          ValueSet().apply {
            id = valueSetId
            expansion =
              ValueSet.ValueSetExpansionComponent().apply {
                addContains(
                  ValueSet.ValueSetExpansionContainsComponent().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "Y"
                    display = "Yes"
                  }
                )

                addContains(
                  ValueSet.ValueSetExpansionContainsComponent().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "N"
                    display = "No"
                  }
                )

                addContains(
                  ValueSet.ValueSetExpansionContainsComponent().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "asked-unknown"
                    display = "Don't Know"
                  }
                )
              }
          }
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    val codeSet = viewModel.resolveAnswerValueSet("#$valueSetId")

    assertThat(codeSet.map { it.valueCoding.display })
      .containsExactly("Yes", "No", "Don't Know")
      .inOrder()
  }

  @Test
  fun questionnaire_resolveAnswerValueSetExternalResolved() = runBlocking {
    val questionnaire = Questionnaire().apply { id = "a-questionnaire" }

    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .dataCaptureConfiguration =
      DataCaptureConfig(
        valueSetResolverExternal =
          object : ExternalAnswerValueSetResolver {
            override suspend fun resolve(uri: String): List<Coding> {

              return if (uri == CODE_SYSTEM_YES_NO)
                listOf(
                  Coding().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "Y"
                    display = "Yes"
                  },
                  Coding().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "N"
                    display = "No"
                  },
                  Coding().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "asked-unknown"
                    display = "Don't Know"
                  }
                )
              else emptyList()
            }
          }
      )

    val viewModel = createQuestionnaireViewModel(questionnaire)
    val codeSet = viewModel.resolveAnswerValueSet(CODE_SYSTEM_YES_NO)
    assertThat(codeSet.map { it.valueCoding.display })
      .containsExactly("Yes", "No", "Don't Know")
      .inOrder()
  }

  @Test
  fun questionnaireItem_hiddenExtensionTrue_doNotCreateQuestionnaireItemView() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item-1"
            text = "a question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addExtension().apply {
              url = EXTENSION_HIDDEN_URL
              setValue(BooleanType(true))
            }
          }
        )
      }

    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, serializedQuestionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList()).isEmpty()
  }

  @Test
  fun questionnaireItem_hiddenExtensionFalse_shouldCreateQuestionnaireItemView() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addExtension().apply {
              url = EXTENSION_HIDDEN_URL
              setValue(BooleanType(false))
            }
            addInitial().apply { value = BooleanType(true) }
          }
        )
      }
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, serializedQuestionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList().single().questionnaireItem.linkId)
      .isEqualTo("a-boolean-item-1")
  }

  @Test
  fun questionnaireItem_hiddenExtensionValueIsNotBoolean_shouldCreateQuestionnaireItemView() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addExtension().apply {
              url = EXTENSION_HIDDEN_URL
              setValue(IntegerType(1))
            }
            addInitial().apply { value = BooleanType(true) }
          }
        )
      }
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, serializedQuestionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList().single().questionnaireItem.linkId)
      .isEqualTo("a-boolean-item-1")
  }

  @Test
  fun `should return questionnaire response without disabled questions`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(false) }
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          }
        )
      }
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

    val viewModel = QuestionnaireViewModel(context, state)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-1"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `should return questionnaire response with enabled questions`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(true) }
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          }
        )
      }
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

    val viewModel = QuestionnaireViewModel(context, state)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-1"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        )
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { linkId = "question-2" }
        )
      }
    )
  }

  @Test
  fun nestedDisplayItem_parentQuestionItemIsGroup_createQuestionnaireStateItem() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "parent-question"
            text = "parent question text"
            type = Questionnaire.QuestionnaireItemType.GROUP
            item =
              listOf(
                Questionnaire.QuestionnaireItemComponent().apply {
                  linkId = "nested-display-question"
                  text = "subtitle text"
                  type = Questionnaire.QuestionnaireItemType.DISPLAY
                }
              )
          }
        )
      }
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList().last().questionnaireItem.linkId)
      .isEqualTo("nested-display-question")
  }

  @Test
  fun `nested display item with instructions code should not be created as questionnaire state item`() =
      runBlocking {
    val displayCategoryExtension =
      Extension().apply {
        url = EXTENSION_DISPLAY_CATEGORY_URL
        setValue(
          CodeableConcept().apply {
            coding =
              listOf(
                Coding().apply {
                  code = INSTRUCTIONS
                  system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
                }
              )
          }
        )
      }

    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "parent-question"
            text = "parent question text"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            item =
              listOf(
                Questionnaire.QuestionnaireItemComponent().apply {
                  linkId = "nested-display-question"
                  text = "subtitle text"
                  type = Questionnaire.QuestionnaireItemType.DISPLAY
                  extension = listOf(displayCategoryExtension)
                }
              )
          }
        )
      }
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList().last().questionnaireItem.linkId)
      .isEqualTo("parent-question")
  }

  @Test
  fun `nested display item with flyover code should not be created as questionnaire state item`() =
      runBlocking {
    val itemControlExtensionWithFlyOverCode =
      Extension().apply {
        url = EXTENSION_ITEM_CONTROL_URL
        setValue(
          CodeableConcept().apply {
            coding =
              listOf(
                Coding().apply {
                  code = "flyover"
                  system = EXTENSION_ITEM_CONTROL_SYSTEM
                }
              )
          }
        )
      }

    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "parent-question"
            text = "parent question text"
            type = Questionnaire.QuestionnaireItemType.STRING
            item =
              listOf(
                Questionnaire.QuestionnaireItemComponent().apply {
                  linkId = "nested-display-question"
                  text = "flyover text"
                  type = Questionnaire.QuestionnaireItemType.DISPLAY
                  extension = listOf(itemControlExtensionWithFlyOverCode)
                }
              )
          }
        )
      }
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList().last().questionnaireItem.linkId)
      .isEqualTo("parent-question")
  }

  @Test
  fun `setShowSubmitButtonFlag() to false should not show submit button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.setShowSubmitButtonFlag(false)
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showSubmitButton).isFalse()
    }
  }

  @Test
  fun `setShowSubmitButtonFlag() to true should show submit button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.setShowSubmitButtonFlag(true)
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showSubmitButton).isTrue()
    }
  }

  @Test
  fun `state has review feature and submit button to true should show submit button when moved to review page`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      viewModel.setShowSubmitButtonFlag(true)
      viewModel.setReviewMode(true)
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showSubmitButton).isTrue()
    }
  }

  @Test
  fun `state has no review feature should not show review button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = false)
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showReviewButton).isFalse()
    }
  }

  @Test
  fun `state has review feature should show review button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showReviewButton).isTrue()
    }
  }

  @Test
  fun `state has review feature and show review page first should not show review button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel =
        createQuestionnaireViewModel(
          questionnaire,
          enableReviewPage = true,
          showReviewPageFirst = true
        )
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showReviewButton).isFalse()
    }
  }

  @Test
  fun `state has no review feature but show review page first should not show review button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel =
        createQuestionnaireViewModel(
          questionnaire,
          enableReviewPage = false,
          showReviewPageFirst = true
        )
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showReviewButton).isFalse()
    }
  }

  @Test
  fun `paginated questionnaire with no review feature should not show review button when moved to next page`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = false)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(pagination?.showReviewButton).isFalse()
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `paginated questionnaire with review feature should show review button when moved to next page`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
    var pagination: QuestionnairePagination? = null
    val observer =
      launch(Dispatchers.Main) {
        viewModel.questionnaireStateFlow.collect { pagination = it.pagination }
      }
    try {
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      viewModel.goToNextPage()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

      assertThat(pagination?.showReviewButton).isTrue()
    } finally {
      observer.cancel()
      ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
      observer.cancelAndJoin()
    }
  }

  @Test
  fun `toggle review mode to false should show review button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      viewModel.setReviewMode(false)
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showReviewButton).isTrue()
    }
  }

  @Test
  fun `toggle review mode to true should not show review button`() {
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            }
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      viewModel.setReviewMode(true)
      assertThat(viewModel.questionnaireStateFlow.first().pagination.showReviewButton).isFalse()
    }
  }

  private fun createQuestionnaireViewModel(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse? = null,
    enableReviewPage: Boolean = false,
    showReviewPageFirst: Boolean = false
  ): QuestionnaireViewModel {
    if (questionnaireSource == QuestionnaireSource.STRING) {
      state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))
    } else if (questionnaireSource == QuestionnaireSource.URI) {
      val questionnaireFile = File(context.cacheDir, "test_questionnaire")
      questionnaireFile.outputStream().bufferedWriter().use {
        printer.encodeResourceToWriter(questionnaire, it)
      }
      val questionnaireUri = Uri.fromFile(questionnaireFile)
      state.set(EXTRA_QUESTIONNAIRE_JSON_URI, questionnaireUri)
      shadowOf(context.contentResolver)
        .registerInputStream(questionnaireUri, questionnaireFile.inputStream())
    }

    questionnaireResponse?.let {
      if (questionnaireResponseSource == QuestionnaireResponseSource.STRING) {
        state.set(
          EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING,
          printer.encodeResourceToString(questionnaireResponse)
        )
      } else if (questionnaireResponseSource == QuestionnaireResponseSource.URI) {
        val questionnaireResponseFile = File(context.cacheDir, "test_questionnaire_response")
        questionnaireResponseFile.outputStream().bufferedWriter().use {
          printer.encodeResourceToWriter(questionnaireResponse, it)
        }
        val questionnaireResponseUri = Uri.fromFile(questionnaireResponseFile)
        state.set(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI, questionnaireResponseUri)
        shadowOf(context.contentResolver)
          .registerInputStream(questionnaireResponseUri, questionnaireResponseFile.inputStream())
      }
    }
    enableReviewPage.let { state.set(EXTRA_ENABLE_REVIEW_PAGE, it) }
    showReviewPageFirst.let { state.set(EXTRA_SHOW_REVIEW_PAGE_FIRST, it) }
    return QuestionnaireViewModel(context, state)
  }

  private suspend fun QuestionnaireViewModel.getQuestionnaireItemViewItemList() =
    questionnaireStateFlow.first().items

  private companion object {
    const val CODE_SYSTEM_YES_NO = "http://terminology.hl7.org/CodeSystem/v2-0136"

    private val paginationExtension =
      Extension().apply {
        url = EXTENSION_ITEM_CONTROL_URL
        setValue(
          CodeableConcept(
            Coding().apply {
              code = "page"
              system = EXTENSION_ITEM_CONTROL_SYSTEM
            }
          )
        )
      }

    val printer: IParser = FhirContext.forR4().newJsonParser()

    fun assertResourceEquals(r1: IBaseResource, r2: IBaseResource) {
      assertThat(printer.encodeResourceToString(r1)).isEqualTo(printer.encodeResourceToString(r2))
    }

    @JvmStatic
    @Parameters
    fun parameters() =
      listOf(
        arrayOf(QuestionnaireSource.URI, QuestionnaireResponseSource.URI),
        arrayOf(QuestionnaireSource.URI, QuestionnaireResponseSource.STRING),
        arrayOf(QuestionnaireSource.STRING, QuestionnaireResponseSource.URI),
        arrayOf(QuestionnaireSource.STRING, QuestionnaireResponseSource.STRING)
      )
  }
}

/** The source of questionnaire. */
enum class QuestionnaireSource {
  STRING,
  URI
}

/** The source of questionnaire-response. */
enum class QuestionnaireResponseSource {
  STRING,
  URI
}
