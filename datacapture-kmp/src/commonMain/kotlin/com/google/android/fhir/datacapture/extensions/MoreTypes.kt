package com.google.android.fhir.datacapture.extensions

internal fun com.google.fhir.model.r4b.String.getLocalizedText(lang: String = "en"): String? {
    return getTranslation(lang) ?: getTranslation(lang.split("-").firstOrNull()) ?: value
}

internal fun com.google.fhir.model.r4b.String.getTranslation(l: String?): String? {
    for (e in extension) {
        if (e.url == EXT_TRANSLATION) {
            val langExtValue = e.readStringExtension("lang")
            if (langExtValue == l) return e.readStringExtension("content")
        }
    }
    return null
}

internal const val EXT_TRANSLATION = "http://hl7.org/fhir/StructureDefinition/translation"
