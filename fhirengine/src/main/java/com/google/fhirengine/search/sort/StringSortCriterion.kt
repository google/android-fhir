package com.google.fhirengine.search.sort

import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.fhirengine.search.filter.FilterCriterion

/**
 * [FilterCriterion] on a string value.
 *
 * For example:
 * * name that matches 'Tom'
 * * address that includes 'London'
 */
data class StringSortCriterion constructor(
        val stringParam: StringClientParam,
        override val ascending: Boolean
): SortCriterion {
    override val table: String = "StringIndexEntity"
    override val param = stringParam.paramName
}

fun stringSort(param: StringClientParam, ascending: Boolean) =
        StringSortCriterion(param, ascending)
