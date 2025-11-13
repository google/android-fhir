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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun QuestionnaireScreen(
  viewModel: QuestionnaireViewModel,
  matchersProvider: QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider,
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
  matchersProvider: QuestionnaireFragment.QuestionnaireItemViewHolderFactoryMatchersProvider,
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
      text = stringResource(R.string.questionnaire_review_mode_title),
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.weight(1f),
    )

    if (showEditButton) {
      OutlinedButton(onClick = onEditClick) {
        Icon(
          imageVector = Icons.Outlined.Edit,
          contentDescription = "Edit",
        )
        Text(text = stringResource(R.string.edit_button_text))
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
      color = MaterialTheme.colorScheme.surface,
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
                text = cancelState.labelText ?: stringResource(R.string.cancel_questionnaire),
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
                      ?: stringResource(R.string.button_pagination_previous),
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
                  text = nextState.labelText ?: stringResource(R.string.button_pagination_next),
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
                  text = reviewState.labelText ?: stringResource(R.string.button_review),
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
                  text = submitState.labelText ?: stringResource(R.string.submit_questionnaire),
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

/** Calculates the progress percentage from given [count] and [totalCount] values. */
private fun calculateProgressPercentage(count: Int, totalCount: Int): Int {
  return if (totalCount == 0) 0 else (count * 100 / totalCount)
}
