/*
 * Copyright 2021 Google LLC
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

import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.BuildConfig
import com.google.android.fhir.sync.Authenticator
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.hl7.fhir.r4.model.Resource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/** Interface to make http requests to the FHIR server. */
internal interface RemoteFhirService {

  @GET suspend fun download(@Url url: String): Resource

  @POST(".") suspend fun upload(@Body body: RequestBody): Resource

  companion object {

    fun create(baseUrl: String, parser: IParser, authenticator: Authenticator?): RemoteFhirService {
      val logger = HttpLoggingInterceptor()
      logger.level =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.BASIC
      val client =
        OkHttpClient.Builder()
          .apply {
            addInterceptor(logger)
            authenticator?.let {
              addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                  val accessToken = runBlocking { authenticator.getAccessToken() }
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
        .addConverterFactory(FhirConverterFactory(parser))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RemoteFhirService::class.java)
    }
  }
}
