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

package com.google.fhirengine.db.impl

import androidx.room.TypeConverter
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.math.BigDecimal
import java.util.Calendar
import org.hl7.fhir.r4.model.ResourceType

/**
 * Type converters for Room to persist ResourceType as a string.
 * see: https://developer.android.com/training/data-storage/room/referencing-data
 */
internal object DbTypeConverters {
    private val resourceTypeLookup = ResourceType.values().associateBy { it.name }

    /**
     * Converts a [ResourceType] into a String to be persisted in the database. This allows us to
     * save [ResourceType] into the database while keeping it as the real type in entities.
     */
    @JvmStatic
    @TypeConverter
    fun typeToString(resourceType: ResourceType) = resourceType.name

    /**
     * Converts a String into a [ResourceType]. Called when a query returns a [ResourceType].
     */
    @JvmStatic
    @TypeConverter
    fun stringToResourceType(data: String) = resourceTypeLookup[data]
        ?: throw IllegalArgumentException("invalid resource type: $data")

    @JvmStatic
    @TypeConverter
    fun bigDecimalToString(value: BigDecimal): String = value.toString()

    @JvmStatic
    @TypeConverter
    fun stringToBigDecimal(value: String): BigDecimal = value.toBigDecimal()

    @JvmStatic
    @TypeConverter
    fun temporalPrecisionToInt(temporalPrecision: TemporalPrecisionEnum): Int =
        temporalPrecision.calendarConstant

    @JvmStatic
    @TypeConverter
    fun intToTemporalPrecision(intTp: Int): TemporalPrecisionEnum {
        return when (intTp) {
            Calendar.YEAR -> TemporalPrecisionEnum.YEAR
            Calendar.MONTH -> TemporalPrecisionEnum.MONTH
            Calendar.DATE -> TemporalPrecisionEnum.DAY
            Calendar.MINUTE -> TemporalPrecisionEnum.MINUTE
            Calendar.SECOND -> TemporalPrecisionEnum.SECOND
            Calendar.MILLISECOND -> TemporalPrecisionEnum.MILLI
            else -> throw IllegalArgumentException("Unknown TemporalPrecision int $intTp")
        }
    }
}
