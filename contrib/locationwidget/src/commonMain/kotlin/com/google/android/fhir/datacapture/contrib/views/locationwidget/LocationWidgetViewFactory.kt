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

import android_fhir.contrib.locationwidget.generated.resources.Res
import android_fhir.contrib.locationwidget.generated.resources.gm_location_on_24
import android_fhir.contrib.locationwidget.generated.resources.record_gps_location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.fhir.datacapture.QuestionnaireItemViewFactoryMatcher
import com.google.android.fhir.datacapture.extensions.itemControlCode
import com.google.android.fhir.datacapture.extensions.itemMedia
import com.google.android.fhir.datacapture.theme.QuestionnaireTheme
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.components.Header
import com.google.android.fhir.datacapture.views.components.MediaItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewFactory
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.GeolocatorResult
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

object LocationItemViewFactory : QuestionnaireItemViewFactory {

  @Composable
  override fun Content(questionnaireViewItem: QuestionnaireViewItem) {
    val coroutineScope = rememberCoroutineScope()
    val geolocator = rememberGeolocator()
    var isLoadingLocation by remember(questionnaireViewItem) { mutableStateOf(false) }
    var geolocatorError: String? by remember(questionnaireViewItem) { mutableStateOf(null) }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            horizontal = QuestionnaireTheme.dimensions.itemMarginHorizontal,
            vertical = QuestionnaireTheme.dimensions.itemMarginVertical,
          ),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Header(questionnaireViewItem)

      questionnaireViewItem.questionnaireItem.itemMedia?.let { MediaItem(it) }

      TextButton(
        onClick = {
          isLoadingLocation = true
          coroutineScope.launch {
            val result = geolocator.current(priority = Priority.HighAccuracy)
            if (result is GeolocatorResult.Success) {
              geolocatorError = null
              val loc = result.data
              LocationEventEmitter.emit(
                LocationData(
                  latitude = loc.coordinates.latitude,
                  longitude = loc.coordinates.longitude,
                  altitude = loc.mslAltitude?.meters,
                ),
              )
            } else if (result is GeolocatorResult.Error) {
              geolocatorError = result.message
              println("Location fetch failed: $geolocatorError")
            }

            isLoadingLocation = false
          }
        },
        enabled = !isLoadingLocation,
      ) {
        Icon(
          painter = painterResource(Res.drawable.gm_location_on_24),
          contentDescription = "Record GPS icon",
          modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(Res.string.record_gps_location).uppercase())
        if (isLoadingLocation) {
          Spacer(modifier = Modifier.width(8.dp))
          CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        }
      }

      if (!geolocatorError.isNullOrBlank()) {
        Text(
          text = geolocatorError!!,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier.padding(start = 12.dp),
        )
      }
    }
  }
}

val LocationItemViewFactoryMatcher =
  QuestionnaireItemViewFactoryMatcher(LocationItemViewFactory) {
    it.itemControlCode == "location-widget"
  }
