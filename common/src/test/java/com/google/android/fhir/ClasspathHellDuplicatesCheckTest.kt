/*
 * Copyright 2023 Google LLC
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

import org.junit.ClassRule
import org.junit.Test

/** Classpath Test; @see ClasspathHellDuplicatesCheckRule. */
class ClasspathHellDuplicatesCheckTest {

  companion object {
    @ClassRule @JvmField val classgraph = object : ClasspathHellDuplicatesCheckRule() {}
  }

  @Test // we just need this because JUnit doesn't like a *Test class with only a Rule
  fun check_for_classpath_duplicates() {}
}
