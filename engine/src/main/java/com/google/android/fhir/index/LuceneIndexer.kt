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

package com.google.android.fhir.index

import android.os.Build
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.index.LuceneIndexer.asStringValue
import com.google.android.fhir.index.LuceneIndexer.getStringProperties
import java.io.File
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.utils.FHIRPathEngine

object LuceneIndexer {
  val fhirContext = FhirContext.forCached(FhirVersionEnum.R4)
  val jsonParser = fhirContext.newJsonParser()

  private var writer: IndexWriter? = null

  private var analyzer: Analyzer? = null

  private var iwc: IndexWriterConfig? = null

  fun resourceIndexer(indexDirPath: String) {
    val indexDir: Directory =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FSDirectory.open(File(indexDirPath))
      } else {
        TODO("VERSION.SDK_INT < O")
        throw UnsupportedOperationException("SDK LOWER")
      }
    analyzer = StandardAnalyzer(Version.LUCENE_48) // TODO ???
    iwc = IndexWriterConfig(Version.LUCENE_48, analyzer)

    // default configuration for index. This cannot be changed after index is created using this
    // object. For any changes we will need to getConfig from the index writer.
    iwc!!.openMode = IndexWriterConfig.OpenMode.CREATE_OR_APPEND
    iwc!!.ramBufferSizeMB = 16.0
    try {
      writer = IndexWriter(indexDir, iwc)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  fun close() {
    writer!!.close()
  }

  fun optimize() {
    writer!!.maybeMerge()
  }

  fun indexResources(resources: List<Resource>) {
    resources.forEach { indexResource(it) }
  }

  fun indexResource(resource: Resource) {
    val doc = Document()
    doc.add(StringField(Resource.SP_RES_ID, resource.id, Field.Store.YES))

    getSearchParamList(resource).forEach { param ->
      FHIRPathEngine(SimpleWorkerContext())
        .evaluate(resource, param.path)
        .takeIf { it.isNotEmpty() }
        ?.map {
          if (it.hasPrimitiveValue()) StringField(param.name, it.primitiveValue(), Field.Store.YES)
          else if (it.isDateTime)
            StringField(param.name, it.dateTimeValue().toString(), Field.Store.YES)
          else
            when (Enumerations.FHIRAllTypes.fromCode(it.fhirType())) {
              Enumerations.FHIRAllTypes.ADDRESS ->
                TextField(param.name, it.asStringValue(), Field.Store.YES)
              Enumerations.FHIRAllTypes.AGE,
              Enumerations.FHIRAllTypes.CODEABLECONCEPT,
              Enumerations.FHIRAllTypes.CODING,
              Enumerations.FHIRAllTypes.CONTACTPOINT,
              Enumerations.FHIRAllTypes.DURATION,
              Enumerations.FHIRAllTypes.HUMANNAME,
              Enumerations.FHIRAllTypes.IDENTIFIER,
              Enumerations.FHIRAllTypes.MONEY ->
                TextField(param.name, it.asStringValue(), Field.Store.YES)
              // TODO Period
              Enumerations.FHIRAllTypes.QUANTITY ->
                TextField(param.name, it.asStringValue(), Field.Store.YES)
              // TODO Range
              Enumerations.FHIRAllTypes.REFERENCE ->
                StringField(param.name, it.asStringValue(), Field.Store.YES)
              // TODO Timing
              else -> StringField(param.name, "A??????${it.asStringValue()}", Field.Store.YES)
            }
        }
        ?.forEach { doc.add(it) }
    }
    if (writer!!.config.openMode == IndexWriterConfig.OpenMode.CREATE) {
      writer!!.addDocument(doc)
    } else {
      val idTerm = Term(Resource.SP_RES_ID, resource.id)
      writer!!.updateDocument(idTerm, doc)
    }
  }

  // TODO https://www.hl7.org/fhir/search.html#table
  fun Base.asStringValue(): String {
    return when (Enumerations.FHIRAllTypes.fromCode(this.fhirType())) {
      Enumerations.FHIRAllTypes.ADDRESS ->
        this.getStringProperties().joinToString { it.primitiveValue() }
      Enumerations.FHIRAllTypes.AGE -> this.castToQuantity(this).asStringValue()
      Enumerations.FHIRAllTypes.CODEABLECONCEPT ->
        this.castToCodeableConcept(this).getStringProperties().joinToString()
      Enumerations.FHIRAllTypes.CODING -> this.castToCoding(this).asStringValue()
      Enumerations.FHIRAllTypes.CONTACTPOINT -> this.castToContactPoint(this).value
      Enumerations.FHIRAllTypes.DURATION -> this.castToQuantity(this).asStringValue()
      Enumerations.FHIRAllTypes.HUMANNAME ->
        this.getStringProperties().joinToString { it.primitiveValue() }
      Enumerations.FHIRAllTypes.IDENTIFIER -> this.castToIdentifier(this).asStringValue()
      Enumerations.FHIRAllTypes.MONEY -> this.castToQuantity(this).asStringValue()
      // TODO Period
      Enumerations.FHIRAllTypes.QUANTITY -> this.castToQuantity(this).asStringValue()
      // TODO Range
      Enumerations.FHIRAllTypes.REFERENCE -> this.castToReference(this).reference
      // TODO Timing
      else -> this.getStringProperties().map { it.toString() }.joinToString()
    }
  }

  fun Base.getStringProperties() =
    this.children().flatMap { it.values }.filter {
      it.fhirType() == Enumerations.FHIRDefinedType.STRING.toCode()
    }

  fun Quantity.asStringValue() = "${this.value}|${this.system ?: ""}|${this.unit ?: ""}"

  fun Identifier.asStringValue() = "${this.system ?: ""}|${this.value}|${this.type.text}"

  fun Coding.asStringValue() = "${this.system ?: ""}|${this.code}|${this.display ?: ""}"

  fun CodeableConcept.asStringValue() =
    this.coding.map { it.getStringProperties() }.let {
      if (this.hasText()) it.plus("|${this.text}") else it
    }
}
