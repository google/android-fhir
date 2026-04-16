/*
 * Copyright 2025-2026 Google LLC
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

import com.google.fhir.model.r4.Resource

/** Provides a list of [SearchParamDefinition]s for a [Resource]. */
internal fun interface SearchParamDefinitionsProvider {

  /** @return [SearchParamDefinition]s based on the resource type name. */
  fun get(resource: Resource): List<SearchParamDefinition>
}

/**
 * An implementation of [SearchParamDefinitionsProvider] that provides the [List]<
 * [SearchParamDefinition]> from the default params and custom params(if any).
 */
internal class SearchParamDefinitionsProviderImpl(
  private val defaultParams: Map<String, List<SearchParamDefinition>> = emptyMap(),
  private val customParams: Map<String, List<SearchParamDefinition>> = emptyMap(),
) : SearchParamDefinitionsProvider {

  override fun get(resource: Resource): List<SearchParamDefinition> {
    val resourceTypeName = resource::class.simpleName ?: return emptyList()
    return defaultParams.getOrElse(resourceTypeName) { emptyList() } +
      customParams.getOrElse(resourceTypeName) { emptyList() }
  }
}
