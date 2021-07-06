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

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Id
import org.hl7.fhir.r4.model.IdType

/** contains functions that convert between the hapi and proto representations of id */
public object IdConverter {
  /** returns the proto Id equivalent of the hapi IdType */
  public fun IdType.toProto(): Id {
    val protoValue = Id.newBuilder().setValue(value).build()
    return protoValue
  }

  /** returns the hapi IdType equivalent of the proto Id */
  public fun Id.toHapi(): IdType {
    val hapiValue = IdType()
    hapiValue.value = value
    return hapiValue
  }
}
