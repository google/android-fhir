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
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.google.android.material.internal.CheckableImageButton
import com.google.android.material.textfield.TextInputLayout

/** Clicks end or start icon in TextInputLayout widget. */
fun clickIcon(isEndIcon: Boolean): ViewAction {
  return object : ViewAction {
    override fun getConstraints(): org.hamcrest.Matcher<View> {
      return ViewMatchers.isAssignableFrom(TextInputLayout::class.java)
    }

    override fun getDescription(): String {
      return "Icon Click"
    }

    override fun perform(uiController: UiController?, view: View?) {
      val item = view as TextInputLayout
      val iconView: CheckableImageButton =
        item.findViewById(
          if (isEndIcon) com.google.android.material.R.id.text_input_end_icon
          else com.google.android.material.R.id.text_input_start_icon
        )
      iconView.performClick()
      uiController!!.loopMainThreadForAtLeast(1000)
    }
  }
}
