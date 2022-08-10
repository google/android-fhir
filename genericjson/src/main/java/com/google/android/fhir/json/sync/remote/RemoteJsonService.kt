/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.json.sync.remote

import com.google.android.fhir.common.BuildConfig
import com.google.android.fhir.json.NetworkConfiguration
import com.google.android.fhir.json.sync.Authenticator
import com.google.android.fhir.json.sync.DataSource
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Interface to make http requests to the FHIR server. */
internal interface RemoteJsonService : DataSource {

  @POST("/{path}")
  override suspend fun download(
    @Path("path") path: String,
    @Body extraBody: String,
  ): JSONObject

  @PUT("{type}/{id}")
  override suspend fun upload(
    @Path("type") type: String,
    @Path("id") id: String,
    @Body jsonObject: JSONObject,
  ): JSONObject

  class Builder(
    private val baseUrl: String,
    private val networkConfiguration: NetworkConfiguration
  ) {
    private var authenticator: Authenticator? = null

    fun setAuthenticator(authenticator: Authenticator?) {
      this.authenticator = authenticator
    }

    fun build(): RemoteJsonService {
      val logger = HttpLoggingInterceptor()
      logger.level =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.BASIC
      val client =
        OkHttpClient.Builder()
          .apply {
            connectTimeout(networkConfiguration.connectionTimeOut, TimeUnit.SECONDS)
            readTimeout(networkConfiguration.readTimeOut, TimeUnit.SECONDS)
            writeTimeout(networkConfiguration.writeTimeOut, TimeUnit.SECONDS)
            addInterceptor(logger)
            addInterceptor(
              Interceptor { chain: Interceptor.Chain ->
                val request =
                  chain
                    .request()
                    .newBuilder()
                    .addHeader(
                      "x-access-token",
                      "TOKEN_HERE"
                    )
                    .build()
                chain.proceed(request)
              }
            )
            authenticator?.let {
              addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                  val accessToken = it.getAccessToken()
                  val request =
                    chain
                      .request()
                      .newBuilder()
                      .addHeader("Authorization", "Bearer $accessToken")
                      .build()
                  chain.proceed(request)
                }
              )
            }
          }
          .build()
      return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(JsonConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RemoteJsonService::class.java)
    }
  }

  companion object {
    fun builder(baseUrl: String, networkConfiguration: NetworkConfiguration) =
      Builder(baseUrl, networkConfiguration)
  }
}
