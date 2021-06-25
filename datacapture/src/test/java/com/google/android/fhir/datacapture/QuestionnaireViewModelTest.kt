/*
 * Copyright 2020 Google LLC
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

import android.os.Build
import androidx.lifecycle.SavedStateHandle
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireViewModelTest {
  private lateinit var state: SavedStateHandle

  @Before
  fun setUp() {
    state = SavedStateHandle()
  }

  @Test
  fun stateHasNoQuestionnaireResponse_shouldCopyQuestionnaireId() {
    val questionnaire = Questionnaire().setId("a-questionnaire")
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    val viewModel = QuestionnaireViewModel(state)

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
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    val viewModel = QuestionnaireViewModel(state)

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
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    val viewModel = QuestionnaireViewModel(state)

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
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
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
    val serializedQuestionnaireResponse = printer.encodeResourceToString(questionnaireResponse)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    state.set(
      QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE,
      serializedQuestionnaireResponse
    )

    QuestionnaireViewModel(state)
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
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
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
    val serializedQuestionnaireResponse = printer.encodeResourceToString(questionnaireResponse)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    state.set(
      QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE,
      serializedQuestionnaireResponse
    )

    QuestionnaireViewModel(state)
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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
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
    val serializedQuestionniareResponse = printer.encodeResourceToString(questionnaireResponse)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
    state.set(
      QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE,
      serializedQuestionniareResponse
    )

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { QuestionnaireViewModel(state) }.localizedMessage

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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
    val questionnaireResponse = QuestionnaireResponse().apply { id = "a-questionnaire-response" }
    val serializedQuestionniareResponse = printer.encodeResourceToString(questionnaireResponse)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
    state.set(
      QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE,
      serializedQuestionniareResponse
    )

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { QuestionnaireViewModel(state) }.localizedMessage

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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
    val viewModel = QuestionnaireViewModel(state)

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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
    val viewModel = QuestionnaireViewModel(state)

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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
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
    val serializedQuestionniareResponse = printer.encodeResourceToString(questionnaireResponse)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
    state.set(
      QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE,
      serializedQuestionniareResponse
    )
    QuestionnaireViewModel(state)
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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { QuestionnaireViewModel(state) }.localizedMessage

    assertThat(errorMessage)
      .isEqualTo(
        "Questionnaire item a-link-id can only have multiple initial values for repeating items. See rule que-13 at https://www.hl7.org/fhir/questionnaire-definitions.html#Questionnaire.item.initial."
      )
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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { QuestionnaireViewModel(state) }.localizedMessage

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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { QuestionnaireViewModel(state) }.localizedMessage

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
    val serializedQuestionniare = printer.encodeResourceToString(questionnaire)
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
    val serializedQuestionniareResponse = printer.encodeResourceToString(questionnaireResponse)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionniare)
    state.set(
      QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE,
      serializedQuestionniareResponse
    )

    val errorMessage =
      assertFailsWith<IllegalArgumentException> { QuestionnaireViewModel(state) }.localizedMessage

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
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    val viewModel = QuestionnaireViewModel(state)
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
    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
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
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    val viewModel = QuestionnaireViewModel(state)

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

    val serializedQuestionnaire = printer.encodeResourceToString(questionnaire)
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
    state.set(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE, serializedQuestionnaire)
    val viewModel = QuestionnaireViewModel(state)

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

  private fun createQuestionnaireViewModel(
    questionnaire: Questionnaire,
    response: QuestionnaireResponse? = null
  ): QuestionnaireViewModel {
    state.set(
      QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE,
      printer.encodeResourceToString(questionnaire)
    )
    if (response != null) {
      state.set(
        QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE_RESPONSE,
        printer.encodeResourceToString(response)
      )
    }
    return QuestionnaireViewModel(state)
  }

  private suspend fun QuestionnaireViewModel.getQuestionnaireItemViewItemList() =
    questionnaireStateFlow.first().items

  private companion object {
    val printer: IParser = FhirContext.forR4().newJsonParser()
    fun assertResourceEquals(r1: IBaseResource, r2: IBaseResource) {
      assertThat(printer.encodeResourceToString(r1)).isEqualTo(printer.encodeResourceToString(r2))
    }
  }
}
