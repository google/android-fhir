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

import android.os.Build
import com.google.android.fhir.datacapture.mapping.ITEM_INITIAL_EXPRESSION_URL
import com.google.common.truth.Truth.assertThat
import java.util.Locale
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreQuestionnaireItemComponentsTest {

  @Test
  fun itemControl_shouldReturnItemControlCodeDropDown() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.CHOICE
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(ItemControlTypes.DROP_DOWN.extensionCode)
                    .setDisplay("Drop Down")
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.itemControl).isEqualTo(ItemControlTypes.DROP_DOWN)
  }

  @Test
  fun itemControl_shouldReturnItemControlCodeRadioButton() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.CHOICE
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(ItemControlTypes.RADIO_BUTTON.extensionCode)
                    .setDisplay("Radio Group")
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.itemControl).isEqualTo(ItemControlTypes.RADIO_BUTTON)
  }

  @Test
  fun itemControl_shouldReturnItemControlCodePhoneNumber() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.STRING
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL_ANDROID_FHIR)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(ItemControlTypes.PHONE_NUMBER.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM_ANDROID_FHIR)
                )
            )
        )
      }

    assertThat(questionnaireItem.itemControl).isEqualTo(ItemControlTypes.PHONE_NUMBER)
  }

  @Test
  fun itemControl_wrongExtensionUrl_shouldReturnNull() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.CHOICE
        addExtension(
          Extension()
            .setUrl("null-test")
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(ItemControlTypes.DROP_DOWN.extensionCode)
                    .setDisplay("Drop Down")
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.itemControl).isNull()
  }

  @Test
  fun itemControl_wrongExtensionCoding_shouldReturnNull() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.CHOICE
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode("null-test")
                    .setDisplay("Drop Down")
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.itemControl).isNull()
  }

  @Test
  fun `displayItemControl should return flyover type`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.STRING
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.FLYOVER.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.displayItemControl).isEqualTo(DisplayItemControlType.FLYOVER)
  }

  @Test
  fun `displayItemControl should return page type`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.STRING
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.PAGE.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.displayItemControl).isEqualTo(DisplayItemControlType.PAGE)
  }

  @Test
  fun `displayItemControl should return null if the extension url is missing`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.STRING
        addExtension(
          Extension()
            .setValue(
              CodeableConcept()
                .addCoding(
                  Coding()
                    .setCode(DisplayItemControlType.PAGE.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.displayItemControl).isNull()
  }

  @Test
  fun `displayItemControl should return null if the extension system is missing`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.STRING
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(Coding().setCode(DisplayItemControlType.PAGE.extensionCode))
            )
        )
      }

    assertThat(questionnaireItem.displayItemControl).isNull()
  }

  @Test
  fun choiceOrientation_shouldReturnVertical() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          EXTENSION_CHOICE_ORIENTATION_URL,
          CodeType(ChoiceOrientationTypes.VERTICAL.extensionCode)
        )
      }
    assertThat(questionnaire.choiceOrientation).isEqualTo(ChoiceOrientationTypes.VERTICAL)
  }

  @Test
  fun choiceOrientation_shouldReturnHorizontal() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          EXTENSION_CHOICE_ORIENTATION_URL,
          CodeType(ChoiceOrientationTypes.HORIZONTAL.extensionCode)
        )
      }
    assertThat(questionnaire.choiceOrientation).isEqualTo(ChoiceOrientationTypes.HORIZONTAL)
  }

  @Test
  fun choiceOrientation_missingExtension_shouldReturnNull() {
    val questionnaire = Questionnaire.QuestionnaireItemComponent()
    assertThat(questionnaire.choiceOrientation).isNull()
  }

  @Test
  fun choiceOrientation_missingOrientation_shouldReturnNull() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_CHOICE_ORIENTATION_URL, CodeType(""))
      }
    assertThat(questionnaire.choiceOrientation).isNull()
  }

  @Test
  fun localizedTextSpanned_noText_shouldReturnNull() {
    assertThat(Questionnaire.QuestionnaireItemComponent().localizedTextSpanned).isNull()
  }

  @Test
  fun localizedTextSpanned_shouldReturnText() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Patient Information in <strong>strong</strong>"
      }
    Locale.setDefault(Locale.US)
    assertThat(questionnaireItem.localizedTextSpanned.toString())
      .isEqualTo("Patient Information in strong")
  }

  @Test
  fun localizedTextSpanned_nonMatchingLocale_shouldReturnText() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Patient Information"
        textElement.apply {
          addExtension(
            Extension(ToolingExtensions.EXT_TRANSLATION).apply {
              addExtension(Extension("lang", StringType("vi-VN")))
              addExtension(Extension("content", StringType("Thông tin bệnh nhân")))
            }
          )
        }
      }
    Locale.setDefault(Locale.US)

    assertThat(questionnaireItem.localizedTextSpanned.toString()).isEqualTo("Patient Information")
  }

  @Test
  fun localizedTextSpanned_matchingLocale_shouldReturnLocalizedText() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Patient Information"
        textElement.apply {
          addExtension(
            Extension(ToolingExtensions.EXT_TRANSLATION).apply {
              addExtension(Extension("lang", StringType("vi-VN")))
              addExtension(Extension("content", StringType("Thông tin bệnh nhân")))
            }
          )
        }
      }
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionnaireItem.localizedTextSpanned.toString()).isEqualTo("Thông tin bệnh nhân")
  }

  @Test
  fun localizedTextSpanned_matchingLocaleWithoutCountryCode_shouldReturnLocalizedText() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        text = "Patient Information"
        textElement.apply {
          addExtension(
            Extension(ToolingExtensions.EXT_TRANSLATION).apply {
              addExtension(Extension("lang", StringType("vi")))
              addExtension(Extension("content", StringType("Thông tin bệnh nhân")))
            }
          )
        }
      }
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionnaireItem.localizedTextSpanned.toString()).isEqualTo("Thông tin bệnh nhân")
  }

  @Test
  fun localizedPrefixSpanned_noPrefix_shouldReturnNull() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()

    assertThat(questionnaireItem.localizedPrefixSpanned).isNull()
  }

  @Test
  fun localizedPrefixSpanned_shouldReturnPrefix() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply { prefix = "One in <strong>strong</strong>" }
    Locale.setDefault(Locale.US)

    assertThat(questionnaireItem.localizedPrefixSpanned.toString()).isEqualTo("One in strong")
  }

  @Test
  fun localizedPrefixSpanned_nonMatchingLocale_shouldReturnText() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        prefix = "One"
        prefixElement.apply {
          addExtension(
            Extension(ToolingExtensions.EXT_TRANSLATION).apply {
              addExtension(Extension("lang", StringType("vi-VN")))
              addExtension(Extension("content", StringType("Một")))
            }
          )
        }
      }
    Locale.setDefault(Locale.US)

    assertThat(questionnaireItem.localizedPrefixSpanned.toString()).isEqualTo("One")
  }

  @Test
  fun localizedPrefixSpanned_matchingLocale_shouldReturnLocalizedText() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        prefix = "One"
        prefixElement.apply {
          addExtension(
            Extension(ToolingExtensions.EXT_TRANSLATION).apply {
              addExtension(Extension("lang", StringType("vi-VN")))
              addExtension(Extension("content", StringType("Một")))
            }
          )
        }
      }
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionnaireItem.localizedPrefixSpanned.toString()).isEqualTo("Một")
  }

  @Test
  fun localizedPrefixSpanned_matchingLocaleWithoutCountryCode_shouldReturnLocalizedText() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        prefix = "One"
        prefixElement.apply {
          addExtension(
            Extension(ToolingExtensions.EXT_TRANSLATION).apply {
              addExtension(Extension("lang", StringType("vi")))
              addExtension(Extension("content", StringType("Một")))
            }
          )
        }
      }
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionnaireItem.localizedPrefixSpanned.toString()).isEqualTo("Một")
  }

  @Test
  fun localizedHintSpanned_noNestedDisplayItem_shouldReturnNull() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        }
      )

    assertThat(questionItemList.first().localizedHintSpanned).isNull()
  }

  @Test
  fun localizedHintSpanned_groupType_shouldReturnNull() {
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.GROUP
        item =
          listOf(
            Questionnaire.QuestionnaireItemComponent().apply {
              linkId = "nested-display-question"
              text = "text"
            }
          )
      }

    assertThat(questionnaireItemComponent.localizedHintSpanned).isNull()
  }

  @Test
  fun localizedHintSpanned_shouldReturnText() {
    val questionItemList =
      listOf(
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
              }
            )
        }
      )
    Locale.setDefault(Locale.US)

    assertThat(questionItemList.first().localizedHintSpanned.toString()).isEqualTo("subtitle text")
  }

  @Test
  fun localizedHintSpanned_nonMatchingLocale_shouldReturnText() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "subtitle text"
                textElement.apply {
                  addExtension(
                    Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                      addExtension(Extension("lang", StringType("vi-VN")))
                      addExtension(Extension("content", StringType("phụ đề")))
                    }
                  )
                }
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.US)

    assertThat(questionItemList.first().localizedHintSpanned.toString()).isEqualTo("subtitle text")
  }

  @Test
  fun localizedHintSpanned_matchingLocale_shouldReturnLocalizedText() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "subtitle text"
                textElement.apply {
                  addExtension(
                    Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                      addExtension(Extension("lang", StringType("vi-VN")))
                      addExtension(Extension("content", StringType("phụ đề")))
                    }
                  )
                }
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionItemList.first().localizedHintSpanned.toString()).isEqualTo("phụ đề")
  }

  @Test
  fun localizedHintSpanned_matchingLocaleWithoutCountryCode_shouldReturnLocalizedText() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "subtitle text"
                textElement.apply {
                  addExtension(
                    Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                      addExtension(Extension("lang", StringType("vi")))
                      addExtension(Extension("content", StringType("phụ đề")))
                    }
                  )
                }
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionItemList.first().localizedHintSpanned.toString()).isEqualTo("phụ đề")
  }

  @Test
  fun localizedFlyoverSpanned_noFlyover_shouldReturnNull() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        }
      )

    assertThat(questionItemList.first().localizedFlyoverSpanned).isNull()
  }

  @Test
  fun localizedFlyoverSpanned_shouldReturnFlyover() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "flyover text"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = "flyover"
                    }
                  }
                )
              }
            )
        }
      )

    assertThat(questionItemList.first().localizedFlyoverSpanned.toString())
      .isEqualTo("flyover text")
  }

  @Test
  fun localizedFlyoverSpanned_nonMatchingLocale_shouldReturnFlyover() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "flyover text"
                textElement.apply {
                  addExtension(
                    Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                      addExtension(Extension("lang", StringType("vi-VN")))
                      addExtension(Extension("content", StringType("gợi ý")))
                    }
                  )
                }
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = "flyover"
                    }
                  }
                )
              }
            )
        }
      )
    Locale.setDefault(Locale.US)

    assertThat(questionItemList.first().localizedFlyoverSpanned.toString())
      .isEqualTo("flyover text")
  }

  @Test
  fun enableWhenExpression_shouldReturnExpression() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.TEXT
            extension =
              listOf(
                Extension(
                  ITEM_ENABLE_WHEN_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression =
                      "%resource.repeat(item).where(linkId='4.2.1').answer.value.code ='female'"
                  }
                )
              )
          }
        )

    assertThat(questionItem.itemFirstRep.enableWhenExpression!!.expression)
      .isEqualTo("%resource.repeat(item).where(linkId='4.2.1').answer.value.code ='female'")
  }

  @Test
  fun enableWhenExpression_shouldReturnNull() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.TEXT
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "today()"
                  }
                )
              )
          }
        )

    assertThat(questionItem.itemFirstRep.enableWhenExpression).isNull()
  }

  @Test
  fun localizedFlyoverSpanned_matchingLocale_shouldReturnFlyover() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "flyover text"
                textElement.apply {
                  addExtension(
                    Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                      addExtension(Extension("lang", StringType("vi-VN")))
                      addExtension(Extension("content", StringType("gợi ý")))
                    }
                  )
                }
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = "flyover"
                    }
                  }
                )
              }
            )
        }
      )
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionItemList.first().localizedFlyoverSpanned.toString()).isEqualTo("gợi ý")
  }

  @Test
  fun localizedFlyoverSpanned_matchingLocaleWithoutCountryCode_shouldReturnFlyover() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
          item =
            listOf(
              Questionnaire.QuestionnaireItemComponent().apply {
                linkId = "nested-display-question"
                text = "flyover text"
                textElement.apply {
                  addExtension(
                    Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                      addExtension(Extension("lang", StringType("vi")))
                      addExtension(Extension("content", StringType("gợi ý")))
                    }
                  )
                }
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = "flyover"
                    }
                  }
                )
              }
            )
        }
      )
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionItemList.first().localizedFlyoverSpanned.toString()).isEqualTo("gợi ý")
  }

  @Test
  fun createQuestionResponseWithoutGroupAndNestedQuestions() {
    val question =
      Questionnaire.QuestionnaireItemComponent(
          StringType("gender"),
          Enumeration(
            Questionnaire.QuestionnaireItemTypeEnumFactory(),
            Questionnaire.QuestionnaireItemType.STRING
          )
        )
        .apply {
          initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(StringType("male")))
        }

    val questionResponse = question.createQuestionnaireResponseItem()

    assertThat((questionResponse.answer[0].value as StringType).value).isEqualTo("male")
  }

  @Test
  fun createQuestionResponseWithGroupQuestions() {
    val question =
      Questionnaire.QuestionnaireItemComponent(
          StringType("group-test"),
          Enumeration(
            Questionnaire.QuestionnaireItemTypeEnumFactory(),
            Questionnaire.QuestionnaireItemType.GROUP
          )
        )
        .apply {
          addItem(
            Questionnaire.QuestionnaireItemComponent(
                StringType("gender"),
                Enumeration(
                  Questionnaire.QuestionnaireItemTypeEnumFactory(),
                  Questionnaire.QuestionnaireItemType.STRING
                )
              )
              .apply {
                initial =
                  listOf(Questionnaire.QuestionnaireItemInitialComponent(StringType("male")))
              }
          )

          addItem(
            Questionnaire.QuestionnaireItemComponent(
                StringType("isActive"),
                Enumeration(
                  Questionnaire.QuestionnaireItemTypeEnumFactory(),
                  Questionnaire.QuestionnaireItemType.BOOLEAN
                )
              )
              .apply {
                initial = listOf(Questionnaire.QuestionnaireItemInitialComponent(BooleanType(true)))
              }
          )
        }

    val questionResponse = question.createQuestionnaireResponseItem()

    assertThat((questionResponse.item[0].answer[0].value as StringType).value).isEqualTo("male")
    assertThat((questionResponse.item[1].answer[0].value as BooleanType).booleanValue())
      .isEqualTo(true)
  }

  @Test
  fun createQuestionResponseWithNestedQuestions() {
    val question =
      Questionnaire.QuestionnaireItemComponent(
          StringType("group-test"),
          Enumeration(
            Questionnaire.QuestionnaireItemTypeEnumFactory(),
            Questionnaire.QuestionnaireItemType.GROUP
          )
        )
        .apply {
          addItem(
            Questionnaire.QuestionnaireItemComponent(
                StringType("gender"),
                Enumeration(
                  Questionnaire.QuestionnaireItemTypeEnumFactory(),
                  Questionnaire.QuestionnaireItemType.STRING
                )
              )
              .apply {
                initial =
                  listOf(Questionnaire.QuestionnaireItemInitialComponent(StringType("male")))

                addItem(
                  Questionnaire.QuestionnaireItemComponent(
                      StringType("isActive"),
                      Enumeration(
                        Questionnaire.QuestionnaireItemTypeEnumFactory(),
                        Questionnaire.QuestionnaireItemType.BOOLEAN
                      )
                    )
                    .apply {
                      initial =
                        listOf(Questionnaire.QuestionnaireItemInitialComponent(BooleanType(true)))
                    }
                )
              }
          )
        }

    val questionResponse = question.createQuestionnaireResponseItem()

    assertThat((questionResponse.item[0].answer[0].value as StringType).value).isEqualTo("male")
    assertThat(
        (questionResponse.item[0].answer[0].item[0].answer[0].value as BooleanType).booleanValue()
      )
      .isEqualTo(true)
  }

  @Test
  fun entryFormat_missingFormat_shouldReturnNull() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_ENTRY_FORMAT_URL, null)
      }
    assertThat(questionnaireItem.entryFormat).isNull()
  }

  @Test
  fun entryFormat_shouldReturnDateFormat() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_ENTRY_FORMAT_URL, StringType("yyyy-mm-dd"))
      }
    assertThat(questionnaireItem.entryFormat).isEqualTo("yyyy-mm-dd")
  }

  @Test
  fun entryFormat_formatExtensionMissing_shouldReturnNull() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    assertThat(questionnaireItem.entryFormat).isNull()
  }

  @Test
  fun `answerExpression should return expression`() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(
                  EXTENSION_ANSWER_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%resource.item.where(linkId='diseases').value"
                  }
                )
              )
          }
        )

    assertThat(questionItem.itemFirstRep.answerExpression!!.expression)
      .isEqualTo("%resource.item.where(linkId='diseases').value")
  }

  @Test
  fun `answerExpression should return null for missing extension`() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "today()"
                  }
                )
              )
          }
        )

    assertThat(questionItem.itemFirstRep.answerExpression).isNull()
  }

  @Test
  fun `choiceColumn should return choice columns list`() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(EXTENSION_CHOICE_COLUMN_URL).apply {
                  addExtension(Extension("path", StringType("code")))
                  addExtension(Extension("label", StringType("CODE")))
                  addExtension(Extension("forDisplay", BooleanType(false)))
                },
                Extension(EXTENSION_CHOICE_COLUMN_URL).apply {
                  addExtension(Extension("path", StringType("display")))
                  addExtension(Extension("label", StringType("DESCRIPTION")))
                  addExtension(Extension("forDisplay", BooleanType(true)))
                }
              )
          }
        )

    assertThat(questionItem.itemFirstRep.choiceColumn!!)
      .contains(ChoiceColumn("code", "CODE", false))
    assertThat(questionItem.itemFirstRep.choiceColumn!!)
      .contains(ChoiceColumn("display", "DESCRIPTION", true))
  }

  @Test
  fun `choiceColumn should return null for missing extension`() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.TEXT
            extension =
              listOf(
                Extension(
                  ITEM_INITIAL_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "today()"
                  }
                )
              )
          }
        )

    assertThat(questionItem.itemFirstRep.choiceColumn).isEmpty()
  }

  @Test
  fun `populateAnswerOptions should fill answer options for coding`() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(EXTENSION_CHOICE_COLUMN_URL).apply {
                  addExtension(Extension("path", StringType("code")))
                  addExtension(Extension("label", StringType("CODE")))
                  addExtension(Extension("forDisplay", BooleanType(false)))
                },
                Extension(EXTENSION_CHOICE_COLUMN_URL).apply {
                  addExtension(Extension("path", StringType("display")))
                  addExtension(Extension("label", StringType("DESCRIPTION")))
                  addExtension(Extension("forDisplay", BooleanType(true)))
                }
              )
          }
        )

    questionItem.itemFirstRep.populateAnswerOptions(
      listOf(Coding("a.com", "a", "A"), Coding("b.com", "b", "B"))
    )

    val answers =
      questionItem.itemFirstRep.answerOption.map {
        "${it.valueCoding.code}|${it.valueCoding.display}"
      }

    assertThat(answers.first()).isEqualTo("a|A")
    assertThat(answers.last()).contains("b|B")
  }

  @Test
  fun `populateAnswerOptions should fill answer options for resources`() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "full-name"
            type = Questionnaire.QuestionnaireItemType.REFERENCE
            extension =
              listOf(
                Extension(EXTENSION_CHOICE_COLUMN_URL).apply {
                  addExtension(Extension("path", StringType("name.given")))
                  addExtension(Extension("label", StringType("GIVEN")))
                  addExtension(Extension("forDisplay", BooleanType(true)))
                },
                Extension(EXTENSION_CHOICE_COLUMN_URL).apply {
                  addExtension(Extension("path", StringType("name.family")))
                  addExtension(Extension("label", StringType("FAMILY")))
                  addExtension(Extension("forDisplay", BooleanType(true)))
                }
              )
          }
        )

    questionItem.itemFirstRep.populateAnswerOptions(
      listOf(
        Patient().apply {
          id = "1234"
          nameFirstRep.family = "Doe"
          nameFirstRep.addGiven("John")
        },
        Patient().apply {
          id = "5678"
          nameFirstRep.family = "Doe"
          nameFirstRep.addGiven("Jane")
        }
      )
    )

    val answers = questionItem.itemFirstRep.answerOption.map { it.valueReference }

    assertThat(answers.first().display).isEqualTo("John Doe")
    assertThat(answers.first().reference).isEqualTo("Patient/1234")
    assertThat(answers.last().display).isEqualTo("Jane Doe")
    assertThat(answers.last().reference).isEqualTo("Patient/5678")
  }

  @Test
  fun `populateAnswerOptions should throw IllegalArgumentException when item type is not reference and data type is resource`() {
    val questionItem =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "full-name"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(EXTENSION_CHOICE_COLUMN_URL).apply {
                  addExtension(Extension("path", StringType("name.given")))
                  addExtension(Extension("label", StringType("GIVEN")))
                  addExtension(Extension("forDisplay", BooleanType(true)))
                }
              )
          }
        )

    assertThrows(IllegalArgumentException::class.java) {
      questionItem.itemFirstRep.populateAnswerOptions(listOf(Patient()))
    }
      .run {
        assertThat(this.message)
          .isEqualTo(
            "$EXTENSION_CHOICE_COLUMN_URL can not be applied on choice. Only type reference is allowed with resource."
          )
      }
  }
}
