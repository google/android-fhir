/*
 * Copyright 2023-2024 Google LLC
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

package com.google.android.fhir.sync.upload.patch

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonpatch.JsonPatch
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChange.Type
import com.google.android.fhir.db.Database
import com.google.android.fhir.sync.upload.patch.PatchOrdering.sccOrderByReferences

/**
 * Generates a [Patch] for all [LocalChange]es made to a single FHIR resource.
 *
 * Used when individual client-side changes do not need to be uploaded to the server in order to
 * maintain an audit trail, but instead, multiple changes made to the same FHIR resource on the
 * client can be recorded as a single change on the server.
 */
internal class PerResourcePatchGenerator private constructor(val database: Database) :
  PatchGenerator {

  override suspend fun generate(
    localChanges: List<LocalChange>,
  ): List<StronglyConnectedPatchMappings> {
    return generateSquashedChangesMapping(localChanges).sccOrderByReferences(database)
  }

  @androidx.annotation.VisibleForTesting
  internal fun generateSquashedChangesMapping(localChanges: List<LocalChange>) =
    localChanges
      .groupBy { it.resourceType to it.resourceId }
      .values
      .mapNotNull { resourceLocalChanges ->
        mergeLocalChangesForSingleResource(resourceLocalChanges)?.let { patch ->
          PatchMapping(
            localChanges = resourceLocalChanges,
            generatedPatch = patch,
          )
        }
      }

  private fun mergeLocalChangesForSingleResource(localChanges: List<LocalChange>): Patch? {
    // TODO (maybe this should throw exception when two entities don't have the same versionID)
    val firstDeleteLocalChange = localChanges.indexOfFirst { it.type == Type.DELETE }
    require(firstDeleteLocalChange == -1 || firstDeleteLocalChange == localChanges.size - 1) {
      "Changes after deletion of resource are not permitted"
    }

    val lastInsertLocalChange = localChanges.indexOfLast { it.type == Type.INSERT }
    require(lastInsertLocalChange == -1 || lastInsertLocalChange == 0) {
      "Changes before creation of resource are not permitted"
    }

    return when {
      localChanges.first().type == Type.INSERT && localChanges.last().type == Type.DELETE -> null
      localChanges.first().type == Type.INSERT -> {
        createPatch(
          localChanges = localChanges,
          type = Patch.Type.INSERT,
          payload = localChanges.map { it.payload }.reduce(::applyPatch),
        )
      }
      localChanges.last().type == Type.DELETE -> {
        createPatch(
          localChanges = localChanges,
          type = Patch.Type.DELETE,
          payload = "",
        )
      }
      else -> {
        createPatch(
          localChanges = localChanges,
          type = Patch.Type.UPDATE,
          payload = localChanges.map { it.payload }.reduce(::mergePatches),
        )
      }
    }
  }

  private fun createPatch(localChanges: List<LocalChange>, type: Patch.Type, payload: String) =
    Patch(
      resourceId = localChanges.first().resourceId,
      resourceType = localChanges.first().resourceType,
      type = type,
      payload = payload,
      versionId = localChanges.first().versionId,
      timestamp = localChanges.last().timestamp,
    )

  /** Update a JSON object with a JSON patch (RFC 6902). */
  private fun applyPatch(resourceString: String, patchString: String): String {
    val objectMapper = ObjectMapper()
    val resourceJson = objectMapper.readValue(resourceString, JsonNode::class.java)
    val patchJson = objectMapper.readValue(patchString, JsonPatch::class.java)
    return patchJson.apply(resourceJson).toString()
  }

  /**
   * Merges two JSON patches represented as strings.
   *
   * This function combines operations from two JSON patch arrays into a single patch array. The
   * merging rules are as follows:
   * - "replace" and "remove" operations from the second patch will overwrite any existing
   *   operations for the same path.
   * - "add" operations from the second patch will be added to the list of operations for that path,
   *   even if operations already exist for that path.
   * - The function does not handle other operation types like "move", "copy", or "test".
   */
  private fun mergePatches(firstPatch: String, secondPatch: String): String {
    // TODO: validate patches are RFC 6902 compliant JSON patches
    val objectMapper = ObjectMapper()
    val firstPatchNode: JsonNode = JsonLoader.fromString(firstPatch)
    val secondPatchNode: JsonNode = JsonLoader.fromString(secondPatch)
    val mergedOperations = hashMapOf<String, MutableList<JsonNode>>()

    firstPatchNode.forEach { patchNode ->
      val path = patchNode.get("path").asText()
      mergedOperations.getOrPut(path) { mutableListOf() }.add(patchNode)
    }

    secondPatchNode.forEach { patchNode ->
      val path = patchNode.get("path").asText()
      val opType = patchNode.get("op").asText()
      when (opType) {
        "replace",
        "remove", -> mergedOperations[path] = mutableListOf(patchNode)
        "add" -> mergedOperations.getOrPut(path) { mutableListOf() }.add(patchNode)
      }
    }
    val mergedNode = objectMapper.createArrayNode()
    mergedOperations.values.flatten().forEach(mergedNode::add)
    return objectMapper.writeValueAsString(mergedNode)
  }

  companion object {

    @Volatile private lateinit var _instance: PerResourcePatchGenerator

    @Synchronized
    fun with(database: Database): PerResourcePatchGenerator {
      if (!::_instance.isInitialized) {
        _instance = PerResourcePatchGenerator(database)
      } else if (_instance.database != database) {
        _instance = PerResourcePatchGenerator(database)
      }

      return _instance
    }
  }
}
