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

package com.google.android.fhir.json.db

/** Thrown to indicate that the requested resource is not found. */
class ResourceNotFoundException : Exception {
  val id: String

  constructor(id: String, cause: Throwable) : super("Resource not found with id $id!", cause) {
    this.id = id
  }

  constructor(id: String) : super("Resource not found with id $id!") {
    this.id = id
  }
}
