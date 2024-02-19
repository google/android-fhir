/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.datacapture.contrib.views.locationwidget

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isVisible
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationWidgetViewHolderFactoryInstrumentedTest {
  private lateinit var context: Context
  private lateinit var parent: ViewGroup

  @Before
  fun setUp() {
    context =
      ContextThemeWrapper(
        InstrumentationRegistry.getInstrumentation().targetContext,
        com.google.android.fhir.datacapture.R.style.Theme_Questionnaire,
      )
    parent = FrameLayout(context)
  }

  @Test
  fun createShouldReturnViewHolderWithLocationWidgetButton() {
    val viewHolder = LocationWidgetViewHolderFactory.create(parent)
    assertThat(viewHolder.itemView.findViewById<Button>(R.id.gps_widget_button).isVisible).isTrue()
    assertThat(viewHolder.itemView.findViewById<Button>(R.id.gps_widget_button).text.toString())
      .isEqualTo(context.getString(R.string.record_gps_location))
  }
}
