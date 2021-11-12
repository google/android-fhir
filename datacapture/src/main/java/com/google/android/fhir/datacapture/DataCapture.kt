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

package com.google.android.fhir.datacapture

import android.app.Application

/**
 * The clients may use [DataCapture] to provide [Configuration] to the library. The clients should
 * set the configuration by calling [initialize] in [Application.onCreate] as it would retain it
 * across configuration changes.
 */
object DataCapture {
  private var _configuration: Configuration? = null
  internal val configuration: Configuration
    get() {
      if (_configuration == null) _configuration = Configuration()
      return _configuration!!
    }

  /**
   * The client may set the [Configuration] for the DataCapture module using this api.
   * [Configuration] should only be set once, calling this api again may cause exception.
   */
  fun initialize(configuration: Configuration) {
    check(_configuration == null) { "DataCapture is already initialized" }
    this._configuration = configuration
  }
}
