package com.google.fhirengine.index

import java.math.BigDecimal

internal data class QuantityIndex (
        val name : String,
        val path : String,
        val system: String,
        val unit: String,
        val value: BigDecimal
)