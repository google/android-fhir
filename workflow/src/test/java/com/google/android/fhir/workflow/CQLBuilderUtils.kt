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

import com.google.common.truth.Truth
import java.io.InputStream
import org.cqframework.cql.cql2elm.CqlTranslator
import org.cqframework.cql.cql2elm.CqlTranslatorException
import org.cqframework.cql.cql2elm.FhirLibrarySourceProvider
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Library

class CQLBuilderUtils {
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

    Truth.assertThat(translator.errors).isEmpty()
    return translator
  }

  fun translate(cqlText: InputStream): String {
    return compile(cqlText).toJxson()
  }

  fun build(jxsonElmStr: String, libName: String, libVersion: String): Library {
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

  fun build(cqlText: InputStream): Library {
    val translator = compile(cqlText)
    return build(
      translator.toJxson(),
      translator.toELM().identifier.id,
      translator.toELM().identifier.version
    )
  }
}
