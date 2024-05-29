/*
 * Copyright 2023 Google LLC
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
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.param.UriParam
import ca.uhn.fhir.util.BundleBuilder
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.runBlockingOrThrowMainThreadException
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseConformance
import org.hl7.fhir.instance.model.api.IBaseParameters
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.opencds.cqf.fhir.api.Repository
import timber.log.Timber

class KnowledgeRepository(
  private val fhirContext: FhirContext,
  private val knowledgeManager: KnowledgeManager,
) : Repository {
  override fun <T : IBaseResource, I : IIdType> read(
    resourceType: Class<T>?,
    id: I,
    headers: MutableMap<String, String>?,
  ): T? = runBlockingOrThrowMainThreadException {
    println("$resourceType ${id.baseUrl}/${id.resourceType}/${id.idPart}")

    val result =
      try {
        knowledgeManager
          .loadResources(
            resourceType = id.resourceType,
            id = id.idPart,
          )
          .single() as T?
      } catch (resourceNotFoundException: ResourceNotFoundException) {
        Timber.w("Found more than one value in the IgManager for the id $id")
        null as T?
      }

    if (result != null) {
      return@runBlockingOrThrowMainThreadException result as T?
    }

    val anotherTry =
      try {
        knowledgeManager.loadResources(resourceType = id.resourceType, id = id.toString()).single()
      } catch (resourceNotFoundException: ResourceNotFoundException) {
        Timber.w("Found more than one value in the IgManager for the id $id")
        null as T
      }

    return@runBlockingOrThrowMainThreadException anotherTry as T?
  }

  override fun <T : IBaseResource?> create(
    resource: T,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    TODO("Not yet implemented")
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
  ): MethodOutcome {
    TODO("Not yet implemented")
  }

  override fun <T : IBaseResource?, I : IIdType?> delete(
    resourceType: Class<T>?,
    id: I,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    TODO("Not yet implemented")
  }

  override fun <B : IBaseBundle, T : IBaseResource?> search(
    bundleType: Class<B>?,
    resourceType: Class<T>?,
    searchParameters: MutableMap<String, MutableList<IQueryParameterType>>?,
    headers: MutableMap<String, String>?,
  ): B {
    return runBlockingOrThrowMainThreadException {
      println("Knowledge Search = $resourceType $searchParameters")

      val builder = BundleBuilder(fhirContext)
      builder.setType("searchset")

      if (resourceType != null) {
        val urls =
          searchParameters?.get("url")?.let { param ->
            param.mapNotNull { ((it as? UriParam)?.value ?: (it as? StringParam)?.value) }
          }

        val ids =
          searchParameters?.get("id")?.let { param ->
            param.mapNotNull { ((it as? UriParam)?.value ?: (it as? StringParam)?.value) }
          }

        val names =
          searchParameters?.get("name")?.let { param ->
            param.mapNotNull { ((it as? UriParam)?.value ?: (it as? StringParam)?.value) }
          }

        val versions =
          searchParameters?.get("version")?.let { param ->
            param.mapNotNull { ((it as? UriParam)?.value ?: (it as? StringParam)?.value) }
          }

        knowledgeManager
          .loadResources(
            resourceType = resourceType.getSimpleName(),
            url = urls?.getOrNull(0),
            id = ids?.getOrNull(0),
            name = names?.getOrNull(0),
            version = versions?.getOrNull(0),
          )
          .forEach(builder::addCollectionEntry)
      }

      builder.bundle as B
    }
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
    println("Knowledge Invoke 1 $name")
    TODO("Not yet implemented")
  }

  override fun <P : IBaseParameters?> invoke(
    name: String?,
    parameters: P,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    println("Knowledge Invoke 2 $name")
    TODO("Not yet implemented")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?, T : IBaseResource?> invoke(
    resourceType: Class<T>?,
    name: String?,
    parameters: P,
    returnType: Class<R>?,
    headers: MutableMap<String, String>?,
  ): R {
    println("Knowledge Invoke 3 $name")
    TODO("Not yet implemented")
  }

  override fun <P : IBaseParameters?, T : IBaseResource?> invoke(
    resourceType: Class<T>?,
    name: String?,
    parameters: P,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    println("Knowledge Invoke 4 $name")
    TODO("Not yet implemented")
  }

  override fun <R : IBaseResource?, P : IBaseParameters?, I : IIdType?> invoke(
    id: I,
    name: String?,
    parameters: P,
    returnType: Class<R>?,
    headers: MutableMap<String, String>?,
  ): R {
    println("Knowledge Invoke 5 $name")
    TODO("Not yet implemented")
  }

  override fun <P : IBaseParameters?, I : IIdType?> invoke(
    id: I,
    name: String?,
    parameters: P,
    headers: MutableMap<String, String>?,
  ): MethodOutcome {
    println("Knowledge Invoke 6 $name")
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
