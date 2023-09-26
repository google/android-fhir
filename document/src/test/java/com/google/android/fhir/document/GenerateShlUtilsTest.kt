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

// import com.google.android.fhir.document.fileExamples.minimalBundleString
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.utils.GenerateShlUtils
import com.google.android.fhir.document.utils.ReadShlUtils
import com.google.android.fhir.testing.readFromFile
import com.nimbusds.jose.shaded.gson.Gson
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GenerateShlUtilsTest {

  private val generateShlUtils = GenerateShlUtils()
  private val readShlUtilsMock = ReadShlUtils()
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val minimalBundleString =
    parser.encodeResourceToString(readFromFile(Bundle::class.java, "/bundleMinimal.json"))

  @Test
  fun postingToServerReturnsManifestIdAndToken() {
    runBlocking {
      val res = generateShlUtils.getManifestUrl()
      println(res)
      Assert.assertTrue(
        res.contains("id") && res.contains("managementToken") && res.contains("active"),
      )
    }
  }

  @Test
  fun randomKeysCanBeGenerated() {
    val key = generateShlUtils.generateRandomKey()
    assert(key.length == 44)
  }

  @Test
  fun canConvertFilesIntoJweTokens() {
    val encryptionKey = generateShlUtils.generateRandomKey()
    val contentJson = Gson().toJson(minimalBundleString)
    val contentEncrypted = generateShlUtils.encrypt(contentJson, encryptionKey)
    println(contentEncrypted)
    Assert.assertEquals(contentEncrypted.split('.').size, 5)
  }

  @Test
  fun fileCanSuccessfullyBeEncryptedAndDecrypted() {
    val key = generateShlUtils.generateRandomKey()
    val content = Gson().toJson(minimalBundleString)

    val encrypted = generateShlUtils.encrypt(content, key)
    val decrypted = readShlUtilsMock.decodeShc(encrypted, key)
    Assert.assertEquals(content, decrypted)
  }
}
