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

package com.google.android.fhir.demo.extensions

import android.content.Context
import android.content.SharedPreferences
import com.google.android.fhir.demo.extensions.AppConstants.FIRST_LAUNCH_KEY
import com.google.android.fhir.demo.extensions.AppConstants.PREFS_NAME

fun Context.isFirstLaunch(): Boolean {
  val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  return sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true)
}

fun Context.setFirstLaunchCompleted() {
  val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  val editor = sharedPreferences.edit()
  editor.putBoolean(FIRST_LAUNCH_KEY, false)
  editor.apply()
}

object AppConstants {
  const val PREFS_NAME = "app_prefs"
  const val FIRST_LAUNCH_KEY = "is_first_launch"
}
