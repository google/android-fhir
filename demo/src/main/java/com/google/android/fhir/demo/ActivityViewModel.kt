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

package com.google.android.fhir.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.demo.extensions.isFirstLaunch
import com.google.android.fhir.demo.extensions.setFirstLaunchCompleted
import com.google.android.fhir.demo.helpers.PatientCreationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ActivityViewModel(application: Application) : AndroidViewModel(application) {
  private var fhirEngine: FhirEngine = FhirApplication.fhirEngine(application.applicationContext)

  fun createPatientsOnAppFirstLaunch() {
    viewModelScope.launch(Dispatchers.IO) {
      if (getApplication<FhirApplication>().applicationContext.isFirstLaunch()) {
        Timber.i("Creating patients on first launch")
        PatientCreationHelper.createSamplePatients().forEach { fhirEngine.create(it) }
        getApplication<FhirApplication>().applicationContext.setFirstLaunchCompleted()
        Timber.i("Patients created on first launch")
      }
    }
  }
}
