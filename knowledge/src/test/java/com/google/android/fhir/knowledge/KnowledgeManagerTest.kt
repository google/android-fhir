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

package com.google.android.fhir.knowledge

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.knowledge.db.impl.KnowledgeDatabase
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
internal class KnowledgeManagerTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val knowledgeDb =
    Room.inMemoryDatabaseBuilder(context, KnowledgeDatabase::class.java).build()
  private val knowledgeManager = KnowledgeManager(knowledgeDb)
  private val implementationGuide = ImplementationGuide("anc-cds", "0.3.0", "http://url.com")
  private val dataFolder = File(javaClass.getResource("/anc-cds")!!.file)

  @After
  fun closeDb() {
    knowledgeDb.close()
  }

  @Test
  fun `importing IG creates entries in DB`() = runTest {
    knowledgeManager.install(implementationGuide, dataFolder)
    val implementationGuideId =
      knowledgeDb.knowledgeDao().getImplementationGuide("anc-cds", "0.3.0")!!.implementationGuideId

    assertThat(
        knowledgeDb
          .knowledgeDao()
          .getImplementationGuidesWithResources(implementationGuideId)
          ?.resources
      )
      .hasSize(6)
  }

  @Test
  fun `deleting IG deletes files and DB entries`() = runTest {
    val igRoot = File(dataFolder.parentFile, "anc-cds.copy")
    igRoot.deleteOnExit()
    dataFolder.copyRecursively(igRoot)
    knowledgeManager.install(implementationGuide, igRoot)

    knowledgeManager.delete(implementationGuide)

    assertThat(knowledgeDb.knowledgeDao().getImplementationGuides()).isEmpty()
    assertThat(igRoot.exists()).isFalse()
  }

  @Test
  fun `imported entries are readable`() = runTest {
    knowledgeManager.install(implementationGuide, dataFolder)

    assertThat(knowledgeManager.loadResources(resourceType = "Library", name = "WHOCommon"))
      .isNotNull()
    assertThat(knowledgeManager.loadResources(resourceType = "Library", url = "FHIRCommon"))
      .isNotNull()
    assertThat(knowledgeManager.loadResources(resourceType = "Measure")).hasSize(1)
    assertThat(
        knowledgeManager.loadResources(
          resourceType = "Measure",
          url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01"
        )
      )
      .isNotEmpty()
    assertThat(knowledgeManager.loadResources(resourceType = "Measure", url = "Measure/ANCIND01"))
      .isNotNull()
  }
}
