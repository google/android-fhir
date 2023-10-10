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

package com.google.android.fhir.db.impl.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.util.FhirTerser
import com.google.android.fhir.db.impl.ResourceDatabase
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.logicalId
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class LocalChangeDaoTest {
  private lateinit var database: ResourceDatabase
  private lateinit var localChangeDao: LocalChangeDao

  @Before
  fun setupDatabase() {
    database =
      Room.inMemoryDatabaseBuilder(
          ApplicationProvider.getApplicationContext(),
          ResourceDatabase::class.java,
        )
        .allowMainThreadQueries()
        .build()

    localChangeDao =
      database.localChangeDao().also {
        it.iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
        it.fhirTerser = FhirTerser(FhirContext.forCached(FhirVersionEnum.R4))
      }
  }

  @After
  fun closeDatabase() {
    database.close()
  }

  @Test
  fun addInsert_shouldAddLocalChangeAndLocalChangeReferences() = runBlocking {
    val patientId = "Patient1"
    val carePlanResourceUuid = UUID.randomUUID()
    val carePlan =
      CarePlan().apply {
        id = "CarePlan1"
        subject = Reference("Patient/$patientId")
        activityFirstRep.detail.performer.add(Reference("Patient/$patientId"))
        category =
          listOf(
            CodeableConcept(
              Coding("http://snomed.info/sct", "698360004", "Diabetes self management plan"),
            ),
          )
      }
    val carePlanCreationTime = Instant.now()
    localChangeDao.addInsert(carePlan, carePlanResourceUuid, carePlanCreationTime)

    val carePlanLocalChanges = localChangeDao.getLocalChanges(carePlanResourceUuid)
    assertThat(carePlanLocalChanges.size).isEqualTo(1)
    val carePlanLocalChange1 = carePlanLocalChanges[0]
    assertThat(carePlanLocalChange1.resourceUuid).isEqualTo(carePlanResourceUuid)
    assertThat(carePlanLocalChange1.resourceId).isEqualTo(carePlan.id)
    assertThat(carePlanLocalChange1.type).isEqualTo(LocalChangeEntity.Type.INSERT)
    assertThat(carePlanLocalChange1.payload)
      .isEqualTo(localChangeDao.iParser.encodeResourceToString(carePlan))
    val carePlanLocalChange1Id = carePlanLocalChange1.id

    val localChangeResourceReferences =
      localChangeDao.getReferencesForLocalChange(carePlanLocalChange1Id)
    assertThat(localChangeResourceReferences.size).isEqualTo(2)
    assertThat(localChangeResourceReferences[0].resourceReferencePath).isEqualTo("subject")
    assertThat(localChangeResourceReferences[0].resourceReferenceValue)
      .isEqualTo("Patient/$patientId")
    assertThat(localChangeResourceReferences[1].resourceReferencePath)
      .isEqualTo("activity.detail.performer")
    assertThat(localChangeResourceReferences[1].resourceReferenceValue)
      .isEqualTo("Patient/$patientId")
  }

  @Test
  fun addUpdate_shouldAddLocalChangeAndLocalChangeReferences() = runBlocking {
    val patientId = "Patient1"
    val carePlanResourceUuid = UUID.randomUUID()
    val originalCarePlan =
      CarePlan().apply {
        id = "CarePlan1"
        subject = Reference("Patient/$patientId")
        activityFirstRep.detail.performer.add(Reference("Patient/$patientId"))
        category =
          listOf(
            CodeableConcept(
              Coding("http://snomed.info/sct", "698360004", "Diabetes self management plan"),
            ),
          )
      }
    val carePlanCreationTime = Instant.now()
    localChangeDao.addInsert(originalCarePlan, carePlanResourceUuid, carePlanCreationTime)

    val practitionerReference = "Practitioner/Practitioner123"
    val modifiedCarePlan =
      originalCarePlan.copy().apply {
        author = Reference(practitionerReference)
        activityFirstRep.detail.performer.clear()
        activityFirstRep.detail.performer.add(Reference(practitionerReference))
      }
    val carePlanUpdateTime = Instant.now()
    localChangeDao.addUpdate(
      oldEntity =
        ResourceEntity(
          id = 0,
          lastUpdatedLocal = carePlanCreationTime,
          lastUpdatedRemote = null,
          versionId = null,
          resourceId = originalCarePlan.logicalId,
          resourceType = originalCarePlan.resourceType,
          resourceUuid = carePlanResourceUuid,
          serializedResource = localChangeDao.iParser.encodeResourceToString(originalCarePlan),
        ),
      updatedResource = modifiedCarePlan,
      timeOfLocalChange = carePlanUpdateTime,
    )

    val carePlanLocalChanges = localChangeDao.getLocalChanges(carePlanResourceUuid)
    assertThat(carePlanLocalChanges.size).isEqualTo(2)
    val carePlanLocalChange1 = carePlanLocalChanges[0]
    assertThat(carePlanLocalChange1.resourceUuid).isEqualTo(carePlanResourceUuid)
    assertThat(carePlanLocalChange1.resourceId).isEqualTo(originalCarePlan.id)
    assertThat(carePlanLocalChange1.type).isEqualTo(LocalChangeEntity.Type.INSERT)
    assertThat(carePlanLocalChange1.payload)
      .isEqualTo(localChangeDao.iParser.encodeResourceToString(originalCarePlan))

    val carePlanLocalChange2 = carePlanLocalChanges[1]
    assertThat(carePlanLocalChange2.resourceUuid).isEqualTo(carePlanResourceUuid)
    assertThat(carePlanLocalChange2.resourceId).isEqualTo(originalCarePlan.id)
    assertThat(carePlanLocalChange2.type).isEqualTo(LocalChangeEntity.Type.UPDATE)
    assertThat(carePlanLocalChange2.payload)
      .isEqualTo(
        "[{\"op\":\"add\",\"path\":\"\\/author\",\"value\":{\"reference\":\"Practitioner\\/Practitioner123\"}}" +
          ",{\"op\":\"replace\",\"path\":\"\\/activity\\/0\\/detail\\/performer\\/0\\/reference\",\"value\":\"Practitioner\\/Practitioner123\"}]",
      )
    val carePlanLocalChange2Id = carePlanLocalChange2.id

    val localChangeResourceReferences =
      localChangeDao.getReferencesForLocalChange(carePlanLocalChange2Id)
    assertThat(localChangeResourceReferences.size).isEqualTo(3)
    assertThat(localChangeResourceReferences[0].resourceReferencePath)
      .isEqualTo("activity.detail.performer")
    assertThat(localChangeResourceReferences[0].resourceReferenceValue)
      .isEqualTo("Patient/$patientId")
    assertThat(localChangeResourceReferences[1].resourceReferencePath).isEqualTo("author")
    assertThat(localChangeResourceReferences[1].resourceReferenceValue)
      .isEqualTo(practitionerReference)
    assertThat(localChangeResourceReferences[2].resourceReferencePath)
      .isEqualTo("activity.detail.performer")
    assertThat(localChangeResourceReferences[2].resourceReferenceValue)
      .isEqualTo(practitionerReference)
  }

  @Test
  fun addDelete_shouldAddOnlyLocalChangeEntity() = runBlocking {
    val patientId = "Patient1"
    val carePlanResourceUuid = UUID.randomUUID()
    val carePlan =
      CarePlan().apply {
        id = "CarePlan1"
        subject = Reference("Patient/$patientId")
        activityFirstRep.detail.performer.add(Reference("Patient/$patientId"))
        category =
          listOf(
            CodeableConcept(
              Coding("http://snomed.info/sct", "698360004", "Diabetes self management plan"),
            ),
          )
      }
    val carePlanCreationTime = Instant.now()
    localChangeDao.addInsert(carePlan, carePlanResourceUuid, carePlanCreationTime)

    localChangeDao.addDelete(
      resourceUuid = carePlanResourceUuid,
      resourceType = carePlan.resourceType,
      remoteVersionId = null,
      resourceId = carePlan.id,
    )

    val carePlanLocalChanges = localChangeDao.getLocalChanges(carePlanResourceUuid)
    assertThat(carePlanLocalChanges.size).isEqualTo(2)
    val carePlanLocalChange1 = carePlanLocalChanges[0]
    assertThat(carePlanLocalChange1.resourceUuid).isEqualTo(carePlanResourceUuid)
    assertThat(carePlanLocalChange1.resourceId).isEqualTo(carePlan.id)
    assertThat(carePlanLocalChange1.type).isEqualTo(LocalChangeEntity.Type.INSERT)
    assertThat(carePlanLocalChange1.payload)
      .isEqualTo(localChangeDao.iParser.encodeResourceToString(carePlan))

    val carePlanLocalChange2 = carePlanLocalChanges[1]
    assertThat(carePlanLocalChange2.resourceUuid).isEqualTo(carePlanResourceUuid)
    assertThat(carePlanLocalChange2.resourceId).isEqualTo(carePlan.id)
    assertThat(carePlanLocalChange2.type).isEqualTo(LocalChangeEntity.Type.DELETE)
    assertThat(carePlanLocalChange2.payload).isEqualTo("")
    val carePlanLocalChange2Id = carePlanLocalChange2.id

    val localChangeResourceReferences =
      localChangeDao.getReferencesForLocalChange(carePlanLocalChange2Id)
    assertThat(localChangeResourceReferences.size).isEqualTo(0)
  }

  @Test
  fun updateResourceId_shouldUpdateLocalChangeAndLocalChangeReferences() = runBlocking {
    val patientId = "Patient1"
    val patientResourceUuid = UUID.randomUUID()
    val patient =
      Patient().apply {
        gender = Enumerations.AdministrativeGender.MALE
        id = patientId
      }
    val patientCreationTime = Instant.now()
    localChangeDao.addInsert(patient, patientResourceUuid, patientCreationTime)

    val carePlanResourceUuid = UUID.randomUUID()
    val originalCarePlan =
      CarePlan().apply {
        id = "CarePlan1"
        subject = Reference("Patient/$patientId")
        activityFirstRep.detail.performer.add(Reference("Patient/$patientId"))
        category =
          listOf(
            CodeableConcept(
              Coding("http://snomed.info/sct", "698360004", "Diabetes self management plan"),
            ),
          )
      }
    val carePlanCreationTime = Instant.now()
    localChangeDao.addInsert(originalCarePlan, carePlanResourceUuid, carePlanCreationTime)

    val practitionerReference = "Practitioner/Practitioner123"
    val modifiedCarePlan =
      originalCarePlan.copy().apply {
        author = Reference(practitionerReference)
        activityFirstRep.detail.performer.clear()
        activityFirstRep.detail.performer.add(Reference(practitionerReference))
      }
    val carePlanUpdateTime = Instant.now()
    localChangeDao.addUpdate(
      oldEntity =
        ResourceEntity(
          id = 0,
          lastUpdatedLocal = carePlanCreationTime,
          lastUpdatedRemote = null,
          versionId = null,
          resourceId = originalCarePlan.logicalId,
          resourceType = originalCarePlan.resourceType,
          resourceUuid = carePlanResourceUuid,
          serializedResource = localChangeDao.iParser.encodeResourceToString(originalCarePlan),
        ),
      updatedResource = modifiedCarePlan,
      timeOfLocalChange = carePlanUpdateTime,
    )

    val updatedPatientId = "SyncedPatient1"
    val updatedPatient = patient.copy().apply { id = updatedPatientId }
    localChangeDao.updateResourceIdAndReferences(
      resourceUuid = patientResourceUuid,
      oldResource = patient,
      updatedResource = updatedPatient,
    )

    // assert that Patient's new ID is reflected in the Patient Resource Change
    val patientLocalChanges = localChangeDao.getLocalChanges(patientResourceUuid)
    assertThat(patientLocalChanges.size).isEqualTo(1)
    assertThat(patientLocalChanges[0].resourceId).isEqualTo(updatedPatientId)

    // assert that LocalChanges are still retrieved in the same sequence
    val carePlanLocalChanges = localChangeDao.getLocalChanges(carePlanResourceUuid)
    assertThat(carePlanLocalChanges.size).isEqualTo(2)
    val carePlanLocalChange1 = carePlanLocalChanges[0]
    assertThat(carePlanLocalChange1.resourceUuid).isEqualTo(carePlanResourceUuid)
    assertThat(carePlanLocalChange1.resourceId).isEqualTo(originalCarePlan.id)
    assertThat(carePlanLocalChange1.type).isEqualTo(LocalChangeEntity.Type.INSERT)
    val updatedReferencesCarePlan =
      originalCarePlan.copy().apply {
        subject = Reference("Patient/$updatedPatientId")
        activityFirstRep.detail.performer.clear()
        activityFirstRep.detail.performer.add(Reference("Patient/$updatedPatientId"))
      }
    assertThat(carePlanLocalChange1.payload)
      .isEqualTo(localChangeDao.iParser.encodeResourceToString(updatedReferencesCarePlan))
    val carePlanLocalChange1Id = carePlanLocalChange1.id
    // assert that LocalChangeReferences are updated as well
    val localChange1ResourceReferences =
      localChangeDao.getReferencesForLocalChange(carePlanLocalChange1Id)
    assertThat(localChange1ResourceReferences.size).isEqualTo(2)
    assertThat(localChange1ResourceReferences[0].resourceReferencePath).isEqualTo("subject")
    assertThat(localChange1ResourceReferences[0].resourceReferenceValue)
      .isEqualTo("Patient/$updatedPatientId")
    assertThat(localChange1ResourceReferences[1].resourceReferencePath)
      .isEqualTo("activity.detail.performer")
    assertThat(localChange1ResourceReferences[1].resourceReferenceValue)
      .isEqualTo("Patient/$updatedPatientId")

    val carePlanLocalChange2 = carePlanLocalChanges[1]
    assertThat(carePlanLocalChange2.resourceUuid).isEqualTo(carePlanResourceUuid)
    assertThat(carePlanLocalChange2.resourceId).isEqualTo(originalCarePlan.id)
    assertThat(carePlanLocalChange2.type).isEqualTo(LocalChangeEntity.Type.UPDATE)
    assertThat(carePlanLocalChange2.payload)
      .isEqualTo(
        "[{\"op\":\"add\",\"path\":\"\\/author\",\"value\":{\"reference\":\"Practitioner\\/Practitioner123\"}}" +
          ",{\"op\":\"replace\",\"path\":\"\\/activity\\/0\\/detail\\/performer\\/0\\/reference\",\"value\":\"Practitioner\\/Practitioner123\"}]",
      )
    val carePlanLocalChange2Id = carePlanLocalChange2.id
    // assert that LocalChangeReferences are updated as well
    val localChangeResourceReferences =
      localChangeDao.getReferencesForLocalChange(carePlanLocalChange2Id)
    assertThat(localChangeResourceReferences.size).isEqualTo(3)
    assertThat(localChangeResourceReferences[0].resourceReferencePath)
      .isEqualTo("activity.detail.performer")
    assertThat(localChangeResourceReferences[0].resourceReferenceValue)
      .isEqualTo("Patient/$updatedPatientId")
    assertThat(localChangeResourceReferences[1].resourceReferencePath).isEqualTo("author")
    assertThat(localChangeResourceReferences[1].resourceReferenceValue)
      .isEqualTo(practitionerReference)
    assertThat(localChangeResourceReferences[2].resourceReferencePath)
      .isEqualTo("activity.detail.performer")
    assertThat(localChangeResourceReferences[2].resourceReferenceValue)
      .isEqualTo(practitionerReference)
  }
}
