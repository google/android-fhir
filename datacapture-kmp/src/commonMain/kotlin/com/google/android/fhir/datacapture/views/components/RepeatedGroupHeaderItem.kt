/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir.datacapture.views.components

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.delete
import android_fhir.datacapture_kmp.generated.resources.delete_24px
import android_fhir.datacapture_kmp.generated.resources.repeated_group_title
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.QuestionnaireAdapterItem
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RepeatedGroupHeaderItem(
  repeatedGroupHeader: QuestionnaireAdapterItem.RepeatedGroupHeader,
  enabled: Boolean = true,
) {
  val coroutineScope = rememberCoroutineScope { Dispatchers.Main }
  val indexNumber = remember(repeatedGroupHeader.index) { "${repeatedGroupHeader.index + 1}" }
  val title = remember(repeatedGroupHeader.title) { repeatedGroupHeader.title }
  val color =
    if (enabled) QuestionnaireTheme.colorScheme.error else QuestionnaireTheme.colorScheme.onError

  Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = stringResource(Res.string.repeated_group_title, indexNumber, title),
      modifier = Modifier.testTag(REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG).weight(1f),
      style = QuestionnaireTheme.typography.titleMedium,
    )

    OutlinedButton(
      modifier = Modifier.testTag(DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG),
      onClick = { coroutineScope.launch { repeatedGroupHeader.onDeleteClicked() } },
      shape = RoundedCornerShape(4.dp),
      border = ButtonDefaults.outlinedButtonBorder().copy(brush = SolidColor(color)),
      contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
      Icon(
        painter = painterResource(Res.drawable.delete_24px),
        contentDescription = DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG,
        tint = color,
      )
      Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
      Text(
        text = stringResource(Res.string.delete),
        color = color,
      )
    }
  }
}

internal const val DELETE_REPEATED_GROUP_ITEM_BUTTON_TAG = "delete_repeated_group_item_button"
internal const val REPEATED_GROUP_INSTANCE_HEADER_TITLE_TAG = "repeated_group_instance_header_title"
