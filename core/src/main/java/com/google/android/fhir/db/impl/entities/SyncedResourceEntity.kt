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

package com.google.android.fhir.db.impl.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hl7.fhir.r4.model.ResourceType

/**
 * Class that models a table that holds all resource types that were synced and the highest
 * `_lastUpdate` value of each resource type.
 */
@Entity
data class SyncedResourceEntity(
  /** Resource synced */
  @PrimaryKey val resourceType: ResourceType,
  /** The highest `_lastUpdate` value of the resources synced of a specific type */
  val lastUpdate: String
)
