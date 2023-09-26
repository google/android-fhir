package com.google.android.fhir.document

import android.os.Build
import androidx.annotation.RequiresApi
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.dataClasses.IPSDocument
import com.google.android.fhir.document.dataClasses.SHLData
import com.google.android.fhir.document.interfaces.SHLDecoder
import com.google.android.fhir.document.utils.ReadShlUtils
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.CloseableHttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClients
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.coroutineScope
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONObject

class Decoder(private val shlData: SHLData?) : SHLDecoder {

  private val readShlUtils = ReadShlUtils()
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @RequiresApi(Build.VERSION_CODES.O)
  override suspend fun decodeSHLToDocument(jsonBody: String): IPSDocument? {
    constructShlObj()
    val bundle = postToServer(jsonBody)
    return if (bundle != null) {
      IPSDocument(bundle)
    } else {
      null
    }
  }

  override fun storeDocument(doc: IPSDocument) {
    TODO("Not yet implemented")
  }

  override fun hasPasscode(): Boolean {
    shlData?.flags?.forEach {
      if (it == 'P') {
        return true
      }
    }
    return false
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private suspend fun postToServer(jsonData: String): Bundle? = coroutineScope {
    try {
      val httpClient: CloseableHttpClient = HttpClients.createDefault()
      val httpPost = HttpPost(shlData?.manifestUrl)
      httpPost.addHeader("Content-Type", "application/smart-health-card")
      val entity = StringEntity(jsonData)
      httpPost.entity = entity

      val response = httpClient.execute(httpPost)
      val responseBody = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
      httpClient.close()

      val jsonObject = JSONObject(responseBody)
      val embeddedArray = jsonObject.getJSONArray("files").let { jsonArray ->
        (0 until jsonArray.length()).mapNotNull { i ->
          val fileObject = jsonArray.getJSONObject(i)
          if (fileObject.has("embedded")) {
            fileObject.getString("embedded")
          } else {
            fileObject.getString("location").let { readShlUtils.getRequest(it) }
          }
        }.toTypedArray()
      }
      return@coroutineScope decodeEmbeddedArray(embeddedArray)
    } catch (e: Exception) {
      return@coroutineScope null // Return the exception as an error result
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun decodeEmbeddedArray(embeddedArray: Array<String>): Bundle {
    var healthData = ""
    for (elem in embeddedArray) {
      val decodedShc = shlData?.key?.let { readShlUtils.decodeShc(elem, it) }
      if (decodedShc != "") {
        val toDecode = readShlUtils.extractVerifiableCredential(decodedShc!!)
        if (toDecode.isEmpty()) {
          healthData = decodedShc
          break
        }
        val obj = JSONObject(readShlUtils.decodeAndDecompressPayload(toDecode))
        val doc =
          obj.getJSONObject("vc").getJSONObject("credentialSubject").getJSONObject("fhirBundle")
        return parser.parseResource(doc.toString()) as Bundle
      }
    }
    return parser.parseResource(healthData) as Bundle
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun constructShlObj() {
    shlData?.fullLink?.let { fullLink ->
      val extractedJson = readShlUtils.extractUrl(fullLink)
      val decodedJson = readShlUtils.decodeUrl(extractedJson)
      val jsonObject = JSONObject(String(decodedJson, StandardCharsets.UTF_8))

      shlData.apply {
        encodedShlPayload = extractedJson
        manifestUrl = jsonObject.optString("url", "")
        key = jsonObject.optString("key", "")
        flags = jsonObject.optString("flag", "")
      }
    }
  }
}
