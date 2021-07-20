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

package com.google.android.fhir

import android.content.Context
import android.content.SharedPreferences
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object SharedPreferencesUtil {

  private lateinit var prefs: SharedPreferences
  private const val PREFS_NAME = "FHIR_ENGINE_PREFERENCES"

  fun init(context: Context): SharedPreferencesUtil {
    prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return this
  }

  fun read(key: String, value: String): String? {
    return prefs.getString(key, value)
  }

  fun readTimestamp(key: String): LocalDateTime {
    val millis = prefs.getLong(key, 0)
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
  }

  fun read(key: String, value: Long): Long {
    return prefs.getLong(key, value)
  }

  fun write(key: String, value: String) {
    val prefsEditor: SharedPreferences.Editor = prefs.edit()
    with(prefsEditor) {
      putString(key, value)
      commit()
    }
  }

  fun write(key: String, value: LocalDateTime) {
    val millis = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    write(key, millis)
  }

  fun write(key: String, value: Long) {
    val prefsEditor: SharedPreferences.Editor = prefs.edit()
    with(prefsEditor) {
      putLong(key, value)
      commit()
    }
  }
}
