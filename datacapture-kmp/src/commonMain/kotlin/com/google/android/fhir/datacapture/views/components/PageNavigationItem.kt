/*
 * Copyright 2023-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.components

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.button_pagination_next
import android_fhir.datacapture_kmp.generated.resources.button_pagination_previous
import android_fhir.datacapture_kmp.generated.resources.button_review
import android_fhir.datacapture_kmp.generated.resources.cancel_questionnaire
import android_fhir.datacapture_kmp.generated.resources.submit_questionnaire
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.QuestionnaireNavigationUIState
import com.google.android.fhir.datacapture.QuestionnaireNavigationViewUIState
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import org.jetbrains.compose.resources.stringResource

internal const val PAGE_NAVIGATION_BUTTON_TAG = "page_navigation_button"

@Composable
internal fun QuestionnaireBottomNavigation(
  pageNavigationUIState: QuestionnaireNavigationUIState,
  modifier: Modifier = Modifier,
) {
  val navigationUIState = remember(pageNavigationUIState) { pageNavigationUIState }

  Column(modifier = modifier.fillMaxWidth()) {
    HorizontalDivider(color = QuestionnaireTheme.colorScheme.onSurface.copy(alpha = 0.15f))

    Surface(
      color = QuestionnaireTheme.colorScheme.surface,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Cancel button (left-aligned)
        if (navigationUIState.navCancel is QuestionnaireNavigationViewUIState.Enabled) {
          val cancelNavigationViewState =
            remember(navigationUIState.navCancel) { navigationUIState.navCancel }
          TextButton(
            onClick = cancelNavigationViewState.onClickAction,
            modifier = modifier,
          ) {
            Text(
              text = cancelNavigationViewState.labelText?.takeIf { it.isNotBlank() }
                  ?: stringResource(Res.string.cancel_questionnaire),
            )
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right-aligned buttons
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          PageNavigationButton(
            navigationViewState = navigationUIState.navPrevious,
            defaultText = stringResource(Res.string.button_pagination_previous),
          )

          PageNavigationButton(
            navigationViewState = navigationUIState.navNext,
            defaultText = stringResource(Res.string.button_pagination_next),
          )

          PageNavigationButton(
            navigationViewState = navigationUIState.navReview,
            defaultText = stringResource(Res.string.button_review),
          )

          PageNavigationButton(
            navigationViewState = navigationUIState.navSubmit,
            defaultText = stringResource(Res.string.submit_questionnaire),
          )
        }
      }
    }
  }
}

/** Individual navigation button that handles visibility and click actions. */
@Composable
private fun PageNavigationButton(
  navigationViewState: QuestionnaireNavigationViewUIState,
  defaultText: String,
  modifier: Modifier = Modifier,
) {
  if (navigationViewState is QuestionnaireNavigationViewUIState.Enabled) {
    val buttonText = navigationViewState.labelText?.takeIf { it.isNotBlank() } ?: defaultText
    Button(
      onClick = navigationViewState.onClickAction,
      modifier = modifier.testTag(PAGE_NAVIGATION_BUTTON_TAG),
    ) {
      Text(
        text = buttonText,
      )
    }
  }
}
