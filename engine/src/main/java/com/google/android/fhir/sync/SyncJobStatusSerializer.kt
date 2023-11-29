/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.sync

import androidx.work.Data
import androidx.work.workDataOf
import com.google.android.fhir.OffsetDateTimeTypeAdapter
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime

/** Helper class for serializing and deserializing [SyncJobStatus] objects. */
internal class SyncJobStatusSerializer {
  private val serializer =
    GsonBuilder()
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter().nullSafe())
      .setExclusionStrategies(FhirSyncWorker.StateExclusionStrategy())
      .create()

  private val allowedSyncJobStatusPackages =
    listOf(
      "com.google.android.fhir.sync.SyncJobStatus\$Finished",
      "com.google.android.fhir.sync.SyncJobStatus\$Failed",
    )

  /**
   * Deserializes the given data string into a [SyncJobStatus] object.
   *
   * @param data The serialized data string.
   * @return The deserialized [SyncJobStatus] object, or null if the deserialization fails or the
   *   data is not of an allowed class.
   */
  fun deserialize(data: String?): SyncJobStatus {
    return serializer.fromJson(data, Data::class.java)?.let {
      val stateType = it.getString(STATE_TYPE)
      val stateData = it.getString(STATE)
      if (stateType?.isAllowedClass() == true) {
        stateData?.let { stateData ->
          serializer.fromJson(stateData, Class.forName(stateType)) as? SyncJobStatus
        }
      } else {
        SyncJobStatus.Unknown
      }
    }
      ?: SyncJobStatus.Unknown
  }

  /**
   * Serializes the given [SyncJobStatus] object into a JSON-formatted string.
   *
   * @param syncJobStatus The [SyncJobStatus] object to serialize.
   * @return The JSON-formatted string representing the serialized [SyncJobStatus] object.
   */
  fun serialize(syncJobStatus: SyncJobStatus?): String {
    val data =
      syncJobStatus?.let {
        workDataOf(
          STATE_TYPE to it::class.java.name,
          STATE to serializer.toJson(it),
        )
      }
    return serializer.toJson(data)
  }

  private fun String.isAllowedClass(): Boolean {
    return allowedSyncJobStatusPackages.any { this.startsWith(it) }
  }

  companion object {
    internal const val STATE_TYPE = "STATE_TYPE"
    internal const val STATE = "STATE"
  }
}
