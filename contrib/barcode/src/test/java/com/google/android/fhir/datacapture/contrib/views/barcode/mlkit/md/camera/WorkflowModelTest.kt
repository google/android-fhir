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

import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import java.util.HashSet
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers.getField

@RunWith(RobolectricTestRunner::class)
class WorkflowModelTest {

  private lateinit var workflowModel: WorkflowModel

  @Before
  fun setUp() {
    workflowModel = WorkflowModel(ApplicationProvider.getApplicationContext())
  }

  @Test
  fun shouldSet_workFlowStates() {
    // initial state should be null
    assertThat(workflowModel.workflowState.value).isNull()

    workflowModel.setWorkflowState(WorkflowModel.WorkflowState.CONFIRMED)
    assertThat(workflowModel.workflowState.value).isEqualTo(WorkflowModel.WorkflowState.CONFIRMED)
  }

  @Test
  fun markCameraLive_shouldAlsoClear_objectIds() {
    val objectIdsToSearch = getField<HashSet<Int>>(workflowModel, "objectIdsToSearch")

    // initially it should be empty and camera should not be live
    assertThat(objectIdsToSearch).isEmpty()
    assertThat(getField<Boolean>(workflowModel, "isCameraLive")).isFalse()

    // add dummy search ids
    objectIdsToSearch.add(10)
    objectIdsToSearch.add(20)

    assertThat(objectIdsToSearch).hasSize(2)

    // mark camera live
    workflowModel.markCameraLive()

    // all search ids should be clear and camera should be live
    assertThat(objectIdsToSearch).isEmpty()
    assertThat(getField<Boolean>(workflowModel, "isCameraLive")).isTrue()
  }

  @Test
  fun markCameraFrozen_shouldSetFalse_WhateverItIs() {
    workflowModel.markCameraFrozen()
    assertThat(getField<Boolean>(workflowModel, "isCameraLive")).isFalse()
  }
}
