package com.google.android.fhir.document

import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface RetrofitSHLService {
  @GET
  suspend fun get(@Url path: String, @HeaderMap headers: Map<String, String>): JSONObject

  @POST("file")
  @Headers("Content-Type: application/json")
  suspend fun postPayload(
    @Body contentEncrypted: String,
    @Header("Authorization") authorization: String,
  ): Response<ResponseBody>

  @Headers("Content-Type: application/json")
  @POST("shl")
  suspend fun getManifestUrlAndToken(@Body request: RequestBody): Response<ResponseBody>

}
