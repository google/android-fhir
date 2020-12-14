package com.google.android.fhir.datacapture

enum class WidgetType (val value: Int) {
    CHECK_BOX(1),
    EDIT_TEXT(2),
    GROUP(3);

    companion object {
        private val VALUES = values()
        fun fromInt(value: Int) = VALUES.first { it.value == value }
    }
}