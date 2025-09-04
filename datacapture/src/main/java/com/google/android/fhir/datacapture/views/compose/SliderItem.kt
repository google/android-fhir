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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun SliderItem(
  startPosition: Float,
  steps: Int,
  valueRange: ClosedFloatingPointRange<Float>,
  enabled: Boolean,
  onPositionChanged: (Float) -> Unit,
) {
  var sliderPosition by remember(startPosition) { mutableFloatStateOf(startPosition) }
  val interactionSource =
    remember(startPosition, steps, valueRange, enabled) { MutableInteractionSource() }
  val isDragged by interactionSource.collectIsDraggedAsState()

  Box {
    if (isDragged) {
      Surface(
        modifier =
          Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val sliderWidth = constraints.maxWidth
            val sliderValueNormalized =
              (sliderPosition - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val textX =
              (sliderWidth * sliderValueNormalized - placeable.width / 2).coerceIn(
                0f,
                (sliderWidth - placeable.width).toFloat(),
              )
            val bottomPadding = 16.dp.toPx().roundToInt()
            layout(placeable.width, placeable.height - bottomPadding) {
              placeable.placeRelative(textX.roundToInt(), -bottomPadding)
            }
          },
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primary,
      ) {
        Text(
          text = sliderPosition.roundToInt().toString(),
          color = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
      }
    }

    Slider(
      value = sliderPosition,
      onValueChange = { sliderPosition = it },
      onValueChangeFinished = { onPositionChanged(sliderPosition) },
      steps = steps,
      valueRange = valueRange,
      interactionSource = interactionSource,
      modifier = Modifier.padding(top = 16.dp).fillMaxWidth().testTag(SLIDER_TAG),
      enabled = enabled,
    )
  }
}

const val SLIDER_TAG = "slider"
