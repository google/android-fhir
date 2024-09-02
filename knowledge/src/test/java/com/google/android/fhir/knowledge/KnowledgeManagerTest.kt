/*
 * Copyright 2023-2024 Google LLC
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.BaseResource
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.PlanDefinition
import org.junit.After
import org.junit.Assert.assertThrows
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
    knowledgeManager.import(fhirNpmPackage, dataFolder)
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
    igRoot.deleteRecursively()
    igRoot.deleteOnExit()
    dataFolder.copyRecursively(igRoot)
    knowledgeManager.import(fhirNpmPackage, igRoot)

    knowledgeManager.delete(fhirNpmPackage)

    assertThat(knowledgeDb.knowledgeDao().getImplementationGuides()).isEmpty()
    assertThat(igRoot.exists()).isFalse()
  }

  @Test
  fun `imported entries are readable`() = runTest {
    knowledgeManager.import(fhirNpmPackage, dataFolder)

    assertThat(
        knowledgeManager.loadResources(
          url = "http://fhir.org/guides/who/anc-cds/Library/WHOCommon",
          version = "0.3.0",
        ),
      )
      .hasSize(1)
    assertThat(
        knowledgeManager.loadResources(
          url = "http://fhir.org/guides/who/anc-cds/Library/FHIRCommon",
          version = "0.3.0",
        ),
      )
      .hasSize(1)
    assertThat(
        knowledgeManager.loadResources(
          url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
          version = "0.3.0",
        ),
      )
      .hasSize(1)
  }

  @Test
  fun `indexing non metadata resource should throw an exception`() {
    val patient = Patient().apply { id = "Patient/defaultA-A.1.0.0" }

    assertThrows(IllegalStateException::class.java) {
      runBlocking { knowledgeManager.index(writeToFile(patient)) }
    }
  }

  @Test
  fun `should index a library of a different version`() = runTest {
    val libraryAOld =
      Library().apply {
        id = "Library/defaultA-A.1.0.0"
        name = "defaultA"
        url = "www.exampleA.com/Library/defaultA-A.1.0.0"
        version = "A.1.0.0"
      }
    val libraryANew =
      Library().apply {
        id = "Library/defaultA-A.1.0.1"
        name = "defaultA"
        url = "www.exampleA.com/Library/defaultA-A.1.0.1"
        version = "A.1.0.1"
      }

    knowledgeManager.index(writeToFile(libraryAOld))
    knowledgeManager.index(writeToFile(libraryANew))

    val resources = knowledgeDb.knowledgeDao().getResources()
    assertThat(resources).hasSize(2)

    val resourceA100 =
      knowledgeManager
        .loadResources(url = "www.exampleA.com/Library/defaultA-A.1.0.0", version = "A.1.0.0")
        .single() as Library
    assertThat(resourceA100.version).isEqualTo("A.1.0.0")

    val resourceA101 =
      knowledgeManager
        .loadResources(url = "www.exampleA.com/Library/defaultA-A.1.0.1", version = "A.1.0.1")
        .single() as Library
    assertThat(resourceA101.version.toString()).isEqualTo("A.1.0.1")
  }

  fun `installing from npmPackageManager`() = runTest {
    knowledgeManager.install(fhirNpmPackage)

    assertThat(
        knowledgeManager.loadResources(
          url = "http://fhir.org/guides/who/anc-cds/Library/WHOCommon",
          version = "0.3.0",
        ),
      )
      .hasSize(1)
    assertThat(
        knowledgeManager.loadResources(
          url = "http://fhir.org/guides/who/anc-cds/Library/FHIRCommon",
          version = "0.3.0",
        ),
      )
      .hasSize(1)
    assertThat(
        knowledgeManager.loadResources(
          url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01",
          version = "0.3.0",
        ),
      )
      .hasSize(1)
  }

  @Test
  fun `for different resources with URL loading by URL should be correct`() = runTest {
    val libraryWithSameUrl =
      Library().apply {
        id = "Library/lId"
        name = "LibraryName"
        url = "www.sample-url.com/Library/lId"
      }
    val planDefinitionWithSameUrl =
      PlanDefinition().apply {
        id = "PlanDefinition/pdId"
        name = "PlanDefinitionName"
        url = "www.sample-url.com/PlanDefinition/pdId"
      }

    knowledgeManager.index(writeToFile(libraryWithSameUrl))
    knowledgeManager.index(writeToFile(planDefinitionWithSameUrl))

    val resources = knowledgeDb.knowledgeDao().getResources()
    assertThat(resources).hasSize(2)

    val libraryLoadedByUrl =
      knowledgeManager.loadResources(url = "www.sample-url.com/Library/lId").single() as Library
    assertThat(libraryLoadedByUrl.name.toString()).isEqualTo("LibraryName")

    val planDefinitionLoadedByUrl =
      knowledgeManager.loadResources(url = "www.sample-url.com/PlanDefinition/pdId").single()
        as PlanDefinition
    assertThat(planDefinitionLoadedByUrl.name.toString()).isEqualTo("PlanDefinitionName")
  }

  @Test
  fun `for different resources with URL and Version loading by URL should be correct`() = runTest {
    val libraryWithSameUrl =
      Library().apply {
        id = "Library/lId"
        name = "LibraryName"
        url = "www.sample-url.com/Library/lId"
        version = "0"
      }
    val planDefinitionWithSameUrl =
      PlanDefinition().apply {
        id = "PlanDefinition/pdId"
        name = "PlanDefinitionName"
        url = "www.sample-url.com/PlanDefinition/pdId"
        version = "0"
      }

    knowledgeManager.index(writeToFile(libraryWithSameUrl))
    knowledgeManager.index(writeToFile(planDefinitionWithSameUrl))

    val resources = knowledgeDb.knowledgeDao().getResources()
    assertThat(resources).hasSize(2)

    val libraryLoadedByUrl =
      knowledgeManager.loadResources(url = "www.sample-url.com/Library/lId").single() as Library
    assertThat(libraryLoadedByUrl.name.toString()).isEqualTo("LibraryName")

    val planDefinitionLoadedByUrl =
      knowledgeManager.loadResources(url = "www.sample-url.com/PlanDefinition/pdId").single()
        as PlanDefinition
    assertThat(planDefinitionLoadedByUrl.name.toString()).isEqualTo("PlanDefinitionName")
  }

  private fun writeToFile(resource: BaseResource): File {
    return File(context.filesDir, resource.id).apply {
      this.parentFile?.mkdirs()
      writeText(jsonParser.encodeResourceToString(resource))
    }
  }
}
