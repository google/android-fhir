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

/** Splits the [List]<[LocalChange]> into smaller chunks. */
internal fun interface LocalChangesPaginator {
  fun page(list: List<LocalChange>): List<List<LocalChange>>

  companion object Factory {

    fun create(uploadConfiguration: UploadConfiguration): LocalChangesPaginator {
      require(uploadConfiguration.uploadBundleSize > 0) {
        "UploadConfiguration.uploadBundleSize ${uploadConfiguration.uploadBundleSize} must be greater than zero."
      }

      return SizeBasedLocalChangesPaginator(uploadConfiguration.uploadBundleSize)
    }

    val DEFAULT = create(UploadConfiguration())
  }
}

/**
 * Splits the [List]<[LocalChange]> into equal chunks of provided [size]. The last chunk may have
 * fewer elements than the [size].
 */
internal class SizeBasedLocalChangesPaginator(val size: Int) : LocalChangesPaginator {
  override fun page(list: List<LocalChange>) = list.chunked(size)
}
