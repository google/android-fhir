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

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.implementationguide.db.impl.ImplementationGuideDatabase
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class IgResourceRetrieverTest {
  private val igDb =
    Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        ImplementationGuideDatabase::class.java
      )
      .build()
  private val igManager = IgManager(igDb)
  private val igDependency = IgContext.Dependency("anc-cds", "0.3.0")
  private val igRoot = File(javaClass.getResource("/anc-cds")!!.file)
  private val igContext = IgContext(listOf(igDependency))

  @Before
  fun importIg() = runBlocking {
    igManager.import(igDependency, igRoot, igRoot.listFiles()!!.asIterable())
  }

  @After
  fun closeDb() {
    igDb.close()
  }

  @Test
  fun `imported entries are readable`() = runBlocking {
    val igResourceRetriever = igManager.createResourceRetriever(igContext)
    assertThat(igResourceRetriever.loadResourceByName("Library", "WHOCommon")).isNotNull()
    assertThat(igResourceRetriever.loadResourceByName("Library", "FHIRCommon")).isNotNull()
    assertThat(igResourceRetriever.loadResources("Measure")).hasSize(1)
    assertThat(
        igResourceRetriever.loadResourcesByUrl(
          "Measure",
          "http://fhir.org/guides/who/anc-cds/Measure/ANCIND01"
        )
      )
      .isNotEmpty()
    assertThat(igResourceRetriever.loadResourceById("Measure", "Measure/ANCIND01")).isNotNull()
  }

  // TODO: add tests for multiple igs
}
