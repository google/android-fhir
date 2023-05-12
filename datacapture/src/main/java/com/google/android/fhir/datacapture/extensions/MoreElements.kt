package com.google.android.fhir.datacapture.extensions

import org.hl7.fhir.r4.model.Element
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.utils.ToolingExtensions

internal const val EXTENSION_CQF_EXPRESSION_URL: String =
    "http://hl7.org/fhir/StructureDefinition/cqf-expression"

internal val Element.cqfExpression: Expression?
    get() =
        ToolingExtensions.getExtension(this, EXTENSION_CQF_EXPRESSION_URL)?.value?.let {
            it.castToExpression(it)
        }