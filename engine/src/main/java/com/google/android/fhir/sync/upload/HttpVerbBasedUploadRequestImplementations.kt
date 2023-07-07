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

package com.google.android.fhir.sync.upload

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.sync.DeleteUploadRequest
import com.google.android.fhir.sync.PatchUploadRequest
import com.google.android.fhir.sync.PostUploadRequest
import com.google.android.fhir.sync.PutUploadRequest
import com.google.android.fhir.sync.UploadRequest
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource

class HttpPostForCreateUploadRequestGenerator(useETagForUpload: Boolean) :
  HttpVerbBasedUploadRequestGenerator(Bundle.HTTPVerb.PUT, useETagForUpload) {

  override fun getUploadRequest(localChange: LocalChange): UploadRequest {
    val resource: Resource =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(localChange.payload)
        as Resource
    return PostUploadRequest(
      localChangeToken = localChange.token,
      resource = resource,
      resourceType = resource.fhirType()
    )
  }
}

class HttpPutForUpdateUploadRequestGenerator(useETagForUpload: Boolean) :
  HttpVerbBasedUploadRequestGenerator(Bundle.HTTPVerb.PUT, useETagForUpload) {

  override fun getUploadRequest(localChange: LocalChange): UploadRequest {
    val resource =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(localChange.payload)
        as Resource
    val headerMap = addIfMatchHeader(localChange, mutableMapOf())
    return PutUploadRequest(
      localChangeToken = localChange.token,
      resource = resource,
      resourceId = resource.idElement.idPart,
      resourceType = resource.fhirType(),
      headers = headerMap
    )
  }
}

class HttpPutForCreateUploadRequestGenerator(useETagForUpload: Boolean) :
  HttpVerbBasedUploadRequestGenerator(Bundle.HTTPVerb.PUT, useETagForUpload) {

  override fun getUploadRequest(localChange: LocalChange): UploadRequest {
    val resource =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(localChange.payload)
        as Resource
    return PutUploadRequest(
      localChangeToken = localChange.token,
      resource = resource,
      resourceId = resource.idElement.idPart,
      resourceType = resource.fhirType()
    )
  }
}

class HttpPatchForUpdateUploadRequestGenerator(useETagForUpload: Boolean) :
  HttpVerbBasedUploadRequestGenerator(Bundle.HTTPVerb.PATCH, useETagForUpload) {
  override fun getUploadRequest(localChange: LocalChange): UploadRequest {
    val headerMap = addIfMatchHeader(localChange, mutableMapOf())
    return PatchUploadRequest(
      localChangeToken = localChange.token,
      patchBody = localChange.payload,
      resourceId = localChange.resourceId,
      resourceType = localChange.resourceType,
      headers = headerMap
    )
  }
}

class HttpDeleteUploadRequestGenerator(useETagForUpload: Boolean) :
  HttpVerbBasedUploadRequestGenerator(Bundle.HTTPVerb.DELETE, useETagForUpload) {
  override fun getUploadRequest(localChange: LocalChange): UploadRequest {
    val headerMap = addIfMatchHeader(localChange, mutableMapOf())
    return DeleteUploadRequest(
      localChangeToken = localChange.token,
      resourceId = localChange.resourceId,
      resourceType = localChange.resourceType,
      headers = headerMap
    )
  }
}
