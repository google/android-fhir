package com.google.android.fhir.datacapture

import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Expression

sealed class ContextVariable(
    open val name: String
)

data class ContextVariableImmutable(
    override val name: String,
    val data: Base?
) : ContextVariable(name)

data class ContextVariableMutable(
    override val name: String,
    val expression: Expression,
    var data: Base?
) : ContextVariable(name)

data class ContextVariableGroup(
    override val name: String,
    val expression: Expression,
    var data: List<Base>
) : ContextVariable(name)

