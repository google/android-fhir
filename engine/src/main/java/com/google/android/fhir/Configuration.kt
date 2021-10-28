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

/** Used to configure [FhirEngine] module. */
data class Configuration(
  /** Set the desired number of resources to be returned in each search-set bundle. */
  val pageSize: Int = 500
) {
  /**
   * Clients may implement [Provider] interface to provide [Configuration] for the [FhirEngine]
   * module.
   */
  interface Provider {
    fun getFhirEngineConfiguration(): Configuration
  }
}
