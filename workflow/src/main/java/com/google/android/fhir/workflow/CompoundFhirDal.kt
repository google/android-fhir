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

import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal

class CompoundFhirDal(private val providers: List<FhirDal>) : FhirDal {
  override fun read(id: IIdType): IBaseResource? = providers.firstNotNullOfOrNull { it.read(id) }

  // TODO: inserting to the first provider. Is it correct?
  override fun create(resource: IBaseResource) = providers.first().create(resource)

  override fun update(resource: IBaseResource) = providers.first().update(resource)

  override fun delete(id: IIdType) = providers.first().delete(id)

  override fun search(resourceType: String): Iterable<IBaseResource> =
    providers.flatMap { it.search(resourceType) }

  override fun searchByUrl(resourceType: String, url: String): Iterable<IBaseResource> =
    providers.flatMap { it.searchByUrl(resourceType, url) }
}
