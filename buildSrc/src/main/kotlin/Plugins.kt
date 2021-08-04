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

object Plugins {

  object BuildPlugins {
    const val androidLib = "com.android.library"
    const val application = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinKapt = "kotlin-kapt"
    const val mavenPublish = "maven-publish"
    const val navSafeArgs = "androidx.navigation.safeargs.kotlin"
    const val spotless = "com.diffplug.spotless"
  }

  // classpath plugins
  const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
  const val kotlinGradlePlugin =
    "org.jetbrains.kotlin:kotlin-gradle-plugin:${Dependencies.Versions.Kotlin.stdlib}"
  const val navSafeArgsGradlePlugin =
    "androidx.navigation:navigation-safe-args-gradle-plugin:${Dependencies.Versions.Androidx.navigation}"
  const val coveragePlugin = "com.vanniktech.android.junit.jacoco"

  object Versions {
    const val androidGradlePlugin = "4.2.2"
    const val buildTools = "30.0.2"
  }
}
