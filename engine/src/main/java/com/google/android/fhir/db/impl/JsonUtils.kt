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
import org.json.JSONObject

fun addUpdatedReferenceToResource(
  iParser: IParser,
  resource: Resource,
  outdatedReference: String,
  updatedReference: String,
): Resource {
  val resourceJsonObject = JSONObject(iParser.encodeResourceToString(resource))
  val updatedResource = replaceJsonValue(resourceJsonObject, outdatedReference, updatedReference)
  return iParser.parseResource(updatedResource.toString()) as Resource
}

fun replaceJsonValue(
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
        return jsonObject
      }
    }

    // if it's jsonobject
    if (jsonObject.optJSONObject(key) != null) {
      replaceJsonValue(jsonObject.getJSONObject(key), currentValue, newValue)
    }

    // if it's jsonarray
    if (jsonObject.optJSONArray(key) != null) {
      val jArray = jsonObject.getJSONArray(key)
      for (i in 0 until jArray.length()) {
        replaceJsonValue(jArray.getJSONObject(i), currentValue, newValue)
      }
    }
  }
  return jsonObject
}
