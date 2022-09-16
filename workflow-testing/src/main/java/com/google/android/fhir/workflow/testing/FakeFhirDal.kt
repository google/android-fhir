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

package com.google.android.fhir.workflow.testing

import java.util.ArrayList
import java.util.HashMap
import java.util.function.Consumer
import java.util.stream.Collectors
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.MetadataResource
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal

class FakeFhirDal : FhirDal {
  private val cacheById: MutableMap<String, IBaseResource> = HashMap()
  private val cacheByURL: MutableMap<String, MutableList<IBaseResource>> = HashMap()
  private val cacheByType: MutableMap<String, MutableList<IBaseResource>> = HashMap()

  private fun toKey(resource: IIdType): String {
    return resource.resourceType + "/" + resource.idPart
  }

  private fun insertOrUpdate(
    list: MutableMap<String, MutableList<IBaseResource>>,
    key: String,
    element: IBaseResource
  ) {
    if (list.containsKey(key)) list[key]!!.add(element) else list[key] = ArrayList(listOf(element))
  }

  private fun putIntoCache(resource: IBaseResource) {
    cacheById[toKey(resource.idElement)] = resource
    insertOrUpdate(cacheByType, resource.idElement.resourceType, resource)
    if (resource is MetadataResource) {
      insertOrUpdate(cacheByURL, resource.url, resource)
    }
  }

  fun addAll(resource: IBaseResource) {
    if (resource is Bundle) {
      resource.entry.forEach(
        Consumer { entry: Bundle.BundleEntryComponent -> addAll(entry.resource) }
      )
    } else {
      putIntoCache(resource)
    }
  }

  override fun read(id: IIdType): IBaseResource {
    return cacheById[toKey(id)]!!
  }

  override fun create(resource: IBaseResource) {}
  override fun update(resource: IBaseResource) {}
  override fun delete(id: IIdType) {}
  override fun search(resourceType: String): Iterable<IBaseResource> {
    return cacheByType[resourceType]!!
  }

  override fun searchByUrl(resourceType: String, url: String): Iterable<IBaseResource> {
    return cacheByURL[url]!!
      .stream()
      .filter { resource: IBaseResource -> resourceType == resource.idElement.resourceType }
      .collect(Collectors.toList())
  }
}
