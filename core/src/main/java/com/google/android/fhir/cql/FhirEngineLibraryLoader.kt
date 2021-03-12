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

package com.google.android.fhir.cql

import android.util.Log
import com.google.android.fhir.db.Database
import java.io.IOException
import java.io.StringReader
import org.cqframework.cql.elm.execution.Library
import org.cqframework.cql.elm.execution.VersionedIdentifier
import org.opencds.cqf.cql.execution.JsonCqlLibraryReader
import org.opencds.cqf.cql.execution.LibraryLoader

/**
 * FHIR Engine's implementation of [LibraryLoader] that loads a CQL/ELM library for the [ ] to use.
 */
private const val TAG = "FEngineLibraryLoader"

internal class FhirEngineLibraryLoader(private val database: Database) : LibraryLoader {
  /** Cached libraries. */
  private val _libraryMap = mutableMapOf<String, Library>()
  val libraryMap: Map<String, Library>
    get() = _libraryMap

  override fun load(libraryIdentifier: VersionedIdentifier): Library? {
    val matchedLibrary =
      libraryMap
        .asSequence()
        .filter {
          // TODO: Change this to an exact match once the libraries are correctly indexed by name
          it.key.contains(libraryIdentifier.id)
        }
        .map { it.value }
        .firstOrNull()
    if (matchedLibrary != null) return matchedLibrary
    val fhirLibrary: List<org.hl7.fhir.r4.model.Library>? =
      database.searchByString(
        org.hl7.fhir.r4.model.Library::class.java,
        LIBRARY_NAME_INDEX,
        libraryIdentifier.id
      )
    // TODO: remove the assumption that there will be only one FHIR library resource which has one
    //  content element.

    val stringReader: StringReader? =
      fhirLibrary?.first()?.content?.first()?.let { String(it.data).reader() }

    try {
      stringReader?.let {
        val cqlLibrary = JsonCqlLibraryReader.read(it)
        _libraryMap[libraryIdentifier.id] = cqlLibrary
        Log.d("deb: ", cqlLibrary.toString())
        return cqlLibrary
      }
    } catch (e: IOException) {
      // TODO: Replace this with a logger call
      Log.d("deb: ", "load: throwing exception: $e")
      e.printStackTrace()
      throw RuntimeException(e)
    }
    return null
  }

  companion object {
    /** The index for library name. */
    private const val LIBRARY_NAME_INDEX = "Library.name"
  }
}
