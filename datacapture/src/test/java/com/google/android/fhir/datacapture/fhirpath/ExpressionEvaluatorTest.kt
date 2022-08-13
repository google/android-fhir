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

package com.google.android.fhir.datacapture.fhirpath

import com.google.android.fhir.datacapture.EXTENSION_VARIABLE_URL
import com.google.android.fhir.datacapture.common.datatype.asStringValue
import com.google.android.fhir.datacapture.variableExpressions
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type
import org.junit.Assert.assertThrows
import org.junit.Test

class ExpressionEvaluatorTest {
  @Test
  fun `should return not null value with simple variable expression for questionnaire root level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "A"
              language = "text/fhirpath"
              expression = "1"
            }
          )
        }
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
        questionnaire.variableExpressions.first(),
        questionnaire,
        QuestionnaireResponse()
      )

    assertThat((result as Type).asStringValue()).isEqualTo("1")
  }

  @Test
  fun `should return not null value with variables dependent on other variables for questionnaire root level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "A"
              language = "text/fhirpath"
              expression = "1"
            }
          )
        }
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "B"
              language = "text/fhirpath"
              expression = "%A + 1"
            }
          )
        }
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
        questionnaire.variableExpressions.last(),
        questionnaire,
        QuestionnaireResponse()
      )

    assertThat((result as Type).asStringValue()).isEqualTo("2")
  }

  @Test
  fun `should return not null value with variables dependent on other variables in parent for questionnaire item level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-group-item"
            text = "a question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "A"
                  language = "text/fhirpath"
                  expression = "1"
                }
              )
            }
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "an-item"
                text = "a question"
                type = Questionnaire.QuestionnaireItemType.TEXT
                addExtension().apply {
                  url = EXTENSION_VARIABLE_URL
                  setValue(
                    Expression().apply {
                      name = "B"
                      language = "text/fhirpath"
                      expression = "%A + 1"
                    }
                  )
                }
              }
            )
          }
        )
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireItemVariableExpression(
        questionnaire.item[0].item[0].variableExpressions.last(),
        questionnaire,
        QuestionnaireResponse(),
        mapOf(questionnaire.item[0].item[0] to questionnaire.item[0]),
        questionnaire.item[0].item[0]
      )

    assertThat((result as Type).asStringValue()).isEqualTo("2")
  }

  @Test
  fun `should return not null value with variables dependent on multiple variables for questionnaire root level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "A"
              language = "text/fhirpath"
              expression = "1"
            }
          )
        }
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "B"
              language = "text/fhirpath"
              expression = "2"
            }
          )
        }
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "C"
              language = "text/fhirpath"
              expression = "%A + %B"
            }
          )
        }
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
        questionnaire.variableExpressions.last(),
        questionnaire,
        QuestionnaireResponse()
      )

    assertThat((result as Type).asStringValue()).isEqualTo("3")
  }

  @Test
  fun `should return null with variables dependent on missing variables for questionnaire root level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "A"
              language = "text/fhirpath"
              expression = "%B + 1"
            }
          )
        }
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
        questionnaire.variableExpressions.last(),
        questionnaire,
        QuestionnaireResponse()
      )

    assertThat(result).isEqualTo(null)
  }

  @Test
  fun `should return not null value with variables dependent on other variables at origin for questionnaire item level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "an-item"
            text = "a question"
            type = Questionnaire.QuestionnaireItemType.TEXT
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "B"
                  language = "text/fhirpath"
                  expression = "1"
                }
              )
            }
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "A"
                  language = "text/fhirpath"
                  expression = "%B + 1"
                }
              )
            }
          }
        )
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireItemVariableExpression(
        questionnaire.item[0].variableExpressions.last(),
        questionnaire,
        QuestionnaireResponse(),
        mapOf(),
        questionnaire.item[0]
      )

    assertThat((result as Type).asStringValue()).isEqualTo("2")
  }

  @Test
  fun `should return null with variables dependent on missing variables at origin for questionnaire item level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "an-item"
            text = "a question"
            type = Questionnaire.QuestionnaireItemType.TEXT
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "A"
                  language = "text/fhirpath"
                  expression = "%B + 1"
                }
              )
            }
          }
        )
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireItemVariableExpression(
        questionnaire.item[0].variableExpressions.last(),
        questionnaire,
        QuestionnaireResponse(),
        mapOf(),
        questionnaire.item[0]
      )

    assertThat(result).isEqualTo(null)
  }

  @Test
  fun `should throw illegal argument exception with missing expression name for questionnaire variables`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        val questionnaire =
          Questionnaire().apply {
            id = "a-questionnaire"
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  language = "text/fhirpath"
                  expression = "%resource.repeat(item).where(linkId='an-item').answer.first().value"
                }
              )
            }
          }

        ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
          questionnaire,
          QuestionnaireResponse()
        )
      }
    }
  }

  @Test
  fun `should throw illegal argument exception with missing exception language for questionnaire variables`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        val questionnaire =
          Questionnaire().apply {
            id = "a-questionnaire"
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "X"
                  expression = "1"
                }
              )
            }
          }

        ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
          questionnaire,
          QuestionnaireResponse()
        )
      }
    }
  }

  @Test
  fun `should throw illegal argument exception with unsupported expression language for questionnaire variables`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        val questionnaire =
          Questionnaire().apply {
            id = "a-questionnaire"
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "X"
                  expression = "1"
                  language = "application/x-fhir-query"
                }
              )
            }
          }

        ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
          questionnaire,
          QuestionnaireResponse()
        )
      }
    }
  }

  @Test
  fun `should throw null pointer exception with missing expression for questionnaire variables`() {
    assertThrows(NullPointerException::class.java) {
      runBlocking {
        val questionnaire =
          Questionnaire().apply {
            id = "a-questionnaire"
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "X"
                  language = "text/fhirpath"
                }
              )
            }
          }

        ExpressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
          questionnaire,
          QuestionnaireResponse()
        )
      }
    }
  }

  @Test
  fun `should return not null value with expression dependent on answers of items for questionnaire item level`() =
      runBlocking {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-group-item"
            text = "a question"
            type = Questionnaire.QuestionnaireItemType.GROUP
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "M"
                  language = "text/fhirpath"
                  expression = "%resource.repeat(item).where(linkId='an-item').answer.first().value"
                }
              )
            }
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "an-item"
                text = "a question"
                type = Questionnaire.QuestionnaireItemType.TEXT
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
            linkId = "an-item"
            addAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = IntegerType(2)
              }
            )
          }
        )
      }

    val result =
      ExpressionEvaluator.evaluateQuestionnaireItemVariableExpression(
        questionnaire.item[0].variableExpressions.last(),
        questionnaire,
        questionnaireResponse,
        mapOf(),
        questionnaire.item[0]
      )

    assertThat((result as Type).asStringValue()).isEqualTo("2")
  }
}
