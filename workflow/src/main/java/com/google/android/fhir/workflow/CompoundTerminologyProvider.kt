/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.workflow

import org.opencds.cqf.cql.engine.exception.TerminologyProviderException
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

class CompoundTerminologyProvider(private val providers: List<TerminologyProvider>) :
  TerminologyProvider {

  // TODO: should the `in` be isolated?
  override fun `in`(code: Code, valueSet: ValueSetInfo): Boolean {
    try {
      return expand(valueSet).any { it.code == code.code && it.system == code.system }
    } catch (e: Exception) {
      throw TerminologyProviderException(
        "Error performing membership check of Code: $code in ValueSet: ${valueSet.id}",
        e
      )
    }
  }

  override fun expand(valueSet: ValueSetInfo): Iterable<Code> =
    providers.flatMap { it.expand(valueSet) }

  override fun lookup(code: Code, codeSystem: CodeSystemInfo): Code =
    providers.firstNotNullOf {
      try {
        it.lookup(code, codeSystem)
      } catch (e: TerminologyProviderException) {
        null
      }
    }
}
