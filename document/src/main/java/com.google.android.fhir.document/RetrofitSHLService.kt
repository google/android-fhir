/*
 * Copyright 2023-2024 Google LLC
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

import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.sync.remote.GzipUploadInterceptor
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

/* Interface to make HTTP requests to the SHL server */
interface RetrofitSHLService {

  /* Initial POST request to generate a manifest URL */
  @POST
  @Headers("Content-Type: application/json")
  suspend fun getManifestUrlAndToken(
    @Url path: String,
    @Body request: RequestBody,
  ): Response<ResponseBody>

  /* POST request to add data to the SHL */
  @POST
  @Headers("Content-Type: application/json")
  suspend fun postPayload(
    @Url path: String,
    @Body contentEncrypted: String,
    @Header("Authorization") authorization: String,
  ): Response<ResponseBody>

  /* POST request to the SHL's manifest url to get the list of files associated with the link */
  @POST
  @Headers("Content-Type: application/json")
  suspend fun getFilesFromManifest(
    @Url path: String,
    @Body jsonData: JSONObject,
  ): Response<ResponseBody>

  /* GET request if files are stored in an external "location" */
  @GET
  suspend fun getFromLocation(
    @Url path: String,
  ): Response<ResponseBody>

  class Builder(
    private val baseUrl: String,
    private val networkConfiguration: NetworkConfiguration,
  ) {
    private var httpLoggingInterceptor: HttpLoggingInterceptor? = null

    fun build(): RetrofitSHLService {
      val client =
        OkHttpClient.Builder()
          .connectTimeout(networkConfiguration.connectionTimeOut, TimeUnit.SECONDS)
          .readTimeout(networkConfiguration.readTimeOut, TimeUnit.SECONDS)
          .writeTimeout(networkConfiguration.writeTimeOut, TimeUnit.SECONDS)
          .apply {
            if (networkConfiguration.uploadWithGzip) {
              addInterceptor(GzipUploadInterceptor)
            }
            httpLoggingInterceptor?.let { addInterceptor(it) }
          }
          .build()
      return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitSHLService::class.java)
    }
  }
}
