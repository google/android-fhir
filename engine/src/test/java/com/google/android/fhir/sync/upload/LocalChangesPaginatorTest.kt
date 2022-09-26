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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalChangesPaginatorTest {

  @Test
  fun `SizeSplitter split should return 3 lists in order`() {
    val changes =
      listOf(
        LocalChange(
          ResourceType.Patient.name,
          "patient-001",
          type = LocalChange.Type.INSERT,
          payload = "{}",
          token = LocalChangeToken(listOf(1L))
        ),
        LocalChange(
          ResourceType.Patient.name,
          "patient-002",
          type = LocalChange.Type.INSERT,
          payload = "{}",
          token = LocalChangeToken(listOf(2L))
        ),
        LocalChange(
          ResourceType.Patient.name,
          "patient-003",
          type = LocalChange.Type.INSERT,
          payload = "{}",
          token = LocalChangeToken(listOf(3L))
        ),
        LocalChange(
          ResourceType.Patient.name,
          "patient-004",
          type = LocalChange.Type.INSERT,
          payload = "{}",
          token = LocalChangeToken(listOf(4L))
        ),
        LocalChange(
          ResourceType.Patient.name,
          "patient-005",
          type = LocalChange.Type.INSERT,
          payload = "{}",
          token = LocalChangeToken(listOf(5L))
        )
      )

    val result = SizeBasedLocalChangesPaginator(2).page(changes)
    assertThat(result).hasSize(3)
    assertThat(result[0].map { it.resourceId }).containsExactly("patient-001", "patient-002")
    assertThat(result[1].map { it.resourceId }).containsExactly("patient-003", "patient-004")
    assertThat(result[2].map { it.resourceId }).containsExactly("patient-005")
  }

  @Test
  fun `SizeSplitter split should return empty lists when input its empty`() {
    val result = SizeBasedLocalChangesPaginator(2).page(emptyList())
    assertThat(result).hasSize(0)
  }
}
