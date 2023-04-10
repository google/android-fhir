/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.demo.external

import android.content.Context
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.ExternalAnswerValueSetResolver
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ValueSet

abstract class ValueSetResolver : ExternalAnswerValueSetResolver {

  companion object {
    private lateinit var fhirEngine: FhirEngine

    fun init(context: Context) {
      fhirEngine = FhirApplication.fhirEngine(context)
    }

    private suspend fun fetchValueSetFromDb(uri: String): List<Coding> {

      val valueSets = fhirEngine.search<ValueSet> { filter(ValueSet.URL, { value = uri }) }

      if (valueSets.isEmpty())
        return listOf(Coding().apply { display = "No referral facility available." })
      else {
        val valueSetList = ArrayList<Coding>()
        for (valueSet in valueSets) {
          for (item in valueSet.expansion.contains) {
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
    return fetchValueSetFromDb(uri)
  }
}
