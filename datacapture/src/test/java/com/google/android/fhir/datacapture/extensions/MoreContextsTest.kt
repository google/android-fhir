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

package com.google.android.fhir.datacapture.extensions

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MoreContextsTest {

  @Test
  fun context_should_return_appCompatActivity() {
    val context = AppCompatActivity().tryUnwrapContext()
    assertThat(context).isInstanceOf(AppCompatActivity::class.java)
  }

  @Test
  fun context_should_return_baseContext_as_appCompatActivity() {
    val context = ContextThemeWrapper(AppCompatActivity(), 0).tryUnwrapContext()
    assertThat(context).isInstanceOf(AppCompatActivity::class.java)
  }

  @Test
  fun context_should_return_null() {
    val context = Application().tryUnwrapContext()
    assertThat(context).isNull()
  }
}
