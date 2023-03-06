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

package com.google.android.fhir.datacapture

import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

// ICU on Android does not observe the user's 24h/12h time format setting (obtained from
// DateFormat.is24HourFormat()). In order to observe the setting, we are using DateFormat as
// suggested in the docs. See
// https://developer.android.com/guide/topics/resources/internationalization#24h-setting for
// details.
internal fun LocalTime.toLocalizedString(context: Context) =
  LocalDateTime.of(LocalDate.now(), this).toLocalizedTimeString(context)
