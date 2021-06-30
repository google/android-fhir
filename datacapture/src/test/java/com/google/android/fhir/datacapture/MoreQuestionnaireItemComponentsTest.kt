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
import com.google.common.truth.Truth.assertThat
import java.util.Locale
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
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
      Questionnaire.QuestionnaireItemComponent().setType(Questionnaire.QuestionnaireItemType.CHOICE)
    questionnaireItem.addExtension(
      Extension()
        .setUrl(EXTENSION_ITEM_CONTROL_URL)
        .setValue(
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode(ITEM_CONTROL_DROP_DOWN)
                .setDisplay("Drop Down")
                .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
            )
        )
    )

    assertThat(questionnaireItem.itemControl).isEqualTo(ITEM_CONTROL_DROP_DOWN)
  }

  @Test
  fun itemControl_shouldReturnItemControlCodeRadioButton() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().setType(Questionnaire.QuestionnaireItemType.CHOICE)
    questionnaireItem.addExtension(
      Extension()
        .setUrl(EXTENSION_ITEM_CONTROL_URL)
        .setValue(
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode(ITEM_CONTROL_RADIO_BUTTON)
                .setDisplay("Radio Group")
                .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
            )
        )
    )

    assertThat(questionnaireItem.itemControl).isEqualTo(ITEM_CONTROL_RADIO_BUTTON)
  }

  @Test
  fun itemControl_wrongExtensionUrl_shouldReturnNull() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().setType(Questionnaire.QuestionnaireItemType.CHOICE)
    questionnaireItem.addExtension(
      Extension()
        .setUrl("null-test")
        .setValue(
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode(ITEM_CONTROL_DROP_DOWN)
                .setDisplay("Drop Down")
                .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
            )
        )
    )

    assertThat(questionnaireItem.itemControl).isNull()
  }

  @Test
  fun itemControl_wrongExtensionCoding_shouldReturnNull() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().setType(Questionnaire.QuestionnaireItemType.CHOICE)
    questionnaireItem.addExtension(
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

    assertThat(questionnaireItem.itemControl).isNull()
  }

  @Test
  fun localizedText_default() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply { text = "Patient Information" }
    Locale.setDefault(Locale.US)

    assertThat(questionnaireItem.localizedText).isEqualTo("Patient Information")
  }

  @Test
  fun localizedPrefix_default() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent().apply { prefix = "One" }
    Locale.setDefault(Locale.US)

    assertThat(questionnaireItem.localizedPrefix).isEqualTo("One")
  }

  @Test
  fun localizedText_vietnameseTranslation_usLocale_shouldReturnDefault() {
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

    assertThat(questionnaireItem.localizedText).isEqualTo("Patient Information")
  }

  @Test
  fun localizedPrefix_vietnameseTranslation_usLocale_shouldReturnDefault() {
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

    assertThat(questionnaireItem.localizedPrefix).isEqualTo("One")
  }

  @Test
  fun localizedText_vietnameseTranslation_vietnameseLocale_shouldReturnVietnamese() {
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

    assertThat(questionnaireItem.localizedText).isEqualTo("Thông tin bệnh nhân")
  }

  @Test
  fun localizedPrefix_vietnameseTranslation_vietnameseLocale_shouldReturnVietnamese() {
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

    assertThat(questionnaireItem.localizedPrefix).isEqualTo("Một")
  }

  @Test
  fun localizedText_vietnameseTranslationWithoutCountryCode_vietnameseLocale_shouldReturnVietnamese() {
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

    assertThat(questionnaireItem.localizedText).isEqualTo("Thông tin bệnh nhân")
  }

  @Test
  fun localizedPrefix_vietnameseTranslationWithoutCountryCode_vietnameseLocale_shouldReturnVietnamese() {
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

    assertThat(questionnaireItem.localizedPrefix).isEqualTo("Một")
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
}
