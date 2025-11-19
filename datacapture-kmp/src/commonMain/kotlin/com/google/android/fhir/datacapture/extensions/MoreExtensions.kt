package com.google.android.fhir.datacapture.extensions

import com.google.fhir.model.r4b.Extension

fun Extension.readStringExtension(uri: String): String? {
    val ext = extension.single { it.url == uri }
    return ext.value?.asUri()?.value?.value ?: ext.value?.asCanonical()?.value?.value ?: ext.value?.asCode()?.value?.value ?: ext.value?.asInteger()?.value?.value?.toString()
    ?: ext.value?.asMarkdown()?.value?.value ?: ext.value?.asString()?.value?.value
}