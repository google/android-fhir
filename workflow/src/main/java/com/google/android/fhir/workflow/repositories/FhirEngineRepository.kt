/*
 * Copyright 2023-2026 Google LLC
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

package com.google.android.fhir.workflow.repositories

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.model.api.IQueryParameterType
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.gclient.TokenClientParam
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.util.BundleBuilder
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.getResourceType
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.filter.TokenParamFilterCriterion
import com.google.android.fhir.workflow.applyFilterParam
import com.google.android.fhir.workflow.runBlockingOrThrowMainThreadException
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseConformance
import org.hl7.fhir.instance.model.api.IBaseParameters
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ValueSet
import org.opencds.cqf.fhir.api.Repository
import timber.log.Timber

class FhirEngineRepository(
  private val fhirContext: FhirContext,
  private val fhirEngine: FhirEngine,
  private val knowledgeManager: KnowledgeManager,
) : Repository {
  override fun <T : IBaseResource, I : IIdType> read(
    resourceType: Class<T>?,
    id: I,
    headers: MutableMap<String, String>?,
  ): T? = runBlockingOrThrowMainThreadException {
    val result =
      try {
        fhirEngine.get(getResourceType(resourceType as Class<Resource>), id.idPart) as T?
      } catch (resourceNotFoundException: ResourceNotFoundException) {
        Timber.w("Found more than one value in the IgManager for the id $id")
        null
      }

    return@runBlockingOrThrowMainThreadException result
  }

  override fun <T : IBaseResource?> create(
    resource: T,
    headers: MutableMap<String, String>?,
  ): MethodOutcome = runBlockingOrThrowMainThreadException {
    val results = fhirEngine.create(resource as Resource)

    val outcome = MethodOutcome()
    outcome.created = true
    outcome.id = IdType(resource.fhirType(), results.first())
    return@runBlockingOrThrowMainThreadException outcome
  }

  override fun <I : IIdType?, P : IBaseParameters?> patch(
    id: I,
    patchParameters: P,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    TODO("Not yet implemented")
  }

  override fun <T : IBaseResource?> update(
    resource: T,
    headers: MutableMap<String, String>?,
  ): MethodOutcome = runBlockingOrThrowMainThreadException {
    fhirEngine.update(resource as Resource)
    return@runBlockingOrThrowMainThreadException MethodOutcome()
  }

  override fun <T : IBaseResource?, I : IIdType?> delete(
    resourceType: Class<T>?,
    id: I,
    headers: MutableMap<String, String>?,
  ): MethodOutcome = runBlockingOrThrowMainThreadException {
    if (id != null) {
      fhirEngine.delete(getResourceType(resourceType as Class<Resource>), id.idPart)
    }
    return@runBlockingOrThrowMainThreadException MethodOutcome()
  }

  override fun <B : IBaseBundle, T : IBaseResource?> search(
    bundleType: Class<B>?,
    resourceType: Class<T>?,
    searchParameters: MutableMap<String, MutableList<IQueryParameterType>>?,
    headers: MutableMap<String, String>?,
  ): B {
    return runBlockingOrThrowMainThreadException {
      val builder = BundleBuilder(fhirContext)
      builder.setType("searchset")

      if (resourceType != null) {
        val search = Search(type = getResourceType(resourceType as Class<Resource>))
        if (searchParameters == null) {
          fhirEngine
            .search<Resource>(search)
            .map { it.resource }
            .forEach(builder::addCollectionEntry)
        } else if (searchParameters.size == 1 && searchParameters.containsKey("url")) {
          // first AND then OR
          searchParameters.forEach { param ->
            param.value.forEach { search.applyFilterParam(param.key, it, Operation.OR) }
          }

          fhirEngine
            .search<Resource>(search)
            .map { it.resource }
            .forEach(builder::addCollectionEntry)
        } else {
          searchParameters.forEach { (key, values) ->
            values.forEach { param ->
              // cqf passes a value-set code filter as an UNEXPANDED token (code:in <ValueSet url>).
              // The data repository must resolve + expand it to member codes — mirroring the legacy
              // FhirEngineRetrieveProvider.filterByValueSet. Otherwise applyFilterParam treats the
              // value-set URL as a literal code that matches nothing, silently breaking value-set
              // retrieves like [Condition: "Absence of cervix"]. One filter() with N criteria = OR.
              val codings = valueSetCodings(param)
              if (codings != null) {
                search.filter(
                  TokenClientParam(key),
                  *codings
                    .map<Coding, TokenParamFilterCriterion.() -> Unit> { c -> { value = of(c) } }
                    .toTypedArray(),
                )
              } else {
                search.applyFilterParam(key, param, Operation.OR)
              }
            }
          }

          fhirEngine
            .search<Resource>(search)
            .map { it.resource }
            .forEach(builder::addCollectionEntry)
        }
      }

      builder.bundle as B
    }
  }

  /**
   * If [param] is a value-set reference (a code token whose value is a ValueSet canonical URL, as
   * cqf passes for `code:in <valueset>` retrieves), resolve the ValueSet from the KnowledgeManager
   * and return its member codes; otherwise null (the caller then treats it as a literal code).
   * Prefers a precomputed `expansion`, falling back to a naive `compose.include.concept` expansion.
   */
  private suspend fun valueSetCodings(param: IQueryParameterType): List<Coding>? {
    if (param !is TokenParam || param.system != null) return null
    val url = param.value ?: return null
    if (!(url.contains("://") || url.startsWith("urn:"))) return null
    val valueSet =
      knowledgeManager.loadResources(url = url).filterIsInstance<ValueSet>().firstOrNull()
        ?: return null
    val codings =
      when {
        valueSet.hasExpansion() && valueSet.expansion.hasContains() ->
          valueSet.expansion.contains.map { Coding(it.system, it.code, it.display) }
        valueSet.hasCompose() ->
          valueSet.compose.include.flatMap { inc ->
            inc.concept.map { Coding(inc.system, it.code, it.display) }
          }
        else -> emptyList()
      }
    return codings.ifEmpty { null }
  }

  override fun <B : IBaseBundle?> link(
    bundleType: Class<B>?,
    url: String?,
    headers: MutableMap<String, String>?,
  ): B {
    TODO("Not yet implemented")
  }

  override fun <C : IBaseConformance?> capabilities(
    resourceType: Class<C>?,
    headers: MutableMap<String, String>?,
  ): C {
    TODO("Not yet implemented")
  }

  override fun <B : IBaseBundle?> transaction(
    transaction: B,
    headers: MutableMap<String, String>?,
  ): B {
    TODO("Not yet implemented")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?> invoke(
    name: String?,
    parameters: P,
    returnType: Class<R>?,
    headers: MutableMap<String, String>?,
  ): R {
    println("Engine Invoke 1 $name")
    TODO("Not yet implemented")
  }

  override fun <P : IBaseParameters?> invoke(
    name: String?,
    parameters: P,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    println("Engine 2 $name")
    TODO("Not yet implemented")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?, T : IBaseResource?> invoke(
    resourceType: Class<T>?,
    name: String?,
    parameters: P,
    returnType: Class<R>?,
    headers: MutableMap<String, String>?,
  ): R {
    println("Engine 3 $name")
    TODO("Not yet implemented")
  }

  override fun <P : IBaseParameters?, T : IBaseResource?> invoke(
    resourceType: Class<T>?,
    name: String?,
    parameters: P,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    println("Engine 4 $name")
    TODO("Not yet implemented")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?, I : IIdType?> invoke(
    id: I,
    name: String?,
    parameters: P,
    returnType: Class<R>?,
    headers: MutableMap<String, String>?,
  ): R {
    println("Engine 5 $name")
    TODO("Not yet implemented")
  }

  override fun <P : IBaseParameters?, I : IIdType?> invoke(
    id: I,
    name: String?,
    parameters: P,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    println("Engine 6 $name")
    TODO("Not yet implemented")
  }

  override fun <B : IBaseBundle?, P : IBaseParameters?> history(
    parameters: P,
    returnType: Class<B>?,
    headers: MutableMap<String, String>?,
  ): B {
    TODO("Not yet implemented")
  }

  override fun <B : IBaseBundle?, P : IBaseParameters?, T : IBaseResource?> history(
    resourceType: Class<T>?,
    parameters: P,
    returnType: Class<B>?,
    headers: MutableMap<String, String>?,
  ): B {
    TODO("Not yet implemented")
  }

  override fun <B : IBaseBundle?, P : IBaseParameters?, I : IIdType?> history(
    id: I,
    parameters: P,
    returnType: Class<B>?,
    headers: MutableMap<String, String>?,
  ): B {
    TODO("Not yet implemented")
  }

  override fun fhirContext() = fhirContext
}
