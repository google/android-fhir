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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.mapping.ITEM_INITIAL_EXPRESSION_URL
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.common.truth.Truth.assertThat
import java.nio.charset.Charset
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], application = DataCaptureTestApplication::class)
class MoreQuestionnaireItemComponentsTest {

  @Before
  fun setUp() {
    ReflectionHelpers.setStaticField(DataCapture::class.java, "configuration", null)
  }

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
  fun `isFlyoverCode should return true`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.DISPLAY
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

    assertThat(questionnaireItem.isFlyoverCode).isTrue()
  }

  @Test
  fun `isFlyoverCode item returns false if type is not display`() {
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

    assertThat(questionnaireItem.isFlyoverCode).isFalse()
  }

  @Test
  fun `isFlyoverCode item returns false if code is not flyover`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.STRING
        addExtension(
          Extension()
            .setUrl(EXTENSION_ITEM_CONTROL_URL)
            .setValue(
              CodeableConcept()
                .addCoding(Coding().setCode("random-code").setSystem(EXTENSION_ITEM_CONTROL_SYSTEM))
            )
        )
      }

    assertThat(questionnaireItem.isFlyoverCode).isFalse()
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
  fun `nested display item without instructions code returns null`() {
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
                text = "text"
              }
            )
        }
      )

    assertThat(questionItemList.first().localizedInstructionsSpanned).isNull()
  }

  @Test
  fun `localizedInstructionsSpanned returns text`() {
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
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.US)

    assertThat(questionItemList.first().localizedInstructionsSpanned.toString())
      .isEqualTo("subtitle text")
  }

  @Test
  fun `localizedInstructionsSpanned returns text for non matching locale`() {
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
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.US)

    assertThat(questionItemList.first().localizedInstructionsSpanned.toString())
      .isEqualTo("subtitle text")
  }

  @Test
  fun `localizedInstructionsSpanned returns localized text for matching locale`() {
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
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionItemList.first().localizedInstructionsSpanned.toString()).isEqualTo("phụ đề")
  }

  @Test
  fun `localizedInstructionsSpanned returns localized text for matching locale without country code`() {
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
                extension = listOf(displayCategoryExtensionWithInstructionsCode)
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionItemList.first().localizedInstructionsSpanned.toString()).isEqualTo("phụ đề")
  }

  @Test
  fun `isInstructionsCode returns true`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "nested-display-question"
        text = "subtitle text"
        extension = listOf(displayCategoryExtensionWithInstructionsCode)
        type = Questionnaire.QuestionnaireItemType.DISPLAY
      }
    assertThat(questionnaireItem.isInstructionsCode).isTrue()
  }

  @Test
  fun `isInstructionsCode returns false if item type is not display`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "nested-display-question"
        text = "subtitle text"
        extension = listOf(displayCategoryExtensionWithInstructionsCode)
        type = Questionnaire.QuestionnaireItemType.STRING
      }
    assertThat(questionnaireItem.isInstructionsCode).isFalse()
  }

  @Test
  fun `isInstructionsCode returns false if instructions code is not present`() {
    val displayCategoryExtension =
      Extension().apply {
        url = EXTENSION_DISPLAY_CATEGORY_URL
        setValue(
          CodeableConcept().apply {
            coding =
              listOf(
                Coding().apply {
                  code = "some random code"
                  system = EXTENSION_DISPLAY_CATEGORY_SYSTEM
                }
              )
          }
        )
      }
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "nested-display-question"
        text = "subtitle text"
        extension = listOf(displayCategoryExtension)
        type = Questionnaire.QuestionnaireItemType.STRING
      }
    assertThat(questionnaireItem.isInstructionsCode).isFalse()
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
                  EXTENSION_ENABLE_WHEN_EXPRESSION_URL,
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
  fun `Attachment#fetchBitmap() should return null when Attachment has data and incorrect contentType`() {
    val attachment =
      Attachment().apply {
        data = "some-byte".toByteArray(Charset.forName("UTF-8"))
        contentType = "document/pdf"
      }
    val bitmap: Bitmap?
    runBlocking { bitmap = attachment.fetchBitmap(ApplicationProvider.getApplicationContext()) }
    assertThat(bitmap).isNull()
  }

  @Test
  fun `Attachment#fetchBitmap() should return null when Attachment has no data and no url`() {
    val attachment = Attachment()
    val bitmap: Bitmap?

    runBlocking { bitmap = attachment.fetchBitmap(ApplicationProvider.getApplicationContext()) }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `Attachment#fetchBitmap() should return Bitmap when Attachment has data and correct contentType`() {
    val attachment =
      Attachment().apply {
        data =
          "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".toByteArray(
            Charset.forName("UTF-8")
          )
        contentType = "image/png"
      }
    val bitmap: Bitmap?
    runBlocking { bitmap = attachment.fetchBitmap(ApplicationProvider.getApplicationContext()) }
    assertThat(bitmap).isNotNull()
  }

  @Test
  fun `Attachment#isImage() should return true when Attachment contentType is image`() {
    val attachment = Attachment().apply { contentType = "image/png" }
    assertThat(attachment.isImage).isTrue()
  }

  @Test
  fun `Attachment#isImage() should return false when Attachment contentType is not image`() {
    val attachment = Attachment().apply { contentType = "document/pdf" }
    assertThat(attachment.isImage).isFalse()
  }

  @Test
  fun `Attachment#isImage() should return false when Attachment does not have contentType`() {
    val attachment = Attachment()
    assertThat(attachment.isImage).isFalse()
  }

  @Test
  fun `Binary#getBitmap() should return null when contentType is not for an image`() {
    val binary = Binary().apply { contentType = "document/pdf" }
    assertThat(binary.getBitmap()).isNull()
  }

  @Test
  fun `Binary#getBitmap() should return null when contentType is null`() {
    val binary = Binary()
    assertThat(binary.getBitmap()).isNull()
  }

  @Test
  fun `Binary#isImage() should return false when contentType is not for an image`() {
    val binary = Binary().apply { contentType = "document/pdf" }
    assertThat(binary.isImage()).isFalse()
  }

  @Test
  fun `Binary#isImage() should return false when contentType is null`() {
    val binary = Binary()
    assertThat(binary.isImage()).isFalse()
  }

  @Test
  fun `Attachment#fetchBitmap() should return Bitmap and call AttachmentResolver#resolveBinaryResource`() {
    val attachment = Attachment().apply { url = "https://hapi.fhir.org/Binary/f006" }
    val binary =
      Binary().apply {
        contentType = "image/png"
        data =
          "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".toByteArray(
            Charset.forName("UTF-8")
          )
      }
    val bitmap: Bitmap?
    val attachmentResolver: AttachmentResolver = mock()
    runBlocking {
      Mockito.`when`(attachmentResolver.resolveBinaryResource("https://hapi.fhir.org/Binary/f006"))
        .doReturn(binary)
    }

    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .getDataCaptureConfig()
      .attachmentResolver = attachmentResolver

    runBlocking { bitmap = attachment.fetchBitmap(ApplicationProvider.getApplicationContext()) }

    assertThat(bitmap).isNotNull()
    runBlocking {
      Mockito.verify(attachmentResolver).resolveBinaryResource("https://hapi.fhir.org/Binary/f006")
    }
  }

  @Test
  fun `Attachment#fetchBitmap() should return null when Attachment has Binary resource url but AttachmentResolver not configured`() {
    val attachment = Attachment().apply { url = "https://hapi.fhir.org/Binary/f006" }
    val bitmap: Bitmap?

    runBlocking { bitmap = attachment.fetchBitmap(ApplicationProvider.getApplicationContext()) }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `Attachment#fetchBitmap() should return Bitmap and call AttachmentResolver#resolveImageUrl`() {
    val attachment = Attachment().apply { url = "https://some-image-server.com/images/f0006.png" }
    val byteArray =
      Base64.decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7", Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    val attachmentResolver: AttachmentResolver = mock()
    runBlocking {
      Mockito.`when`(
          attachmentResolver.resolveImageUrl("https://some-image-server.com/images/f0006.png")
        )
        .doReturn(bitmap)
    }
    var context = ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
    context.getDataCaptureConfig().attachmentResolver = attachmentResolver

    val resolvedBitmap: Bitmap?
    runBlocking { resolvedBitmap = attachment.fetchBitmap(context) }

    assertThat(resolvedBitmap).isEqualTo(bitmap)
    runBlocking {
      Mockito.verify(attachmentResolver)
        .resolveImageUrl("https://some-image-server.com/images/f0006.png")
    }
  }

  @Test
  fun `Attachment#fetchBitmap() should return null when Attachment has external url to image but AttachmentResolver is not configured`() {
    val attachment = Attachment().apply { url = "https://some-image-server.com/images/f0006.png" }

    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .getDataCaptureConfig()
      .attachmentResolver = null
    val resolvedBitmap: Bitmap?
    runBlocking {
      resolvedBitmap = attachment.fetchBitmap(ApplicationProvider.getApplicationContext())
    }

    assertThat(resolvedBitmap).isNull()
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

  private val displayCategoryExtensionWithInstructionsCode =
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
}
