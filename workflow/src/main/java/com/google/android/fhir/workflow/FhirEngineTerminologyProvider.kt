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

package com.google.android.fhir.workflow

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.search.search
import org.hl7.fhir.r4.model.CodeSystem
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.ValueSet
import org.opencds.cqf.cql.engine.exception.TerminologyProviderException
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo
import org.opencds.cqf.cql.evaluator.engine.util.ValueSetUtil

internal class FhirEngineTerminologyProvider(
  private val fhirContext: FhirContext,
  private val fhirEngine: FhirEngine,
  private val knowledgeManager: KnowledgeManager,
) : TerminologyProvider {

  companion object {
    const val URN_UUID = "urn:uuid:"
    const val URN_OID = "urn:oid:"
  }

  override fun `in`(code: Code, valueSet: ValueSetInfo): Boolean {
    try {
      return expand(valueSet).any { it.code == code.code && it.system == code.system }
    } catch (b: BlockingMainThreadException) {
      throw b
    } catch (e: Exception) {
      throw TerminologyProviderException(
        "Error performing membership check of Code: $code in ValueSet: ${valueSet.id}",
        e
      )
    }
  }

  override fun expand(valueSetInfo: ValueSetInfo): MutableIterable<Code> =
    runBlockingOrThrowMainThreadException {
      try {
        resolveValueSet(valueSetInfo).let {
          ValueSetUtil.getCodesInExpansion(fhirContext, it)
            ?: ValueSetUtil.getCodesInCompose(fhirContext, it)
        }
      } catch (e: Exception) {
        throw TerminologyProviderException(
          "Error performing expansion of ValueSet: ${valueSetInfo.id}",
          e
        )
      }
    }

  override fun lookup(code: Code, codeSystem: CodeSystemInfo): Code =
    runBlockingOrThrowMainThreadException {
      try {
        fhirEngine
          .search<CodeSystem> {
            filter(CodeSystem.CODE, { value = of(code.code) })
            filter(CodeSystem.SYSTEM, { value = codeSystem.id })
          }
          .first()
          .concept
          .first { it.code == code.code }
          .let {
            Code().apply {
              this.code = code.code
              display = it.display
              system = codeSystem.id
            }
          }
      } catch (e: Exception) {
        throw TerminologyProviderException(
          "Error performing lookup of Code: $code in CodeSystem: ${codeSystem.id}",
          e
        )
      }
    }

  private suspend fun searchByUrl(url: String?): List<ValueSet> {
    if (url == null) return emptyList()
    return knowledgeManager
      .loadResources(resourceType = ResourceType.ValueSet.name, url = url)
      .map { it as ValueSet } + fhirEngine.search { filter(ValueSet.URL, { value = url }) }
  }

  private suspend fun searchByIdentifier(identifier: String?): List<ValueSet> {
    if (identifier == null) return emptyList()
    return fhirEngine.search { filter(ValueSet.IDENTIFIER, { value = of(identifier) }) }
  }

  private suspend fun searchById(id: String): List<ValueSet> =
    listOfNotNull(
      safeGet(fhirEngine, ResourceType.ValueSet, id.removePrefix(URN_OID).removePrefix(URN_UUID))
        as? ValueSet
    )

  private suspend fun safeGet(fhirEngine: FhirEngine, type: ResourceType, id: String): Resource? {
    return try {
      fhirEngine.get(type, id)
    } catch (e: ResourceNotFoundException) {
      null
    }
  }

  private suspend fun resolveValueSet(valueSet: ValueSetInfo): ValueSet {
    if (valueSet.version != null ||
        (valueSet.codeSystems != null && valueSet.codeSystems.isNotEmpty())
    ) {
      // Cannot do both at the same time yet.
      throw UnsupportedOperationException(
        "Could not expand value set ${valueSet.id}; version and code system bindings are not supported at this time."
      )
    }

    var searchResults = searchByUrl(valueSet.id)
    if (searchResults.isEmpty()) {
      searchResults = searchByIdentifier(valueSet.id)
    }
    if (searchResults.isEmpty()) {
      searchResults = searchById(valueSet.id)
    }

    return if (searchResults.isEmpty()) {
      throw IllegalArgumentException("Could not resolve value set ${valueSet.id}.")
    } else if (searchResults.size > 1) {
      throw IllegalArgumentException("Found more than 1 ValueSet with url: ${valueSet.id}")
    } else {
      searchResults.first()
    }
  }

  suspend fun resolveValueSetId(valueSet: ValueSetInfo): String {
    return resolveValueSet(valueSet).idElement.idPart
  }
}
