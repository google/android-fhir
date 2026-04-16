/*
 * Copyright 2023-2026 Google LLC
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

import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChange.Type
import com.google.android.fhir.db.LocalChangeResourceReference
import com.google.android.fhir.sync.upload.patch.PatchOrdering.sccOrderByReferences
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Generates a [Patch] for all [LocalChange]es made to a single FHIR resource.
 *
 * Used when individual client-side changes do not need to be uploaded to the server in order to
 * maintain an audit trail, but instead, multiple changes made to the same FHIR resource on the
 * client can be recorded as a single change on the server.
 */
internal object PerResourcePatchGenerator : PatchGenerator {

  override suspend fun generate(
    localChanges: List<LocalChange>,
    localChangesReferences: List<LocalChangeResourceReference>,
  ): List<StronglyConnectedPatchMappings> {
    return generateSquashedChangesMapping(localChanges).sccOrderByReferences(localChangesReferences)
  }

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
    val resourceJson = Json.parseToJsonElement(resourceString)
    val patchJson = Json.parseToJsonElement(patchString).jsonArray
    var currentElement = resourceJson
    patchJson.forEach { patchElement ->
      val patchObj = patchElement.jsonObject
      currentElement = applySinglePatch(currentElement, patchObj)
    }
    return currentElement.toString()
  }

  private fun applySinglePatch(element: JsonElement, operation: JsonObject): JsonElement {
    val op = operation["op"]?.jsonPrimitive?.content ?: return element
    val path = operation["path"]?.jsonPrimitive?.content ?: return element
    val value = operation["value"]
    val tokens = path.split("/").filter { it.isNotEmpty() }
    return applyModification(element, tokens, op, value)
  }

  private fun applyModification(
    element: JsonElement,
    tokens: List<String>,
    op: String,
    value: JsonElement?,
  ): JsonElement {
    if (tokens.isEmpty()) {
      return when (op) {
        "replace",
        "add", -> value ?: JsonNull
        else -> element
      }
    }
    val token = tokens.first()
    val remaining = tokens.drop(1)

    return when (element) {
      is JsonObject -> {
        val mutableMap = element.toMutableMap()
        if (remaining.isEmpty()) {
          when (op) {
            "replace",
            "add", -> mutableMap[token] = value ?: JsonNull
            "remove" -> mutableMap.remove(token)
          }
        } else {
          val child = mutableMap[token] ?: JsonObject(emptyMap())
          mutableMap[token] = applyModification(child, remaining, op, value)
        }
        JsonObject(mutableMap)
      }
      is JsonArray -> {
        val mutList = element.toMutableList()
        val index = if (token == "-") mutList.size else token.toIntOrNull() ?: return element
        if (remaining.isEmpty()) {
          when (op) {
            "add" -> if (index <= mutList.size) mutList.add(index, value ?: JsonNull)
            "replace" -> if (index < mutList.size) mutList[index] = value ?: JsonNull
            "remove" -> if (index < mutList.size) mutList.removeAt(index)
          }
        } else {
          val child = mutList.getOrNull(index) ?: JsonObject(emptyMap())
          val newChild = applyModification(child, remaining, op, value)
          if (index < mutList.size) {
            mutList[index] = newChild
          } else {
            mutList.add(newChild)
          }
        }
        JsonArray(mutList)
      }
      else -> element
    }
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
    val firstPatchArray = Json.parseToJsonElement(firstPatch).jsonArray
    val secondPatchArray = Json.parseToJsonElement(secondPatch).jsonArray
    val mergedOperations = hashMapOf<String, MutableList<JsonObject>>()

    firstPatchArray.forEach { patchElement ->
      val patchObj = patchElement.jsonObject
      val path = patchObj["path"]?.jsonPrimitive?.content ?: return@forEach
      mergedOperations.getOrPut(path) { mutableListOf() }.add(patchObj)
    }

    secondPatchArray.forEach { patchElement ->
      val patchObj = patchElement.jsonObject
      val path = patchObj["path"]?.jsonPrimitive?.content ?: return@forEach
      val opType = patchObj["op"]?.jsonPrimitive?.content ?: return@forEach
      when (opType) {
        "replace",
        "remove", -> mergedOperations[path] = mutableListOf(patchObj)
        "add" -> mergedOperations.getOrPut(path) { mutableListOf() }.add(patchObj)
      }
    }

    val mergedNodeList = mergedOperations.values.flatten()
    return JsonArray(mergedNodeList).toString()
  }
}
