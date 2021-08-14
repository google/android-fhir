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

package com.google.android.fhir.hapiprotoconverter.generated
internal fun String.hapiCodeCheck(): String {
  return if (equals("INVALID_UNINITIALIZED", true) ||
      equals("UNRECOGNIZED", true) ||
      isNullOrBlank()
  )
    "NULL"
  else this
}

internal fun String?.protoCodeCheck(): String {
  return if (isNullOrBlank()) "INVALID_UNINITIALIZED"
  else if (equals("NULL", true)) "UNRECOGNIZED" else this
}
