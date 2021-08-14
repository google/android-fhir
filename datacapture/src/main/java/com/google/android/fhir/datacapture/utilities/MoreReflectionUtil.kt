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

package com.google.android.fhir.datacapture.utilities

/**
 * Invokes function specified by [functionName] on the calling object with the provided arguments
 * [args]
 */
internal fun Any.invokeFunction(
  functionName: String,
  parameterTypes: List<Class<*>> = listOf(),
  vararg args: Any?
): Any? =
  this::class
    .java
    .getDeclaredMethod(functionName, *parameterTypes.toTypedArray())
    .apply { isAccessible = true }
    .invoke(this, *args)
