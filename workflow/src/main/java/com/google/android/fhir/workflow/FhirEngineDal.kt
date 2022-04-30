/*
 * Copyright 2021 Google LLC
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

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.getResourceType
import com.google.android.fhir.search.search
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Measure
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal

class FhirEngineDal(private val fhirEngine: FhirEngine) : FhirDal {
  val libs = mutableMapOf<String, Library>()

  override fun read(id: IIdType): IBaseResource {
    val clazz = id.getResourceClass()

    return runBlocking { fhirEngine.get(getResourceType(clazz), id.idPart) as IBaseResource }
  }

  override fun create(resource: IBaseResource) {
    runBlocking { fhirEngine.create(resource as Resource) }
  }

  override fun update(resource: IBaseResource) {
    runBlocking { fhirEngine.update(resource as Resource) }
  }

  override fun delete(id: IIdType) {
    runBlocking {
      val clazz = id.getResourceClass()
      runBlocking { fhirEngine.delete(getResourceType(clazz), id.idPart) }
    }
  }

  override fun search(resourceType: String): Iterable<IBaseResource> {
    return runBlocking {
      when (resourceType) {
        "Patient" -> fhirEngine.search<Patient> {}.toMutableList()
        else -> throw NotImplementedError("Not yet implemented")
      }
    }
  }

  override fun searchByUrl(resourceType: String, url: String): Iterable<IBaseResource> {
    return runBlocking {
      when (resourceType) {
        "Measure" -> fhirEngine.search<Measure> { filter(Measure.URL, { value = url }) }
        "Library" -> listOf(libs[url] as Library)
        else -> listOf()
      }.toMutableList()
    }
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
