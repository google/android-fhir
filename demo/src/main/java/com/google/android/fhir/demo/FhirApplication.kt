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

package com.google.android.fhir.demo

import android.app.Application
import android.content.Context
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.sync.Authenticator
import timber.log.Timber

class FhirApplication : Application() {
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { constructFhirEngine() }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
    val authenticator =
      object : Authenticator {
        override fun getAccessToken(): String {
          return token
        }
      }
    FhirEngineProvider.init(
      FhirEngineConfiguration(
        enableEncryptionIfSupported = true,
        RECREATE_AT_OPEN,
        ServerConfiguration(
          "https://malaria1.opencampaignlink.org/fhir/",
          authenticator = authenticator
        )
      )
    )
  }

  private fun constructFhirEngine(): FhirEngine {
    return FhirEngineProvider.getInstance(this)
  }

  companion object {
    fun fhirEngine(context: Context) = (context.applicationContext as FhirApplication).fhirEngine
    val token =
      "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFdzNlZVlQeE4tNFhaVGlEamg4MjhBQUk3RTh6eUJnZTJJaGNFVlhFX0FBIn0.eyJleHAiOjE2NjAwNDExMDgsImlhdCI6MTY1OTUyMjcwOCwianRpIjoiOTkwOGY2YjgtNzg1NS00ZjVmLTlhOTQtNmJkNjZkNWFkMGRhIiwiaXNzIjoiaHR0cHM6Ly9tYWxhcmlhMS5vcGVuY2FtcGFpZ25saW5rLm9yZzo4NDQzL2F1dGgvcmVhbG1zL2ZoaXItaGFwaSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIyMWViNzg5MC0yOTQ3LTRhZjEtYTJiYS1kNTc2MTNjMDE4MTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJmaGlyLWhhcGktc2VydmVyIiwic2Vzc2lvbl9zdGF0ZSI6IjMyOGVmMGYxLWI4YmMtNDFjMC1iNzQyLTUxZTc2NWI5YWFiZSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9tYWxhcmlhMS5vcGVuY2FtcGFpZ25saW5rLm9yZyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1maGlyLWhhcGkiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMzI4ZWYwZjEtYjhiYy00MWMwLWI3NDItNTFlNzY1YjlhYWJlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiS2FzaHlhcCBKb2lzIiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FzaHlhcF9qb2lzIiwiZ2l2ZW5fbmFtZSI6Ikthc2h5YXAiLCJmYW1pbHlfbmFtZSI6IkpvaXMiLCJlbWFpbCI6Imtqb2lzQGlwcmRncm91cC5jb20ifQ.bQGJfU9vX2mgKaopKIwWXKFFvnlLTlvh3LNzXRhj-FI_XyU9uZ1G3mIc9ZNl7dctAD4O2tid_TMRflyp0G8G1HeSOg2GiI91npBVopQHYDPqCHahd0p5HR5hG9dyzPZKNveu7fX0ZBB6IZj6XSyD29RuTQTo1LDgtc2DQJudnlC3K-Qq-rtN3dhFtvCufcTW9SERuuIiCMth3x6Vk0F_HvTF2urh6q7yq_Hi1vbyc_ZlrLOKk8WawfxbtTrdBkxCW2YrQShNOLSNpWATu1VeJiFphAhDUH5OEBcdK1Qdg1JCeuH6I8ujzHWdg3OhkdIEq5fU_TJ_JFVDmZVtu3IrWw"
  }
}
