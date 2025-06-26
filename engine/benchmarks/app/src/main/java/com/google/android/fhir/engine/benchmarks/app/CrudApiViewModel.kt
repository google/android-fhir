/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.engine.benchmarks.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.tracing.traceAsync
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.engine.benchmarks.app.data.ResourcesDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

internal class CrudApiViewModel(
  private val resourcesDataProvider: ResourcesDataProvider,
  private val fhirEngine: FhirEngine,
) : ViewModel() {

  private val _crudMutableStateFlow = MutableStateFlow(CRUDUiState())
  val crudStateFlow = _crudMutableStateFlow.asStateFlow()

  init {
    viewModelScope.launch(benchmarkingViewModelWorkDispatcher) { traceCRUD() }
  }

  private suspend fun traceCRUD() {
    // Create
    fhirEngine.clearDatabase()
    var savedResourceTypeIdPairSequence: Sequence<Pair<ResourceType, String>> = sequenceOf()

    resourcesDataProvider.collectPatientResources { resources ->
      val (logicalIds, duration) = traceCreateResources(resources)
      val result = BenchmarkDuration(logicalIds.size, duration)
      _crudMutableStateFlow.update { it.copy(create = it.create + result) }

      savedResourceTypeIdPairSequence +=
        resources.zip(logicalIds) { r, l -> Pair(r.resourceType, l) }
    }

    // Get
    val dbResources =
      savedResourceTypeIdPairSequence.shuffled().take(RUD_SAMPLE_SIZE).toList().map { pair ->
        val (resourceType, logicalId) = pair
        val (resource, duration) = traceGetResource(resourceType, logicalId)

        val result = BenchmarkDuration(1, duration)
        _crudMutableStateFlow.update { it.copy(read = it.read + result) }
        resource
      }

    // Update
    val updateDbResources =
      dbResources.shuffled().map { resource ->
        val duration = traceUpdateResources(listOf(resource))

        val result = BenchmarkDuration(1, duration)
        _crudMutableStateFlow.update { it.copy(update = it.update + result) }
        resource
      }

    // Delete
    updateDbResources.shuffled().forEach { resource ->
      val logicalId = resource.idElement?.idPart.orEmpty()
      val duration = traceDeleteResources(resource.resourceType, logicalId)
      val result = BenchmarkDuration(1, duration)
      _crudMutableStateFlow.update { it.copy(delete = it.delete + result) }
    }
  }

  /**
   * measureTime wraps to get time elapsed for display in UI while trace wraps for use in
   * macrobenchmarking of the application as a TraceMetric
   */
  private suspend fun traceCreateResources(resources: List<Resource>) = measureTimedValueAsync {
    traceAsync(TRACE_CREATE_SECTION_NAME, 0) { fhirEngine.create(*resources.toTypedArray()) }
  }

  private suspend fun traceUpdateResources(resources: List<Resource>) = measureTimeAsync {
    traceAsync(TRACE_UPDATE_SECTION_NAME, 1) { fhirEngine.update(*resources.toTypedArray()) }
  }

  private suspend fun traceGetResource(resourceType: ResourceType, resourceId: String) =
    measureTimedValueAsync {
      traceAsync(TRACE_GET_SECTION_NAME, 2) { fhirEngine.get(resourceType, resourceId) }
    }

  private suspend fun traceDeleteResources(resourceType: ResourceType, resourceId: String) =
    measureTimeAsync {
      traceAsync(TRACE_DELETE_SECTION_NAME, 3) { fhirEngine.delete(resourceType, resourceId) }
    }

  companion object {
    const val TRACE_CREATE_SECTION_NAME = "Create API"
    const val TRACE_UPDATE_SECTION_NAME = "Update API"
    const val TRACE_GET_SECTION_NAME = "Get API"
    const val TRACE_DELETE_SECTION_NAME = "Delete API"

    const val RUD_SAMPLE_SIZE = 500
  }
}

internal data class CRUDUiState(
  val create: BenchmarkDuration = BenchmarkDuration.ZERO,
  val read: BenchmarkDuration = BenchmarkDuration.ZERO,
  val update: BenchmarkDuration = BenchmarkDuration.ZERO,
  val delete: BenchmarkDuration = BenchmarkDuration.ZERO,
)
