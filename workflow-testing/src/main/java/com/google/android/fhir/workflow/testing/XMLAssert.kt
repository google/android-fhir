/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.workflow.testing

import com.ctc.wstx.shaded.msv_core.verifier.jaxp.DocumentBuilderFactoryImpl
import com.google.common.truth.Truth.assertWithMessage
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.diff.ElementSelectors

object XMLAssert {
  fun assertEquals(expected: String, actual: String) {
    val diff =
      DiffBuilder.compare(expected)
        .withTest(actual)
        .withNodeMatcher(DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
        .withDocumentBuilderFactory(
          DocumentBuilderFactoryImpl(), // Overrides the incomplete default DocumentBuilderFactory
        )
        .checkForSimilar()
        .build()

    assertWithMessage(diff.fullDescription()).that(diff.hasDifferences()).isFalse()
  }
}
