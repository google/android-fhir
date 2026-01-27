/*
 * Copyright 2023-2025 Google LLC
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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.add_repeated_group_item
import android_fhir.datacapture_kmp.generated.resources.button_pagination_next
import android_fhir.datacapture_kmp.generated.resources.button_pagination_previous
import android_fhir.datacapture_kmp.generated.resources.button_review
import android_fhir.datacapture_kmp.generated.resources.cancel_questionnaire
import android_fhir.datacapture_kmp.generated.resources.delete
import android_fhir.datacapture_kmp.generated.resources.edit_button_text
import android_fhir.datacapture_kmp.generated.resources.questionnaire_review_mode_title
import android_fhir.datacapture_kmp.generated.resources.repeated_group_title
import android_fhir.datacapture_kmp.generated.resources.submit_questionnaire
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.fhir.model.r4.QuestionnaireResponse
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * A JSON encoded string extra for a questionnaire. This should only be used for questionnaires with
 * size at most 512KB. For large questionnaires, use [EXTRA_QUESTIONNAIRE_JSON_URI].
 *
 * This is required unless [EXTRA_QUESTIONNAIRE_JSON_URI] is provided.
 *
 * If this and [EXTRA_QUESTIONNAIRE_JSON_URI] are provided, [EXTRA_QUESTIONNAIRE_JSON_URI] takes
 * precedence.
 */
internal const val EXTRA_QUESTIONNAIRE_JSON_STRING = "questionnaire"

/**
 * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire.
 *
 * This is required unless [EXTRA_QUESTIONNAIRE_JSON_STRING] is provided.
 *
 * If this and [EXTRA_QUESTIONNAIRE_JSON_STRING] are provided, this extra takes precedence.
 */
internal const val EXTRA_QUESTIONNAIRE_JSON_URI = "questionnaire-uri"

/**
 * A JSON encoded string extra for a prefilled questionnaire response. This should only be used for
 * questionnaire response with size at most 512KB. For large questionnaire response, use
 * [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI].
 *
 * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] are provided,
 * [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI] takes precedence.
 */
internal const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING = "questionnaire-response"

/** A map of launchContext name and JSON encoded strings extra for each questionnaire context. */
internal const val EXTRA_QUESTIONNAIRE_LAUNCH_CONTEXT_MAP = "questionnaire-launch-contexts"

/**
 * A [URI][android.net.Uri] extra for streaming a JSON encoded questionnaire response.
 *
 * If this and [EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING] are provided, this extra takes precedence.
 */
internal const val EXTRA_QUESTIONNAIRE_RESPONSE_JSON_URI = "questionnaire-response-uri"

/**
 * A [Boolean] extra to control if a review page is shown. By default it will be shown at the end of
 * the questionnaire.
 */
internal const val EXTRA_ENABLE_REVIEW_PAGE = "enable-review-page"

/**
 * A [Boolean] extra to control if the review page is to be opened first. This has no effect if
 * review page is not enabled.
 */
internal const val EXTRA_SHOW_REVIEW_PAGE_FIRST = "show-review-page-first"

/**
 * An [Boolean] extra to control if the questionnaire is read-only. If review page and read-only are
 * both enabled, read-only will take precedence.
 */
internal const val EXTRA_READ_ONLY = "read-only"

internal const val EXTRA_MATCHERS_FACTORY = "matcher_factory_class"

const val SUBMIT_REQUEST_KEY = "submit-request-key"

const val CANCEL_REQUEST_KEY = "cancel-request-key"

/** A [Boolean] extra to show or hide the Submit button in the questionnaire. Default is true. */
internal const val EXTRA_SHOW_SUBMIT_BUTTON = "show-submit-button"

/** A [Boolean] extra to show or hide the Cancel button in the questionnaire. Default is false. */
internal const val EXTRA_SHOW_CANCEL_BUTTON = "show-cancel-button"

internal const val EXTRA_SHOW_OPTIONAL_TEXT = "show-optional-text"

internal const val EXTRA_SHOW_ASTERISK_TEXT = "show-asterisk-text"

internal const val EXTRA_SHOW_REQUIRED_TEXT = "show-required-text"

internal const val EXTRA_SUBMIT_BUTTON_TEXT = "submit-button-text"

internal const val EXTRA_SHOW_NAVIGATION_IN_DEFAULT_LONG_SCROLL =
  "show-navigation-in-default-long-scroll"

/**
 * A [Boolean] extra to show or hide the Submit anyway button in the questionnaire. Default is true.
 */
internal const val EXTRA_SHOW_SUBMIT_ANYWAY_BUTTON = "show-submit-anyway-button"

@Composable
internal fun QuestionnaireScreen(
  viewModel: QuestionnaireViewModel,
  matchersProvider: QuestionnaireItemViewHolderFactoryMatchersProvider,
) {
  val questionnaireState by viewModel.questionnaireStateFlow.collectAsStateWithLifecycle()

  Box(modifier = Modifier.fillMaxSize()) {
    when (val displayMode = questionnaireState.displayMode) {
      is DisplayMode.InitMode -> {
        // Empty state - nothing to show
      }
      is DisplayMode.EditMode -> {
        EditModeContent(
          state = questionnaireState,
          displayMode = displayMode,
          matchersProvider = matchersProvider,
          bottomNavItem = questionnaireState.bottomNavItem,
        )
      }
      is DisplayMode.ReviewMode -> {
        ReviewModeContent(
          state = questionnaireState,
          displayMode = displayMode,
          onEditClick = { viewModel.setReviewMode(false) },
          bottomNavItem = questionnaireState.bottomNavItem,
        )
      }
    }
  }
}

@Composable
private fun EditModeContent(
  state: QuestionnaireState,
  displayMode: DisplayMode.EditMode,
  matchersProvider: QuestionnaireItemViewHolderFactoryMatchersProvider,
  bottomNavItem: QuestionnaireAdapterItem.Navigation?,
) {
  var progress by remember { mutableIntStateOf(0) }

  LaunchedEffect(displayMode) {
    if (displayMode.pagination.isPaginated) {
      progress =
        calculateProgressPercentage(
          count = displayMode.pagination.currentPageIndex + 1,
          totalCount = displayMode.pagination.pages.size,
        )
    }
  }
  Scaffold(
    topBar = {
      LinearProgressIndicator(
        progress = { progress / 100f },
        modifier = Modifier.height(4.dp).fillMaxWidth(),
      )
    },
    bottomBar = {
      if (bottomNavItem != null) {
        QuestionnaireBottomNavigation(
          navigationState = bottomNavItem.questionnaireNavigationUIState,
        )
      }
    },
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      QuestionnaireEditList(
        items = state.items,
        displayMode = displayMode,
        questionnaireItemViewHolderMatchers = matchersProvider.get(),
        onUpdateProgressIndicator = { currentPage, totalCount ->
          progress = calculateProgressPercentage(count = (currentPage + 1), totalCount = totalCount)
        },
      )
    }
  }
}

@Composable
private fun ReviewModeContent(
  state: QuestionnaireState,
  displayMode: DisplayMode.ReviewMode,
  onEditClick: () -> Unit,
  bottomNavItem: QuestionnaireAdapterItem.Navigation?,
) {
  Scaffold(
    topBar = {
      QuestionnaireTitleBar(
        showEditButton = displayMode.showEditButton,
        onEditClick = onEditClick,
        modifier = Modifier.fillMaxWidth(),
      )
    },
    bottomBar = {
      if (bottomNavItem != null) {
        QuestionnaireBottomNavigation(
          navigationState = bottomNavItem.questionnaireNavigationUIState,
        )
      }
    },
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
      QuestionnaireReviewList(items = state.items)
    }
  }
}

@Composable
fun QuestionnaireTitleBar(
  showEditButton: Boolean,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(Res.string.questionnaire_review_mode_title),
      style = QuestionnaireTheme.typography.titleLarge,
      modifier = Modifier.weight(1f),
    )

    if (showEditButton) {
      OutlinedButton(onClick = onEditClick) {
        Icon(
          imageVector = Icons.Outlined.Edit,
          contentDescription = "Edit",
        )
        Text(text = stringResource(Res.string.edit_button_text))
      }
    }
  }
}

@Composable
fun QuestionnaireBottomNavigation(
  navigationState: QuestionnaireNavigationUIState,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    HorizontalDivider()

    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = QuestionnaireTheme.colorScheme.surface,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Cancel button (start)
        when (val cancelState = navigationState.navCancel) {
          is QuestionnaireNavigationViewUIState.Enabled -> {
            OutlinedButton(
              onClick = cancelState.onClickAction,
            ) {
              Text(
                text = cancelState.labelText ?: stringResource(Res.string.cancel_questionnaire),
              )
            }
          }
          QuestionnaireNavigationViewUIState.Hidden -> {
            /* Hidden */
          }
        }

        // Spacer to push navigation buttons to the end
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Previous button
          when (val previousState = navigationState.navPrevious) {
            is QuestionnaireNavigationViewUIState.Enabled -> {
              OutlinedButton(
                onClick = previousState.onClickAction,
              ) {
                Text(
                  text = previousState.labelText
                      ?: stringResource(Res.string.button_pagination_previous),
                )
              }
            }
            QuestionnaireNavigationViewUIState.Hidden -> {
              /* Hidden */
            }
          }

          // Next button
          when (val nextState = navigationState.navNext) {
            is QuestionnaireNavigationViewUIState.Enabled -> {
              Button(
                onClick = nextState.onClickAction,
              ) {
                Text(
                  text = nextState.labelText ?: stringResource(Res.string.button_pagination_next),
                )
              }
            }
            QuestionnaireNavigationViewUIState.Hidden -> {
              /* Hidden */
            }
          }

          // Review button
          when (val reviewState = navigationState.navReview) {
            is QuestionnaireNavigationViewUIState.Enabled -> {
              Button(
                onClick = reviewState.onClickAction,
              ) {
                Text(
                  text = reviewState.labelText ?: stringResource(Res.string.button_review),
                )
              }
            }
            QuestionnaireNavigationViewUIState.Hidden -> {
              /* Hidden */
            }
          }

          // Submit button
          when (val submitState = navigationState.navSubmit) {
            is QuestionnaireNavigationViewUIState.Enabled -> {
              Button(
                onClick = submitState.onClickAction,
              ) {
                Text(
                  text = submitState.labelText ?: stringResource(Res.string.submit_questionnaire),
                )
              }
            }
            QuestionnaireNavigationViewUIState.Hidden -> {
              /* Hidden */
            }
          }
        }
      }
    }
  }
}

@Composable
internal fun RepeatedGroupHeaderItem(
  repeatedGroupHeader: QuestionnaireAdapterItem.RepeatedGroupHeader,
) {
  Row(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text =
        stringResource(
          Res.string.repeated_group_title,
          "${repeatedGroupHeader.index + 1}",
          repeatedGroupHeader.title,
        ),
      style = QuestionnaireTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.weight(1f),
    )

    OutlinedButton(
      onClick = repeatedGroupHeader.onDeleteClicked,
    ) {
      Text(text = stringResource(Res.string.delete))
    }
  }
}

@Composable
internal fun RepeatedGroupAddButtonItem(
  addButtonItem: QuestionnaireAdapterItem.RepeatedGroupAddButton,
) {
  val scope = rememberCoroutineScope()

  if (addButtonItem.item.questionnaireItem.repeats?.value == true) {
    Box(
      modifier = Modifier.padding(vertical = 8.dp),
      contentAlignment = Alignment.Center,
    ) {
      Button(
        onClick = {
          scope.launch {
            addButtonItem.item.addAnswer(
              QuestionnaireResponse.Item.Answer(),
            )
          }
        },
        enabled = addButtonItem.item.questionnaireItem.readOnly?.value != true,
      ) {
        Text(
          text =
            stringResource(
              Res.string.add_repeated_group_item,
              // TODO addButtonItem.item.questionText ?: "",
            ),
        )
      }
    }
  }
}

/** Calculates the progress percentage from given [count] and [totalCount] values. */
private fun calculateProgressPercentage(count: Int, totalCount: Int): Int {
  return if (totalCount == 0) 0 else (count * 100 / totalCount)
}
