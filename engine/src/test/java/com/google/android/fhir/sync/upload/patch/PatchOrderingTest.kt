/*
 * Copyright 2024 Google LLC
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

import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.LocalChangeResourceReference
import com.google.android.fhir.db.impl.dao.diff
import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.upload.patch.PatchOrdering.createAdjacencyListForCreateReferences
import com.google.android.fhir.testing.jsonParser
import com.google.android.fhir.versionId
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.util.LinkedList
import kotlin.random.Random
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Group
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.Resource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PatchOrderingTest {

  @Mock private lateinit var database: Database
  private lateinit var patchGenerator: PerResourcePatchGenerator

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    patchGenerator = PerResourcePatchGenerator(database)
  }

  @Test
  fun `createReferenceAdjacencyList with local changes for only new resources should only have edges to inserted resources`() =
    runTest {
      val localChanges = LinkedList<LocalChange>()
      val localChangeResourceReferences = mutableListOf<LocalChangeResourceReference>()

      var group =
        Group()
          .apply {
            id = "group-1"
            type = Group.GroupType.PERSON
          }
          .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }

      // pa-01
      Patient()
        .apply { id = "patient-1" }
        .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }
      Encounter()
        .apply {
          id = "encounter-1"
          subject = Reference("Patient/patient-1")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-1",
              "Encounter.subject",
            ),
          )
        }

      Observation()
        .apply {
          id = "observation-1"
          subject = Reference("Patient/patient-1")
          encounter = Reference("Encounter/encounter-1")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-1",
              "Observation.subject",
            ),
          )
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Encounter/encounter-1",
              "Observation.encounter",
            ),
          )
        }

      group =
        group
          .copy()
          .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-1"))) }
          .also {
            localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
            localChangeResourceReferences.add(
              LocalChangeResourceReference(
                localChanges.last().token.ids.first(),
                "Patient/patient-1",
                "Group.member",
              ),
            )
          }

      // pa-02
      Patient()
        .apply { id = "patient-2" }
        .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }
      Encounter()
        .apply {
          id = "encounter-2"
          subject = Reference("Patient/patient-2")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-2",
              "Encounter.subject",
            ),
          )
        }

      Observation()
        .apply {
          id = "observation-2"
          subject = Reference("Patient/patient-2")
          encounter = Reference("Encounter/encounter-2")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-2",
              "Observation.subject",
            ),
          )
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Encounter/encounter-2",
              "Observation.encounter",
            ),
          )
        }

      group =
        group
          .copy()
          .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-2"))) }
          .also {
            localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
            localChangeResourceReferences.add(
              LocalChangeResourceReference(
                localChanges.last().token.ids.first(),
                "Patient/patient-2",
                "Group.member",
              ),
            )
          }

      // pa-03
      Patient()
        .apply { id = "patient-3" }
        .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }

      Encounter()
        .apply {
          id = "encounter-3"
          subject = Reference("Patient/patient-3")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-3",
              "Encounter.subject",
            ),
          )
        }

      Observation()
        .apply {
          id = "observation-3"
          subject = Reference("Patient/patient-3")
          encounter = Reference("Encounter/encounter-3")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-3",
              "Observation.subject",
            ),
          )
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Encounter/encounter-3",
              "Observation.encounter",
            ),
          )
        }

      group =
        group
          .copy()
          .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-3"))) }
          .also {
            localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
            localChangeResourceReferences.add(
              LocalChangeResourceReference(
                localChanges.last().token.ids.first(),
                "Patient/patient-3",
                "Group.member",
              ),
            )
          }

      whenever(database.getLocalChangeResourceReferences(any()))
        .thenReturn(localChangeResourceReferences)

      val result =
        patchGenerator
          .generateSquashedChangesMapping(localChanges)
          .createAdjacencyListForCreateReferences(
            localChanges.map { "${it.resourceType}/${it.resourceId}" }.toSet(),
            localChangeResourceReferences.groupBy { it.localChangeId },
          )

      assertThat(result)
        .isEqualTo(
          mutableMapOf(
            "Group/group-1" to
              listOf("Patient/patient-1", "Patient/patient-2", "Patient/patient-3"),
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
  fun `createReferenceAdjacencyList with local changes for new and old resources should only have edges to inserted resources`() =
    runTest {
      val localChanges = mutableListOf<LocalChange>()
      val localChangeResourceReferences = mutableListOf<LocalChangeResourceReference>()

      var group =
        Group()
          .apply {
            id = "group-1"
            type = Group.GroupType.PERSON
          }
          .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }

      // pa-01
      Patient()
        .apply { id = "patient-1" }
        .also {
          localChanges.add(
            createUpdateLocalChange(
              it,
              it.copy().apply { active = true },
              Random.nextLong(),
            ),
          )
        }

      Encounter()
        .apply {
          id = "encounter-1"
          subject = Reference("Patient/patient-1")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-1",
              "Encounter.subject",
            ),
          )
        }

      Observation()
        .apply {
          id = "observation-1"
          subject = Reference("Patient/patient-1")
          encounter = Reference("Encounter/encounter-1")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-1",
              "Observation.subject",
            ),
          )
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Encounter/encounter-1",
              "Observation.encounter",
            ),
          )
        }

      group =
        group
          .copy()
          .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-1"))) }
          .also {
            localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
            localChangeResourceReferences.add(
              LocalChangeResourceReference(
                localChanges.last().token.ids.first(),
                "Patient/patient-1",
                "Group.member",
              ),
            )
          }

      // pa-02
      Patient()
        .apply { id = "patient-2" }
        .also {
          localChanges.add(
            createUpdateLocalChange(
              it,
              it.copy().apply { active = true },
              Random.nextLong(),
            ),
          )
        }
      Encounter()
        .apply {
          id = "encounter-2"
          subject = Reference("Patient/patient-2")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-2",
              "Encounter.subject",
            ),
          )
        }

      Observation()
        .apply {
          id = "observation-2"
          subject = Reference("Patient/patient-2")
          encounter = Reference("Encounter/encounter-2")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-2",
              "Observation.subject",
            ),
          )
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Encounter/encounter-2",
              "Observation.encounter",
            ),
          )
        }

      group =
        group
          .copy()
          .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-2"))) }
          .also {
            localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
            localChangeResourceReferences.add(
              LocalChangeResourceReference(
                localChanges.last().token.ids.first(),
                "Patient/patient-2",
                "Group.member",
              ),
            )
          }

      // pa-03
      Patient()
        .apply { id = "patient-3" }
        .also {
          localChanges.add(
            createUpdateLocalChange(
              it,
              it.copy().apply { active = true },
              Random.nextLong(),
            ),
          )
        }

      Encounter()
        .apply {
          id = "encounter-3"
          subject = Reference("Patient/patient-3")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-3",
              "Encounter.subject",
            ),
          )
        }

      Observation()
        .apply {
          id = "observation-3"
          subject = Reference("Patient/patient-3")
          encounter = Reference("Encounter/encounter-3")
        }
        .also {
          localChanges.add(createInsertLocalChange(it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-3",
              "Observation.subject",
            ),
          )
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Encounter/encounter-3",
              "Observation.encounter",
            ),
          )
        }

      group =
        group
          .copy()
          .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-3"))) }
          .also {
            localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
            localChangeResourceReferences.add(
              LocalChangeResourceReference(
                localChanges.last().token.ids.first(),
                "Patient/patient-3",
                "Group.member",
              ),
            )
          }

      whenever(database.getLocalChangeResourceReferences(any()))
        .thenReturn(localChangeResourceReferences)

      val result =
        patchGenerator
          .generateSquashedChangesMapping(localChanges)
          .createAdjacencyListForCreateReferences(
            localChanges
              .filter { it.type == LocalChange.Type.INSERT }
              .map { "${it.resourceType}/${it.resourceId}" }
              .toSet(),
            localChangeResourceReferences.groupBy { it.localChangeId },
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
  fun `generate with acyclic references should return the list in topological order`() = runTest {
    val localChanges = LinkedList<LocalChange>()
    val localChangeResourceReferences = mutableListOf<LocalChangeResourceReference>()

    var group =
      Group()
        .apply {
          id = "group-1"
          type = Group.GroupType.PERSON
        }
        .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }

    // pa-01
    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-1"))) }
        .also {
          localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-1",
              "Group.member",
            ),
          )
        }

    Observation()
      .apply {
        id = "observation-1"
        subject = Reference("Patient/patient-1")
        encounter = Reference("Encounter/encounter-1")
      }
      .also {
        localChanges.add(createInsertLocalChange(it, Random.nextLong()))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Patient/patient-1",
            "Observation.subject",
          ),
        )
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Encounter/encounter-1",
            "Observation.encounter",
          ),
        )
      }

    Encounter()
      .apply {
        id = "encounter-1"
        subject = Reference("Patient/patient-1")
      }
      .also {
        localChanges.add(createInsertLocalChange(it, Random.nextLong()))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Patient/patient-1",
            "Encounter.subject",
          ),
        )
      }

    Patient()
      .apply { id = "patient-1" }
      .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }

    // pa-02
    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-2"))) }
        .also {
          localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-2",
              "Group.member",
            ),
          )
        }

    Observation()
      .apply {
        id = "observation-2"
        subject = Reference("Patient/patient-2")
        encounter = Reference("Encounter/encounter-2")
      }
      .also {
        localChanges.add(createInsertLocalChange(it, Random.nextLong()))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Patient/patient-2",
            "Observation.subject",
          ),
        )
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Encounter/encounter-2",
            "Observation.encounter",
          ),
        )
      }

    Encounter()
      .apply {
        id = "encounter-2"
        subject = Reference("Patient/patient-2")
      }
      .also {
        localChanges.add(createInsertLocalChange(it, Random.nextLong()))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Patient/patient-1",
            "Encounter.subject",
          ),
        )
      }

    Patient()
      .apply { id = "patient-2" }
      .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }

    // pa-03
    group =
      group
        .copy()
        .apply { addMember(Group.GroupMemberComponent(Reference("Patient/patient-3"))) }
        .also {
          localChanges.add(createUpdateLocalChange(group, it, Random.nextLong()))
          localChangeResourceReferences.add(
            LocalChangeResourceReference(
              localChanges.last().token.ids.first(),
              "Patient/patient-3",
              "Group.member",
            ),
          )
        }

    Observation()
      .apply {
        id = "observation-3"
        subject = Reference("Patient/patient-3")
        encounter = Reference("Encounter/encounter-3")
      }
      .also {
        localChanges.add(createInsertLocalChange(it, Random.nextLong()))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Patient/patient-3",
            "Observation.subject",
          ),
        )
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Encounter/encounter-3",
            "Observation.encounter",
          ),
        )
      }

    Encounter()
      .apply {
        id = "encounter-3"
        subject = Reference("Patient/patient-3")
      }
      .also {
        localChanges.add(createInsertLocalChange(it, Random.nextLong()))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Patient/patient-3",
            "Encounter.subject",
          ),
        )
      }

    Patient()
      .apply { id = "patient-3" }
      .also { localChanges.add(createInsertLocalChange(it, Random.nextLong())) }

    whenever(database.getLocalChangeResourceReferences(any()))
      .thenReturn(localChangeResourceReferences)

    val result = patchGenerator.generate(localChanges)
    Truth.assertThat(result.map { it.generatedPatch.resourceId })
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
  fun `generate with cyclic references should throw exception`() = runTest {
    val localChanges = LinkedList<LocalChange>()
    val localChangeResourceReferences = mutableListOf<LocalChangeResourceReference>()

    Patient()
      .apply {
        id = "patient-1"
        addLink(
          Patient.PatientLinkComponent().apply { other = Reference("RelatedPerson/related-1") },
        )
      }
      .also {
        localChanges.add(createInsertLocalChange(it, Random.nextLong()))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "RelatedPerson/related-1",
            "Patient.other",
          ),
        )
      }

    RelatedPerson()
      .apply {
        id = "related-1"
        patient = Reference("Patient/patient-1")
      }
      .also {
        localChanges.add(createInsertLocalChange(it))
        localChangeResourceReferences.add(
          LocalChangeResourceReference(
            localChanges.last().token.ids.first(),
            "Patient/patient-1",
            "RelatedPerson.patient",
          ),
        )
      }

    whenever(database.getLocalChangeResourceReferences(any()))
      .thenReturn(localChangeResourceReferences)

    val errorMessage =
      assertFailsWith<IllegalStateException> { patchGenerator.generate(localChanges) }
        .localizedMessage

    assertThat(errorMessage).isEqualTo("Detected a cycle.")
  }

  companion object {

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

    private fun createInsertLocalChange(entity: Resource, currentChangeId: Long = 1): LocalChange {
      return LocalChange(
        resourceId = entity.logicalId,
        resourceType = entity.resourceType.name,
        type = LocalChange.Type.INSERT,
        payload = jsonParser.encodeResourceToString(entity),
        versionId = entity.versionId,
        token = LocalChangeToken(listOf(currentChangeId)),
        timestamp = Instant.now(),
      )
    }
  }
}
