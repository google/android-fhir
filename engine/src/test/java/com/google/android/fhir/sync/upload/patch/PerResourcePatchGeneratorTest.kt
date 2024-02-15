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

package com.google.android.fhir.sync.upload.patch

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.impl.dao.diff
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.testing.assertJsonArrayEqualsIgnoringOrder
import com.google.android.fhir.testing.jsonParser
import com.google.android.fhir.testing.readFromFile
import com.google.android.fhir.testing.readJsonArrayFromFile
import com.google.android.fhir.toLocalChange
import com.google.android.fhir.versionId
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.util.Date
import java.util.LinkedList
import java.util.UUID
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Group
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONArray
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PerResourcePatchGeneratorTest {

  @Test
  fun `should generate a single insert patch if the resource is inserted`() {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    val insertionLocalChange = createInsertLocalChange(patient)

    val patches = PerResourcePatchGenerator.generate(listOf(insertionLocalChange))

    with(patches.single()) {
      with(generatedPatch) {
        assertThat(type).isEqualTo(Patch.Type.INSERT)
        assertThat(resourceId).isEqualTo(patient.logicalId)
        assertThat(resourceType).isEqualTo(patient.resourceType.name)
        assertThat(payload).isEqualTo(jsonParser.encodeResourceToString(patient))
      }

      with(localChanges) {
        assertThat(this).hasSize(1)
        assertThat(this[0]).isEqualTo(insertionLocalChange)
      }
    }
  }

  @Test
  fun `should generate a single update patch if the resource is updated`() {
    val remoteMeta =
      Meta().apply {
        versionId = "patient-version-1"
        lastUpdated = Date()
      }
    val remotePatient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    remotePatient.meta = remoteMeta
    val updatedPatient1 = readFromFile(Patient::class.java, "/update_test_patient_1.json")
    updatedPatient1.meta = remoteMeta
    val updateLocalChange1 = createUpdateLocalChange(remotePatient, updatedPatient1, 1L)
    val updatePatch = readJsonArrayFromFile("/update_patch_1.json")

    val patches = PerResourcePatchGenerator.generate(listOf(updateLocalChange1))

    with(patches.single()) {
      with(generatedPatch) {
        assertThat(type).isEqualTo(Patch.Type.UPDATE)
        assertThat(resourceId).isEqualTo(remotePatient.logicalId)
        assertThat(resourceType).isEqualTo(remotePatient.resourceType.name)
        assertThat(versionId).isEqualTo(remoteMeta.versionId)
        assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
      }

      with(localChanges) {
        assertThat(this).hasSize(1)
        assertThat(this[0]).isEqualTo(updateLocalChange1)
      }
    }
  }

  @Test
  fun `should generate a single delete patch if the resource is deleted`() {
    val remoteMeta =
      Meta().apply {
        versionId = "patient-version-1"
        lastUpdated = Date()
      }
    val remotePatient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    remotePatient.meta = remoteMeta
    val deleteLocalChange = createDeleteLocalChange(remotePatient, 3L)

    val patches = PerResourcePatchGenerator.generate(listOf(deleteLocalChange))

    with(patches.single()) {
      with(generatedPatch) {
        assertThat(type).isEqualTo(Patch.Type.DELETE)
        assertThat(resourceId).isEqualTo(remotePatient.logicalId)
        assertThat(resourceType).isEqualTo(remotePatient.resourceType.name)
        assertThat(versionId).isEqualTo(remoteMeta.versionId)
        assertThat(payload).isEmpty()
      }

      with(localChanges) {
        assertThat(this).hasSize(1)
        assertThat(this[0]).isEqualTo(deleteLocalChange)
      }
    }
  }

  @Test
  fun `should generate a single insert patch if the resource is inserted and updated`() {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    val insertionLocalChange = createInsertLocalChange(patient)
    val updatedPatient = readFromFile(Patient::class.java, "/update_test_patient_1.json")
    val updateLocalChange = createUpdateLocalChange(patient, updatedPatient, 1L)
    val patientString = jsonParser.encodeResourceToString(updatedPatient)

    val patches =
      PerResourcePatchGenerator.generate(listOf(insertionLocalChange, updateLocalChange))

    with(patches.single()) {
      with(generatedPatch) {
        assertThat(type).isEqualTo(Patch.Type.INSERT)
        assertThat(resourceId).isEqualTo(patient.logicalId)
        assertThat(resourceType).isEqualTo(patient.resourceType.name)
        assertThat(payload).isEqualTo(patientString)
      }

      with(localChanges) {
        assertThat(this).hasSize(2)
        assertThat(this).containsExactly(insertionLocalChange, updateLocalChange)
      }
    }
  }

  @Test
  fun `should generate no patch if the resource is inserted and deleted`() {
    val changes =
      listOf(
        LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            resourceUuid = UUID.randomUUID(),
            type = LocalChangeEntity.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("John")
                        family = "Doe"
                      },
                    )
                  },
                ),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            resourceUuid = UUID.randomUUID(),
            type = LocalChangeEntity.Type.DELETE,
            payload = "",
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(2)) },
      )
    val patchToUpload = PerResourcePatchGenerator.generate(changes)

    assertThat(patchToUpload).isEmpty()
  }

  @Test
  fun `should generate no patch if the resource is inserted, updated, and deleted`() {
    val changes =
      listOf(
        LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            resourceUuid = UUID.randomUUID(),
            type = LocalChangeEntity.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("John")
                        family = "Doe"
                      },
                    )
                  },
                ),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            resourceUuid = UUID.randomUUID(),
            type = LocalChangeEntity.Type.UPDATE,
            payload =
              diff(
                  jsonParser,
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("Jane")
                        family = "Doe"
                      },
                    )
                  },
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("Janet")
                        family = "Doe"
                      },
                    )
                  },
                )
                .toString(),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            resourceUuid = UUID.randomUUID(),
            type = LocalChangeEntity.Type.DELETE,
            payload = "",
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) },
      )
    val patchToUpload = PerResourcePatchGenerator.generate(changes)

    assertThat(patchToUpload).isEmpty()
  }

  @Test
  fun `should generate a single update patch if the resource is updated twice`() {
    val remoteMeta =
      Meta().apply {
        versionId = "patient-version-1"
        lastUpdated = Date()
      }
    val remotePatient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    remotePatient.meta = remoteMeta
    val updatedPatient1 = readFromFile(Patient::class.java, "/update_test_patient_1.json")
    updatedPatient1.meta = remoteMeta
    val updateLocalChange1 = createUpdateLocalChange(remotePatient, updatedPatient1, 1L)
    val updatedPatient2 = readFromFile(Patient::class.java, "/update_test_patient_2.json")
    updatedPatient2.meta = remoteMeta
    val updateLocalChange2 = createUpdateLocalChange(updatedPatient1, updatedPatient2, 2L)
    val updatePatch = readJsonArrayFromFile("/update_patch_2.json")

    val patches = PerResourcePatchGenerator.generate(listOf(updateLocalChange1, updateLocalChange2))

    with(patches.single()) {
      with(generatedPatch) {
        assertThat(type).isEqualTo(Patch.Type.UPDATE)
        assertThat(resourceId).isEqualTo(remotePatient.logicalId)
        assertThat(resourceType).isEqualTo(remotePatient.resourceType.name)
        assertThat(versionId).isEqualTo(remoteMeta.versionId)
        assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
      }

      with(localChanges) {
        assertThat(size).isEqualTo(2)
        assertThat(this).containsExactly(updateLocalChange1, updateLocalChange2)
      }
    }
  }

  @Test
  fun `should generate a single update patch with three elements of two adds and one remove`() {
    val expectedPatch = readJsonArrayFromFile("/update_careplan_patch.json")
    val updatePatch1 = readJsonArrayFromFile("/update_careplan_patch_1.json")
    val updatePatch2 = readJsonArrayFromFile("/update_careplan_patch_2.json")

    val updatedLocalChange1 =
      LocalChange(
        resourceType = "CarePlan",
        resourceId = "131b5257-a8b3-435a-8cb3-4cb1296be24a",
        type = LocalChange.Type.UPDATE,
        payload = updatePatch1.toString(),
        timestamp = Instant.now(),
        token = LocalChangeToken(listOf(1)),
      )

    val updatedLocalChange2 =
      LocalChange(
        resourceType = "CarePlan",
        resourceId = "131b5257-a8b3-435a-8cb3-4cb1296be24a",
        type = LocalChange.Type.UPDATE,
        payload = updatePatch2.toString(),
        timestamp = Instant.now(),
        token = LocalChangeToken(listOf(1)),
      )

    val patches =
      PerResourcePatchGenerator.generate(listOf(updatedLocalChange1, updatedLocalChange2))

    with(patches.single().generatedPatch) {
      assertThat(type).isEqualTo(Patch.Type.UPDATE)
      assertThat(resourceId).isEqualTo("131b5257-a8b3-435a-8cb3-4cb1296be24a")
      assertThat(resourceType).isEqualTo("CarePlan")
      assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), expectedPatch)
    }
  }

  @Test
  fun `should generate a single delete patch if the resource is updated and deleted`() {
    val remoteMeta =
      Meta().apply {
        versionId = "patient-version-1"
        lastUpdated = Date()
      }
    val remotePatient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    remotePatient.meta = remoteMeta
    val updatedPatient1 = remotePatient.copy()
    updatedPatient1.name = listOf(HumanName().addGiven("John").setFamily("Doe"))
    val updateLocalChange1 = createUpdateLocalChange(remotePatient, updatedPatient1, 1L)
    val updatedPatient2 = updatedPatient1.copy()
    updatedPatient2.name = listOf(HumanName().addGiven("Jimmy").setFamily("Doe"))
    val updateLocalChange2 = createUpdateLocalChange(updatedPatient1, updatedPatient2, 2L)
    val deleteLocalChange = createDeleteLocalChange(updatedPatient2, 3L)

    val patches =
      PerResourcePatchGenerator.generate(
        listOf(updateLocalChange1, updateLocalChange2, deleteLocalChange),
      )

    with(patches.single()) {
      with(generatedPatch) {
        assertThat(type).isEqualTo(Patch.Type.DELETE)
        assertThat(resourceId).isEqualTo(remotePatient.logicalId)
        assertThat(resourceType).isEqualTo(remotePatient.resourceType.name)
        assertThat(versionId).isEqualTo(remoteMeta.versionId)
        assertThat(payload).isEmpty()
      }

      with(localChanges) {
        assertThat(size).isEqualTo(3)
        assertThat(this).containsExactly(updateLocalChange1, updateLocalChange2, deleteLocalChange)
      }
    }
  }

  @Test
  fun `should throw an error if a change is done after a resource is deleted locally`() {
    val changes =
      listOf(
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.DELETE,
            payload = "",
            resourceUuid = UUID.randomUUID(),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(2)) },
        LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.UPDATE,
            payload = "",
            resourceUuid = UUID.randomUUID(),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) },
      )

    val errorMessage =
      Assert.assertThrows(IllegalArgumentException::class.java) {
          PerResourcePatchGenerator.generate(changes)
        }
        .localizedMessage

    assertThat(errorMessage).isEqualTo("Changes after deletion of resource are not permitted")
  }

  @Test
  fun `should throw an error if a change is done before a resource is created locally`() {
    val changes =
      listOf(
        LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.UPDATE,
            payload = "",
            resourceUuid = UUID.randomUUID(),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(1)) },
        LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            resourceUuid = UUID.randomUUID(),
            type = LocalChangeEntity.Type.INSERT,
            payload =
              FhirContext.forCached(FhirVersionEnum.R4)
                .newJsonParser()
                .encodeResourceToString(
                  Patient().apply {
                    id = "Patient-001"
                    addName(
                      HumanName().apply {
                        addGiven("John")
                        family = "Doe"
                      },
                    )
                  },
                ),
            timestamp = Instant.now(),
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(2)) },
      )

    val errorMessage =
      Assert.assertThrows(IllegalArgumentException::class.java) {
          PerResourcePatchGenerator.generate(changes)
        }
        .localizedMessage

    assertThat(errorMessage).isEqualTo("Changes before creation of resource are not permitted")
  }

  private fun createUpdateLocalChange(
    oldEntity: Resource,
    updatedResource: Resource,
    currentChangeId: Long,
  ): LocalChange {
    val jsonDiff = diff(jsonParser, oldEntity, updatedResource)
    return LocalChange(
      resourceId = oldEntity.logicalId,
      resourceType = oldEntity.resourceType.name,
      type = LocalChange.Type.UPDATE,
      payload = jsonDiff.toString(),
      versionId = oldEntity.versionId,
      token = LocalChangeToken(listOf(currentChangeId + 1)),
      timestamp = Instant.now(),
    )
  }

  private fun createInsertLocalChange(entity: Resource): LocalChange {
    return LocalChange(
      resourceId = entity.logicalId,
      resourceType = entity.resourceType.name,
      type = LocalChange.Type.INSERT,
      payload = jsonParser.encodeResourceToString(entity),
      versionId = entity.versionId,
      token = LocalChangeToken(listOf(1L)),
      timestamp = Instant.now(),
    )
  }

  private fun createDeleteLocalChange(entity: Resource, currentChangeId: Long): LocalChange {
    return LocalChange(
      resourceId = entity.logicalId,
      resourceType = entity.resourceType.name,
      type = LocalChange.Type.DELETE,
      payload = "",
      versionId = entity.versionId,
      token = LocalChangeToken(listOf(currentChangeId + 1)),
      timestamp = Instant.now(),
    )
  }

  @Test
  fun test_observation_findOutgoingReferences() {
    val observation =
      FhirContext.forR4Cached()
        .newJsonParser()
        .parseResource(
          """
      {
        "resourceType" : "Observation",
        "id" : "f206",
        "text" : {
          "status" : "generated",
          "div" : "<div xmlns=\"http://www.w3.org/1999/xhtml\"><p><b>Generated Narrative: Observation</b><a name=\"f206\"> </a></p><div style=\"display: inline-block; background-color: #d9e0e7; padding: 6px; margin: 4px; border: 1px solid #8da1b4; border-radius: 5px; line-height: 60%\"><p style=\"margin-bottom: 0px\">Resource Observation &quot;f206&quot; </p></div><p><b>status</b>: final</p><p><b>code</b>: Blood culture <span style=\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\"> (acmelabs.org#104177; <a href=\"https://loinc.org/\">LOINC</a>#600-7 &quot;Bacteria identified in Blood by Culture&quot;)</span></p><p><b>subject</b>: <span title=\"  No identifier could be provided to this observation  \"><a href=\"patient-example-f201-roel.html\">Patient/f201: Roel</a> &quot;Roel&quot;</span></p><p><b>issued</b>: 11 Mar 2013, 8:28:00 pm</p><p><b>performer</b>: <a href=\"practitioner-example-f202-lm.html\">Practitioner/f202: Luigi Maas</a> &quot;Luigi Maas&quot;</p><p><b>value</b>: Staphylococcus aureus <span style=\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\"> (<a href=\"https://browser.ihtsdotools.org/\">SNOMED CT</a>#3092008)</span></p><p><b>interpretation</b>: Positive <span style=\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\"> (<a href=\"http://terminology.hl7.org/5.1.0/CodeSystem-v3-ObservationInterpretation.html\">ObservationInterpretation</a>#POS)</span></p><p><b>method</b>: <span title=\"  BodySite not relevant  \">Blood culture for bacteria, including anaerobic screen <span style=\"background: LightGoldenRodYellow; margin: 4px; border: 1px solid khaki\"> (<a href=\"https://browser.ihtsdotools.org/\">SNOMED CT</a>#104177005)</span></span></p></div>"
        },
        "status" : "final",
        "code" : {
          "coding" : [{
            "system" : "http://acmelabs.org",
            "code" : "104177",
            "display" : "Blood culture"
          },
          {
            "system" : "http://loinc.org",
            "code" : "600-7",
            "display" : "Bacteria identified in Blood by Culture"
          }]
        },
        "subject" : {
          "reference" : "Patient/f201",
          "display" : "Roel"
        },
        "issued" : "2013-03-11T10:28:00+01:00",
        "performer" : [{
          "reference" : "Practitioner/f202",
          "display" : "Luigi Maas"
        }],
        "valueCodeableConcept" : {
          "coding" : [{
            "system" : "http://snomed.info/sct",
            "code" : "3092008",
            "display" : "Staphylococcus aureus"
          }]
        },
        "interpretation" : [{
          "coding" : [{
            "system" : "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation",
            "code" : "POS"
          }]
        }],
        "method" : {
          "coding" : [{
            "system" : "http://snomed.info/sct",
            "code" : "104177005",
            "display" : "Blood culture for bacteria, including anaerobic screen"
          }]
        }
      }
    """
            .trimIndent(),
        ) as Resource

    val result =
      PerResourcePatchGenerator.generateSquashedChangesMapping(
          listOf(createInsertLocalChange(observation)),
        )
        .single()
        .findOutgoingReferences()
    assertThat(result).containsExactly("Patient/f201", "Practitioner/f202")
  }

  @Test
  fun test_group_findOutgoingReferences() {
    val group =
      FhirContext.forR4Cached()
        .newJsonParser()
        .parseResource(
          """
      {
  "resourceType" : "Group",
  "id" : "102",
  "text" : {
    "status" : "additional",
    "div" : "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n      <p>Selected Patients</p>\n      <ul>\n        <li>Patient Donald DUCK @ Acme Healthcare, Inc. MR = 654321</li>\n        <li>Patient Donald D DUCK @ Acme Healthcare, Inc. MR = 123456</li>\n        <li>Patient Simon Notsowell @ Acme Healthcare, Inc. MR = 123457, DECEASED</li>\n        <li>Patient Sandy Notsowell @ Acme Healthcare, Inc. MR = 123458, DECEASED</li>\n      </ul>\n    </div>"
  },
  "type" : "person",
  "membership" : "enumerated",
  "member" : [{
    "entity" : {
      "reference" : "Patient/pat1"
    },
    "period" : {
      "start" : "2014-10-08"
    }
  },
  {
    "entity" : {
      "reference" : "Patient/pat2"
    },
    "period" : {
      "start" : "2015-04-02"
    },
    "inactive" : true
  },
  {
    "entity" : {
      "reference" : "Patient/pat3"
    },
    "period" : {
      "start" : "2015-08-06"
    }
  },
  {
    "entity" : {
      "reference" : "Patient/pat4"
    },
    "period" : {
      "start" : "2015-08-06"
    }
  }],
  "characteristic" : [{
    "code" : {
      "coding" : [{
        "system" : "http://example.org",
        "code" : "attributed-to"
      }],
      "text" : "Patients primarily attributed to"
    },
    "valueReference" : {
      "reference" : "Practitioner/123"
    },
    "exclude" : false
  }]
}
    """
            .trimIndent(),
        ) as Resource

    val result =
      PerResourcePatchGenerator.generateSquashedChangesMapping(
          listOf(createInsertLocalChange(group)),
        )
        .single()
        .findOutgoingReferences()
    assertThat(result)
      .containsExactly(
        "Patient/pat1",
        "Patient/pat2",
        "Patient/pat3",
        "Patient/pat4",
        "Practitioner/123",
      )
  }

  @Test
  fun test_careplan_insertpatch_findOutgoingReferences() {
    val group =
      FhirContext.forR4Cached()
        .newJsonParser()
        .parseResource(
          """{
  "resourceType": "CarePlan",
  "id": "gpvisit",
  "text": {
    "status": "additional",
    "div": "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n      <p>  Represents the flow of a patient within a practice. The plan is created when\n        they arrive and represents the 'care' of the patient over the course of that encounter.\n        They first see the nurse for basic observations (BP, pulse, temp) then the doctor for\n        the consultation and finally the nurse again for a tetanus immunization. As the plan is\n        updated (e.g. a new activity added), different versions of the plan exist, and workflow timings\n        for reporting can be gained by examining the plan history. This example is the version after\n        seeing the doctor, and waiting for the nurse.The plan can either be created 'ad hoc' and modified as\n        the parient progresses, or start with a standard template (which can, of course, be altered to suit the patient.</p>\n    </div>"
  },
  "contained": [
    {
      "resourceType": "Condition",
      "id": "p1",
      "clinicalStatus": {
        "coding": [
          {
            "system": "http://terminology.hl7.org/CodeSystem/condition-clinical",
            "code": "active"
          }
        ]
      },
      "verificationStatus": {
        "coding": [
          {
            "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
            "code": "confirmed"
          }
        ]
      },
      "code": {
        "text": "Overseas encounter"
      },
      "subject": {
        "reference": "Patient/100",
        "display": "Peter James Chalmers"
      }
    },
    {
      "resourceType": "CareTeam",
      "id": "careteam",
      "participant": [
        {
          "id": "part1",
          "role": [
            {
              "coding": [
                {
                  "system": "http://example.org/local",
                  "code": "nur"
                }
              ],
              "text": "nurse"
            }
          ],
          "member": {
            "reference": "Practitioner/13",
            "display": "Nurse Nancy"
          }
        },
        {
          "id": "part2",
          "role": [
            {
              "coding": [
                {
                  "system": "http://example.org/local",
                  "code": "doc"
                }
              ],
              "text": "doctor"
            }
          ],
          "member": {
            "reference": "Practitioner/14",
            "display": "Doctor Dave"
          }
        }
      ]
    },
    {
      "resourceType": "Goal",
      "id": "goal",
      "lifecycleStatus": "planned",
      "description": {
        "text": "Complete consultation"
      },
      "subject": {
        "reference": "Patient/100",
        "display": "Peter James Chalmers"
      }
    }
  ],
  "status": "active",
  "intent": "plan",
  "subject": {
    "reference": "Patient/100",
    "display": "Peter James Chalmers"
  },
  "period": {
    "start": "2013-01-01T10:30:00+00:00"
  },
  "careTeam": [
    {
      "reference": "#careteam"
    }
  ],
  "addresses": [
    {
      "reference": "#p1",
      "display": "obesity"
    }
  ],
  "goal": [
    {
      "reference": "#goal"
    }
  ],
  "activity": [
    {
      "outcomeReference": [
        {
          "reference": "Encounter/example"
        }
      ],
      "detail": {
        "kind": "Appointment",
        "code": {
          "coding": [
            {
              "system": "http://example.org/local",
              "code": "nursecon"
            }
          ],
          "text": "Nurse Consultation"
        },
        "status": "completed",
        "doNotPerform": false,
        "scheduledPeriod": {
          "start": "2013-01-01T10:38:00+00:00",
          "end": "2013-01-01T10:50:00+00:00"
        },
        "performer": [
          {
            "reference": "Practitioner/13",
            "display": "Nurse Nancy"
          }
        ]
      }
    },
    {
      "detail": {
        "kind": "Appointment",
        "code": {
          "coding": [
            {
              "system": "http://example.org/local",
              "code": "doccon"
            }
          ],
          "text": "Doctor Consultation"
        },
        "status": "scheduled",
        "doNotPerform": false,
        "performer": [
          {
            "reference": "Practitioner/14",
            "display": "Doctor Dave"
          }
        ]
      }
    }
  ]
}
                    """
            .trimIndent(),
        ) as Resource

    val result =
      PerResourcePatchGenerator.generateSquashedChangesMapping(
          listOf(createInsertLocalChange(group)),
        )
        .single()
        .findOutgoingReferences()
    assertThat(result)
      .containsExactly(
        "Patient/100",
        "Practitioner/13",
        "Practitioner/14",
        "Patient/100",
        "Patient/100",
        "#careteam",
        "#p1",
        "#goal",
        "Encounter/example",
        "Practitioner/13",
        "Practitioner/14",
      )
      .inOrder()
  }

  @Test
  fun test_careplan_updatepatch_findOutgoingReferences() {
    val group =
      FhirContext.forR4Cached()
        .newJsonParser()
        .parseResource(
          """{
  "resourceType": "CarePlan",
  "id": "gpvisit",
  "text": {
    "status": "additional",
    "div": "<div xmlns=\"http://www.w3.org/1999/xhtml\">\n      <p>  Represents the flow of a patient within a practice. The plan is created when\n        they arrive and represents the 'care' of the patient over the course of that encounter.\n        They first see the nurse for basic observations (BP, pulse, temp) then the doctor for\n        the consultation and finally the nurse again for a tetanus immunization. As the plan is\n        updated (e.g. a new activity added), different versions of the plan exist, and workflow timings\n        for reporting can be gained by examining the plan history. This example is the version after\n        seeing the doctor, and waiting for the nurse.The plan can either be created 'ad hoc' and modified as\n        the parient progresses, or start with a standard template (which can, of course, be altered to suit the patient.</p>\n    </div>"
  },
  "contained": [
    {
      "resourceType": "Condition",
      "id": "p1",
      "clinicalStatus": {
        "coding": [
          {
            "system": "http://terminology.hl7.org/CodeSystem/condition-clinical",
            "code": "active"
          }
        ]
      },
      "verificationStatus": {
        "coding": [
          {
            "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
            "code": "confirmed"
          }
        ]
      },
      "code": {
        "text": "Overseas encounter"
      },
      "subject": {
        "reference": "Patient/100",
        "display": "Peter James Chalmers"
      }
    },
    {
      "resourceType": "CareTeam",
      "id": "careteam",
      "participant": [
        {
          "id": "part1",
          "role": [
            {
              "coding": [
                {
                  "system": "http://example.org/local",
                  "code": "nur"
                }
              ],
              "text": "nurse"
            }
          ],
          "member": {
            "reference": "Practitioner/13",
            "display": "Nurse Nancy"
          }
        },
        {
          "id": "part2",
          "role": [
            {
              "coding": [
                {
                  "system": "http://example.org/local",
                  "code": "doc"
                }
              ],
              "text": "doctor"
            }
          ],
          "member": {
            "reference": "Practitioner/14",
            "display": "Doctor Dave"
          }
        }
      ]
    },
    {
      "resourceType": "Goal",
      "id": "goal",
      "lifecycleStatus": "planned",
      "description": {
        "text": "Complete consultation"
      },
      "subject": {
        "reference": "Patient/100",
        "display": "Peter James Chalmers"
      }
    }
  ],
  "status": "active",
  "intent": "plan",
  "subject": {
    "reference": "Patient/100",
    "display": "Peter James Chalmers"
  },
  "period": {
    "start": "2013-01-01T10:30:00+00:00"
  },
  "careTeam": [
    {
      "reference": "#careteam"
    }
  ],
  "addresses": [
    {
      "reference": "#p1",
      "display": "obesity"
    }
  ],
  "goal": [
    {
      "reference": "#goal"
    }
  ],
  "activity": [
    {
      "outcomeReference": [
        {
          "reference": "Encounter/example"
        }
      ],
      "detail": {
        "kind": "Appointment",
        "code": {
          "coding": [
            {
              "system": "http://example.org/local",
              "code": "nursecon"
            }
          ],
          "text": "Nurse Consultation"
        },
        "status": "completed",
        "doNotPerform": false,
        "scheduledPeriod": {
          "start": "2013-01-01T10:38:00+00:00",
          "end": "2013-01-01T10:50:00+00:00"
        },
        "performer": [
          {
            "reference": "Practitioner/13",
            "display": "Nurse Nancy"
          }
        ]
      }
    },
    {
      "detail": {
        "kind": "Appointment",
        "code": {
          "coding": [
            {
              "system": "http://example.org/local",
              "code": "doccon"
            }
          ],
          "text": "Doctor Consultation"
        },
        "status": "scheduled",
        "doNotPerform": false,
        "performer": [
          {
            "reference": "Practitioner/14",
            "display": "Doctor Dave"
          }
        ]
      }
    }
  ]
}
                    """
            .trimIndent(),
        ) as Resource

    val result =
      PerResourcePatchGenerator.generateSquashedChangesMapping(
          listOf(createUpdateLocalChange(CarePlan().apply { id = "gpvisit" }, group, 1)),
        )
        .single()
        .also { println(it) }
        .findOutgoingReferences()
    assertThat(result)
      .containsExactly(
        "Patient/100",
        "Practitioner/13",
        "Practitioner/14",
        "Patient/100",
        "Patient/100",
        "#careteam",
        "#p1",
        "#goal",
        "Encounter/example",
        "Practitioner/13",
        "Practitioner/14",
      )
  }

  @Test
  fun test_createReferenceAdjacencyList() {
    val localChanges = LinkedList<LocalChange>()

    var group =
      Group()
        .apply {
          id = "group-1"
          type = Group.GroupType.PERSON
        }
        .also { localChanges.add(createInsertLocalChange(it)) }

    // pa-01
    Patient().apply { id = "patient-1" }.also { localChanges.add(createInsertLocalChange(it)) }
    Encounter()
      .apply {
        id = "encounter-1"
        subject = Reference("Patient/patient-1")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Observation()
      .apply {
        id = "observation-1"
        subject = Reference("Patient/patient-1")
        encounter = Reference("Encounter/encounter-1")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-1"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 1)) }

    // pa-02
    Patient().apply { id = "patient-2" }.also { localChanges.add(createInsertLocalChange(it)) }
    Encounter()
      .apply {
        id = "encounter-2"
        subject = Reference("Patient/patient-2")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Observation()
      .apply {
        id = "observation-2"
        subject = Reference("Patient/patient-2")
        encounter = Reference("Encounter/encounter-2")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-2"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 2)) }

    // pa-03
    Patient().apply { id = "patient-3" }.also { localChanges.add(createInsertLocalChange(it)) }

    Encounter()
      .apply {
        id = "encounter-3"
        subject = Reference("Patient/patient-3")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Observation()
      .apply {
        id = "observation-3"
        subject = Reference("Patient/patient-3")
        encounter = Reference("Encounter/encounter-3")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-3"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 3)) }

    val result =
      PerResourcePatchGenerator.generateSquashedChangesMapping(localChanges)
        .createReferenceAdjacencyList(
          localChanges.map { "${it.resourceType}/${it.resourceId}" }.toSet(),
        )

    assertThat(result)
      .isEqualTo(
        mutableMapOf(
          "Group/group-1" to listOf("Patient/patient-1", "Patient/patient-2", "Patient/patient-3"),
          "Patient/patient-1" to emptyList(),
          "Patient/patient-2" to emptyList(),
          "Patient/patient-3" to emptyList(),
          "Encounter/encounter-1" to listOf("Patient/patient-1"),
          "Encounter/encounter-2" to listOf("Patient/patient-2"),
          "Encounter/encounter-3" to listOf("Patient/patient-3"),
          "Observation/observation-1" to listOf("Patient/patient-1", "Encounter/encounter-1"),
          "Observation/observation-2" to listOf("Patient/patient-2", "Encounter/encounter-2"),
          "Observation/observation-3" to listOf("Patient/patient-3", "Encounter/encounter-3"),
        ),
      )
  }

  @Test
  fun test_createReferenceAdjacencyList_WithUpdate() {
    val localChanges = LinkedList<LocalChange>()

    var group =
      Group()
        .apply {
          id = "group-1"
          type = Group.GroupType.PERSON
        }
        .also { localChanges.add(createInsertLocalChange(it)) }

    // pa-01
    Patient()
      .apply { id = "patient-1" }
      .also {
        localChanges.add(
          createUpdateLocalChange(
            it,
            it.copy().apply { active = true },
            1,
          ),
        )
      }
    Encounter()
      .apply {
        id = "encounter-1"
        subject = Reference("Patient/patient-1")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Observation()
      .apply {
        id = "observation-1"
        subject = Reference("Patient/patient-1")
        encounter = Reference("Encounter/encounter-1")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-1"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 1)) }

    // pa-02
    Patient()
      .apply { id = "patient-2" }
      .also {
        localChanges.add(
          createUpdateLocalChange(
            it,
            it.copy().apply { active = true },
            1,
          ),
        )
      }
    Encounter()
      .apply {
        id = "encounter-2"
        subject = Reference("Patient/patient-2")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Observation()
      .apply {
        id = "observation-2"
        subject = Reference("Patient/patient-2")
        encounter = Reference("Encounter/encounter-2")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-2"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 2)) }

    // pa-03
    Patient()
      .apply { id = "patient-3" }
      .also {
        localChanges.add(
          createUpdateLocalChange(
            it,
            it.copy().apply { active = true },
            1,
          ),
        )
      }

    Encounter()
      .apply {
        id = "encounter-3"
        subject = Reference("Patient/patient-3")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Observation()
      .apply {
        id = "observation-3"
        subject = Reference("Patient/patient-3")
        encounter = Reference("Encounter/encounter-3")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-3"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 3)) }

    val result =
      PerResourcePatchGenerator.generateSquashedChangesMapping(localChanges)
        .createReferenceAdjacencyList(
          localChanges
            .filter { it.type == LocalChange.Type.INSERT }
            .map { "${it.resourceType}/${it.resourceId}" }
            .toSet(),
        )

    assertThat(result)
      .isEqualTo(
        mutableMapOf(
          "Group/group-1" to emptyList(),
          "Patient/patient-1" to emptyList(),
          "Patient/patient-2" to emptyList(),
          "Patient/patient-3" to emptyList(),
          "Encounter/encounter-1" to emptyList(),
          "Encounter/encounter-2" to emptyList(),
          "Encounter/encounter-3" to emptyList(),
          "Observation/observation-1" to listOf("Encounter/encounter-1"),
          "Observation/observation-2" to listOf("Encounter/encounter-2"),
          "Observation/observation-3" to listOf("Encounter/encounter-3"),
        ),
      )
  }

  @Test
  fun `generate with acyclic references should return the list in topological order`() {
    val localChanges = LinkedList<LocalChange>()

    var group =
      Group()
        .apply {
          id = "group-1"
          type = Group.GroupType.PERSON
        }
        .also { localChanges.add(createInsertLocalChange(it)) }

    // pa-01
    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-1"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 1)) }

    Observation()
      .apply {
        id = "observation-1"
        subject = Reference("Patient/patient-1")
        encounter = Reference("Encounter/encounter-1")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Encounter()
      .apply {
        id = "encounter-1"
        subject = Reference("Patient/patient-1")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Patient().apply { id = "patient-1" }.also { localChanges.add(createInsertLocalChange(it)) }

    // pa-02
    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-2"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 2)) }

    Observation()
      .apply {
        id = "observation-2"
        subject = Reference("Patient/patient-2")
        encounter = Reference("Encounter/encounter-2")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Encounter()
      .apply {
        id = "encounter-2"
        subject = Reference("Patient/patient-2")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Patient().apply { id = "patient-2" }.also { localChanges.add(createInsertLocalChange(it)) }

    // pa-03
    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-3"))) }
        .also { localChanges.add(createUpdateLocalChange(group, it, 3)) }

    Observation()
      .apply {
        id = "observation-3"
        subject = Reference("Patient/patient-3")
        encounter = Reference("Encounter/encounter-3")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Encounter()
      .apply {
        id = "encounter-3"
        subject = Reference("Patient/patient-3")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    Patient().apply { id = "patient-3" }.also { localChanges.add(createInsertLocalChange(it)) }

    val result = PerResourcePatchGenerator.generate(localChanges)
    assertThat(result.map { it.generatedPatch.resourceId })
      .containsExactly(
        "patient-1",
        "patient-2",
        "patient-3",
        "group-1",
        "encounter-1",
        "observation-1",
        "encounter-2",
        "observation-2",
        "encounter-3",
        "observation-3",
      )
      .inOrder()
  }

  @Test
  fun `generate with cyclic references should throw exception`() {
    val localChanges = LinkedList<LocalChange>()

    Patient()
      .apply {
        id = "patient-1"
        addLink(
          Patient.PatientLinkComponent().apply { other = Reference("RelatedPerson/related-1") },
        )
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    RelatedPerson()
      .apply {
        id = "related-1"
        patient = Reference("Patient/patient-1")
      }
      .also { localChanges.add(createInsertLocalChange(it)) }

    val errorMessage =
      Assert.assertThrows(IllegalStateException::class.java) {
          PerResourcePatchGenerator.generate(localChanges)
        }
        .localizedMessage

    assertThat(errorMessage).isEqualTo("Detected a cycle.")
  }
}
