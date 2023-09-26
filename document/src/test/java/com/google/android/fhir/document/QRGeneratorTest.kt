package com.google.android.fhir.document

import com.google.android.fhir.document.fileExamples.file
import com.google.android.fhir.document.utils.GenerateShlUtils
import com.google.android.fhir.document.utils.ReadShlUtils
import com.nimbusds.jose.shaded.gson.Gson
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest= Config.NONE)
class QRGeneratorTest {

  private val generateShlUtils = GenerateShlUtils()
  private val readShlUtilsMock = ReadShlUtils()

  @Test
  fun postingToServerReturnsManifestIdAndToken() {
    runBlocking {
      val res = generateShlUtils.getManifestUrl()
      println(res)
      Assert.assertTrue(res.contains("id") && res.contains("managementToken") &&
                          res.contains("active"))
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
    val contentJson = Gson().toJson(file)
    val contentEncrypted = generateShlUtils.encrypt(contentJson, encryptionKey)
    println(contentEncrypted)
    Assert.assertEquals(contentEncrypted.split('.').size, 5)
  }

  @Test
  fun fileCanSuccessfullyBeEncryptedAndDecrypted() {
    val key = generateShlUtils.generateRandomKey()
    val content = Gson().toJson(file)

    val encrypted = generateShlUtils.encrypt(content, key)
    val decrypted = readShlUtilsMock.decodeShc(encrypted, key)
    Assert.assertEquals(content, decrypted)
  }
}