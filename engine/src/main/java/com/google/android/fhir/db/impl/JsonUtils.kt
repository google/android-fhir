/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.db.impl

import ca.uhn.fhir.parser.IParser
import org.hl7.fhir.r4.model.Resource
import org.json.JSONArray
import org.json.JSONObject

internal fun addUpdatedReferenceToResource(
  iParser: IParser,
  resource: Resource,
  outdatedReference: String,
  updatedReference: String,
): Resource {
  val resourceJsonObject = JSONObject(iParser.encodeResourceToString(resource))
  val updatedResource = replaceJsonValue(resourceJsonObject, outdatedReference, updatedReference)
  return iParser.parseResource(updatedResource.toString()) as Resource
}

internal fun replaceJsonValue(
  jsonObject: JSONObject,
  currentValue: String,
  newValue: String,
): JSONObject {
  val iterator: Iterator<*> = jsonObject.keys()
  var key: String?
  while (iterator.hasNext()) {
    key = iterator.next() as String
    // if object is just string we change value in key
    if (jsonObject.optJSONArray(key) == null && jsonObject.optJSONObject(key) == null) {
      if (jsonObject.optString(key) == currentValue) {
        jsonObject.put(key, newValue)
      }
    }

    // if it's jsonobject
    if (jsonObject.optJSONObject(key) != null) {
      replaceJsonValue(jsonObject.getJSONObject(key), currentValue, newValue)
    }

    // if it's jsonarray
    if (jsonObject.optJSONArray(key) != null) {
      val jArray = jsonObject.getJSONArray(key)
      replaceJsonValue(jArray, currentValue, newValue)
    }
  }
  return jsonObject
}

internal fun replaceJsonValue(
  jsonArray: JSONArray,
  currentValue: String,
  newValue: String,
): JSONArray {
  for (i in 0 until jsonArray.length()) {
    if (jsonArray.optJSONArray(i) != null) {
      replaceJsonValue(jsonArray.getJSONArray(i), currentValue, newValue)
    } else if (jsonArray.optJSONObject(i) != null) {
      replaceJsonValue(jsonArray.getJSONObject(i), currentValue, newValue)
    } else if (currentValue.equals(jsonArray.optString(i))) {
      jsonArray.put(i, newValue)
    }
  }
  return jsonArray
}

internal fun lookForReferencesInJsonPatch(jsonObject: JSONObject): String? {
  // "[{\"op\":\"replace\",\"path\":\"\\/basedOn\\/0\\/reference\",\"value\":\"CarePlan\\/345\"}]"
  if (jsonObject.getString("path").endsWith("reference")) {
    return jsonObject.getString("value")
  }
  return null
}

internal fun extractAllValuesWithKey(lookupKey: String, jsonObject: JSONObject): List<String> {
  val iterator: Iterator<*> = jsonObject.keys()
  var key: String?
  val referenceValues = mutableListOf<String>()
  while (iterator.hasNext()) {
    key = iterator.next() as String
    // if object is just string we change value in key
    if (jsonObject.optJSONArray(key) == null && jsonObject.optJSONObject(key) == null) {
      if (key.equals(lookupKey)) {
        referenceValues.add(jsonObject.getString(key))
      }
    }

    // if it's jsonobject
    if (jsonObject.optJSONObject(key) != null) {
      referenceValues.addAll(extractAllValuesWithKey(lookupKey, jsonObject.getJSONObject(key)))
    }

    // if it's jsonarray
    if (jsonObject.optJSONArray(key) != null) {
      referenceValues.addAll(
        extractAllValuesWithKey(lookupKey, jsonObject.getJSONArray(key)),
      )
    }
  }
  return referenceValues
}

internal fun extractAllValuesWithKey(lookupKey: String, jArray: JSONArray): List<String> {
  val referenceValues = mutableListOf<String>()
  for (i in 0 until jArray.length()) {
    if (jArray.optJSONObject(i) != null) {
      referenceValues.addAll(extractAllValuesWithKey(lookupKey, jArray.getJSONObject(i)))
    } else if (jArray.optJSONArray(i) != null) {
      referenceValues.addAll(
        extractAllValuesWithKey(lookupKey, jArray.getJSONArray(i)),
      )
    }
  }
  return referenceValues
}
