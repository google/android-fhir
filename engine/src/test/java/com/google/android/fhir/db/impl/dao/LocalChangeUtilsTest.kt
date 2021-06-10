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

package com.google.android.fhir.db.impl.dao

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.fhir.db.impl.dao.LocalChangeUtils.mergeLocalChanges
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.toTimeZoneString
import com.google.common.truth.Truth.assertThat
import java.util.Date
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Unit tests for [LocalChangeUtils]. */
@RunWith(RobolectricTestRunner::class)
class LocalChangeUtilsTest {

  @Test
  fun mergeLocalChange_checkResourceID() {
    val res1_typeDelete =
      LocalChangeEntity(
        id = 10L,
        resourceType = "Test3",
        resourceId = "Anam10",
        timestamp = Date().toTimeZoneString(),
        payload = "payload3",
        type = LocalChangeEntity.Type.DELETE
      )
    val res2_typeInsert =
      LocalChangeEntity(
        id = 30L,
        resourceType = "Test1",
        resourceId = "Anam30",
        timestamp = Date().toTimeZoneString(),
        payload = "payload1",
        type = LocalChangeEntity.Type.INSERT
      )

    val exception: Throwable =
      Assert.assertThrows(IllegalStateException::class.java) {
        mergeLocalChanges(res1_typeDelete, res2_typeInsert)
      }
    assertThat("Resource IDs Anam10 and Anam30 do not match").isEqualTo(exception.message)
  }

  // INSERT-INSERT,UPDATE,DELETE test cases
  @Test
  fun mergeLocalChanges_insert_insert() { // insert insert
    val insert_payload1 =
      LocalChangeEntity(
        id = 1L,
        resourceType = "Test1",
        resourceId = "Anam2",
        timestamp = Date().toTimeZoneString(),
        payload = "payload1",
        type = LocalChangeEntity.Type.INSERT
      )
    val insert_payload2 =
      LocalChangeEntity(
        id = 1L,
        resourceType = "Test2",
        resourceId = "Anam2",
        timestamp = Date().toTimeZoneString(),
        payload = "payload2",
        type = LocalChangeEntity.Type.INSERT
      )
    val insertPayload1_and_insertPayload2 =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test2",
        resourceId = "Anam2",
        timestamp = "",
        type = LocalChangeEntity.Type.INSERT,
        payload = "payload2"
      )
    assertThat(mergeLocalChanges(insert_payload1, insert_payload2))
      .isEqualTo(insertPayload1_and_insertPayload2)
  }

  @Test
  fun mergeLocalChanges_insert_update() { // insert update

    val payload1 =
      """
    {
      "resourceType": "Patient",
      "id": "human",
      "name": [
        {
          "use": "usual",
          "given": [
            "Kenzi"
          ]
        }
      ]
    }
    """

    val json_patch =
      """
      [
        {
         "op": "replace",
         "path": "/name/0/use",
         "value":"Ana2k"         
         }
      ]
    """.trimMargin()

    // converting the json payload to json node type
    val objectMapper = ObjectMapper()
    val payload_json_node: JsonNode = objectMapper.readTree(payload1)

    val update_json_patch =
      LocalChangeEntity(
        id = 2L,
        resourceType = "Test4",
        resourceId = "Anam5",
        timestamp = Date().toTimeZoneString(),
        payload = json_patch,
        type = LocalChangeEntity.Type.UPDATE
      )
    val insert_payload_json_node =
      LocalChangeEntity(
        id = 2L,
        resourceType = "Test5",
        resourceId = "Anam5",
        timestamp = Date().toTimeZoneString(),
        payload = payload_json_node.toString(),
        type = LocalChangeEntity.Type.INSERT
      )
    val insertPayloadJsonNode_and_UpdateJsonPatch =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test4",
        resourceId = "Anam5",
        timestamp = "",
        type = LocalChangeEntity.Type.INSERT,
        payload =
          """{"resourceType":"Patient","id":"human","name":[{"use":"Ana2k","given":["Kenzi"]}]}"""
      )
    assertThat(mergeLocalChanges(insert_payload_json_node, update_json_patch))
      .isEqualTo(insertPayloadJsonNode_and_UpdateJsonPatch)
  }

  @Test
  fun mergeLocalChanges_insert_delete() { // insert delete

    val insert_payload1 =
      LocalChangeEntity(
        id = 4L,
        resourceType = "Test1",
        resourceId = "Anam3",
        timestamp = Date().toTimeZoneString(),
        payload = "payload1",
        type = LocalChangeEntity.Type.INSERT
      )
    val delete_payload =
      LocalChangeEntity(
        id = 4L,
        resourceType = "Test3",
        resourceId = "Anam3",
        timestamp = Date().toTimeZoneString(),
        payload = "payload3",
        type = LocalChangeEntity.Type.DELETE
      )
    val insertPayload1_and_deletePayload =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test3",
        resourceId = "Anam3",
        timestamp = "",
        type = LocalChangeEntity.Type.DELETE,
        payload = ""
      )

    assertThat(mergeLocalChanges(insert_payload1, delete_payload))
      .isEqualTo(insertPayload1_and_deletePayload)
  }

  // UPDATE-INSERT,UPDATE,DELETE test cases
  @Test
  fun mergeLocalChanges_update_insert() { // update insert
    val payload1 =
      """
    {
      "resourceType": "Patient",
      "id": "human",
      "name": [
        {
          "use": "usual",
          "given": [
            "Kenzi"
          ]
        }
      ]
    }
    """

    val json_patch =
      """
      [
        {
         "op": "replace",
         "path": "/name/0/use",
         "value":"Ana2k"         
         }
      ]
    """.trimMargin()

    // converting the json payload to json node type
    val objectMapper = ObjectMapper()
    val payload_json_node: JsonNode = objectMapper.readTree(payload1)

    val update_json_patch =
      LocalChangeEntity(
        id = 4L,
        resourceType = "Test4",
        resourceId = "Anam5",
        timestamp = Date().toTimeZoneString(),
        payload = json_patch,
        type = LocalChangeEntity.Type.UPDATE
      )
    val insert_payload_json_node =
      LocalChangeEntity(
        id = 4L,
        resourceType = "Test5",
        resourceId = "Anam5",
        timestamp = Date().toTimeZoneString(),
        payload = payload_json_node.toString(),
        type = LocalChangeEntity.Type.INSERT
      )
    val UpdateJsonPatch_and_insertPayloadJsonNode =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test5",
        resourceId = "Anam5",
        timestamp = "",
        type = LocalChangeEntity.Type.INSERT,
        payload =
          """{"resourceType":"Patient","id":"human","name":[{"use":"usual","given":["Kenzi"]}]}"""
      )

    assertThat(mergeLocalChanges(update_json_patch, insert_payload_json_node))
      .isEqualTo(UpdateJsonPatch_and_insertPayloadJsonNode)
  }

  @Test
  fun mergeLocalChanges_update_update() { // update update

    val json_patch =
      """
      [
        {
         "op": "replace",
         "path": "/name/0/use",
         "value":"Ana2k"         
         }
      ]
    """.trimMargin()

    val updateJsonPatch =
      LocalChangeEntity(
        id = 5L,
        resourceType = "Test4",
        resourceId = "Anam2",
        timestamp = Date().toTimeZoneString(),
        payload = json_patch,
        type = LocalChangeEntity.Type.UPDATE
      )
    val updateJsonPatch2 =
      LocalChangeEntity(
        id = 5L,
        resourceType = "Test2",
        resourceId = "Anam2",
        timestamp = Date().toTimeZoneString(),
        payload = json_patch,
        type = LocalChangeEntity.Type.UPDATE
      )
    val updateJsonPatch_and_updateJsonPatch2 =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test2",
        resourceId = "Anam2",
        timestamp = "",
        type = LocalChangeEntity.Type.UPDATE,
        payload = """[{"op":"replace","path":"\/name\/0\/use","value":"Ana2k"}]"""
      )

    assertThat(mergeLocalChanges(updateJsonPatch, updateJsonPatch2))
      .isEqualTo(updateJsonPatch_and_updateJsonPatch2)
  }

  @Test
  fun mergeLocalChanges_update_delete() { // update delete

    val json_patch =
      """
      [
        {
         "op": "replace",
         "path": "/name/0/use",
         "value":"Ana2k"         
         }
      ]
    """.trimMargin()

    val updateJsonPatch =
      LocalChangeEntity(
        id = 6L,
        resourceType = "Test4",
        resourceId = "Anam3",
        timestamp = Date().toTimeZoneString(),
        payload = json_patch,
        type = LocalChangeEntity.Type.UPDATE
      )
    val deletePayload =
      LocalChangeEntity(
        id = 6L,
        resourceType = "Test3",
        resourceId = "Anam3",
        timestamp = Date().toTimeZoneString(),
        payload = "payload3",
        type = LocalChangeEntity.Type.DELETE
      )
    val uploadJsonPatch_deletePayload =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test3",
        resourceId = "Anam3",
        timestamp = "",
        type = LocalChangeEntity.Type.DELETE,
        payload = ""
      )

    assertThat(mergeLocalChanges(updateJsonPatch, deletePayload))
      .isEqualTo(uploadJsonPatch_deletePayload)
  }

  // DELETE-INSERT,UPDATE,DELETE test cases
  @Test
  fun mergeLocalChanges_delete_and_insert() { // delete insert

    val delete_payload =
      LocalChangeEntity(
        id = 7L,
        resourceType = "Test3",
        resourceId = "Anam1",
        timestamp = Date().toTimeZoneString(),
        payload = "payload3",
        type = LocalChangeEntity.Type.DELETE
      )
    val insert_payload1 =
      LocalChangeEntity(
        id = 7L,
        resourceType = "Test1",
        resourceId = "Anam1",
        timestamp = Date().toTimeZoneString(),
        payload = "payload1",
        type = LocalChangeEntity.Type.INSERT
      )

    val deletePayload_and_insertPayload1 =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test1",
        resourceId = "Anam1",
        timestamp = "",
        type = LocalChangeEntity.Type.INSERT,
        payload = "payload1"
      )
    assertThat(mergeLocalChanges(delete_payload, insert_payload1))
      .isEqualTo(deletePayload_and_insertPayload1)
  }

  @Test
  fun mergeLocalChanges_delete_and_update() { // delete update

    val json_patch =
      """
      [
        {
         "op": "replace",
         "path": "/name/0/use",
         "value":"Ana2k"         
         }
      ]
    """.trimMargin()

    val delete_payload =
      LocalChangeEntity(
        id = 8L,
        resourceType = "Test3",
        resourceId = "Anam3",
        timestamp = Date().toTimeZoneString(),
        payload = "payload3",
        type = LocalChangeEntity.Type.DELETE
      )
    val update_json_patch =
      LocalChangeEntity(
        id = 8L,
        resourceType = "Test4",
        resourceId = "Anam3",
        timestamp = Date().toTimeZoneString(),
        payload = json_patch,
        type = LocalChangeEntity.Type.UPDATE
      )

    val exception: Throwable =
      Assert.assertThrows(IllegalArgumentException::class.java) {
        mergeLocalChanges(delete_payload, update_json_patch)
      }
    assertThat("Cannot merge local changes with type DELETE and UPDATE.")
      .isEqualTo(exception.message)
  }

  @Test
  fun mergeLocalChanges_delete_delete() { // delete delete

    val delete_payload =
      LocalChangeEntity(
        id = 9L,
        resourceType = "Test3",
        resourceId = "Anam2",
        timestamp = Date().toTimeZoneString(),
        payload = "payload3",
        type = LocalChangeEntity.Type.DELETE
      )
    val delete_payload2 =
      LocalChangeEntity(
        id = 9L,
        resourceType = "Test2",
        resourceId = "Anam2",
        timestamp = Date().toTimeZoneString(),
        payload = "payload2",
        type = LocalChangeEntity.Type.DELETE
      )
    val deletePayload_deletePayload2 =
      LocalChangeEntity(
        id = 0L,
        resourceType = "Test2",
        resourceId = "Anam2",
        timestamp = "",
        type = LocalChangeEntity.Type.DELETE,
        payload = ""
      )

    assertThat(mergeLocalChanges(delete_payload, delete_payload2))
      .isEqualTo(deletePayload_deletePayload2)
  }
}
