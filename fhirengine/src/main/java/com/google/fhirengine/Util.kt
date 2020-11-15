package com.google.fhirengine

import java.text.SimpleDateFormat
import java.util.*

fun Date.toTimeZoneString(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    return simpleDateFormat.format(this)
}