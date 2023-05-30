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

import com.google.android.fhir.datacapture.extensions.EXTENSION_CALCULATED_EXPRESSION_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_VARIABLE_URL
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.extensions.variableExpressions
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator.detectExpressionCyclicDependency
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator.evaluateCalculatedExpressions
import com.google.common.truth.Truth.assertThat
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.HumanName
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
                    expression =
                      "%resource.repeat(item).where(linkId='an-item').answer.first().value"
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
                }
              )
            }
          }
        )
        addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "a-age-years"
            type = Questionnaire.QuestionnaireItemType.QUANTITY
          }
        )
      }

    val questionnaireResponse =
      QuestionnaireResponse().apply {
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-birthdate"
          }
        )
        addItem(
          QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
            linkId = "a-age-years"
            answer =
              listOf(
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  this.value = Quantity(1).apply { unit = "year" }
                }
              )
          }
        )
      }

    val result =
      evaluateCalculatedExpressions(
        questionnaire.item.elementAt(1),
        questionnaireResponse.item.elementAt(1),
        questionnaire,
        questionnaireResponse,
        emptyMap()
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
              }
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
                  }
                )
              }
            }
          )
          addItem(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "a-age-years"
              type = Questionnaire.QuestionnaireItemType.QUANTITY
            }
          )
        }

      val questionnaireResponse =
        QuestionnaireResponse().apply {
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-birthdate"
            }
          )
          addItem(
            QuestionnaireResponse.QuestionnaireResponseItemComponent().apply {
              linkId = "a-age-years"
              answer =
                listOf(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    this.value = Quantity(1).apply { unit = "year" }
                  }
                )
            }
          )
        }

      val result =
        evaluateCalculatedExpressions(
          questionnaire.item.elementAt(1),
          questionnaireResponse.item.elementAt(1),
          questionnaire,
          questionnaireResponse,
          emptyMap()
        )

      assertThat(result.first().second.first().asStringValue())
        .isEqualTo(DateType(Date()).apply { add(Calendar.YEAR, -1) }.asStringValue())
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
        detectExpressionCyclicDependency(questionnaire.item)
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

    val expressionsToEvaluate =
      ExpressionEvaluator.createXFhirQueryFromExpression(
        expression,
        mapOf(Practitioner().resourceType.name.lowercase() to Practitioner())
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

    val expressionsToEvaluate =
      ExpressionEvaluator.createXFhirQueryFromExpression(
        expression,
        mapOf(practitioner.resourceType.name to practitioner)
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

    val expressionsToEvaluate =
      ExpressionEvaluator.createXFhirQueryFromExpression(
        expression,
        mapOf(practitioner.resourceType.name.lowercase() to practitioner)
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

    val expressionsToEvaluate =
      ExpressionEvaluator.createXFhirQueryFromExpression(
        expression,
        mapOf(practitioner.resourceType.name to practitioner)
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

    val expressionsToEvaluate =
      ExpressionEvaluator.createXFhirQueryFromExpression(
        expression,
        mapOf(patient.resourceType.name.lowercase() to patient)
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

    val expressionsToEvaluate =
      ExpressionEvaluator.createXFhirQueryFromExpression(
        expression,
        mapOf(
          patient.resourceType.name.lowercase() to patient,
          location.resourceType.name.lowercase() to location
        )
      )
    assertThat(expressionsToEvaluate).isEqualTo("Patient?family=John&address-city=NAIROBI")
  }
}
