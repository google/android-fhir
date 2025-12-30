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

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.android.fhir.datacapture.R

@Composable
internal fun ChoiceCheckbox(
  label: AnnotatedString,
  checked: Boolean,
  enabled: Boolean,
  modifier: Modifier = Modifier,
  image: Drawable? = null,
  onCheckedChange: (Boolean) -> Unit,
) {
  val backgroundColor =
    if (checked) {
      MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
      MaterialTheme.colorScheme.surface
    }

  val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
  val textColor =
    if (checked) {
      MaterialTheme.colorScheme.primary
    } else {
      MaterialTheme.colorScheme.onSurface
    }

  val shape =
    if (checked) {
      RoundedCornerShape(4.dp)
    } else {
      RoundedCornerShape(8.dp)
    }

  Row(
    modifier =
      modifier
        .clip(shape)
        .background(backgroundColor)
        .then(
          if (!checked) {
            Modifier.border(1.dp, borderColor, shape)
          } else {
            Modifier
          },
        )
        .toggleable(
          value = checked,
          enabled = enabled,
          role = Role.Checkbox,
          onValueChange = onCheckedChange,
        )
        .padding(
          start = dimensionResource(R.dimen.option_item_between_text_and_icon_padding),
          end = dimensionResource(R.dimen.option_item_after_text_padding),
          top = dimensionResource(R.dimen.option_item_padding),
          bottom = dimensionResource(R.dimen.option_item_padding),
        ),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = null,
      enabled = enabled,
      colors =
        CheckboxDefaults.colors(
          checkedColor = MaterialTheme.colorScheme.primary,
          uncheckedColor = MaterialTheme.colorScheme.onSurface,
          checkmarkColor = MaterialTheme.colorScheme.surface,
        ),
    )
    // Display image
    image?.let { drawable ->
      Spacer(modifier = Modifier.width(8.dp))
      Icon(
        bitmap = drawable.toBitmap().asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.testTag(CHOICE_CHECKBOX_IMAGE_TAG).size(24.dp),
      )
    }
    Spacer(
      modifier =
        Modifier.width(dimensionResource(R.dimen.option_item_between_text_and_icon_padding)),
    )
    Text(
      text = label,
      color = textColor,
    )
    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.option_item_after_text_padding)))
  }
}

const val CHOICE_CHECKBOX_IMAGE_TAG = "checkbox_option_icon"
