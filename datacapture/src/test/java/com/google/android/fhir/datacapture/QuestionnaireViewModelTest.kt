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
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_ENABLE_REVIEW_PAGE
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_READ_ONLY
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_REVIEW_PAGE_FIRST
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_SUBMIT_BUTTON
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.android.fhir.testing.FhirEngineProviderTestRule
import com.google.common.truth.Truth.assertThat
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.test.assertFailsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

/**
 * In local unit tests, the Main dispatcher that wraps the Android UI thread will be unavailable, as
 * these tests are executed on a local JVM and not an Android device.
 * [androidx.lifecycle.viewModelScope], which we use in [QuestionnaireViewModel], uses a hardcoded
 * Main dispatcher under the hood, which needs to be replaced with a TestDispatcher.
 *
 * See: https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher
 *
 * The TestDispatcher we create is then used to launch a job to collect the results from
 * [QuestionnaireViewModel.questionnaireStateFlow]
 *
 * See: https://developer.android.com/kotlin/flow/test#statein
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
  val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
  override fun starting(description: Description) {
    Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description) {
    Dispatchers.resetMain()
  }
}

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class QuestionnaireViewModelTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private lateinit var fhirEngine: FhirEngine
  private lateinit var state: SavedStateHandle
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Before
  fun setUp() {
    fhirEngine = FhirEngineProvider.getInstance(context)
    state = SavedStateHandle()
    check(
      ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
        is DataCaptureConfig.Provider
    ) { "Few tests require a custom application class that implements DataCaptureConfig.Provider" }
    ReflectionHelpers.setStaticField(DataCapture::class.java, "configuration", null)
  }

  // Test cases for initialization

  @Test
  fun `init should throw an exception if no questionnaire is provided`() {
    val errorMessage =
      assertFailsWith<IllegalStateException> { QuestionnaireViewModel(context, state) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Neither EXTRA_QUESTIONNAIRE_JSON_URI nor EXTRA_QUESTIONNAIRE_JSON_STRING is supplied."
      )
  }

  @Test
  fun `should copy questionnaire URL if no response is provided`() {
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
  fun `should copy questions if no response is provided`() {
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
  fun `should copy nested questions if no response is provided`() {
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
  fun `should throw an exception for questions without type`() {
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
  fun `should set initial value`() {
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
  fun `should throw an exception for group items with an initial value`() {
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
  fun `should throw an exception for display items with an initial value`() {
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
  fun `should set the first of multiple initial values`() {
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
  fun `should throw an exception for multiple initial values for a non-repeating item`() {
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
  fun `should not throw exception for matching URL`() {
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

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun `should throw exception for non-matching URLs`() {
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
  fun `should throw exception for non-matching question linkIds`() {
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
  fun `should throw exception for non-matching question types`() {
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
  fun `should not throw exception with repeated questions with multiple answers`() {
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
  fun `should throw an exception with non-repeated questions with multiple answers`() {
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
  fun `should not throw exception for questions nested under group`() {
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
                  }
                )
              }
            )
          }
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun `should not throw exception for questions nested under question`() {
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

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun `should not throw exception for non primitive type`() {
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
            text = "Basic question"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = testOption1
              }
            )
          }
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun `should override initial value with empty answer`() {
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

  // Test cases for getQuestionnaireResponse function

  @Test
  fun `getQuestionnaireResponse() should copy question text`() {
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
  fun `getQuestionnaireResponse() should copy translated question text`() {
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
  fun `getQuestionnaireResponse() should copy question text for nested questions`() {
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
  fun `should return questionnaire response without disabled questions`() = runTest {
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
  fun `should return questionnaire response with enabled questions`() = runTest {
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

  @Test // https://github.com/google/android-fhir/issues/1664
  fun `should skip disabled questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
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
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "question-3"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(false)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          }
        )
      }
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
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
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-3"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        )
      }
    state.set(
      EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING,
      printer.encodeResourceToString(questionnaireResponse)
    )

    val viewModel = QuestionnaireViewModel(context, state)

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun `should disable all questions in a chain of dependent questions after top question is disabled`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "question-1"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
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
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "question-3"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              addEnableWhen().apply {
                answer = BooleanType(true)
                question = "question-2"
                operator = Questionnaire.QuestionnaireItemOperator.EQUAL
              }
            }
          )
        }

      val questionnaireResponse =
        QuestionnaireResponse().apply {
          id = "a-questionnaire-response"
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
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-2"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            }
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-3"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            }
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

      viewModel.runViewModelBlocking {
        var items = viewModel.getQuestionnaireItemViewItemList().map { it.asQuestion() }
        assertThat(items.map { it.questionnaireItem.linkId })
          .containsExactly("question-1", "question-2", "question-3")

        items.first { it.questionnaireItem.linkId == "question-1" }.clearAnswer()

        items = viewModel.getQuestionnaireItemViewItemList().map { it.asQuestion() }
        assertThat(items.map { it.questionnaireItem.linkId }).containsExactly("question-1")
      }
    }

  @Test
  fun `should restore previous state in a chain of dependent question items when item is disabled and enabled`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "question-1"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
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
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "question-3"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              addEnableWhen().apply {
                answer = BooleanType(true)
                question = "question-2"
                operator = Questionnaire.QuestionnaireItemOperator.EQUAL
              }
            }
          )
        }

      val questionnaireResponse =
        QuestionnaireResponse().apply {
          id = "a-questionnaire-response"
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
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-2"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            }
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-3"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                }
              )
            }
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

      viewModel.runViewModelBlocking {
        val items = viewModel.getQuestionnaireItemViewItemList().map { it.asQuestion() }
        // Clearing the answer disables question-2 that in turn disables question-3.
        items.first { it.questionnaireItem.linkId == "question-1" }.clearAnswer()

        assertResourceEquals(
          viewModel.getQuestionnaireResponse(),
          QuestionnaireResponse().apply {
            id = "a-questionnaire-response"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "question-1"
              }
            )
          }
        )

        // Setting the answer of  "question-1" to true should enable question-2 that in turn enables
        // question-3 and restore their previous states.
        items
          .first { it.questionnaireItem.linkId == "question-1" }
          .setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            }
          )

        assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
      }
    }

  // Test cases for state flow

  @Test
  fun stateHasQuestionnaireResponse_lessItemsInQuestionnaireResponse_shouldAddTheMissingItem() =
    runTest {
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

      val questionnaireViewModel =
        createQuestionnaireViewModel(questionnaire, questionnaireResponse)

      val questionnaireItemViewItem = questionnaireViewModel.questionnaireStateFlow.first()
      assertThat(questionnaireItemViewItem.items.first().asQuestion().questionnaireItem.linkId)
        .isEqualTo(questionnaireResponseWithMissingItem.item.first().linkId)
      assertThat(
          questionnaireItemViewItem.items
            .single()
            .asQuestion()
            .answers
            .single()
            .valueBooleanType.booleanValue()
        )
        .isTrue()
    }

  @Test
  fun stateHasQuestionnaireResponse_lessItemsInQuestionnaireResponse_shouldCopyAnswer() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "q1"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(BooleanType(false)))
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "q2"
            text = "Another basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(BooleanType(false)))
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "q3"
            text = "Another basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(BooleanType(false)))
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "q2"
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
    val questionnaireItemViewItemList = questionnaireViewModel.questionnaireStateFlow.first().items

    // Answer to first question should be created from questionnaire
    val questionnaireItemViewItem1 = questionnaireItemViewItemList[0].asQuestion()
    assertThat(questionnaireItemViewItem1.questionnaireItem.linkId).isEqualTo("q1")
    assertThat(questionnaireItemViewItem1.answers.single().valueBooleanType.booleanValue())
      .isFalse()

    // Answer to second question should be copied from questionnaire response
    val questionnaireItemViewItem2 = questionnaireItemViewItemList[1].asQuestion()
    assertThat(questionnaireItemViewItem2.questionnaireItem.linkId).isEqualTo("q2")
    assertThat(questionnaireItemViewItem2.answers.single().valueBooleanType.booleanValue()).isTrue()

    // Answer to third question should be created from questionnaire
    val questionnaireItemViewItem3 = questionnaireItemViewItemList[2].asQuestion()
    assertThat(questionnaireItemViewItem3.questionnaireItem.linkId).isEqualTo("q3")
    assertThat(questionnaireItemViewItem3.answers.single().valueBooleanType.booleanValue())
      .isFalse()
  }

  @Test
  fun `should emit questionnaire state flow`() = runTest {
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

    val firstQuestionnaireItem = questionnaireItemViewItemList[0].asQuestion().questionnaireItem
    assertThat(firstQuestionnaireItem.linkId).isEqualTo("a-link-id")
    assertThat(firstQuestionnaireItem.text).isEqualTo("Basic questions")
    assertThat(firstQuestionnaireItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.GROUP)

    val secondQuestionnaireItem = questionnaireItemViewItemList[1].asQuestion().questionnaireItem
    assertThat(secondQuestionnaireItem.linkId).isEqualTo("another-link-id")
    assertThat(secondQuestionnaireItem.text).isEqualTo("Name?")
    assertThat(secondQuestionnaireItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.STRING)
  }

  @Test
  fun `should emit questionnaire state flow without initial validation`() = runTest {
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
    assertThat(questionnaireItemViewItemList.single().asQuestion().validationResult)
      .isEqualTo(NotValidated)
  }

  @Test
  fun `should emit questionnaire state flow with validation for modified items`() = runTest {
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
    viewModel.runViewModelBlocking {
      val question = viewModel.getQuestionnaireItemViewItemList().single().asQuestion()
      question.clearAnswer()

      assertThat(
          viewModel.getQuestionnaireItemViewItemList().single().asQuestion().validationResult
        )
        .isEqualTo(Invalid(listOf("Missing answer for required field.")))
    }
  }

  @Test
  fun `should emit questionnaire state flow without disabled questions`() = runTest {
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

    assertThat(
        viewModel.getQuestionnaireItemViewItemList().single().asQuestion().questionnaireItem.linkId
      )
      .isEqualTo("question-1")
  }

  @Test
  fun `should emit questionnaire state flow with enabled questions`() = runTest {
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
    assertThat(questionnaireItemViewItemList[0].asQuestion().questionnaireItem.linkId)
      .isEqualTo("question-1")
    assertThat(questionnaireItemViewItemList[1].asQuestion().questionnaireItem.linkId)
      .isEqualTo("question-2")
  }

  @Test
  fun `should emit questionnaire state flow without hidden questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item-1"
            text = "a question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addExtension(hiddenExtension)
          }
        )
      }

    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, serializedQuestionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList()).isEmpty()
  }

  @Test
  fun `should emit questionnaire state flow with non-hidden questions`() = runTest {
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

    assertThat(
        viewModel.getQuestionnaireItemViewItemList().single().asQuestion().questionnaireItem.linkId
      )
      .isEqualTo("a-boolean-item-1")
  }

  @Test
  fun `should emit questionnaire state flow with hidden extension without valid value`() = runTest {
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

    assertThat(
        viewModel.getQuestionnaireItemViewItemList().single().asQuestion().questionnaireItem.linkId
      )
      .isEqualTo("a-boolean-item-1")
  }

  // Test cases for user interaction

  @Test
  fun questionnaireHasNestedItem_ofTypeGroup_shouldNestItemWithinItem() = runTest {
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

    viewModel
      .getQuestionnaireItemViewItemList()[1]
      .asQuestion()
      .setAnswer(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          this.value = valueBooleanType.setValue(false)
        }
      )

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun questionnaireHasNestedItem_ofTypeRepeatedGroup_shouldNestMultipleItems() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "repeated-group-a"
            text = "Group question A"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-item-a"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "another-nested-item-a"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "repeated-group-b"
            text = "Group question B"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-item-b"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "another-nested-item-b"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    fun repeatedGroupA() =
      viewModel.getQuestionnaireItemViewItemList().single {
        it.asQuestion().questionnaireItem.linkId == "repeated-group-a"
      }

    fun repeatedGroupB() =
      viewModel.getQuestionnaireItemViewItemList().single {
        it.asQuestion().questionnaireItem.linkId == "repeated-group-b"
      }
    viewModel.runViewModelBlocking {
      // Calling addAnswer out of order should not result in the answers in the response being out
      // of order; all of the answers to repeated-group-a should come before repeated-group-b.
      repeat(times = 2) {
        repeatedGroupA()
          .asQuestion()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              item =
                repeatedGroupA()
                  .asQuestion()
                  .questionnaireItem.getNestedQuestionnaireResponseItems()
            }
          )
        repeatedGroupB()
          .asQuestion()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              item =
                repeatedGroupB()
                  .asQuestion()
                  .questionnaireItem.getNestedQuestionnaireResponseItems()
            }
          )
      }

      assertThat(
          viewModel.getQuestionnaireItemViewItemList().map {
            it.asQuestion().questionnaireItem.linkId
          }
        )
        .containsExactly(
          "repeated-group-a",
          "nested-item-a",
          "another-nested-item-a",
          "nested-item-a",
          "another-nested-item-a",
          "repeated-group-b",
          "nested-item-b",
          "another-nested-item-b",
          "nested-item-b",
          "another-nested-item-b"
        )
        .inOrder()

      assertResourceEquals(
        actual = viewModel.getQuestionnaireResponse(),
        expected =
          QuestionnaireResponse().apply {
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "repeated-group-a"
                text = "Group question A"
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-item-a"
                    text = "Basic question"
                  }
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-a"
                    text = "Basic question"
                  }
                )
              }
            )
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "repeated-group-a"
                text = "Group question A"
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-item-a"
                    text = "Basic question"
                  }
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-a"
                    text = "Basic question"
                  }
                )
              }
            )
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "repeated-group-b"
                text = "Group question B"
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-item-b"
                    text = "Basic question"
                  }
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-b"
                    text = "Basic question"
                  }
                )
              }
            )
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "repeated-group-b"
                text = "Group question B"
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-item-b"
                    text = "Basic question"
                  }
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-b"
                    text = "Basic question"
                  }
                )
              }
            )
          }
      )
    }
  }

  @Test
  @Ignore("https://github.com/google/android-fhir/issues/487")
  fun questionnaireHasNestedItem_notOfTypeGroup_shouldNestItemWithinAnswerItem() = runTest {
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

    viewModel
      .getQuestionnaireItemViewItemList()[0]
      .asQuestion()
      .setAnswer(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          this.value = valueBooleanType.setValue(false)
        }
      )
    viewModel
      .getQuestionnaireItemViewItemList()[1]
      .asQuestion()
      .setAnswer(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          this.value = valueBooleanType.setValue(false)
        }
      )

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  // Test cases for pagination and navigation

  @Test
  fun `should show questionnaire items in the active page in a paginated questionnaire`() =
    runTest {
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
      assertThat((state.displayMode as DisplayMode.EditMode).pagination)
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = false)
              ),
            currentPageIndex = 0
          )
        )
      assertThat(state.items).hasSize(2)
      state.items[0].asQuestion().questionnaireItem.let { groupItem ->
        assertThat(groupItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.GROUP)
        assertThat(groupItem.linkId).isEqualTo("page1")
      }
      state.items[1].asQuestion().questionnaireItem.let { questionItem ->
        assertThat(questionItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.BOOLEAN)
        assertThat(questionItem.linkId).isEqualTo("page1-1")
        assertThat(questionItem.text).isEqualTo("Question on page 1")
      }
    }

  @Test
  fun `should go to next page in a paginated questionnaire`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()

      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = false)
              ),
            currentPageIndex = 1,
            showSubmitButton = true
          )
        )
    }
  }

  @Test
  fun `should go to previous page in a paginated questionnaire`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = false)
              ),
            currentPageIndex = 0
          )
        )
    }
  }

  @Test
  fun `should skip disabled page in a paginated questionnaire`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = false, hidden = false),
                QuestionnairePage(2, enabled = true, hidden = false),
              ),
            currentPageIndex = 2,
            showSubmitButton = true
          )
        )
    }
  }

  @Test
  fun `should skip first page if it is hidden in a paginated questionnaire`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addExtension(hiddenExtension)
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
    viewModel.runViewModelBlocking {
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = true),
                QuestionnairePage(1, enabled = true, hidden = false),
                QuestionnairePage(2, enabled = true, hidden = false)
              ),
            currentPageIndex = 1
          )
        )
    }
  }

  @Test
  fun `should skip hidden page in a paginated questionnaire`() = runTest {
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
            addExtension(hiddenExtension)
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              }
            )
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = true),
                QuestionnairePage(2, enabled = true, hidden = false)
              ),
            currentPageIndex = 2,
            showSubmitButton = true
          )
        )
    }
  }

  @Test
  fun `should allow user to move forward using prior entry-mode`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.PRIOR_EDIT)
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
            showSubmitButton = true
          )
        )
    }
  }

  @Test
  fun `should allow user to move forward and back using prior entry-mode`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.PRIOR_EDIT)
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0
          )
        )
    }
  }

  @Test
  fun `should not allow user to move forward using prior entry-mode`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0
          )
        )
    }
  }

  @Test
  fun `should allow user to move forward using random entry-mode`() = runTest {
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
    assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.last()?.index)
  }

  @Test
  fun `should allow user to move forward and back using random entry-mode`() = runTest {
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
    assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.first()?.index)
  }

  @Test
  fun `should allow user to move forward when no entry-mode is defined`() = runTest {
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
    assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.last()?.index)
  }

  @Test
  fun `should allow user to move forward and back when no entry-mode is defined`() = runTest {
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
    assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.first()?.index)
  }

  @Test
  fun `should allow user to move forward only using sequential entry-mode`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.SEQUENTIAL)
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
            showSubmitButton = true
          )
        )
    }
  }

  @Test
  fun `should not allow user to move forward using sequential entry-mode`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0
          )
        )
    }
  }

  @Test
  fun `should not allow user to move backward only using sequential entry-mode`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
            showSubmitButton = true
          )
        )
    }
  }

  // Test cases for answer value set

  @Test
  fun questionnaire_resolveContainedAnswerValueSet() = runTest {
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
  fun questionnaire_resolveAnswerValueSetExternalResolved() = runTest {
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

  // Test cases for nested display items

  @Test
  fun nestedDisplayItem_parentQuestionItemIsGroup_createQuestionnaireStateItem() = runTest {
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

    assertThat(
        viewModel.getQuestionnaireItemViewItemList().last().asQuestion().questionnaireItem.linkId
      )
      .isEqualTo("nested-display-question")
  }

  @Test
  fun `nested display item with instructions code should not be created as questionnaire state item`() =
    runTest {
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

      assertThat(
          viewModel.getQuestionnaireItemViewItemList().last().asQuestion().questionnaireItem.linkId
        )
        .isEqualTo("parent-question")
    }

  @Test
  fun `nested display item with flyover code should not be created as questionnaire state item`() =
    runTest {
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

      assertThat(
          viewModel.getQuestionnaireItemViewItemList().last().asQuestion().questionnaireItem.linkId
        )
        .isEqualTo("parent-question")
    }

  @Test
  fun `nested display item with help code should not be created as questionnaire state item`() =
    runTest {
      val itemControlExtensionWithHelpCode =
        Extension().apply {
          url = EXTENSION_ITEM_CONTROL_URL
          setValue(
            CodeableConcept().apply {
              coding =
                listOf(
                  Coding().apply {
                    code = DisplayItemControlType.HELP.extensionCode
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
                    text = "help description"
                    type = Questionnaire.QuestionnaireItemType.DISPLAY
                    extension = listOf(itemControlExtensionWithHelpCode)
                  }
                )
            }
          )
        }
      state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

      val viewModel = QuestionnaireViewModel(context, state)

      assertThat(
          viewModel.getQuestionnaireItemViewItemList().last().asQuestion().questionnaireItem.linkId
        )
        .isEqualTo("parent-question")
    }

  // Test cases for expressions

  @Test
  fun `resolveAnswerExpression() should return questionnaire item answer options for answer expression and choice column`() =
    runTest {
      val practitioner =
        Practitioner().apply {
          id = UUID.randomUUID().toString()
          active = true
          addName(HumanName().apply { this.family = "John" })
        }
      ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
        .dataCaptureConfiguration = DataCaptureConfig(xFhirQueryResolver = { listOf(practitioner) })

      val questionnaire =
        Questionnaire().apply {
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a"
              text = "answer expression question text"
              type = Questionnaire.QuestionnaireItemType.REFERENCE
              extension =
                listOf(
                  Extension(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression",
                    Expression().apply {
                      this.expression = "Practitioner?active=true"
                      this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
                    }
                  ),
                  Extension(
                      "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn"
                    )
                    .apply {
                      this.addExtension(Extension("path", StringType("id")))
                      this.addExtension(Extension("label", StringType("name")))
                      this.addExtension(Extension("forDisplay", BooleanType(true)))
                    }
                )
            }
          )
        }
      state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

      val viewModel = QuestionnaireViewModel(context, state)
      val answerOptions = viewModel.resolveAnswerExpression(questionnaire.itemFirstRep)

      assertThat(answerOptions.first().valueReference.reference)
        .isEqualTo("Practitioner/${practitioner.logicalId}")
    }

  @Test
  fun `resolveAnswerExpression() should throw exception when XFhirQueryResolver is not provided`() {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a"
            text = "answer expression question text"
            type = Questionnaire.QuestionnaireItemType.REFERENCE
            extension =
              listOf(
                Extension(
                  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression",
                  Expression().apply {
                    this.expression = "Practitioner?active=true"
                    this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
                  }
                ),
                Extension(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn"
                  )
                  .apply {
                    this.addExtension(Extension("path", StringType("id")))
                    this.addExtension(Extension("label", StringType("name")))
                    this.addExtension(Extension("forDisplay", BooleanType(true)))
                  }
              )
          }
        )
      }
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))
    val viewModel = QuestionnaireViewModel(context, state)
    val exception =
      assertThrows(null, IllegalStateException::class.java) {
        runTest { viewModel.resolveAnswerExpression(questionnaire.itemFirstRep) }
      }
    assertThat(exception.message)
      .isEqualTo(
        "XFhirQueryResolver cannot be null. Please provide the XFhirQueryResolver via DataCaptureConfig."
      )
  }
  // Test cases for submit button

  @Test
  fun `EXTRA_SHOW_SUBMIT_BUTTON set to false should not show submit button`() = runTest {
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
    val viewModel = createQuestionnaireViewModel(questionnaire, showSubmitButton = false)
    assertThat(
        (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.EditMode)
          .pagination.showSubmitButton
      )
      .isFalse()
  }

  @Test
  fun `EXTRA_SHOW_SUBMIT_BUTTON set to true should show submit button`() = runTest {
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
    val viewModel = createQuestionnaireViewModel(questionnaire, showSubmitButton = true)
    assertThat(
        (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.EditMode)
          .pagination.showSubmitButton
      )
      .isTrue()
  }

  @Test
  fun `EXTRA_SHOW_SUBMIT_BUTTON not setting should show submit button`() = runTest {
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
    val viewModel = createQuestionnaireViewModel(questionnaire, showSubmitButton = null)
    assertThat(
        (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.EditMode)
          .pagination.showSubmitButton
      )
      .isTrue()
  }

  // Test cases for review mode

  @Test
  fun `state has review feature and submit button to true should move to review page`() {
    runTest {
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
          showSubmitButton = true
        )
      viewModel.setReviewMode(true)
      assertThat(viewModel.questionnaireStateFlow.first().displayMode)
        .isInstanceOf(DisplayMode.ReviewMode::class.java)
      assertThat(viewModel.questionnaireStateFlow.first().displayMode)
        .isEqualTo(DisplayMode.ReviewMode(showEditButton = true, showSubmitButton = true))
    }
  }

  @Test
  fun `state has no review feature should not show review button`() {
    runTest {
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
      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.EditMode)
            .pagination.showReviewButton
        )
        .isFalse()
    }
  }

  @Test
  fun `state has review feature should show review button`() {
    runTest {
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
      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.EditMode)
            .pagination.showReviewButton
        )
        .isTrue()
    }
  }

  @Test
  fun `state has review feature and show review page first should be in review mode`() {
    runTest {
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

      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.ReviewMode)
            .showEditButton
        )
        .isTrue()
    }
  }

  @Test
  fun `state has no review feature but show review page first should not show review button`() {
    runTest {
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
      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.EditMode)
            .pagination.showReviewButton
        )
        .isFalse()
    }
  }

  @Test
  fun `paginated questionnaire with no review feature should not show review button when moved to next page`() =
    runTest {
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
      viewModel.runViewModelBlocking {
        viewModel.goToNextPage()
        assertThat(
            (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode)
              .pagination.showReviewButton
          )
          .isFalse()
      }
    }

  @Test
  fun `paginated questionnaire with no review feature should not show review button when last page is hidden`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addExtension(hiddenExtension)
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

      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode)
            .pagination.showReviewButton
        )
        .isFalse()
    }

  @Test
  fun `paginated questionnaire with review feature should show review button when moved to next page`() =
    runTest {
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
      viewModel.runViewModelBlocking {
        viewModel.goToNextPage()
        assertThat(
            (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode)
              .pagination.showReviewButton
          )
          .isTrue()
      }
    }

  @Test
  fun `paginated questionnaire with review feature should show review button when last page is hidden`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addExtension(hiddenExtension)
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

      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode)
            .pagination.showReviewButton
        )
        .isTrue()
    }

  @Test
  fun `toggle review mode to false should show review button`() {
    runTest {
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
      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.EditMode)
            .pagination.showReviewButton
        )
        .isTrue()
    }
  }

  @Test
  fun `toggle review mode to true should show edit button only`() {
    runTest {
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

      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.ReviewMode)
            .showEditButton
        )
        .isTrue()
    }
  }

  // Read-only mode

  @Test
  fun `read-only mode should not show edit button`() {
    runTest {
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
      val viewModel = createQuestionnaireViewModel(questionnaire, readOnlyMode = true)

      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.ReviewMode)
            .showEditButton
        )
        .isFalse()
    }
  }

  // Other test cases

  @Test
  fun `should calculate value on start for questionnaire item with calculated expression extension`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  }
                )
              }
            }
          )
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.QUANTITY
              addInitial(
                Questionnaire.QuestionnaireItemInitialComponent(Quantity.fromUcum("1", "year"))
              )
            }
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)

      assertThat(
          viewModel
            .getQuestionnaireResponse()
            .item
            .single { it.linkId == "a-birthdate" }
            .answerFirstRep.value.asStringValue()
        )
        .isEqualTo(DateType(Date()).apply { add(Calendar.YEAR, -1) }.asStringValue())

      assertThat(
          viewModel
            .getQuestionnaireResponse()
            .item
            .single { it.linkId == "a-age-years" }
            .answerFirstRep.valueQuantity.value.toString()
        )
        .isEqualTo("1")
    }

  @Test
  fun `should calculate value on change for questionnaire item with calculated expression extension`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  }
                )
              }
            }
          )
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.INTEGER
            }
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)

      val birthdateItem =
        viewModel
          .getQuestionnaireItemViewItemList()
          .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "a-birthdate" }
          .asQuestion()

      assertThat(birthdateItem.getQuestionnaireResponseItem().answer).isEmpty()

      viewModel
        .getQuestionnaireItemViewItemList()
        .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "a-age-years" }
        .asQuestion()
        .apply {
          this.answersChangedCallback(
            this.questionnaireItem,
            this.getQuestionnaireResponseItem(),
            listOf(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                this.value = Quantity.fromUcum("2", "years")
              }
            ),
            null
          )
        }

      assertThat(
          birthdateItem.getQuestionnaireResponseItem().answer.first().valueDateType.valueAsString
        )
        .isEqualTo(DateType(Date()).apply { add(Calendar.YEAR, -2) }.valueAsString)
    }

  @Test
  fun `should not change value for modified questionnaire items with calculated expression extension`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  }
                )
              }
            }
          )
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.INTEGER
            }
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      val birthdateItem =
        viewModel
          .getQuestionnaireItemViewItemList()
          .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "a-birthdate" }
          .asQuestion()
      val birthdateValue = DateType(Date())
      birthdateItem.apply {
        this.answersChangedCallback(
          this.questionnaireItem,
          this.getQuestionnaireResponseItem(),
          listOf(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              this.value = birthdateValue
            }
          ),
          null
        )
      }

      assertThat(
          birthdateItem.getQuestionnaireResponseItem().answer.first().valueDateType.valueAsString
        )
        .isEqualTo(birthdateValue.valueAsString)

      viewModel
        .getQuestionnaireItemViewItemList()
        .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "a-age-years" }
        .asQuestion()
        .apply {
          this.answersChangedCallback(
            this.questionnaireItem,
            this.getQuestionnaireResponseItem(),
            listOf(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                this.value = Quantity.fromUcum("2", "years")
              }
            ),
            null
          )
        }

      assertThat(
          birthdateItem.getQuestionnaireResponseItem().answer.first().valueDateType.valueAsString
        )
        .isEqualTo(birthdateValue.valueAsString)
    }

  @Test
  fun `should detect cyclic dependency for questionnaire item with calculated expression extension in flat list`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addInitial(
                Questionnaire.QuestionnaireItemInitialComponent(
                  DateType(Date()).apply { add(Calendar.YEAR, -2) }
                )
              )
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  }
                )
              }
            }
          )

          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.INTEGER
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "today().toString().substring(0, 4).toInteger() - %resource.repeat(item).where(linkId='a-birthdate').answer.value.toString().substring(0, 4).toInteger()"
                  }
                )
              }
            }
          )
        }

      val exception =
        assertThrows(null, IllegalStateException::class.java) {
          createQuestionnaireViewModel(questionnaire)
        }
      assertThat(exception.message)
        .isEqualTo(
          "a-birthdate and a-age-years have cyclic dependency in expression based extension"
        )
    }

  @Test
  fun `should detect cyclic dependency for questionnaire item with calculated expression extension in nested list`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "a-birthdate"
                type = Questionnaire.QuestionnaireItemType.DATE
                addInitial(
                  Questionnaire.QuestionnaireItemInitialComponent(
                    DateType(Date()).apply { add(Calendar.YEAR, -2) }
                  )
                )
                addExtension().apply {
                  url = EXTENSION_CALCULATED_EXPRESSION_URL
                  setValue(
                    Expression().apply {
                      this.language = "text/fhirpath"
                      this.expression =
                        "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                    }
                  )
                }
              }
            )
            .addItem()
            .apply {
              linkId = "a.1"
              type = Questionnaire.QuestionnaireItemType.GROUP
            }
            .addItem()
            .apply {
              linkId = "a.1.1"
              type = Questionnaire.QuestionnaireItemType.GROUP
            }
            .addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "a-age-years"
                type = Questionnaire.QuestionnaireItemType.INTEGER
                addExtension().apply {
                  url = EXTENSION_CALCULATED_EXPRESSION_URL
                  setValue(
                    Expression().apply {
                      this.language = "text/fhirpath"
                      this.expression =
                        "today().toString().substring(0, 4).toInteger() - %resource.repeat(item).where(linkId='a-birthdate').answer.value.toString().substring(0, 4).toInteger()"
                    }
                  )
                }
              }
            )
        }

      val exception =
        assertThrows(null, IllegalStateException::class.java) {
          createQuestionnaireViewModel(questionnaire)
        }
      assertThat(exception.message)
        .isEqualTo(
          "a-birthdate and a-age-years have cyclic dependency in expression based extension"
        )
    }

  @Test
  fun `should throw exception on invalid cast inside runViewModelBlocking`() = runTest {
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
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertFailsWith<ClassCastException> {
        (viewModel.questionnaireStateFlow.value as DisplayMode.EditMode).pagination
      }
    }
  }

  private fun createQuestionnaireViewModel(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse? = null,
    enableReviewPage: Boolean = false,
    showReviewPageFirst: Boolean = false,
    readOnlyMode: Boolean = false,
    showSubmitButton: Boolean? = null
  ): QuestionnaireViewModel {
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, printer.encodeResourceToString(questionnaire))

    questionnaireResponse?.let {
      state.set(
        EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING,
        printer.encodeResourceToString(questionnaireResponse)
      )
    }
    enableReviewPage.let { state.set(EXTRA_ENABLE_REVIEW_PAGE, it) }
    showReviewPageFirst.let { state.set(EXTRA_SHOW_REVIEW_PAGE_FIRST, it) }
    readOnlyMode.let { state.set(EXTRA_READ_ONLY, it) }
    showSubmitButton?.let { state.set(EXTRA_SHOW_SUBMIT_BUTTON, it) }

    return QuestionnaireViewModel(context, state)
  }

  private fun QuestionnaireViewModel.getQuestionnaireItemViewItemList() =
    questionnaireStateFlow.value.items

  private fun QuestionnaireItemViewItem.getQuestionnaireResponseItem() =
    ReflectionHelpers.getField<QuestionnaireResponse.QuestionnaireResponseItemComponent>(
      this,
      "questionnaireResponseItem"
    )

  /**
   * Runs code that relies on the [QuestionnaireViewModel.viewModelScope]. Runs on
   * [MainDispatcherRule.testDispatcher], so that `ShadowLooper` idle functions are not necessary.
   */
  private suspend inline fun QuestionnaireViewModel.runViewModelBlocking(
    crossinline block: suspend () -> Unit,
  ) {
    val collectJob =
      viewModelScope.launch(mainDispatcherRule.testDispatcher) { questionnaireStateFlow.collect() }
    block.invoke()
    collectJob.cancel()
  }

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

    private val hiddenExtension =
      Extension().apply {
        url = EXTENSION_HIDDEN_URL
        setValue(BooleanType(true))
      }

    val printer: IParser = FhirContext.forR4().newJsonParser()

    fun <T : IBaseResource> assertResourceEquals(actual: T, expected: T) {
      assertThat(printer.encodeResourceToString(actual))
        .isEqualTo(printer.encodeResourceToString(expected))
    }
  }
}

private fun QuestionnaireAdapterItem.asQuestion(): QuestionnaireItemViewItem {
  assertThat(this).isInstanceOf(QuestionnaireAdapterItem.Question::class.java)
  return (this as QuestionnaireAdapterItem.Question).item
}

private fun QuestionnaireAdapterItem.asQuestionOrNull(): QuestionnaireItemViewItem? =
  (this as? QuestionnaireAdapterItem.Question)?.item
