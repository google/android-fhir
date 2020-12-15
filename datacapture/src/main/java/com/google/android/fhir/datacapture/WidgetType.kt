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

enum class WidgetType(val value: Int) {
    GROUP(1),
    CHECK_BOX(2),
    DATE(3),
    EDIT_TEXT(4);

    companion object {
        private val VALUES = values()
        fun fromInt(value: Int) = VALUES.first { it.value == value }
    }
}
