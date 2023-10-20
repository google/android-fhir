/*
 * Copyright 2022-2023 Google LLC
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
package com.google.android.fhir.configurablecare.external

import android.content.Context
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.ExternalAnswerValueSetResolver
import com.google.android.fhir.search.search
import com.google.android.fhir.configurablecare.FhirApplication
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ValueSet
import timber.log.Timber

abstract class ValueSetResolver : ExternalAnswerValueSetResolver {

  companion object {
    private lateinit var fhirEngine: FhirEngine
    private lateinit var workerContext: SimpleWorkerContext

    fun init(context: Context) {
      fhirEngine = FhirApplication.fhirEngine(context)
    }

    fun init(context: Context, workerContext: SimpleWorkerContext) {
      println("init")
      fhirEngine = FhirApplication.fhirEngine(context)
      Companion.workerContext = workerContext
    }

    private suspend fun fetchValueSetFromDb(uri: String): List<Coding> {
      val valueSets = fhirEngine.search<ValueSet> { filter(ValueSet.URL, { value = uri }) }

      if (valueSets.isEmpty())
        return listOf(Coding().apply { display = "No referral facility available." })
      else {
        val valueSetList = ArrayList<Coding>()
        for (valueSet in valueSets) {
          for (item in valueSet.resource.expansion.contains) {
            valueSetList.add(
              Coding().apply {
                system = item.system
                code = item.code
                display = item.display
              }
            )
          }
        }
        return valueSetList
      }
    }

    private suspend fun fetchValuesSetFromWorkerContext(uri: String): List<Coding> {
      val valueSets = fhirEngine.search<ValueSet> { filter(ValueSet.URL, { value = uri }) }

      if (valueSets.isEmpty()) {
        val systemUrl = workerContext.fetchResource(
          ValueSet::class.java,
          uri
        )?.compose?.include?.firstOrNull()?.system

        val list = workerContext.fetchCodeSystem(systemUrl)?.concept?.map {
          Coding().apply {
            code = it.code
            display = it.display
            system = systemUrl
          }
        } ?: emptyList()

        if (list.isNotEmpty()) return list

        return workerContext
          .fetchResource(ValueSet::class.java, uri)
          .expansion?.contains?.map {
            Coding().apply {
              code = it.code
              display = it.display
              system = uri
            }
          } ?: emptyList()
      }
      else {
        val valueSetList = ArrayList<Coding>()
        for (valueSet in valueSets) {
          for (item in valueSet.resource.expansion.contains) {
            valueSetList.add(
              Coding().apply {
                system = item.system
                code = item.code
                display = item.display
              }
            )
          }
        }
        return valueSetList
      }
    }
  }

  override suspend fun resolve(uri: String): List<Coding> {
    return fetchValuesSetFromWorkerContext(uri)
  }
}

