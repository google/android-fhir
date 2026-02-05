/*
 * Copyright 2025-2026 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.help
import android_fhir.datacapture_kmp.generated.resources.required
import android_fhir.datacapture_kmp.generated.resources.space_asterisk
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.extensions.StyleUrl
import com.google.android.fhir.datacapture.extensions.getLocalizedInstructionsAnnotatedString
import com.google.android.fhir.datacapture.extensions.getLocalizedText
import com.google.android.fhir.datacapture.extensions.hasHelpButton
import com.google.android.fhir.datacapture.extensions.localizedHelpAnnotatedString
import com.google.android.fhir.datacapture.extensions.localizedPrefixAnnotatedString
import com.google.android.fhir.datacapture.extensions.readCustomStyleExtension
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun Header(
  questionnaireViewItem: QuestionnaireViewItem,
  displayValidationResult: Boolean = false,
  showRequiredOrOptionalText: Boolean = false,
) {
  val validationResult =
    remember(questionnaireViewItem.validationResult) { questionnaireViewItem.validationResult }
  val questionnaireItem =
    remember(questionnaireViewItem.questionnaireItem) { questionnaireViewItem.questionnaireItem }
  val questionnaireResponseItem =
    remember(questionnaireViewItem) { questionnaireViewItem.getQuestionnaireResponseItem() }
  val requiredOptionalText = getRequiredOrOptionalText(questionnaireViewItem)

  val prefixLocalizedText = questionnaireViewItem.questionnaireItem.localizedPrefixAnnotatedString
  val spaceAsterisk = stringResource(Res.string.space_asterisk)
  val questionLocalizedText =
    remember(questionnaireViewItem) {
      buildAnnotatedString {
        questionnaireViewItem.questionText?.let { append(it) }
        if (
          questionnaireViewItem.questionViewTextConfiguration.showAsterisk &&
            questionnaireViewItem.questionnaireItem.required?.value == true &&
            !questionnaireViewItem.questionnaireItem.text?.getLocalizedText().isNullOrEmpty()
        ) {
          append(spaceAsterisk)
        }
      }
    }
  val hintLocalizedText =
    questionnaireViewItem.enabledDisplayItems.getLocalizedInstructionsAnnotatedString()
  val itemLocalizedHelpText = questionnaireItem.localizedHelpAnnotatedString

  //  This is to avoid an empty row in the questionnaire.
  if (
    listOf(prefixLocalizedText, questionLocalizedText, hintLocalizedText, itemLocalizedHelpText)
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
      helpCardLocalizedText = itemLocalizedHelpText,
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
  prefixLocalizedText: AnnotatedString?,
  questionLocalizedText: AnnotatedString,
  readCustomStyleName: (StyleUrl) -> String?,
  hintLocalizedText: AnnotatedString?,
  isHelpCardOpen: Boolean,
  isHelpButtonVisible: Boolean,
  helpCardLocalizedText: AnnotatedString?,
  helpButtonOnClick: (Boolean) -> Unit,
  validationResult: ValidationResult,
  displayValidationResult: Boolean,
  showRequiredOrOptionalText: Boolean,
  requiredOptionalText: String?,
) {
  Column(
    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp).testTag(HEADER_TAG),
  ) {
    PrefixQuestionTitle(prefixLocalizedText, questionLocalizedText, readCustomStyleName)

    if (!hintLocalizedText.isNullOrBlank() || isHelpButtonVisible || isHelpCardOpen) {
      Help(
        hintLocalizedText,
        readCustomStyleName,
        isHelpCardOpen,
        isHelpButtonVisible,
        helpButtonOnClick,
        helpCardLocalizedText,
      )
    }

    // Required/Optional Text
    if (showRequiredOrOptionalText && !requiredOptionalText.isNullOrBlank()) {
      Text(text = requiredOptionalText, style = QuestionnaireTheme.typography.bodyMedium)
    }

    // Validation Error
    if (displayValidationResult && validationResult is Invalid) {
      Text(
        modifier = Modifier.testTag(ERROR_TEXT_AT_HEADER_TEST_TAG),
        text = validationResult.singleStringValidationMessage,
        color = QuestionnaireTheme.colorScheme.error,
        style = QuestionnaireTheme.typography.bodySmall,
      )
    }
  }
}

@Composable
internal fun PrefixQuestionTitle(
  prefixLocalizedText: AnnotatedString?,
  questionLocalizedText: AnnotatedString,
  readCustomStyleName: (StyleUrl) -> String?,
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    if (!prefixLocalizedText.isNullOrBlank()) {
      Text(
        prefixLocalizedText,
        style = QuestionnaireTheme.textStyles.questionText,
        modifier = Modifier.testTag(PREFIX_HEADER_TAG),
      )

      //      AndroidView(
      //        factory = {
      //          TextView(it).apply {
      //            id = R.id.prefix
      //            applyCustomOrDefaultStyle(
      //              context = it,
      //              view = this,
      //              customStyleName =
      //                readCustomStyleName(
      //                  StyleUrl.PREFIX_TEXT_VIEW,
      //                ),
      //              defaultStyleResId =
      //                getStyleResIdFromAttribute(it, R.attr.questionnaireQuestionTextStyle),
      //            )
      //          }
      //        },
      //        update = { it.text = prefixLocalizedText },
      //      )
      Spacer(modifier = Modifier.width(5.dp))
    }

    Text(
      questionLocalizedText,
      style = QuestionnaireTheme.textStyles.questionText,
      modifier = Modifier.testTag(QUESTION_HEADER_TAG),
    )
    //    AndroidView(
    //      factory = {
    //        TextView(it).apply {
    //          id = R.id.question
    //          movementMethod = LinkMovementMethod.getInstance()
    //          applyCustomOrDefaultStyle(
    //            context = it,
    //            view = this,
    //            customStyleName =
    //              readCustomStyleName(
    //                StyleUrl.QUESTION_TEXT_VIEW,
    //              ),
    //            defaultStyleResId =
    //              getStyleResIdFromAttribute(it, R.attr.questionnaireQuestionTextStyle),
    //          )
    //        }
    //      },
    //      modifier = Modifier.weight(1f),
    //      update = { it.text = questionLocalizedText },
    //    )
  }
}

@Composable
internal fun Help(
  hintLocalizedText: AnnotatedString?,
  readCustomStyleName: (StyleUrl) -> String?,
  isHelpCardInitiallyOpen: Boolean,
  isHelpButtonVisible: Boolean,
  helpButtonOnClick: (Boolean) -> Unit,
  helpCardLocalizedText: AnnotatedString?,
) {
  var isCardOpen by remember { mutableStateOf(isHelpCardInitiallyOpen) }

  Row(
    modifier = Modifier.padding(top = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    hintLocalizedText?.let {
      Text(
        it,
        style = QuestionnaireTheme.textStyles.subtitleText,
        modifier = Modifier.testTag(HINT_HEADER_TAG),
      )
      //      AndroidView(
      //        modifier = Modifier.weight(0.7f),
      //        factory = {
      //          TextView(it).apply {
      //            id = R.id.hint
      //            movementMethod = LinkMovementMethod.getInstance()
      //            applyCustomOrDefaultStyle(
      //              context = it,
      //              view = this,
      //              customStyleName =
      //                readCustomStyleName(
      //                  StyleUrl.SUBTITLE_TEXT_VIEW,
      //                ),
      //              defaultStyleResId =
      //                getStyleResIdFromAttribute(it, R.attr.questionnaireSubtitleTextStyle),
      //            )
      //          }
      //        },
      //        update = { it.text = hintLocalizedText },
      //      )
    }

    if (isHelpButtonVisible) {
      IconButton(
        onClick = {
          isCardOpen = !isCardOpen
          helpButtonOnClick(isCardOpen)
        },
        modifier =
          Modifier.padding(2.dp)
            .padding(start = 4.dp)
            .testTag(HELP_BUTTON_TAG)
            .weight(0.3f)
            .size(
              width = 24.dp,
              height = 24.dp,
            ),
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Help,
          contentDescription = stringResource(Res.string.help),
          modifier = Modifier.size(48.dp),
        )
      }
    }
  }

  if (isCardOpen) {
    Card(
      modifier = Modifier.padding(top = 16.dp).testTag(HELP_CARD_TAG),
      colors =
        CardDefaults.cardColors()
          .copy(containerColor = QuestionnaireTheme.colorScheme.surfaceVariant),
    ) {
      Column {
        Text(
          text = stringResource(Res.string.help),
          modifier =
            Modifier.padding(horizontal = 16.dp)
              .padding(
                top = 16.dp,
                bottom = 4.dp,
              ),
          style = QuestionnaireTheme.typography.titleSmall,
        )

        helpCardLocalizedText?.let {
          Text(
            it,
            modifier =
              Modifier.testTag(HELP_HEADER_TAG)
                .padding(horizontal = QuestionnaireTheme.dimensions.helpTextMarginHorizontal)
                .padding(bottom = QuestionnaireTheme.dimensions.helpTextMarginBottom),
            style = QuestionnaireTheme.textStyles.helpText,
          )
        }
        //        AndroidView(
        //          factory = {
        //            TextView(it).apply {
        //              id = R.id.helpText
        //              movementMethod = LinkMovementMethod.getInstance()
        //
        //              QuestionItemDefaultStyle()
        //                .applyStyle(
        //                  context,
        //                  this,
        //                  getStyleResIdFromAttribute(it, R.attr.questionnaireHelpTextStyle),
        //                )
        //            }
        //          },
        //          modifier =
        //            Modifier.padding(horizontal =
        // dimensionResource(R.dimen.help_text_margin_horizontal))
        //              .padding(bottom = dimensionResource(R.dimen.help_text_margin_bottom)),
        //          update = { it.text = helpCardLocalizedText },
        //        )
      }
    }
  }
}

const val ERROR_TEXT_AT_HEADER_TEST_TAG = "error-text-at-header"
const val HELP_BUTTON_TAG = "help-button"
const val HELP_CARD_TAG = "help-card-view"
const val HELP_HEADER_TAG = "hint_text"
const val HEADER_TAG = "header-View"
const val HINT_HEADER_TAG = "hint_text"
const val PREFIX_HEADER_TAG = "prefix_text"
const val QUESTION_HEADER_TAG = "question_text"
