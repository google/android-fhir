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
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResourceDatabaseMigrationTest {

  @get:Rule
  val helper: MigrationTestHelper =
    MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), ResourceDatabase::class.java)

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
        .let { FhirContext.forR4Cached().newJsonParser().encodeResourceToString(it) }

    helper.createDatabase(DB_NAME, 1).apply {
      execSQL(
        "INSERT INTO ResourceEntity (resourceUuid, resourceType, resourceId, serializedResource) VALUES ('migrate1-2-test', 'Patient', 'migrate1-2-test', '$insertedPatientJson' );"
      )
      close()
    }

    val readPatientJson: String?
    // Open latest version of the database. Room will validate the schema
    // once all migrations execute.
    Room.databaseBuilder(
        InstrumentationRegistry.getInstrumentation().targetContext,
        ResourceDatabase::class.java,
        DB_NAME
      )
      .addMigrations(MIGRATION_1_2)
      .build()
      .apply {
        readPatientJson = this.resourceDao().getResource("migrate1-2-test", ResourceType.Patient)
        openHelper.writableDatabase.close()
      }

    assertThat(readPatientJson).isEqualTo(insertedPatientJson)
  }

  companion object {
    const val DB_NAME = "migration_tests.db"
  }
}
