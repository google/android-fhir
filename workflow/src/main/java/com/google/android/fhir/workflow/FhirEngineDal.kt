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

import ca.uhn.fhir.rest.gclient.UriClientParam
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.getResourceType
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.search.Search
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal
import timber.log.Timber

internal class FhirEngineDal(
  private val fhirEngine: FhirEngine,
  private val knowledgeManager: KnowledgeManager,
) : FhirDal {

  override fun read(id: IIdType): IBaseResource = runBlockingOrThrowMainThreadException {
    val clazz = id.getResourceClass()
    if (id.isAbsolute) {
      knowledgeManager
        .loadResources(
          resourceType = id.resourceType,
          url = "${id.baseUrl}/${id.resourceType}/${id.idPart}"
        )
        .single()
    } else {
      try {
        fhirEngine.get(getResourceType(clazz), id.idPart)
      } catch (resourceNotFoundException: ResourceNotFoundException) {
        // Searching by resourceType and Id to workaround
        // https://github.com/google/android-fhir/issues/1920
        // remove when the issue is resolved.
        val searchByNameWorkaround =
          knowledgeManager.loadResources(resourceType = id.resourceType, id = id.toString())
        if (searchByNameWorkaround.count() > 1) {
          Timber.w("Found more than one value in the IgManager for the id $id")
        }
        searchByNameWorkaround.firstOrNull() ?: throw resourceNotFoundException
      }
    }
  }

  override fun create(resource: IBaseResource): Unit = runBlockingOrThrowMainThreadException {
    fhirEngine.create(resource as Resource)
  }

  override fun update(resource: IBaseResource) = runBlockingOrThrowMainThreadException {
    fhirEngine.update(resource as Resource)
  }

  override fun delete(id: IIdType) = runBlockingOrThrowMainThreadException {
    val clazz = id.getResourceClass()
    fhirEngine.delete(getResourceType(clazz), id.idPart)
  }

  override fun search(resourceType: String): Iterable<IBaseResource> =
    runBlockingOrThrowMainThreadException {
      val search = Search(type = ResourceType.fromCode(resourceType))
      knowledgeManager.loadResources(resourceType = resourceType) + fhirEngine.search(search)
    }

  override fun searchByUrl(resourceType: String, url: String): Iterable<IBaseResource> =
    runBlockingOrThrowMainThreadException {
      val search = Search(type = ResourceType.fromCode(resourceType))
      search.filter(UriClientParam("url"), { value = url })
      // Searching for knowledge artifact, no need to lookup for fhirEngine
      knowledgeManager.loadResources(resourceType = resourceType, url = url)
    }

  @Suppress("UNCHECKED_CAST")
  private fun IIdType.getResourceClass(): Class<Resource> {
    try {
      return Class.forName("org.hl7.fhir.r4.model.$resourceType") as Class<Resource>
    } catch (exception: ClassNotFoundException) {
      throw IllegalArgumentException("invalid resource type : $resourceType", exception)
    } catch (exception: ClassCastException) {
      throw IllegalArgumentException("invalid resource type : $resourceType", exception)
    }
  }
}
