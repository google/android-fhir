/*
 * Copyright 2022-2024 Google LLC
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

package com.google.android.fhir.catalog

import android.app.Application
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.datacapture.DataCaptureConfig
import com.google.android.fhir.datacapture.ExternalAnswerValueSetResolver
import com.google.android.fhir.search.search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Coding

class CatalogApplication : Application(), DataCaptureConfig.Provider {
  // Only initiate the FhirEngine when used for the first time, not when the app is created.
  private val fhirEngine: FhirEngine by lazy { FhirEngineProvider.getInstance(this) }

  private lateinit var dataCaptureConfig: DataCaptureConfig

  override fun onCreate() {
    super.onCreate()

    FhirEngineProvider.init(FhirEngineConfiguration())

    dataCaptureConfig =
      DataCaptureConfig(
        xFhirQueryResolver = { fhirEngine.search(it).map { it.resource } },
        questionnaireItemViewHolderFactoryMatchersProviderFactory =
          ContribQuestionnaireItemViewHolderFactoryMatchersProviderFactory,
        valueSetResolverExternal =
          object : ExternalAnswerValueSetResolver {
            override suspend fun resolve(uri: String, query: String?): List<Coding> {
              delay(1000)
              // Here we can call out to our FHIR terminology server with the provided uri and query
              if (uri == "https://my.url/fhir/ValueSet/my-valueset" && !query.isNullOrBlank()) {
                return listOf(
                  Coding().apply {
                    code = "a"
                    display = "Custom response A"
                  },
                  Coding().apply {
                    code = "b"
                    display = "Custom response B"
                  },
                )
              }
              return emptyList()
            }
          },
      )

    CoroutineScope(Dispatchers.IO).launch {
      assets
        .open("resource_data_bundle.json")
        .bufferedReader()
        .use { bufferedReader -> bufferedReader.readText() }
        .let { stringValue ->
          FhirContext.forR4Cached().newJsonParser().parseResource(stringValue) as Bundle
        }
        .entry
        .map { bundleEntryComponent -> bundleEntryComponent.resource }
        .let { resources -> fhirEngine.create(*resources.toTypedArray()) }
    }
  }

  override fun getDataCaptureConfig(): DataCaptureConfig = dataCaptureConfig
}
