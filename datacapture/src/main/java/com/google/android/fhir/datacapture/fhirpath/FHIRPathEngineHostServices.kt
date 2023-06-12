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

package com.google.android.fhir.datacapture.fhirpath

import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.TypeDetails
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.utils.FHIRPathEngine

/**
 * Resolves constants defined in the fhir path expressions beyond those defined in the specification
 */
internal object FHIRPathEngineHostServices : FHIRPathEngine.IEvaluationContext {
  override fun resolveConstant(appContext: Any?, name: String?, beforeContext: Boolean): Base? =
    (appContext as? Map<*, *>)?.get(name) as? Base

  override fun resolveConstantType(appContext: Any?, name: String?): TypeDetails {
    throw UnsupportedOperationException()
  }

  override fun log(argument: String?, focus: MutableList<Base>?): Boolean {
    throw UnsupportedOperationException()
  }

  override fun resolveFunction(
    functionName: String?
  ): FHIRPathEngine.IEvaluationContext.FunctionDetails {
    throw UnsupportedOperationException()
  }

  override fun checkFunction(
    appContext: Any?,
    functionName: String?,
    parameters: MutableList<TypeDetails>?
  ): TypeDetails {
    throw UnsupportedOperationException()
  }

  override fun executeFunction(
    appContext: Any?,
    focus: MutableList<Base>?,
    functionName: String?,
    parameters: MutableList<MutableList<Base>>?
  ): MutableList<Base> {
    throw UnsupportedOperationException()
  }

  override fun resolveReference(appContext: Any?, url: String?): Base {
    throw UnsupportedOperationException()
  }

  override fun conformsToProfile(appContext: Any?, item: Base?, url: String?): Boolean {
    throw UnsupportedOperationException()
  }

  override fun resolveValueSet(appContext: Any?, url: String?): ValueSet {
    throw UnsupportedOperationException()
  }
}
