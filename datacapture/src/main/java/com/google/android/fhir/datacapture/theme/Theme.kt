/*
 * Copyright 2021-2025 Google LLC
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

package com.google.android.fhir.datacapture.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Questionnaire theme dimensions matching the XML theme values. */
@Immutable
data class QuestionnaireDimensions(
  // Widget related
  val itemMarginHorizontal: Dp = 16.dp,
  val itemMarginVertical: Dp = 8.dp,

  // Action button
  val actionButtonMarginHorizontal: Dp = 16.dp,
  val actionButtonMarginVertical: Dp = 16.dp,

  // Title
  val titleLayoutMarginHorizontal: Dp = 16.dp,
  val titleMarginEnd: Dp = 2.dp,

  // Header
  val errorTextAtHeaderMarginTop: Dp = 4.dp,
  val headerPaddingBottom: Dp = 4.dp,
  val headerMarginBottom: Dp = 16.dp,
  val helpButtonMarginStart: Dp = 4.dp,
  val helpButtonHeight: Dp = 24.dp,
  val helpButtonWidth: Dp = 24.dp,
  val helpIconPadding: Dp = 2.dp,
  val helpCardMarginTop: Dp = 16.dp,
  val helpContainerMarginTop: Dp = 4.dp,
  val helpHeaderMarginBottom: Dp = 4.dp,
  val helpHeaderMarginHorizontal: Dp = 16.dp,
  val helpHeaderMarginTop: Dp = 16.dp,
  val helpMarginBottom: Dp = 16.dp,
  val helpTextMarginBottom: Dp = 16.dp,
  val helpTextMarginHorizontal: Dp = 16.dp,
  val prefixPaddingEnd: Dp = 5.dp,

  // Error text
  val errorTextMarginHorizontal: Dp = 16.dp,
  val errorTextMarginVertical: Dp = 16.dp,

  // Attachment
  val attachmentActionButtonIconSize: Dp = 24.dp,
  val attachmentActionButtonMarginEnd: Dp = 8.dp,
  val attachmentActionButtonPaddingHorizontal: Dp = 16.dp,
  val attachmentDividerHeight: Dp = 1.dp,
  val attachmentDividerMarginTop: Dp = 16.dp,
  val attachmentErrorMarginTop: Dp = 4.dp,
  val attachmentPreviewDividerMarginTop: Dp = 8.dp,
  val attachmentPreviewFileIconMargin: Dp = 16.dp,
  val attachmentPreviewMarginTop: Dp = 8.dp,
  val attachmentPreviewPhotoHeight: Dp = 72.dp,
  val attachmentPreviewPhotoWidth: Dp = 72.dp,
  val attachmentPreviewTitleMarginHorizontal: Dp = 8.dp,
  val attachmentUploadedLabelMarginBottom: Dp = 4.dp,
  val attachmentUploadedLabelMarginTop: Dp = 16.dp,

  // Auto Complete
  val autoCompleteChipMargin: Dp = 10.dp,
  val autoCompleteChipMarginBottom: Dp = 50.dp,

  // Barcode
  val barcodeQuestionMarginEnd: Dp = 8.dp,
  val barcodeQuestionMarginStart: Dp = 8.dp,

  // Option item
  val optionItemAfterTextPadding: Dp = 24.dp,
  val optionItemBetweenTextAndIconPadding: Dp = 16.dp,
  val optionItemMarginHorizontal: Dp = 16.dp,
  val optionItemMarginVertical: Dp = 16.dp,
  val optionItemPadding: Dp = 16.dp,

  // Date picker and Date time picker
  val datePickerAndTimePickerGap: Dp = 16.dp,

  // Dialog
  val dialogConfirmationButtonPadding: Dp = 8.dp,
  val dialogContentScrollMarginVertical: Dp = 8.dp,
  val dialogOptionScrollMarginTop: Dp = 16.dp,
  val dialogPadding: Dp = 24.dp,
  val dialogSubtitleMarginTop: Dp = 16.dp,
  val dialogTitleMarginBottom: Dp = 4.dp,

  // Dropdown
  val dropDownPadding: Dp = 16.dp,
  val dropDownClearIconMarginEnd: Dp = 38.dp,
  val dropDownClearIconMarginTop: Dp = 4.dp,

  // Item Answer Media
  val itemAnswerMediaImageSize: Dp = 48.dp,
  val choiceButtonImage: Dp = 48.dp,

  // Item Media
  val itemMediaImageMarginHorizontal: Dp = 16.dp,
  val itemMediaImageMarginVertical: Dp = 8.dp,
  val itemMediaImageMaxHeight: Dp = 200.dp,
  val itemMediaImageMaxWidth: Dp = 200.dp,
  val itemMediaImagePreviewMaxWidth: Dp = 200.dp,
  val itemMediaImagePreviewMaxHeight: Dp = 200.dp,

  // Simple question
  val simpleQuestionViewAnswerMarginTop: Dp = 8.dp,
  val simpleQuestionViewDividerMarginTop: Dp = 8.dp,

  // Radio Button
  val paddingBetweenTextAndIcon: Dp = 16.dp,
  val paddingAfterText: Dp = 24.dp,

  // Icon
  val iconDrawablePadding: Dp = 16.dp,
  val iconInset: Dp = 16.dp,

  // Bottom Container
  val bottomContainerPaddingVertical: Dp = 8.dp,

  // Error Icon
  val errorIconWidth: Dp = 20.dp,
  val errorIconHeight: Dp = 20.dp,
  val errorIconMarginEnd: Dp = 4.dp,
)

/** Questionnaire theme shapes matching the XML theme values. */
@Immutable
data class QuestionnaireShapes(
  val attachmentPreviewPhoto: RoundedCornerShape = RoundedCornerShape(8.dp),
  val addRepeatedGroupButton: RoundedCornerShape = RoundedCornerShape(4.dp),
  val deleteRepeatedGroupButton: RoundedCornerShape = RoundedCornerShape(4.dp),
)

/** Questionnaire theme text styles matching the XML theme values. */
@Immutable
data class QuestionnaireTextStyles(
  val groupTypeQuestionText: TextStyle,
  val questionText: TextStyle,
  val reviewModeQuestionText: TextStyle,
  val subtitleText: TextStyle,
  val helpHeaderText: TextStyle,
  val helpText: TextStyle,
  val reviewModeAnswerText: TextStyle,
  val reviewModeNotAnsweredText: TextStyle,
  val dropDownText: TextStyle,
  val dropDownSelectedText: TextStyle,
  val errorText: TextStyle,
  val attachmentUploadedLabel: TextStyle,
  val attachmentPreviewTitle: TextStyle,
  val dialogTitle: TextStyle,
  val validationDialogTitle: TextStyle,
  val validationDialogBody: TextStyle,
  val titleText: TextStyle,
)

/** Questionnaire theme alpha values. */
@Immutable
data class QuestionnaireAlphas(
  val reviewModeAnswer: Float = 0.5f,
  val reviewModeDivider: Float = 0.5f,
)

/** Local composition providers for Questionnaire theme. */
val LocalQuestionnaireDimensions = staticCompositionLocalOf { QuestionnaireDimensions() }
val LocalQuestionnaireShapes = staticCompositionLocalOf { QuestionnaireShapes() }
val LocalQuestionnaireTextStyles =
  staticCompositionLocalOf<QuestionnaireTextStyles> { error("No QuestionnaireTextStyles provided") }
val LocalQuestionnaireAlphas = staticCompositionLocalOf { QuestionnaireAlphas() }

/**
 * The default theme applied to the questionnaire rendered using Compose.
 *
 * This theme is a Compose migration of the XML Theme.Questionnaire theme. It provides Material 3
 * styling with customizable attributes for questionnaire components.
 *
 * To override the theme attributes, you can provide custom values through the composition locals:
 * - LocalQuestionnaireDimensions
 * - LocalQuestionnaireShapes
 * - LocalQuestionnaireTextStyles
 * - LocalQuestionnaireAlphas
 *
 * Example:
 * ```
 * QuestionnaireTheme {
 *   // Your questionnaire composables
 * }
 * ```
 *
 * @param darkTheme Whether to use dark theme colors. Defaults to system theme.
 * @param dimensions Custom dimensions for the questionnaire. Defaults to QuestionnaireDimensions().
 * @param shapes Custom shapes for the questionnaire. Defaults to QuestionnaireShapes().
 * @param alphas Custom alpha values for the questionnaire. Defaults to QuestionnaireAlphas().
 * @param content The composable content to be themed.
 */
@Composable
fun QuestionnaireTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dimensions: QuestionnaireDimensions = QuestionnaireDimensions(),
  shapes: QuestionnaireShapes = QuestionnaireShapes(),
  alphas: QuestionnaireAlphas = QuestionnaireAlphas(),
  content: @Composable () -> Unit,
) {
  val colorScheme =
    if (darkTheme) {
      darkColorScheme()
    } else {
      lightColorScheme()
    }

  val typography = Typography()

  val questionnaireTextStyles =
    QuestionnaireTextStyles(
      groupTypeQuestionText = typography.titleMedium,
      questionText = typography.titleMedium,
      reviewModeQuestionText = typography.titleMedium,
      subtitleText = typography.bodyMedium,
      helpHeaderText = typography.titleSmall,
      helpText = typography.bodyMedium,
      reviewModeAnswerText = typography.bodyMedium,
      reviewModeNotAnsweredText = typography.bodyMedium,
      dropDownText = typography.bodyLarge,
      dropDownSelectedText = typography.bodyLarge,
      errorText = typography.bodySmall,
      attachmentUploadedLabel = typography.titleSmall,
      attachmentPreviewTitle = typography.bodyLarge,
      dialogTitle = typography.titleMedium,
      validationDialogTitle = typography.headlineSmall,
      validationDialogBody = typography.bodyMedium,
      titleText = typography.titleLarge,
    )

  val materialShapes =
    Shapes(
      small = RoundedCornerShape(4.dp),
      medium = RoundedCornerShape(8.dp),
      large = RoundedCornerShape(16.dp),
    )

  MaterialTheme(
    colorScheme = colorScheme,
    typography = typography,
    shapes = materialShapes,
  ) {
    CompositionLocalProvider(
      LocalQuestionnaireDimensions provides dimensions,
      LocalQuestionnaireShapes provides shapes,
      LocalQuestionnaireTextStyles provides questionnaireTextStyles,
      LocalQuestionnaireAlphas provides alphas,
    ) {
      content()
    }
  }
}

/** Object to access the current questionnaire theme values. */
object QuestionnaireTheme {
  /**
   * Retrieves the current [QuestionnaireDimensions] at the call site's position in the hierarchy.
   */
  val dimensions: QuestionnaireDimensions
    @Composable get() = LocalQuestionnaireDimensions.current

  /** Retrieves the current [QuestionnaireShapes] at the call site's position in the hierarchy. */
  val shapes: QuestionnaireShapes
    @Composable get() = LocalQuestionnaireShapes.current

  /**
   * Retrieves the current [QuestionnaireTextStyles] at the call site's position in the hierarchy.
   */
  val textStyles: QuestionnaireTextStyles
    @Composable get() = LocalQuestionnaireTextStyles.current

  /** Retrieves the current [QuestionnaireAlphas] at the call site's position in the hierarchy. */
  val alphas: QuestionnaireAlphas
    @Composable get() = LocalQuestionnaireAlphas.current

  /**
   * Retrieves the current Material3 [androidx.compose.material3.ColorScheme] from MaterialTheme.
   */
  val colorScheme
    @Composable get() = MaterialTheme.colorScheme

  /** Retrieves the current Material3 [Typography] from MaterialTheme. */
  val typography
    @Composable get() = MaterialTheme.typography
}
