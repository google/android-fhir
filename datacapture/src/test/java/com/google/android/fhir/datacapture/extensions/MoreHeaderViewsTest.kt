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

package com.google.android.fhir.datacapture.extensions

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.core.text.toSpanned
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreHeaderViewsTest {

  @Test
  fun `appendOptionalText appends the optional text at the end of the question text`() {
    val textView = TextView(RuntimeEnvironment.getApplication().applicationContext)
    appendOptionalText(textView, "question text".toSpanned())

    assertThat(textView.text.toString()).isEqualTo("question text (optional)")
    assertThat(textView.visibility).isEqualTo(View.VISIBLE)
  }
}
