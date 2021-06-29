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

package com.google.android.fhir.datacapture.utilities

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.utils.IOUtils

/**
 * Provides utility for decompressing tar.gz or tgz files using the commons-io and commons-compress
 * libraries
 */
object TarGzipUtility {

  @Throws(IOException::class)
  fun decompress(compressedFilePath: String?, out: File?) {
    TarArchiveInputStream(GzipCompressorInputStream(FileInputStream(compressedFilePath))).use { fin
      ->
      var entry: TarArchiveEntry?
      // Fix `it` can be null
      while (fin.nextTarEntry.also { entry = it } != null) {
        if (entry!!.isDirectory) {
          continue
        }
        val curfile = File(out, entry!!.name)
        val parent = curfile.parentFile
        if (parent != null && !parent.exists()) {
          parent.mkdirs()
        }

        IOUtils.copy(fin, FileOutputStream(curfile))
      }
    }
  }
}
