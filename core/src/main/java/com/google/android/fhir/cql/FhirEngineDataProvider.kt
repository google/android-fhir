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

package com.google.android.fhir.cql

import com.google.android.fhir.db.Database
import org.opencds.cqf.cql.data.CompositeDataProvider
import org.opencds.cqf.cql.model.ModelResolver
import org.opencds.cqf.cql.retrieve.RetrieveProvider

/**
 * FHIR Engine's implementation of a [org.opencds.cqf.cql.data.DataProvider] which provides
 * the [org.opencds.cqf.cql.execution.CqlEngine] required data to complete CQL evaluation.
 */
internal class FhirEngineDataProvider internal constructor(
    modelResolver: ModelResolver,
    retrieveProvider: RetrieveProvider
) : CompositeDataProvider(modelResolver, retrieveProvider) {

    internal object Factory {
        internal fun create(database: Database): FhirEngineDataProvider = FhirEngineDataProvider(
            AndroidR4FhirModelResolver(),
            FhirEngineRetrieveProvider(database)
        )
    }
}
