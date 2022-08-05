package com.google.android.fhir.search

enum class Modifier {
    TEXT, NOT, IN;

    fun toCode(): String =
        this.name.lowercase()

    fun toQuery(): String =
            ":${this.name.lowercase()}"
}