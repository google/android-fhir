package com.google.fhirengine.db.roomimpl

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

@Entity(
        indices = [
            Index(
                    value = ["resourceType", "resourceId"],
                    unique = true
            )
        ]
)
data class ResourceEntity(
        @PrimaryKey(
                autoGenerate = true
        )
        val id: Long,
        val resourceType: ResourceType,
        val resourceId: String,
        val serializedResource: String
)

@Entity(
        indices = [
            Index(
                    value = ["resourceType", "indexName", "indexValue"]
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
data class StringIndexEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val resourceType: ResourceType,
        val indexName: String,
        val indexPath: String,
        val indexValue: String,
        val resourceId: String
)

/**
 * TODO not sure why StringIndexEntity and ReferenceIndexEntity are two separate tables
 */
@Entity(
        indices = [
            Index(
                    value = ["resourceType", "indexName", "indexValue"]
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
data class ReferenceIndexEntitiy(
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val resourceType: ResourceType,
        val indexName: String,
        val indexPath: String,
        val indexValue: String,
        val resourceId: String
)