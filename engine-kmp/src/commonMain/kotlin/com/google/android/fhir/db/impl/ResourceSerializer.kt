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

package com.google.android.fhir.db.impl

import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Resource

/**
 * Singleton FHIR JSON parser for serializing/deserializing resources. Replaces HAPI's
 * `FhirContext.forR4Cached().newJsonParser()`. Thread-safe and reusable.
 */
internal val fhirJsonParser = FhirR4Json()

/** Serializes a FHIR [Resource] to a JSON string for database storage. */
internal fun serializeResource(resource: Resource): String = fhirJsonParser.encodeToString(resource)

/**
 * Deserializes a JSON string back to a FHIR [Resource]. Polymorphic deserialization is handled
 * automatically via the `"resourceType"` JSON field.
 */
internal fun deserializeResource(json: String): Resource = fhirJsonParser.decodeFromString(json)
