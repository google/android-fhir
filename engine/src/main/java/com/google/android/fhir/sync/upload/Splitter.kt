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

import com.google.android.fhir.LocalChange
import com.google.android.fhir.sync.UploadConfiguration
import org.hl7.fhir.r4.model.ResourceType

/** Splits the [List]<[LocalChange]> into smaller chunks. */
internal fun interface Splitter {
  fun split(list: List<LocalChange>): List<List<LocalChange>>

  companion object Factory {

    fun create(uploadConfiguration: UploadConfiguration): Splitter {
      require(uploadConfiguration.uploadBundleSize > 0) {
        "UploadConfiguration.uploadBundleSize ${uploadConfiguration.uploadBundleSize} must be greater than zero."
      }
      return when {
        uploadConfiguration.resourceTypes.isNullOrEmpty() ->
          SizeSplitter(uploadConfiguration.uploadBundleSize)
        else ->
          SizeAndTypeSplitter(
            uploadConfiguration.uploadBundleSize,
            uploadConfiguration.resourceTypes
          )
      }
    }

    val DEFAULT = create(UploadConfiguration())
  }
}

/**
 * Splits the [List]<[LocalChange]> into equal chunks of provided [size]. The last chunk may have
 * fewer elements than the [size].
 */
internal class SizeSplitter(val size: Int) : Splitter {
  override fun split(list: List<LocalChange>) = list.chunked(size)
}

/**
 * Splits the [List]<[LocalChange]> into equal chunks of provided [size] ordered by [ResourceType].
 * The last chunk may have fewer elements than the [size]. In case resource types are missing in the
 * provided order, they will be pushed to the end.
 */
internal class SizeAndTypeSplitter(val size: Int, val order: List<ResourceType>) : Splitter {
  override fun split(list: List<LocalChange>): List<List<LocalChange>> =
    list
      .sortedBy { localChange ->
        order.indexOf(ResourceType.fromCode(localChange.resourceType)).let {
          if (it >= 0) it else Int.MAX_VALUE
        }
      }
      .chunked(size)
}
