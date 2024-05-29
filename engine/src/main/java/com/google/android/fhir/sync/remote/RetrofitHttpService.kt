/*
 * Copyright 2023 Google LLC
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

import com.github.fge.jsonpatch.JsonPatch
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.sync.HttpAuthenticator
import java.util.concurrent.TimeUnit
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.hl7.fhir.r4.model.Resource
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

/** Retrofit service to make http requests to the FHIR server. */
internal interface RetrofitHttpService : FhirHttpService {

  @GET
  override suspend fun get(@Url path: String, @HeaderMap headers: Map<String, String>): Resource

  @POST
  override suspend fun post(
    @Url path: String,
    @Body resource: Resource,
    @HeaderMap headers: Map<String, String>,
  ): Resource

  @PUT
  override suspend fun put(
    @Url path: String,
    @Body resource: Resource,
    @HeaderMap headers: Map<String, String>,
  ): Resource

  @PATCH
  override suspend fun patch(
    @Url path: String,
    @Body patchDocument: JsonPatch,
    @HeaderMap headers: Map<String, String>,
  ): Resource

  @DELETE
  override suspend fun delete(@Url path: String, @HeaderMap headers: Map<String, String>): Resource

  class Builder(
    private val baseUrl: String,
    private val networkConfiguration: NetworkConfiguration,
  ) {
    private var authenticator: HttpAuthenticator? = null
    private var httpLoggingInterceptor: HttpLoggingInterceptor? = null

    fun setAuthenticator(authenticator: HttpAuthenticator?) = apply {
      this.authenticator = authenticator
    }

    fun setHttpLogger(httpLogger: HttpLogger) = apply {
      httpLoggingInterceptor = httpLogger.toOkHttpLoggingInterceptor()
    }

    fun build(): RetrofitHttpService {
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
            authenticator?.let {
              addInterceptor(
                Interceptor { chain: Interceptor.Chain ->
                  val request =
                    chain
                      .request()
                      .newBuilder()
                      .addHeader(
                        "Authorization",
                        it.getAuthenticationMethod().getAuthorizationHeader(),
                      )
                      .build()
                  chain.proceed(request)
                },
              )
            }
            networkConfiguration.httpCache?.let { this.cache(Cache(it.cacheDir, it.maxSize)) }
          }
          .build()
      return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(FhirConverterFactory.create())
        .build()
        .create(RetrofitHttpService::class.java)
    }
  }

  companion object {
    fun builder(baseUrl: String, networkConfiguration: NetworkConfiguration) =
      Builder(baseUrl, networkConfiguration)
  }
}
