/*
 * Copyright 2022-2026 Google LLC
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

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

// TODO: Add platform-specific time formatting that observes user's 24h/12h format preferences
//  On Android, ICU does not observe DateFormat.is24HourFormat() setting.
//  On iOS, should respect NSLocale time format preferences.
//  See: https://developer.android.com/guide/topics/resources/internationalization#24h-setting
//  Currently using basic 24h format as fallback.
@OptIn(FormatStringsInDatetimeFormats::class)
internal fun LocalDateTime.toLocalizedTimeString(): String {
  return LocalDateTime.Format { byUnicodePattern("HH:mm") }.format(this)
}
