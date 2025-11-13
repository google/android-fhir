/*
 * Copyright 2024-2025 Google LLC
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

import android.view.ViewGroup
import android.widget.LinearLayout
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.QUESTIONNAIRE_EDIT_LIST
import com.google.android.fhir.datacapture.contrib.views.PhoneNumberViewHolderFactory
import com.google.android.fhir.datacapture.extensions.inflate
import com.google.android.fhir.datacapture.extensions.itemControl
import com.google.android.fhir.datacapture.extensions.shouldUseDialog
import com.google.android.fhir.datacapture.views.NavigationViewHolder
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.RepeatedGroupAddItemViewHolder
import com.google.android.fhir.datacapture.views.factories.AttachmentViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.AutoCompleteViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.BooleanChoiceViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.CheckBoxGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.DatePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.DateTimePickerViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.DisplayViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.DropDownViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.EditTextDecimalViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.EditTextIntegerViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.EditTextMultiLineViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.EditTextSingleLineViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.GroupViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuantityViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemDialogSelectViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolder
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.RadioGroupViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.RepeatedGroupHeaderItemViewHolder
import com.google.android.fhir.datacapture.views.factories.SliderViewHolderFactory
import com.google.android.fhir.datacapture.views.factories.TimePickerViewHolderFactory
import kotlin.uuid.ExperimentalUuidApi
import org.hl7.fhir.r4.model.Questionnaire

// Choice questions are rendered as dialogs if they have at least this many options
const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DIALOG = 10

// Choice questions are rendered as radio group if number of options less than this constant
const val MINIMUM_NUMBER_OF_ANSWER_OPTIONS_FOR_DROP_DOWN = 4

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun QuestionnaireEditList(
  items: List<QuestionnaireAdapterItem>,
  displayMode: DisplayMode,
  questionnaireItemViewHolderMatchers:
    List<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher>,
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
      AndroidView(
        factory = { context ->
          LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            ViewCompat.setNestedScrollingEnabled(this, false)
          }
        },
        modifier = Modifier.fillMaxWidth(),
        update = { view ->
          val existingViewHolder = view.getTag(R.id.question_view_holder)

          val createViews =
            when {
              existingViewHolder == null -> true
              adapterItem is QuestionnaireAdapterItem.Question &&
                existingViewHolder !is QuestionnaireItemViewHolder -> true
              adapterItem is QuestionnaireAdapterItem.Navigation &&
                existingViewHolder !is NavigationViewHolder -> true
              adapterItem is QuestionnaireAdapterItem.RepeatedGroupHeader &&
                existingViewHolder !is RepeatedGroupHeaderItemViewHolder -> true
              adapterItem is QuestionnaireAdapterItem.RepeatedGroupAddButton &&
                existingViewHolder !is RepeatedGroupAddItemViewHolder -> true
              else -> false
            }

          if (createViews) {
            view.removeAllViews()
            when (adapterItem) {
              is QuestionnaireAdapterItem.Question -> {
                val viewHolder =
                  getQuestionnaireItemViewHolder(
                    parent = view,
                    questionnaireViewItem = adapterItem.item,
                    questionnaireItemViewHolderMatchers = questionnaireItemViewHolderMatchers,
                  )
                view.setTag(R.id.question_view_holder, viewHolder)
                view.addView(viewHolder.itemView)
                viewHolder.bind(adapterItem.item)
              }
              is QuestionnaireAdapterItem.Navigation -> {
                val viewHolder =
                  NavigationViewHolder(view.inflate(R.layout.pagination_navigation_view))
                view.setTag(R.id.question_view_holder, viewHolder)
                view.addView(viewHolder.itemView)
                viewHolder.bind(adapterItem.questionnaireNavigationUIState)
              }
              is QuestionnaireAdapterItem.RepeatedGroupHeader -> {
                val viewHolder =
                  RepeatedGroupHeaderItemViewHolder(
                    view.inflate(R.layout.repeated_group_instance_header_view),
                  )
                view.setTag(R.id.question_view_holder, viewHolder)
                view.addView(viewHolder.itemView)
                viewHolder.bind(adapterItem)
              }
              is QuestionnaireAdapterItem.RepeatedGroupAddButton -> {
                val viewHolder =
                  RepeatedGroupAddItemViewHolder(
                    view.inflate(R.layout.add_repeated_item),
                  )
                view.setTag(R.id.question_view_holder, viewHolder)
                view.addView(viewHolder.itemView)
                viewHolder.bind(adapterItem.item)
              }
            }
          } else {
            // Update existing view holder
            when (adapterItem) {
              is QuestionnaireAdapterItem.Question -> {
                (existingViewHolder as QuestionnaireItemViewHolder).bind(adapterItem.item)
              }
              is QuestionnaireAdapterItem.Navigation -> {
                (existingViewHolder as NavigationViewHolder).bind(
                  adapterItem.questionnaireNavigationUIState,
                )
              }
              is QuestionnaireAdapterItem.RepeatedGroupHeader -> {
                (existingViewHolder as RepeatedGroupHeaderItemViewHolder).bind(adapterItem)
              }
              is QuestionnaireAdapterItem.RepeatedGroupAddButton -> {
                (existingViewHolder as RepeatedGroupAddItemViewHolder).bind(adapterItem.item)
              }
            }
          }
        },
        onReset = { view -> view.setTag(R.id.question_view_holder, null) },
      )
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
          // TODO("Not implemented yet")
        }
        is QuestionnaireAdapterItem.RepeatedGroupAddButton -> {
          // TODO("Not implemented yet")
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
    val hasPrefix = questionnaireViewItem.questionnaireItem.prefix?.isNotEmpty() == true
    val hasQuestion = questionnaireViewItem.questionText?.isNotEmpty() == true
    val hasHint = questionnaireViewItem.enabledDisplayItems.any { it.text?.isNotEmpty() == true }

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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
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
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
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
      displayItem.extension?.forEach { ext ->
        if (ext.url == "http://hl7.org/fhir/StructureDefinition/questionnaire-displayCategory") {
          ext.value?.primitiveValue()?.let { flyoverText ->
            if (flyoverText.isNotEmpty()) {
              Text(
                text = flyoverText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
              )
            }
          }
        }
      }
    }

    // Answer section (only for non-group, non-display items)
    when (questionnaireViewItem.questionnaireItem.type) {
      Questionnaire.QuestionnaireItemType.GROUP,
      Questionnaire.QuestionnaireItemType.DISPLAY, -> {
        // No answer display for groups and display items
      }
      else -> {
        val answerText =
          questionnaireViewItem.answers.joinToString(", ") { answer ->
            when {
              answer.hasValueStringType() -> answer.valueStringType.value
              answer.hasValueIntegerType() -> answer.valueIntegerType.value.toString()
              answer.hasValueDecimalType() -> answer.valueDecimalType.value.toString()
              answer.hasValueBooleanType() -> if (answer.valueBooleanType.value) "Yes" else "No"
              answer.hasValueDateType() -> answer.valueDateType.valueAsString
              answer.hasValueTimeType() -> answer.valueTimeType.valueAsString
              answer.hasValueDateTimeType() -> answer.valueDateTimeType.valueAsString
              answer.hasValueQuantity() -> answer.valueQuantity.value.toString()
              answer.hasValueCoding() -> answer.valueCoding.display ?: answer.valueCoding.code
              else -> ""
            }
          }

        if (answerText.isNotEmpty()) {
          Text(
            text = answerText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
              tint = MaterialTheme.colorScheme.error,
            )
            Text(
              text = questionnaireViewItem.validationResult.getSingleStringValidationMessage(),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.error,
            )
          }
        }
      }
    }

    // Divider
    HorizontalDivider(
      modifier = Modifier.padding(top = 16.dp),
      color = MaterialTheme.colorScheme.outlineVariant,
      thickness = 0.5.dp,
    )
  }
}

private fun getQuestionnaireItemViewHolder(
  parent: ViewGroup,
  questionnaireViewItem: QuestionnaireViewItem,
  questionnaireItemViewHolderMatchers:
    List<QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatcher>,
): QuestionnaireItemViewHolder {
  // Find a matching custom widget
  val questionnaireViewHolderFactory =
    questionnaireItemViewHolderMatchers
      .find { it.matches(questionnaireViewItem.questionnaireItem) }
      ?.factory
      ?: getQuestionnaireItemViewHolderFactory(getItemViewTypeForQuestion(questionnaireViewItem))
  return questionnaireViewHolderFactory.create(parent)
}

private fun getQuestionnaireItemViewHolderFactory(
  questionnaireViewHolderType: QuestionnaireViewHolderType,
): QuestionnaireItemViewHolderFactory {
  val viewHolderFactory =
    when (questionnaireViewHolderType) {
      QuestionnaireViewHolderType.GROUP -> GroupViewHolderFactory
      QuestionnaireViewHolderType.BOOLEAN_TYPE_PICKER -> BooleanChoiceViewHolderFactory
      QuestionnaireViewHolderType.DATE_PICKER -> DatePickerViewHolderFactory
      QuestionnaireViewHolderType.TIME_PICKER -> TimePickerViewHolderFactory
      QuestionnaireViewHolderType.DATE_TIME_PICKER -> DateTimePickerViewHolderFactory
      QuestionnaireViewHolderType.EDIT_TEXT_SINGLE_LINE -> EditTextSingleLineViewHolderFactory
      QuestionnaireViewHolderType.EDIT_TEXT_MULTI_LINE -> EditTextMultiLineViewHolderFactory
      QuestionnaireViewHolderType.EDIT_TEXT_INTEGER -> EditTextIntegerViewHolderFactory
      QuestionnaireViewHolderType.EDIT_TEXT_DECIMAL -> EditTextDecimalViewHolderFactory
      QuestionnaireViewHolderType.RADIO_GROUP -> RadioGroupViewHolderFactory
      QuestionnaireViewHolderType.DROP_DOWN -> DropDownViewHolderFactory
      QuestionnaireViewHolderType.DISPLAY -> DisplayViewHolderFactory
      QuestionnaireViewHolderType.QUANTITY -> QuantityViewHolderFactory
      QuestionnaireViewHolderType.CHECK_BOX_GROUP -> CheckBoxGroupViewHolderFactory
      QuestionnaireViewHolderType.AUTO_COMPLETE -> AutoCompleteViewHolderFactory
      QuestionnaireViewHolderType.DIALOG_SELECT -> QuestionnaireItemDialogSelectViewHolderFactory
      QuestionnaireViewHolderType.SLIDER -> SliderViewHolderFactory
      QuestionnaireViewHolderType.PHONE_NUMBER -> PhoneNumberViewHolderFactory
      QuestionnaireViewHolderType.ATTACHMENT -> AttachmentViewHolderFactory
    }
  return viewHolderFactory
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

  return when (val type = questionnaireItem.type) {
    Questionnaire.QuestionnaireItemType.GROUP -> QuestionnaireViewHolderType.GROUP
    Questionnaire.QuestionnaireItemType.BOOLEAN -> QuestionnaireViewHolderType.BOOLEAN_TYPE_PICKER
    Questionnaire.QuestionnaireItemType.DATE -> QuestionnaireViewHolderType.DATE_PICKER
    Questionnaire.QuestionnaireItemType.TIME -> QuestionnaireViewHolderType.TIME_PICKER
    Questionnaire.QuestionnaireItemType.DATETIME -> QuestionnaireViewHolderType.DATE_TIME_PICKER
    Questionnaire.QuestionnaireItemType.STRING -> getStringViewHolderType(questionnaireViewItem)
    Questionnaire.QuestionnaireItemType.TEXT -> QuestionnaireViewHolderType.EDIT_TEXT_MULTI_LINE
    Questionnaire.QuestionnaireItemType.INTEGER -> getIntegerViewHolderType(questionnaireViewItem)
    Questionnaire.QuestionnaireItemType.DECIMAL -> QuestionnaireViewHolderType.EDIT_TEXT_DECIMAL
    Questionnaire.QuestionnaireItemType.CHOICE,
    Questionnaire.QuestionnaireItemType.REFERENCE, -> getChoiceViewHolderType(questionnaireViewItem)
    Questionnaire.QuestionnaireItemType.DISPLAY -> QuestionnaireViewHolderType.DISPLAY
    Questionnaire.QuestionnaireItemType.QUANTITY -> QuestionnaireViewHolderType.QUANTITY
    Questionnaire.QuestionnaireItemType.ATTACHMENT -> QuestionnaireViewHolderType.ATTACHMENT
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
        questionnaireItem.repeats -> QuestionnaireViewHolderType.CHECK_BOX_GROUP

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
