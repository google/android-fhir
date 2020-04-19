package com.google.fhirengine.db.roomimpl

import androidx.room.TypeConverter
import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.r4.model.ResourceType

object DbTypeConverters {
    private val resourceTypeLookup = ResourceType.values().associateBy { it.name }
    @JvmStatic
    @TypeConverter
    fun typeToString(resourceType: ResourceType) = resourceType.name

    @JvmStatic
    @TypeConverter
    fun stringToResourceType(data : String) = resourceTypeLookup[data]
            ?: throw IllegalArgumentException("invalid resource type: $data")
}