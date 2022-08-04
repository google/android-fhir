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

import android.content.Context
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.sync.Authenticator
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.remote.RemoteServiceLoggingHelper.SYNC_FOLDER
import com.google.android.fhir.sync.remote.RemoteServiceLoggingHelper.getSyncLogsDirectory
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
import retrofit2.http.Url
import timber.log.Timber

/** Interface to make http requests to the FHIR server. */
internal interface RemoteFhirService : DataSource {

  @GET override suspend fun download(@Url path: String): Resource

  @POST(".") override suspend fun upload(@Body bundle: Bundle): Resource

  class Builder(
    private val context: Context,
    private val baseUrl: String,
    private val networkConfiguration: NetworkConfiguration
  ) {
    private var authenticator: Authenticator? = null
    private var logFileNamePostFix: String = ""

    fun setLogFilePostFix(logFileNamePostFix: String) {
      this.logFileNamePostFix = logFileNamePostFix
    }

    fun setAuthenticator(authenticator: Authenticator?) {
      this.authenticator = authenticator
    }

    fun build(): RemoteFhirService {
      val dateString = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())
      val fullFileName = dateString + "_" + logFileNamePostFix
      val customLogger =
        HttpLoggingInterceptor.Logger {
          try {
            writeToFile(fullFileName, it)
          } catch (exception: FileNotFoundException) {
            Timber.i("File not found")
          }
        }
      val logger = HttpLoggingInterceptor(customLogger)
      logger.level = HttpLoggingInterceptor.Level.HEADERS

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

    private fun writeToFile(fileName: String, content: String) {
      File(getSyncLogsDirectory(context, SYNC_FOLDER), fileName).appendText(content + "\n")
    }
  }

  companion object {
    fun builder(context: Context, baseUrl: String, networkConfiguration: NetworkConfiguration) =
      Builder(context, baseUrl, networkConfiguration)
  }
}
