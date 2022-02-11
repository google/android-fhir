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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.UrlType
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireResponseValidatorTest {

  lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun validateQuestionnaireResponseAnswers_shouldReturnValidResult() {
    val questionnaire =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent()
            .setLinkId("a-question")
            .setMaxLength(3)
            .setType(Questionnaire.QuestionnaireItemType.INTEGER)
            .setText("Age in years?")
        )
    val questionnaireResponse =
      QuestionnaireResponse()
        .addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
            .setLinkId("a-question")
            .setAnswer(
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                  .setValue(IntegerType(3))
              )
            )
        )
    val result =
      QuestionnaireResponseValidator.validateQuestionnaireResponseAnswers(
        questionnaire.item,
        questionnaireResponse.item,
        context
      )
    assertThat(result["a-question"]).isEqualTo(listOf(ValidationResult(true, listOf())))
  }

  @Test
  fun validateQuestionnaireResponseAnswers_shouldReturnInvalidResultWithMessages() {
    val questionnaire =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent()
            .setLinkId("a-question")
            .setMaxLength(3)
            .setType(Questionnaire.QuestionnaireItemType.INTEGER)
            .setText("Age in years?")
        )
    val questionnaireResponse =
      QuestionnaireResponse()
        .addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
            .setLinkId("a-question")
            .setAnswer(
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                  .setValue(IntegerType(1000))
              )
            )
        )
    val result =
      QuestionnaireResponseValidator.validateQuestionnaireResponseAnswers(
        questionnaire.item,
        questionnaireResponse.item,
        context
      )
    assertThat(result["a-question"])
      .isEqualTo(
        listOf(
          ValidationResult(
            false,
            listOf("The maximum number of characters that are permitted in the answer is: 3")
          )
        )
      )
  }

  @Test
  fun validateQuestionnaireResponseAnswers_shouldReturnInvalidResultWithMessages_forNestedItems() {
    val questionnaire =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent()
            .setLinkId("a-question")
            .setMaxLength(3)
            .setType(Questionnaire.QuestionnaireItemType.INTEGER)
            .setText("Age in years?")
            .addItem(
              Questionnaire.QuestionnaireItemComponent()
                .setLinkId("a-nested-question")
                .setMaxLength(3)
                .setType(Questionnaire.QuestionnaireItemType.STRING)
                .setText("Country code")
            )
        )
    val questionnaireResponse =
      QuestionnaireResponse()
        .addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent()
            .setLinkId("a-question")
            .setAnswer(
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                  .setValue(IntegerType(1000))
                  .addItem(
                    QuestionnaireResponse.QuestionnaireResponseItemComponent()
                      .setLinkId("a-nested-question")
                      .setAnswer(
                        listOf(
                          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
                            .setValue(StringType("ABCD"))
                        )
                      )
                  )
              )
            )
        )
    val result =
      QuestionnaireResponseValidator.validateQuestionnaireResponseAnswers(
        questionnaire.item,
        questionnaireResponse.item,
        context
      )
    assertThat(result["a-question"])
      .containsExactly(
        ValidationResult(
          false,
          listOf("The maximum number of characters that are permitted in the answer is: 3")
        )
      )
    assertThat(result["a-nested-question"])
      .containsExactly(
        ValidationResult(
          false,
          listOf("The maximum number of characters that are permitted in the answer is: 3")
        )
      )
  }

  @Test
  fun `check passes if questionnaire response matches questionnaire`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "http://www.sample-org/FHIR/Resources/Questionnaire/questionnaire-1"
      },
      QuestionnaireResponse().apply {
        questionnaire = "http://www.sample-org/FHIR/Resources/Questionnaire/questionnaire-1"
      }
    )
  }

  @Test
  fun `check fails if questionnaire response does not match questionnaire`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply { url = "questionnaire-1" },
      QuestionnaireResponse().apply { questionnaire = "questionnaire-2" },
      "Mismatching Questionnaire questionnaire-1 and QuestionnaireResponse (for Questionnaire questionnaire-2)"
    )
  }

  @Test
  fun `check passes if questionnaire response does not specify questionnaire`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply { url = "questionnaire-1" },
      QuestionnaireResponse()
    )
  }

  @Test
  fun `check fails if questionnaire has no questionnaire item`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply { url = "questionnaire-1" },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")))
      },
      "Missing questionnaire item for questionnaire response item question-1"
    )
  }

  @Test
  fun `check fails if there is no questionnaire item for the questionnaire response item`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-2"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.STRING
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")))
      },
      "Missing questionnaire item for questionnaire response item question-1"
    )
  }

  @Test
  fun `check passes for questionnaire item type DISPLAY`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("display-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.DISPLAY
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("display-1")))
      }
    )
  }

  @Test
  fun `check passes for questionnaire item type NULL`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("null-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.NULL
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("null-1")))
      }
    )
  }

  @Test
  fun `check recursively for questionnaire item type GROUP`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("group-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.GROUP
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("group-1")).apply {
            addItem(
              QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1"))
            )
          }
        )
      },
      "Missing questionnaire item for questionnaire response item question-1"
    )
  }

  @Test
  fun `check fails if there are too many answers`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.INTEGER
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = IntegerType(1)
              }
            )
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = IntegerType(2)
              }
            )
          }
        )
      },
      "Multiple answers for non-repeat questionnaire item question-1"
    )
  }

  @Test
  fun `check passes if boolean type question has boolean type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.BOOLEAN
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
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
  fun `check fails if boolean type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.BOOLEAN
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = IntegerType(1)
              }
            )
          }
        )
      },
      "Mismatching question type BOOLEAN and answer type integer for question-1"
    )
  }

  @Test
  fun `check passes if decimal type question has decimal type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.DECIMAL
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if decimal type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.DECIMAL
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = IntegerType(1)
              }
            )
          }
        )
      },
      "Mismatching question type DECIMAL and answer type integer for question-1"
    )
  }

  @Test
  fun `check passes if integer type question has integer type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.INTEGER
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = IntegerType(1)
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if integer type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.INTEGER
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type INTEGER and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if date type question has date type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.DATE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DateType("1900-01-01")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if date type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.DATE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type DATE and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if datetime type question has datetime type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.DATETIME
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DateTimeType("1990-01-01")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if datetime type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.DATETIME
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type DATETIME and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if time type question has time type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.TIME
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = TimeType("10:30.000")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if time type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.TIME
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type TIME and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if string type question has string type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.STRING
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if string type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.STRING
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type STRING and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if text type question has string type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.TEXT
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("Some text")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if text type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.TEXT
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type TEXT and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if url type question has url type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.URL
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = UrlType("http://unitsofmeasure.org")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if url type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.URL
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type URL and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if choice type question has coding type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.CHOICE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = Coding()
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if choice type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.CHOICE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type CHOICE and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if open choice type question has coding type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.OPENCHOICE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = Coding().apply { code = "some code" }
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check passes if open choice type question has string type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.OPENCHOICE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if open choice type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.OPENCHOICE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type OPENCHOICE and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if attachment type question has attachment type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.ATTACHMENT
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = Attachment().apply { id = "some id" }
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if attachment type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.ATTACHMENT
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type ATTACHMENT and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if reference type question has reference type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.REFERENCE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = Reference().apply { id = "non-empty ID" }
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if reference type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.REFERENCE
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type REFERENCE and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if quantity type question has quantity type answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.QUANTITY
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = Quantity().apply { value = BigDecimal("100") }
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if quantity type question has answer of wrong type`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
            StringType("question-1"),
            Enumeration(
              Questionnaire.QuestionnaireItemTypeEnumFactory(),
              Questionnaire.QuestionnaireItemType.QUANTITY
            )
          )
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = DecimalType(1.0)
              }
            )
          }
        )
      },
      "Mismatching question type QUANTITY and answer type decimal for question-1"
    )
  }

  @Test
  fun `check passes if required questionnaire item has answer`() {
    QuestionnaireResponseValidator.checkQuestionnaireResponse(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
              StringType("question-1"),
              Enumeration(
                Questionnaire.QuestionnaireItemTypeEnumFactory(),
                Questionnaire.QuestionnaireItemType.STRING
              )
            )
            .apply { required = true }
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")).apply {
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType("answer-1")
              }
            )
          }
        )
      }
    )
  }

  @Test
  fun `check fails if required questionnaire item does not have answer`() {
    assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
      Questionnaire().apply {
        url = "questionnaire-1"
        addItem(
          Questionnaire.QuestionnaireItemComponent(
              StringType("question-1"),
              Enumeration(
                Questionnaire.QuestionnaireItemTypeEnumFactory(),
                Questionnaire.QuestionnaireItemType.STRING
              )
            )
            .apply { required = true }
        )
      },
      QuestionnaireResponse().apply {
        questionnaire = "questionnaire-1"
        addItem(QuestionnaireResponse.QuestionnaireResponseItemComponent(StringType("question-1")))
      },
      "Questionnaire response item question-1 must have answer"
    )
  }

  private fun assertCheckQuestionnaireResponseThrowsIllegalArgumentException(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    message: String
  ) {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        QuestionnaireResponseValidator.checkQuestionnaireResponse(
          questionnaire,
          questionnaireResponse
        )
      }
    assertThat(exception.message).isEqualTo(message)
  }
}
