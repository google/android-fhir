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

package com.google.android.fhir.datacapture.views.compose

import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.StyleUrl
import com.google.android.fhir.datacapture.extensions.appendAsteriskToQuestionText
import com.google.android.fhir.datacapture.extensions.applyCustomOrDefaultStyle
import com.google.android.fhir.datacapture.extensions.getLocalizedInstructionsSpanned
import com.google.android.fhir.datacapture.extensions.getStyleResIdFromAttribute
import com.google.android.fhir.datacapture.extensions.hasHelpButton
import com.google.android.fhir.datacapture.extensions.localizedHelpSpanned
import com.google.android.fhir.datacapture.extensions.localizedPrefixSpanned
import com.google.android.fhir.datacapture.extensions.readCustomStyleExtension
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem

@Composable
fun Header(
  questionnaireViewItem: QuestionnaireViewItem,
  displayValidationResult: Boolean = false,
  showRequiredOrOptionalText: Boolean = false,
) {
  val context = LocalContext.current
  val validationResult = questionnaireViewItem.validationResult
  val questionnaireItem = questionnaireViewItem.questionnaireItem
  val questionnaireResponseItem = questionnaireViewItem.getQuestionnaireResponseItem()
  val requiredOptionalText =
    when {
      (questionnaireItem.required &&
        questionnaireViewItem.questionViewTextConfiguration.showRequiredText) ->
        stringResource(R.string.required)
      (!questionnaireItem.required &&
        questionnaireViewItem.questionViewTextConfiguration.showOptionalText) ->
        stringResource(R.string.optional_helper_text)
      else -> null
    }

  val prefixLocalizedText = questionnaireViewItem.questionnaireItem.localizedPrefixSpanned
  val questionLocalizedText = appendAsteriskToQuestionText(context, questionnaireViewItem)
  val hintLocalizedText =
    questionnaireViewItem.enabledDisplayItems.getLocalizedInstructionsSpanned()
  val itemLocalizedHelpSpanned = questionnaireItem.localizedHelpSpanned

  //  This is to avoid an empty row in the questionnaire.
  if (
    listOf(prefixLocalizedText, questionLocalizedText, hintLocalizedText, itemLocalizedHelpSpanned)
      .any { !it.isNullOrBlank() } ||
      (showRequiredOrOptionalText && !requiredOptionalText.isNullOrBlank()) ||
      (displayValidationResult && validationResult is Invalid)
  ) {
    Header(
      prefixLocalizedText = prefixLocalizedText,
      questionLocalizedText = questionLocalizedText,
      readCustomStyleName = remember { { questionnaireItem.readCustomStyleExtension(it) } },
      hintLocalizedText = hintLocalizedText,
      isHelpCardOpen = questionnaireViewItem.isHelpCardOpen,
      isHelpButtonVisible = questionnaireItem.hasHelpButton,
      helpCardLocalizedText = itemLocalizedHelpSpanned,
      helpButtonOnClick = {
        questionnaireViewItem.helpCardStateChangedCallback(it, questionnaireResponseItem)
      },
      validationResult = validationResult,
      displayValidationResult = displayValidationResult,
      showRequiredOrOptionalText = showRequiredOrOptionalText,
      requiredOptionalText = requiredOptionalText,
    )
  }
}

@Composable
internal fun Header(
  prefixLocalizedText: Spanned?,
  questionLocalizedText: Spanned,
  readCustomStyleName: (StyleUrl) -> String?,
  hintLocalizedText: Spanned?,
  isHelpCardOpen: Boolean,
  isHelpButtonVisible: Boolean,
  helpCardLocalizedText: Spanned?,
  helpButtonOnClick: (Boolean) -> Unit,
  validationResult: ValidationResult,
  displayValidationResult: Boolean,
  showRequiredOrOptionalText: Boolean,
  requiredOptionalText: String?,
) {
  Column(
    modifier =
      Modifier.fillMaxWidth()
        .padding(bottom = dimensionResource(R.dimen.header_padding_bottom))
        .testTag(HEADER_TAG),
  ) {
    PrefixQuestionTitle(prefixLocalizedText, questionLocalizedText, readCustomStyleName)

    Help(
      hintLocalizedText,
      readCustomStyleName,
      isHelpCardOpen,
      isHelpButtonVisible,
      helpButtonOnClick,
      helpCardLocalizedText,
    )

    // Required/Optional Text
    if (showRequiredOrOptionalText && !requiredOptionalText.isNullOrBlank()) {
      Text(text = requiredOptionalText, style = MaterialTheme.typography.bodyMedium)
    }

    // Validation Error
    if (displayValidationResult && validationResult is Invalid) {
      Text(
        modifier = Modifier.testTag(ERROR_TEXT_AT_HEADER_TEST_TAG),
        text = validationResult.getSingleStringValidationMessage(),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
      )
    }
  }
}

@Composable
internal fun PrefixQuestionTitle(
  prefixLocalizedText: Spanned?,
  questionLocalizedText: Spanned,
  readCustomStyleName: (StyleUrl) -> String?,
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    if (!prefixLocalizedText.isNullOrBlank()) {
      AndroidView(
        factory = {
          TextView(it).apply {
            id = R.id.prefix
            applyCustomOrDefaultStyle(
              context = it,
              view = this,
              customStyleName =
                readCustomStyleName(
                  StyleUrl.PREFIX_TEXT_VIEW,
                ),
              defaultStyleResId =
                getStyleResIdFromAttribute(it, R.attr.questionnaireQuestionTextStyle),
            )
          }
        },
        update = { it.text = prefixLocalizedText },
      )
      Spacer(modifier = Modifier.width(dimensionResource(R.dimen.prefix_padding_end)))
    }
    AndroidView(
      factory = {
        TextView(it).apply {
          id = R.id.question
          movementMethod = LinkMovementMethod.getInstance()
          applyCustomOrDefaultStyle(
            context = it,
            view = this,
            customStyleName =
              readCustomStyleName(
                StyleUrl.QUESTION_TEXT_VIEW,
              ),
            defaultStyleResId =
              getStyleResIdFromAttribute(it, R.attr.questionnaireQuestionTextStyle),
          )
        }
      },
      modifier = Modifier.weight(1f),
      update = { it.text = questionLocalizedText },
    )
  }
}

@Composable
internal fun Help(
  hintLocalizedText: Spanned?,
  readCustomStyleName: (StyleUrl) -> String?,
  isHelpCardInitiallyOpen: Boolean,
  isHelpButtonVisible: Boolean,
  helpButtonOnClick: (Boolean) -> Unit,
  helpCardLocalizedText: Spanned?,
) {
  var isCardOpen by remember { mutableStateOf(isHelpCardInitiallyOpen) }

  Row(
    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.help_container_margin_top)),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    AndroidView(
      factory = {
        TextView(it).apply {
          id = R.id.hint
          movementMethod = LinkMovementMethod.getInstance()
          applyCustomOrDefaultStyle(
            context = it,
            view = this,
            customStyleName =
              readCustomStyleName(
                StyleUrl.SUBTITLE_TEXT_VIEW,
              ),
            defaultStyleResId =
              getStyleResIdFromAttribute(it, R.attr.questionnaireSubtitleTextStyle),
          )
        }
      },
      update = { it.text = hintLocalizedText },
    )

    if (isHelpButtonVisible) {
      IconButton(
        onClick = {
          isCardOpen = !isCardOpen
          helpButtonOnClick(isCardOpen)
        },
        modifier =
          Modifier.padding(start = dimensionResource(R.dimen.help_button_margin_start))
            .testTag(HELP_BUTTON_TAG)
            .size(
              width = dimensionResource(R.dimen.help_button_width),
              height = dimensionResource(R.dimen.help_button_height),
            ),
      ) {
        Icon(
          painterResource(R.drawable.ic_help_48px),
          contentDescription = stringResource(R.string.help),
        )
      }
    }
  }

  if (isCardOpen) {
    Card(modifier = Modifier.padding(top = 8.dp).testTag(HELP_CARD_TAG)) {
      Column(modifier = Modifier.padding(8.dp)) {
        Text(
          text = stringResource(id = R.string.help),
          modifier =
            Modifier.padding(horizontal = dimensionResource(R.dimen.help_header_margin_horizontal)),
          style = MaterialTheme.typography.titleSmall,
        )

        AndroidView(
          factory = {
            TextView(it).apply {
              id = R.id.helpText
              movementMethod = LinkMovementMethod.getInstance()
            }
          },
          modifier =
            Modifier.padding(horizontal = dimensionResource(R.dimen.help_text_margin_horizontal)),
          update = { it.text = helpCardLocalizedText },
        )
      }
    }
  }
}

const val ERROR_TEXT_AT_HEADER_TEST_TAG = "error_text_at_header"
const val HELP_BUTTON_TAG = "helpButton"
const val HELP_CARD_TAG = "helpCardView"
const val HEADER_TAG = "headerView"
