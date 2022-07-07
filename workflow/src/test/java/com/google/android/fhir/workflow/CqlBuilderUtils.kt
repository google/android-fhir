/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.workflow

import java.io.InputStream
import java.io.StringReader
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.CqlTranslatorException
import org.cqframework.cql.cql2elm.FhirLibrarySourceProvider
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Library
import org.opencds.cqf.cql.engine.execution.JsonCqlLibraryReader

internal object CqlBuilderUtils {
  private fun load(asset: InputStream): String {
    return asset.bufferedReader().use { bufferReader -> bufferReader.readText() }
  }

  /**
   * Compiles a CQL Text to ELM
   *
   * @param cqlText the CQL Library
   * @return a [CqlTranslator] object that contains the elm representation of the libray inside it.
   */
  fun compile(cqlText: InputStream): CqlTranslator {
    val modelManager = ModelManager()
    val libraryManager =
      LibraryManager(modelManager).apply {
        librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
      }

    val translator: CqlTranslator =
      CqlTranslator.fromStream(
        cqlText,
        modelManager,
        libraryManager,
        CqlTranslatorException.ErrorSeverity.Info,
        LibraryBuilder.SignatureLevel.All
      )

    return translator
  }

  /**
   * Assembles an ELM Library exported as a JSON in to a FHIRLibrary
   *
   * @param jxsonElmStr the JSON representation of the ELM Library
   * @param libName the Library name
   * @param libVersion the Library Version
   *
   * @return a FHIR Library that includes the ELM Library.
   */
  fun assembleFhirLib(jxsonElmStr: String, libName: String, libVersion: String): Library {
    val attachment =
      Attachment().apply {
        contentType = "application/elm+json"
        data = jxsonElmStr.toByteArray()
      }

    return Library().apply {
      id = "$libName-$libVersion"
      name = libName
      version = libVersion
      status = Enumerations.PublicationStatus.ACTIVE
      experimental = true
      url = "http://localhost/Library/$libName|$libVersion"
      addContent(attachment)
    }
  }

  /**
   * Parses a JSON representation of an ELM Library and assembles into a FHIR Library
   *
   * @param jsonElm the JSON representation of the ELM Library
   * @return the assembled FHIR Library
   */
  fun build(jsonElm: InputStream): Library {
    val strLib = load(jsonElm)
    val elmLibrary = JsonCqlLibraryReader.read(StringReader(strLib))
    return assembleFhirLib(strLib, elmLibrary.identifier.id, elmLibrary.identifier.version)
  }

  /**
   * Compiles a CQL Text into ELM and assembles a FHIR Library that includes a Base64 representation
   * of the JSON representation of the compiled ELM Library
   *
   * @param cqlText the CQL Library
   * @return the assembled FHIR Library
   */
  fun compileAndBuild(cqlInputStream: InputStream): Library {
    return compile(cqlInputStream).let {
      assembleFhirLib(it.toJxson(), it.toELM().identifier.id, it.toELM().identifier.version)
    }
  }
}
