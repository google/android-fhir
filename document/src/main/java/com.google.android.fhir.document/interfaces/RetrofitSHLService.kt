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

package com.google.android.fhir.document.interfaces

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

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
