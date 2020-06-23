package com.google.fhirengine.search.impl

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery


/** Query that returns a list of resource IDs. */
abstract class Query {
    abstract fun getQueryString(): String
    abstract fun getQueryArgs(): List<Any?>
    fun getSupportSQLiteQuery(): SupportSQLiteQuery =
            SimpleSQLiteQuery(getQueryString(), getQueryArgs().toTypedArray())
}
