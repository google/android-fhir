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

package deps

object Plugins {
  const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
  const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Versions.Kotlin.stdlib}"
  const val navSafeArgsGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Dependencies.Versions.Androidx.navigation}"
  const val spotlessGradlePlugin = "com.diffplug.spotless:spotless-plugin-gradle:${Versions.spotlessGradlePlugin}"

  object Versions {
    const val androidGradlePlugin = "4.2.0-beta02"
    const val buildTools = "30.0.2"
    const val spotlessGradlePlugin = "5.11.0"
  }

}
