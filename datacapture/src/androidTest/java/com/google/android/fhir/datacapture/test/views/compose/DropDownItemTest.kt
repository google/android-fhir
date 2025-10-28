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

package com.google.android.fhir.datacapture.test.views.compose

import android.content.Context
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.compose.DROP_DOWN_TEXT_FIELD_TAG
import com.google.android.fhir.datacapture.views.compose.DropDownItem
import com.google.android.fhir.datacapture.views.factories.DropDownAnswerOption
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DropDownItemTest {

  @get:Rule val composeTestRule = createComposeRule()

  val context: Context = ApplicationProvider.getApplicationContext()

  @Test
  fun shouldShowLeadingIconForDropDownOptions() {
    val testDropDownAnswerOption =
      DropDownAnswerOption(
        answerId = "",
        answerOptionString = "Test Option",
        answerOptionImage = ContextCompat.getDrawable(context, R.drawable.ic_image_file),
      )

    composeTestRule.setContent {
      DropDownItem(
        modifier = Modifier,
        enabled = true,
        options = listOf(testDropDownAnswerOption),
        onDropDownAnswerOptionSelected = {},
      )
    }

    composeTestRule.onNodeWithTag(DROP_DOWN_TEXT_FIELD_TAG).performClick()
    composeTestRule
      .onNodeWithContentDescription(testDropDownAnswerOption.answerOptionString)
      .assertIsDisplayed()
  }
}
