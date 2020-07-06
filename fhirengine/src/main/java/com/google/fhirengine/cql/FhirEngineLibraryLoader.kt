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

package com.google.fhirengine.cql

import com.google.fhirengine.db.Database
import org.cqframework.cql.elm.execution.Library
import org.cqframework.cql.elm.execution.VersionedIdentifier
import org.opencds.cqf.cql.execution.JsonCqlLibraryReader
import org.opencds.cqf.cql.execution.LibraryLoader
import java.io.IOException

/**
 * FHIR Engine's implementation of [LibraryLoader] that loads a CQL/ELM library for the [ ] to use.
 */
internal class FhirEngineLibraryLoader(private val database: Database) : LibraryLoader {
  /** Cached libraries.  */
  private val _libraryMap = mutableMapOf<String, Library>()
  val libraryMap: Map<String, Library>
    get() = _libraryMap

  override fun load(libraryIdentifier: VersionedIdentifier): Library {
    val matchedLibrary = libraryMap
      .asSequence()
      .filter { it.key.contains(libraryIdentifier.id) }
      .map { it.value }
      .firstOrNull()
    if (matchedLibrary != null) return matchedLibrary
//    for ((key, value) in _libraryMap) {
//      // TODO: Change this to an exact match once the libraries are correctly indexed by their name
//      //  instead of FHIR resource ID.
//      if (key.contains(libraryIdentifier.id)) {
//        return value
//      }
//    }
    val fhirLibrary = database.searchByString(
      org.hl7.fhir.r4.model.Library::class.java,
      LIBRARY_NAME_INDEX,
      libraryIdentifier.id
    )
    // TODO: remove the assumption that there will be only one FHIR library resource which has one
    //  content element.
    val stringReader = String(fhirLibrary.first().content.first().data).reader()
    return try {
      val cqlLibrary = JsonCqlLibraryReader.read(stringReader)
      _libraryMap[libraryIdentifier.id] = cqlLibrary
      cqlLibrary
    } catch (e: IOException) {
      // TODO: Replace this with a logger call
      e.printStackTrace()
      throw RuntimeException(e)
    }
  }

  companion object {
    /** The index for library name.  */
    private const val LIBRARY_NAME_INDEX = "Library.name"
  }
}
