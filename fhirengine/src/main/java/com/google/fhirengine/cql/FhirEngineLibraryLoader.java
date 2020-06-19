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

package com.google.fhirengine.cql;

import com.google.fhirengine.db.Database;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.execution.JsonCqlLibraryReader;
import org.opencds.cqf.cql.execution.LibraryLoader;

/**
 * FHIR Engine's implementation of {@link LibraryLoader} that loads a CQL/ELM library for the {@link
 * org.opencds.cqf.cql.execution.CqlEngine} to use.
 */
public class FhirEngineLibraryLoader implements LibraryLoader {
  /** The index for library name. */
  private static final String LIBRARY_NAME_INDEX = "Library.name";

  private final Database database;

  /** Cached libraries. */
  Map<String, Library> libraryMap = new HashMap<>();

  public FhirEngineLibraryLoader(Database database) {
    this.database = database;
  }

  @Override
  public Library load(VersionedIdentifier libraryIdentifier) {
    for (String key : libraryMap.keySet()) {
      // TODO: Change this to an exact match once the libraries are correctly indexed by their name
      //  instead of FHIR resource ID.
      if (key.contains(libraryIdentifier.getId())) {
        return libraryMap.get(key);
      }
    }
    List<org.hl7.fhir.r4.model.Library> fhirLibrary =
        database.searchByString(
            org.hl7.fhir.r4.model.Library.class, LIBRARY_NAME_INDEX, libraryIdentifier.getId());

    // TODO: remove the assumption that there will be only one FHIR library resource which has one
    // content element.
    StringReader stringReader =
        new StringReader(new String(fhirLibrary.get(0).getContent().get(0).getData()));
    try {
      Library cqlLibrary = JsonCqlLibraryReader.read(stringReader);

      libraryMap.put(libraryIdentifier.getId(), cqlLibrary);
      return cqlLibrary;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
