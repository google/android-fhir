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

package com.google.android.fhir.knowledge.npm

import com.google.android.fhir.knowledge.FhirNpmPackage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.GZIPInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream

/** Downloads Npm package from the provided package server using OkHttp library. */
internal class OkHttpNpmPackageDownloader(
  private val packageServerUrl: String,
) : NpmPackageDownloader {

  val client = OkHttpClient()

  @Throws(IOException::class)
  override suspend fun downloadPackage(
    fhirNpmPackage: FhirNpmPackage,
    packageFolder: File,
  ) {
    return withContext(Dispatchers.IO) {
      val packageName = fhirNpmPackage.name
      val version = fhirNpmPackage.version
      val url = "$packageServerUrl$packageName/$version"

      val request = Request.Builder().url(url).get().build()

      val response = client.newCall(request).execute()

      if (!response.isSuccessful) {
        throw IOException("Unexpected code $response")
      }

      response.body?.use { responseBody ->
        packageFolder.mkdirs()
        val tgzFile = File(packageFolder, "$packageName-$version.tgz")
        saveResponseToFile(responseBody, tgzFile)

        extractTgzFile(tgzFile, packageFolder)

        tgzFile.delete()
      }
    }
  }

  private fun saveResponseToFile(responseBody: ResponseBody, file: File) {
    FileOutputStream(file).use { fileOutputStream ->
      val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
      var bytesRead: Int
      while (responseBody.byteStream().read(buffer).also { bytesRead = it } != -1) {
        fileOutputStream.write(buffer, 0, bytesRead)
      }
    }
  }

  private fun extractTgzFile(tgzFile: File, outputFolder: File) {
    FileInputStream(tgzFile).use { fileInputStream ->
      outputFolder.mkdirs()
      GZIPInputStream(fileInputStream).use { gzipInputStream ->
        TarArchiveInputStream(gzipInputStream).use { tarInputStream ->
          var entry: TarArchiveEntry? = tarInputStream.nextTarEntry
          while (entry != null) {
            val outputFile = File(outputFolder, entry.name)

            if (entry.isDirectory) {
              outputFile.mkdirs()
            } else {
              outputFile.parentFile?.mkdirs()

              val outputFileStream = FileOutputStream(outputFile)
              val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
              var bytesRead: Int
              while (tarInputStream.read(buffer).also { bytesRead = it } != -1) {
                outputFileStream.write(buffer, 0, bytesRead)
              }
              outputFileStream.close()
            }

            entry = tarInputStream.nextTarEntry
          }
        }
      }
    }
  }
}
