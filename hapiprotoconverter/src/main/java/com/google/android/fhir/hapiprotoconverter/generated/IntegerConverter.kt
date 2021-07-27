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

import com.google.fhir.r4.core.Integer
import org.hl7.fhir.r4.model.IntegerType

/** returns the proto Integer equivalent of the hapi IntegerType */
public fun IntegerType.toProto(): Integer {
  val protoValue = Integer.newBuilder()
  if (value != null) protoValue.setValue(value)
  return protoValue.build()
}

/** returns the hapi IntegerType equivalent of the proto Integer */
public fun Integer.toHapi(): IntegerType {
  val hapiValue = IntegerType()
  hapiValue.value = value
  return hapiValue
}
