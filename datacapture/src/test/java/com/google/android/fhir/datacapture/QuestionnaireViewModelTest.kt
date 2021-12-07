/*
 * Copyright 2021 Google LLC
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
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_STRING
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_JSON_URI
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING
import com.google.android.fhir.datacapture.mapping.DataCaptureTestApplication
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.first
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class QuestionnaireViewModelTest(private val questionnaireSource: QuestionnaireSource) {
  private lateinit var state: SavedStateHandle
  private val context = ApplicationProvider.getApplicationContext<Application>()

  @Before
  fun setUp() {
    state = SavedStateHandle()
    check(
      ApplicationProvider.getApplicationContext<DataCaptureTestApplication>() is
        DataCaptureConfig.Provider
    ) { "Few tests require a custom application class that implements DataCaptureConfig.Provider" }
    DataCapture.initialize(
      (ApplicationProvider.getApplicationContext() as DataCaptureConfig.Provider)
        .getDataCaptureConfiguration()
    )
  }

  @Test
  fun stateHasNoQuestionnaire_shouldThrow() {
    val errorMessage =
      assertFailsWith<IllegalStateException> { QuestionnaireViewModel(context, state) }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Neither EXTRA_QUESTIONNAIRE_URI nor EXTRA_JSON_ENCODED_QUESTIONNAIRE is supplied."
      )
  }

  @Test
  fun stateHasNoQuestionnaireResponse_shouldCopyQuestionnaireId() {
    val questionnaire = Questionnaire().apply { id = "a-questionnaire" }
    val viewModel = createQuestionnaireViewModel(questionnaire)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      QuestionnaireResponse().apply { setQuestionnaire("Questionnaire/a-questionnaire") }
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
        setQuestionnaire("Questionnaire/a-questionnaire")
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply { linkId = "a-link-id" }
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
        setQuestionnaire("Questionnaire/a-questionnaire")
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "another-link-id"
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun stateHasQuestionnaireResponse_nestedItemsWithinGroupItems_shouldNotThrowException() { // ktlint-disable max-line-length
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
        id = "a-questionnaire-reponse"
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
      .isEqualTo(
        "Mismatching linkIds for questionnaire item a-link-id and " +
          "questionnaire response item a-different-link-id"
      )
  }

  @Test
  fun stateHasQuestionnaireResponse_lessItemsInQuestionnaireResponse_shouldThrowError() {
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
    val questionnaireResponse = QuestionnaireResponse().apply { id = "a-questionnaire-response" }

    val errorMessage =
      assertFailsWith<IllegalArgumentException> {
          createQuestionnaireViewModel(questionnaire, questionnaireResponse)
        }
        .localizedMessage

    assertThat(errorMessage)
      .isEqualTo("No matching questionnaire response item for questionnaire item a-link-id")
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
        setQuestionnaire("Questionnaire/a-questionnaire")
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-link-id"
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
        setQuestionnaire("Questionnaire/a-questionnaire")
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
      }
    )
  }

  @Test
  fun questionnaireHasInitialValueButQuestionnareResponseAsEmpty_shouldSetEmptyAnswer() {
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
  fun questionnareHasMoreThanOneInitialValuesAndNotRepeating_shouldThrowError() {
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
  fun questionnareHasInitialValueAndGroupType_shouldThrowError() {
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
  fun questionnareHasInitialValueAndDisplayType_shouldThrowError() {
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
      .isEqualTo(
        "No matching questionnaire item for questionnaire response item a-different-link-id"
      )
  }

  @Test
  fun questionnaireItemViewItemList_shouldGenerateQuestionnaireItemViewItemList() = runBlocking {
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
    questionnaireItemViewItemList[0].questionnaireResponseItemChangedCallback()
    assertThat(questionnaireItemViewItemList.size).isEqualTo(2)
    val firstQuestionnaireItemViewItem = questionnaireItemViewItemList[0]
    val firstQuestionnaireItem = firstQuestionnaireItemViewItem.questionnaireItem
    assertThat(firstQuestionnaireItem.linkId).isEqualTo("a-link-id")
    assertThat(firstQuestionnaireItem.text).isEqualTo("Basic questions")
    assertThat(firstQuestionnaireItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.GROUP)
    assertThat(firstQuestionnaireItemViewItem.questionnaireResponseItem.linkId)
      .isEqualTo("a-link-id")
    val secondQuestionnaireItemViewItem = questionnaireItemViewItemList[1]
    val secondQuestionnaireItem = secondQuestionnaireItemViewItem.questionnaireItem
    assertThat(secondQuestionnaireItem.linkId).isEqualTo("another-link-id")
    assertThat(secondQuestionnaireItem.text).isEqualTo("Name?")
    assertThat(secondQuestionnaireItem.type).isEqualTo(Questionnaire.QuestionnaireItemType.STRING)
    assertThat(secondQuestionnaireItemViewItem.questionnaireResponseItem.linkId)
      .isEqualTo("another-link-id")
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
        this.questionnaire = "Questionnaire/a-questionnaire"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-group-item"
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
                linkId = "a-nested-item"
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

    viewModel.getQuestionnaireItemViewItemList()[0].questionnaireResponseItem.item[0].addAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        this.value = valueBooleanType.setValue(false)
      }
    )
    viewModel.getQuestionnaireItemViewItemList()[0].questionnaireResponseItemChangedCallback()

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
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
        this.questionnaire = "Questionnaire/a-questionnaire"
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

    viewModel.getQuestionnaireItemViewItemList()[0].questionnaireResponseItem.addAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
        this.value = valueBooleanType.setValue(false)
      }
    )
    viewModel.getQuestionnaireItemViewItemList()[0].questionnaireResponseItemChangedCallback()
    viewModel.getQuestionnaireItemViewItemList()[0].questionnaireResponseItem.answer[0].item[0]
      .addAnswer(
        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
          this.value = valueBooleanType.setValue(false)
        }
      )

    assertResourceEquals(viewModel.getQuestionnaireResponse(), questionnaireResponse)
  }

  @Test
  fun questionnaireIsPaginated_shouldOnlyShowStateFromActivePage() = runBlocking {
    val paginationExtension =
      Extension().apply {
        url = EXTENSION_ITEM_CONTROL_URL
        setValue(CodeableConcept(Coding().apply { code = "page" }))
      }
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
      .isEqualTo(QuestionnairePagination(currentPageIndex = 0, lastPageIndex = 1))
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
    assertThat(
        viewModel.getQuestionnaireItemViewItemList().single().questionnaireResponseItem.linkId
      )
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
    assertThat(
        viewModel.getQuestionnaireItemViewItemList().single().questionnaireResponseItem.linkId
      )
      .isEqualTo("a-boolean-item-1")
  }

  @Test
  fun questionnaireItemWithInitialValue_enableWhenFalse_removeItemFromResponse() = runBlocking {
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
                linkId = "question-1"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                addInitial().apply { value = BooleanType(false) }
              }
            )
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "question-2"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                addEnableWhen().apply {
                  answer = BooleanType(true)
                  question = "question-1"
                  operator = Questionnaire.QuestionnaireItemOperator.EQUAL
                }
                addInitial().apply { value = BooleanType(true) }
              }
            )
          }
        )
      }
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    val questionnaireResponseJsonString =
      """
  {
    "resourceType": "QuestionnaireResponse",
    "questionnaire": "Questionnaire/a-questionnaire",
    "item": [
      {
        "linkId": "a-group-item",
        "item": [
          {
            "linkId": "question-1",
            "answer": [
              {
                "valueBoolean": false
              }
            ]
          }
        ]
      }
    ]
  }
      """.trimIndent()
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, serializedQuestionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      printer.parseResource(questionnaireResponseJsonString)
    )
  }

  @Test
  fun questionnaireItemWithInitialValue_enableWhenTrue_addItemToResponse() = runBlocking {
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
                linkId = "question-1"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                addInitial().apply { value = BooleanType(true) }
              }
            )
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "question-2"
                text = "Basic question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
                addEnableWhen().apply {
                  answer = BooleanType(true)
                  question = "question-1"
                  operator = Questionnaire.QuestionnaireItemOperator.EQUAL
                }
                addInitial().apply { value = BooleanType(true) }
              }
            )
          }
        )
      }
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    val questionnaireResponseJsonString =
      """
        {
          "resourceType": "QuestionnaireResponse",
          "questionnaire": "Questionnaire/a-questionnaire",
          "item": [
            {
              "linkId": "a-group-item",
              "item": [
                {
                  "linkId": "question-1",
                  "answer": [
                    {
                      "valueBoolean": true
                    }
                  ]
                },
                {
                  "linkId": "question-2",
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
      """.trimIndent()
    state.set(EXTRA_QUESTIONNAIRE_JSON_STRING, serializedQuestionnaire)

    val viewModel = QuestionnaireViewModel(context, state)

    assertResourceEquals(
      viewModel.getQuestionnaireResponse(),
      printer.parseResource(questionnaireResponseJsonString)
    )
  }

  private fun createQuestionnaireViewModel(
    questionnaire: Questionnaire,
    response: QuestionnaireResponse? = null
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
    response?.let {
      state.set(EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING, printer.encodeResourceToString(it))
    }
    return QuestionnaireViewModel(context, state)
  }

  private suspend fun QuestionnaireViewModel.getQuestionnaireItemViewItemList() =
    questionnaireStateFlow.first().items

  companion object {
    const val CODE_SYSTEM_YES_NO = "http://terminology.hl7.org/CodeSystem/v2-0136"

    val printer: IParser = FhirContext.forR4().newJsonParser()

    fun assertResourceEquals(r1: IBaseResource, r2: IBaseResource) {
      assertThat(printer.encodeResourceToString(r1)).isEqualTo(printer.encodeResourceToString(r2))
    }

    @JvmStatic
    @Parameters(name = "questionnaireSource={0}")
    fun parameters() = listOf(QuestionnaireSource.STRING, QuestionnaireSource.URI)
  }
}

/** The source of questionnaire. */
enum class QuestionnaireSource {
  STRING,
  URI
}
