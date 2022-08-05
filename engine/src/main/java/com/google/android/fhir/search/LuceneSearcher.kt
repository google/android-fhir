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

package com.google.android.fhir.search

import android.os.Build
import androidx.annotation.RequiresApi
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.getResourceClass
import java.io.File
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexableField
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

object LuceneSearcher {
  val MAX_SEARCH = 20

  var indexSearcher: IndexSearcher? = null

  var reader: IndexReader? = null

  var query: Query? = null

  fun resourceSearcher(indexDirPath: String) {
    val indexDir: Directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      FSDirectory.open(File(indexDirPath))
    } else {
      throw UnsupportedOperationException("LOWER SDK")
    }
    reader = DirectoryReader.open(indexDir)
    indexSearcher = IndexSearcher(reader)
  }

  fun search(searchQuery: String): List<Document> {
    val queryParser = QueryParser(Version.LUCENE_48, searchQuery, StandardAnalyzer(Version.LUCENE_48))
    query = queryParser.parse(searchQuery)
    return indexSearcher!!.search(query, MAX_SEARCH).scoreDocs.map { getDocument(it) }
  }

  fun getDocument(scoreDoc: ScoreDoc): Document {
    return indexSearcher!!.doc(scoreDoc.doc)
  }

  fun close() {
    reader!!.close()
  }
}
