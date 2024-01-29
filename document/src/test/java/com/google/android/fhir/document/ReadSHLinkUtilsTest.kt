package com.google.android.fhir.document

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.fhir.document.decode.ReadSHLinkUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadSHLinkUtilsTest {
  private val readSHLinkUtils = ReadSHLinkUtils()

  @Test
  fun testExtractUrl() {
    val scannedData = "shlink:/example-url"
    val result = readSHLinkUtils.extractUrl(scannedData)
    assertEquals("example-url", result)
  }

  @Test
  fun testDecodeUrl() {
    val extractedUrl = "aGVsbG8="
    val result = readSHLinkUtils.decodeUrl(extractedUrl)
    assertTrue(result.contentEquals("hello".toByteArray()))
  }

  @Test
  @RequiresApi(Build.VERSION_CODES.O)
  fun testDecodeShc() {
    val responseBody = "eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..OgGwTWbECJk9tQc4.PUxr0STCtKQ6DmdPqPtJtTowTBxdprFykeZ2WUOUw234_TtdGWLJ0hzfuWjZXDyBpa55TXwvSwobpcbut9Cdl2nATA0_j1nW0-A32uAwH0qEE1ELV5G0IQVT5AqKJRTCMGpy0mWH.qATmrk-UdwCOaT1TY6GEJg"
    val key = "VmFndWVseS1FbmdhZ2luZy1QYXJhZG94LTA1NTktMDg"
    val result = readSHLinkUtils.decodeShc(responseBody, key)
    assertEquals("{\"iss\":\"DinoChiesa.github.io\",\"sub\":\"idris\",\"aud\":\"kina\",\"iat\":1691158997,\"exp\":1691159597,\"aaa\":true}", result.trim())
  }
}