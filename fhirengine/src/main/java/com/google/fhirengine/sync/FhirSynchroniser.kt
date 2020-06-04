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

package com.google.fhirengine.sync

import android.util.Log
import com.google.fhirengine.FhirEngine

/**
 * Class that helps synchronize the data source and save it in the local database
 * TODO remove the FhirEngine dependency
 */
class FhirSynchroniser(
    private val dataSource: FhirDataSource,
    private val fhirEngine: FhirEngine
) {

    private var entriesDownloaded = 0
    suspend fun synchronise() {
        var loadResult: FhirLoadResult
        do {
            loadResult = dataSource.loadData()
            loadResult.resource?.let { bundle ->
                val total = bundle.total
                entriesDownloaded += bundle.entry.size

                val percentage = if (total == 0) {
                    0
                } else {
                    entriesDownloaded * 100 / total
                }
                Log.d("FhirSynchroniser", "downloaded $percentage%: $entriesDownloaded out of $total")
//            loadResult.resource?.let { fhirEngine.save(it) }
            }
        } while (loadResult.canLoadMore)
    }
}
