/*
 * Copyright 2022-2023 Google LLC
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
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_SYSTEM_ANDROID_FHIR
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL
import com.google.android.fhir.datacapture.extensions.EXTENSION_ITEM_CONTROL_URL_ANDROID_FHIR
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class QuestionnaireEditAdapterTest {
  @Test
  fun getItemViewType_groupItemType_shouldReturnGroupViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.GROUP),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.GROUP.value)
  }

  @Test
  fun getItemViewType_booleanItemType_shouldReturnBooleanViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.BOOLEAN),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.BOOLEAN_TYPE_PICKER.value)
  }

  @Test
  fun getItemViewType_dateItemType_shouldReturnDatePickerViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.DATE),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.DATE_PICKER.value)
  }

  @Test
  fun getItemViewType_dateItemType_answerOption_shouldReturnDropDownViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.DATE
        answerOption =
          listOf(Questionnaire.QuestionnaireItemAnswerOptionComponent(DateType("2022-06-22")))
      }
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItemComponent,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.RADIO_GROUP.value)
  }

  @Test
  fun getItemViewType_dateTimeItemType_shouldReturnDateTimePickerViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.DATETIME),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.DATE_TIME_PICKER.value)
  }

  @Test
  fun getItemViewType_stringItemType_shouldReturnEditTextViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.STRING),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.EDIT_TEXT_SINGLE_LINE.value)
  }

  @Suppress("ktlint:standard:max-line-length")
  @Test
  fun getItemViewType_stringItemType_androidItemControlExtension_shouldReturnPhoneNumberViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().setType(Questionnaire.QuestionnaireItemType.STRING)
    questionnaireItem.addExtension(
      Extension()
        .setUrl(EXTENSION_ITEM_CONTROL_URL_ANDROID_FHIR)
        .setValue(
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode(ItemControlTypes.PHONE_NUMBER.extensionCode)
                .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM_ANDROID_FHIR),
            ),
        ),
    )
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItem,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.PHONE_NUMBER.value)
  }

  @Test
  fun getItemViewType_stringItemType_answerOption_shouldReturnDropDownViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.STRING
        answerOption =
          listOf(Questionnaire.QuestionnaireItemAnswerOptionComponent(StringType("option-1")))
      }
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItemComponent,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.RADIO_GROUP.value)
  }

  @Test
  fun getItemViewType_textItemType_shouldReturnEditTextViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.TEXT),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.EDIT_TEXT_MULTI_LINE.value)
  }

  @Test
  fun getItemViewType_integerItemType_shouldReturnEditTextIntegerViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.INTEGER),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.EDIT_TEXT_INTEGER.value)
  }

  @Test
  fun getItemViewType_integerItemType_itemControlExtensionWithSlider_shouldReturnSliderViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent()
        .setType(Questionnaire.QuestionnaireItemType.INTEGER)
    questionnaireItem.addExtension(
      Extension()
        .setUrl(EXTENSION_ITEM_CONTROL_URL)
        .setValue(
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode(ItemControlTypes.SLIDER.extensionCode)
                .setDisplay("Slider")
                .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM),
            ),
        ),
    )
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItem,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.SLIDER.value)
  }

  @Test
  fun getItemViewType_integerItemType_answerOption_shouldReturnDropDownViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val questionnaireItemComponent =
      Questionnaire.QuestionnaireItemComponent().apply {
        type = Questionnaire.QuestionnaireItemType.INTEGER
        answerOption =
          listOf(Questionnaire.QuestionnaireItemAnswerOptionComponent(IntegerType("1")))
      }
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItemComponent,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.RADIO_GROUP.value)
  }

  @Test
  fun getItemViewType_decimalItemType_shouldReturnEditTextDecimalViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.DECIMAL),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.EDIT_TEXT_DECIMAL.value)
  }

  @Test
  fun getItemViewType_choiceItemType_lessAnswerOptions_shouldReturnRadioGroupViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.CHOICE),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.RADIO_GROUP.value)
  }

  @Test
  fun getItemViewType_choiceItemType_moreAnswerOptions_shouldReturnDropDownViewHolderType() {
    val answerOptions =
      List(QuestionnaireEditAdapter.MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN) {
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(Coding().setCode("test-code").setDisplay("Test Code"))
      }
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            Questionnaire.QuestionnaireItemComponent()
              .setType(Questionnaire.QuestionnaireItemType.CHOICE)
              .setAnswerOption(answerOptions),
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.DROP_DOWN.value)
  }

  @Suppress("ktlint:standard:max-line-length")
  @Test
  fun getItemViewType_choiceItemType_itemControlExtensionWithRadioButton_shouldReturnRadioGroupViewHolder() {
    val answerOptions =
      List(QuestionnaireEditAdapter.MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN) {
        Questionnaire.QuestionnaireItemAnswerOptionComponent()
          .setValue(Coding().setCode("test-code").setDisplay("Test Code"))
      }
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent()
        .setType(Questionnaire.QuestionnaireItemType.CHOICE)
        .setAnswerOption(answerOptions)
    questionnaireItem.addExtension(
      Extension()
        .setUrl(EXTENSION_ITEM_CONTROL_URL)
        .setValue(
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode(ItemControlTypes.RADIO_BUTTON.extensionCode)
                .setDisplay("Radio Button")
                .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM),
            ),
        ),
    )
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItem,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.RADIO_GROUP.value)
  }

  @Suppress("ktlint:standard:max-line-length")
  @Test
  fun getItemViewType_choiceItemType_itemControlExtensionWithDropDown_shouldReturnDropDownViewHolderType() {
    val questionnaireEditAdapter = QuestionnaireEditAdapter()
    val questionnaireItem =
      Questionnaire.QuestionnaireItemComponent().setType(Questionnaire.QuestionnaireItemType.CHOICE)
    questionnaireItem.addExtension(
      Extension()
        .setUrl(EXTENSION_ITEM_CONTROL_URL)
        .setValue(
          CodeableConcept()
            .addCoding(
              Coding()
                .setCode(ItemControlTypes.DROP_DOWN.extensionCode)
                .setDisplay("Drop Down")
                .setSystem(EXTENSION_ITEM_CONTROL_SYSTEM),
            ),
        ),
    )
    questionnaireEditAdapter.submitList(
      listOf(
        QuestionnaireAdapterItem.Question(
          QuestionnaireViewItem(
            questionnaireItem,
            QuestionnaireResponse.QuestionnaireResponseItemComponent(),
            validationResult = NotValidated,
            answersChangedCallback = { _, _, _, _ -> },
          ),
        ),
      ),
    )

    assertThat(questionnaireEditAdapter.getItemViewType(0))
      .isEqualTo(QuestionnaireViewHolderType.DROP_DOWN.value)
  }

  // TODO: test errors thrown for unsupported types

  @Test
  fun `areItemsTheSame() should return false if the questionnaire items are different`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val otherQuestionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areItemsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              otherQuestionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isFalse()
  }

  fun `areItemsTheSame() should return false if the questionnaire response items are different`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val otherQuestionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areItemsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              otherQuestionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isFalse()
  }

  fun `areItemsTheSame() should return true if the questionnaire item and the questionnaire response item are the same`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areItemsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isTrue()
  }

  @Test
  fun `areContentsTheSame() should return false if the questionnaire items are different`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val otherQuestionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areContentsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              otherQuestionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isFalse()
  }

  fun `areContentsTheSame() should return false if the questionnaire response items are different`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()
    val otherQuestionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areContentsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              otherQuestionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isFalse()
  }

  fun `areContentsTheSame() should return false if the answers are different`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areContentsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
                questionnaireItem,
                questionnaireResponseItem,
                validationResult = NotValidated,
                answersChangedCallback = { _, _, _, _ -> },
              )
              .apply {
                addAnswer(
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = StringType("answer")
                  },
                )
              },
          ),
        ),
      )
      .isFalse()
  }

  fun `areContentsTheSame() should return false if the validation results are different`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areContentsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = Invalid(listOf()),
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isFalse()
  }

  fun `areContentsTheSame() should treat not validated and invalid validation results as the same`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areContentsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isTrue()
  }

  fun `areContentsTheSame() should return true if the questionnaire, the questionnaire response, the answers, and the validation results are all the same`() {
    val questionnaireItem = Questionnaire.QuestionnaireItemComponent()
    val questionnaireResponseItem = QuestionnaireResponse.QuestionnaireResponseItemComponent()

    assertThat(
        DiffCallbacks.ITEMS.areContentsTheSame(
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
          QuestionnaireAdapterItem.Question(
            QuestionnaireViewItem(
              questionnaireItem,
              questionnaireResponseItem,
              validationResult = NotValidated,
              answersChangedCallback = { _, _, _, _ -> },
            ),
          ),
        ),
      )
      .isTrue()
  }

  @Test
  fun onCreateViewHolder_customViewType_shouldReturnCorrectCustomViewHolder() {
    val viewFactoryMatchers = getQuestionnaireItemViewHolderFactoryMatchers()
    val questionnaireEditAdapter = QuestionnaireEditAdapter(viewFactoryMatchers)
    assertThat(
        questionnaireEditAdapter.onCreateViewHolder(
          mock(),
          QuestionnaireViewHolderType.values().size,
        ),
      )
      .isEqualTo(viewFactoryMatchers[0].factory.create(mock()))
  }

  @Test
  fun onCreateViewHolder_customViewType_shouldThrowExceptionForInvalidWidgetType() {
    val viewFactoryMatchers = getQuestionnaireItemViewHolderFactoryMatchers()
    val questionnaireEditAdapter = QuestionnaireEditAdapter(viewFactoryMatchers)
    assertThrows(IllegalStateException::class.java) {
      QuestionnaireEditAdapter(getQuestionnaireItemViewHolderFactoryMatchers())
      questionnaireEditAdapter.onCreateViewHolder(
        mock(),
        QuestionnaireViewHolderType.values().size + viewFactoryMatchers.size,
      )
    }
  }

  @Test
  fun getItemViewTypeMapping_customViewType_shouldReturnCorrectIntValue() {
    val expectedItemViewType = QuestionnaireViewHolderType.values().size
    val questionnaireViewItem =
      QuestionnaireViewItem(
        Questionnaire.QuestionnaireItemComponent().apply {
          type = Questionnaire.QuestionnaireItemType.DATE
        },
        QuestionnaireResponse.QuestionnaireResponseItemComponent(),
        validationResult = NotValidated,
        answersChangedCallback = { _, _, _, _ -> },
      )

    assertThat(expectedItemViewType)
      .isEqualTo(
        QuestionnaireEditAdapter(getQuestionnaireItemViewHolderFactoryMatchers())
          .getItemViewTypeForQuestion(questionnaireViewItem),
      )
  }

  private fun getQuestionnaireItemViewHolderFactoryMatchers():
    List<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher> {
    return listOf(
      QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher(
        mock<QuestionnaireItemViewHolderFactory>().apply {
          whenever(create(any())).thenReturn(mock())
        },
      ) { questionnaireItem ->
        questionnaireItem.type == Questionnaire.QuestionnaireItemType.DATE
      },
    )
  }
}
