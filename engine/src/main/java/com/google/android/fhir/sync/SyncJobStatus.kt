/*
 * Copyright 2022-2024 Google LLC
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

/**
 * Data class representing the state of a periodic synchronization operation. It is a combined state
 * of [WorkInfo.State] and [SyncJobStatus]. See [CurrentSyncJobStatus] and [LastSyncJobStatus] for
 * more details.
 *
 * @property lastSyncJobStatus The result of the last synchronization job [LastSyncJobStatus]. It
 *   only represents terminal states.
 * @property currentSyncJobStatus The current state of the synchronization job
 *   [CurrentSyncJobStatus].
 */
data class PeriodicSyncJobStatus(
  val lastSyncJobStatus: LastSyncJobStatus?,
  val currentSyncJobStatus: CurrentSyncJobStatus,
)

/**
 * Sealed class representing the result of a synchronization operation. These are terminal states of
 * the sync operation, representing [Succeeded] and [Failed].
 *
 * @property timestamp The timestamp when the synchronization result occurred.
 */
sealed class LastSyncJobStatus(val timestamp: OffsetDateTime) {
  /** Represents a successful synchronization result. */
  class Succeeded(timestamp: OffsetDateTime) : LastSyncJobStatus(timestamp)

  /** Represents a failed synchronization result. */
  class Failed(timestamp: OffsetDateTime) : LastSyncJobStatus(timestamp)
}

/**
 * Sealed class representing different states of a synchronization operation. It combines
 * [WorkInfo.State] and [SyncJobStatus]. Enqueued state represents [WorkInfo.State.ENQUEUED] where
 * [SyncJobStatus] is not applicable. Running state is a combined state of [WorkInfo.State.ENQUEUED]
 * and [SyncJobStatus.Started] or [SyncJobStatus.InProgress]. Succeeded state is a combined state of
 * [WorkInfo.State.SUCCEEDED] and [SyncJobStatus.Started] or [SyncJobStatus.Succeeded]. Failed state
 * is a combined state of [WorkInfo.State.FAILED] and [SyncJobStatus.Failed]. Cancelled state
 * represents [WorkInfo.State.CANCELLED] where [SyncJobStatus] is not applicable.
 */
sealed class CurrentSyncJobStatus {
  /** State indicating that the synchronization operation is enqueued. */
  object Enqueued : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation is running.
   *
   * @param inProgressSyncJob The current status of the synchronization job.
   */
  data class Running(val inProgressSyncJob: SyncJobStatus) : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation succeeded.
   *
   * @param timestamp The timestamp when the synchronization result occurred.
   */
  class Succeeded(val timestamp: OffsetDateTime) : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation failed.
   *
   * @param timestamp The timestamp when the synchronization result occurred.
   */
  class Failed(val timestamp: OffsetDateTime) : CurrentSyncJobStatus()

  /** State indicating that the synchronization operation is canceled. */
  object Cancelled : CurrentSyncJobStatus()

  /** State indicating that the synchronization operation is blocked. */
  data object Blocked : CurrentSyncJobStatus()
}

/**
 * Sealed class representing different states of a synchronization operation. These states do not
 * represent [WorkInfo.State], whereas [CurrentSyncJobStatus] combines [WorkInfo.State] and
 * [SyncJobStatus] in one-time and periodic sync. For more details, see [CurrentSyncJobStatus] and
 * [PeriodicSyncJobStatus].
 */
sealed class SyncJobStatus {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  /** Sync job has been started on the client but the syncing is not necessarily in progress. */
  class Started : SyncJobStatus()

  /** Syncing in progress with the server. */
  data class InProgress(
    val syncOperation: SyncOperation,
    val total: Int = 0,
    val completed: Int = 0,
  ) : SyncJobStatus()

  /** Sync job finished successfully. */
  class Succeeded : SyncJobStatus()

  /** Sync job failed. */
  data class Failed(val exceptions: List<ResourceSyncException>) : SyncJobStatus()

  /** Helper class for serializing and deserializing [SyncJobStatus] objects. */
  internal class SyncJobStatusSerializer {
    private val serializer =
      GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter().nullSafe())
        .setExclusionStrategies(FhirSyncWorker.StateExclusionStrategy())
        .create()

    private val allowedSyncJobStatusPackages =
      listOf(
        AllowedSyncJobStatus.SUCCEEDED.allowedPackage,
        AllowedSyncJobStatus.FAILED.allowedPackage,
      )

    /**
     * Deserializes the given data string into a [SyncJobStatus] object.
     *
     * @param data The serialized data string.
     * @return The deserialized [SyncJobStatus] object, or null if the deserialization fails or the
     *   data is not of an allowed class.
     */
    fun deserialize(data: String?): SyncJobStatus? {
      return serializer.fromJson(data, Data::class.java)?.let {
        val stateType = it.getString(STATE_TYPE)
        val stateData = it.getString(STATE)
        if (stateType?.isAllowedClass() == true) {
          stateData?.let { stateData ->
            serializer.fromJson(stateData, Class.forName(stateType)) as? SyncJobStatus
          }
        } else {
          error("Corrupt state type : $stateType")
        }
      }
    }

    /**
     * Serializes the given [SyncJobStatus] object into a JSON-formatted string.
     *
     * @param syncJobStatus The [SyncJobStatus] object to serialize.
     * @return The JSON-formatted string representing the serialized [SyncJobStatus] object.
     */
    fun serialize(syncJobStatus: SyncJobStatus): String {
      val data =
        workDataOf(
          STATE_TYPE to syncJobStatus::class.java.name,
          STATE to serializer.toJson(syncJobStatus),
        )
      return serializer.toJson(data)
    }

    private fun String.isAllowedClass(): Boolean {
      return allowedSyncJobStatusPackages.any { this.startsWith(it) }
    }

    private companion object {
      private const val STATE_TYPE = "STATE_TYPE"
      private const val STATE = "STATE"
    }
  }

  private enum class AllowedSyncJobStatus(val allowedPackage: String) {
    SUCCEEDED(SyncJobStatus.Succeeded::class.java.name),
    FAILED(SyncJobStatus.Failed::class.java.name),
  }
}
