package com.google.android.fhir.datacapture.utilities

import android.os.Build
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.M, Build.VERSION_CODES.N])
class MoreLocalDateTest {

  @Test
  fun localizedString_US() {
    Locale.setDefault(Locale.US)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.localizedString).isEqualTo("Oct 18, 2010")
  }

  @Test
  fun localizedString_Japan() {
    Locale.setDefault(Locale.JAPAN)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.localizedString).isEqualTo("2010/10/18")
  }

  @Test
  fun localizedString_Italy() {
    Locale.setDefault(Locale.ITALY)
    val localDate = LocalDate.of(2010, 10, 18)
    assertThat(localDate.localizedString).isEqualTo("18 ott 2010")
  }
}