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
import com.google.android.fhir.document.generate.EncryptionUtils
import com.google.android.fhir.testing.readFromFile
import com.nimbusds.jose.shaded.gson.Gson
import org.hl7.fhir.r4.model.Bundle
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EncryptionUtilsTest {

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val minimalBundleString =
    parser.encodeResourceToString(readFromFile(Bundle::class.java, "/bundleMinimal.json"))

  @Test
  fun randomKeysCanBeGenerated() {
    val key = EncryptionUtils.generateRandomKey()
    val base64UrlEncodedKeyLength = 45
    assert(key.length == base64UrlEncodedKeyLength)
  }

  @Test
  fun canConvertFilesIntoJweTokens() {
    val encryptionKey = EncryptionUtils.generateRandomKey()
    val contentJson = Gson().toJson(minimalBundleString)
    val contentEncrypted = EncryptionUtils.encrypt(contentJson, encryptionKey)
    Assert.assertEquals(contentEncrypted.split('.').size, 5)
  }
}
