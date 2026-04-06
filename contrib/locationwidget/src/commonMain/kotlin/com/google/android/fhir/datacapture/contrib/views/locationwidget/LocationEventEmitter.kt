/*
 * Copyright 2026 Google LLC
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

package com.google.android.fhir.datacapture.contrib.views.locationwidget

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal data class LocationData(
  val latitude: Double,
  val longitude: Double,
  val altitude: Double? = null,
)

internal object LocationEventEmitter {
  private val _locationUpdates = MutableSharedFlow<LocationData>(extraBufferCapacity = 1)
  val locationUpdates = _locationUpdates.asSharedFlow()

  fun emit(locationData: LocationData) {
    _locationUpdates.tryEmit(locationData)
  }
}
