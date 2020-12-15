package com.google.android.fhir.datacapture

import android.os.Build
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for {@link FhirIndexerImpl}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class WidgetTypeTest {

    @Test
    fun size_shouldReturnNumberOfSupportedWidgetTypes() {
        assertThat(WidgetType.values().size).isEqualTo(4)
    }

    @Test
    fun fromInt_shouldReturnTheWidgetType() {
        WidgetType.values().forEach {
            assertThat(WidgetType.fromInt(it.value)).isEqualTo(it)
        }
    }
}