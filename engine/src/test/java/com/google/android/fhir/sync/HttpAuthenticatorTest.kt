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

package com.google.android.fhir.sync

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HttpAuthenticatorTest {
  @Test
  fun `should generate basic authentication header`() {
    assertThat(HttpAuthenticationMethod.Basic("username", "password").getAuthorizationHeader())
      .isEqualTo("Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
  }

  @Test
  fun `should generate bearer authentication token`() {
    assertThat(HttpAuthenticationMethod.Bearer("token").getAuthorizationHeader())
      .isEqualTo("Bearer token")
  }
}
