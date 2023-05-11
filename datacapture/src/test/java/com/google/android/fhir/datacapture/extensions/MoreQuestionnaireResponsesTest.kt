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

package com.google.android.fhir.datacapture.extensions

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.junit.Test

class MoreQuestionnaireResponsesTest {

  @Test
  fun `allItems should return all items in pre-order`() {
    val questionnaireResponseItem11 = QuestionnaireResponseItemComponent().apply { linkId = "1.1" }
    val questionnaireResponseItem12 = QuestionnaireResponseItemComponent().apply { linkId = "1.2" }
    val questionnaireResponseItem1 =
      QuestionnaireResponseItemComponent().apply {
        linkId = "1"
        // Nested questions
        addItem(questionnaireResponseItem11)
        addItem(questionnaireResponseItem12)
      }

    val questionnaireResponseItem21 = QuestionnaireResponseItemComponent().apply { linkId = "1.1" }
    val questionnaireResponseItem22 = QuestionnaireResponseItemComponent().apply { linkId = "1.2" }
    val questionnaireResponseItem2 =
      QuestionnaireResponseItemComponent().apply {
        linkId = "2"
        addAnswer(
          QuestionnaireResponseItemAnswerComponent().apply {
            // Nested questions under answer
            addItem(questionnaireResponseItem21)
            addItem(questionnaireResponseItem22)
          }
        )
      }

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(questionnaireResponseItem1)
        addItem(questionnaireResponseItem2)
      }

    assertThat(questionnaireResponse.allItems)
      .containsExactly(
        questionnaireResponseItem1,
        questionnaireResponseItem11,
        questionnaireResponseItem12,
        questionnaireResponseItem2,
        questionnaireResponseItem21,
        questionnaireResponseItem22,
      )
  }

  @Test
  fun `should pack repeated groups`() {
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(false) }
                )
              }
            )
          }
        )
      }

    val packedQuestionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                    )
                  }
                )
              }
            )
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply {
                        value = BooleanType(false)
                      }
                    )
                  }
                )
              }
            )
          }
        )
      }

    questionnaireResponse.packRepeatedGroups()
    assertResourceEquals(questionnaireResponse, packedQuestionnaireResponse)
  }

  @Test
  fun `should not modify other items while packing repeated groups`() {
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "non-repeated-group-1"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-1"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "non-repeated-group-2"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-2"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(false) }
                )
              }
            )
          }
        )
      }

    val packedQuestionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "non-repeated-group-1"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-1"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "non-repeated-group-2"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-2"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(false) }
                )
              }
            )
          }
        )
      }

    questionnaireResponse.packRepeatedGroups()
    assertResourceEquals(questionnaireResponse, packedQuestionnaireResponse)
  }

  @Test
  fun `should unpack repeated groups`() {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "repeated-group"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-question"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                    )
                  }
                )
              }
            )
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply {
                        value = BooleanType(false)
                      }
                    )
                  }
                )
              }
            )
          }
        )
      }
    val unpackedQuestionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(false) }
                )
              }
            )
          }
        )
      }

    questionnaireResponse.unpackRepeatedGroups(questionnaire)
    assertResourceEquals(questionnaireResponse, unpackedQuestionnaireResponse)
  }

  @Test
  fun `should unpack repeated groups correctly with missing questionnaire response items`() {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "simple-question-1"
            type = Questionnaire.QuestionnaireItemType.STRING
          }
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "repeated-group"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-question-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-question-2"
                type = Questionnaire.QuestionnaireItemType.REFERENCE
              }
            )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        // linkId = "simple-question-1" not present due to enablement
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                // linkId = "nested-question-1" not present due to enablement
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question-2"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply {
                        value = Reference().apply { reference = "Patient/123" }
                      }
                    )
                  }
                )
              }
            )
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question-2"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply {
                        value = Reference().apply { reference = "Patient/456" }
                      }
                    )
                  }
                )
              }
            )
          }
        )
      }
    val unpackedQuestionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-2"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply {
                    value = Reference().apply { reference = "Patient/123" }
                  }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-2"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply {
                    value = Reference().apply { reference = "Patient/456" }
                  }
                )
              }
            )
          }
        )
      }

    questionnaireResponse.unpackRepeatedGroups(questionnaire)
    assertResourceEquals(questionnaireResponse, unpackedQuestionnaireResponse)
  }

  @Test
  fun `should not modify other items while unpacking repeated groups`() {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "non-repeated-group-1"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-question-1"
                type = Questionnaire.QuestionnaireItemType.BOOLEAN
              }
            )
          }
        )
        addItem(
          QuestionnaireItemComponent().apply {
            linkId = "repeated-group-2"
            type = Questionnaire.QuestionnaireItemType.GROUP
            repeats = true
            addItem(
              QuestionnaireItemComponent().apply {
                linkId = "nested-question-2"
                QuestionnaireItemComponent().apply {
                  type = Questionnaire.QuestionnaireItemType.BOOLEAN
                }
              }
            )
          }
        )
      }
    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "non-repeated-group-1"
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question-1"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                    )
                  }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group-2"
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question-2"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply {
                        value = BooleanType(false)
                      }
                    )
                  }
                )
              }
            )
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question-2"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                    )
                  }
                )
              }
            )
          }
        )
      }
    val unpackedQuestionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "non-repeated-group-1"
            addAnswer(
              QuestionnaireResponseItemAnswerComponent().apply {
                addItem(
                  QuestionnaireResponseItemComponent().apply {
                    linkId = "nested-question-1"
                    addAnswer(
                      QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                    )
                  }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group-2"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-2"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(false) }
                )
              }
            )
          }
        )
        addItem(
          QuestionnaireResponseItemComponent().apply {
            linkId = "repeated-group-2"
            addItem(
              QuestionnaireResponseItemComponent().apply {
                linkId = "nested-question-2"
                addAnswer(
                  QuestionnaireResponseItemAnswerComponent().apply { value = BooleanType(true) }
                )
              }
            )
          }
        )
      }

    questionnaireResponse.unpackRepeatedGroups(questionnaire)
    assertResourceEquals(questionnaireResponse, unpackedQuestionnaireResponse)
  }

  private val iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  /** Asserts that the `expected` and the `actual` FHIR resources are equal. */
  private fun assertResourceEquals(expected: Resource, actual: Resource) {
    assertThat(iParser.encodeResourceToString(actual))
      .isEqualTo(iParser.encodeResourceToString(expected))
  }
}
