package com.google.android.fhir.document.decode

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.IPSDocument
import com.google.android.fhir.document.RetrofitSHLService
import com.google.android.fhir.document.scan.SHLinkScanData
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.coroutineScope
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Patient
import org.json.JSONObject
import timber.log.Timber

class SHLinkDecoderImpl(
  private val shLinkScanDataInput: SHLinkScanData?,
  private val readSHLinkUtils: ReadSHLinkUtils,
  private val apiService: RetrofitSHLService,
) : SHLinkDecoder {

  private lateinit var shLinkScanData: SHLinkScanData
  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  override suspend fun decodeSHLinkToDocument(jsonData: String): IPSDocument? {
    constructShlObj()
    val bundle = postToServer(jsonData)
    return if (bundle != null) {
      IPSDocument(bundle, ArrayList(), Patient()) // change this construction
    } else {
      null
    }
  }

  private suspend fun postToServer(jsonData: String): Bundle? = coroutineScope {
    try {
      val response = apiService.getFilesFromManifest(shLinkScanData.manifestUrl, jsonData)
      return@coroutineScope if (response.isSuccessful) {
        val responseBody = response.body()?.string()
        if (!responseBody.isNullOrBlank()) {
          val jsonObject = JSONObject(responseBody)
          val embeddedArray = jsonObject.getJSONArray("files").let { jsonArray ->
            (0 until jsonArray.length()).mapNotNull { i ->
              val fileObject = jsonArray.getJSONObject(i)
              if (fileObject.has("embedded")) {
                fileObject.getString("embedded")
              } else {
                fileObject.getString("location").let {
                  val responseFromLocation = apiService.getFromLocation(it)
                  val responseBodyFromLocation = responseFromLocation.body()?.string()
                  if (!responseBodyFromLocation.isNullOrBlank()) {
                    responseBodyFromLocation
                  } else {
                    null
                  }
                }
              }
            }.toTypedArray()
          }
          decodeEmbeddedArray(embeddedArray)
        } else {
          Timber.e("Empty response body")
          null
        }
      } else {
        Timber.e("HTTP Error: ${response.code()}")
        null
      }
    } catch (err: Error) {
      Timber.e("Error posting to the manifest: $err")
      null
    }
  }

  private fun decodeEmbeddedArray(embeddedArray: Array<String>): Bundle {
    var healthData = ""
    for (elem in embeddedArray) {
      val decodedShc = shLinkScanData.key.let { readSHLinkUtils.decodeShc(elem, it) }
      if (decodedShc != "") {
        val toDecode = readSHLinkUtils.extractVerifiableCredential(decodedShc)
        if (toDecode.isEmpty()) {
          healthData = decodedShc
          break
        }
        val obj = JSONObject(readSHLinkUtils.decodeAndDecompressPayload(toDecode))
        val doc =
          obj.getJSONObject("vc").getJSONObject("credentialSubject").getJSONObject("fhirBundle")
        return parser.parseResource(doc.toString()) as Bundle
      }
    }
    return parser.parseResource(healthData) as Bundle
  }

  private fun constructShlObj() {
    shLinkScanDataInput?.fullLink?.let { fullLink ->
      val extractedJson = readSHLinkUtils.extractUrl(fullLink)
      val decodedJson = readSHLinkUtils.decodeUrl(extractedJson)
      val jsonObject = JSONObject(String(decodedJson, StandardCharsets.UTF_8))

      shLinkScanData = SHLinkScanData(
        shLinkScanDataInput.fullLink,
        extractedJson,
        jsonObject.optString("url", ""),
        key = jsonObject.optString("key", ""),
        flag = jsonObject.optString("flag", "")
      )
    }
  }
}