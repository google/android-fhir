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

package com.google.android.fhir.db.impl

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ca.uhn.fhir.context.FhirContext
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.Task
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceDatabaseMigrationTest {

  @get:Rule
  val helper: MigrationTestHelper =
    MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), ResourceDatabase::class.java)
  private val iParser = FhirContext.forR4Cached().newJsonParser()

  @Test
  @Throws(IOException::class)
  fun migrate1To2_should_not_throw_exception(): Unit = runBlocking {
    val insertedPatientJson: String =
      Patient()
        .apply {
          id = "migrate1-2-test"
          addName(
            HumanName().apply {
              addGiven("Jane")
              family = "Doe"
            }
          )
        }
        .let { iParser.encodeResourceToString(it) }

    helper.createDatabase(DB_NAME, 1).apply {
      execSQL(
        "INSERT INTO ResourceEntity (resourceUuid, resourceType, resourceId, serializedResource) VALUES ('migrate1-2-test', 'Patient', 'migrate1-2-test', '$insertedPatientJson' );"
      )
      close()
    }

    // Open latest version of the database. Room will validate the schema
    // once all migrations execute.
    helper.runMigrationsAndValidate(DB_NAME, 2, true, MIGRATION_1_2)

    val readPatientJson: String?
    getMigratedRoomDatabase().apply {
      readPatientJson = this.resourceDao().getResource("migrate1-2-test", ResourceType.Patient)
      openHelper.writableDatabase.close()
    }

    assertThat(readPatientJson).isEqualTo(insertedPatientJson)
  }

  @Test
  @Throws(IOException::class)
  fun migrate2To3_should_execute_with_no_exception(): Unit = runBlocking {
    val taskId = "bed-net-001"
    val bedNetTask: String =
      Task()
        .apply {
          id = taskId
          description = "Issue bed net"
          meta.lastUpdated = Date()
        }
        .let { iParser.encodeResourceToString(it) }

    helper.createDatabase(DB_NAME, 2).apply {
      execSQL(
        "INSERT INTO ResourceEntity (resourceUuid, resourceType, resourceId, serializedResource) VALUES ('bed-net-001', 'Task', 'bed-net-001', '$bedNetTask');"
      )
      close()
    }

    // Re-open the database with version 3 and provide MIGRATION_2_3 as the migration process.
    helper.runMigrationsAndValidate(DB_NAME, 3, true, MIGRATION_2_3)

    val retrievedTask: String?
    getMigratedRoomDatabase().apply {
      retrievedTask = this.resourceDao().getResource(taskId, ResourceType.Task)
      openHelper.writableDatabase.close()
    }

    assertThat(retrievedTask).isEqualTo(bedNetTask)
  }

  private fun getMigratedRoomDatabase(): ResourceDatabase =
    Room.databaseBuilder(
        InstrumentationRegistry.getInstrumentation().targetContext,
        ResourceDatabase::class.java,
        DB_NAME
      )
      .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
      .build()

  companion object {
    const val DB_NAME = "migration_tests.db"
  }
}
