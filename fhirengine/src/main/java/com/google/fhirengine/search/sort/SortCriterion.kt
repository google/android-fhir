package com.google.fhirengine.search.sort

/** Interface to specify filtering criteria for search. */
interface SortCriterion {
    val table: String
    val param: String
    val ascending: Boolean
}
