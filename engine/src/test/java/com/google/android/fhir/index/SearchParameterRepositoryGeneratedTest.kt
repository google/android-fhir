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

package com.google.android.fhir.index

import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SearchParameterRepositoryGeneratedTest(private val resource: Resource) {
  @Test
  fun testResources() {
    assertThat(getSearchParamList(resource))
      .containsExactlyElementsIn(getSearchParamListReflection(resource))
  }

  private fun getSearchParamListReflection(resource: Resource): MutableList<SearchParamDefinition> {
    val searchParams = getBaseSearchParameters()
    searchParams.addAll(
      resource.javaClass.fields
        .asSequence()
        .mapNotNull {
          it.getAnnotation(ca.uhn.fhir.model.api.annotation.SearchParamDefinition::class.java)
        }
        .filter { it.path.isNotEmpty() }
        .map {
          SearchParamDefinition(
            it.name,
            Enumerations.SearchParamType.valueOf(it.type.toUpperCase()),
            it.path
          )
        }
        .toMutableList()
    )

    return searchParams
  }

  // We are adding these manually because they don't exists in HAPI FHIR java classes
  private fun getBaseSearchParameters(): MutableList<SearchParamDefinition> {
    val searchParams = mutableListOf<SearchParamDefinition>()
    searchParams.add(
      SearchParamDefinition(
        "_id",
        Enumerations.SearchParamType.TOKEN,
        "${resource.resourceType.name}.id"
      )
    )
    searchParams.add(
      SearchParamDefinition(
        "_lastUpdated",
        Enumerations.SearchParamType.DATE,
        "${resource.resourceType.name}.meta.lastUpdated"
      )
    )
    searchParams.add(
      SearchParamDefinition(
        "_profile",
        Enumerations.SearchParamType.URI,
        "${resource.resourceType.name}.meta.profile"
      )
    )
    searchParams.add(
      SearchParamDefinition(
        "_security",
        Enumerations.SearchParamType.TOKEN,
        "${resource.resourceType.name}.meta.security"
      )
    )
    searchParams.add(
      SearchParamDefinition(
        "_source",
        Enumerations.SearchParamType.URI,
        "${resource.resourceType.name}.meta.source"
      )
    )
    searchParams.add(
      SearchParamDefinition(
        "_tag",
        Enumerations.SearchParamType.TOKEN,
        "${resource.resourceType.name}.meta.tag"
      )
    )

    return searchParams
  }

  private companion object {
    @Parameterized.Parameters @JvmStatic fun data(): List<Resource> = getAllResources()
  }
}
