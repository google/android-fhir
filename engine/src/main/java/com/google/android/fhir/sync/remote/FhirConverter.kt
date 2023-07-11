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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.MediaTypes
import java.lang.reflect.Type
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.hl7.fhir.r4.model.Resource
import retrofit2.Converter
import retrofit2.Retrofit

class FhirConverterFactory private constructor(val fhirContext: FhirContext) : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *> = FhirResponseBodyConverter(fhirContext.newJsonParser())

  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<out Annotation>,
    methodAnnotations: Array<out Annotation>,
    retrofit: Retrofit
  ): Converter<*, RequestBody> = FhirRequestBodyConverter(fhirContext.newJsonParser())

  companion object {
    fun create() = FhirConverterFactory(FhirContext.forCached(FhirVersionEnum.R4))
  }
}

/** Retrofit converter that allows us to parse FHIR resources in the response. */
private class FhirResponseBodyConverter(private val parser: IParser) :
  Converter<ResponseBody, Resource> {
  override fun convert(value: ResponseBody): Resource {
    return parser.parseResource(value.string()) as Resource
  }
}

/** Retrofit converter that allows us to parse FHIR resources in the requests. */
private class FhirRequestBodyConverter(private val parser: IParser) :
  Converter<Resource, RequestBody> {
  override fun convert(value: Resource): RequestBody {
    return parser.encodeResourceToString(value).toRequestBody(MediaTypes.MEDIA_TYPE_FHIR_JSON)
  }
}
