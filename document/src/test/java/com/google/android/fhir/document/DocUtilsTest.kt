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

package com.google.android.fhir.document

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.utils.DocumentUtils
import com.google.android.fhir.testing.readFromFile
import org.hl7.fhir.r4.model.Bundle
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DocUtilsTest {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val fileBundle = readFromFile(Bundle::class.java, "/bundleMinimal.json")
  private val immunizationBundleContent =
    this::class.java.classLoader?.getResource("immunizationBundle.json")?.readText()
  private val immunizationBundle = parser.parseResource(immunizationBundleContent) as Bundle
  private val docUtils = DocumentUtils()

  @Test
  fun getTitlesFromMinBundleDoc() {
    val doc = IPSDocument(fileBundle)
    docUtils.getSectionsFromDoc(doc)
    Assert.assertEquals(3, doc.titles.size)
  }

  @Test
  fun getTitlesFromImmunizationBundle() {
    val doc = IPSDocument(immunizationBundle)
    docUtils.getSectionsFromDoc(doc)
    Assert.assertEquals(7, doc.titles.size)
  }
}
