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

package com.google.android.fhir.datacapture.views.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@Composable
fun SliderItem(
  startPosition: Float,
  steps: Int,
  valueRange: ClosedFloatingPointRange<Float>,
  enabled: Boolean,
  onPositionChanged: (Float) -> Unit,
) {
  var sliderPosition by remember { mutableFloatStateOf(startPosition) }

  Column {
    Text(text = sliderPosition.roundToInt().toString())

    Slider(
      value = sliderPosition,
      onValueChange = { sliderPosition = it },
      onValueChangeFinished = { onPositionChanged(sliderPosition) },
      steps = steps,
      valueRange = valueRange,
      modifier = Modifier.fillMaxWidth(),
      enabled = enabled,
    )
  }
}
