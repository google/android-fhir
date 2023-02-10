/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_HIGH
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.fhir.security.FhirSecurityConfiguration
import com.google.android.fhir.security.LockScreenRequirement
import com.google.android.fhir.security.RequirementViolationAction.WARN
import com.google.common.truth.Truth.assertThat
import java.lang.IllegalStateException
import java.util.EnumSet
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Unit tests for [FhirEngineProvider]. */
@RunWith(RobolectricTestRunner::class)
class FhirEngineProviderTest {
  private val provider: FhirEngineProvider = FhirEngineProvider

  @After
  fun tearDown() {
    provider.forceCleanup()
  }

  @Test
  fun build_twiceWithAppContext_shouldReturnSameFhirEngine() {
    val engineOne = provider.getInstance(ApplicationProvider.getApplicationContext())
    val engineTwo = provider.getInstance(ApplicationProvider.getApplicationContext())
    assertThat(engineOne).isSameInstanceAs(engineTwo)
  }

  @Test
  fun build_withAppAndActivityContext_shouldReturnSameFhirEngine() {
    val engineAppContext = provider.getInstance(ApplicationProvider.getApplicationContext())
    val engineActivityContext =
      provider.getInstance(InstrumentationRegistry.getInstrumentation().context)
    assertThat(engineAppContext).isSameInstanceAs(engineActivityContext)
  }

  @Test
  fun build_twiceWithAppContext_afterCleanup_shouldReturnDifferentInstances() {
    provider.init(FhirEngineConfiguration(testMode = true))
    val engineOne = provider.getInstance(ApplicationProvider.getApplicationContext())
    provider.cleanup()
    val engineTwo = provider.getInstance(ApplicationProvider.getApplicationContext())
    assertThat(engineOne).isNotSameInstanceAs(engineTwo)
  }

  @Test
  fun cleanup_not_in_test_mode_fails() {
    provider.init(FhirEngineConfiguration(testMode = false))

    provider.getInstance(ApplicationProvider.getApplicationContext())

    assertThat(runCatching { provider.cleanup() }.exceptionOrNull())
      .isInstanceOf(IllegalStateException::class.java)
  }

  @Test
  fun createFhirEngineConfiguration_withDefaultNetworkConfig_shouldHaveDefaultTimeout() {
    val config = FhirEngineConfiguration(serverConfiguration = ServerConfiguration(""))
    with(config.serverConfiguration!!.networkConfiguration) {
      assertThat(this.connectionTimeOut).isEqualTo(10L)
      assertThat(this.readTimeOut).isEqualTo(10L)
      assertThat(this.writeTimeOut).isEqualTo(10L)
    }
  }

  @Test
  fun createFhirEngineConfiguration_configureNetworkTimeouts_shouldHaveconfiguredTimeout() {
    val config =
      FhirEngineConfiguration(
        serverConfiguration =
          ServerConfiguration(
            "",
            NetworkConfiguration(connectionTimeOut = 5, readTimeOut = 4, writeTimeOut = 6)
          )
      )
    with(config.serverConfiguration!!.networkConfiguration) {
      assertThat(this.connectionTimeOut).isEqualTo(5)
      assertThat(this.readTimeOut).isEqualTo(4)
      assertThat(this.writeTimeOut).isEqualTo(6)
    }
  }

  @Test
  fun createFhirEngineConfiguration_securityConfig_shouldHaveExpectedSecurityConfiguration() {
    val pendingIntent =
      PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        /* requestCode= */ 0,
        Intent("TEST_ACTION"),
        /* flags= */ 0
      )

    val config =
      FhirEngineConfiguration(
        securityConfiguration =
          FhirSecurityConfiguration(
            LockScreenRequirement(PASSWORD_COMPLEXITY_HIGH, EnumSet.of(WARN)),
            warningCallback = pendingIntent
          )
      )

    with(config.securityConfiguration) {
      assertThat(this?.lockScreenRequirement)
        .isEqualTo(LockScreenRequirement(PASSWORD_COMPLEXITY_HIGH, EnumSet.of(WARN)))
      assertThat(this?.warningCallback).isEqualTo(pendingIntent)
    }
  }
}
