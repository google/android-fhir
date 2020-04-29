// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.db.impl

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hl7.fhir.r4.model.ResourceType

/**
 * TODO not sure why StringIndexEntity and ReferenceIndexEntity are two separate tables
 */
@Entity(
        indices = [
            Index(
                    value = ["resourceType", "indexName", "indexValue"]
            ),
            Index(
                    // keep this index for faster foreign lookup
                    value = ["resourceId", "resourceType"]
            )
        ],
        foreignKeys = [
            ForeignKey(
                    entity = ResourceEntity::class,
                    parentColumns = ["resourceId", "resourceType"],
                    childColumns = ["resourceId", "resourceType"],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.NO_ACTION,
                    deferred = true
            )
        ]
)
data class ReferenceIndexEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val resourceType: ResourceType,
        val indexName: String,
        val indexPath: String,
        val indexValue: String,
        val resourceId: String
)
