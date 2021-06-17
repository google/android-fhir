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

package com.google.android.fhir.hapiprotoconverter

import ca.uhn.fhir.parser.IParser
import com.google.fhir.common.JsonFormat
import com.google.fhir.shaded.protobuf.GeneratedMessageV3
import org.hl7.fhir.r4.model.Resource

@Suppress("UNCHECKED_CAST")
inline fun <reified T : GeneratedMessageV3> convert(
  resource: Resource,
  hapiParser: IParser,
  protoParser: JsonFormat.Parser
): T {
  // Generates a new Builder of proto class T
  val newBuilder =
    T::class.java.getDeclaredMethod("newBuilder").invoke(null) as GeneratedMessageV3.Builder<*>
  protoParser.merge(hapiParser.encodeResourceToString(resource), newBuilder)
  return newBuilder.build() as T
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Resource> convert(
  resource: GeneratedMessageV3,
  hapiParser: IParser,
  protoPrinter: JsonFormat.Printer
): T {
  return hapiParser.parseResource(
    T::class.java,
    protoPrinter.omittingInsignificantWhitespace().print(resource)
  )
}
