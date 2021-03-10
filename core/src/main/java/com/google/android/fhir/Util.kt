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

package com.google.android.fhir

import android.annotation.SuppressLint
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import org.hl7.fhir.r4.model.Resource

/**
 * Utility function to format a [Date] object using the system's default locale.
 */
@SuppressLint("NewApi")
internal fun Date.toTimeZoneString(): String {
    val simpleDateFormat = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        Locale.getDefault()
    ).withZone(ZoneId.systemDefault())
    return simpleDateFormat.format(this.toInstant())
}

/**
 * Returns only the logical ID part of this Resource's ID. For example, if the ID is
 * "http://example.com/fhir/Patient/123/_history/456", then this method would return "123".
 */
internal fun Resource.logicalId(): String = this.idElement.idPart
