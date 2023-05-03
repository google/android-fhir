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

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.ImplementationGuide
import com.google.android.fhir.knowledge.KnowledgeManager

class FhirOperatorBuilder(private val applicationContext: Context) {
  private var fhirContext: FhirContext? = null
  private var fhirEngine: FhirEngine? = null
  private var implementationGuides: List<ImplementationGuide> = emptyList()
  private var knowledgeManager: KnowledgeManager? = null

  fun withFhirEngine(fhirEngine: FhirEngine): FhirOperatorBuilder {
    this.fhirEngine = fhirEngine
    return this
  }

  fun withIgManager(knowledgeManager: KnowledgeManager): FhirOperatorBuilder {
    this.knowledgeManager = knowledgeManager
    return this
  }

  fun withFhirContext(fhirContext: FhirContext): FhirOperatorBuilder {
    this.fhirContext = fhirContext
    return this
  }

  fun withImplementationGuides(
    vararg implementationGuides: ImplementationGuide
  ): FhirOperatorBuilder {
    this.implementationGuides = implementationGuides.toList()
    return this
  }

  fun build(): FhirOperator {
    return FhirOperator(
      fhirContext ?: FhirContext(FhirVersionEnum.R4),
      fhirEngine ?: FhirEngineProvider.getInstance(applicationContext),
      knowledgeManager ?: KnowledgeManager.create(applicationContext)
    )
  }
}
