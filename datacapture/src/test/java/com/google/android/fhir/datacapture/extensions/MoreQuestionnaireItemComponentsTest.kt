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

import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.DataCapture
import com.google.android.fhir.datacapture.mapping.ITEM_INITIAL_EXPRESSION_URL
import com.google.android.fhir.datacapture.testing.DataCaptureTestApplication
import com.google.android.fhir.datacapture.testing.TestUrlResolver
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.utils.ToolingExtensions
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
  fun `displayItemControl should return help`() {
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
                    .setCode(DisplayItemControlType.HELP.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.displayItemControl).isEqualTo(DisplayItemControlType.HELP)
  }

  @Test
  fun `help button is visible if questionnaireItem has helpCode`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        item =
          listOf(
            Questionnaire.QuestionnaireItemComponent().apply {
              type = Questionnaire.QuestionnaireItemType.DISPLAY
              addExtension(
                Extension()
                  .setUrl(EXTENSION_ITEM_CONTROL_URL)
                  .setValue(
                    CodeableConcept()
                      .addCoding(
                        Coding()
                          .setCode(DisplayItemControlType.HELP.extensionCode)
                          .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                      )
                  )
              )
            }
          )
      }

    assertThat(questionnaireItem.hasHelpButton).isTrue()
  }

  @Test
  fun `help button is hidden if questionnaireItem does not have helpCode`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        item =
          listOf(
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
          )
      }

    assertThat(questionnaireItem.hasHelpButton).isFalse()
  }

  @Test
  fun `isHelpCode returns true if displayItemControl is help`() {
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
                    .setCode(DisplayItemControlType.HELP.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.isHelpCode).isTrue()
  }

  @Test
  fun `isHelpCode returns false if displayItemControl is not help`() {
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
                    .setCode(DisplayItemControlType.PAGE.extensionCode)
                    .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM)
                )
            )
        )
      }

    assertThat(questionnaireItem.isHelpCode).isFalse()
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
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          EXTENSION_CHOICE_ORIENTATION_URL,
          CodeType(ChoiceOrientationTypes.VERTICAL.extensionCode)
        )
      }
    assertThat(questionnaireItem.choiceOrientation).isEqualTo(ChoiceOrientationTypes.VERTICAL)
  }

  @Test
  fun choiceOrientation_shouldReturnHorizontal() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          EXTENSION_CHOICE_ORIENTATION_URL,
          CodeType(ChoiceOrientationTypes.HORIZONTAL.extensionCode)
        )
      }
    assertThat(questionnaireItem.choiceOrientation).isEqualTo(ChoiceOrientationTypes.HORIZONTAL)
  }

  @Test
  fun choiceOrientation_missingExtension_shouldReturnNull() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    assertThat(questionnaireItem.choiceOrientation).isNull()
  }

  @Test
  fun choiceOrientation_missingOrientation_shouldReturnNull() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_CHOICE_ORIENTATION_URL, CodeType(""))
      }
    assertThat(questionnaireItem.choiceOrientation).isNull()
  }

  @Test
  fun mimeTypes_shouldReturnMimeTypes() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MIME_TYPE, CodeType("image/jpg"))
        addExtension(EXTENSION_MIME_TYPE, CodeType("application/pdf"))
      }
    assertThat(questionnaireItem.mimeTypes).isEqualTo(listOf("image/jpg", "application/pdf"))
  }

  @Test
  fun mimeTypes_missingMimeType_shouldReturnEmpty() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MIME_TYPE, CodeType(""))
      }
    assertThat(questionnaireItem.mimeTypes).isEmpty()
  }

  @Test
  fun mimeTypes_missingExtension_shouldReturnNull() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    assertThat(questionnaireItem.mimeTypes).isEmpty()
  }

  @Test
  fun `should return max size in bytes`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaireItem.maxSizeInBytes).isEqualTo(BigDecimal(5242880))
  }

  @Test
  fun `should return max size in kibibytes`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaireItem.maxSizeInKiBs).isEqualTo(BigDecimal(5120))
  }

  @Test
  fun `should return max size in mebibytes`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaireItem.maxSizeInMiBs).isEqualTo(BigDecimal(5))
  }

  @Test
  fun `should return null for max size in bytes`() {
    assertThat(Questionnaire.QuestionnaireItemComponent().maxSizeInBytes).isNull()
  }

  @Test
  fun `should return null for max size in kibibytes`() {
    assertThat(Questionnaire.QuestionnaireItemComponent().maxSizeInKiBs).isNull()
  }

  @Test
  fun `should return null for max size in mebibytes`() {
    assertThat(Questionnaire.QuestionnaireItemComponent().maxSizeInMiBs).isNull()
  }

  @Test
  fun `should return false if given size is below maximum size allowed`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaireItem.isGivenSizeOverLimit(BigDecimal(10))).isFalse()
  }

  @Test
  fun `should return true if given size is above maximum size allowed`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaireItem.isGivenSizeOverLimit(BigDecimal(52428809))).isTrue()
  }

  @Test
  fun mimeType_size_shouldReturnNumberOfSupportedMimeTypes() {
    assertThat(MimeType.values().size).isEqualTo(4)
  }

  @Test
  fun hasMimeType_shouldReturnTrue() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MIME_TYPE, CodeType("image/jpg"))
        addExtension(EXTENSION_MIME_TYPE, CodeType("application/pdf"))
      }
    assertThat(questionnaire.hasMimeType(MimeType.IMAGE.value)).isTrue()
  }

  @Test
  fun hasMimeType_shouldReturnFalse() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MIME_TYPE, CodeType("image/jpg"))
        addExtension(EXTENSION_MIME_TYPE, CodeType("application/pdf"))
      }
    assertThat(questionnaire.hasMimeType(MimeType.VIDEO.value)).isFalse()
  }

  @Test
  fun hasMimeTypeOnly_shouldReturnTrue() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MIME_TYPE, CodeType("image/jpg"))
        addExtension(EXTENSION_MIME_TYPE, CodeType("image/png"))
      }
    assertThat(questionnaire.hasMimeTypeOnly(MimeType.IMAGE.value)).isTrue()
  }

  @Test
  fun hasMimeTypeOnly_shouldReturnFalse() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MIME_TYPE, CodeType("image/jpg"))
        addExtension(EXTENSION_MIME_TYPE, CodeType("application/pdf"))
      }
    assertThat(questionnaire.hasMimeTypeOnly(MimeType.IMAGE.value)).isFalse()
  }

  @Test
  fun maxSize_shouldReturnBytes() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaire.maxSizeInBytes).isEqualTo(BigDecimal(5242880))
  }

  @Test
  fun maxSize_shouldReturnKibibytes() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaire.maxSizeInKiBs).isEqualTo(BigDecimal(5120))
  }

  @Test
  fun maxSize_shouldReturnMebibytes() {
    val questionnaire =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_MAX_SIZE, DecimalType(5242880))
      }
    assertThat(questionnaire.maxSizeInMiBs).isEqualTo(BigDecimal(5))
  }

  @Test
  fun maxSizeInByte_missingExtension_shouldReturnNull() {
    val questionnaire = Questionnaire.QuestionnaireItemComponent()
    assertThat(questionnaire.maxSizeInBytes).isNull()
  }

  @Test
  fun maxSizeInKiB_missingExtension_shouldReturnNull() {
    val questionnaire = Questionnaire.QuestionnaireItemComponent()
    assertThat(questionnaire.maxSizeInKiBs).isNull()
  }

  @Test
  fun maxSizeInMiB_missingExtension_shouldReturnNull() {
    val questionnaire = Questionnaire.QuestionnaireItemComponent()
    assertThat(questionnaire.maxSizeInMiBs).isNull()
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
  fun `localizedHelpSpanned should return null if no help code`() {
    val questionItemList =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply {
          linkId = "parent-question"
          text = "parent question text"
          type = Questionnaire.QuestionnaireItemType.BOOLEAN
        }
      )

    assertThat(questionItemList.first().localizedHelpSpanned).isNull()
  }

  @Test
  fun `localizedHelpSpanned should return help`() {
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
                text = "help text"
                type = Questionnaire.QuestionnaireItemType.DISPLAY
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = DisplayItemControlType.HELP.extensionCode
                    }
                  }
                )
              }
            )
        }
      )

    assertThat(questionItemList.first().localizedHelpSpanned.toString()).isEqualTo("help text")
  }

  @Test
  fun `localizedHelpSpanned not matching locale should return help`() {
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
                text = "help text"
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
                      code = DisplayItemControlType.HELP.extensionCode
                    }
                  }
                )
              }
            )
        }
      )
    Locale.setDefault(Locale.US)

    assertThat(questionItemList.first().localizedHelpSpanned.toString()).isEqualTo("help text")
  }

  @Test
  fun `localizedHelpSpanned matching locale should return localized text`() {
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
                text = "help text"
                textElement.apply {
                  addExtension(
                    Extension(ToolingExtensions.EXT_TRANSLATION).apply {
                      addExtension(Extension("lang", StringType("vi-VN")))
                      addExtension(Extension("content", StringType("phụ đề")))
                    }
                  )
                }
                addExtension(
                  EXTENSION_ITEM_CONTROL_URL,
                  CodeableConcept().apply {
                    addCoding().apply {
                      system = EXTENSION_ITEM_CONTROL_SYSTEM
                      code = DisplayItemControlType.HELP.extensionCode
                    }
                  }
                )
                type = Questionnaire.QuestionnaireItemType.DISPLAY
              }
            )
        }
      )
    Locale.setDefault(Locale.forLanguageTag("vi-VN"))

    assertThat(questionItemList.first().localizedHelpSpanned.toString()).isEqualTo("phụ đề")
  }

  @Test
  fun `isHidden should return true`() {
    assertThat(
        Questionnaire.QuestionnaireItemComponent()
          .apply { addExtension(EXTENSION_HIDDEN_URL, BooleanType(true)) }
          .isHidden
      )
      .isTrue()
  }

  @Test
  fun `isHidden should return false`() {
    assertThat(
        Questionnaire.QuestionnaireItemComponent()
          .apply { addExtension(EXTENSION_HIDDEN_URL, BooleanType(false)) }
          .isHidden
      )
      .isFalse()
  }

  @Test
  fun `isHidden should return false for invalid value`() {
    assertThat(
        Questionnaire.QuestionnaireItemComponent()
          .apply { addExtension(EXTENSION_HIDDEN_URL, IntegerType(1)) }
          .isHidden
      )
      .isFalse()
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
  fun `calculatedExpression should return expression for valid extension url`() {
    val item =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          EXTENSION_CALCULATED_EXPRESSION_URL,
          Expression().apply {
            this.expression = "today()"
            this.language = "text/fhirpath"
          }
        )
      }
    assertThat(item.calculatedExpression).isNotNull()
    assertThat(item.calculatedExpression!!.expression).isEqualTo("today()")
  }

  @Test
  fun `calculatedExpression should return null for other extension url`() {
    val item =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(
          ITEM_INITIAL_EXPRESSION_URL,
          Expression().apply {
            this.expression = "today()"
            this.language = "text/fhirpath"
          }
        )
      }
    assertThat(item.calculatedExpression).isNull()
  }

  @Test
  fun `expressionBasedExtensions should return all extension of type expression`() {
    val item =
      Questionnaire.QuestionnaireItemComponent().apply {
        addExtension(EXTENSION_HIDDEN_URL, BooleanType(true))
        addExtension(
          EXTENSION_CALCULATED_EXPRESSION_URL,
          Expression().apply {
            this.expression = "today()"
            this.language = "text/fhirpath"
          }
        )
        addExtension(
          EXTENSION_ENABLE_WHEN_EXPRESSION_URL,
          Expression().apply {
            this.expression = "%resource.status == 'draft'"
            this.language = "text/fhirpath"
          }
        )
      }

    val result = item.expressionBasedExtensions

    assertThat(result.count()).isEqualTo(2)
    assertThat(result.first().url).isEqualTo(EXTENSION_CALCULATED_EXPRESSION_URL)
    assertThat(result.last().url).isEqualTo(EXTENSION_ENABLE_WHEN_EXPRESSION_URL)
  }

  @Test
  fun `isReferencedBy should return true`() {
    val item1 =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "A"
        addExtension(
          EXTENSION_CALCULATED_EXPRESSION_URL,
          Expression().apply {
            this.expression = "%resource.item.where(linkId='B')"
            this.language = "text/fhirpath"
          }
        )
      }
    val item2 = Questionnaire.QuestionnaireItemComponent().apply { linkId = "B" }
    assertThat(item2.isReferencedBy(item1)).isTrue()
  }

  @Test
  fun `isReferencedBy should return false`() {
    val item1 =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "A"
        addExtension(
          EXTENSION_CALCULATED_EXPRESSION_URL,
          Expression().apply {
            this.expression = "%resource.item.where(answer.value.empty())"
            this.language = "text/fhirpath"
          }
        )
      }
    val item2 = Questionnaire.QuestionnaireItemComponent().apply { linkId = "B" }
    assertThat(item2.isReferencedBy(item1)).isFalse()
  }

  @Test
  fun `flattened should return linear list`() {
    val items =
      listOf(
        Questionnaire.QuestionnaireItemComponent().apply { linkId = "A" },
        Questionnaire.QuestionnaireItemComponent()
          .apply { linkId = "B" }
          .addItem(
            Questionnaire.QuestionnaireItemComponent()
              .apply { linkId = "C" }
              .addItem(Questionnaire.QuestionnaireItemComponent().apply { linkId = "D" })
          )
      )
    assertThat(items.flattened().map { it.linkId }).containsExactly("A", "B", "C", "D")
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
  fun `createQuestionResponse should not set answer for quantity type with missing value`() {
    val question =
      Questionnaire.QuestionnaireItemComponent(
          StringType("age"),
          Enumeration(
            Questionnaire.QuestionnaireItemTypeEnumFactory(),
            Questionnaire.QuestionnaireItemType.QUANTITY
          )
        )
        .apply {
          initial =
            listOf(
              Questionnaire.QuestionnaireItemInitialComponent(
                Quantity().apply {
                  code = "months"
                  system = "http://unitofmeausre.org"
                }
              )
            )
        }

    val questionResponse = question.createQuestionnaireResponseItem()

    assertThat(questionResponse.answer).isEmpty()
  }

  @Test
  fun `createQuestionResponse should set answer with quantity type`() {
    val question =
      Questionnaire.QuestionnaireItemComponent(
          StringType("age"),
          Enumeration(
            Questionnaire.QuestionnaireItemTypeEnumFactory(),
            Questionnaire.QuestionnaireItemType.QUANTITY
          )
        )
        .apply {
          initial =
            listOf(
              Questionnaire.QuestionnaireItemInitialComponent(
                Quantity().apply {
                  code = "months"
                  system = "http://unitofmeausre.org"
                  value = BigDecimal("1")
                }
              )
            )
        }

    val questionResponse = question.createQuestionnaireResponseItem()
    val answer = questionResponse.answerFirstRep.value as Quantity
    assertThat(answer.value).isEqualTo(BigDecimal(1))
    assertThat(answer.code).isEqualTo("months")
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
  fun `fetchBitmapFromUrl() should return Bitmap from url`() {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        url = "https://some-image-server.com/images/f0006.png"
      }

    val urlResolver = TestUrlResolver(attachment.url, IMAGE_BASE64_DECODED)

    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .getDataCaptureConfig()
      .urlResolver = urlResolver

    val bitmap: Bitmap?
    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNotNull()
  }

  @Test
  fun `fetchBitmapFromUrl() should return null when Attachment has no content type`() {
    val attachment = Attachment().apply { url = "https://some-image-server.com/images/f0006.png" }

    val bitmap: Bitmap?
    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `fetchBitmapFromUrl() should return null when Attachment has no url`() {
    val attachment = Attachment().apply { contentType = "image/png" }

    val bitmap: Bitmap?
    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `fetchBitmapFromUrl() should return null when Attachment has no content type and no url`() {
    val attachment = Attachment()

    val bitmap: Bitmap?
    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `fetchBitmapFromUrl() should return null when Attachment has invalid url`() {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        url = "invalid url"
      }

    val bitmap: Bitmap?
    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `fetchBitmapFromUrl() should return null when Attachment has non image content type`() {
    val attachment =
      Attachment().apply {
        contentType = "document/pdf"
        data = DOCUMENT_BASE64_DECODED
      }

    val bitmap: Bitmap?
    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `fetchBitmapFromUrl() should return null when Attachment UrlResolver is not configured`() {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        url = "https://hapi.fhir.org/Binary/f006"
      }
    val bitmap: Bitmap?

    ApplicationProvider.getApplicationContext<DataCaptureTestApplication>()
      .getDataCaptureConfig()
      .urlResolver = null

    runBlocking {
      bitmap = attachment.fetchBitmapFromUrl(ApplicationProvider.getApplicationContext())
    }

    assertThat(bitmap).isNull()
  }

  @Test
  fun `decodeToBitmap() should return Attachment when Binary has data and content type for image`() {
    val attachment =
      Attachment().apply {
        contentType = "image/png"
        data = IMAGE_BASE64_DECODED
      }
    assertThat(attachment.decodeToBitmap()).isNotNull()
  }

  @Test
  fun `decodeToBitmap() should return null when Attachment has no content type`() {
    val attachment = Attachment().apply { data = IMAGE_BASE64_DECODED }
    assertThat(attachment.decodeToBitmap()).isNull()
  }

  @Test
  fun `decodeToBitmap() should return null when Attachment has no data`() {
    val attachment = Attachment().apply { contentType = "image/png" }
    assertThat(attachment.decodeToBitmap()).isNull()
  }

  @Test
  fun `decodeToBitmap() should return null when Attachment has no content type and no data`() {
    val attachment = Attachment()
    assertThat(attachment.decodeToBitmap()).isNull()
  }

  @Test
  fun `decodeToBitmap() should return null when Attachment contentType is for non image`() {
    val attachment =
      Attachment().apply {
        contentType = "document/pdf"
        data = DOCUMENT_BASE64_DECODED
      }
    assertThat(attachment.decodeToBitmap()).isNull()
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
  fun `candidateExpression should return expression`() {
    val questionnaire =
      Questionnaire()
        .addItem(
          Questionnaire.QuestionnaireItemComponent().apply {
            linkId = "first-name"
            type = Questionnaire.QuestionnaireItemType.CHOICE
            extension =
              listOf(
                Extension(
                  EXTENSION_CANDIDATE_EXPRESSION_URL,
                  Expression().apply {
                    language = "text/fhirpath"
                    expression = "%resource.item.where(linkId='diseases').value"
                  }
                )
              )
          }
        )

    assertThat(questionnaire.itemFirstRep.candidateExpression!!.expression)
      .isEqualTo("%resource.item.where(linkId='diseases').value")
  }

  @Test
  fun `candidateExpression should return null for missing extension`() {
    val questionnaire =
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

    assertThat(questionnaire.itemFirstRep.candidateExpression).isNull()
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
      .containsExactly(
        ChoiceColumn("code", "CODE", false),
        ChoiceColumn("display", "DESCRIPTION", true)
      )
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
  fun `extractAnswerOptions should return answer options for coding`() {
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

    val answers =
      questionItem.itemFirstRep
        .extractAnswerOptions(listOf(Coding("a.com", "a", "A"), Coding("b.com", "b", "B")))
        .map { "${it.valueCoding.code}|${it.valueCoding.display}" }

    assertThat(answers.first()).isEqualTo("a|A")
    assertThat(answers.last()).contains("b|B")
  }

  @Test
  fun `extractAnswerOptions should return answer options for resources`() {
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

    val answers =
      questionItem.itemFirstRep
        .extractAnswerOptions(
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
        .map { it.valueReference }

    assertThat(answers.size).isEqualTo(2)
    assertThat(answers.first().display).isEqualTo("John Doe")
    assertThat(answers.first().reference).isEqualTo("Patient/1234")
    assertThat(answers.last().display).isEqualTo("Jane Doe")
    assertThat(answers.last().reference).isEqualTo("Patient/5678")
  }

  @Test
  fun `extractAnswerOptions should throw IllegalArgumentException when item type is not reference and data type is resource`() {
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
        questionItem.itemFirstRep.extractAnswerOptions(listOf(Patient()))
      }
      .run {
        assertThat(this.message)
          .isEqualTo(
            "$EXTENSION_CHOICE_COLUMN_URL not applicable for 'choice'. Only type reference is allowed with resource."
          )
      }
  }

  @Test
  fun `sliderStepValue should return the integer value in the sliderStepValue extension`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply {
        linkId = "slider-step-value"
        addExtension(EXTENSION_SLIDER_STEP_VALUE_URL, IntegerType(10))
      }

    assertThat(questionnaireItem.sliderStepValue).isEqualTo(10)
  }

  @Test
  fun `sliderStepValue should return null if sliderStepValue extension is not present`() {
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().apply { linkId = "slider-step-value" }

    assertThat(questionnaireItem.sliderStepValue).isNull()
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

  companion object {
    private val IMAGE_BASE64_ENCODED =
      "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7".encodeToByteArray()
    private val IMAGE_BASE64_DECODED = Base64.decode(IMAGE_BASE64_ENCODED, Base64.DEFAULT)
    private val DOCUMENT_BASE64_DECODED = "document".encodeToByteArray()
  }
}
