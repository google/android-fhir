/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir.workflow.testing

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.io.InputStream
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Library
import org.junit.Assert.fail
import org.skyscreamer.jsonassert.JSONAssert

object CqlBuilder : Loadable() {
  private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  private fun IBaseResource.toJson(): String {
    return jsonParser.encodeResourceToString(this)
  }

  /**
   * Compiles a CQL InputStream to ELM
   *
   * @param cqlText the CQL Library
   * @return a [CqlTranslator] object that contains the elm representation of the library inside it.
   */
  fun compile(cqlText: InputStream): CqlTranslator {
    return compile(load(cqlText))
  }

  /**
   * Compiles a CQL Text to ELM
   *
   * @param cqlText the CQL Library
   * @return a [CqlTranslator] object that contains the elm representation of the library inside it.
   */
  fun compile(cqlText: String): CqlTranslator {
    val modelManager = ModelManager()
    val libraryManager =
      LibraryManager(modelManager).apply {
        librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
      }

    val translator = CqlTranslator.fromText(cqlText, libraryManager)

    // Helper makes sure the test CQL compiles. Reports an error if it doesn't
    if (translator.errors.isNotEmpty()) {
      val errors =
        translator.errors
          .map { "${it.locator?.toLocator() ?: "[n/a]"}: ${it.message}" }
          .joinToString("\n")

      fail("Could not compile CQL File. Errors:\n$errors")
    }

    return translator
  }

  /**
   * Assembles an ELM Library exported as a JSON into a FHIRLibrary
   *
   * @param jsonElmStr the JSON representation of the ELM Library
   * @param libName the Library name
   * @param libVersion the Library Version
   * @return a FHIR Library that includes the ELM Library.
   */
  fun assembleFhirLib(
    cqlStr: String?,
    jsonElmStr: String?,
    xmlElmStr: String?,
    libName: String,
    libVersion: String,
  ): Library {
    val attachmentCql =
      cqlStr?.let {
        Attachment().apply {
          contentType = "text/cql"
          data = it.toByteArray()
        }
      }

    val attachmentJson =
      jsonElmStr?.let {
        Attachment().apply {
          contentType = "application/elm+json"
          data = it.toByteArray()
        }
      }

    val attachmentXml =
      xmlElmStr?.let {
        Attachment().apply {
          contentType = "application/elm+xml"
          data = it.toByteArray()
        }
      }

    return Library().apply {
      id = "$libName-$libVersion"
      name = libName
      version = libVersion
      status = Enumerations.PublicationStatus.ACTIVE
      experimental = true
      url = "http://localhost/Library/$libName|$libVersion"
      attachmentCql?.let { addContent(it) }
      attachmentJson?.let { addContent(it) }
      attachmentXml?.let { addContent(it) }
    }
  }

  /**
   * Parses a JSON representation of an ELM Library and assembles into a FHIR Library
   *
   * @param jsonElm the JSON representation of the ELM Library
   * @return the assembled FHIR Library
   */

  /*
  fun buildJsonLib(jsonElm: InputStream): Library {
    val strLib = load(jsonElm)
    val elmLibrary =
      CqlLibraryReaderFactory.getReader("application/elm+json").read(StringReader(strLib))
    return assembleFhirLib(
      null,
      strLib,
      null,
      elmLibrary.identifier.id,
      elmLibrary.identifier.version,
    )
  }
   */

  /**
   * Compiles a CQL Text into ELM and assembles a FHIR Library that includes a Base64 representation
   * of the JSON representation of the compiled ELM Library
   *
   * @param cqlInputStream the CQL Library
   * @return the assembled FHIR Library
   */
  fun compileAndBuild(cqlInputStream: InputStream): Library {
    val cqlText = load(cqlInputStream)
    return compile(cqlText).let {
      assembleFhirLib(
        cqlText,
        it.toJson(),
        it.toXml(),
        it.toELM().identifier.id,
        it.toELM().identifier.version,
      )
    }
  }

  // Test Helpers

  object Assert {
    fun that(cqlAssetName: String) = Compiler(load(cqlAssetName))
  }

  class Compiler(val cqlText: String) {
    fun compiles() = CompiledCql(cqlText, compile(cqlText))
  }

  class CompiledCql(val cqlText: String, private val translator: CqlTranslator) {
    private lateinit var expectedElmJsonAsset: String
    private lateinit var expectedElmXmlAsset: String

    init {
      // Manually removes the version information to make tests pass.
      // Remove it after https://github.com/cqframework/clinical_quality_language/issues/804
      translator
        .toELM()
        .annotation
        .filterIsInstance(org.hl7.cql_annotations.r1.CqlToElmInfo::class.java)
        .forEach { it.translatorVersion = null }
    }

    fun withJsonEqualsTo(expectedElmJsonAssetName: String): CompiledCql {
      expectedElmJsonAsset = load(expectedElmJsonAssetName)

      // JSONAssert ignores property order and whitespace/tabs
      JSONAssert.assertEquals(expectedElmJsonAsset, translator.toJson(), false)
      return this
    }

    fun withXmlEqualsTo(expectedElmXmlAssetName: String): CompiledCql {
      expectedElmXmlAsset = load(expectedElmXmlAssetName)

      // XmlAssert ignores property order and whitespace/tabs
      XMLAssert.assertEquals(expectedElmXmlAsset, translator.toXml())
      return this
    }

    fun generatesFhirLibraryEqualsTo(expectedFhirAssetName: String): CompiledCql {
      // Given the ELM is the same, builds the lib with the expected, not the new ELM to make sure
      // the base 64 representation of the Library matches.
      val library =
        assembleFhirLib(
          cqlText,
          expectedElmJsonAsset,
          expectedElmXmlAsset,
          translator.toELM().identifier.id,
          translator.toELM().identifier.version,
        )

      JSONAssert.assertEquals(load(expectedFhirAssetName), library.toJson(), false)
      return this
    }
  }
}
