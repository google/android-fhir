/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.datacapture.utilities

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.common.truth.StringSubject
import com.google.common.truth.Truth
import org.hl7.fhir.r4.model.Coding

class EspressoUtilities

fun clickOnText(text: String) {
  Espresso.onView(ViewMatchers.withText(text)).perform(ViewActions.click())
}

fun clickOnTextInDialog(text: String) {
  Espresso.onView(ViewMatchers.withText(text))
    .inRoot(RootMatchers.isDialog())
    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    .perform(ViewActions.click())
}

fun assertQuestionnaireResponseAtIndex(
  questionnaireItemViewItem: QuestionnaireItemViewItem,
  index: Int
): StringSubject =
  Truth.assertThat(
    (questionnaireItemViewItem.questionnaireResponseItem.answer[index].value as Coding).display
  )
