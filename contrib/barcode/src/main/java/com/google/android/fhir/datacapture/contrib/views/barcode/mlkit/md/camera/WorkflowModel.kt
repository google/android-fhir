/*
 * Copyright 2021-2023 Google LLC
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

package com.google.android.fhir.datacapture.contrib.views.barcode.mlkit.md.camera

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.barcode.Barcode
import java.util.HashSet

/** View model for handling application workflow based on camera preview. */
class WorkflowModel(application: Application) : AndroidViewModel(application) {

  val workflowState = MutableLiveData<WorkflowState>()
  val detectedBarcode = MutableLiveData<Barcode>()

  private val objectIdsToSearch = HashSet<Int>()

  var isCameraLive = false
    private set

  private val context: Context
    get() = getApplication<Application>().applicationContext

  /** State set of the application workflow. */
  enum class WorkflowState {
    NOT_STARTED,
    DETECTING,
    DETECTED,
    CONFIRMING,
    CONFIRMED,
    SEARCHING,
    SEARCHED,
  }

  @MainThread
  fun setWorkflowState(workflowState: WorkflowState) {
    this.workflowState.value = workflowState
  }

  fun markCameraLive() {
    isCameraLive = true
    objectIdsToSearch.clear()
  }

  fun markCameraFrozen() {
    isCameraLive = false
  }
}
