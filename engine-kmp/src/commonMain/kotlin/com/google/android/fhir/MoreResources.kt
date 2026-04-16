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

package com.google.android.fhir

import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.terminologies.ResourceType
import kotlin.reflect.KClass
import kotlinx.datetime.Instant

/**
 * Resource type registry mapping KClass to ResourceType. This replaces JVM reflection
 * (Class.forName) used in the original engine module with a KMP-compatible lookup.
 *
 * Note: This registry is populated with common resource types. Additional types can be registered
 * via [registerResourceType].
 */
private val resourceTypeRegistry = mutableMapOf<KClass<out Resource>, ResourceType>()

private val resourceTypeNameRegistry = mutableMapOf<String, KClass<out Resource>>()

/** Registers a resource KClass with its corresponding [ResourceType]. */
fun registerResourceType(kClass: KClass<out Resource>, type: ResourceType) {
  resourceTypeRegistry[kClass] = type
  resourceTypeNameRegistry[type.name] = kClass
}

/**
 * Returns the FHIR [ResourceType] for the given resource [KClass].
 *
 * @throws IllegalArgumentException if the class is not registered in the resource type registry
 */
fun getResourceType(kClass: KClass<out Resource>): ResourceType =
  resourceTypeRegistry[kClass]
    ?: throw IllegalArgumentException(
      "Cannot resolve resource type for ${kClass.simpleName}. " +
        "Register it with registerResourceType() first.",
    )

/**
 * Returns the [KClass] for the given resource type name.
 *
 * @throws IllegalArgumentException if the resource type name is not registered
 */
fun getResourceClass(resourceType: String): KClass<out Resource> {
  val className = resourceType.replace(Regex("\\{[^}]*\\}"), "")
  return resourceTypeNameRegistry[className]
    ?: throw IllegalArgumentException(
      "Cannot resolve resource class for $className. " +
        "Register it with registerResourceType() first.",
    )
}

/** Returns the [KClass] for the given [ResourceType]. */
fun getResourceClass(resourceType: ResourceType): KClass<out Resource> =
  getResourceClass(resourceType.name)

/**
 * Returns the FHIR resource type name string for this [Resource] (e.g., "Patient", "Observation").
 *
 * This uses the Kotlin class simple name which matches the FHIR resource type name for all
 * kotlin-fhir model classes.
 */
internal val Resource.resourceType: String
  get() = this::class.simpleName ?: error("Cannot determine resource type for $this")

internal val Resource.versionId: String?
  get() = meta?.versionId?.value

internal val Resource.lastUpdated: Instant?
  get() = meta?.lastUpdated?.value?.toString()?.let { Instant.parse(it) }
