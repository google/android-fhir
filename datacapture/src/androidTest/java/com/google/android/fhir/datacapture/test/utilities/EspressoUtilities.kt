/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.datacapture.test.utilities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.QuestionnaireResponse

fun clickOnText(text: String) {
  onView(withText(text)).perform(click())
}

fun clickOnTextInDialog(text: String) {
  onView(withText(text)).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
}

fun endIconClickInTextInputLayout(id: Int) {
  onView(withId(id)).perform(clickIcon(isEndIcon = true))
}

fun assertQuestionnaireResponseAtIndex(
  answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>,
  vararg expectedStrings: String
) {
  for ((index, expectedString) in expectedStrings.withIndex()) {
    assertThat((answers[index].value as Coding).display).isEqualTo(expectedString)
  }
}
