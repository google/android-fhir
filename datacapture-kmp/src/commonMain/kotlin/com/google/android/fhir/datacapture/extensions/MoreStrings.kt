package com.google.android.fhir.datacapture.extensions

import androidx.compose.ui.text.AnnotatedString

internal fun String.toAnnotatedString(): AnnotatedString {
    return AnnotatedString(this)
}