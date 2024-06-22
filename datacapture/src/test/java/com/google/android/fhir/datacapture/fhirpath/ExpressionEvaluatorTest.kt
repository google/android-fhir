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

package com.google.android.fhir.datacapture.fhirpath

import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.datacapture.extensions.EXTENSION_ANSWER_EXPRESSION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_CALCULATED_EXPRESSION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_VARIABLE_URL
import com.google.android.fhir.datacapture.extensions.answerExpression
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.extensions.variableExpressions
import com.google.common.truth.Truth.assertThat
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Location
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.Quantity
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
              },
            )
          }
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
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
              },
            )
          }
          addExtension().apply {
            url = EXTENSION_VARIABLE_URL
            setValue(
              Expression().apply {
                name = "B"
                language = "text/fhirpath"
                expression = "%A + 1"
              },
            )
          }
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.last(),
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
                  },
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
                      },
                    )
                  }
                },
              )
            },
          )
        }

      val expressionEvaluator =
        ExpressionEvaluator(
          questionnaire,
          QuestionnaireResponse(),
          mapOf(questionnaire.item[0].item[0] to questionnaire.item[0]),
        )

      val result =
        expressionEvaluator.evaluateQuestionnaireItemVariableExpression(
          questionnaire.item[0].item[0].variableExpressions.last(),
          questionnaire.item[0].item[0],
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
              },
            )
          }
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
          addExtension().apply {
            url = EXTENSION_VARIABLE_URL
            setValue(
              Expression().apply {
                name = "C"
                language = "text/fhirpath"
                expression = "%A + %B"
              },
            )
          }
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.last(),
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
              },
            )
          }
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.last(),
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
                  },
                )
              }
              addExtension().apply {
                url = EXTENSION_VARIABLE_URL
                setValue(
                  Expression().apply {
                    name = "A"
                    language = "text/fhirpath"
                    expression = "%B + 1"
                  },
                )
              }
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireItemVariableExpression(
          questionnaire.item[0].variableExpressions.last(),
          questionnaire.item[0],
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
                  },
                )
              }
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireItemVariableExpression(
          questionnaire.item[0].variableExpressions.last(),
          questionnaire.item[0],
        )

      assertThat(result).isEqualTo(null)
    }

  @Test
  fun `resource should exists with variable expression that uses x-fhir-query`() = runBlocking {
    val questionnaire =
      Questionnaire().apply {
        addExtension().apply {
          url = EXTENSION_VARIABLE_URL
          setValue(
            Expression().apply {
              name = "A"
              language = "application/x-fhir-query"
              expression = "Patient?name=fikri"
            },
          )
        }
      }

    val patient = Patient().apply { addName().apply { addGiven("fikri") } }

    val expressionEvaluator =
      ExpressionEvaluator(
        questionnaire,
        QuestionnaireResponse(),
        xFhirQueryResolver =
          XFhirQueryResolver {
            return@XFhirQueryResolver listOf(patient)
          },
      )

    val result =
      expressionEvaluator.evaluateQuestionnaireVariableExpression(
        questionnaire.variableExpressions.first(),
      )

    val resultAsResourceList = (result as Bundle).entry.map { it.resource }
    assertThat(resultAsResourceList).contains(patient)
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
                },
              )
            }
          }

        val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
        )
      }
    }
  }

  @Test
  fun `should throw illegal argument exception with missing exception language for questionnaire variables`() {
    assertThrows(UnsupportedOperationException::class.java) {
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
                },
              )
            }
          }

        val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
        )
      }
    }
  }

  @Test
  fun `should throw illegal state exception with missing x-fhir-query resolver for questionnaire variables`() {
    assertThrows(IllegalStateException::class.java) {
      runBlocking {
        val questionnaire =
          Questionnaire().apply {
            id = "a-questionnaire"
            addExtension().apply {
              url = EXTENSION_VARIABLE_URL
              setValue(
                Expression().apply {
                  name = "X"
                  expression = "Patient?name=fikri"
                  language = "application/x-fhir-query"
                },
              )
            }
          }

        val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
        )
      }
    }
  }

  @Test
  fun `should throw unsupported operation exception with cql expression language for questionnaire variables`() {
    assertThrows(UnsupportedOperationException::class.java) {
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
                  language = "text/cql"
                },
              )
            }
          }

        val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
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
                },
              )
            }
          }

        val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

        expressionEvaluator.evaluateQuestionnaireVariableExpression(
          questionnaire.variableExpressions.first(),
        )
      }
    }
  }

  @Test
  fun `should return not null value with expression for %questionnaire fhirpath supplement`() =
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          identifier =
            listOf(
              Identifier().apply { value = "q-identifier" },
            )

          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-item"
              text = "a question"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension().apply {
                url = EXTENSION_VARIABLE_URL
                setValue(
                  Expression().apply {
                    name = "M"
                    language = "text/fhirpath"
                    expression = "%questionnaire.identifier.first().value"
                  },
                )
              }
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireItemVariableExpression(
          questionnaire.item[0].variableExpressions.last(),
          questionnaire.item[0],
        )

      assertThat((result as Type).asStringValue()).isEqualTo("q-identifier")
    }

  @Test
  fun `should return not null value with expression for %qItem fhirpath supplement`() =
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"

          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-item"
              text = "a question"
              type = Questionnaire.QuestionnaireItemType.GROUP
              addExtension().apply {
                url = EXTENSION_VARIABLE_URL
                setValue(
                  Expression().apply {
                    name = "M"
                    language = "text/fhirpath"
                    expression = "%qItem.text"
                  },
                )
              }
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateQuestionnaireItemVariableExpression(
          questionnaire.item[0].variableExpressions.last(),
          questionnaire.item[0],
        )

      assertThat((result as Type).asStringValue()).isEqualTo("a question")
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
                    expression =
                      "%resource.repeat(item).where(linkId='an-item').answer.first().value"
                  },
                )
              }
              addItem(
                Questionnaire.QuestionnaireItemComponent().apply {
                  linkId = "an-item"
                  text = "a question"
                  type = Questionnaire.QuestionnaireItemType.TEXT
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
              linkId = "an-item"
              addAnswer(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = IntegerType(2)
                },
              )
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, questionnaireResponse)

      val result =
        expressionEvaluator.evaluateQuestionnaireItemVariableExpression(
          questionnaire.item[0].variableExpressions.last(),
          questionnaire.item[0],
        )

      assertThat((result as Type).asStringValue()).isEqualTo("2")
    }

  @Test
  fun `evaluateCalculatedExpressions should return list of calculated values`() = runBlocking {
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
                },
              )
            }
          },
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-age-years"
            type = Questionnaire.QuestionnaireItemType.QUANTITY
          },
        )
      }

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-birthdate"
          },
        )
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-age-years"
            answer =
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  this.value = Quantity(1).apply { unit = "year" }
                },
              )
          },
        )
      }

    val expressionEvaluator = ExpressionEvaluator(questionnaire, questionnaireResponse)

    val result =
      expressionEvaluator.evaluateCalculatedExpressions(
        questionnaire.item.elementAt(1),
        questionnaireResponse.item.elementAt(1),
      )

    assertThat(result.first().second.first().asStringValue())
      .isEqualTo(DateType(Date()).apply { add(Calendar.YEAR, -1) }.asStringValue())
  }

  @Test
  fun `evaluateCalculatedExpressions should return list of calculated values with variables`() =
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          addExtension().apply {
            url = EXTENSION_VARIABLE_URL
            setValue(
              Expression().apply {
                name = "AGE-YEARS"
                language = "text/fhirpath"
                expression =
                  "%resource.repeat(item).where(linkId='a-age-years' and answer.empty().not()).select(today() - answer.value)"
              },
            )
          }
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-birthdate"
              type = Questionnaire.QuestionnaireItemType.DATE
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression = "%AGE-YEARS"
                  },
                )
              }
            },
          )
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.QUANTITY
            },
          )
        }

      val questionnaireResponse =
        QuestionnaireResponse().apply {
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-birthdate"
            },
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-age-years"
              answer =
                listOf(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    this.value = Quantity(1).apply { unit = "year" }
                  },
                )
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, questionnaireResponse)

      val result =
        expressionEvaluator.evaluateCalculatedExpressions(
          questionnaire.item.elementAt(1),
          questionnaireResponse.item.elementAt(1),
        )

      assertThat(result.first().second.first().asStringValue())
        .isEqualTo(DateType(Date()).apply { add(Calendar.YEAR, -1) }.asStringValue())
    }

  @Test
  fun `evaluateCalculatedExpressions should return list of calculated values with fhirpath supplement %questionnaire`() =
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          identifier = listOf(Identifier().apply { value = "Questionnaire A" })

          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-questionnaire-reason"
              text = "Reason"
              type = Questionnaire.QuestionnaireItemType.STRING
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression = "%questionnaire.identifier.first().value"
                  },
                )
              }
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateCalculatedExpressions(
          questionnaire.item.elementAt(0),
          null,
        )

      assertThat(result.first().second.first().asStringValue()).isEqualTo("Questionnaire A")
    }

  @Test
  fun `evaluateCalculatedExpressions should return list of calculated values with fhirpath supplement %qItem`() =
    runBlocking {
      val questionnaire =
        Questionnaire().apply {
          id = "a-questionnaire"
          identifier = listOf(Identifier().apply { value = "Questionnaire A" })

          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-questionnaire-reason"
              text = "Reason"
              type = Questionnaire.QuestionnaireItemType.STRING
              addExtension().apply {
                url = EXTENSION_CALCULATED_EXPRESSION_URL
                setValue(
                  Expression().apply {
                    this.language = "text/fhirpath"
                    this.expression = "'Question = ' + %qItem.text"
                  },
                )
              }
            },
          )
        }

      val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

      val result =
        expressionEvaluator.evaluateCalculatedExpressions(
          questionnaire.item.elementAt(0),
          null,
        )

      assertThat(result.first().second.first().asStringValue()).isEqualTo("Question = Reason")
    }

  @Test
  fun `detectExpressionCyclicDependency() should throw illegal argument exception when item with calculated expression have cyclic dependency`() {
    val questionnaire =
      Questionnaire().apply {
        id = "a-questionnaire"
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
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
                },
              )
            }
          },
        )
      }

    val expressionEvaluator = ExpressionEvaluator(questionnaire, QuestionnaireResponse())

    val exception =
      assertThrows(null, IllegalStateException::class.java) {
        expressionEvaluator.detectExpressionCyclicDependency(questionnaire.item)
      }
    assertThat(exception.message)
      .isEqualTo("a-birthdate and a-age-years have cyclic dependency in expression based extension")
  }

  @Test
  fun `createXFhirQueryFromExpression() should capture all FHIR paths`() {
    val expression =
      Expression().apply {
        this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
        this.expression =
          "Practitioner?var1={{random}}&var2={{  random   }}&var3={{ random}}&var4={{random }}"
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        Questionnaire(),
        QuestionnaireResponse(),
        questionnaireItemParentMap = emptyMap(),
        questionnaireLaunchContextMap =
          mapOf(Practitioner().resourceType.name.lowercase() to Practitioner()),
      )

    val expressionsToEvaluate =
      expressionEvaluator.createXFhirQueryFromExpression(
        expression,
      )

    assertThat(expressionsToEvaluate).isEqualTo("Practitioner?var1=&var2=&var3=&var4=")
  }

  @Test
  fun `createXFhirQueryFromExpression() should evaluate to empty string for field that does not exist in resource`() {
    val practitioner =
      Practitioner().apply {
        id = UUID.randomUUID().toString()
        active = true
        addName(HumanName().apply { this.family = "John" })
      }

    val expression =
      Expression().apply {
        this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
        this.expression = "Practitioner?gender={{Practitioner.gender}}"
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        Questionnaire(),
        QuestionnaireResponse(),
        questionnaireItemParentMap = emptyMap(),
        questionnaireLaunchContextMap = mapOf(practitioner.resourceType.name to practitioner),
      )

    val expressionsToEvaluate =
      expressionEvaluator.createXFhirQueryFromExpression(
        expression,
      )
    assertThat(expressionsToEvaluate).isEqualTo("Practitioner?gender=")
  }

  @Test
  fun `createXFhirQueryFromExpression() should evaluate correct expression`() {
    val practitioner =
      Practitioner().apply {
        id = UUID.randomUUID().toString()
        active = true
        gender = Enumerations.AdministrativeGender.MALE
        addName(HumanName().apply { this.family = "John" })
      }

    val expression =
      Expression().apply {
        this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
        this.expression = "Practitioner?gender={{Practitioner.gender}}"
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        Questionnaire(),
        QuestionnaireResponse(),
        questionnaireItemParentMap = emptyMap(),
        questionnaireLaunchContextMap =
          mapOf(practitioner.resourceType.name.lowercase() to practitioner),
      )

    val expressionsToEvaluate =
      expressionEvaluator.createXFhirQueryFromExpression(
        expression,
      )
    assertThat(expressionsToEvaluate).isEqualTo("Practitioner?gender=male")
  }

  @Test
  fun `createXFhirQueryFromExpression() should return empty string if the resource provided does not match the type in the expression`() {
    val practitioner =
      Practitioner().apply {
        id = UUID.randomUUID().toString()
        active = true
        gender = Enumerations.AdministrativeGender.MALE
        addName(HumanName().apply { this.family = "John" })
      }

    val expression =
      Expression().apply {
        this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
        this.expression = "Practitioner?gender={{%patient.gender}}"
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        Questionnaire(),
        QuestionnaireResponse(),
        questionnaireItemParentMap = emptyMap(),
        questionnaireLaunchContextMap = mapOf(practitioner.resourceType.name to practitioner),
      )

    val expressionsToEvaluate =
      expressionEvaluator.createXFhirQueryFromExpression(
        expression,
      )
    assertThat(expressionsToEvaluate).isEqualTo("Practitioner?gender=")
  }

  @Test
  fun `createXFhirQueryFromExpression() should evaluate fhirPath with percent sign`() {
    val patient =
      Patient().apply {
        id = UUID.randomUUID().toString()
        active = true
        gender = Enumerations.AdministrativeGender.MALE
        addName(HumanName().apply { this.family = "John" })
      }

    val expression =
      Expression().apply {
        this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
        this.expression = "Patient?family={{%patient.name.family}}"
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        Questionnaire(),
        QuestionnaireResponse(),
        questionnaireItemParentMap = emptyMap(),
        questionnaireLaunchContextMap = mapOf(patient.resourceType.name.lowercase() to patient),
      )

    val expressionsToEvaluate =
      expressionEvaluator.createXFhirQueryFromExpression(
        expression,
      )
    assertThat(expressionsToEvaluate).isEqualTo("Patient?family=John")
  }

  @Test
  fun `createXFhirQueryFromExpression() should evaluate when multiple fhir paths are given`() {
    val patient =
      Patient().apply {
        id = UUID.randomUUID().toString()
        active = true
        gender = Enumerations.AdministrativeGender.MALE
        addName(HumanName().apply { this.family = "John" })
      }

    val location =
      Location().apply {
        id = UUID.randomUUID().toString()
        status = Location.LocationStatus.ACTIVE
        mode = Location.LocationMode.INSTANCE
        address =
          Address().apply {
            use = Address.AddressUse.HOME
            type = Address.AddressType.PHYSICAL
            city = "NAIROBI"
          }
      }

    val expression =
      Expression().apply {
        this.language = Expression.ExpressionLanguage.APPLICATION_XFHIRQUERY.toCode()
        this.expression =
          "Patient?family={{%patient.name.family}}&address-city={{%location.address.city}}"
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        Questionnaire(),
        QuestionnaireResponse(),
        questionnaireItemParentMap = emptyMap(),
        questionnaireLaunchContextMap =
          mapOf(
            patient.resourceType.name.lowercase() to patient,
            location.resourceType.name.lowercase() to location,
          ),
      )

    val expressionsToEvaluate =
      expressionEvaluator.createXFhirQueryFromExpression(
        expression,
      )
    assertThat(expressionsToEvaluate).isEqualTo("Patient?family=John&address-city=NAIROBI")
  }

  @Test
  fun `createXFhirQueryFromExpression() should evaluate variables in answer expression when launch context is null`() {
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
                },
              )
            }
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
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "an-item"
                text = "a question"
                type = Questionnaire.QuestionnaireItemType.TEXT
                addExtension().apply {
                  url = EXTENSION_ANSWER_EXPRESSION_URL
                  setValue(
                    Expression().apply {
                      language = "application/x-fhir-query"
                      expression = "Patient?address-city={{%A}}&gender={{%B}}"
                    },
                  )
                }
              },
            )
          },
        )
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        questionnaire,
        QuestionnaireResponse(),
        questionnaireItemParentMap = mapOf(questionnaire.item[0].item[0] to questionnaire.item[0]),
        questionnaireLaunchContextMap = null,
      )

    runBlocking {
      val variablesMap =
        expressionEvaluator.extractItemDependentVariables(
          questionnaire.item[0].item[0].answerExpression!!,
          questionnaire.item[0],
        )

      val result =
        expressionEvaluator.createXFhirQueryFromExpression(
          questionnaire.item[0].item[0].answerExpression!!,
          variablesMap,
        )

      assertThat(result).isEqualTo("Patient?address-city=1&gender=2")
    }
  }

  @Test
  fun `createXFhirQueryFromExpression() should evaluate variables in answer expression when launch context in empty`() {
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
                },
              )
            }
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
            addItem(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "an-item"
                text = "a question"
                type = Questionnaire.QuestionnaireItemType.TEXT
                addExtension().apply {
                  url = EXTENSION_ANSWER_EXPRESSION_URL
                  setValue(
                    Expression().apply {
                      language = "application/x-fhir-query"
                      expression = "Patient?address-city={{%A}}&gender={{%B}}"
                    },
                  )
                }
              },
            )
          },
        )
      }

    val expressionEvaluator =
      ExpressionEvaluator(
        questionnaire,
        QuestionnaireResponse(),
        questionnaireItemParentMap = mapOf(questionnaire.item[0].item[0] to questionnaire.item[0]),
        questionnaireLaunchContextMap = emptyMap(),
      )

    runBlocking {
      val variablesMap =
        expressionEvaluator.extractItemDependentVariables(
          questionnaire.item[0].item[0].answerExpression!!,
          questionnaire.item[0],
        )

      val result =
        expressionEvaluator.createXFhirQueryFromExpression(
          questionnaire.item[0].item[0].answerExpression!!,
          variablesMap,
        )

      assertThat(result).isEqualTo("Patient?address-city=1&gender=2")
    }
  }
}
