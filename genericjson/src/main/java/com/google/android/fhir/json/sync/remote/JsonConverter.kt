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

import com.google.android.fhir.json.MediaTypes
import java.lang.reflect.Type
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit

internal class JsonConverterFactory private constructor() : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *> = JsonResponseBodyConverter()

  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<out Annotation>,
    methodAnnotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<*, RequestBody> = JsonRequestBodyConverter()

  companion object {
    fun create() = JsonConverterFactory()
  }
}

/** Retrofit converter that allows us to parse FHIR resources in the response. */
private class JsonResponseBodyConverter() : Converter<ResponseBody, JSONObject> {
  override fun convert(value: ResponseBody): JSONObject {
    return JSONObject(value.string())
  }
}

/** Retrofit converter that allows us to parse FHIR resources in the requests. */
private class JsonRequestBodyConverter() : Converter<JSONObject, RequestBody> {
  override fun convert(value: JSONObject): RequestBody {
    return value.toString().toRequestBody(MediaTypes.MEDIA_TYPE_JSON)
  }
}
