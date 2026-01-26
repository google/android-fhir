/*
 * Copyright 2024-2026 Google LLC
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.extensions.itemControl
import com.google.android.fhir.datacapture.extensions.shouldUseDialog
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.DisplayViewFactory
import com.google.android.fhir.datacapture.views.factories.EditTextDecimalViewFactory
import com.google.android.fhir.datacapture.views.factories.EditTextIntegerViewFactory
import com.google.android.fhir.datacapture.views.factories.EditTextMultiLineViewFactory
import com.google.android.fhir.datacapture.views.factories.EditTextPhoneNumberViewFactory
import com.google.android.fhir.datacapture.views.factories.EditTextSingleLineViewFactory
import com.google.android.fhir.datacapture.views.factories.QuantityViewFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewFactory
import com.google.android.fhir.datacapture.views.factories.SliderViewFactory
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlin.uuid.ExperimentalUuidApi

// Choice questions are rendered as dialogs if they have at least this many options
const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DIALOG = 10

// Choice questions are rendered as radio group if number of options less than this constant
const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN = 4

// Test tag for QuestionnaireEditList
const val QUESTIONNAIRE_EDIT_LIST = "questionnaire_edit_list"

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun QuestionnaireEditList(
  items: List<QuestionnaireAdapterItem>,
  displayMode: DisplayMode,
  questionnaireItemViewHolderMatchers: List<QuestionnaireItemViewHolderFactoryMatcher>,
  onUpdateProgressIndicator: (Int, Int) -> Unit,
) {
  val listState = rememberLazyListState()
  LaunchedEffect(listState) {
    if (displayMode is DisplayMode.EditMode && !displayMode.pagination.isPaginated) {
      snapshotFlow {
          val layoutInfo = listState.layoutInfo
          val visibleItems = layoutInfo.visibleItemsInfo
          val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
          val total = layoutInfo.totalItemsCount

          // If all items are visible, we're at 100%
          if (visibleItems.size >= total && total > 0) {
            total to total
          } else {
            lastVisible + 1 to total
          }
        }
        .collect { (visibleCount, total) -> onUpdateProgressIndicator(visibleCount, total) }
    }
  }
  LazyColumn(state = listState, modifier = Modifier.testTag(QUESTIONNAIRE_EDIT_LIST)) {
    items(
      items = items,
      key = { item ->
        when (item) {
          is QuestionnaireAdapterItem.Question -> item.id
              ?: throw IllegalStateException("Missing id for the Question: $item")
          is QuestionnaireAdapterItem.RepeatedGroupHeader -> item.id
          is QuestionnaireAdapterItem.Navigation -> "navigation"
          is QuestionnaireAdapterItem.RepeatedGroupAddButton -> item.id
              ?: throw IllegalStateException("Missing id for the RepeatedGroupAddButton: $item")
        }
      },
    ) { adapterItem: QuestionnaireAdapterItem ->
      when (adapterItem) {
        is QuestionnaireAdapterItem.Question -> {
          val questionnaireViewHolderType = getItemViewTypeForQuestion(adapterItem.item)
          val questionnaireItemViewHolderDelegate =
            getQuestionnaireItemViewFactory(questionnaireViewHolderType)
          questionnaireItemViewHolderDelegate.Content(adapterItem.item)
        }
        is QuestionnaireAdapterItem.Navigation -> {
          QuestionnaireBottomNavigation(adapterItem.questionnaireNavigationUIState)
        }
        is QuestionnaireAdapterItem.RepeatedGroupHeader -> {
          RepeatedGroupHeaderItem(adapterItem)
        }
        is QuestionnaireAdapterItem.RepeatedGroupAddButton -> {
          RepeatedGroupAddButtonItem(adapterItem)
        }
      }
    }
  }
}

@Composable
internal fun QuestionnaireReviewList(items: List<QuestionnaireAdapterItem>) {
  LazyColumn {
    items(
      items = items,
      key = { item ->
        when (item) {
          is QuestionnaireAdapterItem.Question -> item.id
              ?: throw IllegalStateException("Missing id for the Question: $item")
          is QuestionnaireAdapterItem.RepeatedGroupHeader -> item.id
          is QuestionnaireAdapterItem.Navigation -> "navigation"
          is QuestionnaireAdapterItem.RepeatedGroupAddButton -> item.id
              ?: throw IllegalStateException("Missing id for the RepeatedGroupAddButton: $item")
        }
      },
    ) { item: QuestionnaireAdapterItem ->
      when (item) {
        is QuestionnaireAdapterItem.Question -> {
          QuestionnaireReviewItem(
            questionnaireViewItem = item.item,
            modifier = Modifier.fillMaxWidth(),
          )
        }
        is QuestionnaireAdapterItem.Navigation -> {
          QuestionnaireBottomNavigation(
            navigationState = item.questionnaireNavigationUIState,
            modifier = Modifier.fillMaxWidth(),
          )
        }
        is QuestionnaireAdapterItem.RepeatedGroupHeader -> {
          // TODO not implemented
        }
        is QuestionnaireAdapterItem.RepeatedGroupAddButton -> {
          //  TODO not implemented
        }
      }
    }
  }
}

@Composable
private fun QuestionnaireReviewItem(
  questionnaireViewItem: QuestionnaireViewItem,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp),
  ) {
    // Header section with prefix, question, and hint
    val hasPrefix = questionnaireViewItem.questionnaireItem.prefix?.value?.isNotEmpty() == true
    val hasQuestion = questionnaireViewItem.questionText?.isNotEmpty() == true
    val hasHint =
      questionnaireViewItem.enabledDisplayItems.any { it.text?.value?.isNotEmpty() == true }

    if (hasPrefix || hasQuestion || hasHint) {
      Column {
        // Question with optional prefix
        if (hasPrefix || hasQuestion) {
          val questionText = buildString {
            if (hasPrefix) {
              append(questionnaireViewItem.questionnaireItem.prefix ?: "")
              if (hasQuestion) append(" ")
            }
            if (hasQuestion) {
              append(questionnaireViewItem.questionText?.toString() ?: "")
            }
          }

          Text(
            text = questionText,
            style = QuestionnaireTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = QuestionnaireTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp),
          )
        }

        // Hint/instructions. Should we show the hint instructions?
        /*if (hasHint) {
          questionnaireViewItem.enabledDisplayItems.forEach { displayItem ->
            displayItem.text?.let { hintText ->
              if (hintText.isNotEmpty()) {
                Text(
                  text = hintText,
                  style = QuestionnaireTheme.typography.bodyMedium,
                  color = QuestionnaireTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(bottom = 8.dp),
                )
              }
            }
          }
        }*/
      }
    }

    // Flyover text
    questionnaireViewItem.enabledDisplayItems.forEach { displayItem ->
      displayItem.extension.forEach { ext ->
        if (ext.url == "http://hl7.org/fhir/StructureDefinition/questionnaire-displayCategory") {
          ext.value?.asString()?.value?.value?.let { flyoverText ->
            if (flyoverText.isNotEmpty()) {
              Text(
                text = flyoverText,
                style = QuestionnaireTheme.typography.bodyMedium,
                color = QuestionnaireTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
              )
            }
          }
        }
      }
    }

    // Answer section (only for non-group, non-display items)
    when (questionnaireViewItem.questionnaireItem.type.value) {
      Questionnaire.QuestionnaireItemType.Group,
      Questionnaire.QuestionnaireItemType.Display, -> {
        // No answer display for groups and display items
      }
      else -> {
        val answerText =
          questionnaireViewItem.answers
            .map { itemAnswer -> itemAnswer.value }
            .joinToString(", ") { answer ->
              when (answer) {
                is QuestionnaireResponse.Item.Answer.Value.String -> answer.asString()?.value?.value
                is QuestionnaireResponse.Item.Answer.Value.Integer ->
                  answer.asInteger()?.value?.value.toString()
                is QuestionnaireResponse.Item.Answer.Value.Decimal ->
                  answer.asDecimal()?.value?.value?.toStringExpanded()
                is QuestionnaireResponse.Item.Answer.Value.Date ->
                  answer.asDate()?.value?.value?.toString()
                is QuestionnaireResponse.Item.Answer.Value.Time ->
                  answer.asTime()?.value?.value?.toString()
                is QuestionnaireResponse.Item.Answer.Value.DateTime ->
                  answer.asDateTime()?.value?.value?.toString()
                is QuestionnaireResponse.Item.Answer.Value.Quantity ->
                  answer.asQuantity()?.value?.value?.value?.toStringExpanded()
                is QuestionnaireResponse.Item.Answer.Value.Coding ->
                  answer.asCoding()?.value?.code?.value.toString()
                else -> ""
              }
                as CharSequence?
                ?: ""
            }

        if (answerText.isNotEmpty()) {
          Text(
            text = answerText,
            style = QuestionnaireTheme.typography.bodyLarge,
            color = QuestionnaireTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
          )
        }

        // Error display
        if (
          questionnaireViewItem.validationResult
            is com.google.android.fhir.datacapture.validation.Invalid
        ) {
          Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Error",
              tint = QuestionnaireTheme.colorScheme.error,
            )
            Text(
              text = questionnaireViewItem.validationResult.getSingleStringValidationMessage(),
              style = QuestionnaireTheme.typography.bodyMedium,
              color = QuestionnaireTheme.colorScheme.error,
            )
          }
        }
      }
    }

    // Divider
    HorizontalDivider(
      modifier = Modifier.padding(top = 16.dp),
      color = QuestionnaireTheme.colorScheme.outlineVariant,
      thickness = 0.5.dp,
    )
  }
}

// TODO provide option to override factory
/*private fun getQuestionnaireItemViewHolder(
  parent: ViewGroup,
  questionnaireViewItem: QuestionnaireViewItem,
  questionnaireItemViewHolderMatchers:
  List<QuestionnaireItemViewHolderFactoryMatcher>,
): QuestionnaireItemViewHolder {
  // Find a matching custom widget
  val questionnaireViewHolderFactory =
    questionnaireItemViewHolderMatchers
      .find { it.matches(questionnaireViewItem.questionnaireItem) }
      ?.factory
      ?: getQuestionnaireItemViewHolderFactory(getItemViewTypeForQuestion(questionnaireViewItem))
  return questionnaireViewHolderFactory.create(parent)
}*/

fun getQuestionnaireItemViewFactory(
  questionnaireViewHolderType: QuestionnaireViewHolderType,
): QuestionnaireItemViewFactory {
  return when (questionnaireViewHolderType) {
    QuestionnaireViewHolderType.EDIT_TEXT_SINGLE_LINE -> EditTextSingleLineViewFactory
    QuestionnaireViewHolderType.EDIT_TEXT_MULTI_LINE -> EditTextMultiLineViewFactory
    QuestionnaireViewHolderType.EDIT_TEXT_INTEGER -> EditTextIntegerViewFactory
    QuestionnaireViewHolderType.EDIT_TEXT_DECIMAL -> EditTextDecimalViewFactory
    QuestionnaireViewHolderType.QUANTITY -> QuantityViewFactory
    QuestionnaireViewHolderType.DISPLAY -> DisplayViewFactory
    QuestionnaireViewHolderType.SLIDER -> SliderViewFactory
    QuestionnaireViewHolderType.PHONE_NUMBER -> EditTextPhoneNumberViewFactory
    else -> TODO()
  }
}

/**
 * Returns the [QuestionnaireViewHolderType] that will be used to render the
 * [QuestionnaireViewItem]. This is determined by a combination of the data type of the question and
 * any additional Questionnaire Item UI Control Codes
 * (http://hl7.org/fhir/R4/valueset-questionnaire-item-control.html) used in the itemControl
 * extension (http://hl7.org/fhir/R4/extension-questionnaire-itemcontrol.html).
 */
private fun getItemViewTypeForQuestion(
  questionnaireViewItem: QuestionnaireViewItem,
): QuestionnaireViewHolderType {
  val questionnaireItem = questionnaireViewItem.questionnaireItem

  if (questionnaireViewItem.enabledAnswerOptions.isNotEmpty()) {
    return getChoiceViewHolderType(questionnaireViewItem)
  }

  return when (val type = questionnaireItem.type.value) {
    Questionnaire.QuestionnaireItemType.Group -> QuestionnaireViewHolderType.GROUP
    Questionnaire.QuestionnaireItemType.Boolean -> QuestionnaireViewHolderType.BOOLEAN_TYPE_PICKER
    Questionnaire.QuestionnaireItemType.Date -> QuestionnaireViewHolderType.DATE_PICKER
    Questionnaire.QuestionnaireItemType.Time -> QuestionnaireViewHolderType.TIME_PICKER
    Questionnaire.QuestionnaireItemType.DateTime -> QuestionnaireViewHolderType.DATE_TIME_PICKER
    Questionnaire.QuestionnaireItemType.String -> getStringViewHolderType(questionnaireViewItem)
    Questionnaire.QuestionnaireItemType.Text -> QuestionnaireViewHolderType.EDIT_TEXT_MULTI_LINE
    Questionnaire.QuestionnaireItemType.Integer -> getIntegerViewHolderType(questionnaireViewItem)
    Questionnaire.QuestionnaireItemType.Decimal -> QuestionnaireViewHolderType.EDIT_TEXT_DECIMAL
    Questionnaire.QuestionnaireItemType.Choice,
    Questionnaire.QuestionnaireItemType.Reference, -> getChoiceViewHolderType(questionnaireViewItem)
    Questionnaire.QuestionnaireItemType.Display -> QuestionnaireViewHolderType.DISPLAY
    Questionnaire.QuestionnaireItemType.Quantity -> QuestionnaireViewHolderType.QUANTITY
    Questionnaire.QuestionnaireItemType.Attachment -> QuestionnaireViewHolderType.ATTACHMENT
    else -> throw NotImplementedError("Question type $type not supported.")
  }
}

private fun getChoiceViewHolderType(
  questionnaireViewItem: QuestionnaireViewItem,
): QuestionnaireViewHolderType {
  val questionnaireItem = questionnaireViewItem.questionnaireItem

  // Use the view type that the client wants if they specified an itemControl or dialog extension
  return when {
    questionnaireItem.shouldUseDialog -> QuestionnaireViewHolderType.DIALOG_SELECT
    else -> questionnaireItem.itemControl?.viewHolderType
  }
  // Otherwise, choose a sensible UI element automatically
  ?: run {
      val numOptions = questionnaireViewItem.enabledAnswerOptions.size
      when {
        // Always use a dialog for questions with a large number of options
        numOptions >= MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DIALOG ->
          QuestionnaireViewHolderType.DIALOG_SELECT

        // Use a check box group if repeated answers are permitted
        questionnaireItem.repeats?.value == true -> QuestionnaireViewHolderType.CHECK_BOX_GROUP

        // Use a dropdown if there are a medium number of options
        numOptions >= MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN ->
          QuestionnaireViewHolderType.DROP_DOWN

        // Use a radio group only if there are a small number of options
        else -> QuestionnaireViewHolderType.RADIO_GROUP
      }
    }
}

private fun getIntegerViewHolderType(
  questionnaireViewItem: QuestionnaireViewItem,
): QuestionnaireViewHolderType {
  val questionnaireItem = questionnaireViewItem.questionnaireItem
  // Use the view type that the client wants if they specified an itemControl
  return questionnaireItem.itemControl?.viewHolderType
    ?: QuestionnaireViewHolderType.EDIT_TEXT_INTEGER
}

private fun getStringViewHolderType(
  questionnaireViewItem: QuestionnaireViewItem,
): QuestionnaireViewHolderType {
  val questionnaireItem = questionnaireViewItem.questionnaireItem
  // Use the view type that the client wants if they specified an itemControl
  return questionnaireItem.itemControl?.viewHolderType
    ?: QuestionnaireViewHolderType.EDIT_TEXT_SINGLE_LINE
}
