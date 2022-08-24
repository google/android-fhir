package com.google.android.fhir.db.impl.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Fts4
import com.google.android.fhir.index.entities.StringIndex

@Fts4(contentEntity = StringIndexEntity::class)
@Entity
internal data class StringIndexEntityFts(@Embedded(prefix = "index_") val index: StringIndex)