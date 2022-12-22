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

package com.google.android.fhir.implementationguide.db.impl

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.implementationguide.db.impl.entities.ImplementationGuideEntity
import com.google.android.fhir.implementationguide.db.impl.entities.ResourceMetadataEntity
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ImplementationGuideDatabaseTest {

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val igDb =
    Room.inMemoryDatabaseBuilder(context, ImplementationGuideDatabase::class.java).build()
  private val igDao = igDb.implementationGuideDao()

  @After
  fun tearDownDb() {
    igDb.close()
  }

  @Test
  fun igInserted(): Unit = runBlocking {
    assertThat(igDao.insert(IG_ENTITY)).isGreaterThan(0)
    assertThat(igDao.getImplementationGuides().map { it.name }).containsExactly(IG_NAME)
  }

  @Test
  fun resourcesInserted() = runBlocking {
    val igId = igDao.insert(IG_ENTITY)
    val resource =
      ResourceMetadataEntity(
        0L,
        igId,
        ResourceType.ValueSet,
        RES_URL,
        RES_NAME,
        RES_VERSION,
        File("resId")
      )

    igDao.insert(resource)

    assertThat(igDao.getResources(ResourceType.ValueSet, listOf(igId)).map { it.url })
      .containsExactly(RES_URL)
    assertThat(igDao.getResources(ResourceType.Account, listOf(igId))).isEmpty()
    assertThat(igDao.getResources(listOf(-1L))).isEmpty()
  }

  @Test
  fun resourcesDeleted() = runBlocking {
    val igId = igDao.insert(IG_ENTITY)
    val resource =
      ResourceMetadataEntity(
        0L,
        igId,
        ResourceType.ValueSet,
        RES_URL,
        RES_NAME,
        RES_VERSION,
        File("resId")
      )
    igDao.insert(resource)

    igDao.deleteImplementationGuide(IG_NAME, IG_VERSION)

    assertThat(igDao.getImplementationGuides()).isEmpty()
    assertThat(igDao.getResources(listOf(igId))).isEmpty()
  }

  private companion object {
    const val IG_NAME = "test.ig"
    const val IG_VERSION = "1.0.0"
    const val RES_NAME = "res-name-1"
    const val RES_VERSION = "1.0.0"
    const val RES_URL = "http://url.com/ValueSet/$RES_NAME/$RES_VERSION"
    val IG_ENTITY =
      ImplementationGuideEntity(
        id = 0L,
        name = IG_NAME,
        version = IG_VERSION,
        rootDirectory = File("test")
      )
  }
}
