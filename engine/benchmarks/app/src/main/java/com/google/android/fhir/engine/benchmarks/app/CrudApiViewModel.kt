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
import androidx.tracing.trace
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.engine.benchmarks.app.data.ResourcesDataProvider
import kotlin.time.measureTime
import kotlin.time.measureTimedValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

@Suppress("unused")
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
    val savedResourceTypeIdPairs: MutableList<List<Pair<ResourceType, String>>> = mutableListOf()

    resourcesDataProvider.provideResources { resources ->
      val (logicalIds, duration) = traceCreateResources(resources)
      val result = BenchmarkDuration(logicalIds.size, duration)
      _crudMutableStateFlow.update {
        val newUpdatedResult =
          when (it.create) {
            is BenchmarkResult.Nil -> result
            is BenchmarkDuration -> it.create + result
          }
        it.copy(create = newUpdatedResult)
      }

      savedResourceTypeIdPairs += resources.zip(logicalIds) { r, l -> Pair(r.resourceType, l) }
    }

    // Get
    val dbResources =
      savedResourceTypeIdPairs.mapIndexed { index, list ->
        val (resourceType, logicalId) = list.shuffled().random()
        val (resource, duration) = traceGetResource(resourceType, logicalId)

        val result = BenchmarkDuration(1, duration)
        _crudMutableStateFlow.update {
          val newUpdatedResult =
            when (it.read) {
              is BenchmarkResult.Nil -> result
              is BenchmarkDuration -> it.read + result
            }
          it.copy(read = newUpdatedResult)
        }
        resource
      }

    // Update
    val updateDbResources =
      dbResources.shuffled().mapIndexed { index, resource ->
        val duration = traceUpdateResources(listOf(resource))

        val result = BenchmarkDuration(1, duration)
        _crudMutableStateFlow.update {
          val newUpdatedResult =
            when (it.update) {
              is BenchmarkResult.Nil -> result
              is BenchmarkDuration -> it.update + result
            }
          it.copy(update = newUpdatedResult)
        }
        resource
      }

    // Delete
    updateDbResources.shuffled().forEachIndexed { index, resource ->
      val logicalId = resource.idElement?.idPart.orEmpty()
      val duration = runBlocking { traceDeleteResources(resource.resourceType, logicalId) }
      val result = BenchmarkDuration(1, duration)
      _crudMutableStateFlow.update {
        val newUpdatedResult =
          when (it.delete) {
            is BenchmarkResult.Nil -> result
            is BenchmarkDuration -> it.delete + result
          }
        it.copy(delete = newUpdatedResult)
      }
    }
  }

  /**
   * measureTime wraps to get time elapsed for display in UI while trace wraps for use in
   * macrobenchmarking of the application as a TraceMetric
   */
  private fun traceCreateResources(resources: List<Resource>) = measureTimedValue {
    trace(TRACE_CREATE_SECTION_NAME) {
      runBlocking { fhirEngine.create(*resources.toTypedArray()) }
    }
  }

  private fun traceUpdateResources(resources: List<Resource>) = measureTime {
    trace(TRACE_UPDATE_SECTION_NAME) {
      runBlocking { fhirEngine.update(*resources.toTypedArray()) }
    }
  }

  private fun traceGetResource(resourceType: ResourceType, resourceId: String) = measureTimedValue {
    trace(TRACE_GET_SECTION_NAME) { runBlocking { fhirEngine.get(resourceType, resourceId) } }
  }

  private fun traceDeleteResources(resourceType: ResourceType, resourceId: String) = measureTime {
    trace(TRACE_DELETE_SECTION_NAME) { runBlocking { fhirEngine.delete(resourceType, resourceId) } }
  }

  companion object {
    const val TRACE_CREATE_SECTION_NAME = "Create API"
    const val TRACE_UPDATE_SECTION_NAME = "Update API"
    const val TRACE_GET_SECTION_NAME = "Get API"
    const val TRACE_DELETE_SECTION_NAME = "Delete API"
  }
}

internal data class CRUDUiState(
  val create: BenchmarkResult = BenchmarkResult.Nil,
  val read: BenchmarkResult = BenchmarkResult.Nil,
  val update: BenchmarkResult = BenchmarkResult.Nil,
  val delete: BenchmarkResult = BenchmarkResult.Nil,
)
