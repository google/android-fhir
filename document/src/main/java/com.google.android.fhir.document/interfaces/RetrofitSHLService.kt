package com.google.android.fhir.document.interfaces

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

/* Interface to make HTTP requests to the SHL server */
interface RetrofitSHLService {

  /* Initial POST request to generate a manifest URL */
  @Headers("Content-Type: application/json")
  @POST("shl")
  suspend fun getManifestUrlAndToken(@Body request: RequestBody): Response<ResponseBody>


  /* POST request to add data to the SHL */
  @POST("file")
  @Headers("Content-Type: application/json")
  suspend fun postPayload(
    @Body contentEncrypted: String,
    @Header("Authorization") authorization: String,
  ): Response<ResponseBody>

}
