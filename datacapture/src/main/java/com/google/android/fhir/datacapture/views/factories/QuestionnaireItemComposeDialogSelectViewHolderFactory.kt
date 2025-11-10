/*
 * Copyright 2025 Google LLC
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

import android.content.Context
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ItemControlTypes
import com.google.android.fhir.datacapture.extensions.asStringValue
import com.google.android.fhir.datacapture.extensions.getRequiredOrOptionalText
import com.google.android.fhir.datacapture.extensions.itemControl
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.extensions.toAnnotatedString
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.Header
import com.google.android.fhir.datacapture.views.compose.MediaItem
import com.google.android.fhir.datacapture.views.compose.OptionSelectDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

internal object QuestionnaireItemComposeDialogSelectViewHolderFactory :
  QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        val context = LocalContext.current
        val activity =
          requireNotNull(context.tryUnwrapContext()) {
            "Can only use dialog select in an AppCompatActivity context"
          }
        val viewModel: QuestionnaireItemDialogSelectViewModel by activity.viewModels()

        val questionnaireItem = questionnaireViewItem.questionnaireItem
        val initialOptions = questionnaireViewItem.extractInitialOptions(context)

        var showDialog by remember { mutableStateOf(false) }

        // Collect selected options from ViewModel
        val selectedOptions by
          viewModel
            .getSelectedOptionsFlow(questionnaireItem.linkId)
            .collectAsStateWithLifecycle(initialValue = initialOptions)

        // Update answers when selected options change
        LaunchedEffect(selectedOptions) {
          questionnaireViewItem.clearAnswer()
          var answers = arrayOf<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>()
          selectedOptions.options
            .filter { it.selected }
            .map { option ->
              answers +=
                QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                  value = option.item.value
                }
            }
          selectedOptions.otherOptions.map { otherOption ->
            answers +=
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = StringType(otherOption)
              }
          }
          questionnaireViewItem.setAnswer(*answers)
        }

        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(
                horizontal = dimensionResource(R.dimen.item_margin_horizontal),
                vertical = dimensionResource(R.dimen.item_margin_vertical),
              ),
        ) {
          Header(questionnaireViewItem = questionnaireViewItem)
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

          // Summary field that opens the dialog
          OutlinedTextField(
            value = selectedOptions.selectedSummary,
            onValueChange = {},
            readOnly = true,
            modifier =
              Modifier.fillMaxWidth()
                .clickable(
                  enabled = !questionnaireItem.readOnly,
                  onClick = { showDialog = true },
                ),
            label = {
              questionnaireItem.localizedFlyoverSpanned?.let {
                Text(it.toString().toAnnotatedString())
              }
            },
            trailingIcon = {
              Icon(
                painterResource(R.drawable.arrow_drop_down_24px),
                contentDescription = null,
              )
            },
            supportingText = {
              val helperText = getRequiredOrOptionalText(questionnaireViewItem, context)
              if (helperText != null) {
                Text(helperText)
              }
            },
            isError =
              questionnaireViewItem.validationResult is Invalid &&
                (questionnaireViewItem.validationResult as Invalid)
                  .getSingleStringValidationMessage()
                  .isNotEmpty(),
            enabled = !questionnaireItem.readOnly,
          )

          // Error message
          if (questionnaireViewItem.validationResult is Invalid) {
            val errorMessage =
              (questionnaireViewItem.validationResult as Invalid).getSingleStringValidationMessage()
            if (errorMessage.isNotEmpty()) {
              Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
              )
            }
          }
        }

        val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
        // Show dialog when triggered
        if (showDialog) {
          OptionSelectDialog(
            title = questionnaireViewItem.questionText
                ?: questionnaireItem.localizedFlyoverSpanned ?: "",
            multiSelect = questionnaireItem.repeats,
            otherOptionsAllowed = questionnaireItem.itemControl == ItemControlTypes.OPEN_CHOICE,
            initialSelectedOptions = selectedOptions,
            onDismiss = { showDialog = false },
            onConfirm = { newOptions ->
              coroutineScope.launch {
                viewModel.updateSelectedOptions(questionnaireItem.linkId, newOptions)
              }
            },
          )
        }
      }
    }
}

private fun QuestionnaireViewItem.extractInitialOptions(context: Context): SelectedOptions {
  val options =
    enabledAnswerOptions.map { answerOption ->
      OptionSelectOption(
        item = answerOption,
        selected = isAnswerOptionSelected(answerOption),
        context = context,
      )
    }
  return SelectedOptions(
    options = options,
    otherOptions =
      answers
        // All of the Other options will be encoded as String value types
        .mapNotNull { if (it.hasValueStringType()) it.valueStringType.value else null }
        // We should also make sure that these values aren't present in the predefined options
        .filter { value -> value !in options.map { it.item.value.asStringValue() } },
  )
}
