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

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.TypeDetails
import org.hl7.fhir.r4.model.ValueSet
import org.hl7.fhir.r4.utils.FHIRPathEngine
import org.hl7.fhir.r4.utils.FHIRPathEngine.IEvaluationContext.FunctionDetails
import timber.log.Timber

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

  override fun resolveFunction(functionName: String?): FunctionDetails {
    return when (functionName) {
      Function.ABS -> FunctionDetails("Returns the absolute value of the input", 0, 0)
      Function.POW -> FunctionDetails("Raises a number to the exponent power", 1, 1)
      Function.EXP -> FunctionDetails("Returns e raised to the power of the input", 0, 0)
      else -> throw UnsupportedOperationException()
    }
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

    return when (functionName) {
      Function.ABS -> computeAbs(focus)
      Function.POW -> computePow(focus, parameters)
      Function.EXP -> computeExp(focus)
      else -> throw UnsupportedOperationException()
    }
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

  private fun computeAbs(focus: MutableList<Base>?): MutableList<Base> {
    val results: MutableList<Base> = mutableListOf()
    focus?.let { f ->
      if (f.size > 1) throw IllegalArgumentException("Too many items specified")
      val primitiveStringValue = f[0].primitiveValue()
      when (f[0]) {
        is IntegerType -> results.add(IntegerType(abs(primitiveStringValue.toInt())))
        is DecimalType -> results.add(DecimalType(abs(primitiveStringValue.toDouble())))
        else -> throw IllegalArgumentException("Wrong type of operand supplied")
      }
    }
    return results
  }

  private fun computePow(
    focus: MutableList<Base>?,
    parameters: MutableList<MutableList<Base>>?
  ): MutableList<Base> {
    val results: MutableList<Base> = mutableListOf()
    focus?.let { f ->
      if (f.size > 1) throw IllegalArgumentException("Too many items specified")
      val paramPrimitiveStringValue =
        parameters?.get(0)?.get(0)?.primitiveValue()
          ?: throw IllegalArgumentException("Param expected")
      val focusPrimitiveStringValue = f[0].primitiveValue()
      try {
        val result = focusPrimitiveStringValue.toDouble().pow(paramPrimitiveStringValue.toDouble())
        if (f[0] is DecimalType || parameters[0][0] is DecimalType) results.add(DecimalType(result))
        if (f[0] is IntegerType && parameters[0][0] is IntegerType)
          results.add(IntegerType(result.toInt()))
      } catch (e: NumberFormatException) {
        Timber.e("Error: " + e.message)
      }
    }
    return results
  }

  private fun computeExp(focus: MutableList<Base>?): MutableList<Base> {
    val results: MutableList<Base> = mutableListOf()
    focus?.let { f ->
      if (f.size > 1) throw IllegalArgumentException("Too many items specified")
      val primitiveStringValue = f[0].primitiveValue()
      when (f[0]) {
        is IntegerType,
        is DecimalType -> results.add(DecimalType(exp(primitiveStringValue.toDouble())))
        else -> throw IllegalArgumentException("Wrong type of operand supplied")
      }
    }
    return results
  }

  object Function {
    const val ABS = "abs"
    const val POW = "power"
    const val EXP = "exp"
  }
}
