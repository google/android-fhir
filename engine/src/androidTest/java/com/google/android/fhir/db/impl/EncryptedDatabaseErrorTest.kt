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

package com.google.android.fhir.db.impl

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.DatabaseErrorStrategy.RECREATE_AT_OPEN
import com.google.android.fhir.DatabaseErrorStrategy.UNSPECIFIED
import com.google.android.fhir.db.impl.DatabaseImpl.Companion.DATABASE_PASSPHRASE_NAME
import com.google.android.fhir.db.impl.DatabaseImpl.Companion.ENCRYPTED_DATABASE_NAME
import com.google.android.fhir.db.impl.DatabaseImpl.Companion.UNENCRYPTED_DATABASE_NAME
import com.google.android.fhir.index.ResourceIndexer
import com.google.android.fhir.index.SearchParamDefinitionsProviderImpl
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.getQuery
import com.google.common.truth.Truth.assertThat
import java.security.KeyStore
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class EncryptedDatabaseErrorTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val parser = FhirContext.forR4().newJsonParser()
  private val resourceIndexer = ResourceIndexer(SearchParamDefinitionsProviderImpl())

  @After
  fun tearDown() {
    context.getDatabasePath(UNENCRYPTED_DATABASE_NAME).delete()
    context.getDatabasePath(ENCRYPTED_DATABASE_NAME).delete()
  }

  @Test
  fun unencryptedDatabase_thenEncryptedDatabase_shouldThrowIllegalStateException() {
    assertThrows(IllegalStateException::class.java) {
      runBlocking {
        // GIVEN an unencrypted database.
        DatabaseImpl(
            context,
            parser,
            DatabaseConfig(
              inMemory = false,
              enableEncryption = false,
              databaseErrorStrategy = UNSPECIFIED,
            ),
            resourceIndexer,
          )
          .let {
            it.insert(TEST_PATIENT_1)
            it.db.close()
          }

        // WHEN requesting an encrypted database.
        // THEN it should throw SQLiteException
        DatabaseImpl(
            context,
            parser,
            DatabaseConfig(
              inMemory = false,
              enableEncryption = true,
              databaseErrorStrategy = UNSPECIFIED,
            ),
            resourceIndexer,
          )
          .let {
            it.search<Patient>(
              Search(ResourceType.Patient)
                .apply {
                  sort(Patient.GIVEN, Order.ASCENDING)
                  count = 100
                  from = 0
                }
                .getQuery(),
            )
          }
      }
    }
  }

  @Test
  fun encryptedDatabase_thenLostKey_shouldThrowSQLiteException() {
    assertThrows(SQLiteException::class.java) {
      runBlocking {
        // GIVEN an unencrypted database.
        DatabaseImpl(
            context,
            parser,
            DatabaseConfig(
              inMemory = false,
              enableEncryption = true,
              databaseErrorStrategy = UNSPECIFIED,
            ),
            resourceIndexer,
          )
          .let {
            it.insert(TEST_PATIENT_1)
            it.db.close()
          }

        // GIVEN the key is lost.
        val keyStore = KeyStore.getInstance(DatabaseEncryptionKeyProvider.ANDROID_KEYSTORE_NAME)
        keyStore.load(null)
        keyStore.deleteEntry(DATABASE_PASSPHRASE_NAME)
        DatabaseEncryptionKeyProvider.clearKeyCache()

        // WHEN requesting an encrypted database.
        // THEN it should throw SQLiteException
        DatabaseImpl(
            context,
            parser,
            DatabaseConfig(
              inMemory = false,
              enableEncryption = true,
              databaseErrorStrategy = UNSPECIFIED,
            ),
            resourceIndexer,
          )
          .let {
            it.search<Patient>(
              Search(ResourceType.Patient)
                .apply {
                  sort(Patient.GIVEN, Order.ASCENDING)
                  count = 100
                  from = 0
                }
                .getQuery(),
            )
          }
      }
    }
  }

  @Test
  fun encryptedDatabase_thenLostKey_recreationStrategy_shouldRecreateDatabase() {
    runBlocking {
      // GIVEN an unencrypted database.
      DatabaseImpl(
          context,
          parser,
          DatabaseConfig(
            inMemory = false,
            enableEncryption = true,
            databaseErrorStrategy = UNSPECIFIED,
          ),
          resourceIndexer,
        )
        .let {
          it.insert(TEST_PATIENT_1)
          it.db.close()
        }

      // GIVEN the key is lost.
      val keyStore = KeyStore.getInstance(DatabaseEncryptionKeyProvider.ANDROID_KEYSTORE_NAME)
      keyStore.load(null)
      keyStore.deleteEntry(DATABASE_PASSPHRASE_NAME)
      DatabaseEncryptionKeyProvider.clearKeyCache()

      // WHEN requesting an encrypted database with RECREATE_AT_OPEN strategy.
      // THEN it should recreate the database
      DatabaseImpl(
          context,
          parser,
          DatabaseConfig(
            inMemory = false,
            enableEncryption = true,
            databaseErrorStrategy = RECREATE_AT_OPEN,
          ),
          resourceIndexer,
        )
        .let {
          assertThat(
              it.search<Patient>(
                Search(ResourceType.Patient)
                  .apply {
                    sort(Patient.GIVEN, Order.ASCENDING)
                    count = 100
                    from = 0
                  }
                  .getQuery(),
              ),
            )
            .isEmpty()
        }
    }
  }

  @Test
  fun encryptedDatabase_thenUnencrypted_shouldThrowIllegalStateException() {
    assertThrows(IllegalStateException::class.java) {
      runBlocking {
        // GIVEN an encrypted database.
        DatabaseImpl(
            context,
            parser,
            DatabaseConfig(
              inMemory = false,
              enableEncryption = true,
              databaseErrorStrategy = UNSPECIFIED,
            ),
            resourceIndexer,
          )
          .let {
            it.insert(TEST_PATIENT_1)
            it.db.close()
          }

        // WHEN requesting an unencrypted database.
        // THEN it should recreate database.
        DatabaseImpl(
            context,
            parser,
            DatabaseConfig(
              inMemory = false,
              enableEncryption = false,
              databaseErrorStrategy = UNSPECIFIED,
            ),
            resourceIndexer,
          )
          .let {
            assertThat(
                it.search<Patient>(
                  Search(ResourceType.Patient)
                    .apply {
                      sort(Patient.GIVEN, Order.ASCENDING)
                      count = 100
                      from = 0
                    }
                    .getQuery(),
                ),
              )
              .isEmpty()
          }
      }
    }
  }

  private companion object {
    const val TEST_PATIENT_1_ID = "patient_1"
    val TEST_PATIENT_1 = Patient()

    init {
      TEST_PATIENT_1.id = TEST_PATIENT_1_ID
      TEST_PATIENT_1.gender = Enumerations.AdministrativeGender.MALE
    }
  }
}
