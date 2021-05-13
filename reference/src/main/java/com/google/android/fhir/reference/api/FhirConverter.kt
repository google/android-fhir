/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.reference.api

import ca.uhn.fhir.parser.IParser
import java.lang.reflect.Type
import okhttp3.ResponseBody
import org.hl7.fhir.r4.model.Resource
import retrofit2.Converter
import retrofit2.Retrofit

class FhirConverterFactory(private val parser: IParser) : Converter.Factory() {
  override fun responseBodyConverter(
    type: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *> {
    return FhirConverter(parser)
  }
}

/** Retrofit converter that allows us to parse FHIR resources */
private class FhirConverter(private val parser: IParser) : Converter<ResponseBody, Resource> {
  override fun convert(value: ResponseBody): Resource {
    return parser.parseResource(value.string()) as Resource
  }
}
