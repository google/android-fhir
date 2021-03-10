/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.datacapture

import com.google.fhir.r4.core.Questionnaire

internal const val ITEM_CONTROL_DROP_DOWN = "drop-down"
internal const val ITEM_CONTROL_RADIO_BUTTON = "radio-button"

internal const val EXTENSION_ITEM_CONTROL_URL =
  "http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl"
internal const val EXTENSION_ITEM_CONTROL_SYSTEM = "http://hl7.org/fhir/questionnaire-item-control"

// Item control code as string or null
internal val Questionnaire.Item.itemControl: String?
  get() {
    return when (this.extensionList
        .firstOrNull { it.url.value == EXTENSION_ITEM_CONTROL_URL }
        ?.value
        ?.codeableConcept
        ?.codingList
        ?.firstOrNull { it.system.value == EXTENSION_ITEM_CONTROL_SYSTEM }
        ?.code
        ?.value
    ) {
      ITEM_CONTROL_DROP_DOWN -> ITEM_CONTROL_DROP_DOWN
      ITEM_CONTROL_RADIO_BUTTON -> ITEM_CONTROL_RADIO_BUTTON
      else -> null
    }
  }
