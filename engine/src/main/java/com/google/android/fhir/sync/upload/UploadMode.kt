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

package com.google.android.fhir.sync.upload

sealed class FetchStrategy {
  data class AllChanges(val pageSize: Int) : FetchStrategy()
  object PerResource : FetchStrategy()
  object EarliestChange : FetchStrategy()
}

sealed class PatchStrategy {
  object Direct : PatchStrategy()
  object Squash : PatchStrategy()
}

sealed class RequestBuilderStrategy {
  object SinglePUT : RequestBuilderStrategy()
  object SinglePOST : RequestBuilderStrategy()
  object BundlePUT : RequestBuilderStrategy()
  object BundlePOST : RequestBuilderStrategy()
}

sealed class ResourceConsolidatorStrategy {
  object IDUpdater : ResourceConsolidatorStrategy()
  object Default : ResourceConsolidatorStrategy()
}

sealed class UploadMode
private constructor(
  val fetchStrategy: FetchStrategy,
  val patchStrategy: PatchStrategy,
  val requestBuilderStrategy: RequestBuilderStrategy,
  val resourceConsolidatorStrategy: ResourceConsolidatorStrategy
) {
  object SINGLE_CHANGE_PUT :
    UploadMode(
      FetchStrategy.EarliestChange,
      PatchStrategy.Direct,
      RequestBuilderStrategy.SinglePUT,
      ResourceConsolidatorStrategy.Default
    )
  object SINGLE_CHANGE_POST :
    UploadMode(
      FetchStrategy.EarliestChange,
      PatchStrategy.Direct,
      RequestBuilderStrategy.SinglePOST,
      ResourceConsolidatorStrategy.IDUpdater
    )
  object SINGLE_RESOURCE_PUT :
    UploadMode(
      FetchStrategy.PerResource,
      PatchStrategy.Squash,
      RequestBuilderStrategy.SinglePUT,
      ResourceConsolidatorStrategy.Default
    )
  object SINGLE_RESOURCE_POST :
    UploadMode(
      FetchStrategy.PerResource,
      PatchStrategy.Squash,
      RequestBuilderStrategy.SinglePOST,
      ResourceConsolidatorStrategy.IDUpdater
    )
  data class ALL_CHANGES_BUNDLE_PUT(val pageSize: Int) :
    UploadMode(
      FetchStrategy.AllChanges(pageSize),
      PatchStrategy.Direct,
      RequestBuilderStrategy.BundlePUT,
      ResourceConsolidatorStrategy.Default
    )
  data class ALL_CHANGES_BUNDLE_POST(val pageSize: Int) :
    UploadMode(
      FetchStrategy.AllChanges(pageSize),
      PatchStrategy.Direct,
      RequestBuilderStrategy.BundlePOST,
      ResourceConsolidatorStrategy.IDUpdater
    )
  data class ALL_CHANGES_SQUASHED_BUNDLE_PUT(val pageSize: Int) :
    UploadMode(
      FetchStrategy.AllChanges(pageSize),
      PatchStrategy.Squash,
      RequestBuilderStrategy.BundlePUT,
      ResourceConsolidatorStrategy.Default
    )
  data class ALL_CHANGES_SQUASHED_BUNDLE_POST(val pageSize: Int) :
    UploadMode(
      FetchStrategy.AllChanges(pageSize),
      PatchStrategy.Squash,
      RequestBuilderStrategy.BundlePOST,
      ResourceConsolidatorStrategy.IDUpdater
    )
}
