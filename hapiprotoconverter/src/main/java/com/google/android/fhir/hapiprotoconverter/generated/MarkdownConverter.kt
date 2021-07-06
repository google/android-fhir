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

import com.google.fhir.r4.core.Markdown
import org.hl7.fhir.r4.model.MarkdownType

/** contains functions that convert between the hapi and proto representations of markdown */
public object MarkdownConverter {
  /** returns the proto Markdown equivalent of the hapi MarkdownType */
  public fun MarkdownType.toProto(): Markdown {
    val protoValue = Markdown.newBuilder().setValue(value).build()
    return protoValue
  }

  /** returns the hapi MarkdownType equivalent of the proto Markdown */
  public fun Markdown.toHapi(): MarkdownType {
    val hapiValue = MarkdownType()
    hapiValue.value = value
    return hapiValue
  }
}
