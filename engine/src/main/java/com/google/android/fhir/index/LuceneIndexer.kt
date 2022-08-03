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
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.utils.FHIRPathEngine

object LuceneIndexer {
  private var writer: IndexWriter? = null

  private var analyzer: Analyzer? = null

  private var iwc: IndexWriterConfig? = null

  fun resourceIndexer(indexDirPath: String) {
    val indexDir: Directory =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        FSDirectory.open(File(indexDirPath).toPath())
      } else {
        TODO("VERSION.SDK_INT < O")
      } // file replaced by path (nio package)
    analyzer = StandardAnalyzer() // TODO ???
    iwc = IndexWriterConfig(analyzer)

    // default configuration for index. This cannot be changed after index is created using this
    // object. For any changes we will need to getConfig from the index writer.
    iwc!!.openMode =
      IndexWriterConfig.OpenMode
        .CREATE_OR_APPEND // only create removes previous index. create or append adds to
    iwc!!.ramBufferSizeMB = 16.0 //
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
    val doc: Document = Document()
    doc.add(StringField(Resource.SP_RES_ID, resource.id, Field.Store.YES))
    getSearchParamList(resource).forEach { param ->
      FHIRPathEngine(SimpleWorkerContext()).evaluate(resource, param.path).forEach {
        if (it.hasPrimitiveValue()) // TODO handle others
          when (param.type) {
            // only display and text can be used
            Enumerations.SearchParamType.TOKEN ->
              TextField(param.name, it.castToType(it).primitiveValue(), Field.Store.YES)
            Enumerations.SearchParamType.STRING ->
              StringField(param.name, it.castToType(it).primitiveValue(), Field.Store.YES)
            else -> null
          // else -> throw UnsupportedOperationException("${param.type} indexing not supported yet")
          }?.let { doc.add(it) }
      }
    }
    if (writer!!.config.openMode == IndexWriterConfig.OpenMode.CREATE) {
      writer!!.addDocument(doc)
    } else {
      val idTerm = Term(Resource.SP_RES_ID, resource.id)
      writer!!.updateDocument(idTerm, doc)
    }
  }
}
