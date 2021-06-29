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
