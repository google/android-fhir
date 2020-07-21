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

package com.google.fhirengine

import android.content.Context
import com.google.fhirengine.sync.FhirDataSource
import com.google.fhirengine.sync.SyncConfiguration

/**
 * The builder for [FhirEngine] instance
 */
class FhirEngineBuilder constructor(
  syncConfiguration: SyncConfiguration,
  dataSource: FhirDataSource,
  context: Context
) {
    private val services = FhirServices.builder(syncConfiguration, dataSource, context)

    /**
     * Sets the database file name for the FhirEngine to use.
     */
    fun databaseName(name: String) = apply {
        services.databaseName(name)
    }

    /**
     * Instructs the FhirEngine to use an in memory database which can be useful for tests.
     */
    internal fun inMemory() = apply {
        services.inMemory()
    }

    /**
     * Builds a new instance of the [FhirEngine].
     */
    fun build() = services.build().fhirEngine
}
