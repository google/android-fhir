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

package com.google.android.fhir.knowledge

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.knowledge.db.KnowledgeDatabase
import com.google.android.fhir.knowledge.files.NpmFileManager
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Library
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class KnowledgeManagerTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val fhirNpmPackage = FhirNpmPackage("anc-cds", "0.3.0", "http://url.com")
  private val dataFolder = File(javaClass.getResource("/anc-cds")!!.file)
  private val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val knowledgeDb =
    Room.inMemoryDatabaseBuilder(context, KnowledgeDatabase::class.java).build()
  private val npmFileManager = NpmFileManager(context.dataDir)
  private val knowledgeManager =
    KnowledgeManager(
      knowledgeDb,
      npmFileManager = npmFileManager,
      npmPackageDownloader = { fhirPackage, _ ->
        LocalFhirNpmPackageMetadata(
          fhirPackage.name,
          fhirPackage.version,
          fhirPackage.canonical,
          emptyList(),
          dataFolder,
        )
      },
    )

  @After
  fun closeDb() {
    knowledgeDb.close()
  }

  @Test
  fun `importing IG creates entries in DB`() = runTest {
    knowledgeManager.install(fhirNpmPackage, dataFolder)
    val implementationGuideId =
      knowledgeDb.knowledgeDao().getImplementationGuide("anc-cds", "0.3.0")!!.implementationGuideId

    assertThat(
        knowledgeDb
          .knowledgeDao()
          .getImplementationGuidesWithResources(implementationGuideId)
          ?.resources,
      )
      .hasSize(6)
  }

  @Test
  fun `deleting IG deletes files and DB entries`() = runTest {
    val igRoot = File(dataFolder.parentFile, "anc-cds.copy")
    igRoot.deleteOnExit()
    dataFolder.copyRecursively(igRoot)
    knowledgeManager.install(fhirNpmPackage, igRoot)

    knowledgeManager.delete(fhirNpmPackage)

    assertThat(knowledgeDb.knowledgeDao().getImplementationGuides()).isEmpty()
    assertThat(igRoot.exists()).isFalse()
  }

  @Test
  fun `imported entries are readable`() = runTest {
    knowledgeManager.install(fhirNpmPackage, dataFolder)

    assertThat(knowledgeManager.loadResources(resourceType = "Library", name = "WHOCommon"))
      .isNotNull()
    assertThat(knowledgeManager.loadResources(resourceType = "Library", url = "FHIRCommon"))
      .isNotNull()
    assertThat(knowledgeManager.loadResources(resourceType = "Measure")).hasSize(1)
    assertThat(
        knowledgeManager.loadResources(
          resourceType = "Measure",
          url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        ),
      )
      .isNotEmpty()
    assertThat(knowledgeManager.loadResources(resourceType = "Measure", url = "Measure/ANCIND01"))
      .isNotNull()
  }

  @Test
  fun `inserting a library of a different version creates new entry`() = runTest {
    val libraryAOld =
      Library().apply {
        id = "Library/defaultA-A.1.0.0"
        name = "defaultA"
        url = "www.exampleA.com"
        version = "A.1.0.0"
      }
    val libraryANew =
      Library().apply {
        id = "Library/defaultA-A.1.0.1"
        name = "defaultA"
        url = "www.exampleA.com"
        version = "A.1.0.1"
      }

    knowledgeManager.install(writeToFile(libraryAOld))
    knowledgeManager.install(writeToFile(libraryANew))

    val resources = knowledgeDb.knowledgeDao().getResources()
    assertThat(resources).hasSize(2)

    val resourceA100 =
      knowledgeManager
        .loadResources(resourceType = "Library", name = "defaultA", version = "A.1.0.0")
        .single()
    assertThat(resourceA100.idElement.toString()).isEqualTo("Library/1")

    val resourceA101 =
      knowledgeManager
        .loadResources(resourceType = "Library", name = "defaultA", version = "A.1.0.1")
        .single()
    assertThat(resourceA101.idElement.toString()).isEqualTo("Library/2")
  }

  fun `installing from npmPackageManager`() = runTest {
    knowledgeManager.install(fhirNpmPackage)

    assertThat(knowledgeManager.loadResources(resourceType = "Library", name = "WHOCommon"))
      .isNotNull()
    assertThat(knowledgeManager.loadResources(resourceType = "Library", url = "FHIRCommon"))
      .isNotNull()
    assertThat(knowledgeManager.loadResources(resourceType = "Measure")).hasSize(1)
    assertThat(
        knowledgeManager.loadResources(
          resourceType = "Measure",
          url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
        ),
      )
      .isNotEmpty()
    assertThat(knowledgeManager.loadResources(resourceType = "Measure", url = "Measure/ANCIND01"))
      .isNotNull()
  }

  private fun writeToFile(library: Library): File {
    return File(context.filesDir, library.id).apply {
      this.parentFile?.mkdirs()
      writeText(jsonParser.encodeResourceToString(library))
    }
  }
}
