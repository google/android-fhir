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

package com.google.android.fhir.datacapture.utilities

import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumeration

/**
 * All the enums defined in [org.hl7.fhir.r4.model.Enumerations] have these common methods
 * [fromCode, valueOf, values, getDefinition, getDisplay, getSystem, toCode]. This function converts
 * the high level [org.hl7.fhir.r4.model.Enumerations] of something like
 * [org.hl7.fhir.r4.model.Enumerations.AdministrativeGender] into a corresponding [Coding]. The
 * reason we use reflection here to get the actual value is that [Enumeration] provides a default
 * implementation for some of the apis like [Enumeration.getDisplay] and always return null. So as
 * client, we have to call the desired api on the GenericType passed to the [Enumeration] and get
 * the desired value by calling the api's as described above.
 */
internal fun Enumeration<*>.toCoding(): Coding {
  val enumeration = this
  return Coding().apply {
    display =
      if (enumeration.hasDisplay()) {
        enumeration.display
      } else {
        enumeration.value.invokeFunction("getDisplay") as String?
      }
    code =
      if (enumeration.hasCode()) {
        enumeration.code
      } else {
        enumeration.value.invokeFunction("toCode") as String?
      }
    system =
      if (enumeration.hasSystem()) {
        enumeration.system
      } else {
        enumeration.value.invokeFunction("getSystem") as String?
      }
  }
}
