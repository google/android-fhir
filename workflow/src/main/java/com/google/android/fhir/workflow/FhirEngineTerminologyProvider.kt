/*
 * Copyright 2021 Google LLC
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

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.ValueSet
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo
import org.opencds.cqf.cql.evaluator.engine.util.ValueSetUtil

class FhirEngineTerminologyProvider(
  private val fhirContext: FhirContext,
  private val fhirEngine: FhirEngine
) : TerminologyProvider {
  override fun `in`(code: Code, valueSet: ValueSetInfo): Boolean {
    return expand(valueSet).any { it.code == code.code && it.system == code.system }
  }

  override fun expand(valueSetInfo: ValueSetInfo): MutableIterable<Code> {
    val valueSet = runBlocking {
      fhirEngine
        .search<ValueSet> { filter(ValueSet.URL, { value = valueSetInfo.id }) }
        .singleOrNull()
    }
    return ValueSetUtil.getCodesInExpansion(fhirContext, valueSet)
      ?: ValueSetUtil.getCodesInCompose(fhirContext, valueSet)
  }

  override fun lookup(code: Code?, codeSystem: CodeSystemInfo?): Code {
    TODO("Not yet implemented")
  }
}
