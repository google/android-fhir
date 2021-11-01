/*
 * Copyright 2020 Google LLC
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
    return resource
      .javaClass
      .fields
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
  }

  private companion object {
    @Parameterized.Parameters @JvmStatic fun data(): List<Resource> = getAllResources()
  }
}
