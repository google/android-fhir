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

package com.google.android.fhir

import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import org.hl7.fhir.r4.model.Resource

/** Data class for squashed local changes for resource */
data class LocalChange(
  /** The [ResourceType] */
  val resourceType: String,
  /** The resource id [Resource.id] */
  val resourceId: String,
  /** last updated timestamp */
  val timestamp: String = "",
  /** Type of local change like insert, delete, etc */
  // TODO extract Type enum from LocalChangeEntity and make LocalChangeEntity as internal class
  val type: LocalChangeEntity.Type,
  /** json string with local changes */
  val payload: String,
  /** last udated vesrion for resource */
  val versionId: String? = null
)
