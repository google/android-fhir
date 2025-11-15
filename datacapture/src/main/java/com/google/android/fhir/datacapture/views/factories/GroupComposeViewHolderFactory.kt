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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.compose.GroupHeader
import com.google.android.fhir.datacapture.views.compose.MediaItem

/**
 * Compose-based factory for group view holders. This is the Compose equivalent of
 * [GroupViewHolderFactory].
 */
internal object GroupComposeViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemComposeViewHolderDelegate {

      @Composable
      override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
        Column(
          modifier =
            Modifier.fillMaxWidth()
              .padding(
                horizontal = dimensionResource(R.dimen.item_margin_horizontal),
                vertical = dimensionResource(R.dimen.item_margin_vertical),
              ),
        ) {
          GroupHeader(questionnaireViewItem)

          // Display validation error
          val validationResult = questionnaireViewItem.validationResult
          if (validationResult is Invalid) {
            Text(
              modifier =
                Modifier.padding(
                    horizontal = dimensionResource(R.dimen.error_text_margin_horizontal),
                    vertical = dimensionResource(R.dimen.error_text_margin_vertical),
                  )
                  .testTag(GROUP_ERROR_TEXT_TAG),
              text = validationResult.getSingleStringValidationMessage(),
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall,
            )
          }

          // Display item media if present
          questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }
        }
      }
    }
}

const val GROUP_ERROR_TEXT_TAG = "groupErrorText"
