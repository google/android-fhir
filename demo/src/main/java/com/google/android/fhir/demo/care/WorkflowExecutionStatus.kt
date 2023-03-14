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

package com.google.android.fhir.demo.care

import java.time.OffsetDateTime

sealed class WorkflowExecutionStatus {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  /** Workflow execution has been started on the client. */
  class Started(val total: Int) : WorkflowExecutionStatus()

  /** Workflow execution in progress. */
  data class InProgress(val completed: Int = 0) : WorkflowExecutionStatus()

  /** Workflow execution finished successfully. */
  class Finished : WorkflowExecutionStatus()

  /** Workflow execution failed. */
  data class Failed(val exceptions: List<WorkflowExecutionException>) : WorkflowExecutionStatus()
}
