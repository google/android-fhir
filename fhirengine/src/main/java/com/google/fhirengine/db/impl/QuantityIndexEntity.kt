package com.google.fhirengine.db.impl

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.fhirengine.index.QuantityIndex
import org.hl7.fhir.r4.model.ResourceType

@Entity(
        indices = [
            Index(
                    value = ["resourceType", "index_name", "index_value"]
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
internal data class QuantityIndexEntity (
        @PrimaryKey(autoGenerate = true)
        val id: Long,
        val resourceType: ResourceType,
        val resourceId: String,
        @Embedded(prefix = "index_")
        val index : QuantityIndex
)