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

/** Currently only Started and Finished statuses are being used. */
sealed class CareWorkflowExecutionStatus {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  /** Workflow execution has been started on the client. */
  class Started(val total: Int) : CareWorkflowExecutionStatus()

  /** Workflow execution in progress. */
  class InProgress : CareWorkflowExecutionStatus()

  /** Workflow execution finished successfully. */
  class Finished(val completed: Int = 0, val total: Int = 0) : CareWorkflowExecutionStatus()

  /** Workflow execution failed. */
  data class Failed(val exceptions: List<CareWorkflowExecutionException>) :
    CareWorkflowExecutionStatus()
}
