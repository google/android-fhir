package com.google.android.fhir.datacapture

enum class WidgetType (val value: Int) {
    GROUP(1),
    CHECK_BOX(2),
    DATE(3),
    EDIT_TEXT(4);

    companion object {
        private val VALUES = values()
        fun fromInt(value: Int) = VALUES.first { it.value == value }
    }
}