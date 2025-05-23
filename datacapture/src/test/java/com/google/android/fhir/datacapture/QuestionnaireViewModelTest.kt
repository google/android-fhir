/*
 * Copyright 2023-2024 Google LLC
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
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_ENABLE_REVIEW_PAGE
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_READ_ONLY
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_CANCEL_BUTTON
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_REVIEW_PAGE_FIRST
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_SHOW_SUBMIT_BUTTON
import com.google.android.fhir.datacapture.extensions.CODE_SYSTEM_LAUNCH_CONTEXT
import com.google.android.fhir.datacapture.extensions.DisplayItemControlType
import com.google.android.fhir.datacapture.extensions.EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION
import com.google.android.fhir.datacapture.extensions.EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION
import com.google.android.fhir.datacapture.extensions.EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_CALCULATED_EXPRESSION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_CQF_CALCULATED_VALUE_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_CQF_EXPRESSION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_DISPLAY_CATEGORY_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_ENABLE_WHEN_EXPRESSION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_ENTRY_MODE_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_HIDDEN_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_LAST_LAUNCHED_TIMESTAMP
import com.google.android.fhir.datacapture.extensions.EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT
import com.google.android.fhir.datacapture.extensions.EXTENSION_VARIABLE_URL
import com.google.android.fhir.datacapture.extensions.EntryMode
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.extensions.createNestedQuestionnaireResponseItems
import com.google.android.fhir.datacapture.extensions.entryMode
import com.google.android.fhir.datacapture.extensions.logicalId
import com.google.android.fhir.datacapture.extensions.maxValue
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.MAX_VALUE_EXTENSION_URL
import com.google.android.fhir.datacapture.validation.MIN_VALUE_EXTENSION_URL
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.test.assertFailsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
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
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Assert.assertThrows
import org.junit.Before
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
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class QuestionnaireViewModelTest {

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private lateinit var state: SavedStateHandle
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Before
  fun setUp() {
    state = SavedStateHandle()
    check(
      ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
        is DataCaptureConfig.Provider,
    ) {
      "Few tests require a custom application class that implements DataCaptureConfig.Provider"
    }
    ReflectionHelpers.setStaticField(DataCapture::class.java, "configuration", null)
  }

  // ==================================================================== //
  //                                                                      //
  //             Initialization without Questionnaire Response            //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should throw an exception if no questionnaire is provided`() {
    val errorMessage =
      assertFailsWith<IllegalStateException> { QuestionnaireViewModel(context, state) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Neither EXTRA_QUESTIONNAIRE_JSON_URI nor EXTRA_QUESTIONNAIRE_JSON_STRING is supplied.",
      )
  }

  @Test
  fun `should copy questionnaire URL if no response is provided`() {
    val questionnaire =
      Questionnaire().apply {
        url = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        QuestionnaireResponse().apply {
          this.questionnaire = "http://www.sample-org/FHIR/Resources/Questionnaire/a-questionnaire"
        },
      )
    }
  }

  @Test
  fun `should copy questions if no response is provided`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Yes or no?"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        QuestionnaireResponse().apply {
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-link-id"
              text = "Yes or no?"
            },
          )
        },
      )
    }
  }

  @Test
  fun `should copy nested questions if no response is provided`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Name?"
                type = Questionnaire.QuestionnaireItemType.STRING
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
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
                },
              )
            },
          )
        },
      )
    }
  }

  @Test
  fun `should throw an exception for questions without type`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
          },
        )
      }

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { linkId = "a-link-id" },
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
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().apply {
                  value = BooleanType(false)
                },
              )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        QuestionnaireResponse().apply {
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-link-id"
              text = "Basic question"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(false)
                },
              )
            },
          )
        },
      )
    }
  }

  @Test
  fun `should throw an exception for group items with an initial value`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
              )
          },
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { createQuestionnaireViewModel(questionnaire) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Questionnaire item a-link-id has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial.",
      )
  }

  @Test
  fun `should throw an exception for display items with an initial value`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.DISPLAY
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
              )
          },
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { createQuestionnaireViewModel(questionnaire) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Questionnaire item a-link-id has initial value(s) and is a group or display item. See rule que-8 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial.",
      )
  }

  @Test
  fun `should set all of multiple initial values`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            repeats = true
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
              )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        QuestionnaireResponse().apply {
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-link-id"
              text = "Basic question"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                },
              )
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                },
              )
            },
          )
        },
      )
    }
  }

  @Test
  fun `should throw an exception for multiple initial values for a non-repeating item`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            repeats = false
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
                Questionnaire.QuestionnaireItemInitialComponent().setValue(BooleanType(true)),
              )
          },
        )
      }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { createQuestionnaireViewModel(questionnaire) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Questionnaire item a-link-id can only have multiple initial values for repeating items. See rule que-13 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial.",
      )
  }

  // ==================================================================== //
  //                                                                      //
  //              Initialization with Questionnaire Response              //
  //                                                                      //
  // ==================================================================== //

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

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
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
        "Mismatching Questionnaire http://www.sample-org/FHIR/Resources/Questionnaire/questionnaire-1 and QuestionnaireResponse (for Questionnaire Questionnaire/a-questionnaire)",
      )
  }

  @Test
  fun `should remove response item of non-matching question linkIds`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
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
              },
            )
          },
        )
      }

    val expectedQuestionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
          },
        )
      }

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        createQuestionnaireViewModel(
            questionnaire,
            questionnaireResponse,
          )
          .getQuestionnaireResponse(),
        expectedQuestionnaireResponse,
      )
    }
  }

  @Test
  fun `should throw exception for non-matching question types`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
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
              },
            )
          },
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
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question which allows multiple answers"
            type = Questionnaire.QuestionnaireItemType.STRING
            repeats = true
          },
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
              },
            )
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("string 2")
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
  }

  @Test
  fun `should throw an exception with non-repeated questions with multiple answers`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            repeats = false
          },
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
              },
            )
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              },
            )
          },
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
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Is this true?"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
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
                  },
                )
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
  }

  @Test
  fun `should not throw exception for questions nested under question`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Is this true?"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Name?"
                type = Questionnaire.QuestionnaireItemType.STRING
              },
            )
          },
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
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
                      },
                    )
                  },
                )
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
  }

  @Test
  fun `should not throw exception for repeated group`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "repeated-group"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question"
                addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = BooleanType(true)
                  },
                )
              },
            )
          },
        )
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question"
                addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = BooleanType(false)
                  },
                )
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
  }

  @Test
  fun `should not throw exception for non primitive type`() {
    val testOption1 = Coding("test", "option", "1")
    val testOption2 = Coding("test", "option", "2")

    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            answerOption =
              listOf(
                Questionnaire.QuestionnaireItemAnswerOptionComponent(testOption1),
                Questionnaire.QuestionnaireItemAnswerOptionComponent(testOption2),
              )
          },
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
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
  }

  @Test
  fun `should override initial value with empty answer`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            initial =
              mutableListOf(
                Questionnaire.QuestionnaireItemInitialComponent().apply { value = valueCoding },
              )
          },
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
              },
            )
          },
        )
      }
    createQuestionnaireViewModel(questionnaire, questionnaireResponse)
  }

  @Test
  fun `add empty nested QuestionnaireResponseItemComponent to the group if questions are not answered`() {
    val questionnaireString =
      """
        {
          "resourceType": "Questionnaire",
          "id": "client-registration-sample",
          "item": [
            {
              "linkId": "1",
              "type": "group",
              "item": [
                {
                  "linkId": "1.1",
                  "text": "First Nested Item",
                  "type": "boolean"
                },
                {
                  "linkId": "1.2",
                  "text": "Second Nested Item",
                  "type": "boolean"
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    val questionnaireResponseString =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "1"
            }
          ]
        }
            """
        .trimIndent()

    val expectedResponseString =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "1",
              "item": [
                {
                  "linkId": "1.1",
                  "text": "First Nested Item"
                },
                {
                  "linkId": "1.2",
                  "text": "Second Nested Item"
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = questionnaireString
    state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] = questionnaireResponseString
    val viewModel = QuestionnaireViewModel(context, state)
    runTest {
      val value = viewModel.getQuestionnaireResponse()
      val expectedResponse =
        printer.parseResource(QuestionnaireResponse::class.java, expectedResponseString)
          as QuestionnaireResponse

      assertQuestionnaireResponseEqualsIgnoringTimestamps(value, expectedResponse)
    }
  }

  @Test
  fun `maintain an order while adding empty QuestionnaireResponseItemComponent to the response items`() {
    val questionnaireString =
      """
          {
            "resourceType": "Questionnaire",
            "item": [
              {
                "linkId": "1",
                "type": "group",
                "text": "Repeated Group",
                "repeats": true,
                "item": [
                  {
                    "linkId": "1-1",
                    "type": "date",
                    "extension": [
                      {
                        "url": "http://hl7.org/fhir/StructureDefinition/entryFormat",
                        "valueString": "yyyy-mm-dd"
                      }
                    ]
                  }
                ]
              },
              {
                "linkId": "2",
                "text": "Is this the first visit?",
                "type": "boolean"
              }
            ]
          }
            """
        .trimIndent()

    val questionnaireResponseString =
      """
              {
                "resourceType": "QuestionnaireResponse",
                "item": [
                  {
                    "linkId": "1",
                    "text": "Repeated Group",
                    "item": [
                      {
                        "linkId": "1-1",
                        "answer": [
                          {
                            "valueDate": "2023-06-14"
                          }
                        ]
                      }
                    ]
                  },
                  {
                    "linkId": "1",
                    "text": "Repeated Group",
                    "item": [
                      {
                        "linkId": "1-1",
                        "answer": [
                          {
                            "valueDate": "2023-06-13"
                          }
                        ]
                      }
                    ]
                  }
                ]
              }
            """
        .trimIndent()

    val expectedResponseString =
      """
            {
              "resourceType": "QuestionnaireResponse",
              "item": [
                {
                  "linkId": "1",
                  "text": "Repeated Group",
                  "item": [
                    {
                      "linkId": "1-1",
                      "answer": [
                        {
                          "valueDate": "2023-06-14"
                        }
                      ]
                    }
                  ]
                },
                {
                  "linkId": "1",
                  "text": "Repeated Group",
                  "item": [
                    {
                      "linkId": "1-1",
                      "answer": [
                        {
                          "valueDate": "2023-06-13"
                        }
                      ]
                    }
                  ]
                },
                {
                  "linkId": "2",
                  "text": "Is this the first visit?"
                }
              ]
            }
            """
        .trimIndent()

    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = questionnaireString
    state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] = questionnaireResponseString
    val viewModel = QuestionnaireViewModel(context, state)
    runTest {
      val value = viewModel.getQuestionnaireResponse()
      val expectedResponse =
        printer.parseResource(QuestionnaireResponse::class.java, expectedResponseString)
          as QuestionnaireResponse

      assertQuestionnaireResponseEqualsIgnoringTimestamps(value, expectedResponse)
    }
  }

  @Test
  fun `should add missing response item inside a repeated group`() {
    val questionnaireString =
      """
          {
            "resourceType": "Questionnaire",
            "item": [
              {
                "linkId": "1",
                "type": "group",
                "text": "Repeated Group",
                "repeats": true,
                "item": [
                  {
                    "linkId": "1-1",
                    "type": "date",
                    "extension": [
                      {
                        "url": "http://hl7.org/fhir/StructureDefinition/entryFormat",
                        "valueString": "yyyy-mm-dd"
                      }
                    ]
                  },
                  {
                    "linkId": "1-2",
                    "type": "boolean"
                  }
                ]
              }
            ]
          }
            """
        .trimIndent()

    val questionnaireResponseString =
      """
              {
                "resourceType": "QuestionnaireResponse",
                "item": [
                  {
                    "linkId": "1",
                    "text": "Repeated Group",
                    "item": [
                      {
                        "linkId": "1-1",
                        "answer": [
                          {
                            "valueDate": "2023-06-14"
                          }
                        ]
                      }
                    ]
                  },
                  {
                    "linkId": "1",
                    "text": "Repeated Group",
                    "item": [
                      {
                        "linkId": "1-1",
                        "answer": [
                          {
                            "valueDate": "2023-06-13"
                          }
                        ]
                      }
                    ]
                  }
                ]
              }
            """
        .trimIndent()

    val expectedQuestionnaireResponseString =
      """
              {
                "resourceType": "QuestionnaireResponse",
                "item": [
                  {
                    "linkId": "1",
                    "text": "Repeated Group",
                    "item": [
                      {
                        "linkId": "1-1",
                        "answer": [
                          {
                            "valueDate": "2023-06-14"
                          }
                        ]
                      },
                      {
                        "linkId": "1-2"
                      }
                    ]
                  },
                  {
                    "linkId": "1",
                    "text": "Repeated Group",
                    "item": [
                      {
                        "linkId": "1-1",
                        "answer": [
                          {
                            "valueDate": "2023-06-13"
                          }
                        ]
                      },
                      {
                        "linkId": "1-2"
                      }
                    ]
                  }
                ]
              }
            """
        .trimIndent()

    val questionnaire =
      printer.parseResource(Questionnaire::class.java, questionnaireString) as Questionnaire

    val response =
      printer.parseResource(QuestionnaireResponse::class.java, questionnaireResponseString)
        as QuestionnaireResponse

    val expectedResponse =
      printer.parseResource(QuestionnaireResponse::class.java, expectedQuestionnaireResponseString)
        as QuestionnaireResponse

    val viewModel = createQuestionnaireViewModel(questionnaire, response)

    runTest {
      viewModel.addMissingResponseItems(questionnaire.item, response.item)
      assertResourceEquals(response, expectedResponse)
    }
  }

  // ==================================================================== //
  //                                                                      //
  //                       Questionnaire State Flow                       //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should emit questionnaire state flow`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic questions"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "another-link-id"
                text = "Name?"
                type = Questionnaire.QuestionnaireItemType.STRING
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
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
  }

  @Test
  fun `should not validate answers before modification by user`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "link-id"
            text = "Name?"
            type = Questionnaire.QuestionnaireItemType.STRING
            required = true
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      val questionnaireItemViewItemList = viewModel.getQuestionnaireItemViewItemList()
      assertThat(questionnaireItemViewItemList.single().asQuestion().validationResult)
        .isEqualTo(NotValidated)
    }
  }

  @Test
  fun `should validate answers after modification by user`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "link-id"
            text = "Name?"
            type = Questionnaire.QuestionnaireItemType.STRING
            required = true
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      val question = viewModel.getQuestionnaireItemViewItemList().single().asQuestion()
      question.clearAnswer()

      assertThat(
          viewModel.getQuestionnaireItemViewItemList().single().asQuestion().validationResult,
        )
        .isEqualTo(Invalid(listOf("Missing answer for required field.")))
    }
  }

  @Test
  fun `should skip disabled questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(false) }
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      assertThat(
          viewModel
            .getQuestionnaireItemViewItemList()
            .single()
            .asQuestion()
            .questionnaireItem
            .linkId,
        )
        .isEqualTo("question-1")
    }
  }

  @Test
  fun `should include enabled questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(true) }
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      val questionnaireItemViewItemList = viewModel.getQuestionnaireItemViewItemList()

      assertThat(questionnaireItemViewItemList).hasSize(2)
      assertThat(questionnaireItemViewItemList[0].asQuestion().questionnaireItem.linkId)
        .isEqualTo("question-1")
      assertThat(questionnaireItemViewItemList[1].asQuestion().questionnaireItem.linkId)
        .isEqualTo("question-2")
    }
  }

  @Test
  fun `should skip hidden questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item-1"
            text = "a question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addExtension(hiddenExtension)
          },
        )
      }

    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = serializedQuestionnaire

    val viewModel = QuestionnaireViewModel(context, state)

    assertThat(viewModel.getQuestionnaireItemViewItemList()).isEmpty()
  }

  @Test
  fun `should include non-hidden questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addExtension().apply {
              url = EXTENSION_HIDDEN_URL
              setValue(BooleanType(false))
            }
            addInitial().apply { value = BooleanType(true) }
          },
        )
      }
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = serializedQuestionnaire

    val viewModel = QuestionnaireViewModel(context, state)
    viewModel.runViewModelBlocking {
      assertThat(
          viewModel
            .getQuestionnaireItemViewItemList()
            .single()
            .asQuestion()
            .questionnaireItem
            .linkId,
        )
        .isEqualTo("a-boolean-item-1")
    }
  }

  // ==================================================================== //
  //                                                                      //
  //                              Pagination                              //
  //                                                                      //
  // ==================================================================== //
  @Test
  fun `should include all top level items as pages when any item has page extension`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1-noExtension"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page3-noExtension"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page3-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 3"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page4-noExtension-hidden"
            addExtension(hiddenExtension)
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page4-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 4"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page5"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page5-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 5"
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = false),
                QuestionnairePage(2, enabled = true, hidden = false),
                QuestionnairePage(3, enabled = true, hidden = true),
                QuestionnairePage(4, enabled = true, hidden = false),
              ),
            currentPageIndex = 0,
          ),
        )
    }
  }

  @Test
  fun `should show current page`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
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
              QuestionnairePage(1, enabled = true, hidden = false),
            ),
          currentPageIndex = 0,
        ),
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
  fun `should go to next page`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()

      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = false),
              ),
            currentPageIndex = 1,
          ),
        )
    }
  }

  @Test
  fun `should go to previous page`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = false),
              ),
            currentPageIndex = 0,
          ),
        )
    }
  }

  @Test
  fun `should skip disabled pages`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "page1-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page3"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page3-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 3"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
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
          ),
        )
    }
  }

  @Test
  fun `should skip first page if it is hidden`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addExtension(hiddenExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page3"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page3-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 3"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      val questionnaireStatePagination =
        (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination

      assertThat(
          questionnaireStatePagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = true),
                QuestionnairePage(1, enabled = true, hidden = false),
                QuestionnairePage(2, enabled = true, hidden = false),
              ),
            currentPageIndex = 1,
          ),
        )

      assertThat(questionnaireStatePagination.hasPreviousPage).isFalse()
    }
  }

  @Test
  fun `should skip last page if it is hidden`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page3"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addExtension(hiddenExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page3-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 3"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      val questionnaireStatePagination =
        (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination

      assertThat(
          questionnaireStatePagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = false),
                QuestionnairePage(2, enabled = true, hidden = true),
              ),
            currentPageIndex = 1,
          ),
        )

      assertThat(questionnaireStatePagination.hasNextPage).isFalse()
    }
  }

  @Test
  fun `should skip hidden pages`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addExtension(hiddenExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page3"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page3-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 3"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages =
              listOf(
                QuestionnairePage(0, enabled = true, hidden = false),
                QuestionnairePage(1, enabled = true, hidden = true),
                QuestionnairePage(2, enabled = true, hidden = false),
              ),
            currentPageIndex = 2,
          ),
        )
    }
  }

  // ==================================================================== //
  //                                                                      //
  //                             Entry Modes                              //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should allow user to move forward in prior-edit entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.PRIOR_EDIT)
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
          ),
        )
    }
  }

  @Test
  fun `should allow user to move forward and back in prior-edit entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.PRIOR_EDIT)
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0,
          ),
        )
    }
  }

  @Test
  fun `should not allow user to move forward in prior-edit entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                required = true
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0,
          ),
        )
    }
  }

  @Test
  fun `should not allow user to go to review page in prior-edit entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                required = true
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.setReviewMode(true)

      val questionnaireState = viewModel.questionnaireStateFlow.value

      assertThat(
          (questionnaireState.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
          ),
        )

      assertThat(
          questionnaireState.bottomNavItem!!.questionnaireNavigationUIState.navReview
            is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()
    }
  }

  @Test
  fun `should allow user to move forward in random entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.RANDOM)
      assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.last()?.index)
    }
  }

  @Test
  fun `should allow user to move forward and back in random entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      assertThat(questionnaire.entryMode).isEqualTo(EntryMode.RANDOM)
      assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.first()?.index)
    }
  }

  @Test
  fun `should allow user to move forward when no entry-mode is defined`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()

      assertThat(viewModel.entryMode).isEqualTo(EntryMode.RANDOM)
      assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.last()?.index)
    }
  }

  @Test
  fun `should allow user to move forward and back when no entry-mode is defined`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      viewModel.goToPreviousPage()

      assertThat(viewModel.entryMode).isEqualTo(EntryMode.RANDOM)
      assertThat(viewModel.currentPageIndexFlow.value).isEqualTo(viewModel.pages?.first()?.index)
    }
  }

  @Test
  fun `should allow user to move forward only in sequential entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()

      val questionnaireState = viewModel.questionnaireStateFlow.value

      assertThat(
          (questionnaireState.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
          ),
        )
      assertThat(
          questionnaireState.bottomNavItem!!.questionnaireNavigationUIState.navSubmit
            is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                required = true
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      assertThat(
          (viewModel.questionnaireStateFlow.value.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 0,
          ),
        )
    }
  }

  @Test
  fun `should not allow user to move backward only in sequential entry-mode`() = runTest {
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
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel.goToNextPage()
      val questionnaireState1 = viewModel.questionnaireStateFlow.value
      assertThat(
          (questionnaireState1.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
          ),
        )
      assertThat(
          questionnaireState1.bottomNavItem!!.questionnaireNavigationUIState.navSubmit
            is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()

      viewModel.goToPreviousPage()

      val questionnaireState2 = viewModel.questionnaireStateFlow.value
      assertThat(
          (questionnaireState2.displayMode as DisplayMode.EditMode).pagination,
        )
        .isEqualTo(
          QuestionnairePagination(
            isPaginated = true,
            pages = viewModel.pages!!,
            currentPageIndex = 1,
          ),
        )
      assertThat(
          questionnaireState2.bottomNavItem!!.questionnaireNavigationUIState.navSubmit
            is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()
    }
  }

  // ==================================================================== //
  //                                                                      //
  //                             Review Mode                              //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should not show review button if review mode is not enabled`() {
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = false)
      val questionnaireState = viewModel.questionnaireStateFlow.first()
      assertThat(
          questionnaireState.bottomNavItem!!.questionnaireNavigationUIState.navReview
            is QuestionnaireNavigationViewUIState.Hidden,
        )
        .isTrue()
    }
  }

  @Test
  fun `should not show review button even if show review page first is enabled`() {
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel =
        createQuestionnaireViewModel(
          questionnaire,
          enableReviewPage = false,
          showReviewPageFirst = true,
        )
      val questionnaireState = viewModel.questionnaireStateFlow.first()
      assertThat(
          questionnaireState.bottomNavItem!!.questionnaireNavigationUIState.navReview
            is QuestionnaireNavigationViewUIState.Hidden,
        )
        .isTrue()
    }
  }

  @Test
  fun `should show review button if review mode is enabled`() {
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      val questionnaireState = viewModel.questionnaireStateFlow.first()
      assertThat(
          questionnaireState.bottomNavItem!!.questionnaireNavigationUIState.navReview
            is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()
    }
  }

  @Test
  fun `should move to review page if review mode is enabled`() {
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel =
        createQuestionnaireViewModel(
          questionnaire,
          enableReviewPage = true,
          showSubmitButton = true,
        )
      viewModel.setReviewMode(true)
      assertThat(viewModel.questionnaireStateFlow.first().displayMode)
        .isInstanceOf(DisplayMode.ReviewMode::class.java)
      assertThat(viewModel.questionnaireStateFlow.first().displayMode)
        .isEqualTo(
          DisplayMode.ReviewMode(
            showEditButton = true,
            showNavAsScroll = false,
          ),
        )
    }
  }

  @Test
  fun `should show review page first`() {
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel =
        createQuestionnaireViewModel(
          questionnaire,
          enableReviewPage = true,
          showReviewPageFirst = true,
        )

      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.ReviewMode)
            .showEditButton,
        )
        .isTrue()
    }
  }

  @Test
  fun `paginated questionnaire with no review feature should not show review button when moved to next page`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page1-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 1"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page2"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page2-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 2"
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = false)
      viewModel.runViewModelBlocking {
        viewModel.goToNextPage()
        assertThat(
            viewModel.questionnaireStateFlow.value.bottomNavItem!!
              .questionnaireNavigationUIState
              .navReview is QuestionnaireNavigationViewUIState.Hidden,
          )
          .isTrue()
      }
    }

  @Test
  fun `paginated questionnaire with no review feature should not show review button when last page is hidden`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addExtension(hiddenExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page1-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 1"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page2"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page2-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 2"
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = false)
      viewModel.runViewModelBlocking {
        assertThat(
            viewModel.questionnaireStateFlow.value.bottomNavItem!!
              .questionnaireNavigationUIState
              .navReview is QuestionnaireNavigationViewUIState.Hidden,
          )
          .isTrue()
      }
    }

  @Test
  fun `paginated questionnaire with review feature should show review button when moved to next page`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page1-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 1"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page2"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page2-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 2"
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      viewModel.runViewModelBlocking {
        viewModel.goToNextPage()
        assertThat(
            viewModel.questionnaireStateFlow.value.bottomNavItem!!
              .questionnaireNavigationUIState
              .navReview is QuestionnaireNavigationViewUIState.Enabled,
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
            QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addExtension(hiddenExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page1-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 1"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page2"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page2-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 2"
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      viewModel.runViewModelBlocking {
        assertThat(
            viewModel.questionnaireStateFlow.value.bottomNavItem!!
              .questionnaireNavigationUIState
              .navReview is QuestionnaireNavigationViewUIState.Enabled,
          )
          .isTrue()
      }
    }

  @Test
  fun `toggle review mode to false should show review button`() {
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      viewModel.setReviewMode(false)
      assertThat(
          viewModel.questionnaireStateFlow
            .first()
            .bottomNavItem!!
            .questionnaireNavigationUIState
            .navReview is QuestionnaireNavigationViewUIState.Enabled,
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
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, enableReviewPage = true)
      viewModel.setReviewMode(true)

      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.ReviewMode)
            .showEditButton,
        )
        .isTrue()
    }
  }

  @Test
  fun `review mode with navigation long scroll appends navigation to view items`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-linkId"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel =
      createQuestionnaireViewModel(
        questionnaire,
        enableReviewPage = true,
        showNavigationInLongScroll = true,
      )
    viewModel.setReviewMode(true)

    val questionnaireState = viewModel.questionnaireStateFlow.first()
    assertThat(questionnaireState.items.last())
      .isInstanceOf(QuestionnaireAdapterItem.Navigation::class.java)
  }

  // ==================================================================== //
  //                                                                      //
  //                            Read-only Mode                            //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `read-only mode should not show edit button`() {
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-link-id"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, readOnlyMode = true)

      assertThat(
          (viewModel.questionnaireStateFlow.first().displayMode as DisplayMode.ReviewMode)
            .showEditButton,
        )
        .isFalse()
    }
  }

  @Test
  fun `read-only mode with navigation long scroll appends navigation to view items`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-linkId"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel =
      createQuestionnaireViewModel(
        questionnaire,
        readOnlyMode = true,
        showNavigationInLongScroll = true,
      )

    val questionnaireState = viewModel.questionnaireStateFlow.first()
    assertThat(questionnaireState.items.last())
      .isInstanceOf(QuestionnaireAdapterItem.Navigation::class.java)
  }

  // ==================================================================== //
  //                                                                      //
  //                            Submit Button                             //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `EXTRA_SHOW_SUBMIT_BUTTON set to false should not show submit button`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, showSubmitButton = false)
    assertThat(
        viewModel.questionnaireStateFlow
          .first()
          .bottomNavItem!!
          .questionnaireNavigationUIState
          .navSubmit is QuestionnaireNavigationViewUIState.Hidden,
      )
      .isTrue()
  }

  @Test
  fun `EXTRA_SHOW_SUBMIT_BUTTON set to true should show submit button`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, showSubmitButton = true)
    assertThat(
        viewModel.questionnaireStateFlow
          .first()
          .bottomNavItem!!
          .questionnaireNavigationUIState
          .navSubmit is QuestionnaireNavigationViewUIState.Enabled,
      )
      .isTrue()
  }

  @Test
  fun `EXTRA_SHOW_SUBMIT_BUTTON not setting should show submit button`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, showSubmitButton = null)
    assertThat(
        viewModel.questionnaireStateFlow
          .first()
          .bottomNavItem!!
          .questionnaireNavigationUIState
          .navSubmit is QuestionnaireNavigationViewUIState.Enabled,
      )
      .isTrue()
  }

  // ==================================================================== //
  //                                                                      //
  //                            Cancel Button                             //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `EXTRA_SHOW_CANCEL_BUTTON set to false should not show cancel button`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, showCancelButton = false)
    assertThat(
        viewModel.questionnaireStateFlow
          .first()
          .bottomNavItem!!
          .questionnaireNavigationUIState
          .navCancel is QuestionnaireNavigationViewUIState.Hidden,
      )
      .isTrue()
  }

  @Test
  fun `EXTRA_SHOW_CANCEL_BUTTON set to true should show cancel button`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, showCancelButton = true)
    assertThat(
        viewModel.questionnaireStateFlow
          .first()
          .bottomNavItem!!
          .questionnaireNavigationUIState
          .navCancel is QuestionnaireNavigationViewUIState.Enabled,
      )
      .isTrue()
  }

  @Test
  fun `EXTRA_SHOW_CANCEL_BUTTON not setting should not show cancel button`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire, showCancelButton = null)
    assertThat(
        viewModel.questionnaireStateFlow
          .first()
          .bottomNavItem!!
          .questionnaireNavigationUIState
          .navCancel is QuestionnaireNavigationViewUIState.Hidden,
      )
      .isTrue()
  }

  @Test
  fun `EXTRA_SHOW_CANCEL_BUTTON set to false should not show cancel button in paginated layout`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page1-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 1"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page2"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page2-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 2"
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, showCancelButton = false)
      assertThat(
          viewModel.questionnaireStateFlow
            .first()
            .bottomNavItem!!
            .questionnaireNavigationUIState
            .navCancel is QuestionnaireNavigationViewUIState.Hidden,
        )
        .isTrue()
    }

  @Test
  fun `EXTRA_SHOW_CANCEL_BUTTON set to true should show cancel button in paginated layout`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page1-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 1"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page2"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page2-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 2"
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, showCancelButton = true)
      assertThat(
          viewModel.questionnaireStateFlow
            .first()
            .bottomNavItem!!
            .questionnaireNavigationUIState
            .navCancel is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()
    }

  @Test
  fun `EXTRA_SHOW_CANCEL_BUTTON not setting should not show cancel button in paginated layout`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page1"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page1-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 1"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "page2"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension(paginationExtension)
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "page2-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  text = "Question on page 2"
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, showCancelButton = null)
      assertThat(
          viewModel.questionnaireStateFlow
            .first()
            .bottomNavItem!!
            .questionnaireNavigationUIState
            .navCancel is QuestionnaireNavigationViewUIState.Hidden,
        )
        .isTrue()
    }
  // ==================================================================== //
  //                                                                      //
  //                       Navigation in Long Scroll                      //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL setting should show navigation last in long scroll`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-linkId"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire, showNavigationInLongScroll = true)
      val questionnaireState = viewModel.questionnaireStateFlow.first()
      assertThat(questionnaireState.bottomNavItem).isNull()
      assertThat(questionnaireState.items.last())
        .isInstanceOf(QuestionnaireAdapterItem.Navigation::class.java)
      val navigationItem = questionnaireState.items.last() as QuestionnaireAdapterItem.Navigation
      assertThat(
          navigationItem.questionnaireNavigationUIState.navSubmit
            is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()
    }

  fun `EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL not setting should not add navigation item to questionnaireState items`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-linkId"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire)
      val questionnaireState = viewModel.questionnaireStateFlow.first()
      assertThat(questionnaireState.items.map { it::class.java })
        .doesNotContain(QuestionnaireAdapterItem.Navigation::class.java)
      assertThat(
          questionnaireState.bottomNavItem!!.questionnaireNavigationUIState.navSubmit
            is QuestionnaireNavigationViewUIState.Enabled,
        )
        .isTrue()
    }

  // ==================================================================== //
  //                                                                      //
  //                        Questionnaire Response                        //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `getQuestionnaireResponse() should copy question text`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial(
              Questionnaire.QuestionnaireItemInitialComponent().apply {
                value = BooleanType(false)
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        QuestionnaireResponse().apply {
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-link-id"
              text = "Basic question"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(false)
                },
              )
            },
          )
        },
      )
    }
  }

  @Test
  fun `getQuestionnaireResponse() should copy translated question text`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            textElement.apply {
              addExtension(
                Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                  addExtension(Extension("lang", StringType("en-US")))
                  addExtension(Extension("content", StringType("Basic Question")))
                },
              )
            }
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial(
              Questionnaire.QuestionnaireItemInitialComponent().apply {
                value = BooleanType(false)
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        QuestionnaireResponse().apply {
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-link-id"
              text = "Basic Question"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(false)
                },
              )
            },
          )
        },
      )
    }
  }

  @Test
  fun `getQuestionnaireResponse() should copy question text for nested questions`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-link-id"
            text = "Basic question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "a.1-link-id"
                text = "Basic Nested question"
                type = Questionnaire.QuestionnaireItemType.STRING
                initial =
                  mutableListOf(
                    Questionnaire.QuestionnaireItemInitialComponent().apply {
                      value = StringType("Test Value")
                    },
                  )
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    runTest {
      assertQuestionnaireResponseEqualsIgnoringTimestamps(
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
                      },
                    )
                },
              )
            },
          )
        },
      )
    }
  }

  @Test
  fun `getQuestionnaireResponse() should skip disabled questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(false) }
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          },
        )
      }
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertQuestionnaireResponseEqualsIgnoringTimestamps(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-1"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              },
            )
          },
        )
      },
    )
  }

  @Test
  fun `getQuestionnaireResponse() should include enabled questions`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addInitial().apply { value = BooleanType(true) }
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          },
        )
      }
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertQuestionnaireResponseEqualsIgnoringTimestamps(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-1"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              },
            )
          },
        )
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-2"
          },
        )
      },
    )
  }

  @Test // https://github.com/google/android-fhir/issues/1664
  fun `getQuestionnaireResponse() should skip questions without matching response`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-1"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-2"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(true)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "question-3"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addEnableWhen().apply {
              answer = BooleanType(false)
              question = "question-1"
              operator = Questionnaire.QuestionnaireItemOperator.EQUAL
            }
          },
        )
      }
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        id = "a-questionnaire-response"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-1"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              },
            )
          },
        )
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "question-3"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              },
            )
          },
        )
      }
    state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] =
      printer.encodeResourceToString(questionnaireResponse)

    val viewModel = QuestionnaireViewModel(context, state)

    assertQuestionnaireResponseEqualsIgnoringTimestamps(
      viewModel.getQuestionnaireResponse(),
      questionnaireResponse,
    )
  }

  @Test
  fun `getQuestionnaireResponse() should disable all questions in a chain of dependent questions after top question is disabled`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "question-1"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "question-2"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              addEnableWhen().apply {
                answer = BooleanType(true)
                question = "question-1"
                operator = Questionnaire.QuestionnaireItemOperator.EQUAL
              }
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "question-3"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              addEnableWhen().apply {
                answer = BooleanType(true)
                question = "question-2"
                operator = Questionnaire.QuestionnaireItemOperator.EQUAL
              }
            },
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
                },
              )
            },
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-2"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                },
              )
            },
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-3"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                },
              )
            },
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
            QuestionnaireItemComponent().apply {
              linkId = "question-1"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "question-2"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              addEnableWhen().apply {
                answer = BooleanType(true)
                question = "question-1"
                operator = Questionnaire.QuestionnaireItemOperator.EQUAL
              }
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "question-3"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              addEnableWhen().apply {
                answer = BooleanType(true)
                question = "question-2"
                operator = Questionnaire.QuestionnaireItemOperator.EQUAL
              }
            },
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
                },
              )
            },
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-2"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                },
              )
            },
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "question-3"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = BooleanType(true)
                },
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire, questionnaireResponse)

      viewModel.runViewModelBlocking {
        val items = viewModel.getQuestionnaireItemViewItemList().map { it.asQuestion() }
        // Clearing the answer disables question-2 that in turn disables question-3.
        items.first { it.questionnaireItem.linkId == "question-1" }.clearAnswer()

        assertQuestionnaireResponseEqualsIgnoringTimestamps(
          viewModel.getQuestionnaireResponse(),
          QuestionnaireResponse().apply {
            id = "a-questionnaire-response"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "question-1"
              },
            )
          },
        )

        // Setting the answer of  "question-1" to true should enable question-2 that in turn enables
        // question-3 and restore their previous states.
        items
          .first { it.questionnaireItem.linkId == "question-1" }
          .setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            },
          )

        assertQuestionnaireResponseEqualsIgnoringTimestamps(
          viewModel.getQuestionnaireResponse(),
          questionnaireResponse,
        )
      }
    }

  @Test
  fun `getQuestionnaireResponse() should return enabled repeated groups`() {
    val questionnaireString =
      """
        {
  "resourceType": "Questionnaire",
  "id": "ANCDELIVERY",
  "item": [
    {
      "linkId": "12.0",
      "type": "group",
      "text": "Pregnancy Outcome - Baby",
      "repeats": true,
      "item": [
        {
          "linkId": "12.6",
          "type": "group",
          "text": "Live Birth/Stillbirth",
          "item": [
            {
              "extension": [
                {
                  "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
                  "valueCodeableConcept": {
                    "coding": [
                      {
                        "system": "http://hl7.org/fhir/questionnaire-item-control",
                        "code": "radio-button",
                        "display": "Radio Button"
                      }
                    ]
                  }
                }
              ],
              "linkId": "12.6.1",
              "type": "choice",
              "text": "Is it Live Birth/Stillbirth?",
              "answerOption": [
                {
                  "valueCoding": {
                    "code": "live-birth",
                    "display": "Live Birth"
                  }
                },
                {
                  "valueCoding": {
                    "code": "still-birth",
                    "display": "Stillbirth"
                  }
                }
              ]
            },
            {
              "enableWhen": [
                {
                  "question": "12.6.1",
                  "operator": "=",
                  "answerCoding": {
                    "code": "live-birth",
                    "display": "Live Birth"
                  }
                }
              ],
              "linkId": "12.6.3",
              "type": "group",
              "text": "Baby Gender",
              "item": [
                {
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
                      "valueCodeableConcept": {
                        "coding": [
                          {
                            "system": "http://hl7.org/fhir/questionnaire-item-control",
                            "code": "radio-button",
                            "display": "Radio Button"
                          }
                        ]
                      }
                    }
                  ],
                  "linkId": "12.6.3.1",
                  "type": "choice",
                  "text": "Sex of Baby",
                  "answerOption": [
                    {
                      "valueCoding": {
                        "code": "male",
                        "display": "Male"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "female",
                        "display": "Female"
                      }
                    }
                  ]
                }
              ]
            },
            {
              "enableWhen": [
                {
                  "question": "12.6.1",
                  "operator": "=",
                  "answerCoding": {
                    "code": "still-birth",
                    "display": "Stillbirth"
                  }
                }
              ],
              "linkId": "12.6.4",
              "type": "group",
              "text": "Stillbirth Type",
              "item": [
                {
                  "extension": [
                    {
                      "url": "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl",
                      "valueCodeableConcept": {
                        "coding": [
                          {
                            "system": "http://hl7.org/fhir/questionnaire-item-control",
                            "code": "radio-button",
                            "display": "Radio Button"
                          }
                        ]
                      }
                    }
                  ],
                  "linkId": "12.6.4.1",
                  "type": "choice",
                  "text": "Stillbirth Type",
                  "answerOption": [
                    {
                      "valueCoding": {
                        "code": "FSB",
                        "display": "FSB"
                      }
                    },
                    {
                      "valueCoding": {
                        "code": "MSB",
                        "display": "MSB"
                      }
                    }
                  ]
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}

            """
        .trimIndent()

    val questionnaireResponseString =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "12.0",
              "item": [
                {
                  "linkId": "12.6",
                  "item": [
                    {
                      "linkId": "12.6.1",
                      "answer": [
                        {
                          "valueCoding": {
                            "code": "live-birth",
                            "display": "Live Birth"
                          }
                        }
                      ]
                    },
                    {
                      "linkId": "12.6.3",
                      "item": [
                        {
                          "linkId": "12.6.3.1",
                          "answer": [
                            {
                              "valueCoding": {
                                "code": "male",
                                "display": "Male"
                              }
                            }
                          ]
                        }
                      ]
                    },
                    {
                      "linkId": "12.6.4",
                      "item": [
                        {
                          "linkId": "12.6.4.1"
                        }
                      ]
                    }
                  ]
                }
              ]
            },
            {            
              "linkId": "12.0",
              "item": [
                {
                  "linkId": "12.6",
                  "item": [
                    {
                      "linkId": "12.6.1",
                      "answer": [
                        {
                          "valueCoding": {
                            "code": "still-birth",
                            "display": "Stillbirth"
                          }
                        }
                      ]
                    },
                    {
                      "linkId": "12.6.3",
                      "item": [
                        {
                          "linkId": "12.6.3.1"
                        }
                      ]
                    },
                    {
                      "linkId": "12.6.4",
                      "item": [
                        {
                          "linkId": "12.6.4.1",
                          "answer": [
                            {
                              "valueCoding": {
                                "code": "FSB",
                                "display": "FSB"
                              }
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }

            """
        .trimIndent()

    val expectedResponseString =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "12.0",
              "text": "Pregnancy Outcome - Baby",
              "item": [
                {
                  "linkId": "12.6",
                  "text": "Live Birth/Stillbirth",
                  "item": [
                    {
                      "linkId": "12.6.1",
                      "text": "Is it Live Birth/Stillbirth?",
                      "answer": [
                        {
                          "valueCoding": {
                            "code": "live-birth",
                            "display": "Live Birth"
                          }
                        }
                      ]
                    },
                    {
                      "linkId": "12.6.3",
                      "text": "Baby Gender",
                      "item": [
                        {
                          "linkId": "12.6.3.1",
                          "text": "Sex of Baby",
                          "answer": [
                            {
                              "valueCoding": {
                                "code": "male",
                                "display": "Male"
                              }
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            },
            {
              "linkId": "12.0",
              "text": "Pregnancy Outcome - Baby",
              "item": [
                {
                  "linkId": "12.6",
                  "text": "Live Birth/Stillbirth",
                  "item": [
                    {
                      "linkId": "12.6.1",
                      "text": "Is it Live Birth/Stillbirth?",
                      "answer": [
                        {
                          "valueCoding": {
                            "code": "still-birth",
                            "display": "Stillbirth"
                          }
                        }
                      ]
                    },
                    {
                      "linkId": "12.6.4",
                      "text": "Stillbirth Type",
                      "item": [
                        {
                          "linkId": "12.6.4.1",
                          "text": "Stillbirth Type",
                          "answer": [
                            {
                              "valueCoding": {
                                "code": "FSB",
                                "display": "FSB"
                              }
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }

            """
        .trimIndent()

    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = questionnaireString
    state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] = questionnaireResponseString
    val viewModel = QuestionnaireViewModel(context, state)
    runTest {
      val value = viewModel.getQuestionnaireResponse()
      val expectedResponse =
        printer.parseResource(QuestionnaireResponse::class.java, expectedResponseString)
          as QuestionnaireResponse

      assertQuestionnaireResponseEqualsIgnoringTimestamps(value, expectedResponse)
    }
  }

  @Test
  fun `clearAllAnswers clears all answers in questionnaire response`() {
    val questionnaireString =
      """
        {
          "resourceType": "Questionnaire",
          "id": "client-registration-sample",
          "item": [
            {
              "linkId": "1",
              "type": "group",
              "item": [
                {
                  "linkId": "1.1",
                  "text": "First Nested Item",
                  "type": "boolean"
                },
                {
                  "linkId": "1.2",
                  "text": "Second Nested Item",
                  "type": "boolean"
                }
              ]
            }
          ]
        }
            """
        .trimIndent()

    val questionnaireResponseString =
      """
           {
              "resourceType": "QuestionnaireResponse",
              "item": [
                {
                  "linkId": "1",
                  "item": [
                    {
                      "linkId": "1.1",
                      "text": "First Nested Item",
                      "answer": [
                        {
                          "valueBoolean": true
                        }
                      ]
                    },
                    {
                      "linkId": "1.2",
                      "text": "Second Nested Item",
                      "answer": [
                        {
                          "valueBoolean": true
                        }
                      ]
                    }
                  ]
                }
              ]
            }
      """
        .trimIndent()

    val expectedResponseString =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "item": [
            {
              "linkId": "1",
              "item": [
                {
                  "linkId": "1.1",
                  "text": "First Nested Item"
                },
                {
                  "linkId": "1.2",
                  "text": "Second Nested Item"
                }
              ]
            }
          ]
        }
      """
        .trimIndent()

    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = questionnaireString
    state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] = questionnaireResponseString
    val viewModel = QuestionnaireViewModel(context, state)
    viewModel.clearAllAnswers()
    runTest {
      val value = viewModel.getQuestionnaireResponse()
      val expectedResponse =
        printer.parseResource(QuestionnaireResponse::class.java, expectedResponseString)
          as QuestionnaireResponse
      assertQuestionnaireResponseEqualsIgnoringTimestamps(value, expectedResponse)
    }
  }

  // ==================================================================== //
  //                                                                      //
  //               Questionnaire Response with Nested Items               //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun questionnaireHasNestedItem_ofTypeGroup_shouldNestItemWithinItem() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-group-item"
            text = "Group question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "a-nested-item"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
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
                  },
                )
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel
        .getQuestionnaireItemViewItemList()[1]
        .asQuestion()
        .setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            this.value = valueBooleanType.setValue(false)
          },
        )

      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
  }

  @Test
  fun questionnaireHasNestedItem_ofTypeRepeatedGroup_shouldNestMultipleItems() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "repeated-group-a"
            text = "Group question A"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-item-a"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "another-nested-item-a"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "repeated-group-b"
            text = "Group question B"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-item-b"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "another-nested-item-b"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              },
            )
          },
        )
      }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    fun getQuestionnaireAdapterItemListA() =
      viewModel.getQuestionnaireItemViewItemList().single {
        it.asQuestionOrNull()?.questionnaireItem?.linkId == "repeated-group-a"
      }

    fun getQuestionnaireAdapterItemListB() =
      viewModel.getQuestionnaireItemViewItemList().single {
        it.asQuestionOrNull()?.questionnaireItem?.linkId == "repeated-group-b"
      }
    viewModel.runViewModelBlocking {
      // Calling addAnswer out of order should not result in the answers in the response being out
      // of order; all of the answers to repeated-group-a should come before repeated-group-b.
      repeat(times = 2) {
        getQuestionnaireAdapterItemListA()
          .asQuestion()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              item =
                getQuestionnaireAdapterItemListA()
                  .asQuestion()
                  .questionnaireItem
                  .createNestedQuestionnaireResponseItems()
            },
          )
        getQuestionnaireAdapterItemListB()
          .asQuestion()
          .addAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              item =
                getQuestionnaireAdapterItemListB()
                  .asQuestion()
                  .questionnaireItem
                  .createNestedQuestionnaireResponseItems()
            },
          )
      }

      assertThat(
          viewModel.getQuestionnaireItemViewItemList().map {
            when (it) {
              is QuestionnaireAdapterItem.Question -> it.item.questionnaireItem.linkId
              is QuestionnaireAdapterItem.RepeatedGroupHeader -> "RepeatedGroupHeader:${it.index}"
              is QuestionnaireAdapterItem.Navigation -> TODO()
            }
          },
        )
        .containsExactly(
          "repeated-group-a",
          "RepeatedGroupHeader:0",
          "nested-item-a",
          "another-nested-item-a",
          "RepeatedGroupHeader:1",
          "nested-item-a",
          "another-nested-item-a",
          "repeated-group-b",
          "RepeatedGroupHeader:0",
          "nested-item-b",
          "another-nested-item-b",
          "RepeatedGroupHeader:1",
          "nested-item-b",
          "another-nested-item-b",
        )
        .inOrder()

      assertQuestionnaireResponseEqualsIgnoringTimestamps(
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
                  },
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-a"
                    text = "Basic question"
                  },
                )
              },
            )
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "repeated-group-a"
                text = "Group question A"
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-item-a"
                    text = "Basic question"
                  },
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-a"
                    text = "Basic question"
                  },
                )
              },
            )
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "repeated-group-b"
                text = "Group question B"
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-item-b"
                    text = "Basic question"
                  },
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-b"
                    text = "Basic question"
                  },
                )
              },
            )
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "repeated-group-b"
                text = "Group question B"
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-item-b"
                    text = "Basic question"
                  },
                )
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "another-nested-item-b"
                    text = "Basic question"
                  },
                )
              },
            )
          },
      )
    }
  }

  @Test
  fun questionnaireHasNestedItem_notOfTypeGroup_shouldNestItemWithinAnswerItem() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-boolean-item"
            text = "Parent question"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "a-nested-boolean-item"
                text = "Nested question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                addItem(
                  QuestionnaireItemComponent().apply {
                    linkId = "a-nested-nested-boolean-item"
                    text = "Nested nested question"
                    type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  },
                )
              },
            )
          },
        )
      }

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-boolean-item"
            text = "Parent question"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                this.value = valueBooleanType.setValue(false)
                addItem(
                  QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                    linkId = "a-nested-boolean-item"
                    text = "Nested question"
                    addAnswer(
                      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                        this.value = valueBooleanType.setValue(false)
                        addItem(
                          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                            linkId = "a-nested-nested-boolean-item"
                            text = "Nested nested question"
                            addAnswer(
                              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                                .apply { this.value = valueBooleanType.setValue(false) },
                            )
                          },
                        )
                      },
                    )
                  },
                )
              },
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      var items = viewModel.getQuestionnaireItemViewItemList().map { it.asQuestion() }
      assertThat(items.map { it.questionnaireItem.linkId }).containsExactly("a-boolean-item")

      items
        .first()
        .setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            this.value = valueBooleanType.setValue(false)
          },
        )

      items = viewModel.getQuestionnaireItemViewItemList().map { it.asQuestion() }
      assertThat(items.map { it.questionnaireItem.linkId })
        .containsExactly("a-boolean-item", "a-nested-boolean-item")

      items
        .last()
        .setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            this.value = valueBooleanType.setValue(false)
          },
        )

      items = viewModel.getQuestionnaireItemViewItemList().map { it.asQuestion() }
      assertThat(items.map { it.questionnaireItem.linkId })
        .containsExactly("a-boolean-item", "a-nested-boolean-item", "a-nested-nested-boolean-item")

      items
        .last()
        .setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            this.value = valueBooleanType.setValue(false)
          },
        )

      assertQuestionnaireResponseEqualsIgnoringTimestamps(
        viewModel.getQuestionnaireResponse(),
        questionnaireResponse,
      )
    }
  }

  // ==================================================================== //
  //                                                                      //
  //                   Repeated Groups with Enable When                   //
  //                                                                      //
  // ==================================================================== //

  // https://github.com/google/android-fhir/issues/2590
  @Test
  fun `should evaluate enable when with new questionnaire response items eg added repeated group`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "repeated-group"
              type = Questionnaire.QuestionnaireItemType.GROUP
              repeats = true
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "nested-1"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                },
              )
              addItem(
                QuestionnaireItemComponent().apply {
                  linkId = "nested-2"
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                  addEnableWhen().apply {
                    answer = BooleanType(true)
                    question = "nested-1"
                    operator = Questionnaire.QuestionnaireItemOperator.EQUAL
                  }
                },
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel.getQuestionnaireItemViewItemList().single().asQuestion().apply {
          this.answersChangedCallback(
            this.questionnaireItem,
            this.getQuestionnaireResponseItem(),
            listOf(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent(),
            ),
            null,
          )
        }

        assertThat(
            viewModel
              .getQuestionnaireItemViewItemList()
              .filterIsInstance<QuestionnaireAdapterItem.Question>()
              .map { it.asQuestion().questionnaireItem.linkId },
          )
          .containsExactly("repeated-group", "nested-1")

        viewModel
          .getQuestionnaireItemViewItemList()
          .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "nested-1" }
          .asQuestion()
          .apply {
            this.answersChangedCallback(
              this.questionnaireItem,
              this.getQuestionnaireResponseItem(),
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  this.value = valueBooleanType.setValue(true)
                },
              ),
              null,
            )
          }

        assertThat(
            viewModel
              .getQuestionnaireItemViewItemList()
              .filterIsInstance<QuestionnaireAdapterItem.Question>()
              .map { it.asQuestion().questionnaireItem.linkId },
          )
          .containsExactly("repeated-group", "nested-1", "nested-2")
      }
    }

  // ==================================================================== //
  //                                                                      //
  //                          Answer Value Sets                           //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun questionnaire_resolveContainedAnswerValueSetInEnabledAnswerOptions() = runTest {
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
                  },
                )

                addContains(
                  ValueSet.ValueSetExpansionContainsComponent().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "N"
                    display = "No"
                  },
                )

                addContains(
                  ValueSet.ValueSetExpansionContainsComponent().apply {
                    system = CODE_SYSTEM_YES_NO
                    code = "asked-unknown"
                    display = "Don't Know"
                  },
                )
              }
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "q-yesnodontknow"
            answerValueSet = "#$valueSetId"
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      val viewItem =
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "q-yesnodontknow" }
      val answerOptions = viewItem.enabledAnswerOptions
      assertThat(answerOptions.map { it.valueCoding.display })
        .containsExactly("Yes", "No", "Don't Know")
        .inOrder()
    }
  }

  @Test
  fun questionnaire_resolveAnswerValueSetExternalResolvedInEnabledAnswerOptions() = runTest {
    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .dataCaptureConfiguration =
      DataCaptureConfig(
        valueSetResolverExternal =
          object : ExternalAnswerValueSetResolver {
            override suspend fun resolve(uri: String): List<Coding> {
              return if (uri == CODE_SYSTEM_YES_NO) {
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
                  },
                )
              } else {
                emptyList()
              }
            }
          },
      )

    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "q-codesystemyesno"
            answerValueSet = CODE_SYSTEM_YES_NO
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      val viewItem =
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "q-codesystemyesno" }
      val codeSet = viewItem.enabledAnswerOptions
      assertThat(codeSet.map { it.valueCoding.display })
        .containsExactly("Yes", "No", "Don't Know")
        .inOrder()
    }
  }

  // ==================================================================== //
  //                                                                      //
  //                          Answer Expression                           //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should return x-fhir-query referring to patient in context on start`() = runTest {
    var searchString = ""
    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .dataCaptureConfiguration =
      DataCaptureConfig(
        xFhirQueryResolver = { xFhirQuery ->
          searchString = xFhirQuery
          emptyList()
        },
      )

    val patientId = "123"
    val patient = Patient().apply { id = patientId }

    val questionnaire =
      Questionnaire().apply {
        extension =
          listOf(
            Extension(
                EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT,
              )
              .apply {
                addExtension(
                  "name",
                  Coding(
                    CODE_SYSTEM_LAUNCH_CONTEXT,
                    "patient",
                    "Patient",
                  ),
                )
                addExtension("type", CodeType("Patient"))
              },
          )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a"
            text = "answer expression question text"
            type = Questionnaire.QuestionnaireItemType.REFERENCE
            extension =
              listOf(
                Extension(
                  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression",
                  Expression().apply {
                    this.expression = "Observation?subject=Patient/{{%patient.id}}"
                    this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
                  },
                ),
              )
          },
        )
      }
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)
    state[EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP] =
      mapOf("patient" to printer.encodeResourceToString(patient))

    val viewModel = QuestionnaireViewModel(context, state)
    viewModel.runViewModelBlocking {
      val viewItem =
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
      assertThat(viewItem.enabledAnswerOptions).isEmpty()
      assertThat(searchString).isEqualTo("Observation?subject=Patient/$patientId")
    }
  }

  @Test
  fun `should return questionnaire item answer options for answer expression and choice column`() =
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
            QuestionnaireItemComponent().apply {
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
                    },
                  ),
                  Extension(
                      "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn",
                    )
                    .apply {
                      this.addExtension(Extension("path", StringType("id")))
                      this.addExtension(Extension("label", StringType("name")))
                      this.addExtension(Extension("forDisplay", BooleanType(true)))
                    },
                )
            },
          )
        }
      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

      val viewModel = QuestionnaireViewModel(context, state)
      viewModel.runViewModelBlocking {
        val viewItem =
          viewModel
            .getQuestionnaireItemViewItemList()
            .map { it.asQuestion() }
            .single { it.questionnaireItem.linkId == "a" }
        assertThat(viewItem.enabledAnswerOptions.first().valueReference.reference)
          .isEqualTo("Practitioner/${practitioner.logicalId}")
      }
    }

  @Test
  fun `should throw exception when XFhirQueryResolver is not provided for x-fhir-query answer expressions`() {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          QuestionnaireItemComponent().apply {
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
                  },
                ),
                Extension(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn",
                  )
                  .apply {
                    this.addExtension(Extension("path", StringType("id")))
                    this.addExtension(Extension("label", StringType("name")))
                    this.addExtension(Extension("forDisplay", BooleanType(true)))
                  },
              )
          },
        )
      }
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)
    val viewModel = QuestionnaireViewModel(context, state)
    val exception =
      assertThrows(IllegalStateException::class.java) {
        runTest { viewModel.runViewModelBlocking {} }
      }
    assertThat(exception.message)
      .isEqualTo(
        "XFhirQueryResolver cannot be null. Please provide the XFhirQueryResolver via DataCaptureConfig.",
      )
  }

  @Test
  fun `should return questionnaire item answer options for answer expression with fhirpath variable`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              text = "Question 1"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              repeats = true
              initial =
                listOf(
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "1", "One")),
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "2", "Two")),
                )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              text = "Question 2"
              type = Questionnaire.QuestionnaireItemType.STRING
              extension =
                listOf(
                  Extension(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression",
                    Expression().apply {
                      this.expression = "%resource.item[0].answer.value.select(%VAR1 + code)"
                      this.language = Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()
                    },
                  ),
                  Extension(
                    "http://hl7.org/fhir/StructureDefinition/variable",
                    Expression().apply {
                      this.name = "VAR1"
                      this.expression = "'Class '"
                      this.language = Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()
                    },
                  ),
                )
            },
          )
        }

      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)
      val viewModel = QuestionnaireViewModel(context, state)

      viewModel.runViewModelBlocking {
        val viewItem =
          viewModel
            .getQuestionnaireItemViewItemList()
            .map { it.asQuestion() }
            .single { it.questionnaireItem.linkId == "b" }
        assertThat(viewItem.enabledAnswerOptions.map { it.valueStringType.value })
          .containsExactly("Class 1", "Class 2")
      }
    }

  @Test
  fun `should return questionnaire item answer options for answer expression with fhirpath supplement %context`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              text = "Question 1"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              repeats = true
              initial =
                listOf(
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "1", "One")),
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "2", "Two")),
                )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              text = "Question 2"
              type = Questionnaire.QuestionnaireItemType.STRING
              extension =
                listOf(
                  Extension(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression",
                    Expression().apply {
                      this.expression =
                        "%resource.item[0].answer.value.select('Code ' + code + '-' + %context.linkId)"
                      this.language = Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()
                    },
                  ),
                )
            },
          )
        }

      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)
      val viewModel = QuestionnaireViewModel(context, state)

      viewModel.runViewModelBlocking {
        val viewItem =
          viewModel
            .getQuestionnaireItemViewItemList()
            .map { it.asQuestion() }
            .single { it.questionnaireItem.linkId == "b" }
        assertThat(viewItem.enabledAnswerOptions.map { it.valueStringType.value })
          .containsExactly("Code 1-b", "Code 2-b")
      }
    }

  @Test
  fun `should return questionnaire item answer options for answer expression with fhirpath supplement %questionnaire`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          this.identifier = listOf(Identifier().apply { value = "A" })
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              text = "Question 1"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              repeats = true
              initial =
                listOf(
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "1", "One")),
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "2", "Two")),
                )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              text = "Q2"
              type = Questionnaire.QuestionnaireItemType.STRING
              extension =
                listOf(
                  Extension(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression",
                    Expression().apply {
                      this.expression = "'Questionnaire = ' + %questionnaire.identifier.value"
                      this.language = Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()
                    },
                  ),
                )
            },
          )
        }

      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)
      val viewModel = QuestionnaireViewModel(context, state)

      viewModel.runViewModelBlocking {
        val viewItem =
          viewModel
            .getQuestionnaireItemViewItemList()
            .map { it.asQuestion() }
            .single { it.questionnaireItem.linkId == "b" }
        assertThat(viewItem.enabledAnswerOptions.map { it.valueStringType.value })
          .containsExactly("Questionnaire = A")
      }
    }

  @Test
  fun `should return questionnaire item answer options for answer expression with fhirpath supplement %qItem`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              text = "Question 1"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              repeats = true
              initial =
                listOf(
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "1", "One")),
                  Questionnaire.QuestionnaireItemInitialComponent(Coding("test", "2", "Two")),
                )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              text = "Q2"
              type = Questionnaire.QuestionnaireItemType.STRING
              extension =
                listOf(
                  Extension(
                    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-answerExpression",
                    Expression().apply {
                      this.expression = "'Id of item = ' + %qItem.linkId"
                      this.language = Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()
                    },
                  ),
                )
            },
          )
        }

      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)
      val viewModel = QuestionnaireViewModel(context, state)

      viewModel.runViewModelBlocking {
        val viewItem =
          viewModel
            .getQuestionnaireItemViewItemList()
            .map { it.asQuestion() }
            .single { it.questionnaireItem.linkId == "b" }
        assertThat(viewItem.enabledAnswerOptions.map { it.valueStringType.value })
          .containsExactly("Id of item = b")
      }
    }

  // ==================================================================== //
  //                                                                      //
  //                 Answer Options Toggle Expression                     //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `only answer options evaluating to true in answerOptionsToggleExpression occurrences should be enabled on initial load`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              text = "Select an option"
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "false"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "true"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              answerOption =
                listOf(
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      }
                  },
                )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        val viewItem =
          viewModel
            .getQuestionnaireItemViewItemList()
            .map { it.asQuestion() }
            .single { it.questionnaireItem.linkId == "b" }
        assertThat(viewItem.enabledAnswerOptions.map { it.value as Coding }.map { it.code })
          .containsExactly("option2")
      }
    }

  @Test
  fun `enabledAnswerOptions should toggle options in answerOptionsToggleExpression occurrence  when answers to depended question change`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              text = "Is Married?"
              addInitial().apply { value = BooleanType(false) }
            },
          )

          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              text = "Select an option"
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression =
                          "%resource.repeat(item).where(linkId='a' and answer.empty().not()).select(answer.value)"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression =
                          "%resource.repeat(item).where(linkId='a' and answer.empty().not()).select(answer.value.not())"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              answerOption =
                listOf(
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      }
                  },
                )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run {
            assertThat(this.enabledAnswerOptions.map { it.valueCoding.code })
              .containsExactly("option2")
          }
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = BooleanType(true)
            },
          )

        assertThat(
            viewModel
              .getQuestionnaireResponse()
              .item
              .single { it.linkId == "a" }
              .answerFirstRep
              .value
              .primitiveValue(),
          )
          .isEqualTo(BooleanType(true).primitiveValue())

        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run {
            assertThat(this.enabledAnswerOptions.map { it.valueCoding.code })
              .containsExactly("option3", "option1")
          }
      }
    }

  @Test
  fun `enabledAnswerOptions should include answer options not listed in any answerOptionsToggleExpression occurrences`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              text = "Select an option"
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "false"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "true"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              answerOption =
                listOf(
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option4"
                        display = "Option 4"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option5"
                        display = "Option 5"
                      }
                  },
                )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run {
            assertThat(enabledAnswerOptions.map { it.valueCoding.code })
              .containsAtLeast("option4", "option5")
          }
      }
    }

  @Test
  fun `enabledAnswerOptions should not include unknown options listed in answerOptionsToggleExpression occurrences`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              text = "Select an option"
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option75"
                        display = "Option 75"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "true"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "false"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              answerOption =
                listOf(
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option3"
                        display = "Option 3"
                      }
                  },
                )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run {
            assertThat(enabledAnswerOptions.map { it.valueCoding.code }).doesNotContain("option75")
            assertThat(enabledAnswerOptions.map { it.valueCoding.code }).containsExactly("option2")
          }
      }
    }

  @Test
  fun `enabledAnswerOptions should include options listed in both allowed and disallowed answerOptionsToggleExtension options`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              text = "Select an option"
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "false"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "true"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )
              answerOption =
                listOf(
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      }
                  },
                  Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                    value =
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      }
                  },
                )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run { assertThat(enabledAnswerOptions.map { it.valueCoding.code }).contains("option1") }
      }
    }

  @Test
  fun `enabledAnswerOptions should include options toggled from answerOptionsToggleExpression occurrences with variable expression`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addExtension(
            Extension(EXTENSION_VARIABLE_URL).apply {
              setValue(
                Expression().apply {
                  name = "textVal"
                  language = "text/fhirpath"
                  expression = "10"
                },
              )
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b-link"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              text = "Select an option"
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "%textVal > 10"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )

              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression = "%textVal = 10"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )

              addAnswerOption(
                Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                  value =
                    Coding().apply {
                      code = "option1"
                      display = "Option 1"
                    }
                },
              )
              addAnswerOption(
                Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                  value =
                    Coding().apply {
                      code = "option2"
                      display = "Option 2"
                    }
                },
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b-link" }
          .run {
            assertThat(enabledAnswerOptions.map { it.valueCoding.code }).containsExactly("option2")
          }
      }
    }

  @Test
  fun `should remove previously selected answers that toggled off and disallowed in answerOptionsToggleExpression occurrences`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              text = "Is Married?"
              addInitial().apply { value = BooleanType(false) }
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              type = Questionnaire.QuestionnaireItemType.CHOICE
              text = "Select an option"
              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option1"
                        display = "Option 1"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression =
                          "%resource.repeat(item).where(linkId='a' and answer.empty().not()).select(answer.value.not())"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )

              addExtension(
                Extension(EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_URL).apply {
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION_OPTION,
                      Coding().apply {
                        code = "option2"
                        display = "Option 2"
                      },
                    ),
                  )
                  addExtension(
                    Extension(
                      EXTENSION_ANSWER_OPTION_TOGGLE_EXPRESSION,
                      Expression().apply {
                        this.expression =
                          "%resource.repeat(item).where(linkId='a' and answer.empty().not()).select(answer.value)"
                        this.language = "text/fhirpath"
                      },
                    ),
                  )
                },
              )

              addAnswerOption(
                Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                  value =
                    Coding().apply {
                      code = "option1"
                      display = "Option 1"
                    }
                },
              )
              addAnswerOption(
                Questionnaire.QuestionnaireItemAnswerOptionComponent().apply {
                  value =
                    Coding().apply {
                      code = "option2"
                      display = "Option 2"
                    }
                },
              )
            },
          )
        }
      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run {
            assertThat(enabledAnswerOptions.map { it.valueCoding.code }).containsExactly("option1")
            setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value =
                  Coding().apply {
                    code = "option1"
                    display = "Option 1"
                  }
              },
            )
          }
        assertThat(
            viewModel
              .getQuestionnaireResponse()
              .item
              .single { it.linkId == "b" }
              .answerFirstRep
              .valueCoding
              .code,
          )
          .isEqualTo("option1")
        // change answer to depended question to trigger re-evaluation of
        // answerOptionsToggleExpression expression
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .run {
            setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              },
            )
          }

        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run {
            assertThat(enabledAnswerOptions.map { it.valueCoding.code }).containsExactly("option2")
            assertThat(answers).isEmpty()
          }
        assertThat(viewModel.getQuestionnaireResponse().item.single { it.linkId == "b" }.answer)
          .isEmpty()
      }
    }

  // ==================================================================== //
  //                                                                      //
  //                        Calculated Expression                         //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should calculate value on start for questionnaire item with calculated expression extension`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  },
                )
              }
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.QUANTITY
              addInitial(
                Questionnaire.QuestionnaireItemInitialComponent(Quantity.fromUcum("1", "year")),
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        assertThat(
            viewModel
              .getQuestionnaireResponse()
              .item
              .single { it.linkId == "a-birthdate" }
              .answerFirstRep
              .value
              .asStringValue(),
          )
          .isEqualTo(DateType(Date()).apply { add(Calendar.YEAR, -1) }.asStringValue())

        assertThat(
            viewModel
              .getQuestionnaireResponse()
              .item
              .single { it.linkId == "a-age-years" }
              .answerFirstRep
              .valueQuantity
              .value
              .toString(),
          )
          .isEqualTo("1")
      }
    }

  @Test
  fun `should calculate value on change for questionnaire item with calculated expression extension`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  },
                )
              }
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.INTEGER
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
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
                },
              ),
              null,
            )
          }

        assertThat(
            birthdateItem.getQuestionnaireResponseItem().answer.first().valueDateType.valueAsString,
          )
          .isEqualTo(DateType(Date()).apply { add(Calendar.YEAR, -2) }.valueAsString)
      }
    }

  @Test
  fun `should not change value for modified questionnaire items with calculated expression extension`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  },
                )
              }
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.INTEGER
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
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
              },
            ),
            null,
          )
        }

        assertThat(
            birthdateItem.getQuestionnaireResponseItem().answer.first().valueDateType.valueAsString,
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
                },
              ),
              null,
            )
          }

        assertThat(
            birthdateItem.getQuestionnaireResponseItem().answer.first().valueDateType.valueAsString,
          )
          .isEqualTo(birthdateValue.valueAsString)
      }
    }

  @Test
  fun `should detect cyclic dependency for questionnaire item with calculated expression extension in flat list`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-birthdate"
            type = Questionnaire.QuestionnaireItemType.DATE
            addInitial(
              Questionnaire.QuestionnaireItemInitialComponent(
                DateType(Date()).apply { add(Calendar.YEAR, -2) },
              ),
            )
            addExtension().apply {
              url = EXTENSION_CALCULATED_EXPRESSION_URL
              setValue(
                Expression().apply {
                  this.language = "text/fhirpath"
                  this.expression =
                    "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                },
              )
            }
          },
        )

        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a-age-years"
            type = Questionnaire.QuestionnaireItemType.INTEGER
            addExtension().apply {
              url = EXTENSION_CALCULATED_EXPRESSION_URL
              setValue(
                Expression().apply {
                  this.language = "text/fhirpath"
                  this.expression =
                    "today().toString().substring(0, 4).toInteger() - %resource.repeat(item).where(linkId='a-birthdate').answer.value.toString().substring(0, 4).toInteger()"
                },
              )
            }
          },
        )
      }

    val exception =
      assertThrows(null, IllegalStateException::class.java) {
        runTest {
          val viewModel = createQuestionnaireViewModel(questionnaire)
          viewModel.runViewModelBlocking {}
        }
      }
    assertThat(exception.message)
      .isEqualTo("a-birthdate and a-age-years have cyclic dependency in expression based extension")
  }

  @Test
  fun `should detect cyclic dependency for questionnaire item with calculated expression extension in nested list`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addInitial(
                Questionnaire.QuestionnaireItemInitialComponent(
                  DateType(Date()).apply { add(Calendar.YEAR, -2) },
                ),
              )
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
                  },
                )
              }
            },
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
            QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.INTEGER
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "today().toString().substring(0, 4).toInteger() - %resource.repeat(item).where(linkId='a-birthdate').answer.value.toString().substring(0, 4).toInteger()"
                  },
                )
              }
            },
          )
      }

    val exception =
      assertThrows(null, IllegalStateException::class.java) {
        runTest {
          val viewModel = createQuestionnaireViewModel(questionnaire)
          viewModel.runViewModelBlocking {}
        }
      }
    assertThat(exception.message)
      .isEqualTo("a-birthdate and a-age-years have cyclic dependency in expression based extension")
  }

  // ==================================================================== //
  //                                                                      //
  //    cqf-calculatedValue Expression for minValue/maxValue Extension    //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should return correct value evaluated for minValue extension with cqf-calculatedValue`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MIN_VALUE_EXTENSION_URL,
                  DateType().apply {
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression = "today()"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .run {
            assertThat((this.minAnswerValue as DateType).valueAsString)
              .isEqualTo(LocalDate.now().toString())
          }
      }
    }

  @Test
  fun `should return calculated value for minValue extension that has both value and cqf-calculatedValue expression`() =
    runTest {
      val lastLocalDate = LocalDate.now().minusMonths(1)
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MIN_VALUE_EXTENSION_URL,
                  DateType().apply {
                    value =
                      Date.from(
                        lastLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(),
                      )
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression = "today()"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .run {
            assertThat((this.minAnswerValue as DateType).valueAsString)
              .isEqualTo(LocalDate.now().toString())
          }
      }
    }

  @Test
  fun `should correctly validate cqf-calculatedValue for minValue extension`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a"
            type = Questionnaire.QuestionnaireItemType.DATE
            text = "Select a date"
            addExtension(
              Extension(
                MIN_VALUE_EXTENSION_URL,
                DateType().apply {
                  addExtension(
                    Extension(
                      EXTENSION_CQF_CALCULATED_VALUE_URL,
                      Expression().apply {
                        expression = "today()"
                        language = "text/fhirpath"
                      },
                    ),
                  )
                },
              ),
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel
        .getQuestionnaireItemViewItemList()
        .map { it.asQuestion() }
        .single { it.questionnaireItem.linkId == "a" }
        .setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = DateType.parseV3("20231010")
          },
        )

      viewModel
        .getQuestionnaireItemViewItemList()
        .map { it.asQuestion() }
        .single { it.questionnaireItem.linkId == "a" }
        .run {
          assertThat(validationResult)
            .isEqualTo(Invalid(listOf("Minimum value allowed is:${LocalDate.now()}")))
        }
    }
  }

  @Test
  fun `should return correct evaluated value for maxValue extension with cqf-calculatedValue extension`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MAX_VALUE_EXTENSION_URL,
                  DateType().apply {
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression = "today()"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .run {
            assertThat((this.maxAnswerValue as DateType).valueAsString)
              .isEqualTo(LocalDate.now().toString())
          }
      }
    }

  @Test
  fun `should return calculated value for maxValue extension that has both value and cqf-calculatedValue expression`() =
    runTest {
      val lastLocalDate = LocalDate.now().minusMonths(1)
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MAX_VALUE_EXTENSION_URL,
                  DateType().apply {
                    value =
                      Date.from(
                        lastLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(),
                      )
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression = "today()"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      assertThat(
          (questionnaire.item.single { it.linkId == "a" }.maxValue as DateType).valueAsString,
        )
        .isEqualTo(lastLocalDate.toString())

      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .run {
            assertThat((this.maxAnswerValue as DateType).valueAsString)
              .isEqualTo(LocalDate.now().toString())
          }
      }
    }

  @Test
  fun `should correctly validate cqf-calculatedValue for maxValue extension`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "a"
            type = Questionnaire.QuestionnaireItemType.DATE
            text = "Select a date"
            addExtension(
              Extension(
                MAX_VALUE_EXTENSION_URL,
                DateType().apply {
                  addExtension(
                    Extension(
                      EXTENSION_CQF_CALCULATED_VALUE_URL,
                      Expression().apply {
                        expression = "today()"
                        language = "text/fhirpath"
                      },
                    ),
                  )
                },
              ),
            )
          },
        )
      }

    val viewModel = createQuestionnaireViewModel(questionnaire)
    viewModel.runViewModelBlocking {
      viewModel
        .getQuestionnaireItemViewItemList()
        .map { it.asQuestion() }
        .single { it.questionnaireItem.linkId == "a" }
        .setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value =
              DateType().apply {
                val tomorrow = LocalDate.now().plusDays(1)
                value =
                  Date.from(tomorrow.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
              }
          },
        )

      viewModel
        .getQuestionnaireItemViewItemList()
        .map { it.asQuestion() }
        .single { it.questionnaireItem.linkId == "a" }
        .run {
          assertThat(validationResult)
            .isEqualTo(Invalid(listOf("Maximum value allowed is:${LocalDate.now()}")))
        }
    }
  }

  @Test
  fun `should evaluate cqf-calculatedValue with expression dependent on other question`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select minimum date"
            },
          )

          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "b"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MIN_VALUE_EXTENSION_URL,
                  DateType().apply {
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression =
                            "%resource.repeat(item).where(linkId='a' and answer.empty().not()).select(answer.value)"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        // Checks dependent answer is null at first
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run { assertThat((this.minAnswerValue as? DateType)?.valueAsString).isNull() }

        // Answers the first question
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value = DateType.parseV3("20231014")
            },
          )

        // Checks dependent answer has min value set correctly
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "b" }
          .run {
            assertThat((this.minAnswerValue as DateType).valueAsString).isEqualTo("2023-10-14")
          }
      }
    }

  @Test
  fun `should evaluate cqf-calculatedValue with expression dependent on a variable expression`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          addExtension(
            Extension(EXTENSION_VARIABLE_URL).apply {
              setValue(
                Expression().apply {
                  name = "dateToday"
                  expression = "today()"
                  language = "text/fhirpath"
                },
              )
            },
          )

          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MIN_VALUE_EXTENSION_URL,
                  DateType().apply {
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression = "%dateToday"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single()
          .run {
            assertThat((this.minAnswerValue as DateType).valueAsString)
              .isEqualTo(LocalDate.now().toString())
          }
      }
    }

  @Test
  fun `should correctly evaluate cqf-calculatedValue with expression dependent on x-fhir-query launchContext`() =
    runTest {
      val testDate = LocalDate.now().minusYears(20)
      val questionnaire =
        Questionnaire().apply {
          addExtension(
            Extension(EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT).apply {
              addExtension("name", Coding(CODE_SYSTEM_LAUNCH_CONTEXT, "patient", "Patient"))
              addExtension("type", CodeType("Patient"))
            },
          )

          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MIN_VALUE_EXTENSION_URL,
                  DateType().apply {
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression = "%patient.birthDate"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val patient0 =
        Patient().apply {
          birthDate = Date.from(testDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        }
      state[EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP] =
        mapOf("patient" to printer.encodeResourceToString(patient0))
      val viewModel = createQuestionnaireViewModel(questionnaire)

      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single()
          .run {
            assertThat((this.minAnswerValue as DateType).valueAsString)
              .isEqualTo(testDate.toString())
          }
      }
    }

  @Test
  fun `should correctly validate cqf-calculatedValue with expression dependent on x-fhir-query launchContext`() =
    runTest {
      val testDate = LocalDate.now().minusYears(20)

      val questionnaire =
        Questionnaire().apply {
          addExtension(
            Extension(EXTENSION_SDC_QUESTIONNAIRE_LAUNCH_CONTEXT).apply {
              addExtension("name", Coding(CODE_SYSTEM_LAUNCH_CONTEXT, "patient", "Patient"))
              addExtension("type", CodeType("Patient"))
            },
          )

          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a"
              type = Questionnaire.QuestionnaireItemType.DATE
              text = "Select a date"
              addExtension(
                Extension(
                  MIN_VALUE_EXTENSION_URL,
                  DateType().apply {
                    addExtension(
                      Extension(
                        EXTENSION_CQF_CALCULATED_VALUE_URL,
                        Expression().apply {
                          expression = "%patient.birthDate"
                          language = "text/fhirpath"
                        },
                      ),
                    )
                  },
                ),
              )
            },
          )
        }

      val patient0 =
        Patient().apply {
          birthDate = Date.from(testDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
        }
      state[EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP] =
        mapOf("patient" to printer.encodeResourceToString(patient0))
      val viewModel = createQuestionnaireViewModel(questionnaire)

      viewModel.runViewModelBlocking {
        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
              value =
                DateType().apply {
                  value = patient0.birthDate
                  add(Calendar.MONTH, -1)
                }
            },
          )

        viewModel
          .getQuestionnaireItemViewItemList()
          .map { it.asQuestion() }
          .single { it.questionnaireItem.linkId == "a" }
          .run {
            assertThat(validationResult)
              .isEqualTo(Invalid(listOf("Minimum value allowed is:$testDate")))
          }
      }
    }

  // ==================================================================== //
  //                                                                      //
  //                           Display Category                           //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun nestedDisplayItem_parentQuestionItemIsGroup_createQuestionnaireStateItem() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "parent-question"
            text = "parent question text"
            type = Questionnaire.QuestionnaireItemType.GROUP
            item =
              listOf(
                QuestionnaireItemComponent().apply {
                  linkId = "nested-display-question"
                  text = "subtitle text"
                  type = Questionnaire.QuestionnaireItemType.DISPLAY
                },
              )
          },
        )
      }
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

    val viewModel = QuestionnaireViewModel(context, state)
    viewModel.runViewModelBlocking {
      assertThat(
          viewModel.getQuestionnaireItemViewItemList().last().asQuestion().questionnaireItem.linkId,
        )
        .isEqualTo("nested-display-question")
    }
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
                    code = EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS
                    system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
                  },
                )
            },
          )
        }

      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "parent-question"
              text = "parent question text"
              type = Questionnaire.QuestionnaireItemType.BOOLEAN
              item =
                listOf(
                  QuestionnaireItemComponent().apply {
                    linkId = "nested-display-question"
                    text = "subtitle text"
                    type = Questionnaire.QuestionnaireItemType.DISPLAY
                    extension = listOf(displayCategoryExtension)
                  },
                )
            },
          )
        }
      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

      val viewModel = QuestionnaireViewModel(context, state)
      viewModel.runViewModelBlocking {
        assertThat(
            viewModel
              .getQuestionnaireItemViewItemList()
              .last()
              .asQuestion()
              .questionnaireItem
              .linkId,
          )
          .isEqualTo("parent-question")
      }
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
                  },
                )
            },
          )
        }

      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "parent-question"
              text = "parent question text"
              type = Questionnaire.QuestionnaireItemType.STRING
              item =
                listOf(
                  QuestionnaireItemComponent().apply {
                    linkId = "nested-display-question"
                    text = "flyover text"
                    type = Questionnaire.QuestionnaireItemType.DISPLAY
                    extension = listOf(itemControlExtensionWithFlyOverCode)
                  },
                )
            },
          )
        }
      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

      val viewModel = QuestionnaireViewModel(context, state)
      viewModel.runViewModelBlocking {
        assertThat(
            viewModel
              .getQuestionnaireItemViewItemList()
              .last()
              .asQuestion()
              .questionnaireItem
              .linkId,
          )
          .isEqualTo("parent-question")
      }
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
                  },
                )
            },
          )
        }

      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "parent-question"
              text = "parent question text"
              type = Questionnaire.QuestionnaireItemType.STRING
              item =
                listOf(
                  QuestionnaireItemComponent().apply {
                    linkId = "nested-display-question"
                    text = "help description"
                    type = Questionnaire.QuestionnaireItemType.DISPLAY
                    extension = listOf(itemControlExtensionWithHelpCode)
                  },
                )
            },
          )
        }
      state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

      val viewModel = QuestionnaireViewModel(context, state)
      viewModel.runViewModelBlocking {
        assertThat(
            viewModel
              .getQuestionnaireItemViewItemList()
              .last()
              .asQuestion()
              .questionnaireItem
              .linkId,
          )
          .isEqualTo("parent-question")
      }
    }

  // ==================================================================== //
  //                                                                      //
  //                               Testing                                //
  //                                                                      //
  // ==================================================================== //

  @Test
  fun `should throw exception on invalid cast inside runViewModelBlocking`() = runTest {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page1-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 1"
              },
            )
          },
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "page2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension(paginationExtension)
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "page2-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                text = "Question on page 2"
              },
            )
          },
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

  @Test
  fun `display item should be enabled when initial value is set`() = runTest {
    val enableWhenExpressionExtension: (Boolean) -> Extension = {
      Extension().apply {
        url = EXTENSION_ENABLE_WHEN_EXPRESSION_URL
        setValue(
          Expression().apply {
            language = Expression.ExpressionLanguage.TEXT_FHIRPATH.toCode()
            expression =
              "%resource.repeat(item).where(linkId='1' and answer.empty().not()).select(answer.value) = ${if (it) "true" else "false"}"
          },
        )
      }
    }
    val displayCategoryExtension =
      Extension().apply {
        url = EXTENSION_DISPLAY_CATEGORY_URL
        setValue(
          CodeableConcept().apply {
            coding =
              listOf(
                Coding().apply {
                  code = EXTENSION_DISPLAY_CATEGORY_INSTRUCTIONS
                  system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
                },
              )
          },
        )
      }
    val questionnaire: (List<Questionnaire.QuestionnaireItemInitialComponent>) -> Questionnaire = {
      Questionnaire().apply {
        id = "questionnaire.enabled.display"
        name = "Questionnaire Enabled Display"
        title = "Questionnaire Enabled Display"
        status = Enumerations.PublicationStatus.ACTIVE
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "1"
            text = "Questionnaire Text"
            type = Questionnaire.QuestionnaireItemType.BOOLEAN
            initial = it
            addItem(
                QuestionnaireItemComponent().apply {
                  extension = listOf(displayCategoryExtension, enableWhenExpressionExtension(false))
                  linkId = "1.1"
                  text = "Text when no is selected"
                  type = Questionnaire.QuestionnaireItemType.DISPLAY
                },
              )
              .addItem(
                QuestionnaireItemComponent().apply {
                  extension = listOf(displayCategoryExtension, enableWhenExpressionExtension(true))
                  linkId = "1.2"
                  text = "Text when yes is selected"
                  type = Questionnaire.QuestionnaireItemType.DISPLAY
                },
              )
          },
        )
      }
    }

    state[EXTRA_QUESTIONNAIRE_JSON_STRING] =
      printer.encodeResourceToString(questionnaire(emptyList()))

    // empty initial value
    var viewModel = QuestionnaireViewModel(context, state)
    viewModel.runViewModelBlocking {
      assertThat(viewModel.getQuestionnaireItemViewItemList().size).isEqualTo(1)
      // enabledDisplayItems is 0 when no choice is present
      assertThat(
          viewModel.getQuestionnaireItemViewItemList()[0].asQuestion().enabledDisplayItems.size,
        )
        .isEqualTo(0)
    }

    // initial value is set to false
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] =
      printer.encodeResourceToString(
        questionnaire(
          listOf(
            Questionnaire.QuestionnaireItemInitialComponent().apply { value = BooleanType(false) },
          ),
        ),
      )

    viewModel = QuestionnaireViewModel(context, state)
    var enabledDisplayItems: List<QuestionnaireItemComponent>
    viewModel.runViewModelBlocking {
      assertThat(viewModel.getQuestionnaireItemViewItemList().size).isEqualTo(1)
      enabledDisplayItems =
        viewModel.getQuestionnaireItemViewItemList()[0].asQuestion().enabledDisplayItems
      assertThat(enabledDisplayItems.size).isEqualTo(1)
      assertThat(enabledDisplayItems[0].type).isEqualTo(Questionnaire.QuestionnaireItemType.DISPLAY)
      assertThat(enabledDisplayItems[0].linkId).isEqualTo("1.1")
      assertThat(enabledDisplayItems[0].text).isEqualTo("Text when no is selected")
    }

    // initial value is set to true
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] =
      printer.encodeResourceToString(
        questionnaire(
          listOf(
            Questionnaire.QuestionnaireItemInitialComponent().apply { value = BooleanType(true) },
          ),
        ),
      )

    viewModel = QuestionnaireViewModel(context, state)
    viewModel.runViewModelBlocking {
      assertThat(viewModel.getQuestionnaireItemViewItemList().size).isEqualTo(1)
      enabledDisplayItems =
        viewModel.getQuestionnaireItemViewItemList()[0].asQuestion().enabledDisplayItems
      assertThat(enabledDisplayItems.size).isEqualTo(1)
      assertThat(enabledDisplayItems[0].type).isEqualTo(Questionnaire.QuestionnaireItemType.DISPLAY)
      assertThat(enabledDisplayItems[0].linkId).isEqualTo("1.2")
      assertThat(enabledDisplayItems[0].text).isEqualTo("Text when yes is selected")
    }
  }

  @Test
  fun `should update question title for questionnaire item with cqf expression extension when corresponding item answer is updated`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-age"
              type = Questionnaire.QuestionnaireItemType.INTEGER
            },
          )
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-description"
              type = Questionnaire.QuestionnaireItemType.STRING
              textElement.addExtension().apply {
                url = EXTENSION_CQF_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression =
                      "%resource.repeat(item).where(linkId='a-age' and answer.empty().not()).select('Notes for child of age ' + answer.value.toString() + ' years')"
                  },
                )
              }
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)

      val ageResponseItem = viewModel.getQuestionnaireResponse().item.first { it.linkId == "a-age" }

      assertThat(ageResponseItem.answer).isEmpty()

      var descriptionResponseItem: QuestionnaireViewItem? = null

      val job =
        this.launch {
          viewModel.questionnaireStateFlow.collect { questionnaireState ->
            descriptionResponseItem =
              questionnaireState.items
                .find { it.asQuestion().questionnaireItem.linkId == "a-description" }!!
                .asQuestion()
            this@launch.cancel()
          }
        }
      job.join()

      assertThat(descriptionResponseItem!!.questionText).isNull()
      val ageItemUpdated =
        viewModel.questionnaireStateFlow.value.items
          .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "a-age" }
          .asQuestion()
          .apply {
            this.answersChangedCallback(
              this.questionnaireItem,
              this.getQuestionnaireResponseItem(),
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  this.value = IntegerType(2)
                },
              ),
              null,
            )
          }

      assertThat(
          ageItemUpdated.getQuestionnaireResponseItem().answer.first().valueIntegerType.value,
        )
        .isEqualTo(2)

      val descriptionItemUpdated =
        viewModel.questionnaireStateFlow.value.items
          .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "a-description" }
          .asQuestion()

      assertThat(descriptionItemUpdated.questionText.toString())
        .isEqualTo("Notes for child of age 2 years")
    }

  @Test
  fun `should update question title for questionnaire item with cqf expression extension when expression has variable`() =
    runTest {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addExtension().apply {
            url = EXTENSION_VARIABLE_URL
            setValue(
              Expression().apply {
                name = "A"
                language = "text/fhirpath"
                expression = "1"
              },
            )
          }
          addItem(
            QuestionnaireItemComponent().apply {
              linkId = "a-description"
              type = Questionnaire.QuestionnaireItemType.STRING
              addExtension().apply {
                url = EXTENSION_VARIABLE_URL
                setValue(
                  Expression().apply {
                    name = "B"
                    language = "text/fhirpath"
                    expression = "2"
                  },
                )
              }
              textElement.addExtension().apply {
                url = EXTENSION_CQF_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression = "'Sum of variables is ' + ( %A + %B ).toString() "
                  },
                )
              }
            },
          )
        }

      val viewModel = createQuestionnaireViewModel(questionnaire)
      viewModel.runViewModelBlocking {
        val descriptionItem =
          viewModel
            .getQuestionnaireItemViewItemList()
            .first { it.asQuestionOrNull()?.questionnaireItem?.linkId == "a-description" }
            .asQuestion()

        assertThat(descriptionItem.questionText.toString()).isEqualTo("Sum of variables is 3")
      }
    }

  private fun createQuestionnaireViewModel(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse? = null,
    enableReviewPage: Boolean = false,
    showReviewPageFirst: Boolean = false,
    readOnlyMode: Boolean = false,
    showSubmitButton: Boolean? = null,
    showCancelButton: Boolean? = null,
    showNavigationInLongScroll: Boolean = false,
  ): QuestionnaireViewModel {
    state[EXTRA_QUESTIONNAIRE_JSON_STRING] = printer.encodeResourceToString(questionnaire)

    questionnaireResponse?.let {
      state[EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] =
        printer.encodeResourceToString(questionnaireResponse)
    }
    enableReviewPage.let { state[EXTRA_ENABLE_REVIEW_PAGE] = it }
    showReviewPageFirst.let { state[EXTRA_SHOW_REVIEW_PAGE_FIRST] = it }
    readOnlyMode.let { state[EXTRA_READ_ONLY] = it }
    showSubmitButton?.let { state[EXTRA_SHOW_SUBMIT_BUTTON] = it }
    showCancelButton?.let { state[EXTRA_SHOW_CANCEL_BUTTON] = it }
    showNavigationInLongScroll.let { state[EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL] = it }

    return QuestionnaireViewModel(context, state)
  }

  private fun QuestionnaireViewModel.getQuestionnaireItemViewItemList() =
    questionnaireStateFlow.value.items

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
            },
          ),
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

    /**
     * Asserts that the `expected` and the `actual` Questionnaire Responses are equal ignoring the
     * stamp values
     */
    private fun assertQuestionnaireResponseEqualsIgnoringTimestamps(
      actual: QuestionnaireResponse,
      expected: QuestionnaireResponse,
    ) {
      val actualResponseWithoutTimestamp =
        actual.copy().apply {
          extension.removeIf { ext -> ext.url == EXTENSION_LAST_LAUNCHED_TIMESTAMP }
          authored = null
        }
      val expectedResponseWithoutTimestamp =
        expected.copy().apply {
          extension.removeIf { ext -> ext.url == EXTENSION_LAST_LAUNCHED_TIMESTAMP }
          authored = null
        }
      assertThat(printer.encodeResourceToString(actualResponseWithoutTimestamp))
        .isEqualTo(printer.encodeResourceToString(expectedResponseWithoutTimestamp))
    }
  }
}

private fun QuestionnaireAdapterItem.asQuestion(): QuestionnaireViewItem {
  assertThat(this).isInstanceOf(QuestionnaireAdapterItem.Question::class.java)
  return (this as QuestionnaireAdapterItem.Question).item
}

private fun QuestionnaireAdapterItem.asQuestionOrNull(): QuestionnaireViewItem? =
  (this as? QuestionnaireAdapterItem.Question)?.item
