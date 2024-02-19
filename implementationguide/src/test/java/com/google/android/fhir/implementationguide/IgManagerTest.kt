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

package com.google.android.fhir.implementationguide

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.implementationguide.db.impl.ImplementationGuideDatabase
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
internal class IgManagerTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val igDb =
    Room.inMemoryDatabaseBuilder(context, ImplementationGuideDatabase::class.java).build()
  private val igManager = IgManager(igDb)
  private val implementationGuide = ImplementationGuide("anc-cds", "0.3.0", "http://url.com")
  private val dataFolder = File(javaClass.getResource("/anc-cds")!!.file)

  @After
  fun closeDb() {
    igDb.close()
  }

  @Test
  fun `importing IG creates entries in DB`() = runTest {
    igManager.install(implementationGuide, dataFolder)

    assertThat(
        igDb
          .implementationGuideDao()
          .getImplementationGuidesWithResources(
            igDb
              .implementationGuideDao()
              .getImplementationGuide("anc-cds", "0.3.0")
              .implementationGuideId
          )
          ?.resources
      )
      .hasSize(6)
  }

  @Test
  fun `deleting IG deletes files and DB entries`() = runTest {
    val igRoot = File(dataFolder.parentFile, "anc-cds.copy")
    igRoot.deleteOnExit()
    dataFolder.copyRecursively(igRoot)
    igManager.install(implementationGuide, igRoot)

    igManager.delete(implementationGuide)

    assertThat(igDb.implementationGuideDao().getImplementationGuides()).isEmpty()
    assertThat(igRoot.exists()).isFalse()
  }

  @Test
  fun `imported entries are readable`() = runTest {
    igManager.install(implementationGuide, dataFolder)

    assertThat(igManager.loadResources(resourceType = "Library", name = "WHOCommon")).isNotNull()
    assertThat(igManager.loadResources(resourceType = "Library", url = "FHIRCommon")).isNotNull()
    assertThat(igManager.loadResources(resourceType = "Measure")).hasSize(1)
    assertThat(
        igManager.loadResources(
          resourceType = "Measure",
          url = "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01"
        )
      )
      .isNotEmpty()
    assertThat(igManager.loadResources(resourceType = "Measure", url = "Measure/ANCIND01"))
      .isNotNull()
  }
}
