package com.google.android.fhir.datacapture

import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Expression.ExpressionLanguage

sealed class ContextExpression(
    open val expression: Expression
)

data class ContextExpressionFixed(
    override val expression: Expression,
    val data: List<Base>
) : ContextExpression(expression)

data class ContextExpressionDynamic(
    override val expression: Expression,
    var data: List<Base>
) : ContextExpression( expression)
