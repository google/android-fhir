/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.demo.data

import android.content.Context
import androidx.work.WorkerParameters
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.demo.LoginRepository
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.FhirSyncWorker
import kotlinx.coroutines.runBlocking

class FhirPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

  override fun getDownloadWorkManager(): DownloadWorkManager {
    val loginRepository = LoginRepository.getInstance(applicationContext)
    // TODO(omarismail@): Remove try/catch when token actually returned
    val patientListId: String? =
      try {
        runBlocking {
          val accessToken = loginRepository.getAccessToken()
          JWT(accessToken.toString()).getClaim("patient_list").asString()
        }
      } catch (e: DecodeException) {
        "patient-list-example"
      }

    return DownloadWorkManagerImpl(patientListId!!)
  }

  override fun getFhirEngine() = FhirApplication.fhirEngine(applicationContext)
}
