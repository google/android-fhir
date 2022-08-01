/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.json.resource

data class Task(
  private val id: String? = null,
  private val type: TaskType? = null,
  private val priority: Int? = null,
  private val requester: String? = null,

  // Entity on behalf of which the task was created.
  private val requestedFor: String? = null,

  // Current assignee of the task.
  private val assignee: String? = null,

  // Actors associated with task.
  private val associatedActors: List<String>? = null,

  // Primary beneficiary of the task.
  private val forEntity: Entity? = null,

  // Primary reason for task.
  private val reason: String? = null,

  // Optional tags for task.
  private val tags: List<String>? = null,

  // Location where task was executed.
  private val location: String? = null,

  // Timestamp when task was created on the server.
  private val createdTime: String? = null,

  // Time in the future when the task becomes effective.
  private val scheduled: String? = null,
  private val lastUpdatedTime: String? = null,

  // Frequency of repetition of the task.
  private val periodicity: String? = null,

  // Machine-readable business status.
  private val businessStatus: String? = null,

  // Human-readable business status.
  private val businessStatusReason: String? = null,

  // Free form JSON to store the state of task for processing.
  private val context: String? = null,

  // Free form JSON to store inputs.
  private val data: String? = null,
  private val stage: Stage? = null,
  private val status: Status? = null,

  // Human-readable task status.
  private val statusReason: String? = null
) {

  enum class Stage {
    STAGE_UNSPECIFIED,
    CREATED,
    INITIALIZED,
    READY,
    ENDED;

    companion object {
      fun parse(stage: String?): Stage {
        val enumStage: Stage
        enumStage =
          try {
            valueOf(stage!!)
          } catch (e: IllegalArgumentException) {
            STAGE_UNSPECIFIED
          }
        return enumStage
      }
    }
  }

  enum class Status {
    STATUS_UNSPECIFIED,
    WAITING,
    SUCCEEDED,
    FAILED,
    ABORTED;

    companion object {
      fun parse(status: String?): Status {
        val enumStatus: Status
        enumStatus =
          try {
            valueOf(status!!)
          } catch (e: IllegalArgumentException) {
            STATUS_UNSPECIFIED
          }
        return enumStatus
      }
    }
  }

  enum class Entity {
    ENTITY_UNSPECIFIED,
    BENEFICIARY,
    FAMILY,
    FACILITY;

    companion object {
      fun parse(entity: String?): Entity {
        val enumEntity: Entity
        enumEntity =
          try {
            valueOf(entity!!)
          } catch (e: IllegalArgumentException) {
            ENTITY_UNSPECIFIED
          }
        return enumEntity
      }
    }
  }
}
