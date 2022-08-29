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

package com.google.android.fhir.sync.remote

import com.google.android.fhir.BuildConfig
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.sync.Authenticator
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.ProgressCallback
import com.google.android.fhir.sync.ProgressInterceptor
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Tag
import retrofit2.http.Url

/** Interface to make http requests to the FHIR server. */
internal interface RemoteFhirService : DataSource {

  @GET override suspend fun download(@Url path: String): Resource

  @POST(".")
  override suspend fun upload(
    @Body bundle: Bundle,
    @Tag progressCallback: ProgressCallback?
  ): Resource

  class Builder(
    private val baseUrl: String,
    private val networkConfiguration: NetworkConfiguration
  ) {
    private var authenticator: Authenticator? = null

    fun setAuthenticator(authenticator: Authenticator?) {
      this.authenticator = authenticator
    }

    fun build(): RemoteFhirService {
      val logger = HttpLoggingInterceptor()
      logger.level =
        if (BuildConfig.DEBUG)
          HttpLoggingInterceptor.Level.BASIC // TODO change it ??????????????????
        else HttpLoggingInterceptor.Level.BASIC
      val client =
        OkHttpClient.Builder()
          .apply {
            connectTimeout(networkConfiguration.connectionTimeOut, TimeUnit.SECONDS)
            readTimeout(networkConfiguration.readTimeOut, TimeUnit.SECONDS)
            writeTimeout(networkConfiguration.writeTimeOut, TimeUnit.SECONDS)
            addInterceptor(logger)
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
              addNetworkInterceptor(ProgressInterceptor())
            }
          }
          .build()
      return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(FhirConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RemoteFhirService::class.java)
    }
  }

  companion object {
    fun builder(baseUrl: String, networkConfiguration: NetworkConfiguration) =
      Builder(baseUrl, networkConfiguration)
  }
}
