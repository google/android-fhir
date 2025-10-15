/*
 * Copyright 2022-2025 Google LLC
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverAnnotatedString
import com.google.android.fhir.datacapture.extensions.toCoding
import com.google.android.fhir.datacapture.extensions.unitOption
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.EditTextFieldItem
import com.google.android.fhir.datacapture.views.compose.EditTextFieldState
import com.google.android.fhir.datacapture.views.compose.ExposedDropDownMenuBoxItem
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuantityViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        val text = remember(questionnaireViewItem) { uiInputText(questionnaireViewItem) }
        val isReadOnly =
          remember(questionnaireViewItem) { questionnaireViewItem.questionnaireItem.readOnly }
        val unitOptions =
          remember(questionnaireViewItem) { unitDropDownOptions(questionnaireViewItem) }
        val dropDownOptions =
          remember(unitOptions) { unitOptions.mapNotNull { it.toDropDownAnswerOption() } }
        val selectedOption =
          remember(questionnaireViewItem) {
            unitTextCoding(questionnaireViewItem)?.toDropDownAnswerOption()
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
              helperText = validationUiMessage.takeIf { !it.isNullOrBlank() }
                  ?: getRequiredOrOptionalText(questionnaireViewItem, context),
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
                horizontal = dimensionResource(R.dimen.item_margin_horizontal),
                vertical = dimensionResource(R.dimen.item_margin_vertical),
              ),
        ) {
          Header(questionnaireViewItem)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            EditTextFieldItem(
              modifier = Modifier.weight(1f),
              textFieldState = composeViewQuestionnaireState,
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.item_margin_horizontal)))
            ExposedDropDownMenuBoxItem(
              modifier = Modifier.weight(1f),
              enabled = !isReadOnly,
              selectedOption = selectedOption,
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
          is Invalid -> validationResult.getSingleStringValidationMessage()
        }

      private suspend fun handleInput(
        questionnaireViewItem: QuestionnaireViewItem,
        input: UiQuantity,
      ) {
        val currentAnswerQuantity = questionnaireViewItem.answers.singleOrNull()?.valueQuantity
        val draftAnswer = questionnaireViewItem.draftAnswer

        val decimal =
          input.value?.toBigDecimalOrNull()
            ?: (draftAnswer as? BigDecimal) ?: currentAnswerQuantity?.value
        val unit =
          input.unitDropDown ?: ((draftAnswer as? Coding) ?: currentAnswerQuantity?.toCoding())

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
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = Quantity(null, decimal.toDouble(), unit.system, unit.code, unit.display)
              },
            )
          }
        }
      }

      private fun uiInputText(questionnaireViewItem: QuestionnaireViewItem): String {
        return questionnaireViewItem.answers.singleOrNull()?.valueQuantity?.value?.toString()
          ?: questionnaireViewItem.draftAnswer?.let { if (it is BigDecimal) it.toString() else "" }
            ?: ""
      }

      private fun unitTextCoding(questionnaireViewItem: QuestionnaireViewItem) =
        questionnaireViewItem.answers.singleOrNull()?.valueQuantity?.toCoding()
          ?: questionnaireViewItem.draftAnswer?.let { it as? Coding }
            ?: questionnaireViewItem.questionnaireItem.initial
            ?.firstOrNull()
            ?.valueQuantity
            ?.toCoding()

      private fun unitDropDownOptions(questionnaireViewItem: QuestionnaireViewItem): List<Coding> =
        questionnaireViewItem.questionnaireItem.unitOption

      private fun Coding.toDropDownAnswerOption() =
        takeIf { it.hasCode() || it.hasDisplay() }
          ?.let {
            DropDownAnswerOption(answerId = it.code ?: it.display, answerOptionString = it.display)
          }

      private fun DropDownAnswerOption.findCoding(options: List<Coding>) =
        options.find { answerId == it.code } ?: options.find { answerId == it.display }
    }
}

private data class UiQuantity(val value: String?, val unitDropDown: Coding?)
