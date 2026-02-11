/*
 * Copyright 2022-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.factories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.extensions.hasCode
import com.google.android.fhir.datacapture.extensions.hasDisplay
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.toBigDecimalOrNull
import com.google.android.fhir.datacapture.extensions.toCoding
import com.google.android.fhir.datacapture.extensions.unitOption
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.DropDownAnswerOption
import com.google.android.fhir.datacapture.views.components.DropDownItem
import com.google.android.fhir.datacapture.views.components.EditTextFieldItem
import com.google.android.fhir.datacapture.views.components.EditTextFieldState
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import com.google.android.fhir.datacapture.views.components.getRequiredOrOptionalText
import com.google.fhir.model.r4.Coding
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.Quantity
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object QuantityViewFactory : QuestionnaireItemViewFactory {
  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
    val text = remember(questionnaireViewItem) { uiInputText(questionnaireViewItem) }
    val isReadOnly =
      remember(questionnaireViewItem) {
        questionnaireViewItem.questionnaireItem.readOnly?.value ?: false
      }
    val requiredOptionalText = getRequiredOrOptionalText(questionnaireViewItem)
    val unitOptions =
      remember(questionnaireViewItem) { questionnaireViewItem.questionnaireItem.unitOption }
    val dropDownOptions =
      remember(unitOptions) { unitOptions.mapNotNull { it.toDropDownAnswerOption() } }
    val selectedOption =
      remember(questionnaireViewItem) {
        unitTextCoding(questionnaireViewItem)?.toDropDownAnswerOption()
          ?: dropDownOptions.singleOrNull() // Select if has only one option
      }

    var quantity by
      remember(questionnaireViewItem) {
        mutableStateOf(UiQuantity(text, selectedOption?.findCoding(unitOptions)))
      }

    val validationUiMessage = uiValidationMessage(questionnaireViewItem.validationResult)

    LaunchedEffect(quantity) {
      coroutineScope.launch { handleInput(questionnaireViewItem, quantity) }
    }

    val composeViewQuestionnaireState =
      remember(questionnaireViewItem) {
        EditTextFieldState(
          initialInputText = text,
          handleTextInputChange = { quantity = UiQuantity(it, quantity.unitDropDown) },
          coroutineScope = coroutineScope,
          hint = questionnaireViewItem.enabledDisplayItems.localizedFlyoverAnnotatedString,
          helperText = validationUiMessage.takeIf { !it.isNullOrBlank() } ?: requiredOptionalText,
          isError = !validationUiMessage.isNullOrBlank(),
          isReadOnly = isReadOnly,
          keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
          isMultiLine = false,
        )
      }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
          ),
    ) {
      Header(questionnaireViewItem)
      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        EditTextFieldItem(
          modifier = Modifier.weight(1f),
          textFieldState = composeViewQuestionnaireState,
        )
        Spacer(modifier = Modifier.width(QuestionnaireTheme.dimensions.itemMarginHorizontal))
        DropDownItem(
          modifier = Modifier.weight(1f),
          enabled = !isReadOnly,
          selectedOption = selectedOption,
          isError = !validationUiMessage.isNullOrBlank(),
          options = dropDownOptions,
        ) { answerOption ->
          quantity = UiQuantity(quantity.value, answerOption?.findCoding(unitOptions))
        }
      }
    }
  }

  private fun uiValidationMessage(validationResult: ValidationResult): String? =
    when (validationResult) {
      is NotValidated,
      Valid, -> null
      is Invalid -> validationResult.singleStringValidationMessage
    }

  private suspend fun handleInput(
    questionnaireViewItem: QuestionnaireViewItem,
    input: UiQuantity,
  ) {
    var decimal: BigDecimal? = null
    var unit: Coding? = null

    // Read decimal value and unit from complete answer
    questionnaireViewItem.answers.singleOrNull()?.let {
      val quantity = it.value?.asQuantity()?.value
      decimal = quantity?.value?.value
      unit = quantity?.toCoding()
    }

    // Read decimal value and unit from partial answer
    questionnaireViewItem.draftAnswer?.let {
      when (it) {
        is BigDecimal -> decimal = it
        is Coding -> unit = it
      }
    }

    // Update decimal value and unit
    input.value?.let { decimal = it.toBigDecimalOrNull() }
    input.unitDropDown?.let { unit = it }

    when {
      decimal == null && unit == null -> {
        questionnaireViewItem.clearAnswer()
      }
      decimal == null -> {
        questionnaireViewItem.setDraftAnswer(unit)
      }
      unit == null -> {
        questionnaireViewItem.setDraftAnswer(decimal)
      }
      else -> {
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.Item.Answer(
            value =
              QuestionnaireResponse.Item.Answer.Value.Quantity(
                Quantity(
                  value = Decimal(value = decimal),
                  unit = unit.display,
                  code = unit.code,
                  system = unit.system,
                ),
              ),
          ),
        )
      }
    }
  }

  private fun uiInputText(questionnaireViewItem: QuestionnaireViewItem): String =
    questionnaireViewItem.answers
      .singleOrNull()
      ?.value
      ?.asQuantity()
      ?.value
      ?.value
      ?.value
      ?.toStringExpanded()
      ?: questionnaireViewItem.draftAnswer?.let { if (it is BigDecimal) it.toString() else "" }
        ?: ""

  private fun unitTextCoding(questionnaireViewItem: QuestionnaireViewItem) =
    questionnaireViewItem.answers.singleOrNull()?.value?.asQuantity()?.value?.toCoding()
      ?: questionnaireViewItem.draftAnswer?.let { it as? Coding }
        ?: questionnaireViewItem.questionnaireItem.initial
        .firstOrNull()
        ?.value
        ?.asQuantity()
        ?.value
        ?.toCoding()

  private fun Coding.toDropDownAnswerOption() =
    takeIf { it.hasCode() || it.hasDisplay() }
      ?.let {
        DropDownAnswerOption(
          elementValue = it,
          displayString = it.display?.value ?: "",
        )
      }

  private fun DropDownAnswerOption.findCoding(options: List<Coding>) =
    options.find { elementValue == it }
}

private data class UiQuantity(val value: String?, val unitDropDown: Coding?)
