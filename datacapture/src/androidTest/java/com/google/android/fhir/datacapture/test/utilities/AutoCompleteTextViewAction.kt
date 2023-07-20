/*
 * Copyright 2022 Google LLC
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

import android.view.View
import android.widget.AutoCompleteTextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

/** Show Drop Down view for AutoCompleteTextView widget. */
fun showDropDown(): ViewAction {
  return object : ViewAction {
    override fun getConstraints(): Matcher<View> =
      allOf(isEnabled(), isAssignableFrom(AutoCompleteTextView::class.java))

    override fun getDescription(): String {
      return "show DropDown"
    }

    override fun perform(uiController: UiController, view: View?) {
      val autoCompleteTextView = view as AutoCompleteTextView
      autoCompleteTextView.showDropDown()
      // Avoid test flakiness with a delay. See https://github.com/google/android-fhir/issues/1323.
      uiController.loopMainThreadForAtLeast(1000)
    }
  }
}
